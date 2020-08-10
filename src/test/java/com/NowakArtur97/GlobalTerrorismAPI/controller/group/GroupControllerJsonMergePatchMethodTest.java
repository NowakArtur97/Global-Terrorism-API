package com.NowakArtur97.GlobalTerrorismAPI.controller.group;

import com.NowakArtur97.GlobalTerrorismAPI.mediaType.PatchMediaType;
import com.NowakArtur97.GlobalTerrorismAPI.node.*;
import com.NowakArtur97.GlobalTerrorismAPI.repository.GroupRepository;
import com.NowakArtur97.GlobalTerrorismAPI.repository.UserRepository;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.configuration.Neo4jTestConfiguration;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.database.Neo4jDatabaseUtil;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.jwt.JwtUtil;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
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
class GroupControllerJsonMergePatchMethodTest {

    private final String TARGET_BASE_PATH = "http://localhost:8080/api/v1/targets";
    private final String EVENT_BASE_PATH = "http://localhost:8080/api/v1/events";
    private final String GROUP_BASE_PATH = "http://localhost:8080/api/v1/groups";
    private final String LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH = GROUP_BASE_PATH + "/" + "{id2}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    private final static UserNode userNode = new UserNode("user1234", "Password1234!", "user1234email@.com",
            Set.of(new RoleNode("user")));

    private final static CountryNode countryNode = new CountryNode("country");

    private final static TargetNode targetNode = new TargetNode("target", countryNode);
    private final static TargetNode targetNode2 = new TargetNode("target 2", countryNode);
    private final static TargetNode targetNode3 = new TargetNode("target 3", countryNode);

    private final static CityNode cityNode = new CityNode("city", 45.0, 45.0);
    private final static CityNode cityNode2 = new CityNode("city 2", 15.0, -45.0);

    private final static EventNode eventNode = new EventNode("summary", "motive", new Date(), true, true, true, targetNode, cityNode);
    private final static EventNode eventNode2 = new EventNode("summary 2", "motive 2", new Date(), false, false, false, targetNode2, cityNode);
    private final static EventNode eventNode3 = new EventNode("summary 3", "motive 3", new Date(), true, false, true, targetNode3, cityNode2);

    private final static GroupNode groupNode = new GroupNode("group", List.of(eventNode));
    private final static GroupNode groupNodeWithMultipleEvents = new GroupNode("group 2 ", List.of(eventNode2, eventNode3));

    @BeforeAll
    private static void setUp(@Autowired UserRepository userRepository, @Autowired GroupRepository groupRepository) {

        userRepository.save(userNode);

        groupRepository.save(groupNode);
        groupRepository.save(groupNodeWithMultipleEvents);
    }

    @AfterAll
    private static void tearDown(@Autowired Neo4jDatabaseUtil neo4jDatabaseUtil) {

        neo4jDatabaseUtil.cleanDatabase();
    }

    @Test
    void when_partial_update_valid_group_using_json_merge_patch_should_return_partially_updated_node() {

        String updatedName = "updated group name";

        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetNode.getId().intValue();
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue();
        String pathToEventTargetLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue() + "/targets";
        String pathToGroupLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue();
        String pathToEventsLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue() + "/events";

        String jsonMergePatch =
                "{\"id\" : \"" + groupNode.getId().intValue() + "\", \"name\" : \"" + updatedName + "\"}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
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
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[0].city.id", is(cityNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.name", is(cityNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.latitude", is(cityNode.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.longitude", is(cityNode.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[1]").doesNotExist()));
    }

    @Test
    void when_partial_update_valid_group_with_events_using_json_merge_patch_should_return_partially_updated_node() {

        String updatedTargetName = "target updated 2";
        String updatedSummary = "summary updated 2";
        String updatedMotive = "motive updated 2";
        String updatedEventDateString = "2011-01-15";
        String updatedEventDateString2 = "2011-02-15";
        boolean updatedIsPartOfMultipleIncidents = false;
        boolean updatedIsSuccessful = true;
        boolean updatedIsSuicidal = false;

        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetNode2.getId().intValue();
        String pathToTargetLink2 = TARGET_BASE_PATH + "/" + targetNode3.getId().intValue();
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventNode2.getId().intValue();
        String pathToEventLink2 = EVENT_BASE_PATH + "/" + eventNode3.getId().intValue();
        String pathToEventTargetLink = EVENT_BASE_PATH + "/" + eventNode2.getId().intValue() + "/targets";
        String pathToEventTargetLink2 = EVENT_BASE_PATH + "/" + eventNode3.getId().intValue() + "/targets";
        String pathToGroupLink = GROUP_BASE_PATH + "/" + groupNodeWithMultipleEvents.getId().intValue();
        String pathToEventsLink = GROUP_BASE_PATH + "/" + groupNodeWithMultipleEvents.getId().intValue() + "/events";

        String jsonMergePatch =
                "{\"eventsCaused\" : [" +
                        "{" +
                        "\"id\" : \"" + eventNode2.getId().intValue() + "\", " +
                        "\"summary\" : \"" + updatedSummary + "\", " +
                        "\"motive\" : \"" + updatedMotive + "\", " +
                        "\"date\" : \"" + updatedEventDateString + "\", " +
                        "\"isPartOfMultipleIncidents\" : \"" + updatedIsPartOfMultipleIncidents + "\", " +
                        "\"isSuccessful\" : \"" + updatedIsSuccessful + "\", " +
                        "\"isSuicidal\" : \"" + updatedIsSuicidal + "\", " +
                        "\"target\" : " +
                        "{" +
                        "\"id\" : \"" + targetNode2.getId().intValue() + "\", " +
                        "\"target\" : \"" + updatedTargetName + "\", " +
                        "\"countryOfOrigin\" : " +
                        "{ " +
                        "\"id\" : \"" + countryNode.getId().intValue() + "\", " +
                        "\"name\" : \"" + countryNode.getName() + "\"" +
                        "}" +
                        "}," +
                        "\"city\" : { " +
                        "\"id\" : \"" + cityNode.getId().intValue() + "\", " +
                        "\"name\" : \"" + cityNode.getName() + "\"," +
                        "\"latitude\" : " + cityNode.getLatitude() + "," +
                        "\"longitude\" : " + cityNode.getLongitude() +
                        "}" +
                        "}," +
                        "{" +
                        "\"id\" : \"" + eventNode3.getId().intValue() + "\", " +
                        "\"summary\" : \"" + eventNode3.getSummary() + "\", " +
                        "\"motive\" : \"" + eventNode3.getMotive() + "\", " +
                        "\"date\" : \"" + updatedEventDateString2 + "\", " +
                        "\"isPartOfMultipleIncidents\" : \"" + eventNode3.getIsPartOfMultipleIncidents() + "\", " +
                        "\"isSuccessful\" : \"" + eventNode3.getIsSuccessful() + "\", " +
                        "\"isSuicidal\" : \"" + eventNode3.getIsSuicidal() + "\", " +
                        "\"target\" : " +
                        "{" +
                        "\"id\" : \"" + targetNode3.getId().intValue() + "\", " +
                        "\"target\" : \"" + targetNode3.getTarget() + "\", " +
                        "\"countryOfOrigin\" : " +
                        "{ " +
                        "\"id\" : \"" + countryNode.getId().intValue() + "\", " +
                        "\"name\" : \"" + countryNode.getName() + "\"" +
                        "}" +
                        "}," +
                        "\"city\" : { " +
                        "\"id\" : \"" + cityNode2.getId().intValue() + "\", " +
                        "\"name\" : \"" + cityNode2.getName() + "\"," +
                        "\"latitude\" : " + cityNode2.getLatitude() + "," +
                        "\"longitude\" : " + cityNode2.getLongitude() +
                        "}" +
                        "}]}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, groupNodeWithMultipleEvents.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
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
                        .andExpect(jsonPath("eventsCaused[0].target.target", is(updatedTargetName)))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[0].city.id", is(cityNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.name", is(cityNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.latitude", is(cityNode.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.longitude", is(cityNode.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.links").isEmpty())

                        .andExpect(jsonPath("eventsCaused[1].links[0].href", is(pathToEventLink2)))
                        .andExpect(jsonPath("eventsCaused[1].links[1].href", is(pathToEventTargetLink2)))
                        .andExpect(jsonPath("eventsCaused[1].id", is(eventNode3.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].summary", is(eventNode3.getSummary())))
                        .andExpect(jsonPath("eventsCaused[1].motive", is(eventNode3.getMotive())))
                        .andExpect(jsonPath("eventsCaused[1].date", is(updatedEventDateString2)))
                        .andExpect(jsonPath("eventsCaused[1].isSuicidal", is(eventNode3.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[1].isSuccessful", is(eventNode3.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[1].isPartOfMultipleIncidents", is(eventNode3.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[1].target.links[0].href", is(pathToTargetLink2)))
                        .andExpect(jsonPath("eventsCaused[1].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].target.id", is(targetNode3.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.target", is(targetNode3.getTarget())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[1].city.id", is(cityNode2.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.name", is(cityNode2.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.latitude", is(cityNode2.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[1].city.longitude", is(cityNode2.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[1].city.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[2]").doesNotExist()));
    }

    @Test
    void when_partial_update_valid_group_but_group_not_exist_using_json_merge_patch_should_return_error_response() {

        Long notExistingId = 10000L;

        String updatedName = "updated group name";

        String jsonMergePatch = "{ \"name\" : \"" + updatedName + "\" }";

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
                        .andExpect(jsonPath("errors[0]", is("Could not find GroupModel with id: " + notExistingId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_partial_update_invalid_group_with_null_fields_using_json_merge_patch_should_return_errors() {

        String jsonMergePatch = "{\"name\" : " + null + ", \"eventsCaused\" : " + null + "}";
        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Group name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("List of Events caused by the Group cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(2))));
    }

    @Test
    void when_partial_update_invalid_group_with_empty_event_list_using_json_merge_patch_should_return_errors() {

        String jsonMergePatch = "{ \"eventsCaused\" : " + "[]" + "}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("List of Events caused by the Group cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Group name: {0} should have violation")
    @EmptySource
    @ValueSource(strings = {" "})
    void when_partial_update_group_with_invalid_name_using_json_merge_patch_should_return_errors(
            String invalidName) {

        String jsonMergePatch = "{ \"name\" : \"" + invalidName + "\" }";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Group name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Group Target: {0} should have violation")
    @EmptySource
    @ValueSource(strings = {" "})
    void when_partial_update_group_event_with_invalid_target_using_json_merge_patch_should_return_errors(
            String invalidTarget) {

        String eventDateString = "2001-08-05";

        String jsonMergePatch = "{\"eventsCaused\" : [{" +
                "\"id\" : \"" + eventNode.getId().intValue() + "\", " +
                "\"summary\" : \"" + eventNode.getSummary() + "\", " +
                "\"motive\" : \"" + eventNode.getMotive() + "\", " +
                "\"date\" : \"" + eventDateString + "\", " +
                "\"isPartOfMultipleIncidents\" : \"" + eventNode.getIsPartOfMultipleIncidents() + "\", " +
                "\"isSuccessful\" : \"" + eventNode.getIsSuccessful() + "\", " +
                "\"isSuicidal\" : \"" + eventNode.getIsSuicidal() + "\", " +
                "\"target\" : " +
                "{" +
                "\"id\" : \"" + targetNode.getId().intValue() + "\", " +
                "\"target\" : \"" + invalidTarget + "\", " +
                "\"countryOfOrigin\" : " +
                "{ " +
                "\"id\" : \"" + countryNode.getId().intValue() + "\", " +
                "\"name\" : \"" + countryNode.getName() + "\"" +
                "}" +
                "}," +
                "\"city\" : " +
                "{ " +
                "\"id\" : \"" + cityNode.getId().intValue() + "\", " +
                "\"name\" : \"" + cityNode.getName() + "\"," +
                "\"latitude\" : " + cityNode.getLatitude() + "," +
                "\"longitude\" : " + cityNode.getLongitude() +
                "}" +
                "}]}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Target name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_partial_update_group_events_target_with_country_as_null_using_json_merge_patch_should_have_errors() {

        String eventDateString = "2001-08-05";

        String jsonMergePatch = "{\"eventsCaused\" : [{" +
                "\"id\" : \"" + eventNode.getId().intValue() + "\", " +
                "\"summary\" : \"" + eventNode.getSummary() + "\", " +
                "\"motive\" : \"" + eventNode.getMotive() + "\", " +
                "\"date\" : \"" + eventDateString + "\", " +
                "\"isPartOfMultipleIncidents\" : \"" + eventNode.getIsPartOfMultipleIncidents() + "\", " +
                "\"isSuccessful\" : \"" + eventNode.getIsSuccessful() + "\", " +
                "\"isSuicidal\" : \"" + eventNode.getIsSuicidal() + "\", " +
                "\"target\" : " +
                "{" +
                "\"id\" : \"" + targetNode.getId().intValue() + "\", " +
                "\"target\" : \"" + targetNode.getTarget() + "\", " +
                "\"countryOfOrigin\" : " +
                "{ " +
                "\"name\" : " + null +
                "}" +
                "}," +
                "\"city\" : " +
                "{ " +
                "\"id\" : \"" + cityNode.getId().intValue() + "\", " +
                "\"name\" : \"" + cityNode.getName() + "\"," +
                "\"latitude\" : " + cityNode.getLatitude() + "," +
                "\"longitude\" : " + cityNode.getLongitude() +
                "}" +
                "}]}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Country name cannot be empty.")))
                        .andExpect(jsonPath("errors", Matchers.hasSize(1))));
    }

    @Test
    void when_partial_update_valid_group_events_target_with_not_existing_country_using_json_merge_patch_should_have_errors() {

        String notExistingCountry = "not existing country";
        String eventDateString = "2001-08-05";

        String jsonMergePatch = "{\"eventsCaused\" : [{" +
                "\"id\" : \"" + eventNode.getId().intValue() + "\", " +
                "\"summary\" : \"" + eventNode.getSummary() + "\", " +
                "\"motive\" : \"" + eventNode.getMotive() + "\", " +
                "\"date\" : \"" + eventDateString + "\", " +
                "\"isPartOfMultipleIncidents\" : \"" + eventNode.getIsPartOfMultipleIncidents() + "\", " +
                "\"isSuccessful\" : \"" + eventNode.getIsSuccessful() + "\", " +
                "\"isSuicidal\" : \"" + eventNode.getIsSuicidal() + "\", " +
                "\"target\" : " +
                "{" +
                "\"id\" : \"" + targetNode.getId().intValue() + "\", " +
                "\"target\" : \"" + targetNode.getTarget() + "\", " +
                "\"countryOfOrigin\" : " +
                "{ " +
                "\"name\" : \"" + notExistingCountry + "\"" +
                "}" +
                "}," +
                "\"city\" : " +
                "{ " +
                "\"id\" : \"" + cityNode.getId().intValue() + "\", " +
                "\"name\" : \"" + cityNode.getName() + "\"," +
                "\"latitude\" : " + cityNode.getLatitude() + "," +
                "\"longitude\" : " + cityNode.getLongitude() +
                "}" +
                "}]}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("A country with the provided name does not exist.")))
                        .andExpect(jsonPath("errors", Matchers.hasSize(1))));
    }

    @Test
    void when_partial_update_invalid_group_event_with_null_fields_using_json_merge_patch_should_return_errors() {

        String jsonMergePatch = "{\"eventsCaused\" : [{" +
                "\"summary\" : " + null + ", " +
                "\"motive\" : " + null + ", " +
                "\"date\" : " + null + ", " +
                "\"isPartOfMultipleIncidents\" : " + null + ", " +
                "\"isSuccessful\" : " + null + ", " +
                "\"isSuicidal\" : " + null + ", " +
                "\"target\" : " + null + ", " +
                "\"city\" : " + null +
                "}]}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, groupNode.getId())
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

    @ParameterizedTest(name = "{index}: For Group Event summary: {0} should have violation")
    @EmptySource
    @ValueSource(strings = {" "})
    void when_partial_update_group_event_with_invalid_summary_using_json_merge_patch_should_return_errors(
            String invalidSummary) {

        String eventDateString = "2001-08-05";

        String jsonMergePatch = "{\"eventsCaused\" : [{" +
                "\"id\" : \"" + eventNode.getId().intValue() + "\", " +
                "\"summary\" : \"" + invalidSummary + "\", " +
                "\"motive\" : \"" + eventNode.getMotive() + "\", " +
                "\"date\" : \"" + eventDateString + "\", " +
                "\"isPartOfMultipleIncidents\" : \"" + eventNode.getIsPartOfMultipleIncidents() + "\", " +
                "\"isSuccessful\" : \"" + eventNode.getIsSuccessful() + "\", " +
                "\"isSuicidal\" : \"" + eventNode.getIsSuicidal() + "\", " +
                "\"target\" : " +
                "{" +
                "\"id\" : \"" + targetNode.getId().intValue() + "\", " +
                "\"target\" : \"" + targetNode.getTarget() + "\", " +
                "\"countryOfOrigin\" : " +
                "{ " +
                "\"id\" : \"" + countryNode.getId().intValue() + "\", " +
                "\"name\" : \"" + countryNode.getName() + "\"" +
                "}" +
                "}," +
                "\"city\" : " +
                "{ " +
                "\"id\" : \"" + cityNode.getId().intValue() + "\", " +
                "\"name\" : \"" + cityNode.getName() + "\"," +
                "\"latitude\" : " + cityNode.getLatitude() + "," +
                "\"longitude\" : " + cityNode.getLongitude() +
                "}" +
                "}]}";


        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event summary cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Group Event motive: {0} should have violation")
    @EmptySource
    @ValueSource(strings = {" "})
    void when_partial_update_group_event_with_invalid_motive_using_json_merge_patch_should_return_errors(
            String invalidMotive) {

        String eventDateString = "2001-08-05";

        String jsonMergePatch = "{\"eventsCaused\" : [{" +
                "\"id\" : \"" + eventNode.getId().intValue() + "\", " +
                "\"summary\" : \"" + eventNode.getSummary() + "\", " +
                "\"motive\" : \"" + invalidMotive + "\", " +
                "\"date\" : \"" + eventDateString + "\", " +
                "\"isPartOfMultipleIncidents\" : \"" + eventNode.getIsPartOfMultipleIncidents() + "\", " +
                "\"isSuccessful\" : \"" + eventNode.getIsSuccessful() + "\", " +
                "\"isSuicidal\" : \"" + eventNode.getIsSuicidal() + "\", " +
                "\"target\" : " +
                "{" +
                "\"id\" : \"" + targetNode.getId().intValue() + "\", " +
                "\"target\" : \"" + targetNode.getTarget() + "\", " +
                "\"countryOfOrigin\" : " +
                "{ " +
                "\"id\" : \"" + countryNode.getId().intValue() + "\", " +
                "\"name\" : \"" + countryNode.getName() + "\"" +
                "}" +
                "}," +
                "\"city\" : " +
                "{ " +
                "\"id\" : \"" + cityNode.getId().intValue() + "\", " +
                "\"name\" : \"" + cityNode.getName() + "\"," +
                "\"latitude\" : " + cityNode.getLatitude() + "," +
                "\"longitude\" : " + cityNode.getLongitude() +
                "}" +
                "}]}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, groupNode.getId())
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
    void when_partial_update_group_event_with_date_in_the_future_using_json_merge_patch_should_return_errors() {

        String invalidDate = "2101-08-05";

        String jsonMergePatch = "{\"eventsCaused\" : [{" +
                "\"id\" : \"" + eventNode.getId().intValue() + "\", " +
                "\"summary\" : \"" + eventNode.getSummary() + "\", " +
                "\"motive\" : \"" + eventNode.getMotive() + "\", " +
                "\"date\" : \"" + invalidDate + "\", " +
                "\"isPartOfMultipleIncidents\" : \"" + eventNode.getIsPartOfMultipleIncidents() + "\", " +
                "\"isSuccessful\" : \"" + eventNode.getIsSuccessful() + "\", " +
                "\"isSuicidal\" : \"" + eventNode.getIsSuicidal() + "\", " +
                "\"target\" : " +
                "{" +
                "\"id\" : \"" + targetNode.getId().intValue() + "\", " +
                "\"target\" : \"" + targetNode.getTarget() + "\", " +
                "\"countryOfOrigin\" : " +
                "{ " +
                "\"id\" : \"" + countryNode.getId().intValue() + "\", " +
                "\"name\" : \"" + countryNode.getName() + "\"" +
                "}" +
                "}," +
                "\"city\" : " +
                "{ " +
                "\"id\" : \"" + cityNode.getId().intValue() + "\", " +
                "\"name\" : \"" + cityNode.getName() + "\"," +
                "\"latitude\" : " + cityNode.getLatitude() + "," +
                "\"longitude\" : " + cityNode.getLongitude() +
                "}" +
                "}]}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event date cannot be in the future.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Group Event City name: {0} should have violation")
    @EmptySource
    @ValueSource(strings = {" "})
    void when_partial_update_group_event_with_invalid_city_name_using_json_merge_patch_should_return_errors(String invalidCityName) {

        String eventDateString = "2001-08-05";

        String jsonMergePatch = "{\"eventsCaused\" : [{" +
                "\"id\" : \"" + eventNode.getId().intValue() + "\", " +
                "\"summary\" : \"" + eventNode.getSummary() + "\", " +
                "\"motive\" : \"" + eventNode.getMotive() + "\", " +
                "\"date\" : \"" + eventDateString + "\", " +
                "\"isPartOfMultipleIncidents\" : \"" + eventNode.getIsPartOfMultipleIncidents() + "\", " +
                "\"isSuccessful\" : \"" + eventNode.getIsSuccessful() + "\", " +
                "\"isSuicidal\" : \"" + eventNode.getIsSuicidal() + "\", " +
                "\"target\" : " +
                "{" +
                "\"id\" : \"" + targetNode.getId().intValue() + "\", " +
                "\"target\" : \"" + targetNode.getTarget() + "\", " +
                "\"countryOfOrigin\" : " +
                "{ " +
                "\"id\" : \"" + countryNode.getId().intValue() + "\", " +
                "\"name\" : \"" + countryNode.getName() + "\"" +
                "}" +
                "}," +
                "\"city\" : " +
                "{ " +
                "\"name\" : \"" + invalidCityName + "\"," +
                "\"latitude\" : " + cityNode.getLatitude() + "," +
                "\"longitude\" : " + cityNode.getLongitude() +
                "}" +
                "}]}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_partial_update_group_event_with_with_null_city_values_using_json_merge_patch_should_return_errors() {

        String eventDateString = "2001-08-05";

        String jsonMergePatch = "{\"eventsCaused\" : [{" +
                "\"id\" : \"" + eventNode.getId().intValue() + "\", " +
                "\"summary\" : \"" + eventNode.getSummary() + "\", " +
                "\"motive\" : \"" + eventNode.getMotive() + "\", " +
                "\"date\" : \"" + eventDateString + "\", " +
                "\"isPartOfMultipleIncidents\" : \"" + eventNode.getIsPartOfMultipleIncidents() + "\", " +
                "\"isSuccessful\" : \"" + eventNode.getIsSuccessful() + "\", " +
                "\"isSuicidal\" : \"" + eventNode.getIsSuicidal() + "\", " +
                "\"target\" : " +
                "{" +
                "\"id\" : \"" + targetNode.getId().intValue() + "\", " +
                "\"target\" : \"" + targetNode.getTarget() + "\", " +
                "\"countryOfOrigin\" : " +
                "{ " +
                "\"id\" : \"" + countryNode.getId().intValue() + "\", " +
                "\"name\" : \"" + countryNode.getName() + "\"" +
                "}" +
                "}," +
                "\"city\" : " +
                "{ " +
                "\"name\" : " + null + "," +
                "\"latitude\" : " + null + "," +
                "\"longitude\" : " + null +
                "}" +
                "}]}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City name cannot be empty.")))
                        .andExpect(jsonPath("errors", Matchers.hasSize(1))));
    }

    @Test
    void when_partial_update_group_event_with_too_small_city_latitude_using_json_merge_patch_should_return_errors() {

        double invalidCityLatitude = -91.0;

        String eventDateString = "2001-08-05";

        String jsonMergePatch = "{\"eventsCaused\" : [{" +
                "\"id\" : \"" + eventNode.getId().intValue() + "\", " +
                "\"summary\" : \"" + eventNode.getSummary() + "\", " +
                "\"motive\" : \"" + eventNode.getMotive() + "\", " +
                "\"date\" : \"" + eventDateString + "\", " +
                "\"isPartOfMultipleIncidents\" : \"" + eventNode.getIsPartOfMultipleIncidents() + "\", " +
                "\"isSuccessful\" : \"" + eventNode.getIsSuccessful() + "\", " +
                "\"isSuicidal\" : \"" + eventNode.getIsSuicidal() + "\", " +
                "\"target\" : " +
                "{" +
                "\"id\" : \"" + targetNode.getId().intValue() + "\", " +
                "\"target\" : \"" + targetNode.getTarget() + "\", " +
                "\"countryOfOrigin\" : " +
                "{ " +
                "\"id\" : \"" + countryNode.getId().intValue() + "\", " +
                "\"name\" : \"" + countryNode.getName() + "\"" +
                "}" +
                "}," +
                "\"city\" : " +
                "{ " +
                "\"name\" : \"" + cityNode.getName() + "\"," +
                "\"latitude\" : " + invalidCityLatitude + "," +
                "\"longitude\" : " + cityNode.getLongitude() +
                "}" +
                "}]}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City latitude must be greater or equal to -90.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_partial_update_group_event_with_too_big_city_latitude_using_json_merge_patch_should_return_errors() {

        double invalidCityLatitude = 91.0;

        String eventDateString = "2001-08-05";

        String jsonMergePatch = "{\"eventsCaused\" : [{" +
                "\"id\" : \"" + eventNode.getId().intValue() + "\", " +
                "\"summary\" : \"" + eventNode.getSummary() + "\", " +
                "\"motive\" : \"" + eventNode.getMotive() + "\", " +
                "\"date\" : \"" + eventDateString + "\", " +
                "\"isPartOfMultipleIncidents\" : \"" + eventNode.getIsPartOfMultipleIncidents() + "\", " +
                "\"isSuccessful\" : \"" + eventNode.getIsSuccessful() + "\", " +
                "\"isSuicidal\" : \"" + eventNode.getIsSuicidal() + "\", " +
                "\"target\" : " +
                "{" +
                "\"id\" : \"" + targetNode.getId().intValue() + "\", " +
                "\"target\" : \"" + targetNode.getTarget() + "\", " +
                "\"countryOfOrigin\" : " +
                "{ " +
                "\"id\" : \"" + countryNode.getId().intValue() + "\", " +
                "\"name\" : \"" + countryNode.getName() + "\"" +
                "}" +
                "}," +
                "\"city\" : " +
                "{ " +
                "\"name\" : \"" + cityNode.getName() + "\"," +
                "\"latitude\" : " + invalidCityLatitude + "," +
                "\"longitude\" : " + cityNode.getLongitude() +
                "}" +
                "}]}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City latitude must be less or equal to 90.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_partial_update_group_event_with_too_small_city_longitude_using_json_merge_patch_should_return_errors() {

        double invalidCityLongitude = -181.0;

        String eventDateString = "2001-08-05";

        String jsonMergePatch = "{\"eventsCaused\" : [{" +
                "\"id\" : \"" + eventNode.getId().intValue() + "\", " +
                "\"summary\" : \"" + eventNode.getSummary() + "\", " +
                "\"motive\" : \"" + eventNode.getMotive() + "\", " +
                "\"date\" : \"" + eventDateString + "\", " +
                "\"isPartOfMultipleIncidents\" : \"" + eventNode.getIsPartOfMultipleIncidents() + "\", " +
                "\"isSuccessful\" : \"" + eventNode.getIsSuccessful() + "\", " +
                "\"isSuicidal\" : \"" + eventNode.getIsSuicidal() + "\", " +
                "\"target\" : " +
                "{" +
                "\"id\" : \"" + targetNode.getId().intValue() + "\", " +
                "\"target\" : \"" + targetNode.getTarget() + "\", " +
                "\"countryOfOrigin\" : " +
                "{ " +
                "\"id\" : \"" + countryNode.getId().intValue() + "\", " +
                "\"name\" : \"" + countryNode.getName() + "\"" +
                "}" +
                "}," +
                "\"city\" : " +
                "{ " +
                "\"name\" : \"" + cityNode.getName() + "\"," +
                "\"latitude\" : " + cityNode.getLatitude() + "," +
                "\"longitude\" : " + invalidCityLongitude +
                "}" +
                "}]}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City longitude must be greater or equal to -180.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_partial_update_group_event_with_too_big_city_longitude_using_json_merge_patch_should_return_errors() {

        double invalidCityLongitude = 181.0;

        String eventDateString = "2001-08-05";

        String jsonMergePatch = "{\"eventsCaused\" : [{" +
                "\"id\" : \"" + eventNode.getId().intValue() + "\", " +
                "\"summary\" : \"" + eventNode.getSummary() + "\", " +
                "\"motive\" : \"" + eventNode.getMotive() + "\", " +
                "\"date\" : \"" + eventDateString + "\", " +
                "\"isPartOfMultipleIncidents\" : \"" + eventNode.getIsPartOfMultipleIncidents() + "\", " +
                "\"isSuccessful\" : \"" + eventNode.getIsSuccessful() + "\", " +
                "\"isSuicidal\" : \"" + eventNode.getIsSuicidal() + "\", " +
                "\"target\" : " +
                "{" +
                "\"id\" : \"" + targetNode.getId().intValue() + "\", " +
                "\"target\" : \"" + targetNode.getTarget() + "\", " +
                "\"countryOfOrigin\" : " +
                "{ " +
                "\"id\" : \"" + countryNode.getId().intValue() + "\", " +
                "\"name\" : \"" + countryNode.getName() + "\"" +
                "}" +
                "}," +
                "\"city\" : " +
                "{ " +
                "\"name\" : \"" + cityNode.getName() + "\"," +
                "\"latitude\" : " + cityNode.getLatitude() + "," +
                "\"longitude\" : " + invalidCityLongitude +
                "}" +
                "}]}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(LINK_WITH_PARAMETER_FOR_JSON_MERGE_PATCH, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City longitude must be less or equal to 180.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }
}