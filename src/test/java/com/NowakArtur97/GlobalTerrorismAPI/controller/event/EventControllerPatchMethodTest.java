package com.NowakArtur97.GlobalTerrorismAPI.controller.event;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import javax.json.JsonMergePatch;
import javax.json.JsonPatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.NowakArtur97.GlobalTerrorismAPI.advice.RestResponseGlobalEntityExceptionHandler;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.EventModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.controller.EventController;
import com.NowakArtur97.GlobalTerrorismAPI.httpMessageConverter.JsonMergePatchHttpMessageConverter;
import com.NowakArtur97.GlobalTerrorismAPI.httpMessageConverter.JsonPatchHttpMessageConverter;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.EventService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.mediaType.PatchMediaType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.ViolationHelper;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventController_Tests")
@DisabledOnOs(OS.LINUX)
class EventControllerPatchMethodTest {

	private final String EVENT_BASE_PATH = "http://localhost:8080/api/events";
	private final String TARGET_BASE_PATH = "http://localhost:8080/api/targets";

	private MockMvc mockMvc;

	private EventController eventController;

	private RestResponseGlobalEntityExceptionHandler restResponseGlobalEntityExceptionHandler;

	@Mock
	private EventService eventService;

	@Mock
	private EventModelAssembler modelAssembler;

	@Mock
	private PagedResourcesAssembler<EventNode> pagedResourcesAssembler;

	@Mock
	private PatchHelper patchHelper;

	@Autowired
	private ViolationHelper violationHelper;

	@BeforeEach
	private void setUp() {

		eventController = new EventController(eventService, modelAssembler, pagedResourcesAssembler, patchHelper,
				violationHelper);

		restResponseGlobalEntityExceptionHandler = new RestResponseGlobalEntityExceptionHandler();

		mockMvc = MockMvcBuilders.standaloneSetup(eventController, restResponseGlobalEntityExceptionHandler)
				.setMessageConverters(new JsonMergePatchHttpMessageConverter(), new JsonPatchHttpMessageConverter(),
						new MappingJackson2HttpMessageConverter())
				.build();
	}

	@Nested
	class EventControllerJsonPatchMethodTest {

		@Test
		void when_partial_update_valid_event_using_json_patch_should_return_partially_updated_node()
				throws ParseException {

			Long eventId = 1L;

			String summary = "summary";
			String motive = "motive";
			String dateString = "2000-08-05";
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
			boolean isPartOfMultipleIncidents = true;
			boolean isSuccessful = true;
			boolean isSuicide = true;

			String updatedSummary = "summary updated";
			String updatedMotive = "motive updated";
			String updatedEventDateString = "2001-08-05";
			Date updatedEventDate = new SimpleDateFormat("yyyy-MM-dd").parse(updatedEventDateString);
			boolean updatedIsPartOfMultipleIncidents = false;
			boolean updatedIsSuccessful = false;
			boolean updatedIsSuicide = false;

			Long targetId = 1L;
			String target = "target";
			TargetNode targetNode = new TargetNode(targetId, target);
			TargetModel targetModel = new TargetModel(targetId, target);

			String pathToTargetLink = TARGET_BASE_PATH + "/" + targetId.intValue();
			Link targetLink = new Link(pathToTargetLink);
			targetModel.add(targetLink);

			EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(targetNode).build();

			EventNode updatedEventNode = EventNode.builder().id(eventId).date(updatedEventDate).summary(updatedSummary)
					.isPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents).isSuccessful(updatedIsSuccessful)
					.isSuicide(updatedIsSuicide).motive(updatedMotive).target(targetNode).build();

			EventModel model = EventModel.builder().id(eventId).date(updatedEventDate).summary(updatedSummary)
					.isPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents).isSuccessful(updatedIsSuccessful)
					.isSuicide(updatedIsSuicide).motive(updatedMotive).target(targetModel).build();

			String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
			Link eventLink = new Link(pathToEventLink);
			model.add(eventLink);

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.<Class<EventNode>>any())).thenReturn(updatedEventNode);
			when(eventService.save(ArgumentMatchers.any(EventNode.class))).thenReturn(updatedEventNode);
			when(modelAssembler.toModel(ArgumentMatchers.any(EventNode.class))).thenReturn(model);

			String jsonPatch = "[" + "{ \"op\": \"replace\", \"path\": \"/summary\", \"value\": \"" + updatedSummary
					+ "\" }," + "{ \"op\": \"replace\", \"path\": \"/motive\", \"value\": \"" + updatedMotive + "\" },"
					+ "{ \"op\": \"replace\", \"path\": \"/date\", \"value\": \"" + updatedEventDateString + "\" },"
					+ "{ \"op\": \"replace\", \"path\": \"/isPartOfMultipleIncidents\", \"value\": \""
					+ updatedIsPartOfMultipleIncidents + "\" },"
					+ "{ \"op\": \"replace\", \"path\": \"/isSuccessful\", \"value\": \"" + updatedIsSuccessful
					+ "\" }," + "{ \"op\": \"replace\", \"path\": \"/isSuicide\", \"value\": \"" + updatedIsSuicide
					+ "\" }" + "]";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonPatch)
									.contentType(PatchMediaType.APPLICATION_JSON_PATCH))
							.andExpect(status().isOk())
							.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
							.andExpect(jsonPath("links[0].href", is(pathToEventLink)))
							.andExpect(jsonPath("id", is(eventId.intValue())))
							.andExpect(jsonPath("summary", is(updatedSummary)))
							.andExpect(jsonPath("motive", is(updatedMotive)))
							.andExpect(jsonPath("date", is(notNullValue())))
							.andExpect(jsonPath("isSuicide", is(updatedIsSuicide)))
							.andExpect(jsonPath("isSuccessful", is(updatedIsSuccessful)))
							.andExpect(jsonPath("isPartOfMultipleIncidents", is(updatedIsPartOfMultipleIncidents)))
							.andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
							.andExpect(jsonPath("target.id", is(targetId.intValue())))
							.andExpect(jsonPath("target.target", is(target))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verify(patchHelper, times(1)).patch(any(JsonPatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.<Class<EventNode>>any()),
					() -> verifyNoMoreInteractions(patchHelper),
					() -> verify(eventService, times(1)).save(ArgumentMatchers.any(EventNode.class)),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(modelAssembler, times(1)).toModel(ArgumentMatchers.any(EventNode.class)),
					() -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@ParameterizedTest(name = "{index}: For Event Target: {0} should have violation")
		@NullAndEmptySource
		@ValueSource(strings = { " " })
		void when_partial_update_invalid_events_target_using_json_patch_should_have_errors(String invalidTarget)
				throws ParseException {

			Long eventId = 1L;

			String summary = "summary";
			String motive = "motive";
			String dateString = "2000-08-05";
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
			boolean isPartOfMultipleIncidents = true;
			boolean isSuccessful = true;
			boolean isSuicide = true;

			Long targetId = 1L;
			String target = "target";
			TargetNode targetNode = new TargetNode(targetId, target);
			TargetNode updatedTargetNode = new TargetNode(targetId, invalidTarget);

			EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(targetNode).build();

			EventNode updatedEventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(updatedTargetNode).build();

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.<Class<EventNode>>any())).thenReturn(updatedEventNode);

			String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/target/target\", \"value\": \"" + invalidTarget
					+ "\" }]";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonPatch)
									.contentType(PatchMediaType.APPLICATION_JSON_PATCH)
									.accept(MediaType.APPLICATION_JSON))
							.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
							.andExpect(jsonPath("status", is(400)))
							.andExpect(jsonPath("errors[0]", is("Target name cannot be empty"))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(patchHelper, times(1)).patch(any(JsonPatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.<Class<EventNode>>any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@Test
		void when_partial_update_valid_events_target_using_json_patch_should_return_partially_updated_node()
				throws ParseException {

			Long eventId = 1L;

			String summary = "summary";
			String motive = "motive";
			String dateString = "2000-08-05";
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
			boolean isPartOfMultipleIncidents = true;
			boolean isSuccessful = true;
			boolean isSuicide = true;

			Long targetId = 1L;
			String target = "target";
			String updatedTarget = "updated target";
			TargetNode targetNode = new TargetNode(targetId, target);
			TargetNode updatedTargetNode = new TargetNode(targetId, updatedTarget);
			TargetModel targetModel = new TargetModel(targetId, updatedTarget);

			String pathToTargetLink = TARGET_BASE_PATH + "/" + targetId.intValue();
			Link targetLink = new Link(pathToTargetLink);
			targetModel.add(targetLink);

			EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(targetNode).build();

			EventNode updatedEventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(updatedTargetNode).build();

			EventModel model = EventModel.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(targetModel).build();

			String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
			Link eventLink = new Link(pathToEventLink);
			model.add(eventLink);

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.<Class<EventNode>>any())).thenReturn(updatedEventNode);
			when(eventService.save(ArgumentMatchers.any(EventNode.class))).thenReturn(updatedEventNode);
			when(modelAssembler.toModel(ArgumentMatchers.any(EventNode.class))).thenReturn(model);

			String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/target/target\", \"value\": \"" + updatedTarget
					+ "\" }]";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonPatch)
									.contentType(PatchMediaType.APPLICATION_JSON_PATCH))
							.andExpect(status().isOk())
							.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
							.andExpect(jsonPath("links[0].href", is(pathToEventLink)))
							.andExpect(jsonPath("id", is(eventId.intValue())))
							.andExpect(jsonPath("summary", is(summary))).andExpect(jsonPath("motive", is(motive)))
							.andExpect(jsonPath("date", is(notNullValue())))
							.andExpect(jsonPath("isSuicide", is(isSuicide)))
							.andExpect(jsonPath("isSuccessful", is(isSuccessful)))
							.andExpect(jsonPath("isPartOfMultipleIncidents", is(isPartOfMultipleIncidents)))
							.andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
							.andExpect(jsonPath("target.id", is(targetId.intValue())))
							.andExpect(jsonPath("target.target", is(updatedTarget))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verify(patchHelper, times(1)).patch(any(JsonPatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.<Class<EventNode>>any()),
					() -> verifyNoMoreInteractions(patchHelper),
					() -> verify(eventService, times(1)).save(ArgumentMatchers.any(EventNode.class)),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(modelAssembler, times(1)).toModel(ArgumentMatchers.any(EventNode.class)),
					() -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@Test
		void when_partial_update_invalid_event_with_null_fields_using_json_patch_should_return_errors()
				throws ParseException {

			Long eventId = 1L;

			String summary = "summary";
			String motive = "motive";
			String dateString = "2000-08-05";
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
			boolean isPartOfMultipleIncidents = true;
			boolean isSuccessful = true;
			boolean isSuicide = true;

			String updatedSummary = null;
			String updatedMotive = null;
			Date updatedDate = null;
			Boolean updatedIsPartOfMultipleIncidents = null;
			Boolean updatedIsSuccessful = null;
			Boolean updatedIsSuicide = null;

			Long targetId = 1L;
			String target = "target";
			String updatedTarget = null;
			TargetNode targetNode = new TargetNode(targetId, target);
			TargetNode updatedTargetNode = new TargetNode(targetId, updatedTarget);

			EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(targetNode).build();

			EventNode updatedEventNode = EventNode.builder().id(eventId).date(updatedDate).summary(updatedSummary)
					.isPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents).isSuccessful(updatedIsSuccessful)
					.isSuicide(updatedIsSuicide).motive(updatedMotive).target(updatedTargetNode).build();

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.<Class<EventNode>>any())).thenReturn(updatedEventNode);

			String jsonPatch = "[" + "{ \"op\": \"replace\", \"path\": \"/summary\", \"value\": \"" + updatedSummary
					+ "\" }," + "{ \"op\": \"replace\", \"path\": \"/motive\", \"value\": \"" + updatedMotive + "\" },"
					+ "{ \"op\": \"replace\", \"path\": \"/date\", \"value\": \"" + updatedDate + "\" },"
					+ "{ \"op\": \"replace\", \"path\": \"/isPartOfMultipleIncidents\", \"value\": \""
					+ updatedIsPartOfMultipleIncidents + "\" },"
					+ "{ \"op\": \"replace\", \"path\": \"/isSuccessful\", \"value\": \"" + updatedIsSuccessful
					+ "\" }," + "{ \"op\": \"replace\", \"path\": \"/isSuicide\", \"value\": \"" + updatedIsSuicide
					+ "\" }," + "{ \"op\": \"replace\", \"path\": \"/target/target\", \"value\": \"" + updatedTarget
					+ "\" }" + "]";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonPatch)
									.contentType(PatchMediaType.APPLICATION_JSON_PATCH))
							.andExpect(jsonPath("timestamp", is(notNullValue()))).andExpect(jsonPath("status", is(400)))
							.andExpect(jsonPath("errors", hasItem("Event summary cannot be empty")))
							.andExpect(jsonPath("errors", hasItem("Event motive cannot be empty")))
							.andExpect(jsonPath("errors", hasItem("Event date cannot be null")))
							.andExpect(jsonPath("errors", hasItem(
									"Event must have information on whether it has been part of many incidents")))
							.andExpect(jsonPath("errors",
									hasItem("Event must have information about whether it was successful")))
							.andExpect(jsonPath("errors",
									hasItem("Event must have information about whether it was a suicide attack")))
							.andExpect(jsonPath("errors", hasItem("Target name cannot be empty"))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(patchHelper, times(1)).patch(any(JsonPatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.<Class<EventNode>>any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@ParameterizedTest(name = "{index}: For Event Target: {0} should have violation")
		@NullAndEmptySource
		@ValueSource(strings = { " " })
		void when_partial_update_invalid_events_target_using_json_patch_should_return_errors(String invalidTarget)
				throws ParseException {

			Long eventId = 1L;

			String summary = "summary";
			String motive = "motive";
			String dateString = "2000-08-05";
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
			boolean isPartOfMultipleIncidents = true;
			boolean isSuccessful = true;
			boolean isSuicide = true;

			Long targetId = 1L;
			String target = "target";
			TargetNode targetNode = new TargetNode(targetId, target);
			TargetNode updatedTargetNode = new TargetNode(targetId, invalidTarget);

			EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(targetNode).build();

			EventNode updatedEventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(updatedTargetNode).build();

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.<Class<EventNode>>any())).thenReturn(updatedEventNode);

			String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/target/target\", \"value\": \"" + invalidTarget
					+ "\" }]";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonPatch)
									.contentType(PatchMediaType.APPLICATION_JSON_PATCH))
							.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
							.andExpect(jsonPath("status", is(400)))
							.andExpect(jsonPath("errors[0]", is("Target name cannot be empty"))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(patchHelper, times(1)).patch(any(JsonPatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.<Class<EventNode>>any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@ParameterizedTest(name = "{index}: For Event summary: {0} should have violation")
		@NullAndEmptySource
		@ValueSource(strings = { " " })
		void when_partial_update_event_with_invalid_summary_using_json_patch_should_return_errors(String invalidSummary)
				throws ParseException {

			Long eventId = 1L;

			String summary = "summary";
			String motive = "motive";
			String dateString = "2000-08-05";
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
			boolean isPartOfMultipleIncidents = true;
			boolean isSuccessful = true;
			boolean isSuicide = true;

			Long targetId = 1L;
			String target = "target";
			String updatedTarget = "update target";
			TargetNode targetNode = new TargetNode(targetId, target);
			TargetNode updatedTargetNode = new TargetNode(targetId, updatedTarget);

			EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(targetNode).build();

			EventNode updatedEventNode = EventNode.builder().id(eventId).date(date).summary(invalidSummary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(updatedTargetNode).build();

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.<Class<EventNode>>any())).thenReturn(updatedEventNode);

			String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/summary\", \"value\": \"" + invalidSummary
					+ "\" }]";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonPatch)
									.contentType(PatchMediaType.APPLICATION_JSON_PATCH))
							.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
							.andExpect(jsonPath("status", is(400)))
							.andExpect(jsonPath("errors[0]", is("Event summary cannot be empty"))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(patchHelper, times(1)).patch(any(JsonPatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.<Class<EventNode>>any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@ParameterizedTest(name = "{index}: For Event motive: {0} should have violation")
		@NullAndEmptySource
		@ValueSource(strings = { " " })
		void when_partial_update_event_with_invalid_motive_using_json_patch_should_return_errors(String invalidMotive)
				throws ParseException {

			Long eventId = 1L;

			String summary = "summary";
			String motive = "motive";
			String dateString = "2000-08-05";
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
			boolean isPartOfMultipleIncidents = true;
			boolean isSuccessful = true;
			boolean isSuicide = true;

			Long targetId = 1L;
			String target = "target";
			String updatedTarget = "update target";
			TargetNode targetNode = new TargetNode(targetId, target);
			TargetNode updatedTargetNode = new TargetNode(targetId, updatedTarget);

			EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(targetNode).build();

			EventNode updatedEventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(invalidMotive).target(updatedTargetNode).build();

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.<Class<EventNode>>any())).thenReturn(updatedEventNode);

			String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/motive\", \"value\": \"" + invalidMotive + "\" }]";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonPatch)
									.contentType(PatchMediaType.APPLICATION_JSON_PATCH))
							.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
							.andExpect(jsonPath("status", is(400)))
							.andExpect(jsonPath("errors[0]", is("Event motive cannot be empty"))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(patchHelper, times(1)).patch(any(JsonPatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.<Class<EventNode>>any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@Test
		void when_partial_update_event_with_date_in_the_future_using_json_patch_should_return_errors()
				throws ParseException {

			Long eventId = 1L;

			String summary = "summary";
			String motive = "motive";
			String invalidDate = "2090-08-05";
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(invalidDate);
			boolean isPartOfMultipleIncidents = true;
			boolean isSuccessful = true;
			boolean isSuicide = true;

			Long targetId = 1L;
			String target = "target";
			String updatedTarget = "update target";
			TargetNode targetNode = new TargetNode(targetId, target);
			TargetNode updatedTargetNode = new TargetNode(targetId, updatedTarget);

			EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(targetNode).build();

			EventNode updatedEventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(updatedTargetNode).build();

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.<Class<EventNode>>any())).thenReturn(updatedEventNode);

			String jsonPatch = "[{ \"op\": \"replace\", \"path\": \"/date\", \"value\": \"" + invalidDate + "\" }]";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonPatch)
									.contentType(PatchMediaType.APPLICATION_JSON_PATCH))
							.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
							.andExpect(jsonPath("status", is(400)))
							.andExpect(jsonPath("errors[0]", is("Event date cannot be in the future"))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(patchHelper, times(1)).patch(any(JsonPatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.<Class<EventNode>>any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}
	}

	@Nested
	class EventControllerMergeJsonPatchMethodTest {

		@Test
		void when_partial_update_valid_event_using_merge_json_patch_should_return_partially_updated_node()
				throws ParseException {

			Long eventId = 1L;

			String summary = "summary";
			String motive = "motive";
			String dateString = "2000-08-05";
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
			boolean isPartOfMultipleIncidents = true;
			boolean isSuccessful = true;
			boolean isSuicide = true;

			String updatedSummary = "summary updated";
			String updatedMotive = "motive updated";
			String updatedEventDateString = "2001-08-05";
			Date updatedEventDate = new SimpleDateFormat("yyyy-MM-dd").parse(updatedEventDateString);
			boolean updatedIsPartOfMultipleIncidents = false;
			boolean updatedIsSuccessful = false;
			boolean updatedIsSuicide = false;

			Long targetId = 1L;
			String target = "target";
			TargetNode targetNode = new TargetNode(targetId, target);
			TargetModel targetModel = new TargetModel(targetId, target);

			String pathToTargetLink = TARGET_BASE_PATH + "/" + targetId.intValue();
			Link targetLink = new Link(pathToTargetLink);
			targetModel.add(targetLink);

			EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(targetNode).build();

			EventNode updatedEventNode = EventNode.builder().id(eventId).date(updatedEventDate).summary(updatedSummary)
					.isPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents).isSuccessful(updatedIsSuccessful)
					.isSuicide(updatedIsSuicide).motive(updatedMotive).target(targetNode).build();

			EventModel model = EventModel.builder().id(eventId).date(updatedEventDate).summary(updatedSummary)
					.isPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents).isSuccessful(updatedIsSuccessful)
					.isSuicide(updatedIsSuicide).motive(updatedMotive).target(targetModel).build();

			String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
			Link eventLink = new Link(pathToEventLink);
			model.add(eventLink);

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.<Class<EventNode>>any())).thenReturn(updatedEventNode);
			when(eventService.save(ArgumentMatchers.any(EventNode.class))).thenReturn(updatedEventNode);
			when(modelAssembler.toModel(ArgumentMatchers.any(EventNode.class))).thenReturn(model);

			String jsonMergePatch = "{\"summary\" : \"" + updatedSummary + "\", \"motive\" : \"" + updatedMotive
					+ "\", \"date\" : \"" + updatedEventDateString + "\", \"isPartOfMultipleIncidents\" : "
					+ updatedIsPartOfMultipleIncidents + ", \"isSuccessful\" : " + updatedIsSuccessful
					+ ", \"isSuicide\" : " + updatedIsSuicide + "}";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonMergePatch)
									.contentType(PatchMediaType.APPLICATION_MERGE_PATCH))
							.andExpect(status().isOk())
							.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
							.andExpect(jsonPath("links[0].href", is(pathToEventLink)))
							.andExpect(jsonPath("id", is(eventId.intValue())))
							.andExpect(jsonPath("summary", is(updatedSummary)))
							.andExpect(jsonPath("motive", is(updatedMotive)))
							.andExpect(jsonPath("date", is(notNullValue())))
							.andExpect(jsonPath("isSuicide", is(updatedIsSuicide)))
							.andExpect(jsonPath("isSuccessful", is(updatedIsSuccessful)))
							.andExpect(jsonPath("isPartOfMultipleIncidents", is(updatedIsPartOfMultipleIncidents)))
							.andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
							.andExpect(jsonPath("target.id", is(targetId.intValue())))
							.andExpect(jsonPath("target.target", is(target))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.<Class<EventNode>>any()),
					() -> verifyNoMoreInteractions(patchHelper),
					() -> verify(eventService, times(1)).save(ArgumentMatchers.any(EventNode.class)),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(modelAssembler, times(1)).toModel(ArgumentMatchers.any(EventNode.class)),
					() -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@ParameterizedTest(name = "{index}: For Event Target: {0} should have violation")
		@NullAndEmptySource
		@ValueSource(strings = { " " })
		void when_partial_update_invalid_events_target_using_merge_json_patch_should_have_errors(String invalidTarget)
				throws ParseException {

			Long eventId = 1L;

			String summary = "summary";
			String motive = "motive";
			String dateString = "2000-08-05";
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
			boolean isPartOfMultipleIncidents = true;
			boolean isSuccessful = true;
			boolean isSuicide = true;

			Long targetId = 1L;
			String target = "target";
			TargetNode targetNode = new TargetNode(targetId, target);
			TargetNode updatedTargetNode = new TargetNode(targetId, invalidTarget);

			EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(targetNode).build();

			EventNode updatedEventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(updatedTargetNode).build();

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.<Class<EventNode>>any())).thenReturn(updatedEventNode);

			String jsonMergePatch = "{\"target\" : {\"target\"  : \"" + invalidTarget + "\"} }";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonMergePatch)
									.contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
									.accept(MediaType.APPLICATION_JSON))
							.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
							.andExpect(jsonPath("status", is(400)))
							.andExpect(jsonPath("errors[0]", is("Target name cannot be empty"))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.<Class<EventNode>>any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@Test
		void when_partial_update_valid_events_target_using_merge_json_patch_should_return_partially_updated_node()
				throws ParseException {

			Long eventId = 1L;

			String summary = "summary";
			String motive = "motive";
			String dateString = "2000-08-05";
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
			boolean isPartOfMultipleIncidents = true;
			boolean isSuccessful = true;
			boolean isSuicide = true;

			Long targetId = 1L;
			String target = "target";
			String updatedTarget = "updated target";
			TargetNode targetNode = new TargetNode(targetId, target);
			TargetNode updatedTargetNode = new TargetNode(targetId, updatedTarget);
			TargetModel targetModel = new TargetModel(targetId, updatedTarget);

			String pathToTargetLink = TARGET_BASE_PATH + "/" + targetId.intValue();
			Link targetLink = new Link(pathToTargetLink);
			targetModel.add(targetLink);

			EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(targetNode).build();

			EventNode updatedEventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(updatedTargetNode).build();

			EventModel model = EventModel.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(targetModel).build();

			String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
			Link eventLink = new Link(pathToEventLink);
			model.add(eventLink);

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.<Class<EventNode>>any())).thenReturn(updatedEventNode);
			when(eventService.save(ArgumentMatchers.any(EventNode.class))).thenReturn(updatedEventNode);
			when(modelAssembler.toModel(ArgumentMatchers.any(EventNode.class))).thenReturn(model);

			String jsonMergePatch = "{ \"target\" : { \"target\" : \"" + updatedTarget + "\" } }";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonMergePatch)
									.contentType(PatchMediaType.APPLICATION_MERGE_PATCH))
							.andExpect(status().isOk())
							.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
							.andExpect(jsonPath("links[0].href", is(pathToEventLink)))
							.andExpect(jsonPath("id", is(eventId.intValue())))
							.andExpect(jsonPath("summary", is(summary))).andExpect(jsonPath("motive", is(motive)))
							.andExpect(jsonPath("date", is(notNullValue())))
							.andExpect(jsonPath("isSuicide", is(isSuicide)))
							.andExpect(jsonPath("isSuccessful", is(isSuccessful)))
							.andExpect(jsonPath("isPartOfMultipleIncidents", is(isPartOfMultipleIncidents)))
							.andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
							.andExpect(jsonPath("target.id", is(targetId.intValue())))
							.andExpect(jsonPath("target.target", is(updatedTarget))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.<Class<EventNode>>any()),
					() -> verifyNoMoreInteractions(patchHelper),
					() -> verify(eventService, times(1)).save(ArgumentMatchers.any(EventNode.class)),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(modelAssembler, times(1)).toModel(ArgumentMatchers.any(EventNode.class)),
					() -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@Test
		void when_partial_update_invalid_event_with_null_fields_using_merge_json_patch_should_return_errors()
				throws ParseException {

			Long eventId = 1L;

			String summary = "summary";
			String motive = "motive";
			String dateString = "2000-08-05";
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
			boolean isPartOfMultipleIncidents = true;
			boolean isSuccessful = true;
			boolean isSuicide = true;

			String updatedSummary = null;
			String updatedMotive = null;
			Date updatedDate = null;
			Boolean updatedIsPartOfMultipleIncidents = null;
			Boolean updatedIsSuccessful = null;
			Boolean updatedIsSuicide = null;

			Long targetId = 1L;
			String target = "target";
			String updatedTarget = null;
			TargetNode targetNode = new TargetNode(targetId, target);
			TargetNode updatedTargetNode = new TargetNode(targetId, updatedTarget);

			EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(targetNode).build();

			EventNode updatedEventNode = EventNode.builder().id(eventId).date(updatedDate).summary(updatedSummary)
					.isPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents).isSuccessful(updatedIsSuccessful)
					.isSuicide(updatedIsSuicide).motive(updatedMotive).target(updatedTargetNode).build();

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.<Class<EventNode>>any())).thenReturn(updatedEventNode);

			String jsonMergePatch = "{\"summary\" : \"" + updatedSummary + "\", \"motive\" : \"" + updatedMotive
					+ "\", \"date\" : \"" + updatedDate + "\", \"isPartOfMultipleIncidents\" : "
					+ updatedIsPartOfMultipleIncidents + ", \"isSuccessful\" : " + updatedIsSuccessful
					+ ", \"isSuicide\" : " + updatedIsSuicide + ", \"target\" : { \"target\" : \"" + updatedTarget
					+ "\" } }";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonMergePatch)
									.contentType(PatchMediaType.APPLICATION_MERGE_PATCH))
							.andExpect(jsonPath("timestamp", is(notNullValue()))).andExpect(jsonPath("status", is(400)))
							.andExpect(jsonPath("errors", hasItem("Event summary cannot be empty")))
							.andExpect(jsonPath("errors", hasItem("Event motive cannot be empty")))
							.andExpect(jsonPath("errors", hasItem("Event date cannot be null")))
							.andExpect(jsonPath("errors", hasItem(
									"Event must have information on whether it has been part of many incidents")))
							.andExpect(jsonPath("errors",
									hasItem("Event must have information about whether it was successful")))
							.andExpect(jsonPath("errors",
									hasItem("Event must have information about whether it was a suicide attack")))
							.andExpect(jsonPath("errors", hasItem("Target name cannot be empty"))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.<Class<EventNode>>any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@ParameterizedTest(name = "{index}: For Event Target: {0} should have violation")
		@NullAndEmptySource
		@ValueSource(strings = { " " })
		void when_partial_update_invalid_events_target_using_merge_json_patch_should_return_errors(String invalidTarget)
				throws ParseException {

			Long eventId = 1L;

			String summary = "summary";
			String motive = "motive";
			String dateString = "2000-08-05";
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
			boolean isPartOfMultipleIncidents = true;
			boolean isSuccessful = true;
			boolean isSuicide = true;

			Long targetId = 1L;
			String target = "target";
			TargetNode targetNode = new TargetNode(targetId, target);
			TargetNode updatedTargetNode = new TargetNode(targetId, invalidTarget);

			EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(targetNode).build();

			EventNode updatedEventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(updatedTargetNode).build();

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.<Class<EventNode>>any())).thenReturn(updatedEventNode);

			String jsonMergePatch = "{ \"target\" : { \"target\" : \"" + invalidTarget + "\" } }";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonMergePatch)
									.contentType(PatchMediaType.APPLICATION_MERGE_PATCH))
							.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
							.andExpect(jsonPath("status", is(400)))
							.andExpect(jsonPath("errors[0]", is("Target name cannot be empty"))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.<Class<EventNode>>any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@ParameterizedTest(name = "{index}: For Event summary: {0} should have violation")
		@NullAndEmptySource
		@ValueSource(strings = { " " })
		void when_partial_update_event_with_invalid_summary_using_merge_json_patch_should_return_errors(
				String invalidSummary) throws ParseException {

			Long eventId = 1L;

			String summary = "summary";
			String motive = "motive";
			String dateString = "2000-08-05";
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
			boolean isPartOfMultipleIncidents = true;
			boolean isSuccessful = true;
			boolean isSuicide = true;

			Long targetId = 1L;
			String target = "target";
			String updatedTarget = "update target";
			TargetNode targetNode = new TargetNode(targetId, target);
			TargetNode updatedTargetNode = new TargetNode(targetId, updatedTarget);

			EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(targetNode).build();

			EventNode updatedEventNode = EventNode.builder().id(eventId).date(date).summary(invalidSummary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(updatedTargetNode).build();

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.<Class<EventNode>>any())).thenReturn(updatedEventNode);

			String jsonMergePatch = "{ \"summary\" : \"" + invalidSummary + "\" }";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonMergePatch)
									.contentType(PatchMediaType.APPLICATION_MERGE_PATCH))
							.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
							.andExpect(jsonPath("status", is(400)))
							.andExpect(jsonPath("errors[0]", is("Event summary cannot be empty"))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.<Class<EventNode>>any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@ParameterizedTest(name = "{index}: For Event motive: {0} should have violation")
		@NullAndEmptySource
		@ValueSource(strings = { " " })
		void when_partial_update_event_with_invalid_motive_using_merge_json_patch_should_return_errors(
				String invalidMotive) throws ParseException {

			Long eventId = 1L;

			String summary = "summary";
			String motive = "motive";
			String dateString = "2000-08-05";
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
			boolean isPartOfMultipleIncidents = true;
			boolean isSuccessful = true;
			boolean isSuicide = true;

			Long targetId = 1L;
			String target = "target";
			String updatedTarget = "update target";
			TargetNode targetNode = new TargetNode(targetId, target);
			TargetNode updatedTargetNode = new TargetNode(targetId, updatedTarget);

			EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(targetNode).build();

			EventNode updatedEventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(invalidMotive).target(updatedTargetNode).build();

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.<Class<EventNode>>any())).thenReturn(updatedEventNode);

			String jsonMergePatch = "{ \"motive\" : \"" + invalidMotive + "\" }";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonMergePatch)
									.contentType(PatchMediaType.APPLICATION_MERGE_PATCH))
							.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
							.andExpect(jsonPath("status", is(400)))
							.andExpect(jsonPath("errors[0]", is("Event motive cannot be empty"))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.<Class<EventNode>>any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@Test
		void when_partial_update_event_with_date_in_the_future_using_merge_json_patch_should_return_errors()
				throws ParseException {

			Long eventId = 1L;

			String summary = "summary";
			String motive = "motive";
			String invalidDate = "2090-08-05";
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(invalidDate);
			boolean isPartOfMultipleIncidents = true;
			boolean isSuccessful = true;
			boolean isSuicide = true;

			Long targetId = 1L;
			String target = "target";
			String updatedTarget = "update target";
			TargetNode targetNode = new TargetNode(targetId, target);
			TargetNode updatedTargetNode = new TargetNode(targetId, updatedTarget);

			EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(targetNode).build();

			EventNode updatedEventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(updatedTargetNode).build();

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.<Class<EventNode>>any())).thenReturn(updatedEventNode);

			String jsonMergePatch = "{ \"date\" : \"" + invalidDate + "\" }";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonMergePatch)
									.contentType(PatchMediaType.APPLICATION_MERGE_PATCH))
							.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
							.andExpect(jsonPath("status", is(400)))
							.andExpect(jsonPath("errors[0]", is("Event date cannot be in the future"))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.<Class<EventNode>>any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}
	}
}