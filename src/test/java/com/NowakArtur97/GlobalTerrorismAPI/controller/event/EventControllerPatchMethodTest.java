package com.NowakArtur97.GlobalTerrorismAPI.controller.event;

import com.NowakArtur97.GlobalTerrorismAPI.advice.RestResponseGlobalEntityExceptionHandler;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.EventModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestController;
import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.httpMessageConverter.JsonMergePatchHttpMessageConverter;
import com.NowakArtur97.GlobalTerrorismAPI.httpMessageConverter.JsonPatchHttpMessageConverter;
import com.NowakArtur97.GlobalTerrorismAPI.mediaType.PatchMediaType;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.patch.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.violation.ViolationHelper;
import com.ibm.icu.util.Calendar;
import org.junit.jupiter.api.*;
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

import javax.json.JsonMergePatch;
import javax.json.JsonPatch;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventController_Tests")
class EventControllerPatchMethodTest {

	private final String EVENT_BASE_PATH = "http://localhost:8080/api/events";
	private final String TARGET_BASE_PATH = "http://localhost:8080/api/targets";

	private MockMvc mockMvc;

	private GenericRestController<EventModel, EventDTO> eventController;

	private RestResponseGlobalEntityExceptionHandler restResponseGlobalEntityExceptionHandler;

	@Mock
	private GenericService<EventNode, EventDTO> eventService;

	@Mock
	private EventModelAssembler modelAssembler;

	@Mock
	private PagedResourcesAssembler<EventNode> pagedResourcesAssembler;

	@Mock
	private PatchHelper patchHelper;

	@Autowired
	private ViolationHelper<EventNode, EventDTO> violationHelper;

	private static TargetBuilder targetBuilder;
	private static EventBuilder eventBuilder;

	@BeforeEach
	private void setUp() {

		eventController = new EventController(eventService, modelAssembler, pagedResourcesAssembler, patchHelper,
				violationHelper);

		restResponseGlobalEntityExceptionHandler = new RestResponseGlobalEntityExceptionHandler();

		mockMvc = MockMvcBuilders.standaloneSetup(eventController, restResponseGlobalEntityExceptionHandler)
				.setMessageConverters(new JsonMergePatchHttpMessageConverter(), new JsonPatchHttpMessageConverter(),
						new MappingJackson2HttpMessageConverter())
				.build();

		targetBuilder = new TargetBuilder();
		eventBuilder = new EventBuilder();
	}

	@Nested
	class EventControllerJsonPatchMethodTest {

		@Test
		void when_partial_update_valid_event_using_json_patch_should_return_partially_updated_node()
				throws ParseException {

			Long eventId = 1L;

			String updatedSummary = "summary updated";
			String updatedMotive = "motive updated";
			String updatedEventDateString = "2001-08-05";
			Date updatedEventDate = new SimpleDateFormat("yyyy-MM-dd").parse(updatedEventDateString);
			boolean updatedIsPartOfMultipleIncidents = false;
			boolean updatedIsSuccessful = false;
			boolean updatedIsSuicide = false;

			TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			TargetModel targetModel = (TargetModel) targetBuilder.build(ObjectType.MODEL);
			String pathToTargetLink = TARGET_BASE_PATH + "/" + targetModel.getId().intValue();
			targetModel.add(new Link(pathToTargetLink));

			EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

			EventNode updatedEventNode = (EventNode) eventBuilder.withDate(updatedEventDate).withSummary(updatedSummary)
					.withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents)
					.withIsSuccessful(updatedIsSuccessful).withIsSuicide(updatedIsSuicide).withMotive(updatedMotive)
					.withTarget(targetNode).build(ObjectType.NODE);

			EventModel updatedEventModel = (EventModel) eventBuilder.withDate(updatedEventDate)
					.withSummary(updatedSummary).withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents)
					.withIsSuccessful(updatedIsSuccessful).withIsSuicide(updatedIsSuicide).withMotive(updatedMotive)
					.withTarget(targetModel).build(ObjectType.MODEL);
			String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
			updatedEventModel.add(new Link(pathToEventLink));

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.any())).thenReturn(updatedEventNode);
			when(eventService.save(ArgumentMatchers.any(EventNode.class))).thenReturn(updatedEventNode);
			when(modelAssembler.toModel(ArgumentMatchers.any(EventNode.class))).thenReturn(updatedEventModel);

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
							.andExpect(jsonPath("target.id", is(targetModel.getId().intValue())))
							.andExpect(jsonPath("target.target", is(targetModel.getTarget()))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verify(patchHelper, times(1)).patch(any(JsonPatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.any()),
					() -> verifyNoMoreInteractions(patchHelper),
					() -> verify(eventService, times(1)).save(ArgumentMatchers.any(EventNode.class)),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(modelAssembler, times(1)).toModel(ArgumentMatchers.any(EventNode.class)),
					() -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@Test
		void when_partial_update_valid_events_target_using_json_patch_should_return_partially_updated_node() {

			Long eventId = 1L;

			String updatedTarget = "updated target";
			TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			TargetModel targetModel = (TargetModel) targetBuilder.build(ObjectType.MODEL);
			TargetNode updatedTargetNode = (TargetNode) targetBuilder.withTarget(updatedTarget).build(ObjectType.NODE);

			TargetModel updatedTargetModel = (TargetModel) targetBuilder.withTarget(updatedTarget)
					.build(ObjectType.MODEL);
			String pathToTargetLink = TARGET_BASE_PATH + "/" + targetModel.getId().intValue();
			updatedTargetModel.add(new Link(pathToTargetLink));

			EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
			EventNode updatedEventNode = (EventNode) eventBuilder.withTarget(updatedTargetNode).build(ObjectType.NODE);
			EventModel updatedEventModel = (EventModel) eventBuilder.withTarget(updatedTargetModel)
					.build(ObjectType.MODEL);

			String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
			Link eventLink = new Link(pathToEventLink);
			updatedEventModel.add(eventLink);

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.any())).thenReturn(updatedEventNode);
			when(eventService.save(ArgumentMatchers.any(EventNode.class))).thenReturn(updatedEventNode);
			when(modelAssembler.toModel(ArgumentMatchers.any(EventNode.class))).thenReturn(updatedEventModel);

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
							.andExpect(jsonPath("summary", is(updatedEventModel.getSummary())))
							.andExpect(jsonPath("motive", is(updatedEventModel.getMotive())))
							.andExpect(jsonPath("date",
									is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
											.format(updatedEventModel.getDate().toInstant()
													.atZone(ZoneId.systemDefault()).toLocalDate()))))
							.andExpect(jsonPath("isSuicide", is(updatedEventModel.getIsSuicide())))
							.andExpect(jsonPath("isSuccessful", is(updatedEventModel.getIsSuccessful())))
							.andExpect(jsonPath("isPartOfMultipleIncidents",
									is(updatedEventModel.getIsPartOfMultipleIncidents())))
							.andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
							.andExpect(jsonPath("target.id", is(updatedTargetModel.getId().intValue())))
							.andExpect(jsonPath("target.target", is(updatedTargetModel.getTarget()))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verify(patchHelper, times(1)).patch(any(JsonPatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.any()),
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
		void when_partial_update_invalid_events_target_using_json_patch_should_have_errors(String invalidTarget) {

			Long eventId = 1L;

			TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

			TargetNode updatedTargetNode = (TargetNode) targetBuilder.withTarget(invalidTarget).build(ObjectType.NODE);
			EventNode updatedEventNode = (EventNode) eventBuilder.withTarget(updatedTargetNode).build(ObjectType.NODE);

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.any())).thenReturn(updatedEventNode);

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
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@Test
		void when_partial_update_invalid_event_with_null_fields_using_json_patch_should_return_errors() {

			Long eventId = 1L;

			TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

			TargetNode updatedTargetNode = (TargetNode) targetBuilder.withTarget(null).build(ObjectType.NODE);
			EventNode updatedEventNode = (EventNode) eventBuilder.withId(null).withSummary(null).withMotive(null)
					.withDate(null).withIsPartOfMultipleIncidents(null).withIsSuccessful(null).withIsSuicide(null)
					.withTarget(updatedTargetNode).build(ObjectType.NODE);

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.any())).thenReturn(updatedEventNode);

			String jsonPatch = "[" + "{ \"op\": \"replace\", \"path\": \"/summary\", \"value\": \"" + null + "\" },"
					+ "{ \"op\": \"replace\", \"path\": \"/motive\", \"value\": \"" + null + "\" },"
					+ "{ \"op\": \"replace\", \"path\": \"/date\", \"value\": \"" + null + "\" },"
					+ "{ \"op\": \"replace\", \"path\": \"/isPartOfMultipleIncidents\", \"value\": \"" + null + "\" },"
					+ "{ \"op\": \"replace\", \"path\": \"/isSuccessful\", \"value\": \"" + null + "\" },"
					+ "{ \"op\": \"replace\", \"path\": \"/isSuicide\", \"value\": \"" + null + "\" },"
					+ "{ \"op\": \"replace\", \"path\": \"/target/target\", \"value\": \"" + null + "\" }" + "]";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonPatch)
									.contentType(PatchMediaType.APPLICATION_JSON_PATCH))
							.andExpect(status().isBadRequest())
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
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@ParameterizedTest(name = "{index}: For Event Target: {0} should have violation")
		@NullAndEmptySource
		@ValueSource(strings = { " " })
		void when_partial_update_invalid_events_target_using_json_patch_should_return_errors(String invalidTarget) {

			Long eventId = 1L;

			TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
			TargetNode updatedTargetNode = (TargetNode) targetBuilder.withTarget(invalidTarget).build(ObjectType.NODE);
			EventNode updatedEventNode = (EventNode) eventBuilder.withTarget(updatedTargetNode).build(ObjectType.NODE);

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.any())).thenReturn(updatedEventNode);

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
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@ParameterizedTest(name = "{index}: For Event summary: {0} should have violation")
		@NullAndEmptySource
		@ValueSource(strings = { " " })
		void when_partial_update_event_with_invalid_summary_using_json_patch_should_return_errors(
				String invalidSummary) {

			Long eventId = 1L;

			TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

			TargetNode updatedTargetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			EventNode updatedEventNode = (EventNode) eventBuilder.withSummary(invalidSummary)
					.withTarget(updatedTargetNode).build(ObjectType.NODE);

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.any())).thenReturn(updatedEventNode);

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
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@ParameterizedTest(name = "{index}: For Event motive: {0} should have violation")
		@NullAndEmptySource
		@ValueSource(strings = { " " })
		void when_partial_update_event_with_invalid_motive_using_json_patch_should_return_errors(String invalidMotive) {

			Long eventId = 1L;

			TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

			TargetNode updatedTargetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			EventNode updatedEventNode = (EventNode) eventBuilder.withMotive(invalidMotive)
					.withTarget(updatedTargetNode).build(ObjectType.NODE);

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.any())).thenReturn(updatedEventNode);

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
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@Test
		void when_partial_update_event_with_date_in_the_future_using_json_patch_should_return_errors() {

			Long eventId = 1L;

			Calendar calendar = Calendar.getInstance();
			calendar.set(2090, 1, 1);
			Date invalidDate = calendar.getTime();

			TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

			TargetNode updatedTargetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			EventNode updatedEventNode = (EventNode) eventBuilder.withDate(invalidDate).withTarget(updatedTargetNode)
					.build(ObjectType.NODE);

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.patch(any(JsonPatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.any())).thenReturn(updatedEventNode);

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
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}
	}

	@Nested
	class EventControllerMergeJsonPatchMethodTest {

		@Test
		void when_partial_update_valid_event_using_json_merge_patch_should_return_partially_updated_node()
				throws ParseException {

			Long eventId = 1L;

			String updatedSummary = "summary updated";
			String updatedMotive = "motive updated";
			String updatedEventDateString = "2001-08-05";
			Date updatedEventDate = new SimpleDateFormat("yyyy-MM-dd").parse(updatedEventDateString);
			boolean updatedIsPartOfMultipleIncidents = false;
			boolean updatedIsSuccessful = false;
			boolean updatedIsSuicide = false;

			TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			TargetModel targetModel = (TargetModel) targetBuilder.build(ObjectType.MODEL);
			String pathToTargetLink = TARGET_BASE_PATH + "/" + targetModel.getId().intValue();
			targetModel.add(new Link(pathToTargetLink));

			EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

			EventNode updatedEventNode = (EventNode) eventBuilder.withDate(updatedEventDate).withSummary(updatedSummary)
					.withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents)
					.withIsSuccessful(updatedIsSuccessful).withIsSuicide(updatedIsSuicide).withMotive(updatedMotive)
					.withTarget(targetNode).build(ObjectType.NODE);

			EventModel updatedEventModel = (EventModel) eventBuilder.withDate(updatedEventDate)
					.withSummary(updatedSummary).withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents)
					.withIsSuccessful(updatedIsSuccessful).withIsSuicide(updatedIsSuicide).withMotive(updatedMotive)
					.withTarget(targetModel).build(ObjectType.MODEL);
			String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
			updatedEventModel.add(new Link(pathToEventLink));
			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.any())).thenReturn(updatedEventNode);
			when(eventService.save(ArgumentMatchers.any(EventNode.class))).thenReturn(updatedEventNode);
			when(modelAssembler.toModel(ArgumentMatchers.any(EventNode.class))).thenReturn(updatedEventModel);

			String jsonMergePatch = "{\"summary\" : \"" + updatedSummary + "\", \"motive\" : \"" + updatedMotive
					+ "\", \"date\" : \"" + updatedEventDateString + "\", \"isPartOfMultipleIncidents\" : "
					+ updatedIsPartOfMultipleIncidents + ", \"isSuccessful\" : " + updatedIsSuccessful
					+ ", \"isSuicide\" : " + updatedIsSuicide + "}";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonMergePatch)
									.contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
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
							.andExpect(jsonPath("target.id", is(targetModel.getId().intValue())))
							.andExpect(jsonPath("target.target", is(targetModel.getTarget()))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.any()),
					() -> verifyNoMoreInteractions(patchHelper),
					() -> verify(eventService, times(1)).save(ArgumentMatchers.any(EventNode.class)),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(modelAssembler, times(1)).toModel(ArgumentMatchers.any(EventNode.class)),
					() -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@Test
		void when_partial_update_valid_events_target_using_json_merge_patch_should_return_partially_updated_node() {

			Long eventId = 1L;

			String updatedTarget = "updated target";
			TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			TargetModel targetModel = (TargetModel) targetBuilder.build(ObjectType.MODEL);
			TargetNode updatedTargetNode = (TargetNode) targetBuilder.withTarget(updatedTarget).build(ObjectType.NODE);

			TargetModel updatedTargetModel = (TargetModel) targetBuilder.withTarget(updatedTarget)
					.build(ObjectType.MODEL);
			String pathToTargetLink = TARGET_BASE_PATH + "/" + targetModel.getId().intValue();
			updatedTargetModel.add(new Link(pathToTargetLink));

			EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
			EventNode updatedEventNode = (EventNode) eventBuilder.withTarget(updatedTargetNode).build(ObjectType.NODE);
			EventModel updatedEventModel = (EventModel) eventBuilder.withTarget(updatedTargetModel)
					.build(ObjectType.MODEL);

			String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
			Link eventLink = new Link(pathToEventLink);
			updatedEventModel.add(eventLink);

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.any())).thenReturn(updatedEventNode);
			when(eventService.save(ArgumentMatchers.any(EventNode.class))).thenReturn(updatedEventNode);
			when(modelAssembler.toModel(ArgumentMatchers.any(EventNode.class))).thenReturn(updatedEventModel);

			String jsonMergePatch = "{ \"target\" : { \"target\" : \"" + updatedTarget + "\" } }";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonMergePatch)
									.contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
							.andExpect(status().isOk())
							.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
							.andExpect(jsonPath("links[0].href", is(pathToEventLink)))
							.andExpect(jsonPath("id", is(eventId.intValue())))
							.andExpect(jsonPath("summary", is(updatedEventModel.getSummary())))
							.andExpect(jsonPath("motive", is(updatedEventModel.getMotive())))
							.andExpect(jsonPath("date",
									is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
											.format(updatedEventModel.getDate().toInstant()
													.atZone(ZoneId.systemDefault()).toLocalDate()))))
							.andExpect(jsonPath("isSuicide", is(updatedEventModel.getIsSuicide())))
							.andExpect(jsonPath("isSuccessful", is(updatedEventModel.getIsSuccessful())))
							.andExpect(jsonPath("isPartOfMultipleIncidents",
									is(updatedEventModel.getIsPartOfMultipleIncidents())))
							.andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
							.andExpect(jsonPath("target.id", is(updatedTargetModel.getId().intValue())))
							.andExpect(jsonPath("target.target", is(updatedTargetModel.getTarget()))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.any()),
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
		void when_partial_update_invalid_events_target_using_json_merge_patch_should_have_errors(String invalidTarget) {

			Long eventId = 1L;

			TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

			TargetNode updatedTargetNode = (TargetNode) targetBuilder.withTarget(invalidTarget).build(ObjectType.NODE);
			EventNode updatedEventNode = (EventNode) eventBuilder.withTarget(updatedTargetNode).build(ObjectType.NODE);

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.any())).thenReturn(updatedEventNode);

			String jsonMergePatch = "{\"target\" : {\"target\"  : \"" + invalidTarget + "\"} }";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonMergePatch)
									.contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH)
									.accept(MediaType.APPLICATION_JSON))
							.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
							.andExpect(jsonPath("status", is(400)))
							.andExpect(jsonPath("errors[0]", is("Target name cannot be empty"))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@Test
		void when_partial_update_invalid_event_with_null_fields_using_json_merge_patch_should_return_errors() {

			Long eventId = 1L;

			TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

			TargetNode updatedTargetNode = (TargetNode) targetBuilder.withTarget(null).build(ObjectType.NODE);
			EventNode updatedEventNode = (EventNode) eventBuilder.withId(null).withSummary(null).withMotive(null)
					.withDate(null).withIsPartOfMultipleIncidents(null).withIsSuccessful(null).withIsSuicide(null)
					.withTarget(updatedTargetNode).build(ObjectType.NODE);

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.any())).thenReturn(updatedEventNode);

			String jsonMergePatch = "{\"summary\" : \"" + null + "\", \"motive\" : \"" + null + "\", \"date\" : \""
					+ null + "\", \"isPartOfMultipleIncidents\" : " + null + ", \"isSuccessful\" : " + null
					+ ", \"isSuicide\" : " + null + ", \"target\" : { \"target\" : \"" + null + "\" } }";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonMergePatch)
									.contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
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
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@ParameterizedTest(name = "{index}: For Event Target: {0} should have violation")
		@NullAndEmptySource
		@ValueSource(strings = { " " })
		void when_partial_update_invalid_events_target_using_json_merge_patch_should_return_errors(
				String invalidTarget) {

			Long eventId = 1L;

			TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
			TargetNode updatedTargetNode = (TargetNode) targetBuilder.withTarget(invalidTarget).build(ObjectType.NODE);
			EventNode updatedEventNode = (EventNode) eventBuilder.withTarget(updatedTargetNode).build(ObjectType.NODE);

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.any())).thenReturn(updatedEventNode);

			String jsonMergePatch = "{ \"target\" : { \"target\" : \"" + invalidTarget + "\" } }";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonMergePatch)
									.contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
							.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
							.andExpect(jsonPath("status", is(400)))
							.andExpect(jsonPath("errors[0]", is("Target name cannot be empty"))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@ParameterizedTest(name = "{index}: For Event summary: {0} should have violation")
		@NullAndEmptySource
		@ValueSource(strings = { " " })
		void when_partial_update_event_with_invalid_summary_using_json_merge_patch_should_return_errors(
				String invalidSummary) {

			Long eventId = 1L;

			TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

			TargetNode updatedTargetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			EventNode updatedEventNode = (EventNode) eventBuilder.withSummary(invalidSummary)
					.withTarget(updatedTargetNode).build(ObjectType.NODE);

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.any())).thenReturn(updatedEventNode);

			String jsonMergePatch = "{ \"summary\" : \"" + invalidSummary + "\" }";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonMergePatch)
									.contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
							.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
							.andExpect(jsonPath("status", is(400)))
							.andExpect(jsonPath("errors[0]", is("Event summary cannot be empty"))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@ParameterizedTest(name = "{index}: For Event motive: {0} should have violation")
		@NullAndEmptySource
		@ValueSource(strings = { " " })
		void when_partial_update_event_with_invalid_motive_using_json_merge_patch_should_return_errors(
				String invalidMotive) {

			Long eventId = 1L;

			TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

			TargetNode updatedTargetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			EventNode updatedEventNode = (EventNode) eventBuilder.withMotive(invalidMotive)
					.withTarget(updatedTargetNode).build(ObjectType.NODE);

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.any())).thenReturn(updatedEventNode);

			String jsonMergePatch = "{ \"motive\" : \"" + invalidMotive + "\" }";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonMergePatch)
									.contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
							.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
							.andExpect(jsonPath("status", is(400)))
							.andExpect(jsonPath("errors[0]", is("Event motive cannot be empty"))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@Test
		void when_partial_update_event_with_date_in_the_future_using_json_merge_patch_should_return_errors() {

			Long eventId = 1L;

			Calendar calendar = Calendar.getInstance();
			calendar.set(2090, 1, 1);
			Date invalidDate = calendar.getTime();

			TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

			TargetNode updatedTargetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			EventNode updatedEventNode = (EventNode) eventBuilder.withDate(invalidDate).withTarget(updatedTargetNode)
					.build(ObjectType.NODE);

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id2}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.mergePatch(any(JsonMergePatch.class), ArgumentMatchers.any(EventNode.class),
					ArgumentMatchers.any())).thenReturn(updatedEventNode);

			String jsonMergePatch = "{ \"date\" : \"" + invalidDate + "\" }";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonMergePatch)
									.contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
							.andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
							.andExpect(jsonPath("status", is(400)))
							.andExpect(jsonPath("errors[0]", is("Event date cannot be in the future"))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class),
							ArgumentMatchers.any(EventNode.class), ArgumentMatchers.any()),
					() -> verifyNoMoreInteractions(patchHelper), () -> verifyNoMoreInteractions(modelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}
	}
}