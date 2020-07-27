package com.NowakArtur97.GlobalTerrorismAPI.controller.event;

import com.NowakArtur97.GlobalTerrorismAPI.mediaType.PatchMediaType;
import com.NowakArtur97.GlobalTerrorismAPI.node.*;
import com.NowakArtur97.GlobalTerrorismAPI.repository.CountryRepository;
import com.NowakArtur97.GlobalTerrorismAPI.repository.EventRepository;
import com.NowakArtur97.GlobalTerrorismAPI.repository.TargetRepository;
import com.NowakArtur97.GlobalTerrorismAPI.repository.UserRepository;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.jwt.JwtUtil;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventController_Tests")
class EventControllerPatchMethodTest {

    private final String EVENT_BASE_PATH = "http://localhost:8080/api/v1/events";
    private final String TARGET_BASE_PATH = "http://localhost:8080/api/v1/targets";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    private static UserNode userNode = new UserNode("user1234", "Password1234!", "user1234email@.com",
            Set.of(new RoleNode("user")));

    private static CountryNode countryNode = new CountryNode("country");
    private static CountryNode anotherCountryNode = new CountryNode("another country");

    private static TargetNode targetNode = new TargetNode("target", countryNode);
    private static TargetNode anotherTargetNode = new TargetNode("target", countryNode);

    private static EventNode eventNode = new EventNode("summary", "motive", new Date(), true, true, true);
    private static EventNode anotherEventNode = new EventNode("summary2", "motive2", new Date(), false, false, false);

    @BeforeAll
    private static void setUp(@Autowired UserRepository userRepository, @Autowired EventRepository eventRepository,
                              @Autowired TargetRepository targetRepository, @Autowired CountryRepository countryRepository) {

        userRepository.save(userNode);

        countryRepository.save(anotherCountryNode);

        eventNode.setTarget(targetNode);
        anotherEventNode.setTarget(anotherTargetNode);

        eventRepository.save(eventNode);
        eventRepository.save(anotherEventNode);
    }

    @AfterAll
    private static void tearDown(@Autowired UserRepository userRepository, @Autowired EventRepository eventRepository,
                                 @Autowired TargetRepository targetRepository, @Autowired CountryRepository countryRepository) {

        userRepository.deleteAll();

        targetRepository.deleteAll();

        eventRepository.deleteAll();

        countryRepository.deleteAll();
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

            String pathToTargetLink = TARGET_BASE_PATH + "/" + targetNode.getId().intValue();
            String pathToEventLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue();
            String pathToTargetEventLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue() + "/targets";

            String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

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
                            .perform(patch(linkWithParameter, eventNode.getId())
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
                            .andExpect(jsonPath("target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.name", is(countryNode.getName())))
                            .andExpect(jsonPath("target.countryOfOrigin.links").isEmpty()));
        }

        @Test
        void when_partial_update_valid_events_target_using_json_patch_should_return_partially_updated_node() {

            String updatedTargetName = "updated target";
            String updatedCountryName = anotherCountryNode.getName();

            String pathToTargetLink = TARGET_BASE_PATH + "/" + anotherTargetNode.getId().intValue();
            String pathToEventLink = EVENT_BASE_PATH + "/" + anotherEventNode.getId().intValue();
            String pathToTargetEventLink = EVENT_BASE_PATH + "/" + anotherEventNode.getId().intValue() + "/targets";

            String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/target/target\", \"value\": \"" + updatedTargetName + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/target/countryOfOrigin/name\", \"value\": \"" + updatedCountryName + "\" }" +
                    "]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, anotherEventNode.getId())
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
                            .andExpect(jsonPath("isPartOfMultipleIncidents", is(anotherEventNode.getIsPartOfMultipleIncidents())))
                            .andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
                            .andExpect(jsonPath("target.links[1].href").doesNotExist())
                            .andExpect(jsonPath("target.id", is(anotherTargetNode.getId().intValue())))
                            .andExpect(jsonPath("target.target", is(updatedTargetName)))
                            .andExpect(jsonPath("target.countryOfOrigin.id", is(anotherCountryNode.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.name", is(updatedCountryName)))
                            .andExpect(jsonPath("target.countryOfOrigin.links").isEmpty()));
        }

        @Test
        void when_partial_update_valid_event_but_event_not_exist_using_json_patch_should_return_error_response() {

            Long notExistingId = 1000L;

            String updatedSummary = "summary updated";
            String updatedMotive = "motive updated";
            String updatedEventDateString = "2001-08-05";
            boolean updatedIsPartOfMultipleIncidents = false;
            boolean updatedIsSuccessful = false;
            boolean updatedIsSuicidal = false;

            String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/summary\", \"value\": \"" + updatedSummary + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/motive\", \"value\": \"" + updatedMotive + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/date\", \"value\": \"" + updatedEventDateString + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/isPartOfMultipleIncidents\", \"value\": \"" + updatedIsPartOfMultipleIncidents + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/isSuccessful\", \"value\": \"" + updatedIsSuccessful + "\" }," +
                    "{ \"op\": \"replace\", \"path\": \"/isSuicidal\", \"value\": \"" + updatedIsSuicidal + "\" }]";

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
                            .andExpect(content().json("{'status': 404}"))
                            .andExpect(jsonPath("errors[0]", is("Could not find EventModel with id: " + notExistingId + ".")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @ParameterizedTest(name = "{index}: For Event Target: {0} should have violation")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_invalid_events_target_using_json_patch_should_have_errors(String invalidTarget) {

            String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/target/target\", \"value\": \"" + invalidTarget + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Target name cannot be empty."))));
        }

        @Test
        void when_partial_update_invalid_event_with_null_fields_using_json_patch_should_return_errors() {

            String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

            String jsonPatch = "[" +
                    "{ \"op\": \"replace\", \"path\": \"/summary\", \"value\": " + null + "}," +
                    "{ \"op\": \"replace\", \"path\": \"/motive\", \"value\": " + null + "}," +
                    "{ \"op\": \"replace\", \"path\": \"/date\", \"value\": " + null + "}," +
                    "{ \"op\": \"replace\", \"path\": \"/isPartOfMultipleIncidents\", \"value\": " + null + "}," +
                    "{ \"op\": \"replace\", \"path\": \"/isSuccessful\", \"value\": " + null + "}," +
                    "{ \"op\": \"replace\", \"path\": \"/isSuicidal\", \"value\": " + null + "}," +
                    "{ \"op\": \"replace\", \"path\": \"/target/target\", \"value\": " + null + "}]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, eventNode.getId())
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
                            .andExpect(jsonPath("errors", hasItem("Target name cannot be empty."))));
        }

        @ParameterizedTest(name = "{index}: For Event Target: {0} should have violation")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_invalid_events_target_using_json_patch_should_return_errors(String invalidTarget) {

            String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/target/target\", \"value\": \"" + invalidTarget + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Target name cannot be empty."))));
        }

        @ParameterizedTest(name = "{index}: For Event summary: {0} should have violation")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_event_with_invalid_summary_using_json_patch_should_return_errors(
                String invalidSummary) {

            String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/summary\", \"value\": \"" + invalidSummary + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Event summary cannot be empty."))));
        }

        @ParameterizedTest(name = "{index}: For Event motive: {0} should have violation")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_event_with_invalid_motive_using_json_patch_should_return_errors(String invalidMotive) {

            String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/motive\", \"value\": \"" + invalidMotive + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Event motive cannot be empty."))));
        }

        @Test
        void when_partial_update_event_with_date_in_the_future_using_json_patch_should_return_errors() {

            String invalidDate = "2101-08-05";

            String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

            String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/date\", \"value\": \"" + invalidDate + "\" }]";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonPatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Event date cannot be in the future."))));
        }
    }

    @Nested
    class EventControllerMergeJsonPatchMethodTest {

        @Test
        void when_partial_update_valid_event_using_json_merge_patch_should_return_partially_updated_node() {

            String updatedSummary = "summary updated 2";
            String updatedMotive = "motive updated 2";
            String updatedEventDateString = "2011-01-03";
            boolean updatedIsPartOfMultipleIncidents = false;
            boolean updatedIsSuccessful = true;
            boolean updatedIsSuicidal = false;

            String pathToTargetLink = TARGET_BASE_PATH + "/" + targetNode.getId().intValue();
            String pathToEventLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue();
            String pathToTargetEventLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue() + "/targets";

            String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

            String jsonMergePatch = "{\"summary\" : \"" + updatedSummary + "\"" + ", " +
                    "\"motive\" : \"" + updatedMotive + "\"" + ", " +
                    "\"date\" : \"" + updatedEventDateString + "\"" + ", " +
                    "\"isPartOfMultipleIncidents\" : " + updatedIsPartOfMultipleIncidents + ", " +
                    "\"isSuccessful\" : " + updatedIsSuccessful + ", " +
                    "\"isSuicidal\" : " + updatedIsSuicidal + "}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, eventNode.getId())
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
                            .andExpect(jsonPath("target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.name", is(countryNode.getName())))
                            .andExpect(jsonPath("target.countryOfOrigin.links").isEmpty()));
        }

        @Test
        void when_partial_update_valid_events_target_using_json_merge_patch_should_return_partially_updated_node() {

            String updatedTargetName = "updated target 2";
            String updatedCountryName = anotherCountryNode.getName();

            String pathToTargetLink = TARGET_BASE_PATH + "/" + anotherTargetNode.getId().intValue();
            String pathToEventLink = EVENT_BASE_PATH + "/" + anotherEventNode.getId().intValue();
            String pathToTargetEventLink = EVENT_BASE_PATH + "/" + anotherEventNode.getId().intValue() + "/targets";

            String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

            String jsonMergePatch =
                    "{\"target\" : {\"target\" : \"" + updatedTargetName + "\"" + ", " +
                            "\"countryOfOrigin\" : { \"name\" : \"" + updatedCountryName + "\"}}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, anotherEventNode.getId())
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
                            .andExpect(jsonPath("target.countryOfOrigin.id", is(anotherCountryNode.getId().intValue())))
                            .andExpect(jsonPath("target.countryOfOrigin.name", is(updatedCountryName)))
                            .andExpect(jsonPath("target.countryOfOrigin.links").isEmpty()));
        }

        @Test
        void when_partial_update_valid_event_but_event_not_exist_using_json_patch_should_return_error_response() {

            Long notExistingId = 1000L;

            String updatedSummary = "summary updated";
            String updatedMotive = "motive updated";
            String updatedEventDateString = "2001-08-05";
            boolean updatedIsPartOfMultipleIncidents = false;
            boolean updatedIsSuccessful = false;
            boolean updatedIsSuicidal = false;

            String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

            String jsonMergePatch = "{\"summary\" : \"" + updatedSummary + "\"" + ", " +
                    "\"motive\" : \"" + updatedMotive + "\"" + ", " +
                    "\"date\" : \"" + updatedEventDateString + "\"" + ", " +
                    "\"isPartOfMultipleIncidents\" : " + updatedIsPartOfMultipleIncidents + ", " +
                    "\"isSuccessful\" : " + updatedIsSuccessful + ", " +
                    "\"isSuicidal\" : " + updatedIsSuicidal + "}";

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
                            .andExpect(content().json("{'status': 404}"))
                            .andExpect(jsonPath("errors[0]", is("Could not find EventModel with id: " + notExistingId + ".")))
                            .andExpect(jsonPath("errors", hasSize(1))));
        }

        @ParameterizedTest(name = "{index}: For Event Target: {0} should have violation")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_invalid_events_target_using_json_merge_patch_should_have_errors(String invalidTarget) {

            String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

            String jsonMergePatch = "{\"target\" : { \"target\" : \"" + invalidTarget + "\"}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Target name cannot be empty."))));
        }

        @Test
        void when_partial_update_invalid_event_with_null_fields_using_json_merge_patch_should_return_errors() {

            String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

            String jsonMergePatch = "{\"summary\" : " + null + ", " +
                    "\"motive\" : " + null + ", " +
                    "\"date\" : " + null + ", " +
                    "\"isPartOfMultipleIncidents\" : " + null + ", " +
                    "\"isSuccessful\" : " + null + ", " +
                    "\"isSuicidal\" : " + null + ", " +
                    "\"target\" : { \"target\" : " + null + "}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, eventNode.getId())
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
                            .andExpect(jsonPath("errors", hasItem("Target name cannot be empty."))));
        }

        @ParameterizedTest(name = "{index}: For Event Target: {0} should have violation")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_invalid_events_target_using_json_merge_patch_should_return_errors(
                String invalidTarget) {

            String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

            String jsonMergePatch = "{\"target\" : {\"target\" : \"" + invalidTarget + "\"}}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Target name cannot be empty."))));
        }

        @ParameterizedTest(name = "{index}: For Event summary: {0} should have violation")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_event_with_invalid_summary_using_json_merge_patch_should_return_errors(
                String invalidSummary) {

            String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

            String jsonMergePatch = "{\"summary\" : \"" + invalidSummary + "\"}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Event summary cannot be empty."))));
        }

        @ParameterizedTest(name = "{index}: For Event motive: {0} should have violation")
        @EmptySource
        @ValueSource(strings = {" "})
        void when_partial_update_event_with_invalid_motive_using_json_merge_patch_should_return_errors(
                String invalidMotive) {

            String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

            String jsonMergePatch = "{\"motive\" : \"" + invalidMotive + "\"}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Event motive cannot be empty."))));
        }

        @Test
        void when_partial_update_event_with_date_in_the_future_using_json_merge_patch_should_return_errors() {

            String invalidDate = "2101-08-05";

            String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

            String jsonMergePatch = "{\"date\" : \"" + invalidDate + "\"}";

            String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                    List.of(new SimpleGrantedAuthority("user"))));

            assertAll(
                    () -> mockMvc
                            .perform(patch(linkWithParameter, eventNode.getId())
                                    .header("Authorization", "Bearer " + token)
                                    .content(jsonMergePatch)
                                    .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("timestamp", is(notNullValue())))
                            .andExpect(jsonPath("status", is(400)))
                            .andExpect(jsonPath("errors[0]", is("Event date cannot be in the future."))));
        }
    }
}