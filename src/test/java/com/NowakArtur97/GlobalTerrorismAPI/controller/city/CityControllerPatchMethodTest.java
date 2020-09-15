package com.NowakArtur97.GlobalTerrorismAPI.controller.city;

import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionNode;
import com.NowakArtur97.GlobalTerrorismAPI.mediaType.PatchMediaType;
import com.NowakArtur97.GlobalTerrorismAPI.node.*;
import com.NowakArtur97.GlobalTerrorismAPI.repository.CityRepository;
import com.NowakArtur97.GlobalTerrorismAPI.repository.ProvinceRepository;
import com.NowakArtur97.GlobalTerrorismAPI.repository.UserRepository;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.configuration.Neo4jTestConfiguration;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.database.Neo4jDatabaseUtil;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.jwt.JwtUtil;
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
@Tag("CityController_Tests")
class CityControllerPatchMethodTest {

    private final String REGION_BASE_PATH = "http://localhost:8080/api/v1/regions";
    private final String COUNTRY_BASE_PATH = "http://localhost:8080/api/v1/countries";
    private final String PROVINCE_BASE_PATH = "http://localhost:8080/api/v1/provinces";
    private final String CITY_BASE_PATH = "http://localhost:8080/api/v1/cities";
    private final String LINK_WITH_PARAMETER_FOR_JSON_PATCH = CITY_BASE_PATH + "/" + "{id}";
    private final String LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH = CITY_BASE_PATH + "/" + "{id2}";

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
    private final static ProvinceNode anotherProvinceNode2 = new ProvinceNode("another province", anotherCountryNode);

    private final static CityNode cityNode = new CityNode("city name", 45.0, 45.0, provinceNode);
    private final static CityNode anotherCityNode = new CityNode("another city name", 15.0, 41.0,
            anotherProvinceNode);
    private final static CityNode anotherCityNode2 = new CityNode("another city", 12.0, 11.0,
            anotherProvinceNode);
    private final static CityNode anotherCityNode3 = new CityNode("another city 2", 11.0, 21.0,
            provinceNode);

    @BeforeAll
    private static void setUp(@Autowired UserRepository userRepository, @Autowired CityRepository cityRepository,
                              @Autowired ProvinceRepository provinceRepository) {

        userRepository.save(userNode);

        provinceRepository.save(anotherProvinceNode2);

        cityRepository.save(cityNode);
        cityRepository.save(anotherCityNode);
        cityRepository.save(anotherCityNode2);
        cityRepository.save(anotherCityNode3);
    }

    @AfterAll
    private static void tearDown(@Autowired Neo4jDatabaseUtil neo4jDatabaseUtil) {

        neo4jDatabaseUtil.cleanDatabase();
    }

    @Nested
    class CityControllerJsonPatchMethodTest {

        @Test
        void when_partial_update_city_using_json_patch_should_return_partially_updated_node() {

            String updatedCityName = "updated city";
            Double updatedCityLatitude = 20.0;
            Double updatedCityLongitude = 20.0;

            String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + provinceNode.getId().intValue();
            String pathToCityLink = CITY_BASE_PATH + "/" + cityNode.getId().intValue();

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/name\", \"value\": \"" + updatedCityName + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/latitude\", \"value\": " + updatedCityLatitude + " }," +
                    "{ \"op\": \"replace\", \"path\": \"/longitude\", \"value\": " + updatedCityLongitude + " }" +
                    "]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, cityNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToCityLink)))
                            .andExpect(jsonPath("links[1].href").doesNotExist())
                            .andExpect(jsonPath("id", is(cityNode.getId().intValue())))
                            .andExpect(jsonPath("name", is(updatedCityName)))
                            .andExpect(jsonPath("latitude", is(updatedCityLatitude)))
                            .andExpect(jsonPath("longitude", is(updatedCityLongitude)))
                            .andExpect(jsonPath("province.links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("province.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.id", is(provinceNode.getId().intValue())))
                            .andExpect(jsonPath("province.name", is(provinceNode.getName())))
                            .andExpect(jsonPath("province.country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("province.country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.country.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("province.country.name", is(countryNode.getName())))
                            .andExpect(jsonPath("province.country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("province.country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.country.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("province.country.region.name", is(regionNode.getName()))));
        }

        @Test
        void when_partial_update_city_with_province_using_json_patch_should_return_partially_updated_node() {

            String updatedCityName = "updated city";
            Double updatedCityLatitude = 20.0;
            Double updatedCityLongitude = 20.0;
            String updatedProvinceName = "updated province";

            String pathToRegionLink = REGION_BASE_PATH + "/" + anotherRegionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + anotherCountryNode.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + anotherProvinceNode.getId().intValue();
            String pathToCityLink = CITY_BASE_PATH + "/" + anotherCityNode.getId().intValue();

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/name\", \"value\": \"" + updatedCityName + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/latitude\", \"value\": " + updatedCityLatitude + " }," +
                    "{ \"op\": \"replace\", \"path\": \"/longitude\", \"value\": " + updatedCityLongitude + " }," +
                    "{ \"op\": \"replace\", \"path\": \"/province/name\", \"value\": \"" + updatedProvinceName + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/province/country/name\", \"value\": \"" + anotherCountryNode.getName() + "\" }" +
                    "]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, anotherCityNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToCityLink)))
                            .andExpect(jsonPath("links[1].href").doesNotExist())
                            .andExpect(jsonPath("id", is(anotherCityNode.getId().intValue())))
                            .andExpect(jsonPath("name", is(updatedCityName)))
                            .andExpect(jsonPath("latitude", is(updatedCityLatitude)))
                            .andExpect(jsonPath("longitude", is(updatedCityLongitude)))
                            .andExpect(jsonPath("province.links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("province.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.id", is(anotherProvinceNode.getId().intValue())))
                            .andExpect(jsonPath("province.name", is(updatedProvinceName)))
                            .andExpect(jsonPath("province.country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("province.country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.country.id", is(anotherCountryNode.getId().intValue())))
                            .andExpect(jsonPath("province.country.name", is(anotherCountryNode.getName())))
                            .andExpect(jsonPath("province.country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("province.country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.country.region.id", is(anotherRegionNode.getId().intValue())))
                            .andExpect(jsonPath("province.country.region.name", is(anotherRegionNode.getName()))));
        }

        @Test
        void when_partial_update_city_with_existing_province_using_json_patch_should_return_partially_updated_node() {

            String pathToRegionLink = REGION_BASE_PATH + "/" + anotherRegionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + anotherCountryNode.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + anotherProvinceNode2.getId().intValue();
            String pathToCityLink = CITY_BASE_PATH + "/" + anotherCityNode2.getId().intValue();

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/province/name\", \"value\": \"" + anotherProvinceNode2.getName() + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/province/country/name\", \"value\": \"" + anotherCountryNode.getName() + "\" }" +
                    "]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, anotherCityNode2.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToCityLink)))
                            .andExpect(jsonPath("links[1].href").doesNotExist())
                            .andExpect(jsonPath("id", is(anotherCityNode2.getId().intValue())))
                            .andExpect(jsonPath("name", is(anotherCityNode2.getName())))
                            .andExpect(jsonPath("latitude", is(anotherCityNode2.getLatitude())))
                            .andExpect(jsonPath("longitude", is(anotherCityNode2.getLongitude())))
                            .andExpect(jsonPath("province.links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("province.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.id", is(anotherProvinceNode2.getId().intValue())))
                            .andExpect(jsonPath("province.name", is(anotherProvinceNode2.getName())))
                            .andExpect(jsonPath("province.country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("province.country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.country.id", is(anotherCountryNode.getId().intValue())))
                            .andExpect(jsonPath("province.country.name", is(anotherCountryNode.getName())))
                            .andExpect(jsonPath("province.country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("province.country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.country.region.id", is(anotherRegionNode.getId().intValue())))
                            .andExpect(jsonPath("province.country.region.name", is(anotherRegionNode.getName()))));
        }

        @Test
        void when_partial_update_city_region_using_json_patch_should_return_node_without_changes() {

            String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + provinceNode.getId().intValue();
            String pathToCityLink = CITY_BASE_PATH + "/" + anotherCityNode3.getId().intValue();

            String notExistingRegionName = "Not existing region";

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/province/country/region/name\", " +
                    "\"value\": \"" + notExistingRegionName + "\" }" +
                    "]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, anotherCityNode3.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToCityLink)))
                            .andExpect(jsonPath("links[1].href").doesNotExist())
                            .andExpect(jsonPath("id", is(anotherCityNode3.getId().intValue())))
                            .andExpect(jsonPath("name", is(anotherCityNode3.getName())))
                            .andExpect(jsonPath("latitude", is(anotherCityNode3.getLatitude())))
                            .andExpect(jsonPath("longitude", is(anotherCityNode3.getLongitude())))
                            .andExpect(jsonPath("province.links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("province.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.id", is(provinceNode.getId().intValue())))
                            .andExpect(jsonPath("province.name", is(provinceNode.getName())))
                            .andExpect(jsonPath("province.country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("province.country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.country.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("province.country.name", is(countryNode.getName())))
                            .andExpect(jsonPath("province.country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("province.country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.country.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("province.country.region.name", is(regionNode.getName()))));
        }

        @Test
        void when_partial_update_valid_city_but_city_not_exist_using_json_patch_should_return_error_response() {

            Long notExistingId = 10000L;

            String updatedCityName = "updated city";
            double updatedCityLatitude = 20.0;
            double updatedCityLongitude = 20.0;

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/name\", \"value\": \"" + updatedCityName + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/latitude\", \"value\": " + updatedCityLatitude + " }," +
                    "{ \"op\": \"replace\", \"path\": \"/longitude\", \"value\": " + updatedCityLongitude + " }" +
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
                            .andExpect(jsonPath("errors[0]", is("Could not find CityModel with id: " + notExistingId + ".")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_invalid_city_with_null_fields_using_json_patch_should_return_errors() {

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/name\", \"value\": " + null + " }," +
                    "{ \"op\": \"replace\", \"path\": \"/latitude\", \"value\": " + null + " }," +
                    "{ \"op\": \"replace\", \"path\": \"/longitude\", \"value\": " + null + " }," +
                    "{ \"op\": \"replace\", \"path\": \"/province\", \"value\": " + null + " }" +
                    "]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, cityNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("City name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("City latitude cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("City longitude cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("Province name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(4))));

        }

        @ParameterizedTest(name = "{index}: For City City name: {0}")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_city_with_invalid_name_using_json_patch_should_return_errors(String invalidCityName) {

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/name\", \"value\": \"" + invalidCityName + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, cityNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("City name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_city_with_too_small_latitude_using_json_patch_should_return_errors() {

            double invalidCityLatitude = -91.0;

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/latitude\", \"value\": \"" + invalidCityLatitude + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, cityNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("City latitude must be greater or equal to -90.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_city_with_too_big_latitude_using_json_patch_should_return_errors() {

            double invalidCityLatitude = 91.0;

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/latitude\", \"value\": \"" + invalidCityLatitude + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, cityNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("City latitude must be less or equal to 90.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_city_with_too_small_longitude_using_json_patch_should_return_errors() {

            double invalidCityLongitude = -181.0;

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/longitude\", \"value\": \"" + invalidCityLongitude + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, cityNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("City longitude must be greater or equal to -180.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_city_with_too_big_longitude_using_json_patch_should_return_errors() {

            double invalidCityLongitude = 181.0;

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/longitude\", \"value\": \"" + invalidCityLongitude + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, cityNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("City longitude must be less or equal to 180.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_city_with_null_province_values_using_json_patch_should_return_errors() {

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/province/name\", \"value\": " + null + " }," +
                    "{ \"op\": \"replace\", \"path\": \"/province/country\", \"value\": " + null + " }" +
                    "]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, cityNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Province name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @ParameterizedTest(name = "{index}: For City Province name: {0}")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_city_with_invalid_province_name_using_json_patch_should_return_errors(String invalidProvinceName) {

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/province/name\", \"value\": \"" + invalidProvinceName + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, cityNode.getId())
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
        void when_partial_update_city_without_province_country_using_json_patch_should_return_errors() {

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/province/country\", \"value\": " + null + " }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, cityNode.getId())
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
        void when_partial_update_city_with_not_existing_country_using_json_patch_should_have_errors() {

            String notExistingCountryName = "not existing country";

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/province/country/name\", \"value\": \"" +
                    notExistingCountryName + "\"}]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, cityNode.getId())
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
    class CityControllerMergeJsonPatchMethodTest {

        @Test
        void when_partial_update_city_using_json_merge_patch_should_return_partially_updated_node() {

            String updatedCityName = "updated city";
            Double updatedCityLatitude = 20.0;
            Double updatedCityLongitude = 20.0;

            String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + provinceNode.getId().intValue();
            String pathToCityLink = CITY_BASE_PATH + "/" + cityNode.getId().intValue();

            String jsonMergePatch = "{" +
                    "\"name\" : \"" + updatedCityName + "\", " +
                    "\"latitude\" : " + updatedCityLatitude + ", " +
                    "\"longitude\" : " + updatedCityLongitude +
                    "}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, cityNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToCityLink)))
                            .andExpect(jsonPath("links[1].href").doesNotExist())
                            .andExpect(jsonPath("id", is(cityNode.getId().intValue())))
                            .andExpect(jsonPath("name", is(updatedCityName)))
                            .andExpect(jsonPath("latitude", is(updatedCityLatitude)))
                            .andExpect(jsonPath("longitude", is(updatedCityLongitude)))
                            .andExpect(jsonPath("province.links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("province.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.id", is(provinceNode.getId().intValue())))
                            .andExpect(jsonPath("province.name", is(provinceNode.getName())))
                            .andExpect(jsonPath("province.country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("province.country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.country.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("province.country.name", is(countryNode.getName())))
                            .andExpect(jsonPath("province.country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("province.country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.country.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("province.country.region.name", is(regionNode.getName()))));
        }

        @Test
        void when_partial_update_city_with_province_using_json_merge_patch_should_return_partially_updated_node() {

            String updatedCityName = "updated city";
            Double updatedCityLatitude = 20.0;
            Double updatedCityLongitude = 20.0;
            String updatedProvinceName = "updated province";

            String pathToRegionLink = REGION_BASE_PATH + "/" + anotherRegionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + anotherCountryNode.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + anotherProvinceNode.getId().intValue();
            String pathToCityLink = CITY_BASE_PATH + "/" + anotherCityNode.getId().intValue();

            String jsonMergePatch = "{" +
                    "\"name\" : \"" + updatedCityName + "\", " +
                    "\"latitude\" : " + updatedCityLatitude + ", " +
                    "\"longitude\" : " + updatedCityLongitude + ", " +
                    "\"province\" :{ " +
                    "\"name\" : \"" + updatedProvinceName + "\"," +
                    "\"country\" :{ " +
                    "\"name\" : \"" + anotherCountryNode.getName() + "\"" +
                    "}" +
                    "}" +
                    "}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, anotherCityNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToCityLink)))
                            .andExpect(jsonPath("links[1].href").doesNotExist())
                            .andExpect(jsonPath("id", is(anotherCityNode.getId().intValue())))
                            .andExpect(jsonPath("name", is(updatedCityName)))
                            .andExpect(jsonPath("latitude", is(updatedCityLatitude)))
                            .andExpect(jsonPath("longitude", is(updatedCityLongitude)))
                            .andExpect(jsonPath("province.links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("province.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.id", is(anotherProvinceNode.getId().intValue())))
                            .andExpect(jsonPath("province.name", is(updatedProvinceName)))
                            .andExpect(jsonPath("province.country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("province.country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.country.id", is(anotherCountryNode.getId().intValue())))
                            .andExpect(jsonPath("province.country.name", is(anotherCountryNode.getName())))
                            .andExpect(jsonPath("province.country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("province.country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.country.region.id", is(anotherRegionNode.getId().intValue())))
                            .andExpect(jsonPath("province.country.region.name", is(anotherRegionNode.getName()))));
        }

        @Test
        void when_partial_update_city_with_existing_province_using_json_merge_patch_should_return_partially_updated_node() {

            String pathToRegionLink = REGION_BASE_PATH + "/" + anotherRegionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + anotherCountryNode.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + anotherProvinceNode2.getId().intValue();
            String pathToCityLink = CITY_BASE_PATH + "/" + anotherCityNode2.getId().intValue();

            String jsonMergePatch = "{" +
                    "\"province\" :{ " +
                    "\"name\" : \"" + anotherProvinceNode2.getName() + "\"," +
                    "\"country\" :{ " +
                    "\"name\" : \"" + anotherCountryNode.getName() + "\"" +
                    "}" +
                    "}" +
                    "}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, anotherCityNode2.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToCityLink)))
                            .andExpect(jsonPath("links[1].href").doesNotExist())
                            .andExpect(jsonPath("id", is(anotherCityNode2.getId().intValue())))
                            .andExpect(jsonPath("name", is(anotherCityNode2.getName())))
                            .andExpect(jsonPath("latitude", is(anotherCityNode2.getLatitude())))
                            .andExpect(jsonPath("longitude", is(anotherCityNode2.getLongitude())))
                            .andExpect(jsonPath("province.links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("province.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.id", is(anotherProvinceNode2.getId().intValue())))
                            .andExpect(jsonPath("province.name", is(anotherProvinceNode2.getName())))
                            .andExpect(jsonPath("province.country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("province.country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.country.id", is(anotherCountryNode.getId().intValue())))
                            .andExpect(jsonPath("province.country.name", is(anotherCountryNode.getName())))
                            .andExpect(jsonPath("province.country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("province.country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.country.region.id", is(anotherRegionNode.getId().intValue())))
                            .andExpect(jsonPath("province.country.region.name", is(anotherRegionNode.getName()))));
        }

        @Test
        void when_partial_update_city_region_using_json_merge_patch_should_return_node_without_changes() {

            String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + provinceNode.getId().intValue();
            String pathToCityLink = CITY_BASE_PATH + "/" + anotherCityNode3.getId().intValue();

            String notExistingRegionName = "Not existing region";

            String jsonMergePatch = "{" +
                    "\"province\" :{ " +
                    "\"country\" :{ " +
                    "\"region\" :{ " +
                    "\"name\" : \"" + notExistingRegionName + "\"" +
                    "}" +
                    "}" +
                    "}" +
                    "}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, anotherCityNode3.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToCityLink)))
                            .andExpect(jsonPath("links[1].href").doesNotExist())
                            .andExpect(jsonPath("id", is(anotherCityNode3.getId().intValue())))
                            .andExpect(jsonPath("name", is(anotherCityNode3.getName())))
                            .andExpect(jsonPath("latitude", is(anotherCityNode3.getLatitude())))
                            .andExpect(jsonPath("longitude", is(anotherCityNode3.getLongitude())))
                            .andExpect(jsonPath("province.links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("province.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.id", is(provinceNode.getId().intValue())))
                            .andExpect(jsonPath("province.name", is(provinceNode.getName())))
                            .andExpect(jsonPath("province.country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("province.country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.country.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("province.country.name", is(countryNode.getName())))
                            .andExpect(jsonPath("province.country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("province.country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("province.country.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("province.country.region.name", is(regionNode.getName()))));
        }

        @Test
        void when_partial_update_valid_city_but_city_not_exist_using_json_merge_patch_should_return_error_response() {

            Long notExistingId = 10000L;

            String updatedCityName = "updated city";
            double updatedCityLatitude = 20.0;
            double updatedCityLongitude = 20.0;

            String jsonMergePatch = "{" +
                    "\"name\" : \"" + updatedCityName + "\", " +
                    "\"latitude\" : " + updatedCityLatitude + ", " +
                    "\"longitude\" : " + updatedCityLongitude +
                    "}";

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
                            .andExpect(jsonPath("errors[0]", is("Could not find CityModel with id: " + notExistingId + ".")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_invalid_city_with_null_fields_using_json_merge_patch_should_return_errors() {

            String jsonMergePatch = "{" +
                    "\"name\" : " + null + ", " +
                    "\"latitude\" : " + null + ", " +
                    "\"longitude\" : " + null + ", " +
                    "\"province\" : " + null +
                    "}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, cityNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("City name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("City latitude cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("City longitude cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("Province name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(4))));
        }

        @ParameterizedTest(name = "{index}: For City name: {0}")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_city_with_invalid_name_using_json_merge_patch_should_return_errors(String invalidCityName) {

            String jsonMergePatch = "{\"name\" : \"" + invalidCityName + "\" }";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, cityNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("City name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_city_with_too_small_latitude_using_json_merge_patch_should_return_errors() {

            double invalidCityLatitude = -91.0;

            String jsonMergePatch = "{\"latitude\" : \"" + invalidCityLatitude + "\"}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, cityNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("City latitude must be greater or equal to -90.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_city_with_too_big_latitude_using_json_merge_patch_should_return_errors() {

            double invalidCityLatitude = 91.0;

            String jsonMergePatch = "{\"latitude\" : \"" + invalidCityLatitude + "\"}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, cityNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("City latitude must be less or equal to 90.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_city_with_too_small_longitude_using_json_merge_patch_should_return_errors() {

            double invalidCityLongitude = -181.0;

            String jsonMergePatch = "{\"longitude\" : \"" + invalidCityLongitude + "\"}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, cityNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("City longitude must be greater or equal to -180.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_city_with_too_big_longitude_using_json_merge_patch_should_return_errors() {

            double invalidCityLongitude = 181.0;

            String jsonMergePatch = "{\"longitude\" : \"" + invalidCityLongitude + "\"}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, cityNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("City longitude must be less or equal to 180.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_city_with_null_province_values_using_json_merge_patch_should_return_errors() {

            String jsonMergePatch = "{" +
                    "\"province\" : {\"name\" : " + null + ", \"country\" : " + null + "}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, cityNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Province name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @ParameterizedTest(name = "{index}: For City Province name: {0}")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_city_with_invalid_province_name_using_json_merge_patch_should_return_errors(String invalidProvinceName) {

            String jsonMergePatch = "{\"province\" : {\"name\" : \"" + invalidProvinceName + "\"}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, cityNode.getId())
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
        void when_partial_update_city_without_province_country_using_json_merge_patch_should_return_errors() {

            String jsonMergePatch = "{\"province\" : {\"country\" : " + null + "}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, cityNode.getId())
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

            String jsonMergePatch = "{\"province\" : {\"country\" : {\"name\" : \"" + notExistingCountryName + "\"}}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, cityNode.getId())
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