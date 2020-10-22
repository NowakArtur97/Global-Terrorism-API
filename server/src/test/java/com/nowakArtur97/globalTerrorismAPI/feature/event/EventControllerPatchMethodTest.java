package com.nowakArtur97.globalTerrorismAPI.feature.event;

import com.nowakArtur97.globalTerrorismAPI.common.mediaType.PatchMediaType;
import com.nowakArtur97.globalTerrorismAPI.common.util.JwtUtil;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityNode;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityRepository;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryNode;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceNode;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceRepository;
import com.nowakArtur97.globalTerrorismAPI.feature.region.RegionNode;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetNode;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetRepository;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.RoleNode;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.UserNode;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.UserRepository;
import com.nowakArtur97.globalTerrorismAPI.feature.victim.VictimNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.configuration.Neo4jTestConfiguration;
import com.nowakArtur97.globalTerrorismAPI.testUtil.database.Neo4jDatabaseUtil;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
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

import java.util.Date;
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
@Tag("EventController_Tests")
class EventControllerPatchMethodTest {

    private final String REGION_BASE_PATH = "http://localhost:8080/api/v1/regions";
    private final String COUNTRY_BASE_PATH = "http://localhost:8080/api/v1/countries";
    private final String TARGET_BASE_PATH = "http://localhost:8080/api/v1/targets";
    private final String PROVINCE_BASE_PATH = "http://localhost:8080/api/v1/provinces";
    private final String CITY_BASE_PATH = "http://localhost:8080/api/v1/cities";
    private final String VICTIM_BASE_PATH = "http://localhost:8080/api/v1/victims";
    private final String EVENT_BASE_PATH = "http://localhost:8080/api/v1/events";
    private final String LINK_WITH_PARAMETER_FOR_JSON_PATCH = EVENT_BASE_PATH + "/" + "{id}";
    private final String LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH = EVENT_BASE_PATH + "/" + "{id2}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    private final static UserNode userNode = new UserNode("user1234", "Password1234!", "user1234email@.com",
            Set.of(new RoleNode("user")));

    private final static RegionNode regionNode = new RegionNode("region");
    private final static RegionNode anotherRegionNode = new RegionNode("another region");
    private final static RegionNode anotherRegionNode2 = new RegionNode("another region 2");

    private final static CountryNode countryNode = new CountryNode("country", regionNode);
    private final static CountryNode anotherCountryNode = new CountryNode("another country", anotherRegionNode);
    private final static CountryNode anotherCountryNode2 = new CountryNode("another country 2", anotherRegionNode2);

    private final static TargetNode targetNode = new TargetNode("target", countryNode);
    private final static TargetNode anotherTargetNode = new TargetNode("target2", countryNode);
    private final static TargetNode anotherTargetNode2 = new TargetNode("target 3", anotherCountryNode2);
    private final static TargetNode anotherTargetNode3 = new TargetNode("target 4", countryNode);
    private final static TargetNode anotherTargetNode4 = new TargetNode("target 5", countryNode);

    private final static ProvinceNode provinceNode = new ProvinceNode("province", countryNode);
    private final static ProvinceNode anotherProvinceNode = new ProvinceNode("province 2", anotherCountryNode);
    private final static ProvinceNode anotherProvinceNode2 = new ProvinceNode("province 3", anotherCountryNode2);
    private final static ProvinceNode anotherProvinceNode3 = new ProvinceNode("province 4", countryNode);
    private final static ProvinceNode anotherProvinceNode4 = new ProvinceNode("province 5", countryNode);

    private final static CityNode cityNode = new CityNode("city", 45.0, 45.0, provinceNode);
    private final static CityNode anotherCityNode = new CityNode("city 2", 15.0, -35.0, anotherProvinceNode2);
    private final static CityNode anotherCityNode2 = new CityNode("city 3", 11.0, -32.0, anotherProvinceNode3);
    private final static CityNode anotherCityNode3 = new CityNode("city 4", 41.0, -12.0, anotherProvinceNode4);

    private final static VictimNode victimNode = new VictimNode(10L, 2L, 13L, 3L, 1000L);
    private final static VictimNode anotherVictimNode = new VictimNode(20L, 12L, 14L, 4L, 2000L);

    private final static EventNode eventNode = new EventNode("summary", "motive", new Date(),
            true, true, true, targetNode, cityNode, victimNode);
    private final static EventNode anotherEventNode = new EventNode("summary2", "motive2", new Date(),
            false, false, false, anotherTargetNode, cityNode, victimNode);
    private final static EventNode anotherEventNode2 = new EventNode("summary3", "motive3", new Date(),
            true, false, true, anotherTargetNode2, anotherCityNode, victimNode);
    private final static EventNode anotherEventNode3 = new EventNode("summary4", "motive4", new Date(),
            false, false, true, anotherTargetNode3, anotherCityNode2, victimNode);
    private final static EventNode anotherEventNode4 = new EventNode("summary5", "motive5", new Date(),
            false, true, true, anotherTargetNode4, anotherCityNode3, anotherVictimNode);

    @BeforeAll
    private static void setUp(@Autowired UserRepository userRepository, @Autowired EventRepository eventRepository,
                              @Autowired TargetRepository targetRepository, @Autowired ProvinceRepository provinceRepository,
                              @Autowired CityRepository cityRepository) {

        userRepository.save(userNode);

        provinceRepository.save(anotherProvinceNode);
        provinceRepository.save(anotherProvinceNode2);

        cityRepository.save(cityNode);
        cityRepository.save(anotherCityNode2);

        eventRepository.save(eventNode);
        eventRepository.save(anotherEventNode);
        eventRepository.save(anotherEventNode2);
        eventRepository.save(anotherEventNode3);
        eventRepository.save(anotherEventNode4);
    }

    @AfterAll
    private static void tearDown(@Autowired Neo4jDatabaseUtil neo4jDatabaseUtil) {

        neo4jDatabaseUtil.cleanDatabase();
    }

    @Nested
    class EventControllerJsonPatchMethodTest {

        @Test
        void when_partial_update_valid_event_using_json_patch_should_return_partially_updated_node() {

            String updatedSummary = "summary updated";
            String updatedMotive = "motive updated";
            String updatedEventDateString = "2001-08-05";
            boolean updatedIsPartOfMultipleIncidents = false;
            boolean updatedIsSuccessful = false;
            boolean updatedIsSuicidal = false;

            String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + provinceNode.getId().intValue();
            String pathToTargetLink = TARGET_BASE_PATH + "/" + targetNode.getId().intValue();
            String pathToCityLink = CITY_BASE_PATH + "/" + cityNode.getId().intValue();
            String pathToVictimLink = VICTIM_BASE_PATH + "/" + victimNode.getId().intValue();
            String pathToEventLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue();
            String pathToTargetEventLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue() + "/targets";

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/summary\", \"value\": \"" + updatedSummary + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/motive\", \"value\": \"" + updatedMotive + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/date\", \"value\": \"" + updatedEventDateString + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/isPartOfMultipleIncidents\", \"value\": \"" + updatedIsPartOfMultipleIncidents + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/isSuccessful\", \"value\": \"" + updatedIsSuccessful + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/isSuicidal\", \"value\": \"" + updatedIsSuicidal + "\" }" +
                    "]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToEventLink)))
                            .andExpect(jsonPath("links[1].href", is(pathToTargetEventLink)))
                            .andExpect(jsonPath("id", is(eventNode.getId().intValue())))
                            .andExpect(jsonPath("summary", is(updatedSummary)))
                            .andExpect(jsonPath("motive", is(updatedMotive)))
                            .andExpect(jsonPath("date", is(notNullValue())))
                            .andExpect(jsonPath("isSuicidal", is(updatedIsSuicidal)))
                            .andExpect(jsonPath("isSuccessful", is(updatedIsSuccessful)))
                            .andExpect(jsonPath("isPartOfMultipleIncidents", is(updatedIsPartOfMultipleIncidents)))
                            .andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
                            .andExpect(jsonPath("target.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.id", is(targetNode.getId().intValue())))
                            .andExpect(jsonPath("target.target", is(targetNode.getTarget())))
                            .andExpect(jsonPath("target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("target.countryOfOrigin.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.name", is(countryNode.getName())))
                            .andExpect(jsonPath("target.countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("target.countryOfOrigin.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.region.name", is(regionNode.getName())))
                            .andExpect(jsonPath("city.links[0].href", is(pathToCityLink)))
                            .andExpect(jsonPath("city.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.id", is(cityNode.getId().intValue())))
                            .andExpect(jsonPath("city.name", is(cityNode.getName())))
                            .andExpect(jsonPath("city.latitude", is(cityNode.getLatitude())))
                            .andExpect(jsonPath("city.longitude", is(cityNode.getLongitude())))
                            .andExpect(jsonPath("city.province.links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("city.province.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.id", is(provinceNode.getId().intValue())))
                            .andExpect(jsonPath("city.province.name", is(provinceNode.getName())))
                            .andExpect(jsonPath("city.province.country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("city.province.country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.country.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("city.province.country.name", is(countryNode.getName())))
                            .andExpect(jsonPath("city.province.country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("city.province.country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.country.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("city.province.country.region.name", is(regionNode.getName())))
                            .andExpect(jsonPath("victim.links[0].href", is(pathToVictimLink)))
                            .andExpect(jsonPath("victim.links[1].href").doesNotExist())
                            .andExpect(jsonPath("victim.id", is(victimNode.getId().intValue())))
                            .andExpect(jsonPath("victim.totalNumberOfFatalities",
                                    is(victimNode.getTotalNumberOfFatalities().intValue())))
                            .andExpect(jsonPath("victim.numberOfPerpetratorFatalities",
                                    is(victimNode.getNumberOfPerpetratorFatalities().intValue())))
                            .andExpect(jsonPath("victim.totalNumberOfInjured",
                                    is(victimNode.getTotalNumberOfInjured().intValue())))
                            .andExpect(jsonPath("victim.numberOfPerpetratorInjured",
                                    is(victimNode.getNumberOfPerpetratorInjured().intValue())))
                            .andExpect(jsonPath("victim.valueOfPropertyDamage",
                                    is(victimNode.getValueOfPropertyDamage().intValue()))));
        }

        @Test
        void when_partial_update_valid_events_target_using_json_patch_should_return_partially_updated_node() {

            String updatedTargetName = "updated target";

            String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + provinceNode.getId().intValue();
            String pathToTargetLink = TARGET_BASE_PATH + "/" + anotherTargetNode.getId().intValue();
            String pathToCityLink = CITY_BASE_PATH + "/" + cityNode.getId().intValue();
            String pathToVictimLink = VICTIM_BASE_PATH + "/" + victimNode.getId().intValue();
            String pathToEventLink = EVENT_BASE_PATH + "/" + anotherEventNode.getId().intValue();
            String pathToTargetEventLink = EVENT_BASE_PATH + "/" + anotherEventNode.getId().intValue() + "/targets";

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/target/target\", \"value\": \"" + updatedTargetName + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, anotherEventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToEventLink)))
                            .andExpect(jsonPath("links[1].href", is(pathToTargetEventLink)))
                            .andExpect(jsonPath("id", is(anotherEventNode.getId().intValue())))
                            .andExpect(jsonPath("summary", is(anotherEventNode.getSummary())))
                            .andExpect(jsonPath("motive", is(anotherEventNode.getMotive())))
                            .andExpect(jsonPath("date", is(notNullValue())))
                            .andExpect(jsonPath("isSuicidal", is(anotherEventNode.getIsSuicidal())))
                            .andExpect(jsonPath("isSuccessful", is(anotherEventNode.getIsSuccessful())))
                            .andExpect(jsonPath("isPartOfMultipleIncidents",
                                    is(anotherEventNode.getIsPartOfMultipleIncidents())))
                            .andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
                            .andExpect(jsonPath("target.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.id", is(anotherTargetNode.getId().intValue())))
                            .andExpect(jsonPath("target.target", is(updatedTargetName)))
                            .andExpect(jsonPath("target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("target.countryOfOrigin.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.name", is(countryNode.getName())))
                            .andExpect(jsonPath("target.countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("target.countryOfOrigin.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.region.name", is(regionNode.getName())))
                            .andExpect(jsonPath("city.links[0].href", is(pathToCityLink)))
                            .andExpect(jsonPath("city.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.id", is(cityNode.getId().intValue())))
                            .andExpect(jsonPath("city.name", is(cityNode.getName())))
                            .andExpect(jsonPath("city.latitude", is(cityNode.getLatitude())))
                            .andExpect(jsonPath("city.longitude", is(cityNode.getLongitude())))
                            .andExpect(jsonPath("city.province.links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("city.province.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.id", is(provinceNode.getId().intValue())))
                            .andExpect(jsonPath("city.province.name", is(provinceNode.getName())))
                            .andExpect(jsonPath("city.province.country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("city.province.country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.country.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("city.province.country.name", is(countryNode.getName())))
                            .andExpect(jsonPath("city.province.country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("city.province.country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.country.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("city.province.country.region.name", is(regionNode.getName())))
                            .andExpect(jsonPath("victim.links[0].href", is(pathToVictimLink)))
                            .andExpect(jsonPath("victim.links[1].href").doesNotExist())
                            .andExpect(jsonPath("victim.id", is(victimNode.getId().intValue())))
                            .andExpect(jsonPath("victim.totalNumberOfFatalities",
                                    is(victimNode.getTotalNumberOfFatalities().intValue())))
                            .andExpect(jsonPath("victim.numberOfPerpetratorFatalities",
                                    is(victimNode.getNumberOfPerpetratorFatalities().intValue())))
                            .andExpect(jsonPath("victim.totalNumberOfInjured",
                                    is(victimNode.getTotalNumberOfInjured().intValue())))
                            .andExpect(jsonPath("victim.numberOfPerpetratorInjured",
                                    is(victimNode.getNumberOfPerpetratorInjured().intValue())))
                            .andExpect(jsonPath("victim.valueOfPropertyDamage",
                                    is(victimNode.getValueOfPropertyDamage().intValue()))));
        }

        @Test
        void when_partial_update_valid_events_city_using_json_patch_should_return_partially_updated_node() {

            String updatedCityName = "updated city";
            Double updatedCityLatitude = 20.0;
            Double updatedCityLongitude = 20.0;
            String updatedProvinceName = "updated province";
            String updatedCountryName = anotherCountryNode2.getName();

            String pathToRegionLink = REGION_BASE_PATH + "/" + anotherRegionNode2.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + anotherCountryNode2.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + anotherProvinceNode2.getId().intValue();
            String pathToTargetLink = TARGET_BASE_PATH + "/" + anotherTargetNode2.getId().intValue();
            String pathToCityLink = CITY_BASE_PATH + "/" + anotherCityNode.getId().intValue();
            String pathToVictimLink = VICTIM_BASE_PATH + "/" + victimNode.getId().intValue();
            String pathToEventLink = EVENT_BASE_PATH + "/" + anotherEventNode2.getId().intValue();
            String pathToTargetEventLink = EVENT_BASE_PATH + "/" + anotherEventNode2.getId().intValue() + "/targets";

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/city/name\", \"value\": \"" + updatedCityName + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/city/latitude\", \"value\": " + updatedCityLatitude + " }," +
                    "{ \"op\": \"replace\", \"path\": \"/city/longitude\", \"value\": " + updatedCityLongitude + " }," +
                    "{ \"op\": \"replace\", \"path\": \"/city/province/name\", \"value\": \"" + updatedProvinceName + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/city/province/country/name\", \"value\": \"" + updatedCountryName + "\" }" +
                    "]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, anotherEventNode2.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToEventLink)))
                            .andExpect(jsonPath("links[1].href", is(pathToTargetEventLink)))
                            .andExpect(jsonPath("id", is(anotherEventNode2.getId().intValue())))
                            .andExpect(jsonPath("summary", is(anotherEventNode2.getSummary())))
                            .andExpect(jsonPath("motive", is(anotherEventNode2.getMotive())))
                            .andExpect(jsonPath("date", is(notNullValue())))
                            .andExpect(jsonPath("isSuicidal", is(anotherEventNode2.getIsSuicidal())))
                            .andExpect(jsonPath("isSuccessful", is(anotherEventNode2.getIsSuccessful())))
                            .andExpect(jsonPath("isPartOfMultipleIncidents", is(anotherEventNode2.getIsPartOfMultipleIncidents())))
                            .andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
                            .andExpect(jsonPath("target.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.id", is(anotherTargetNode2.getId().intValue())))
                            .andExpect(jsonPath("target.target", is(anotherTargetNode2.getTarget())))
                            .andExpect(jsonPath("target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("target.countryOfOrigin.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.countryOfOrigin.id", is(anotherCountryNode2.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.name", is(anotherCountryNode2.getName())))
                            .andExpect(jsonPath("target.countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("target.countryOfOrigin.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.countryOfOrigin.region.id", is(anotherRegionNode2.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.region.name", is(anotherRegionNode2.getName())))
                            .andExpect(jsonPath("city.links[0].href", is(pathToCityLink)))
                            .andExpect(jsonPath("city.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.id", notNullValue()))
                            .andExpect(jsonPath("city.name", is(updatedCityName)))
                            .andExpect(jsonPath("city.latitude", is(updatedCityLatitude)))
                            .andExpect(jsonPath("city.longitude", is(updatedCityLongitude)))
                            .andExpect(jsonPath("city.province.links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("city.province.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.id", notNullValue()))
                            .andExpect(jsonPath("city.province.name", is(updatedProvinceName)))
                            .andExpect(jsonPath("city.province.country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("city.province.country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.country.id", is(anotherCountryNode2.getId().intValue())))
                            .andExpect(jsonPath("city.province.country.name", is(updatedCountryName)))
                            .andExpect(jsonPath("city.province.country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("city.province.country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.country.region.id", is(anotherRegionNode2.getId().intValue())))
                            .andExpect(jsonPath("city.province.country.region.name", is(anotherRegionNode2.getName())))
                            .andExpect(jsonPath("victim.links[0].href", is(pathToVictimLink)))
                            .andExpect(jsonPath("victim.links[1].href").doesNotExist())
                            .andExpect(jsonPath("victim.id", is(victimNode.getId().intValue())))
                            .andExpect(jsonPath("victim.totalNumberOfFatalities",
                                    is(victimNode.getTotalNumberOfFatalities().intValue())))
                            .andExpect(jsonPath("victim.numberOfPerpetratorFatalities",
                                    is(victimNode.getNumberOfPerpetratorFatalities().intValue())))
                            .andExpect(jsonPath("victim.totalNumberOfInjured",
                                    is(victimNode.getTotalNumberOfInjured().intValue())))
                            .andExpect(jsonPath("victim.numberOfPerpetratorInjured",
                                    is(victimNode.getNumberOfPerpetratorInjured().intValue())))
                            .andExpect(jsonPath("victim.valueOfPropertyDamage",
                                    is(victimNode.getValueOfPropertyDamage().intValue()))));
        }

        @Test
        void when_partial_update_event_region_using_json_patch_should_return_node_without_changes() {

            String notExistingRegionName = "not existing region";

            String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + anotherProvinceNode3.getId().intValue();
            String pathToTargetLink = TARGET_BASE_PATH + "/" + anotherTargetNode3.getId().intValue();
            String pathToCityLink = CITY_BASE_PATH + "/" + anotherCityNode2.getId().intValue();
            String pathToVictimLink = VICTIM_BASE_PATH + "/" + victimNode.getId().intValue();
            String pathToEventLink = EVENT_BASE_PATH + "/" + anotherEventNode3.getId().intValue();
            String pathToTargetEventLink = EVENT_BASE_PATH + "/" + anotherEventNode3.getId().intValue() + "/targets";

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/city/province/country/region/name\", \"value\": \"" + notExistingRegionName + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/target/countryOfOrigin/region/name\", \"value\": \"" + notExistingRegionName + "\" }" +
                    "]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, anotherEventNode3.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToEventLink)))
                            .andExpect(jsonPath("links[1].href", is(pathToTargetEventLink)))
                            .andExpect(jsonPath("id", is(anotherEventNode3.getId().intValue())))
                            .andExpect(jsonPath("summary", is(anotherEventNode3.getSummary())))
                            .andExpect(jsonPath("motive", is(anotherEventNode3.getMotive())))
                            .andExpect(jsonPath("date", is(notNullValue())))
                            .andExpect(jsonPath("isSuicidal", is(anotherEventNode3.getIsSuicidal())))
                            .andExpect(jsonPath("isSuccessful", is(anotherEventNode3.getIsSuccessful())))
                            .andExpect(jsonPath("isPartOfMultipleIncidents", is(anotherEventNode3.getIsPartOfMultipleIncidents())))
                            .andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
                            .andExpect(jsonPath("target.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.id", is(anotherTargetNode3.getId().intValue())))
                            .andExpect(jsonPath("target.target", is(anotherTargetNode3.getTarget())))
                            .andExpect(jsonPath("target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("target.countryOfOrigin.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.name", is(countryNode.getName())))
                            .andExpect(jsonPath("target.countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("target.countryOfOrigin.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.region.name", is(regionNode.getName())))
                            .andExpect(jsonPath("city.links[0].href", is(pathToCityLink)))
                            .andExpect(jsonPath("city.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.id", is(anotherCityNode2.getId().intValue())))
                            .andExpect(jsonPath("city.name", is(anotherCityNode2.getName())))
                            .andExpect(jsonPath("city.latitude", is(anotherCityNode2.getLatitude())))
                            .andExpect(jsonPath("city.longitude", is(anotherCityNode2.getLongitude())))
                            .andExpect(jsonPath("city.province.links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("city.province.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.id", is(anotherProvinceNode3.getId().intValue())))
                            .andExpect(jsonPath("city.province.name", is(anotherProvinceNode3.getName())))
                            .andExpect(jsonPath("city.province.country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("city.province.country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.country.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("city.province.country.name", is(countryNode.getName())))
                            .andExpect(jsonPath("city.province.country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("city.province.country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.country.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("city.province.country.region.name", is(regionNode.getName())))
                            .andExpect(jsonPath("victim.links[0].href", is(pathToVictimLink)))
                            .andExpect(jsonPath("victim.links[1].href").doesNotExist())
                            .andExpect(jsonPath("victim.id", is(victimNode.getId().intValue())))
                            .andExpect(jsonPath("victim.totalNumberOfFatalities",
                                    is(victimNode.getTotalNumberOfFatalities().intValue())))
                            .andExpect(jsonPath("victim.numberOfPerpetratorFatalities",
                                    is(victimNode.getNumberOfPerpetratorFatalities().intValue())))
                            .andExpect(jsonPath("victim.totalNumberOfInjured",
                                    is(victimNode.getTotalNumberOfInjured().intValue())))
                            .andExpect(jsonPath("victim.numberOfPerpetratorInjured",
                                    is(victimNode.getNumberOfPerpetratorInjured().intValue())))
                            .andExpect(jsonPath("victim.valueOfPropertyDamage",
                                    is(victimNode.getValueOfPropertyDamage().intValue()))));
        }

        @Test
        void when_partial_update_event_victim_using_json_patch_should_return_partially_updated_node() {

            Long updatedTotalNumberOfFatalities = 20L;
            Long updatedNumberOfPerpetratorFatalities = 10L;
            Long updatedTotalNumberOfInjured = 14L;
            Long updatedNumberOfPerpetratorInjured = 3L;
            Long updatedValueOfPropertyDamage = 10000L;

            String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + anotherProvinceNode4.getId().intValue();
            String pathToTargetLink = TARGET_BASE_PATH + "/" + anotherTargetNode4.getId().intValue();
            String pathToCityLink = CITY_BASE_PATH + "/" + anotherCityNode3.getId().intValue();
            String pathToVictimLink = VICTIM_BASE_PATH + "/" + anotherVictimNode.getId().intValue();
            String pathToEventLink = EVENT_BASE_PATH + "/" + anotherEventNode4.getId().intValue();
            String pathToTargetEventLink = EVENT_BASE_PATH + "/" + anotherEventNode4.getId().intValue() + "/targets";

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/victim/totalNumberOfFatalities\", \"value\": \"" +
                    updatedTotalNumberOfFatalities + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/victim/numberOfPerpetratorFatalities\", \"value\": \"" +
                    updatedNumberOfPerpetratorFatalities + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/victim/totalNumberOfInjured\", \"value\": \"" +
                    updatedTotalNumberOfInjured + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/victim/numberOfPerpetratorInjured\", \"value\": \"" +
                    updatedNumberOfPerpetratorInjured + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/victim/valueOfPropertyDamage\", \"value\": \"" +
                    updatedValueOfPropertyDamage + "\" }" +
                    "]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, anotherEventNode4.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToEventLink)))
                            .andExpect(jsonPath("links[1].href", is(pathToTargetEventLink)))
                            .andExpect(jsonPath("id", is(anotherEventNode4.getId().intValue())))
                            .andExpect(jsonPath("summary", is(anotherEventNode4.getSummary())))
                            .andExpect(jsonPath("motive", is(anotherEventNode4.getMotive())))
                            .andExpect(jsonPath("date", is(notNullValue())))
                            .andExpect(jsonPath("isSuicidal", is(anotherEventNode4.getIsSuicidal())))
                            .andExpect(jsonPath("isSuccessful", is(anotherEventNode4.getIsSuccessful())))
                            .andExpect(jsonPath("isPartOfMultipleIncidents",
                                    is(anotherEventNode4.getIsPartOfMultipleIncidents())))
                            .andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
                            .andExpect(jsonPath("target.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.id", is(anotherTargetNode4.getId().intValue())))
                            .andExpect(jsonPath("target.target", is(anotherTargetNode4.getTarget())))
                            .andExpect(jsonPath("target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("target.countryOfOrigin.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.name", is(countryNode.getName())))
                            .andExpect(jsonPath("target.countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("target.countryOfOrigin.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.region.name", is(regionNode.getName())))
                            .andExpect(jsonPath("city.links[0].href", is(pathToCityLink)))
                            .andExpect(jsonPath("city.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.id", is(anotherCityNode3.getId().intValue())))
                            .andExpect(jsonPath("city.name", is(anotherCityNode3.getName())))
                            .andExpect(jsonPath("city.latitude", is(anotherCityNode3.getLatitude())))
                            .andExpect(jsonPath("city.longitude", is(anotherCityNode3.getLongitude())))
                            .andExpect(jsonPath("city.province.links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("city.province.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.id", is(anotherProvinceNode4.getId().intValue())))
                            .andExpect(jsonPath("city.province.name", is(anotherProvinceNode4.getName())))
                            .andExpect(jsonPath("city.province.country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("city.province.country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.country.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("city.province.country.name", is(countryNode.getName())))
                            .andExpect(jsonPath("city.province.country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("city.province.country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.country.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("city.province.country.region.name", is(regionNode.getName())))
                            .andExpect(jsonPath("victim.links[0].href", is(pathToVictimLink)))
                            .andExpect(jsonPath("victim.links[1].href").doesNotExist())
                            .andExpect(jsonPath("victim.id", is(anotherVictimNode.getId().intValue())))
                            .andExpect(jsonPath("victim.totalNumberOfFatalities",
                                    is(updatedTotalNumberOfFatalities.intValue())))
                            .andExpect(jsonPath("victim.numberOfPerpetratorFatalities",
                                    is(updatedNumberOfPerpetratorFatalities.intValue())))
                            .andExpect(jsonPath("victim.totalNumberOfInjured",
                                    is(updatedTotalNumberOfInjured.intValue())))
                            .andExpect(jsonPath("victim.numberOfPerpetratorInjured",
                                    is(updatedNumberOfPerpetratorInjured.intValue())))
                            .andExpect(jsonPath("victim.valueOfPropertyDamage",
                                    is(updatedValueOfPropertyDamage.intValue()))));
        }

        @Test
        void when_partial_update_valid_event_but_event_not_exist_using_json_patch_should_return_error_response() {

            Long notExistingId = 2000L;

            String updatedSummary = "summary updated";
            String updatedMotive = "motive updated";
            String updatedEventDateString = "2001-08-05";
            boolean updatedIsPartOfMultipleIncidents = false;
            boolean updatedIsSuccessful = false;
            boolean updatedIsSuicidal = false;

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/summary\", \"value\": \"" + updatedSummary + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/motive\", \"value\": \"" + updatedMotive + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/date\", \"value\": \"" + updatedEventDateString + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/isPartOfMultipleIncidents\", \"value\": \"" +
                    updatedIsPartOfMultipleIncidents + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/isSuccessful\", \"value\": \"" + updatedIsSuccessful + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/isSuicidal\", \"value\": \"" + updatedIsSuicidal + "\" }]";

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
                            .andExpect(jsonPath("errors[0]", is("Could not find EventModel with id: " + notExistingId + ".")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @ParameterizedTest(name = "{index}: For Event Target: {0}")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_invalid_events_target_using_json_patch_should_have_errors(String invalidTarget) {

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/target/target\", \"value\": \"" + invalidTarget + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Target name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_events_target_with_country_as_null_using_json_patch_should_have_errors() {

            String updatedTargetName = "updated target";

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/target/target\", \"value\": \"" + updatedTargetName + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/target/countryOfOrigin/name\", \"value\": " + null + "}" +
                    "]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Country name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("Province and target should be located in the same country.")))
                            .andExpect(jsonPath("errors", hasSize(2))));
        }

        @Test
        void when_partial_update_valid_events_target_with_not_existing_country_using_json_patch_should_have_errors() {

            String updatedTargetName = "updated target";
            String notExistingCountryName = "not existing country";

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/target/target\", \"value\": \"" + updatedTargetName + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/target/countryOfOrigin/name\", \"value\": \"" + notExistingCountryName + "\" }" +
                    "]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("A country with the provided name does not exist.")))
                            .andExpect(jsonPath("errors", hasItem("Province and target should be located in the same country.")))
                            .andExpect(jsonPath("errors", hasSize(2))));
        }

        @Test
        void when_partial_update_invalid_event_with_null_fields_using_json_patch_should_return_errors() {

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/summary\", \"value\": " + null + "}," +
                    "{ \"op\": \"replace\", \"path\": \"/motive\", \"value\": " + null + "}," +
                    "{ \"op\": \"replace\", \"path\": \"/date\", \"value\": " + null + "}," +
                    "{ \"op\": \"replace\", \"path\": \"/isPartOfMultipleIncidents\", \"value\": " + null + "}," +
                    "{ \"op\": \"replace\", \"path\": \"/isSuccessful\", \"value\": " + null + "}," +
                    "{ \"op\": \"replace\", \"path\": \"/isSuicidal\", \"value\": " + null + "}," +
                    "{ \"op\": \"replace\", \"path\": \"/target\", \"value\": " + null + "}," +
                    "{ \"op\": \"replace\", \"path\": \"/city\", \"value\": " + null + "}" +
                    "]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event summary cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("Event motive cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("Event date cannot be null.")))
                            .andExpect(jsonPath("errors", hasItem(
                                    "Event must have information on whether it has been part of many incidents.")))
                            .andExpect(jsonPath("errors",
                                    hasItem("Event must have information about whether it was successful.")))
                            .andExpect(jsonPath("errors",
                                    hasItem("Event must have information about whether it was a suicidal attack.")))
                            .andExpect(jsonPath("errors", hasItem("Target name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("City name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(8))));

        }

        @ParameterizedTest(name = "{index}: For Event summary: {0}")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_event_with_invalid_summary_using_json_patch_should_return_errors(
                String invalidSummary) {

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/summary\", \"value\": \"" + invalidSummary + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Event summary cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @ParameterizedTest(name = "{index}: For Event motive: {0}")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_event_with_invalid_motive_using_json_patch_should_return_errors(String invalidMotive) {

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/motive\", \"value\": \"" + invalidMotive + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Event motive cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_event_with_date_in_the_future_using_json_patch_should_return_errors() {

            String invalidDate = "2101-08-05";

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/date\", \"value\": \"" + invalidDate + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Event date cannot be in the future.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @ParameterizedTest(name = "{index}: For Event City name: {0}")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_event_with_invalid_city_name_using_json_patch_should_return_errors(String invalidCityName) {

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/city/name\", \"value\": \"" + invalidCityName + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
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
        void when_partial_update_event_with_null_city_values_using_json_patch_should_return_errors() {

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/city/latitude\", \"value\": " + null + " }," +
                    "{ \"op\": \"replace\", \"path\": \"/city/longitude\", \"value\": " + null + " }," +
                    "{ \"op\": \"replace\", \"path\": \"/city/name\", \"value\": " + null + " }" +
                    "]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("City name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("City latitude cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("City longitude cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(3))));
        }

        @Test
        void when_partial_update_event_with_too_small_city_latitude_using_json_patch_should_return_errors() {

            double invalidCityLatitude = -91.0;

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/city/latitude\", \"value\": \"" + invalidCityLatitude + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
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
        void when_partial_update_event_with_too_big_city_latitude_using_json_patch_should_return_errors() {

            double invalidCityLatitude = 91.0;

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/city/latitude\", \"value\": \"" + invalidCityLatitude + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
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
        void when_partial_update_event_with_too_small_city_longitude_using_json_patch_should_return_errors() {

            double invalidCityLongitude = -181.0;

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/city/longitude\", \"value\": \"" + invalidCityLongitude + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
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
        void when_partial_update_event_with_too_big_city_longitude_using_json_patch_should_return_errors() {

            double invalidCityLongitude = 181.0;

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/city/longitude\", \"value\": \"" + invalidCityLongitude + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
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
        void when_partial_update_event_with_province_and_target_in_different_countries_using_json_patch_should_return_errors() {

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/city/province/country/name\", \"value\": \"" + countryNode.getName() + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/target/countryOfOrigin/name\", \"value\": \"" + anotherCountryNode.getName() + "\"}]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Province and target should be located in the same country.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_event_with_null_province_values_using_json_patch_should_return_errors() {

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/city/province/name\", \"value\": " + null + " }," +
                    "{ \"op\": \"replace\", \"path\": \"/city/province/country\", \"value\": " + null + " }" +
                    "]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Province name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("Province and target should be located in the same country.")))
                            .andExpect(jsonPath("errors", hasSize(2))));
        }

        @ParameterizedTest(name = "{index}: For Event Province name: {0}")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_event_with_invalid_province_name_using_json_patch_should_return_errors(String invalidProvinceName) {

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/city/province/name\", \"value\": \"" + invalidProvinceName + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
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
        void when_partial_update_event_without_province_country_using_json_patch_should_return_errors() {

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/city/province/country\", \"value\": " + null + " }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Country name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("Province and target should be located in the same country.")))
                            .andExpect(jsonPath("errors", hasSize(2))));
        }

        @Test
        void when_partial_update_event_without_total_number_of_fatalities_using_json_patch_should_return_errors() {

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/victim/totalNumberOfFatalities\", \"value\": "
                    + null + " }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event total number of fatalities cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_event_with_negative_total_number_of_fatalities_using_json_patch_should_return_errors() {

            long negativeTotalNumberOfFatalities = -10L;

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/victim/totalNumberOfFatalities\", \"value\": "
                    + negativeTotalNumberOfFatalities + " }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event total number of fatalities must be greater or equal to 0.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_event_without_number_of_perpetrator_fatalities_using_json_patch_should_return_errors() {

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/victim/numberOfPerpetratorFatalities\", \"value\": "
                    + null + " }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event number of perpetrator fatalities cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_event_with_negative_number_of_perpetrator_fatalities_using_json_patch_should_return_errors() {

            long negativeNumberOfPerpetratorFatalities = -10L;

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/victim/numberOfPerpetratorFatalities\", \"value\": "
                    + negativeNumberOfPerpetratorFatalities + " }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event number of perpetrator fatalities must be greater or equal to 0.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_event_without_total_number_of_injured_using_json_patch_should_return_errors() {

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/victim/totalNumberOfInjured\", \"value\": "
                    + null + " }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event total number of injured cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_event_with_negative_total_number_of_injured_using_json_patch_should_return_errors() {

            long negativeTotalNumberOfInjured = -10L;

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/victim/totalNumberOfInjured\", \"value\": "
                    + negativeTotalNumberOfInjured + " }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event total number of injured must be greater or equal to 0.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_event_without_number_of_perpetrator_injured_using_json_patch_should_return_errors() {

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/victim/numberOfPerpetratorInjured\", \"value\": "
                    + null + " }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event number of perpetrator injured cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_event_with_negative_number_of_perpetrator_injured_using_json_patch_should_return_errors() {

            long negativeNumberOfPerpetratorInjured = -10L;

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/victim/numberOfPerpetratorInjured\", \"value\": "
                    + negativeNumberOfPerpetratorInjured + " }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event number of perpetrator injured must be greater or equal to 0.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_event_without_value_of_property_damage_using_json_patch_should_return_errors() {

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/victim/valueOfPropertyDamage\", \"value\": "
                    + null + " }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event total value of property damage cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_event_with_negative_value_of_property_damage_using_json_patch_should_return_errors() {

            long negativeValueOfPropertyDamage = -100L;

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/victim/valueOfPropertyDamage\", \"value\": "
                    + negativeValueOfPropertyDamage + " }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event total value of property damage must be greater or equal to 0.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }
    }

    @Nested
    class EventControllerMergeJsonPatchMethodTest {

        @Test
        void when_partial_update_valid_event_using_json_meusing_json_merge_patch_should_return_partially_updated_node() {

            String updatedSummary = "summary updated 2";
            String updatedMotive = "motive updated 2";
            String updatedEventDateString = "2011-01-03";
            boolean updatedIsPartOfMultipleIncidents = false;
            boolean updatedIsSuccessful = true;
            boolean updatedIsSuicidal = false;

            String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + provinceNode.getId().intValue();
            String pathToTargetLink = TARGET_BASE_PATH + "/" + targetNode.getId().intValue();
            String pathToCityLink = CITY_BASE_PATH + "/" + cityNode.getId().intValue();
            String pathToVictimLink = VICTIM_BASE_PATH + "/" + victimNode.getId().intValue();
            String pathToEventLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue();
            String pathToTargetEventLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue() + "/targets";

            String jsonMergePatch = "{\"summary\" : \"" + updatedSummary + "\", " +
                    "\"motive\" : \"" + updatedMotive + "\", " +
                    "\"date\" : \"" + updatedEventDateString + "\", " +
                    "\"isPartOfMultipleIncidents\" : " + updatedIsPartOfMultipleIncidents + ", " +
                    "\"isSuccessful\" : " + updatedIsSuccessful + ", " +
                    "\"isSuicidal\" : " + updatedIsSuicidal + "}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToEventLink)))
                            .andExpect(jsonPath("links[1].href", is(pathToTargetEventLink)))
                            .andExpect(jsonPath("id", is(eventNode.getId().intValue())))
                            .andExpect(jsonPath("summary", is(updatedSummary)))
                            .andExpect(jsonPath("motive", is(updatedMotive)))
                            .andExpect(jsonPath("date", is(notNullValue())))
                            .andExpect(jsonPath("isSuicidal", is(updatedIsSuicidal)))
                            .andExpect(jsonPath("isSuccessful", is(updatedIsSuccessful)))
                            .andExpect(jsonPath("isPartOfMultipleIncidents", is(updatedIsPartOfMultipleIncidents)))
                            .andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
                            .andExpect(jsonPath("target.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.id", is(targetNode.getId().intValue())))
                            .andExpect(jsonPath("target.target", is(targetNode.getTarget())))
                            .andExpect(jsonPath("target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("target.countryOfOrigin.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.name", is(countryNode.getName())))
                            .andExpect(jsonPath("target.countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("target.countryOfOrigin.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.region.name", is(regionNode.getName())))
                            .andExpect(jsonPath("city.links[0].href", is(pathToCityLink)))
                            .andExpect(jsonPath("city.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.id", is(cityNode.getId().intValue())))
                            .andExpect(jsonPath("city.name", is(cityNode.getName())))
                            .andExpect(jsonPath("city.latitude", is(cityNode.getLatitude())))
                            .andExpect(jsonPath("city.longitude", is(cityNode.getLongitude())))
                            .andExpect(jsonPath("city.province.links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("city.province.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.id", is(provinceNode.getId().intValue())))
                            .andExpect(jsonPath("city.province.name", is(provinceNode.getName())))
                            .andExpect(jsonPath("city.province.country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("city.province.country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.country.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("city.province.country.name", is(countryNode.getName())))
                            .andExpect(jsonPath("city.province.country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("city.province.country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.country.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("city.province.country.region.name", is(regionNode.getName())))
                            .andExpect(jsonPath("victim.links[0].href", is(pathToVictimLink)))
                            .andExpect(jsonPath("victim.links[1].href").doesNotExist())
                            .andExpect(jsonPath("victim.id", is(victimNode.getId().intValue())))
                            .andExpect(jsonPath("victim.totalNumberOfFatalities",
                                    is(victimNode.getTotalNumberOfFatalities().intValue())))
                            .andExpect(jsonPath("victim.numberOfPerpetratorFatalities",
                                    is(victimNode.getNumberOfPerpetratorFatalities().intValue())))
                            .andExpect(jsonPath("victim.totalNumberOfInjured",
                                    is(victimNode.getTotalNumberOfInjured().intValue())))
                            .andExpect(jsonPath("victim.numberOfPerpetratorInjured",
                                    is(victimNode.getNumberOfPerpetratorInjured().intValue())))
                            .andExpect(jsonPath("victim.valueOfPropertyDamage",
                                    is(victimNode.getValueOfPropertyDamage().intValue()))));
        }

        @Test
        void when_partial_update_valid_events_target_using_json_merge_patch_should_return_partially_updated_node() {

            String updatedTargetName = "updated target 2";

            String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + provinceNode.getId().intValue();
            String pathToTargetLink = TARGET_BASE_PATH + "/" + anotherTargetNode.getId().intValue();
            String pathToCityLink = CITY_BASE_PATH + "/" + cityNode.getId().intValue();
            String pathToVictimLink = VICTIM_BASE_PATH + "/" + victimNode.getId().intValue();
            String pathToEventLink = EVENT_BASE_PATH + "/" + anotherEventNode.getId().intValue();
            String pathToTargetEventLink = EVENT_BASE_PATH + "/" + anotherEventNode.getId().intValue() + "/targets";

            String jsonMergePatch = "{\"target\" : {\"target\" : \"" + updatedTargetName + "\"}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, anotherEventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToEventLink)))
                            .andExpect(jsonPath("links[1].href", is(pathToTargetEventLink)))
                            .andExpect(jsonPath("id", is(anotherEventNode.getId().intValue())))
                            .andExpect(jsonPath("summary", is(anotherEventNode.getSummary())))
                            .andExpect(jsonPath("motive", is(anotherEventNode.getMotive())))
                            .andExpect(jsonPath("date", is(notNullValue())))
                            .andExpect(jsonPath("isSuicidal", is(anotherEventNode.getIsSuicidal())))
                            .andExpect(jsonPath("isSuccessful", is(anotherEventNode.getIsSuccessful())))
                            .andExpect(jsonPath("isPartOfMultipleIncidents", is(anotherEventNode.getIsPartOfMultipleIncidents())))
                            .andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
                            .andExpect(jsonPath("target.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.id", is(anotherTargetNode.getId().intValue())))
                            .andExpect(jsonPath("target.target", is(updatedTargetName)))
                            .andExpect(jsonPath("target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("target.countryOfOrigin.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.name", is(countryNode.getName())))
                            .andExpect(jsonPath("target.countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("target.countryOfOrigin.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.region.name", is(regionNode.getName())))
                            .andExpect(jsonPath("city.links[0].href", is(pathToCityLink)))
                            .andExpect(jsonPath("city.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.id", is(cityNode.getId().intValue())))
                            .andExpect(jsonPath("city.name", is(cityNode.getName())))
                            .andExpect(jsonPath("city.latitude", is(cityNode.getLatitude())))
                            .andExpect(jsonPath("city.longitude", is(cityNode.getLongitude())))
                            .andExpect(jsonPath("city.province.links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("city.province.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.id", is(provinceNode.getId().intValue())))
                            .andExpect(jsonPath("city.province.name", is(provinceNode.getName())))
                            .andExpect(jsonPath("city.province.country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("city.province.country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.country.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("city.province.country.name", is(countryNode.getName())))
                            .andExpect(jsonPath("city.province.country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("city.province.country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.country.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("city.province.country.region.name", is(regionNode.getName())))
                            .andExpect(jsonPath("victim.links[0].href", is(pathToVictimLink)))
                            .andExpect(jsonPath("victim.links[1].href").doesNotExist())
                            .andExpect(jsonPath("victim.id", is(victimNode.getId().intValue())))
                            .andExpect(jsonPath("victim.totalNumberOfFatalities",
                                    is(victimNode.getTotalNumberOfFatalities().intValue())))
                            .andExpect(jsonPath("victim.numberOfPerpetratorFatalities",
                                    is(victimNode.getNumberOfPerpetratorFatalities().intValue())))
                            .andExpect(jsonPath("victim.totalNumberOfInjured",
                                    is(victimNode.getTotalNumberOfInjured().intValue())))
                            .andExpect(jsonPath("victim.numberOfPerpetratorInjured",
                                    is(victimNode.getNumberOfPerpetratorInjured().intValue())))
                            .andExpect(jsonPath("victim.valueOfPropertyDamage",
                                    is(victimNode.getValueOfPropertyDamage().intValue()))));
        }

        @Test
        void when_partial_update_valid_events_city_using_json_merge_patch_should_return_partially_updated_node() {

            String updatedCityName = "updated city";
            Double updatedCityLatitude = 20.0;
            Double updatedCityLongitude = 20.0;
            String updatedProvinceName = "updated province";

            String pathToRegionLink = REGION_BASE_PATH + "/" + anotherRegionNode2.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + anotherCountryNode2.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + anotherProvinceNode2.getId().intValue();
            String pathToTargetLink = TARGET_BASE_PATH + "/" + anotherTargetNode2.getId().intValue();
            String pathToCityLink = CITY_BASE_PATH + "/" + anotherCityNode.getId().intValue();
            String pathToVictimLink = VICTIM_BASE_PATH + "/" + victimNode.getId().intValue();
            String pathToEventLink = EVENT_BASE_PATH + "/" + anotherEventNode2.getId().intValue();
            String pathToTargetEventLink = EVENT_BASE_PATH + "/" + anotherEventNode2.getId().intValue() + "/targets";

            String jsonMergePatch = "{\"city\" : " +
                    "{\"name\" : \"" + updatedCityName + "\", " +
                    "\"latitude\" : " + updatedCityLatitude + ", " +
                    "\"longitude\" : " + updatedCityLongitude + ", " +
                    "\"province\" : {\"name\" : \"" + updatedProvinceName + "\"," +
                    "\"country\" : {\"id\" : " + anotherCountryNode2.getId().intValue() + "," +
                    "\"name\" : \"" + anotherCountryNode2.getName() + "\"}" +
                    "}}," +
                    "\"target\" : {" +
                    "\"countryOfOrigin\": " +
                    "{\"id\" : " + anotherCountryNode2.getId().intValue() + "," +
                    "\"name\" : \"" + anotherCountryNode2.getName() + "\"}" +
                    "}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, anotherEventNode2.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToEventLink)))
                            .andExpect(jsonPath("links[1].href", is(pathToTargetEventLink)))
                            .andExpect(jsonPath("id", is(anotherEventNode2.getId().intValue())))
                            .andExpect(jsonPath("summary", is(anotherEventNode2.getSummary())))
                            .andExpect(jsonPath("motive", is(anotherEventNode2.getMotive())))
                            .andExpect(jsonPath("date", is(notNullValue())))
                            .andExpect(jsonPath("isSuicidal", is(anotherEventNode2.getIsSuicidal())))
                            .andExpect(jsonPath("isSuccessful", is(anotherEventNode2.getIsSuccessful())))
                            .andExpect(jsonPath("isPartOfMultipleIncidents", is(anotherEventNode2.getIsPartOfMultipleIncidents())))
                            .andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
                            .andExpect(jsonPath("target.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.id", is(anotherTargetNode2.getId().intValue())))
                            .andExpect(jsonPath("target.target", is(anotherTargetNode2.getTarget())))
                            .andExpect(jsonPath("target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("target.countryOfOrigin.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.countryOfOrigin.id", is(anotherCountryNode2.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.name", is(anotherCountryNode2.getName())))
                            .andExpect(jsonPath("target.countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("target.countryOfOrigin.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.countryOfOrigin.region.id", is(anotherRegionNode2.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.region.name", is(anotherRegionNode2.getName())))
                            .andExpect(jsonPath("city.links[0].href", is(pathToCityLink)))
                            .andExpect(jsonPath("city.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.id", notNullValue()))
                            .andExpect(jsonPath("city.name", is(updatedCityName)))
                            .andExpect(jsonPath("city.latitude", is(updatedCityLatitude)))
                            .andExpect(jsonPath("city.longitude", is(updatedCityLongitude)))
                            .andExpect(jsonPath("city.province.links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("city.province.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.id", notNullValue()))
                            .andExpect(jsonPath("city.province.name", is(updatedProvinceName)))
                            .andExpect(jsonPath("city.province.country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("city.province.country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.country.id", is(anotherCountryNode2.getId().intValue())))
                            .andExpect(jsonPath("city.province.country.name", is(anotherCountryNode2.getName())))
                            .andExpect(jsonPath("city.province.country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("city.province.country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.country.region.id", is(anotherRegionNode2.getId().intValue())))
                            .andExpect(jsonPath("city.province.country.region.name", is(anotherRegionNode2.getName())))
                            .andExpect(jsonPath("victim.links[0].href", is(pathToVictimLink)))
                            .andExpect(jsonPath("victim.links[1].href").doesNotExist())
                            .andExpect(jsonPath("victim.id", is(victimNode.getId().intValue())))
                            .andExpect(jsonPath("victim.totalNumberOfFatalities",
                                    is(victimNode.getTotalNumberOfFatalities().intValue())))
                            .andExpect(jsonPath("victim.numberOfPerpetratorFatalities",
                                    is(victimNode.getNumberOfPerpetratorFatalities().intValue())))
                            .andExpect(jsonPath("victim.totalNumberOfInjured",
                                    is(victimNode.getTotalNumberOfInjured().intValue())))
                            .andExpect(jsonPath("victim.numberOfPerpetratorInjured",
                                    is(victimNode.getNumberOfPerpetratorInjured().intValue())))
                            .andExpect(jsonPath("victim.valueOfPropertyDamage",
                                    is(victimNode.getValueOfPropertyDamage().intValue()))));
        }

        @Test
        void when_partial_update_event_region_using_json_merge_patch_should_return_node_without_changes() {

            String notExistingRegionName = "not existing region";

            String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + anotherProvinceNode3.getId().intValue();
            String pathToTargetLink = TARGET_BASE_PATH + "/" + anotherTargetNode3.getId().intValue();
            String pathToCityLink = CITY_BASE_PATH + "/" + anotherCityNode2.getId().intValue();
            String pathToVictimLink = VICTIM_BASE_PATH + "/" + victimNode.getId().intValue();
            String pathToEventLink = EVENT_BASE_PATH + "/" + anotherEventNode3.getId().intValue();
            String pathToTargetEventLink = EVENT_BASE_PATH + "/" + anotherEventNode3.getId().intValue() + "/targets";

            String jsonMergePatch = "{\"city\" : " +
                    "{\"province\" : {" +
                    "\"country\" : {" +
                    "\"region\" : {" +
                    "\"name\" : \"" + notExistingRegionName + "\"}" +
                    "}}}," +
                    "\"target\" : {" +
                    "\"countryOfOrigin\": {" +
                    "\"region\": {" +
                    "\"name\" : \"" + notExistingRegionName + "\"}" +
                    "}}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, anotherEventNode3.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToEventLink)))
                            .andExpect(jsonPath("links[1].href", is(pathToTargetEventLink)))
                            .andExpect(jsonPath("id", is(anotherEventNode3.getId().intValue())))
                            .andExpect(jsonPath("summary", is(anotherEventNode3.getSummary())))
                            .andExpect(jsonPath("motive", is(anotherEventNode3.getMotive())))
                            .andExpect(jsonPath("date", is(notNullValue())))
                            .andExpect(jsonPath("isSuicidal", is(anotherEventNode3.getIsSuicidal())))
                            .andExpect(jsonPath("isSuccessful", is(anotherEventNode3.getIsSuccessful())))
                            .andExpect(jsonPath("isPartOfMultipleIncidents", is(anotherEventNode3.getIsPartOfMultipleIncidents())))
                            .andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
                            .andExpect(jsonPath("target.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.id", is(anotherTargetNode3.getId().intValue())))
                            .andExpect(jsonPath("target.target", is(anotherTargetNode3.getTarget())))
                            .andExpect(jsonPath("target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("target.countryOfOrigin.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.name", is(countryNode.getName())))
                            .andExpect(jsonPath("target.countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("target.countryOfOrigin.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.region.name", is(regionNode.getName())))
                            .andExpect(jsonPath("city.links[0].href", is(pathToCityLink)))
                            .andExpect(jsonPath("city.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.id", is(anotherCityNode2.getId().intValue())))
                            .andExpect(jsonPath("city.name", is(anotherCityNode2.getName())))
                            .andExpect(jsonPath("city.latitude", is(anotherCityNode2.getLatitude())))
                            .andExpect(jsonPath("city.longitude", is(anotherCityNode2.getLongitude())))
                            .andExpect(jsonPath("city.province.links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("city.province.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.id", is(anotherProvinceNode3.getId().intValue())))
                            .andExpect(jsonPath("city.province.name", is(anotherProvinceNode3.getName())))
                            .andExpect(jsonPath("city.province.country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("city.province.country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.country.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("city.province.country.name", is(countryNode.getName())))
                            .andExpect(jsonPath("city.province.country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("city.province.country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.country.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("city.province.country.region.name", is(regionNode.getName())))
                            .andExpect(jsonPath("victim.links[0].href", is(pathToVictimLink)))
                            .andExpect(jsonPath("victim.links[1].href").doesNotExist())
                            .andExpect(jsonPath("victim.id", is(victimNode.getId().intValue())))
                            .andExpect(jsonPath("victim.totalNumberOfFatalities",
                                    is(victimNode.getTotalNumberOfFatalities().intValue())))
                            .andExpect(jsonPath("victim.numberOfPerpetratorFatalities",
                                    is(victimNode.getNumberOfPerpetratorFatalities().intValue())))
                            .andExpect(jsonPath("victim.totalNumberOfInjured",
                                    is(victimNode.getTotalNumberOfInjured().intValue())))
                            .andExpect(jsonPath("victim.numberOfPerpetratorInjured",
                                    is(victimNode.getNumberOfPerpetratorInjured().intValue())))
                            .andExpect(jsonPath("victim.valueOfPropertyDamage",
                                    is(victimNode.getValueOfPropertyDamage().intValue()))));
        }

        @Test
        void when_partial_update_event_victim_using_json_merge_patch_should_return_partially_updated_node() {

            Long updatedTotalNumberOfFatalities = 20L;
            Long updatedNumberOfPerpetratorFatalities = 10L;
            Long updatedTotalNumberOfInjured = 14L;
            Long updatedNumberOfPerpetratorInjured = 3L;
            Long updatedValueOfPropertyDamage = 10000L;

            String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
            String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
            String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + anotherProvinceNode4.getId().intValue();
            String pathToTargetLink = TARGET_BASE_PATH + "/" + anotherTargetNode4.getId().intValue();
            String pathToCityLink = CITY_BASE_PATH + "/" + anotherCityNode3.getId().intValue();
            String pathToVictimLink = VICTIM_BASE_PATH + "/" + anotherVictimNode.getId().intValue();
            String pathToEventLink = EVENT_BASE_PATH + "/" + anotherEventNode4.getId().intValue();
            String pathToTargetEventLink = EVENT_BASE_PATH + "/" + anotherEventNode4.getId().intValue() + "/targets";

            String jsonMergePatch = "{\"victim\" : {" +
                    "\"totalNumberOfFatalities\" : " + updatedTotalNumberOfFatalities + "," +
                    "\"numberOfPerpetratorFatalities\" : " + updatedNumberOfPerpetratorFatalities + "," +
                    "\"totalNumberOfInjured\" : " + updatedTotalNumberOfInjured + "," +
                    "\"numberOfPerpetratorInjured\" : " + updatedNumberOfPerpetratorInjured + "," +
                    "\"valueOfPropertyDamage\" : " + updatedValueOfPropertyDamage + "}" +
                    "}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, anotherEventNode4.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isOk())
                            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("links[0].href", is(pathToEventLink)))
                            .andExpect(jsonPath("links[1].href", is(pathToTargetEventLink)))
                            .andExpect(jsonPath("id", is(anotherEventNode4.getId().intValue())))
                            .andExpect(jsonPath("summary", is(anotherEventNode4.getSummary())))
                            .andExpect(jsonPath("motive", is(anotherEventNode4.getMotive())))
                            .andExpect(jsonPath("date", is(notNullValue())))
                            .andExpect(jsonPath("isSuicidal", is(anotherEventNode4.getIsSuicidal())))
                            .andExpect(jsonPath("isSuccessful", is(anotherEventNode4.getIsSuccessful())))
                            .andExpect(jsonPath("isPartOfMultipleIncidents",
                                    is(anotherEventNode4.getIsPartOfMultipleIncidents())))
                            .andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
                            .andExpect(jsonPath("target.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.id", is(anotherTargetNode4.getId().intValue())))
                            .andExpect(jsonPath("target.target", is(anotherTargetNode4.getTarget())))
                            .andExpect(jsonPath("target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("target.countryOfOrigin.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.name", is(countryNode.getName())))
                            .andExpect(jsonPath("target.countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("target.countryOfOrigin.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.region.name", is(regionNode.getName())))
                            .andExpect(jsonPath("city.links[0].href", is(pathToCityLink)))
                            .andExpect(jsonPath("city.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.id", is(anotherCityNode3.getId().intValue())))
                            .andExpect(jsonPath("city.name", is(anotherCityNode3.getName())))
                            .andExpect(jsonPath("city.latitude", is(anotherCityNode3.getLatitude())))
                            .andExpect(jsonPath("city.longitude", is(anotherCityNode3.getLongitude())))
                            .andExpect(jsonPath("city.province.links[0].href", is(pathToProvinceLink)))
                            .andExpect(jsonPath("city.province.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.id", is(anotherProvinceNode4.getId().intValue())))
                            .andExpect(jsonPath("city.province.name", is(anotherProvinceNode4.getName())))
                            .andExpect(jsonPath("city.province.country.links[0].href", is(pathToCountryLink)))
                            .andExpect(jsonPath("city.province.country.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.country.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("city.province.country.name", is(countryNode.getName())))
                            .andExpect(jsonPath("city.province.country.region.links[0].href", is(pathToRegionLink)))
                            .andExpect(jsonPath("city.province.country.region.links[1].href").doesNotExist())
                            .andExpect(jsonPath("city.province.country.region.id", is(regionNode.getId().intValue())))
                            .andExpect(jsonPath("city.province.country.region.name", is(regionNode.getName())))
                            .andExpect(jsonPath("victim.links[0].href", is(pathToVictimLink)))
                            .andExpect(jsonPath("victim.links[1].href").doesNotExist())
                            .andExpect(jsonPath("victim.id", is(anotherVictimNode.getId().intValue())))
                            .andExpect(jsonPath("victim.totalNumberOfFatalities",
                                    is(updatedTotalNumberOfFatalities.intValue())))
                            .andExpect(jsonPath("victim.numberOfPerpetratorFatalities",
                                    is(updatedNumberOfPerpetratorFatalities.intValue())))
                            .andExpect(jsonPath("victim.totalNumberOfInjured",
                                    is(updatedTotalNumberOfInjured.intValue())))
                            .andExpect(jsonPath("victim.numberOfPerpetratorInjured",
                                    is(updatedNumberOfPerpetratorInjured.intValue())))
                            .andExpect(jsonPath("victim.valueOfPropertyDamage",
                                    is(updatedValueOfPropertyDamage.intValue()))));
        }

        @Test
        void when_partial_update_valid_event_but_event_not_exist_using_json_merge_patch_should_return_error_response() {

            Long notExistingId = 2000L;

            String updatedSummary = "summary updated";
            String updatedMotive = "motive updated";
            String updatedEventDateString = "2001-08-05";
            boolean updatedIsPartOfMultipleIncidents = false;
            boolean updatedIsSuccessful = false;
            boolean updatedIsSuicidal = false;

            String jsonMergePatch = "{\"summary\" : \"" + updatedSummary + "\", " +
                    "\"motive\" : \"" + updatedMotive + "\", " +
                    "\"date\" : \"" + updatedEventDateString + "\", " +
                    "\"isPartOfMultipleIncidents\" : " + updatedIsPartOfMultipleIncidents + ", " +
                    "\"isSuccessful\" : " + updatedIsSuccessful + ", " +
                    "\"isSuicidal\" : " + updatedIsSuicidal + "}";

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
                            .andExpect(jsonPath("errors[0]", is("Could not find EventModel with id: " + notExistingId + ".")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @ParameterizedTest(name = "{index}: For Event Target: {0}")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_invalid_events_target_using_json_merge_patch_should_have_errors(String invalidTarget) {

            String jsonMergePatch = "{\"target\" : { \"target\" : \"" + invalidTarget + "\"}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Target name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_events_target_with_country_as_null_using_json_merge_patch_should_have_errors() {

            String updatedTargetName = "updated target";

            String jsonMergePatch =
                    "{\"target\" : {\"target\" : \"" + updatedTargetName + "\", " +
                            "\"countryOfOrigin\" : { \"name\" : " + null + "}}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Country name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("Province and target should be located in the same country.")))
                            .andExpect(jsonPath("errors", hasSize(2))));
        }

        @Test
        void when_partial_update_valid_events_target_with_not_existing_country_using_json_merge_patch_should_have_errors() {

            String updatedTargetName = "updated target";
            String notExistingCountryName = "not existing country";

            String jsonMergePatch =
                    "{\"target\" : {\"target\" : \"" + updatedTargetName + "\", " +
                            "\"countryOfOrigin\" : {\"name\" : \"" + notExistingCountryName + "\"}}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("A country with the provided name does not exist.")))
                            .andExpect(jsonPath("errors", hasItem("Province and target should be located in the same country.")))
                            .andExpect(jsonPath("errors", hasSize(2))));
        }

        @Test
        void when_partial_update_invalid_event_with_null_fields_using_json_merge_patch_should_return_errors() {

            String jsonMergePatch = "{\"summary\" : " + null + ", " +
                    "\"motive\" : " + null + ", " +
                    "\"date\" : " + null + ", " +
                    "\"isPartOfMultipleIncidents\" : " + null + ", " +
                    "\"isSuccessful\" : " + null + ", " +
                    "\"isSuicidal\" : " + null + ", " +
                    "\"target\" : " + null + ", " +
                    "\"city\" : " + null +
                    "}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event summary cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("Event motive cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("Event date cannot be null.")))
                            .andExpect(jsonPath("errors", hasItem(
                                    "Event must have information on whether it has been part of many incidents.")))
                            .andExpect(jsonPath("errors",
                                    hasItem("Event must have information about whether it was successful.")))
                            .andExpect(jsonPath("errors",
                                    hasItem("Event must have information about whether it was a suicidal attack.")))
                            .andExpect(jsonPath("errors", hasItem("Target name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("City name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(8))));
        }

        @ParameterizedTest(name = "{index}: For Event summary: {0}")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_event_with_invalid_summary_using_json_merge_patch_should_return_errors(
                String invalidSummary) {

            String jsonMergePatch = "{\"summary\" : \"" + invalidSummary + "\"}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Event summary cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @ParameterizedTest(name = "{index}: For Event motive: {0}")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_event_with_invalid_motive_using_json_merge_patch_should_return_errors(
                String invalidMotive) {

            String jsonMergePatch = "{\"motive\" : \"" + invalidMotive + "\"}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Event motive cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_event_with_date_in_the_future_using_json_merge_patch_should_return_errors() {

            String invalidDate = "2101-08-05";

            String jsonMergePatch = "{\"date\" : \"" + invalidDate + "\"}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Event date cannot be in the future.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @ParameterizedTest(name = "{index}: For Event City name: {0}")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_event_with_invalid_city_name_using_json_merge_patch_should_return_errors(String invalidCityName) {

            String jsonMergePatch = "{\"city\" : {\"name\" : \"" + invalidCityName + "\" }}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
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
        void when_partial_update_event_with_null_city_values_using_json_merge_patch_should_return_errors() {

            String jsonMergePatch = "{\"city\" : " +
                    "{\"name\" : " + null + ", \"latitude\" : " + null + ", \"longitude\" : " + null + "}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("City name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("City latitude cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("City longitude cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(3))));
        }

        @Test
        void when_partial_update_event_with_too_small_city_latitude_using_merge_json_patch_should_return_errors() {

            double invalidCityLatitude = -91.0;

            String jsonMergePatch = "{\"city\" : {\"latitude\" : \"" + invalidCityLatitude + "\"}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
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
        void when_partial_update_event_with_too_big_city_latitude_using_json_merge_patch_should_return_errors() {

            double invalidCityLatitude = 91.0;

            String jsonMergePatch = "{\"city\" : {\"latitude\" : \"" + invalidCityLatitude + "\"}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
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
        void when_partial_update_event_with_too_small_city_longitude_using_json_merge_patch_should_return_errors() {

            double invalidCityLongitude = -181.0;

            String jsonMergePatch = "{\"city\" : {\"longitude\" : \"" + invalidCityLongitude + "\"}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
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
        void when_partial_update_event_with_too_big_city_longitude_using_json_merge_patch_should_return_errors() {

            double invalidCityLongitude = 181.0;

            String jsonMergePatch = "{\"city\" : {\"longitude\" : \"" + invalidCityLongitude + "\"}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
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
        void when_partial_update_event_with_province_and_target_in_different_countries_using_json_merge_patch_should_return_errors() {

            String jsonMergePatch = "{\"city\" : {\"province\" : {" +
                    "\"country\" : {\"name\" : \"" + countryNode.getName() + "\"}}}," +
                    "\"target\" : {\"countryOfOrigin\" : {" +
                    "\"name\" : \"" + anotherCountryNode.getName() + "\"}}" +
                    "}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Province and target should be located in the same country.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_event_with_null_province_values_using_json_merge_patch_should_return_errors() {

            String jsonMergePatch = "{\"city\" : {" +
                    "\"province\" : {\"name\" : " + null + ", \"country\" : " + null + "}}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Province name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("Province and target should be located in the same country.")))
                            .andExpect(jsonPath("errors", hasSize(2))));
        }

        @ParameterizedTest(name = "{index}: For Event Province name: {0}")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_event_with_invalid_province_name_using_json_merge_patch_should_return_errors(String invalidProvinceName) {

            String jsonMergePatch = "{\"city\" : {\"province\" : {\"name\" : \"" + invalidProvinceName + "\"}}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
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
        void when_partial_update_event_without_province_country_using_json_merge_patch_should_return_errors() {

            String jsonMergePatch = "{\"city\" : {\"province\" : {\"country\" : " + null + "}}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Country name cannot be empty.")))
                            .andExpect(jsonPath("errors", hasItem("Province and target should be located in the same country.")))
                            .andExpect(jsonPath("errors", hasSize(2))));
        }

        @Test
        void when_partial_update_event_without_total_number_of_fatalities_using_json_merge_patch_should_return_errors() {

            String jsonMergePatch = "{\"victim\" : {\"totalNumberOfFatalities\" : " + null + "}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event total number of fatalities cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_event_with_negative_total_number_of_fatalities_using_json_merge_patch_should_return_errors() {

            long negativeTotalNumberOfFatalities = -10L;

            String jsonMergePatch = "{\"victim\" : {\"totalNumberOfFatalities\" : " + negativeTotalNumberOfFatalities + "}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event total number of fatalities must be greater or equal to 0.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_event_without_number_of_perpetrator_fatalities_using_json_merge_patch_should_return_errors() {

            String jsonMergePatch = "{\"victim\" : {\"numberOfPerpetratorFatalities\" : " + null + "}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event number of perpetrator fatalities cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_event_with_negative_number_of_perpetrator_fatalities_using_json_merge_patch_should_return_errors() {

            long negativeNumberOfPerpetratorFatalities = -10L;

            String jsonMergePatch = "{\"victim\" : {\"numberOfPerpetratorFatalities\" : "
                    + negativeNumberOfPerpetratorFatalities + "}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event number of perpetrator fatalities must be greater or equal to 0.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_event_without_total_number_of_injured_using_json_merge_patch_should_return_errors() {

            String jsonMergePatch = "{\"victim\" : {\"totalNumberOfInjured\" : " + null + "}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event total number of injured cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_event_with_negative_total_number_of_injured_using_json_merge_patch_should_return_errors() {

            long negativeTotalNumberOfInjured = -10L;

            String jsonMergePatch = "{\"victim\" : {\"totalNumberOfInjured\" : " + negativeTotalNumberOfInjured + "}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event total number of injured must be greater or equal to 0.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_event_without_number_of_perpetrator_injured_using_json_merge_patch_should_return_errors() {

            String jsonMergePatch = "{\"victim\" : {\"numberOfPerpetratorInjured\" : " + null + "}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event number of perpetrator injured cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_event_with_negative_number_of_perpetrator_injured_using_json_merge_patch_should_return_errors() {

            long negativeNumberOfPerpetratorInjured = -10L;

            String jsonMergePatch = "{\"victim\" : {\"numberOfPerpetratorInjured\" : "
                    + negativeNumberOfPerpetratorInjured + "}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event number of perpetrator injured must be greater or equal to 0.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_event_without_value_of_property_damage_using_json_merge_patch_should_return_errors() {

            String jsonMergePatch = "{\"victim\" : {\"valueOfPropertyDamage\" : " + null + "}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event total value of property damage cannot be empty.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @Test
        void when_partial_update_event_with_negative_value_of_property_damage_using_json_merge_patch_should_return_errors() {

            long negativeValueOfPropertyDamage = -100L;

            String jsonMergePatch = "{\"victim\" : {\"valueOfPropertyDamage\" : " + negativeValueOfPropertyDamage + "}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors", hasItem("Event total value of property damage must be greater or equal to 0.")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }
    }
}