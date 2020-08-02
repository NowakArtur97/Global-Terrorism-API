package com.NowakArtur97.GlobalTerrorismAPI.controller.group;

import com.NowakArtur97.GlobalTerrorismAPI.mediaType.PatchMediaType;
import com.NowakArtur97.GlobalTerrorismAPI.node.*;
import com.NowakArtur97.GlobalTerrorismAPI.repository.*;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.jwt.JwtUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
@AutoConfigureMockMvc
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("GroupController_Tests")
class GroupControllerPatchMethodTest {

    private final String TARGET_BASE_PATH = "http://localhost:8080/api/v1/targets";
    private final String EVENT_BASE_PATH = "http://localhost:8080/api/v1/events";
    private final String GROUP_BASE_PATH = "http://localhost:8080/api/v1/groups";

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
    private final static TargetNode targetNode4 = new TargetNode("target 4", countryNode);
    private final static TargetNode targetNode5 = new TargetNode("target 5", countryNode);

    private final static EventNode eventNode = new EventNode("summary", "motive", new Date(), true, true, true, targetNode);
    private final static EventNode eventNode2 = new EventNode("summary 2", "motive 2", new Date(), false, false, false, targetNode2);
    private final static EventNode eventNode3 = new EventNode("summary 3", "motive 3", new Date(), true, false, true, targetNode3);
    private final static EventNode eventNode4 = new EventNode("summary 4", "motive 4", new Date(), true, false, true, targetNode4);
    private final static EventNode eventNode5 = new EventNode("summary 5", "motive 5", new Date(), true, false, true, targetNode5);

    private final static GroupNode groupNode = new GroupNode("group", List.of(eventNode));
    private final static GroupNode groupNodeWithMultipleEvents = new GroupNode("group 2 ", List.of(eventNode2, eventNode3));
    private final static GroupNode groupNodeWithMultipleEvents2 = new GroupNode("group 3", List.of(eventNode4, eventNode5));

    @BeforeAll
    private static void setUp(@Autowired UserRepository userRepository, @Autowired GroupRepository groupRepository) {

        groupRepository.findAll().forEach(System.out::println);

        userRepository.save(userNode);

        groupRepository.save(groupNode);
        groupRepository.save(groupNodeWithMultipleEvents);
        groupRepository.save(groupNodeWithMultipleEvents2);
    }

    @AfterAll
    private static void tearDown(@Autowired UserRepository userRepository, @Autowired GroupRepository groupRepository,
                                 @Autowired CountryRepository countryRepository, @Autowired EventRepository eventRepository,
                                 @Autowired TargetRepository targetRepository) {

        userRepository.deleteAll();

        countryRepository.deleteAll();

        groupRepository.deleteAll();

        eventRepository.deleteAll();

        targetRepository.deleteAll();
    }

    @Test
    void when_partial_update_valid_group_using_json_patch_should_return_partially_updated_node() {

        String updatedName = "updated group name";

        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetNode2.getId().intValue();
        String pathToTargetLink2 = TARGET_BASE_PATH + "/" + targetNode3.getId().intValue();
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventNode2.getId().intValue();
        String pathToEventLink2 = EVENT_BASE_PATH + "/" + eventNode3.getId().intValue();
        String pathToEventTargetLink = EVENT_BASE_PATH + "/" + eventNode2.getId().intValue() + "/targets";
        String pathToEventTargetLink2 = EVENT_BASE_PATH + "/" + eventNode3.getId().intValue() + "/targets";
        String pathToGroupLink = GROUP_BASE_PATH + "/" + groupNodeWithMultipleEvents.getId().intValue();
        String pathToEventsLink = GROUP_BASE_PATH + "/" + groupNodeWithMultipleEvents.getId().intValue() + "/events";

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/name\", \"value\": \"" + updatedName + "\" }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNodeWithMultipleEvents.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToGroupLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
                        .andExpect(jsonPath("id", is(groupNodeWithMultipleEvents.getId().intValue())))
                        .andExpect(jsonPath("name", is(updatedName)))
                        .andExpect(jsonPath("eventsCaused[0].links[0].href", is(pathToEventLink)))
                        .andExpect(jsonPath("eventsCaused[0].links[1].href", is(pathToEventTargetLink)))
                        .andExpect(jsonPath("eventsCaused[0].id", is(eventNode2.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].summary", is(eventNode2.getSummary())))
                        .andExpect(jsonPath("eventsCaused[0].motive", is(eventNode2.getMotive())))
                        .andExpect(jsonPath("eventsCaused[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventNode.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(eventNode2.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(eventNode2.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents", is(eventNode2.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[0].target.links[0].href", is(pathToTargetLink)))
                        .andExpect(jsonPath("eventsCaused[0].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.id", is(targetNode2.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.target", is(targetNode2.getTarget())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links").isEmpty())
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
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[2]").doesNotExist()));
    }

    @Test
    void when_partial_update_valid_group_event_using_json_patch_should_return_partially_updated_node() {

        String updatedSummary = "summary updated";
        String updatedMotive = "motive updated";
        String updatedEventDateString = "2011-02-15";
        boolean updatedIsPartOfMultipleIncidents = false;
        boolean updatedIsSuccessful = false;
        boolean updatedIsSuicidal = false;

        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetNode4.getId().intValue();
        String pathToTargetLink2 = TARGET_BASE_PATH + "/" + targetNode5.getId().intValue();
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventNode4.getId().intValue();
        String pathToEventLink2 = EVENT_BASE_PATH + "/" + eventNode5.getId().intValue();
        String pathToEventTargetLink = EVENT_BASE_PATH + "/" + eventNode4.getId().intValue() + "/targets";
        String pathToEventTargetLink2 = EVENT_BASE_PATH + "/" + eventNode5.getId().intValue() + "/targets";
        String pathToGroupLink = GROUP_BASE_PATH + "/" + groupNodeWithMultipleEvents2.getId().intValue();
        String pathToEventsLink = GROUP_BASE_PATH + "/" + groupNodeWithMultipleEvents2.getId().intValue() + "/events";

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        String jsonPatch = "[" +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/summary\", \"value\": \"" + updatedSummary + "\" }," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/motive\", \"value\": \"" + updatedMotive + "\" }," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/date\", \"value\": \"" + updatedEventDateString + "\" }," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/isPartOfMultipleIncidents\", \"value\": \""
                + updatedIsPartOfMultipleIncidents + "\" }," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/isSuccessful\", \"value\": \""
                + updatedIsSuccessful + "\" }," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/isSuicidal\", \"value\": \"" + updatedIsSuicidal + "\"}" +
                "]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNodeWithMultipleEvents2.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToGroupLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
                        .andExpect(jsonPath("id", is(groupNodeWithMultipleEvents2.getId().intValue())))
                        .andExpect(jsonPath("name", is(groupNodeWithMultipleEvents2.getName())))
                        .andExpect(jsonPath("eventsCaused[0].links[0].href", is(pathToEventLink)))
                        .andExpect(jsonPath("eventsCaused[0].links[1].href", is(pathToEventTargetLink)))
                        .andExpect(jsonPath("eventsCaused[0].id", is(eventNode4.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].summary", is(updatedSummary)))
                        .andExpect(jsonPath("eventsCaused[0].motive", is(updatedMotive)))
                        .andExpect(jsonPath("eventsCaused[0].date", is(updatedEventDateString)))
                        .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(updatedIsSuicidal)))
                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(updatedIsSuccessful)))
                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents", is(updatedIsPartOfMultipleIncidents)))
                        .andExpect(jsonPath("eventsCaused[0].target.links[0].href", is(pathToTargetLink)))
                        .andExpect(jsonPath("eventsCaused[0].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.id", is(targetNode4.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.target", is(targetNode4.getTarget())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[1].links[0].href", is(pathToEventLink2)))
                        .andExpect(jsonPath("eventsCaused[1].links[1].href", is(pathToEventTargetLink2)))
                        .andExpect(jsonPath("eventsCaused[1].id", is(eventNode5.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].summary", is(eventNode5.getSummary())))
                        .andExpect(jsonPath("eventsCaused[1].motive", is(eventNode5.getMotive())))
                        .andExpect(jsonPath("eventsCaused[1].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventNode5.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[1].isSuicidal", is(eventNode5.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[1].isSuccessful", is(eventNode5.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[1].isPartOfMultipleIncidents", is(eventNode5.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[1].target.links[0].href", is(pathToTargetLink2)))
                        .andExpect(jsonPath("eventsCaused[1].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].target.id", is(targetNode5.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.target", is(targetNode5.getTarget())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[2]").doesNotExist()));
    }

    @Test
    void when_partial_update_valid_group_but_group_not_exist_using_json_patch_should_return_error_response() {

        Long notExistingId = 10000L;

        String updatedName = "updated group name";

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/name\", \"value\": \"" + updatedName + "\" }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, notExistingId)
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

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/name\", \"value\": " + null + " }," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused\", \"value\": " + null + "}]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNode.getId())
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

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused\", \"value\": " + "[]" + "}]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("List of Events caused by the Group cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Group name: {0} should have violation")
    @EmptySource
    @ValueSource(strings = {" "})
    void when_partial_update_group_with_invalid_name_using_json_patch_should_return_errors(
            String invalidName) {

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/name\", \"value\": \"" + invalidName + "\" }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Group name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Event Target: {0} should have violation")
    @EmptySource
    @ValueSource(strings = {" "})
    void when_partial_update_invalid_group_events_target_using_json_patch_should_have_errors(String invalidTarget) {

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/target/target\", " +
                "\"value\": \"" + invalidTarget + "\" }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNode.getId())
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

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/target/target\", " +
                "\"value\": \"" + updatedTargetName + "\" }," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/target/countryOfOrigin/name\", " +
                "\"value\": " + null + "}]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Country name cannot be empty.")))
                        .andExpect(jsonPath("errors", Matchers.hasSize(1))));
    }

    @Test
    void when_partial_update_valid_group_events_target_with_not_existing_country_using_json_patch_should_have_errors() {

        String updatedTargetName = "updated target";
        String notExistingCountryName = "not existing country";

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/target/target\", " +
                "\"value\": \"" + updatedTargetName + "\" }," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/target/countryOfOrigin/name\"," +
                " \"value\": \"" + notExistingCountryName + "\" }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("A country with the provided name does not exist.")))
                        .andExpect(jsonPath("errors", Matchers.hasSize(1))));
    }

    @Test
    void when_partial_update_invalid_group_event_with_null_fields_using_json_patch_should_return_errors() {

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        String jsonPatch = "[" +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/summary\", \"value\": " + null + "}," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/motive\", \"value\": " + null + "}," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/date\", \"value\": " + null + "}," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/isPartOfMultipleIncidents\", \"value\": " + null + "}," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/isSuccessful\", \"value\": " + null + "}," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/isSuicidal\", \"value\": " + null + "}," +
                "{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/target\", \"value\": " + null + "}" +
                "]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNode.getId())
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
                        .andExpect(jsonPath("errors", hasSize(7))));
    }

    @ParameterizedTest(name = "{index}: For Group Event summary: {0} should have violation")
    @EmptySource
    @ValueSource(strings = {" "})
    void when_partial_update_group_event_with_invalid_summary_using_json_patch_should_return_errors(
            String invalidSummary) {

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/summary\", \"value\": \"" + invalidSummary + "\" }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event summary cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Group Event motive: {0} should have violation")
    @EmptySource
    @ValueSource(strings = {" "})
    void when_partial_update_group_event_with_invalid_motive_using_json_patch_should_return_errors(String invalidMotive) {

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/motive\", \"value\": \"" + invalidMotive + "\" }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNode.getId())
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

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/eventsCaused/0/date\", \"value\": \"" + invalidDate + "\" }]";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event date cannot be in the future.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_partial_update_valid_group_using_json_merge_patch_should_return_partially_updated_node() {

        String updatedName = "updated group name";

        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetNode2.getId().intValue();
        String pathToTargetLink2 = TARGET_BASE_PATH + "/" + targetNode3.getId().intValue();
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventNode2.getId().intValue();
        String pathToEventLink2 = EVENT_BASE_PATH + "/" + eventNode3.getId().intValue();
        String pathToEventTargetLink = EVENT_BASE_PATH + "/" + eventNode2.getId().intValue() + "/targets";
        String pathToEventTargetLink2 = EVENT_BASE_PATH + "/" + eventNode3.getId().intValue() + "/targets";
        String pathToGroupLink = GROUP_BASE_PATH + "/" + groupNodeWithMultipleEvents.getId().intValue();
        String pathToEventsLink = GROUP_BASE_PATH + "/" + groupNodeWithMultipleEvents.getId().intValue() + "/events";

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id2}";

        String jsonMergePatch = "{\"name\" : \"" + updatedName + "\"}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNodeWithMultipleEvents.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToGroupLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
                        .andExpect(jsonPath("id", is(groupNodeWithMultipleEvents.getId().intValue())))
                        .andExpect(jsonPath("name", is(updatedName)))
                        .andExpect(jsonPath("eventsCaused[0].links[0].href", is(pathToEventLink)))
                        .andExpect(jsonPath("eventsCaused[0].links[1].href", is(pathToEventTargetLink)))
                        .andExpect(jsonPath("eventsCaused[0].id", is(eventNode2.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].summary", is(eventNode2.getSummary())))
                        .andExpect(jsonPath("eventsCaused[0].motive", is(eventNode2.getMotive())))
                        .andExpect(jsonPath("eventsCaused[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventNode.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(eventNode2.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(eventNode2.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents", is(eventNode2.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[0].target.links[0].href", is(pathToTargetLink)))
                        .andExpect(jsonPath("eventsCaused[0].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.id", is(targetNode2.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.target", is(targetNode2.getTarget())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links").isEmpty())
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
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[2]").doesNotExist()));
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

        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetNode4.getId().intValue();
        String pathToTargetLink2 = TARGET_BASE_PATH + "/" + targetNode5.getId().intValue();
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventNode4.getId().intValue();
        String pathToEventLink2 = EVENT_BASE_PATH + "/" + eventNode5.getId().intValue();
        String pathToEventTargetLink = EVENT_BASE_PATH + "/" + eventNode4.getId().intValue() + "/targets";
        String pathToEventTargetLink2 = EVENT_BASE_PATH + "/" + eventNode5.getId().intValue() + "/targets";
        String pathToGroupLink = GROUP_BASE_PATH + "/" + groupNodeWithMultipleEvents2.getId().intValue();
        String pathToEventsLink = GROUP_BASE_PATH + "/" + groupNodeWithMultipleEvents2.getId().intValue() + "/events";

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id2}";

        String jsonMergePatch = "{\"eventsCaused\" : [" +
                "{" +
                "\"id\" : \"" + eventNode4.getId().intValue() + "\", " +
                "\"summary\" : \"" + updatedSummary + "\", " +
                "\"motive\" : \"" + updatedMotive + "\", " +
                "\"date\" : \"" + updatedEventDateString + "\", " +
                "\"isPartOfMultipleIncidents\" : \"" + updatedIsPartOfMultipleIncidents + "\", " +
                "\"isSuccessful\" : \"" + updatedIsSuccessful + "\", " +
                "\"isSuicidal\" : \"" + updatedIsSuicidal + "\", " +
                "\"target\" : {" +
                "\"id\" : \"" + targetNode4.getId().intValue() + "\", " +
                "\"target\" : \"" + updatedTargetName + "\", " +
                "\"countryOfOrigin\" : { " +
                "\"id\" : \"" + countryNode.getId().intValue() + "\", " +
                "\"name\" : \"" + countryNode.getName() + "\"}" +
                "}" +
                "}," +
                "{" +
                "\"id\" : \"" + eventNode5.getId().intValue() + "\", " +
                "\"summary\" : \"" + eventNode5.getSummary() + "\", " +
                "\"motive\" : \"" + eventNode5.getMotive() + "\", " +
                "\"date\" : \"" + updatedEventDateString2 + "\", " +
                "\"isPartOfMultipleIncidents\" : \"" + eventNode5.getIsPartOfMultipleIncidents() + "\", " +
                "\"isSuccessful\" : \"" + eventNode5.getIsSuccessful() + "\", " +
                "\"isSuicidal\" : \"" + eventNode5.getIsSuicidal() + "\", " +
                "\"target\" : {" +
                "\"id\" : \"" + targetNode5.getId().intValue() + "\", " +
                "\"target\" : \"" + targetNode5.getTarget() + "\", " +
                "\"countryOfOrigin\" : { " +
                "\"id\" : \"" + countryNode.getId().intValue() + "\", " +
                "\"name\" : \"" + countryNode.getName() + "\"}}" +
                "}" +
                "]}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNodeWithMultipleEvents2.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToGroupLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
                        .andExpect(jsonPath("id", is(groupNodeWithMultipleEvents2.getId().intValue())))
                        .andExpect(jsonPath("name", is(groupNodeWithMultipleEvents2.getName())))
                        .andExpect(jsonPath("eventsCaused[0].links[0].href", is(pathToEventLink)))
                        .andExpect(jsonPath("eventsCaused[0].links[1].href", is(pathToEventTargetLink)))
                        .andExpect(jsonPath("eventsCaused[0].id", is(eventNode4.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].summary", is(updatedSummary)))
                        .andExpect(jsonPath("eventsCaused[0].motive", is(updatedMotive)))
                        .andExpect(jsonPath("eventsCaused[0].date", is(updatedEventDateString)))
                        .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(updatedIsSuicidal)))
                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(updatedIsSuccessful)))
                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents", is(updatedIsPartOfMultipleIncidents)))
                        .andExpect(jsonPath("eventsCaused[0].target.links[0].href", is(pathToTargetLink)))
                        .andExpect(jsonPath("eventsCaused[0].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.id", is(targetNode4.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.target", is(updatedTargetName)))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[1].links[0].href", is(pathToEventLink2)))
                        .andExpect(jsonPath("eventsCaused[1].links[1].href", is(pathToEventTargetLink2)))
                        .andExpect(jsonPath("eventsCaused[1].id", is(eventNode5.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].summary", is(eventNode5.getSummary())))
                        .andExpect(jsonPath("eventsCaused[1].motive", is(eventNode5.getMotive())))
                        .andExpect(jsonPath("eventsCaused[1].date", is(updatedEventDateString2)))
                        .andExpect(jsonPath("eventsCaused[1].isSuicidal", is(eventNode5.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[1].isSuccessful", is(eventNode5.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[1].isPartOfMultipleIncidents", is(eventNode5.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[1].target.links[0].href", is(pathToTargetLink2)))
                        .andExpect(jsonPath("eventsCaused[1].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].target.id", is(targetNode5.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.target", is(targetNode5.getTarget())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[2]").doesNotExist()));
    }

    @Test
    void when_partial_update_valid_group_but_group_not_exist_using_json_merge_patch_should_return_error_response() {

        Long notExistingId = 10000L;

        String updatedName = "updated group name";

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id2}";

        String jsonMergePatch = "{ \"name\" : \"" + updatedName + "\" }";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, notExistingId)
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

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id2}";

        String jsonMergePatch = "{\"name\" : " + null + ", \"eventsCaused\" : " + null + "}";
        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNode.getId())
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

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id2}";

        String jsonMergePatch = "{ \"eventsCaused\" : " + "[]" + "}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNode.getId())
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

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id2}";

        String jsonMergePatch = "{ \"name\" : \"" + invalidName + "\" }";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNode.getId())
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
        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id2}";

        String jsonMergePatch = "{\"eventsCaused\" : [{" +
                "\"id\" : \"" + eventNode.getId().intValue() + "\", " +
                "\"summary\" : \"" + eventNode.getSummary() + "\", " +
                "\"motive\" : \"" + eventNode.getMotive() + "\", " +
                "\"date\" : \"" + eventDateString + "\", " +
                "\"isPartOfMultipleIncidents\" : \"" + eventNode.getIsPartOfMultipleIncidents() + "\", " +
                "\"isSuccessful\" : \"" + eventNode.getIsSuccessful() + "\", " +
                "\"isSuicidal\" : \"" + eventNode.getIsSuicidal() + "\", " +
                "\"target\" : {" +
                "\"id\" : \"" + targetNode.getId().intValue() + "\", " +
                "\"target\" : \"" + invalidTarget + "\", " +
                "\"countryOfOrigin\" : { " +
                "\"id\" : \"" + countryNode.getId().intValue() + "\", " +
                "\"name\" : \"" + countryNode.getName() + "\"}" +
                "}}]}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNode.getId())
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
        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id2}";

        String jsonMergePatch = "{\"eventsCaused\" : [{" +
                "\"id\" : \"" + eventNode.getId().intValue() + "\", " +
                "\"summary\" : \"" + eventNode.getSummary() + "\", " +
                "\"motive\" : \"" + eventNode.getMotive() + "\", " +
                "\"date\" : \"" + eventDateString + "\", " +
                "\"isPartOfMultipleIncidents\" : \"" + eventNode.getIsPartOfMultipleIncidents() + "\", " +
                "\"isSuccessful\" : \"" + eventNode.getIsSuccessful() + "\", " +
                "\"isSuicidal\" : \"" + eventNode.getIsSuicidal() + "\", " +
                "\"target\" : {" +
                "\"id\" : \"" + targetNode.getId().intValue() + "\", " +
                "\"target\" : \"" + targetNode.getTarget() + "\", " +
                "\"countryOfOrigin\" : { " +
                "\"name\" : " + null + "}" +
                "}}]}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNode.getId())
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
        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id2}";

        String jsonMergePatch = "{\"eventsCaused\" : [{" +
                "\"id\" : \"" + eventNode.getId().intValue() + "\", " +
                "\"summary\" : \"" + eventNode.getSummary() + "\", " +
                "\"motive\" : \"" + eventNode.getMotive() + "\", " +
                "\"date\" : \"" + eventDateString + "\", " +
                "\"isPartOfMultipleIncidents\" : \"" + eventNode.getIsPartOfMultipleIncidents() + "\", " +
                "\"isSuccessful\" : \"" + eventNode.getIsSuccessful() + "\", " +
                "\"isSuicidal\" : \"" + eventNode.getIsSuicidal() + "\", " +
                "\"target\" : {" +
                "\"id\" : \"" + targetNode.getId().intValue() + "\", " +
                "\"target\" : \"" + targetNode.getTarget() + "\", " +
                "\"countryOfOrigin\" : { " +
                "\"name\" : \"" + notExistingCountry + "\"}" +
                "}}]}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNode.getId())
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

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id2}";

        String jsonMergePatch = "{\"eventsCaused\" : [{" +
                "\"summary\" : " + null + ", " +
                "\"motive\" : " + null + ", " +
                "\"date\" : " + null + ", " +
                "\"isPartOfMultipleIncidents\" : " + null + ", " +
                "\"isSuccessful\" : " + null + ", " +
                "\"isSuicidal\" : " + null + ", " +
                "\"target\" : " + null +
                "}]}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNode.getId())
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
                        .andExpect(jsonPath("errors", hasSize(7))));
    }

    @ParameterizedTest(name = "{index}: For Group Event summary: {0} should have violation")
    @EmptySource
    @ValueSource(strings = {" "})
    void when_partial_update_group_event_with_invalid_summary_using_json_merge_patch_should_return_errors(
            String invalidSummary) {

        String eventDateString = "2001-08-05";
        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id2}";

        String jsonMergePatch = "{\"eventsCaused\" : [{" +
                "\"id\" : \"" + eventNode.getId().intValue() + "\", " +
                "\"summary\" : \"" + invalidSummary + "\", " +
                "\"motive\" : \"" + eventNode.getMotive() + "\", " +
                "\"date\" : \"" + eventDateString + "\", " +
                "\"isPartOfMultipleIncidents\" : \"" + eventNode.getIsPartOfMultipleIncidents() + "\", " +
                "\"isSuccessful\" : \"" + eventNode.getIsSuccessful() + "\", " +
                "\"isSuicidal\" : \"" + eventNode.getIsSuicidal() + "\", " +
                "\"target\" : {" +
                "\"id\" : \"" + targetNode.getId().intValue() + "\", " +
                "\"target\" : \"" + targetNode.getTarget() + "\", " +
                "\"countryOfOrigin\" : { " +
                "\"id\" : \"" + countryNode.getId().intValue() + "\", " +
                "\"name\" : \"" + countryNode.getName() + "\"}" +
                "}}]}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNode.getId())
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
        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id2}";

        String jsonMergePatch = "{\"eventsCaused\" : [{" +
                "\"id\" : \"" + eventNode.getId().intValue() + "\", " +
                "\"summary\" : \"" + eventNode.getSummary() + "\", " +
                "\"motive\" : \"" + invalidMotive + "\", " +
                "\"date\" : \"" + eventDateString + "\", " +
                "\"isPartOfMultipleIncidents\" : \"" + eventNode.getIsPartOfMultipleIncidents() + "\", " +
                "\"isSuccessful\" : \"" + eventNode.getIsSuccessful() + "\", " +
                "\"isSuicidal\" : \"" + eventNode.getIsSuicidal() + "\", " +
                "\"target\" : {" +
                "\"id\" : \"" + targetNode.getId().intValue() + "\", " +
                "\"target\" : \"" + targetNode.getTarget() + "\", " +
                "\"countryOfOrigin\" : { " +
                "\"id\" : \"" + countryNode.getId().intValue() + "\", " +
                "\"name\" : \"" + countryNode.getName() + "\"}" +
                "}}]}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNode.getId())
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

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id2}";

        String jsonMergePatch = "{\"eventsCaused\" : [{" +
                "\"id\" : \"" + eventNode.getId().intValue() + "\", " +
                "\"summary\" : \"" + eventNode.getSummary() + "\", " +
                "\"motive\" : \"" + eventNode.getMotive() + "\", " +
                "\"date\" : \"" + invalidDate + "\", " +
                "\"isPartOfMultipleIncidents\" : \"" + eventNode.getIsPartOfMultipleIncidents() + "\", " +
                "\"isSuccessful\" : \"" + eventNode.getIsSuccessful() + "\", " +
                "\"isSuicidal\" : \"" + eventNode.getIsSuicidal() + "\", " +
                "\"target\" : {" +
                "\"id\" : \"" + targetNode.getId().intValue() + "\", " +
                "\"target\" : \"" + targetNode.getTarget() + "\", " +
                "\"countryOfOrigin\" : { " +
                "\"id\" : \"" + countryNode.getId().intValue() + "\", " +
                "\"name\" : \"" + countryNode.getName() + "\"}" +
                "}}]}";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event date cannot be in the future.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }
}
