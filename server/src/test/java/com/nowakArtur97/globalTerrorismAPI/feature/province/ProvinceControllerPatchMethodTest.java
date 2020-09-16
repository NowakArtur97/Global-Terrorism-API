package com.nowakArtur97.globalTerrorismAPI.feature.province;

import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryNode;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryRepository;
import com.nowakArtur97.globalTerrorismAPI.feature.region.RegionNode;
import com.nowakArtur97.globalTerrorismAPI.feature.user.RoleNode;
import com.nowakArtur97.globalTerrorismAPI.feature.user.UserNode;
import com.nowakArtur97.globalTerrorismAPI.feature.user.UserRepository;
import com.nowakArtur97.globalTerrorismAPI.common.mediaType.PatchMediaType;
import com.nowakArtur97.globalTerrorismAPI.testUtil.configuration.Neo4jTestConfiguration;
import com.nowakArtur97.globalTerrorismAPI.testUtil.database.Neo4jDatabaseUtil;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.nowakArtur97.globalTerrorismAPI.common.util.JwtUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(Neo4jTestConfiguration.class)
@AutoConfigureMockMvc
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("ProvinceController_Tests")
class ProvinceControllerPatchMethodTest {

    private final String REGION_BASE_PATH = "http://localhost:8080/api/v1/regions";
    private final String COUNTRY_BASE_PATH = "http://localhost:8080/api/v1/countries";
    private final String PROVINCE_BASE_PATH = "http://localhost:8080/api/v1/provinces";
    private final String LINK_WITH_PARAMETER_FOR_JSON_PATCH = PROVINCE_BASE_PATH + "/" + "{id}";
    private final String LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH = PROVINCE_BASE_PATH + "/" + "{id2}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    private final static UserNode userNode = new UserNode("user1234", "Password1234!", "user1234email@.com",
            Set.of(new RoleNode("user")));

    private final static RegionNode regionNode = new RegionNode("region name");
    private final static RegionNode anotherRegionNode = new RegionNode("another region name");

    private final static CountryNode countryNode = new CountryNode("country name", regionNode);
    private final static CountryNode anotherCountryNode = new CountryNode("another country name", anotherRegionNode);

    private final static ProvinceNode provinceNode = new ProvinceNode("province name", countryNode);
    private final static ProvinceNode anotherProvinceNode = new ProvinceNode("another province name", anotherCountryNode);
    private final static ProvinceNode anotherProvinceNode2 = new ProvinceNode("another province", countryNode);

    @BeforeAll
    private static void setUp(@Autowired UserRepository userRepository, @Autowired ProvinceRepository provinceRepository,
                              @Autowired CountryRepository countryRepository) {

        userRepository.save(userNode);

        countryRepository.save(anotherCountryNode);

        provinceRepository.save(provinceNode);
        provinceRepository.save(anotherProvinceNode);
        provinceRepository.save(anotherProvinceNode2);
    }

    @AfterAll
    private static void tearDown(@Autowired Neo4jDatabaseUtil neo4jDatabaseUtil) {

        neo4jDatabaseUtil.cleanDatabase();
    }

    @Nested
    class ProvinceControllerJsonPatchMethodTest {

        @Test
        void when_partial_update_province_using_json_patch_should_return_partially_updated_node() {

            String updatedProvinceName = "updated province";

            String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + provinceNode.getId().intValue();

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/name\", \"value\": \"" + updatedProvinceName + "\" }" +
                    "]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, provinceNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("links[1].href").doesNotExist())
                            .andExpect(jsonPath("id", is(provinceNode.getId().intValue())))
                            .andExpect(jsonPath("name", is(updatedProvinceName)))
                            .andExpect(jsonPath("country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("country.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("country.name", is(countryNode.getName())))
                            .andExpect(jsonPath("country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("country.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("country.region.name", is(regionNode.getName()))));
        }

        @Test
        void when_partial_update_province_country_using_json_patch_should_return_partially_updated_node() {

            String pathToRegionLink = REGION_BASE_PATH + "/" + anotherRegionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + anotherCountryNode.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + anotherProvinceNode2.getId().intValue();

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/country/name\", \"value\": \"" + anotherCountryNode.getName() + "\" }" +
                    "]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, anotherProvinceNode2.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("links[1].href").doesNotExist())
                            .andExpect(jsonPath("id", is(anotherProvinceNode2.getId().intValue())))
                            .andExpect(jsonPath("name", is(anotherProvinceNode2.getName())))
                            .andExpect(jsonPath("country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("country.id", is(anotherCountryNode.getId().intValue())))
                            .andExpect(jsonPath("country.name", is(anotherCountryNode.getName())))
                            .andExpect(jsonPath("country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("country.region.id", is(anotherRegionNode.getId().intValue())))
                            .andExpect(jsonPath("country.region.name", is(anotherRegionNode.getName()))));
        }

        @Test
        void when_partial_update_province_region_using_json_patch_should_return_node_without_changes() {

            String pathToRegionLink = REGION_BASE_PATH + "/" + anotherRegionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + anotherCountryNode.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + anotherProvinceNode.getId().intValue();

            String notExistingRegionName = "Not existing region";

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/country/region/name\", " +
                    "\"value\": \"" + notExistingRegionName + "\" }" +
                    "]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, anotherProvinceNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("links[1].href").doesNotExist())
                            .andExpect(jsonPath("id", is(anotherProvinceNode.getId().intValue())))
                            .andExpect(jsonPath("name", is(anotherProvinceNode.getName())))
                            .andExpect(jsonPath("country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("country.id", is(anotherCountryNode.getId().intValue())))
                            .andExpect(jsonPath("country.name", is(anotherCountryNode.getName())))
                            .andExpect(jsonPath("country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("country.region.id", is(anotherRegionNode.getId().intValue())))
                            .andExpect(jsonPath("country.region.name", is(anotherRegionNode.getName()))));
        }

        @Test
        void when_partial_update_valid_province_but_province_not_exist_using_json_patch_should_return_error_response() {

            Long notExistingId = 10000L;

            String updatedProvinceName = "updated province";

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/name\", \"value\": \"" + updatedProvinceName + "\" }" +
                    "]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, notExistingId)
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isNotFound())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("timestamp").isNotEmpty())
                            .andExpect(jsonPath("status", is(404)))
                            .andExpect(jsonPath("errors[0]", is("Could not find ProvinceModel with id: " + notExistingId + ".")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_with_null_fields_using_json_patch_should_return_errors() {

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/name\", \"value\": " + null + " }," +
                    "{ \"op\": \"replace\", \"path\": \"/country\", \"value\": " + null + " }" +
                    "]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, provinceNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Province name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("Country name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(2))));
        }

        @ParameterizedTest(name = "{index}: For Province name: {0}")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_province_with_invalid_name_using_json_patch_should_return_errors(String invalidProvinceName) {

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/name\", \"value\": \"" + invalidProvinceName + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, provinceNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Province name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_province_without_province_country_using_json_patch_should_return_errors() {

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/country\", \"value\": " + null + " }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, provinceNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Country name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_province_with_not_existing_country_using_json_patch_should_have_errors() {

            String notExistingCountryName = "not existing country";

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/country/name\", \"value\": \"" +
                    notExistingCountryName + "\"}]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, provinceNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("A country with the provided name does not exist.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }
    }

    @Nested
    class ProvinceControllerMergeJsonPatchMethodTest {

        @Test
        void when_partial_update_province_using_json_merge_patch_should_return_partially_updated_node() {

            String updatedProvinceName = "updated province name";

            String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + provinceNode.getId().intValue();

            String jsonMergePatch = "{\"name\" : \"" + updatedProvinceName + "\"}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, provinceNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("links[1].href").doesNotExist())
                            .andExpect(jsonPath("id", is(provinceNode.getId().intValue())))
                            .andExpect(jsonPath("name", is(updatedProvinceName)))
                            .andExpect(jsonPath("country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("country.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("country.name", is(countryNode.getName())))
                            .andExpect(jsonPath("country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("country.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("country.region.name", is(regionNode.getName()))));
        }

        @Test
        void when_partial_update_province_country_using_json_merge_patch_should_return_partially_updated_node() {

            String pathToRegionLink = REGION_BASE_PATH + "/" + anotherRegionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + anotherCountryNode.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + anotherProvinceNode2.getId().intValue();

            String jsonMergePatch = "{" +
                    "\"country\" :{ " +
                    "\"name\" : \"" + anotherCountryNode.getName() + "\"" +
                    "}" +
                    "}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, anotherProvinceNode2.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("links[1].href").doesNotExist())
                            .andExpect(jsonPath("id", is(anotherProvinceNode2.getId().intValue())))
                            .andExpect(jsonPath("name", is(anotherProvinceNode2.getName())))
                            .andExpect(jsonPath("country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("country.id", is(anotherCountryNode.getId().intValue())))
                            .andExpect(jsonPath("country.name", is(anotherCountryNode.getName())))
                            .andExpect(jsonPath("country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("country.region.id", is(anotherRegionNode.getId().intValue())))
                            .andExpect(jsonPath("country.region.name", is(anotherRegionNode.getName()))));
        }

        @Test
        void when_partial_update_province_region_using_json_merge_patch_should_return_node_without_changes() {

            String pathToRegionLink = REGION_BASE_PATH + "/" + anotherRegionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + anotherCountryNode.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + anotherProvinceNode.getId().intValue();

            String notExistingRegionName = "Not existing region";

            String jsonMergePatch = "{" +
                    "\"country\" :{ " +
                    "\"region\" :{ " +
                    "\"name\" : \"" + notExistingRegionName + "\"" +
                    "}" +
                    "}" +
                    "}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, anotherProvinceNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("links[1].href").doesNotExist())
                            .andExpect(jsonPath("id", is(anotherProvinceNode.getId().intValue())))
                            .andExpect(jsonPath("name", is(anotherProvinceNode.getName())))
                            .andExpect(jsonPath("country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("country.id", is(anotherCountryNode.getId().intValue())))
                            .andExpect(jsonPath("country.name", is(anotherCountryNode.getName())))
                            .andExpect(jsonPath("country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("country.region.id", is(anotherRegionNode.getId().intValue())))
                            .andExpect(jsonPath("country.region.name", is(anotherRegionNode.getName()))));
        }

        @Test
        void when_partial_update_valid_province_but_province_not_exist_using_json_merge_patch_should_return_error_response() {

            Long notExistingId = 10000L;

            String updatedProvinceName = "updated province";

            String jsonMergePatch = "{\"name\" : \"" + updatedProvinceName + "\"}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, notExistingId)
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isNotFound())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("timestamp").isNotEmpty())
                            .andExpect(jsonPath("status", is(404)))
                            .andExpect(jsonPath("errors[0]", is("Could not find ProvinceModel with id: " + notExistingId + ".")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_province_with_null_fields_using_json_merge_patch_should_return_errors() {

            String jsonMergePatch = "{\"name\" : " + null + ", \"country\" : " + null + "}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, provinceNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Country name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("Province name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(2))));
        }

        @ParameterizedTest(name = "{index}: For Province name: {0}")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_province_with_invalid_name_using_json_merge_patch_should_return_errors(String invalidProvinceName) {

            String jsonMergePatch = "{\"name\" : \"" + invalidProvinceName + "\" }";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, provinceNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Province name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_province_without_country_using_json_merge_patch_should_return_errors() {

            String jsonMergePatch = "{\"country\" : " + null + "}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, provinceNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Country name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_city_with_not_existing_country_using_json_merge_patch_should_have_errors() {

            String notExistingCountryName = "not existing country";

            String jsonMergePatch = "{\"country\" : {\"name\" : \"" + notExistingCountryName + "\"}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, provinceNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("A country with the provided name does not exist.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }
    }
}