package com.nowakArtur97.globalTerrorismAPI.feature.target;

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
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(Neo4jTestConfiguration.class)
@AutoConfigureMockMvc
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("TargetController_Tests")
class TargetControllerPatchMethodTest {

    private final String REGION_BASE_PATH = "http://localhost:8080/api/v1/regions";
    private final String COUNTRY_BASE_PATH = "http://localhost:8080/api/v1/countries";
    private final String TARGET_BASE_PATH = "http://localhost:8080/api/v1/targets";
    private final String LINK_WITH_PARAMETER_FOR_JSON_PATCH = TARGET_BASE_PATH + "/" + "{id}";
    private final String LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH = TARGET_BASE_PATH + "/" + "{id2}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    private final static UserNode userNode = new UserNode("user1234", "Password1234!", "user1234email@.com",
            Set.of(new RoleNode("user")));

    private final static RegionNode regionNode = new RegionNode("region");

    private final static CountryNode countryNode = new CountryNode("country", regionNode);
    private final static CountryNode anotherCountryNode = new CountryNode("another country", regionNode);

    private final static TargetNode targetNode = new TargetNode("target", countryNode);
    private final static TargetNode anotherTargetNode = new TargetNode("target 2", countryNode);

    @BeforeAll
    private static void setUp(@Autowired UserRepository userRepository, @Autowired TargetRepository targetRepository, @Autowired CountryRepository countryRepository) {

        userRepository.save(userNode);

        countryRepository.save(anotherCountryNode);

        targetRepository.save(targetNode);
        targetRepository.save(anotherTargetNode);
    }

    @AfterAll
    private static void tearDown(@Autowired Neo4jDatabaseUtil neo4jDatabaseUtil) {

        neo4jDatabaseUtil.cleanDatabase();
    }

    @Nested
    class TargetControllerJsonPatchMethodTest {

        @Test
        void when_partial_update_valid_target_using_json_patch_should_return_partially_updated_node() {

            String updatedTargetName = "updated target";

            String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + anotherCountryNode.getId().intValue();
            String pathToLink = TARGET_BASE_PATH + "/" + targetNode.getId().intValue();

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/target\", \"value\": \"" + updatedTargetName + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/countryOfOrigin/name\", \"value\": \"" + anotherCountryNode.getName() + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, targetNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToLink)))
                            .andExpect(jsonPath("links[1].href").doesNotExist())
                            .andExpect(jsonPath("id", is(targetNode.getId().intValue())))
                            .andExpect(jsonPath("target", is(updatedTargetName)))
                            .andExpect(jsonPath("countryOfOrigin.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("countryOfOrigin.links[1].href").doesNotExist())
                            .andExpect(jsonPath("countryOfOrigin.id", is(anotherCountryNode.getId().intValue())))
                            .andExpect(jsonPath("countryOfOrigin.name", is(anotherCountryNode.getName())))
                            .andExpect(jsonPath("countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("countryOfOrigin.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("countryOfOrigin.region.name", is(regionNode.getName()))));
        }

        @Test
        void when_partial_update_target_region_using_json_patch_should_return_node_without_changes() {

            String notExistingRegionName = "Not existing region";

            String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
            String pathToLink = TARGET_BASE_PATH + "/" + anotherTargetNode.getId().intValue();

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/countryOfOrigin/region/name\", \"value\": \"" + notExistingRegionName + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, anotherTargetNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToLink)))
                            .andExpect(jsonPath("links[1].href").doesNotExist())
                            .andExpect(jsonPath("id", is(anotherTargetNode.getId().intValue())))
                            .andExpect(jsonPath("target", is(anotherTargetNode.getTarget())))
                            .andExpect(jsonPath("countryOfOrigin.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("countryOfOrigin.links[1].href").doesNotExist())
                            .andExpect(jsonPath("countryOfOrigin.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("countryOfOrigin.name", is(countryNode.getName())))
                            .andExpect(jsonPath("countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("countryOfOrigin.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("countryOfOrigin.region.name", is(regionNode.getName()))));
        }

        @Test
        void when_partial_update_target_using_json_patch_but_target_not_exists_should_return_error_response() {

            Long notExistingId = 1000L;

            String updatedTargetName = "updated target";

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/target\", \"value\": \"" + updatedTargetName + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/countryOfOrigin/name\", \"value\": \"" + anotherCountryNode.getName() + "\" }]";

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
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(404)))
                            .andExpect(jsonPath("errors[0]",
                                    is("Could not find TargetModel with id: " + notExistingId + ".")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @ParameterizedTest(name = "{index}: Target Name: {0}")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_invalid_target_using_json_patch_should_return_errors(String invalidTargetName) {

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/target\", \"value\": \"" + invalidTargetName + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, targetNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Target name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @ParameterizedTest(name = "{index}: Target Country: {0}")
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_target_with_null_values_using_json_patch_should_return_error_response(String invalidCountryName) {

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/countryOfOrigin/name\", \"value\": \"" + invalidCountryName + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, targetNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("A country with the provided name does not exist.")))
                            .andExpect(jsonPath("errors", Matchers.hasSize(1))));
        }

        @Test
        void when_partial_update_target_with_country_as_null_using_json_patch_should_return_errors() {

            String updatedTargetName = "updated target";

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/target\", \"value\": \"" + updatedTargetName + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/countryOfOrigin/name\", \"value\": " + null + " }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, targetNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Country name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_valid_target_with_not_existing_country_using_json_patch_should_return_errors() {

            String updatedTargetName = "updated target";
            String notExistingCountryName = "not existing country";

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/target\", \"value\": \"" + updatedTargetName + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/countryOfOrigin/name\", \"value\": \"" + notExistingCountryName + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, targetNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("A country with the provided name does not exist.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }
    }

    @Nested
    class TargetControllerJsonMergePatchMethodTest {
        @Test
        void when_partial_update_valid_target_using_json_merge_patch_should_return_partially_updated_node() {

            String updatedTargetName = "updated target";

            String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + anotherCountryNode.getId().intValue();
            String pathToLink = TARGET_BASE_PATH + "/" + targetNode.getId().intValue();

            String jsonMergePatch =
                    "{\"target\" : \"" + updatedTargetName + "\", " +
                            "\"countryOfOrigin\" : { \"name\" : \"" + anotherCountryNode.getName() + "\" }}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, targetNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToLink)))
                            .andExpect(jsonPath("links[1].href").doesNotExist())
                            .andExpect(jsonPath("id", is(targetNode.getId().intValue())))
                            .andExpect(jsonPath("target", is(updatedTargetName)))
                            .andExpect(jsonPath("countryOfOrigin.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("countryOfOrigin.links[1].href").doesNotExist())
                            .andExpect(jsonPath("countryOfOrigin.id", is(anotherCountryNode.getId().intValue())))
                            .andExpect(jsonPath("countryOfOrigin.name", is(anotherCountryNode.getName())))
                            .andExpect(jsonPath("countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("countryOfOrigin.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("countryOfOrigin.region.name", is(regionNode.getName()))));
        }

        @Test
        void when_partial_update_target_region_using_json_merge_patch_should_return_node_without_changes() {

            String notExistingRegionName = "Not existing region";

            String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
            String pathToLink = TARGET_BASE_PATH + "/" + anotherTargetNode.getId().intValue();

            String jsonMergePatch =
                    "{\"countryOfOrigin\" : { \"region\" : { \"name\" : \"" + notExistingRegionName + "\" }}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, anotherTargetNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToLink)))
                            .andExpect(jsonPath("links[1].href").doesNotExist())
                            .andExpect(jsonPath("id", is(anotherTargetNode.getId().intValue())))
                            .andExpect(jsonPath("target", is(anotherTargetNode.getTarget())))
                            .andExpect(jsonPath("countryOfOrigin.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("countryOfOrigin.links[1].href").doesNotExist())
                            .andExpect(jsonPath("countryOfOrigin.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("countryOfOrigin.name", is(countryNode.getName())))
                            .andExpect(jsonPath("countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("countryOfOrigin.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("countryOfOrigin.region.name", is(regionNode.getName()))));
        }

        @Test
        void when_partial_update_target_using_json_merge_patch_but_target_not_exists_should_return_error_response() {

            Long notExistingId = 1000L;

            String updatedTargetName = "updated target";

            String jsonMergePatch =
                    "{\"target\" : \"" + updatedTargetName + "\", " +
                            "\"countryOfOrigin\" : { \"name\" : \"" + anotherCountryNode.getName() + "\" }}";

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
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(404)))
                            .andExpect(jsonPath("errors[0]", is("Could not find TargetModel with id: " + notExistingId + ".")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @ParameterizedTest(name = "{index}: Target Name: {0}")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_invalid_target_using_json_merge_patch_should_return_errors(String invalidTargetName) {

            String jsonMergePatch = "{\"target\" : \"" + invalidTargetName + "\"}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, targetNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Target name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @ParameterizedTest(name = "{index}: Target Country: {0}")
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_target_with_null_values_using_json_merge_patch_should_return_error_response(String invalidCountryName) {

            String jsonMergePatch = "{\"countryOfOrigin\" : { \"name\" : \"" + invalidCountryName + "\" }}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, targetNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("A country with the provided name does not exist.")))
                            .andExpect(jsonPath("errors", Matchers.hasSize(1))));
        }

        @Test
        void when_partial_update_target_with_country_as_null_using_json_merge_patch_should_return_errors() {

            String jsonMergePatch = "{\"countryOfOrigin\" : { \"name\" : " + null + " }}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, targetNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Country name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_valid_target_with_not_existing_country_using_json_merge_patch_should_return_errors() {

            String notExistingRegionName = "not existing region";

            String jsonMergePatch = "{\"countryOfOrigin\" : { \"name\" : \"" + notExistingRegionName + "\" }}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, targetNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("A country with the provided name does not exist.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }
    }
}