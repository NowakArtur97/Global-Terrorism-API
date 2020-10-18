package com.nowakArtur97.globalTerrorismAPI.feature.group;

import com.nowakArtur97.globalTerrorismAPI.feature.city.CityNode;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryNode;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryRepository;
import com.nowakArtur97.globalTerrorismAPI.feature.event.EventNode;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceNode;
import com.nowakArtur97.globalTerrorismAPI.feature.region.RegionNode;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetNode;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.RoleNode;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.UserNode;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.UserRepository;
import com.nowakArtur97.globalTerrorismAPI.common.mediaType.PatchMediaType;
import com.nowakArtur97.globalTerrorismAPI.feature.victim.VictimNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.configuration.Neo4jTestConfiguration;
import com.nowakArtur97.globalTerrorismAPI.testUtil.database.Neo4jDatabaseUtil;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.nowakArtur97.globalTerrorismAPI.common.util.JwtUtil;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(Neo4jTestConfiguration.class)
@AutoConfigureMockMvc
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("GroupController_Tests")
class GroupControllerJsonPatchMethodTest {

    private final String REGION_BASE_PATH = "http://localhost:8080/api/v1/regions";
    private final String COUNTRY_BASE_PATH = "http://localhost:8080/api/v1/countries";
    private final String TARGET_BASE_PATH = "http://localhost:8080/api/v1/targets";
    private final String PROVINCE_BASE_PATH = "http://localhost:8080/api/v1/provinces";
    private final String CITY_BASE_PATH = "http://localhost:8080/api/v1/cities";
    private final String EVENT_BASE_PATH = "http://localhost:8080/api/v1/events";
    private final String GROUP_BASE_PATH = "http://localhost:8080/api/v1/groups";
    private final String LINK_WITH_PARAMETER_FOR_JSON_PATCH = GROUP_BASE_PATH + "/" + "{id}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    private final static UserNode userNode = new UserNode("user1234", "Password1234!", "user1234email@.com",
            Set.of(new RoleNode("user")));

    private final static RegionNode regionNode = new RegionNode("region name");

    private final static CountryNode countryNode = new CountryNode("country name", regionNode);
    private final static CountryNode anotherCountryNode = new CountryNode("country name 2", regionNode);

    private final static TargetNode targetNode = new TargetNode("target name", countryNode);
    private final static TargetNode targetNode2 = new TargetNode("target name 2", countryNode);
    private final static TargetNode targetNode3 = new TargetNode("target name 3", countryNode);
    private final static TargetNode targetNode4 = new TargetNode("target name 4", countryNode);

    private final static ProvinceNode provinceNode = new ProvinceNode("province name", countryNode);
    private final static ProvinceNode provinceNode2 = new ProvinceNode("province name 2", countryNode);

    private final static CityNode cityNode = new CityNode("city name", 45.0, 45.0, provinceNode);
    private final static CityNode cityNode2 = new CityNode("city name 2", 15.0, -45.0, provinceNode);
    private final static CityNode cityNode3 = new CityNode("city name 3", 35.0, -35.0, provinceNode2);

    private final static VictimNode victimNode = new VictimNode(10L,0L,10L,0L, 1000L);

    private final static EventNode eventNode = new EventNode("summary", "motive", new Date(), true,
            true, true, targetNode, cityNode, victimNode);
    private final static EventNode eventNode2 = new EventNode("summary 2", "motive 2", new Date(),
            false, false, false, targetNode2, cityNode, victimNode);
    private final static EventNode eventNode3 = new EventNode("summary 3", "motive 3", new Date(),
            true, false, true, targetNode3, cityNode2, victimNode);
    private final static EventNode eventNode4 = new EventNode("summary 4", "motive 4", new Date(),
            true, false, true, targetNode4, cityNode3, victimNode);

    private final static GroupNode groupNode = new GroupNode("group name", List.of(eventNode));
    private final static GroupNode groupNode2 = new GroupNode("group name 2", List.of(eventNode4));
    private final static GroupNode groupNodeWithMultipleEvents = new GroupNode("group name 3",
            List.of(eventNode2, eventNode3));

    @BeforeAll
    private static void setUp(@Autowired UserRepository userRepository, @Autowired GroupRepository groupRepository,
                              @Autowired CountryRepository countryRepository) {

        userRepository.save(userNode);

        countryRepository.save(anotherCountryNode);

        groupRepository.save(groupNode);
        groupRepository.save(groupNode2);
        groupRepository.save(groupNodeWithMultipleEvents);
    }

    @AfterAll
    private static void tearDown(@Autowired Neo4jDatabaseUtil neo4jDatabaseUtil) {

        neo4jDatabaseUtil.cleanDatabase();
    }

    @Test
    void when_partial_update_valid_group_using_json_patch_should_return_partially_updated_node() {

        String updatedName = "updated group name";

        String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
        String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetNode.getId().intValue();
        String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + provinceNode.getId().intValue();
        String pathToCityLink = CITY_BASE_PATH + "/" + cityNode.getId().intValue();
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue();
        String pathToEventTargetLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue() + "/targets";
        String pathToGroupLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue();
        String pathToEventsLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue() + "/events";

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/name\", \"value\": \"" + updatedName + "\" }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToGroupLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
                        .andExpect(jsonPath("id", is(groupNode.getId().intValue())))
                        .andExpect(jsonPath("name", is(updatedName)))
                        .andExpect(jsonPath("eventsCaused[0].links[0].href", is(pathToEventLink)))
                        .andExpect(jsonPath("eventsCaused[0].links[1].href", is(pathToEventTargetLink)))
                        .andExpect(jsonPath("eventsCaused[0].id", is(eventNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].summary", is(eventNode.getSummary())))
                        .andExpect(jsonPath("eventsCaused[0].motive", is(eventNode.getMotive())))
                        .andExpect(jsonPath("eventsCaused[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventNode.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(eventNode.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(eventNode.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents", is(eventNode.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[0].target.links[0].href", is(pathToTargetLink)))
                        .andExpect(jsonPath("eventsCaused[0].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.id", is(targetNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.target", is(targetNode.getTarget())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.links[0].href", is(pathToCityLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.id", is(cityNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.name", is(cityNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.latitude", is(cityNode.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.longitude", is(cityNode.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.links[0].href", is(pathToProvinceLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.province.id", is(provinceNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.name", is(provinceNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1]").doesNotExist()));
    }

    @Test
    @DirtiesContext
    @DisabledOnOs(OS.LINUX)
    void when_partial_update_valid_group_with_events_using_json_patch_should_return_partially_updated_node() {

        String updatedSummary = "summary updated";
        String updatedMotive = "motive updated";
        String updatedEventDateString = "2011-02-15";
        boolean updatedIsPartOfMultipleIncidents = false;
        boolean updatedIsSuccessful = false;
        boolean updatedIsSuicidal = false;

        String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
        String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetNode2.getId().intValue();
        String pathToTargetLink2 = TARGET_BASE_PATH + "/" + targetNode3.getId().intValue();
        String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + provinceNode.getId().intValue();
        String pathToCityLink = CITY_BASE_PATH + "/" + cityNode.getId().intValue();
        String pathToCityLink2 = CITY_BASE_PATH + "/" + cityNode2.getId().intValue();
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventNode2.getId().intValue();
        String pathToEventLink2 = EVENT_BASE_PATH + "/" + eventNode3.getId().intValue();
        String pathToEventTargetLink = EVENT_BASE_PATH + "/" + eventNode2.getId().intValue() + "/targets";
        String pathToEventTargetLink2 = EVENT_BASE_PATH + "/" + eventNode3.getId().intValue() + "/targets";
        String pathToGroupLink = GROUP_BASE_PATH + "/" + groupNodeWithMultipleEvents.getId().intValue();
        String pathToEventsLink = GROUP_BASE_PATH + "/" + groupNodeWithMultipleEvents.getId().intValue() + "/events";

        String jsonPatch = "[" +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/id\", \"value\": \"" + eventNode2.getId().intValue() + "\" }," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/summary\", \"value\": \"" + updatedSummary + "\" }," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/motive\", \"value\": \"" + updatedMotive + "\" }," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/date\", \"value\": \"" + updatedEventDateString + "\" }," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/isPartOfMultipleIncidents\", \"value\": \"" +
                updatedIsPartOfMultipleIncidents + "\" }," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/isSuccessful\", \"value\": \"" +
                updatedIsSuccessful + "\" }," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/isSuicidal\", \"value\": \"" + updatedIsSuicidal + "\"}" +
                "]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNodeWithMultipleEvents.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToGroupLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
                        .andExpect(jsonPath("id", is(groupNodeWithMultipleEvents.getId().intValue())))
                        .andExpect(jsonPath("name", is(groupNodeWithMultipleEvents.getName())))
                        .andExpect(jsonPath("eventsCaused[0].links[0].href", is(pathToEventLink)))
                        .andExpect(jsonPath("eventsCaused[0].links[1].href", is(pathToEventTargetLink)))
                        .andExpect(jsonPath("eventsCaused[0].id", is(eventNode2.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].summary", is(updatedSummary)))
                        .andExpect(jsonPath("eventsCaused[0].motive", is(updatedMotive)))
                        .andExpect(jsonPath("eventsCaused[0].date", is(updatedEventDateString)))
                        .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(updatedIsSuicidal)))
                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(updatedIsSuccessful)))
                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents", is(updatedIsPartOfMultipleIncidents)))
                        .andExpect(jsonPath("eventsCaused[0].target.links[0].href", is(pathToTargetLink)))
                        .andExpect(jsonPath("eventsCaused[0].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.id", is(targetNode2.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.target", is(targetNode2.getTarget())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.links[0].href", is(pathToCityLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.id", is(cityNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.name", is(cityNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.latitude", is(cityNode.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.longitude", is(cityNode.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.links[0].href", is(pathToProvinceLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.province.id", is(provinceNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.name", is(provinceNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.name", is(regionNode.getName())))

                        .andExpect(jsonPath("eventsCaused[1].links[0].href", is(pathToEventLink2)))
                        .andExpect(jsonPath("eventsCaused[1].links[1].href", is(pathToEventTargetLink2)))
                        .andExpect(jsonPath("eventsCaused[1].id", is(eventNode3.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].summary", is(eventNode3.getSummary())))
                        .andExpect(jsonPath("eventsCaused[1].motive", is(eventNode3.getMotive())))
                        .andExpect(jsonPath("eventsCaused[1].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventNode3.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[1].isSuicidal", is(eventNode3.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[1].isSuccessful", is(eventNode3.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[1].isPartOfMultipleIncidents", is(eventNode3.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[1].target.links[0].href", is(pathToTargetLink2)))
                        .andExpect(jsonPath("eventsCaused[1].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].target.id", is(targetNode3.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.target", is(targetNode3.getTarget())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.links[0].href", is(pathToCityLink2)))
                        .andExpect(jsonPath("eventsCaused[1].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.id", is(cityNode2.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.name", is(cityNode2.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.latitude", is(cityNode2.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[1].city.longitude", is(cityNode2.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.links[0].href", is(pathToProvinceLink)))
                        .andExpect(jsonPath("eventsCaused[1].city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.province.id", is(provinceNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.name", is(provinceNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[2]").doesNotExist()));
    }

    @Test
    void when_partial_update_group_event_region_using_json_patch_should_return_node_without_changes() {

        String notExistingRegionName = "not existing region";

        String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
        String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetNode4.getId().intValue();
        String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + provinceNode2.getId().intValue();
        String pathToCityLink = CITY_BASE_PATH + "/" + cityNode3.getId().intValue();
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventNode4.getId().intValue();
        String pathToEventTargetLink = EVENT_BASE_PATH + "/" + eventNode4.getId().intValue() + "/targets";
        String pathToGroupLink = GROUP_BASE_PATH + "/" + groupNode2.getId().intValue();
        String pathToEventsLink = GROUP_BASE_PATH + "/" + groupNode2.getId().intValue() + "/events";

        String jsonPatch = "[" +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/city/province/country/region/name\", " +
                "\"value\": \"" + notExistingRegionName + "\" }," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/target/countryOfOrigin/region/name\", " +
                "\"value\": \"" + notExistingRegionName + "\" }" +
                "]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNode2.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToGroupLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
                        .andExpect(jsonPath("id", is(groupNode2.getId().intValue())))
                        .andExpect(jsonPath("name", is(groupNode2.getName())))
                        .andExpect(jsonPath("eventsCaused[0].links[0].href", is(pathToEventLink)))
                        .andExpect(jsonPath("eventsCaused[0].links[1].href", is(pathToEventTargetLink)))
                        .andExpect(jsonPath("eventsCaused[0].id", is(eventNode4.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].summary", is(eventNode4.getSummary())))
                        .andExpect(jsonPath("eventsCaused[0].motive", is(eventNode4.getMotive())))
                        .andExpect(jsonPath("eventsCaused[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventNode4.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(eventNode4.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(eventNode4.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents", is(eventNode4.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[0].target.links[0].href", is(pathToTargetLink)))
                        .andExpect(jsonPath("eventsCaused[0].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.id", is(targetNode4.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.target", is(targetNode4.getTarget())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.links[0].href", is(pathToCityLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.id", is(cityNode3.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.name", is(cityNode3.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.latitude", is(cityNode3.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.longitude", is(cityNode3.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.links[0].href", is(pathToProvinceLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.province.id", is(provinceNode2.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.name", is(provinceNode2.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.name", is(countryNode.getName())))

                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1]").doesNotExist()));
    }


    @Test
    void when_partial_update_valid_group_but_group_not_exist_using_json_patch_should_return_error_response() {

        Long notExistingId = 10000L;

        String updatedName = "updated group name";

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/name\", \"value\": \"" + updatedName + "\" }]";

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
                        .andExpect(jsonPath("errors[0]", is("Could not find GroupModel with id: " + notExistingId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_partial_update_invalid_group_with_null_fields_using_json_patch_should_return_errors() {

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/name\", \"value\": " + null + " }," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused\", \"value\": " + null + "}]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Group name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("List of Events caused by the Group cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(2))));
    }

    @Test
    void when_partial_update_invalid_group_with_empty_event_list_using_json_patch_should_return_errors() {

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused\", \"value\": " + "[]" + "}]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("List of Events caused by the Group cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Group name: {0}")
    @EmptySource
    @ValueSource(strings = {" "})
    void when_partial_update_group_with_invalid_name_using_json_patch_should_return_errors(
            String invalidName) {

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/name\", \"value\": \"" + invalidName + "\" }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Group name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Event Target: {0}")
    @EmptySource
    @ValueSource(strings = {" "})
    void when_partial_update_invalid_group_events_target_using_json_patch_should_have_errors(String invalidTarget) {

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/target/target\", " +
                "\"value\": \"" + invalidTarget + "\" }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Target name cannot be empty.")))
                        .andExpect(jsonPath("errors", Matchers.hasSize(1))));
    }

    @Test
    void when_partial_update_group_events_target_with_country_as_null_using_json_patch_should_have_errors() {

        String updatedTargetName = "updated target";

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/target/target\", " +
                "\"value\": \"" + updatedTargetName + "\" }," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/target/countryOfOrigin/name\", " +
                "\"value\": " + null + "}]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Country name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Province and target should be located in the same country.")))
                        .andExpect(jsonPath("errors", Matchers.hasSize(2))));
    }

    @Test
    void when_partial_update_valid_group_events_target_with_not_existing_country_using_json_patch_should_have_errors() {

        String updatedTargetName = "updated target";
        String notExistingCountryName = "not existing country";

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/target/target\", " +
                "\"value\": \"" + updatedTargetName + "\" }," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/target/countryOfOrigin/name\"," +
                " \"value\": \"" + notExistingCountryName + "\" }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("A country with the provided name does not exist.")))
                        .andExpect(jsonPath("errors", hasItem("Province and target should be located in the same country.")))
                        .andExpect(jsonPath("errors", Matchers.hasSize(2))));
    }

    @Test
    void when_partial_update_invalid_group_event_with_null_fields_using_json_patch_should_return_errors() {

        String jsonPatch = "[" +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/summary\", \"value\": " + null + "}," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/motive\", \"value\": " + null + "}," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/date\", \"value\": " + null + "}," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/isPartOfMultipleIncidents\", \"value\": " + null + "}," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/isSuccessful\", \"value\": " + null + "}," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/isSuicidal\", \"value\": " + null + "}," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/target\", \"value\": " + null + "}," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/city\", \"value\": " + null + "}" +
                "]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue()))).andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Event summary cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Event motive cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Event date cannot be null.")))
                        .andExpect(jsonPath("errors", hasItem("Event must have information on whether it has been part of many incidents.")))
                        .andExpect(jsonPath("errors", hasItem("Event must have information about whether it was successful.")))
                        .andExpect(jsonPath("errors", hasItem("Event must have information about whether it was a suicidal attack.")))
                        .andExpect(jsonPath("errors", hasItem("Target name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("City name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(8))));
    }

    @ParameterizedTest(name = "{index}: For Group Event summary: {0}")
    @EmptySource
    @ValueSource(strings = {" "})
    void when_partial_update_group_event_with_invalid_summary_using_json_patch_should_return_errors(
            String invalidSummary) {

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/summary\", \"value\": \"" + invalidSummary + "\" }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event summary cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Group Event motive: {0}")
    @EmptySource
    @ValueSource(strings = {" "})
    void when_partial_update_group_event_with_invalid_motive_using_json_patch_should_return_errors(String invalidMotive) {

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/motive\", \"value\": \"" + invalidMotive + "\" }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNode.getId())
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
    void when_partial_update_group_event_with_date_in_the_future_using_json_patch_should_return_errors() {

        String invalidDate = "2101-08-05";

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/date\", \"value\": \"" + invalidDate + "\" }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event date cannot be in the future.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Group Event City name: {0}")
    @EmptySource
    @ValueSource(strings = {" "})
    void when_partial_update_group_event_with_invalid_city_name_using_json_patch_should_return_errors(String invalidCityName) {

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/city/name\", \"value\": \"" + invalidCityName + "\" }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_partial_update_group_event_with_with_null_city_values_using_json_patch_should_return_errors() {

        String jsonPatch = "[" +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/city/name\", \"value\": " + null + " }," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/city/latitude\", \"value\": " + null + " }," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/city/longitude\", \"value\": " + null + " }" +
                "]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("City name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("City latitude cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("City longitude cannot be empty.")))
                        .andExpect(jsonPath("errors", Matchers.hasSize(3))));
    }

    @Test
    void when_partial_update_group_event_with_too_small_city_latitude_using_json_patch_should_return_errors() {

        double invalidCityLatitude = -91.0;

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/city/latitude\", \"value\": " + invalidCityLatitude + " }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City latitude must be greater or equal to -90.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_partial_update_group_event_with_too_big_city_latitude_using_json_patch_should_return_errors() {

        double invalidCityLatitude = 91.0;

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/city/latitude\", \"value\": " + invalidCityLatitude + " }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City latitude must be less or equal to 90.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_partial_update_group_event_with_too_small_city_longitude_using_json_patch_should_return_errors() {

        double invalidCityLongitude = -181.0;

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/city/longitude\", \"value\": " + invalidCityLongitude + " }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City longitude must be greater or equal to -180.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_partial_update_group_event_with_too_big_city_longitude_using_json_patch_should_return_errors() {

        double invalidCityLongitude = 181.0;

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/city/longitude\", \"value\": " + invalidCityLongitude + " }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City longitude must be less or equal to 180.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_partial_update_group_event_with_province_and_target_in_different_countries_using_json_patch_should_return_errors() {

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/city/province/country/name\", " +
                "\"value\": \"" + countryNode.getName() + "\" }," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/target/countryOfOrigin/name\", " +
                "\"value\": \"" + anotherCountryNode.getName() + "\"}]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Province and target should be located in the same country.")))
                        .andExpect(jsonPath("errors", Matchers.hasSize(1))));
    }

    @Test
    void when_partial_update_group_event_with_null_province_values_using_json_patch_should_return_errors() {

        String jsonPatch = "[" +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/city/province/name\", \"value\": " + null + " }," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/city/province/country\", \"value\": " + null +
                " }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Province name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Province and target should be located in the same country.")))
                        .andExpect(jsonPath("errors", Matchers.hasSize(2))));
    }

    @ParameterizedTest(name = "{index}: For Group Event Province name: {0}")
    @EmptySource
    @ValueSource(strings = {" "})
    void when_partial_update_group_event_with_invalid_province_name_using_json_patch_should_return_errors(String invalidProvinceName) {

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/city/province/name\", " +
                "\"value\": \"" + invalidProvinceName + "\" }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Province name cannot be empty.")))
                        .andExpect(jsonPath("errors", Matchers.hasSize(1))));
    }

    @Test
    void when_partial_update_group_event_without_province_country_using_json_patch_should_return_errors() {

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/city/province/country\", " +
                "\"value\": " + null + " }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Country name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Province and target should be located in the same country.")))
                        .andExpect(jsonPath("errors", Matchers.hasSize(2))));
    }
}