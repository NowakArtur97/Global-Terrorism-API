package com.NowakArtur97.GlobalTerrorismAPI.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.NowakArtur97.GlobalTerrorismAPI.advice.EventControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.advice.RestResponseGlobalEntityExceptionHandler;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.EventModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
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
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventController_Tests")
@DisabledOnOs(OS.LINUX)
class EventControllerTest {

	private final String EVENT_BASE_PATH = "http://localhost:8080/api/events";
	private final String TARGET_BASE_PATH = "http://localhost:8080/api/targets";

	private MockMvc mockMvc;

	private EventController eventController;

	private RestResponseGlobalEntityExceptionHandler restResponseGlobalEntityExceptionHandler;

	@Mock
	private EventService eventService;

	@Mock
	private EventModelAssembler eventModelAssembler;

	@Mock
	private PagedResourcesAssembler<EventNode> pagedResourcesAssembler;

	@Mock
	private PatchHelper patchHelper;

	@Autowired
	private ViolationHelper violationHelper;

	@BeforeEach
	private void setUp() {

		eventController = new EventController(eventService, eventModelAssembler, pagedResourcesAssembler, patchHelper,
				violationHelper);

		restResponseGlobalEntityExceptionHandler = new RestResponseGlobalEntityExceptionHandler();

		mockMvc = MockMvcBuilders.standaloneSetup(eventController, restResponseGlobalEntityExceptionHandler)
				.setControllerAdvice(new EventControllerAdvice())
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.setMessageConverters(new JsonMergePatchHttpMessageConverter(), new JsonPatchHttpMessageConverter(),
						new MappingJackson2HttpMessageConverter())
				.build();
	}

	@Nested
	@Tag("PatchEventRequest_Tests")
	class PatchEventRequestTest {

		@Test
		void when_partial_update_valid_event_using_json_patch_should_return_partially_updated_node()
				throws ParseException {

			Long eventId = 1L;

			String eventSummary = "summary";
			String eventMotive = "motive";
			String eventDateString = "2000-08-05";
			Date eventDate = new SimpleDateFormat("yyyy-MM-dd").parse(eventDateString);
			boolean isEventPartOfMultipleIncidents = true;
			boolean isEventSuccessful = true;
			boolean isEventSuicide = true;

			String updatedEventSummary = "summary updated";
			String updatedEventMotive = "motive updated";
			String updatedEventDateString = "2001-08-05";
			Date updatedEventDate = new SimpleDateFormat("yyyy-MM-dd").parse(updatedEventDateString);
			boolean updatedIsEventPartOfMultipleIncidents = false;
			boolean updatedIsEventSuccessful = false;
			boolean updatedIsEventSuicide = false;

			Long targetId = 1L;
			String target = "target";
			TargetNode targetNode = new TargetNode(targetId, target);
			TargetModel targetModel = new TargetModel(targetId, target);

			String pathToTargetLink = TARGET_BASE_PATH + "/" + targetId.intValue();
			Link targetLink = new Link(pathToTargetLink);
			targetModel.add(targetLink);

			EventNode eventNode = EventNode.builder().id(eventId).date(eventDate).summary(eventSummary)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents).isSuccessful(isEventSuccessful)
					.isSuicide(isEventSuicide).motive(eventMotive).target(targetNode).build();

			EventNode updatedEventNode = EventNode.builder().id(eventId).date(updatedEventDate)
					.summary(updatedEventSummary).isPartOfMultipleIncidents(updatedIsEventPartOfMultipleIncidents)
					.isSuccessful(updatedIsEventSuccessful).isSuicide(updatedIsEventSuicide).motive(updatedEventMotive)
					.target(targetNode).build();

			EventModel eventModel = EventModel.builder().id(eventId).date(updatedEventDate).summary(updatedEventSummary)
					.isPartOfMultipleIncidents(updatedIsEventPartOfMultipleIncidents)
					.isSuccessful(updatedIsEventSuccessful).isSuicide(updatedIsEventSuicide).motive(updatedEventMotive)
					.target(targetModel).build();

			String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
			Link eventLink = new Link(pathToEventLink);
			eventModel.add(eventLink);

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.patch(any(JsonPatch.class), eq(eventNode), ArgumentMatchers.<Class<EventNode>>any()))
					.thenReturn(updatedEventNode);
			doNothing().when(violationHelper).violate(updatedEventNode, EventDTO.class);
			when(eventService.save(updatedEventNode)).thenReturn(updatedEventNode);
			when(eventModelAssembler.toModel(updatedEventNode)).thenReturn(eventModel);

			String jsonPatch = "[" + "{ \"op\": \"replace\", \"path\": \"/summary\", \"value\": \""
					+ updatedEventSummary + "\" }," + "{ \"op\": \"replace\", \"path\": \"/motive\", \"value\": \""
					+ updatedEventMotive + "\" }," + "{ \"op\": \"replace\", \"path\": \"/date\", \"value\": \""
					+ updatedEventDateString + "\" },"
					+ "{ \"op\": \"replace\", \"path\": \"/partOfMultipleIncidents\", \"value\": \""
					+ updatedIsEventPartOfMultipleIncidents + "\" },"
					+ "{ \"op\": \"replace\", \"path\": \"/successful\", \"value\": \"" + updatedIsEventSuccessful
					+ "\" }," + "{ \"op\": \"replace\", \"path\": \"/suicide\", \"value\": \"" + updatedIsEventSuicide
					+ "\" }" + "]";

			assertAll(
					() -> mockMvc
							.perform(patch(linkWithParameter, eventId).content(jsonPatch)
									.contentType(PatchMediaType.APPLICATION_JSON_PATCH))
							.andExpect(status().isOk())
							.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
							.andExpect(jsonPath("links[0].href", is(pathToEventLink)))
							.andExpect(jsonPath("id", is(eventId.intValue())))
							.andExpect(jsonPath("summary", is(updatedEventSummary)))
							.andExpect(jsonPath("motive", is(updatedEventMotive)))
							.andExpect(jsonPath("date", is(notNullValue())))
							.andExpect(jsonPath("suicide", is(updatedIsEventSuicide)))
							.andExpect(jsonPath("successful", is(updatedIsEventSuccessful)))
							.andExpect(jsonPath("partOfMultipleIncidents", is(updatedIsEventPartOfMultipleIncidents)))
							.andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
							.andExpect(jsonPath("target.id", is(targetId.intValue())))
							.andExpect(jsonPath("target.target", is(target))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verify(patchHelper, times(1)).patch(any(JsonPatch.class), eq(eventNode),
							ArgumentMatchers.<Class<EventNode>>any()),
					() -> verifyNoMoreInteractions(patchHelper),
					() -> verify(violationHelper, times(1)).violate(updatedEventNode, EventDTO.class),
					() -> verifyNoMoreInteractions(violationHelper),
					() -> verify(eventService, times(1)).save(updatedEventNode),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(eventModelAssembler, times(1)).toModel(updatedEventNode),
					() -> verifyNoMoreInteractions(eventModelAssembler));
		}

		@ParameterizedTest(name = "{index}: For Event Target: {0} should have violation")
		@NullAndEmptySource
		@ValueSource(strings = { " " })
		void when_partial_update_invalid_events_target_using_json_patch_should_have_errors(String invalidTarget)
				throws ParseException {

			Long eventId = 1L;

			String eventSummary = "summary";
			String eventMotive = "motive";
			String eventDateString = "2000-08-05";
			Date eventDate = new SimpleDateFormat("yyyy-MM-dd").parse(eventDateString);
			boolean isEventPartOfMultipleIncidents = true;
			boolean isEventSuccessful = true;
			boolean isEventSuicide = true;

			Long targetId = 1L;
			String target = "target";
			TargetNode targetNode = new TargetNode(targetId, target);
			TargetNode updatedTargetNode = new TargetNode(targetId, invalidTarget);

			EventNode eventNode = EventNode.builder().id(eventId).date(eventDate).summary(eventSummary)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents).isSuccessful(isEventSuccessful)
					.isSuicide(isEventSuicide).motive(eventMotive).target(targetNode).build();

			EventNode updatedEventNode = EventNode.builder().id(eventId).date(eventDate).summary(eventSummary)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents).isSuccessful(isEventSuccessful)
					.isSuicide(isEventSuicide).motive(eventMotive).target(updatedTargetNode).build();

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.patch(any(JsonPatch.class), eq(eventNode), ArgumentMatchers.<Class<EventNode>>any()))
					.thenReturn(updatedEventNode);
//			doCallRealMethod().when(violationHelper).violate(updatedEventNode, EventDTO.class);

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
					() -> verifyNoMoreInteractions(eventService), () -> verify(patchHelper, times(1))
							.patch(any(JsonPatch.class), eq(eventNode), ArgumentMatchers.<Class<EventNode>>any()),
					() -> verifyNoMoreInteractions(patchHelper),
//					() -> verify(violationHelper, times(1)).violate(updatedEventNode, EventDTO.class),
//					() -> verifyNoMoreInteractions(violationHelper), 
					() -> verifyNoInteractions(eventModelAssembler),
					() -> verifyNoInteractions(pagedResourcesAssembler));
		}

		@Test
		void when_partial_update_valid_events_target_using_json_patch_should_return_partially_updated_node()
				throws ParseException {

			Long eventId = 1L;

			String eventSummary = "summary";
			String eventMotive = "motive";
			String eventDateString = "2000-08-05";
			Date eventDate = new SimpleDateFormat("yyyy-MM-dd").parse(eventDateString);
			boolean isEventPartOfMultipleIncidents = true;
			boolean isEventSuccessful = true;
			boolean isEventSuicide = true;

			Long targetId = 1L;
			String target = "target";
			String updatedTarget = "updated target";
			TargetNode targetNode = new TargetNode(targetId, target);
			TargetNode updatedTargetNode = new TargetNode(targetId, updatedTarget);
			TargetModel targetModel = new TargetModel(targetId, updatedTarget);

			String pathToTargetLink = TARGET_BASE_PATH + "/" + targetId.intValue();
			Link targetLink = new Link(pathToTargetLink);
			targetModel.add(targetLink);

			EventNode eventNode = EventNode.builder().id(eventId).date(eventDate).summary(eventSummary)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents).isSuccessful(isEventSuccessful)
					.isSuicide(isEventSuicide).motive(eventMotive).target(targetNode).build();

			EventNode updatedEventNode = EventNode.builder().id(eventId).date(eventDate).summary(eventSummary)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents).isSuccessful(isEventSuccessful)
					.isSuicide(isEventSuicide).motive(eventMotive).target(updatedTargetNode).build();

			EventModel eventModel = EventModel.builder().id(eventId).date(eventDate).summary(eventSummary)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents).isSuccessful(isEventSuccessful)
					.isSuicide(isEventSuicide).motive(eventMotive).target(targetModel).build();

			String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
			Link eventLink = new Link(pathToEventLink);
			eventModel.add(eventLink);

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(patchHelper.patch(any(JsonPatch.class), eq(eventNode), ArgumentMatchers.<Class<EventNode>>any()))
					.thenReturn(updatedEventNode);
			doNothing().when(violationHelper).violate(updatedEventNode, EventDTO.class);
			when(eventService.save(updatedEventNode)).thenReturn(updatedEventNode);
			when(eventModelAssembler.toModel(updatedEventNode)).thenReturn(eventModel);

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
							.andExpect(jsonPath("summary", is(eventSummary)))
							.andExpect(jsonPath("motive", is(eventMotive)))
							.andExpect(jsonPath("date", is(notNullValue())))
							.andExpect(jsonPath("suicide", is(isEventSuicide)))
							.andExpect(jsonPath("successful", is(isEventSuccessful)))
							.andExpect(jsonPath("partOfMultipleIncidents", is(isEventPartOfMultipleIncidents)))
							.andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
							.andExpect(jsonPath("target.id", is(targetId.intValue())))
							.andExpect(jsonPath("target.target", is(updatedTarget))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verify(patchHelper, times(1)).patch(any(JsonPatch.class), eq(eventNode),
							ArgumentMatchers.<Class<EventNode>>any()),
					() -> verifyNoMoreInteractions(patchHelper),
					() -> verify(violationHelper, times(1)).violate(updatedEventNode, EventDTO.class),
					() -> verifyNoMoreInteractions(violationHelper),
					() -> verify(eventService, times(1)).save(updatedEventNode),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(eventModelAssembler, times(1)).toModel(updatedEventNode),
					() -> verifyNoMoreInteractions(eventModelAssembler));
		}
	}

	@Nested
	@Tag("DeleteEventRequest_Tests")
	class DeleteEventRequestTest {

		@Test
		void when_delete_existing_event_should_return_event() throws ParseException {

			Long eventId = 1L;

			String eventSummary = "summary";
			String eventMotive = "motive";
			Date eventDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("03/07/2000 02:00:00:000");
			boolean isEventPartOfMultipleIncidents = true;
			boolean isEventSuccessful = true;
			boolean isEventSuicide = true;

			Long targetId = 1L;
			String target = "target";
			TargetNode targetNode = new TargetNode(targetId, target);
			TargetModel targetModel = new TargetModel(targetId, target);
			String pathToTargetLink = TARGET_BASE_PATH + "/" + targetId.intValue();
			Link targetLink = new Link(pathToTargetLink);
			targetModel.add(targetLink);

			EventNode eventNode = EventNode.builder().id(eventId).date(eventDate).summary(eventSummary)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents).isSuccessful(isEventSuccessful)
					.isSuicide(isEventSuicide).motive(eventMotive).target(targetNode).build();

			EventModel eventModel = EventModel.builder().id(eventId).date(eventDate).summary(eventSummary)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents).isSuccessful(isEventSuccessful)
					.isSuicide(isEventSuicide).motive(eventMotive).target(targetModel).build();

			String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
			Link eventLink = new Link(pathToEventLink);
			eventModel.add(eventLink);

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.delete(eventId)).thenReturn(Optional.of(eventNode));
			when(eventModelAssembler.toModel(eventNode)).thenReturn(eventModel);

			assertAll(
					() -> mockMvc.perform(delete(linkWithParameter, eventId)).andExpect(status().isOk())
							.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
							.andExpect(jsonPath("links[0].href", is(pathToEventLink)))
							.andExpect(jsonPath("id", is(eventId.intValue())))
							.andExpect(jsonPath("summary", is(eventSummary)))
							.andExpect(jsonPath("motive", is(eventMotive)))
							.andExpect(jsonPath("date",
									is(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(
											eventDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
							.andExpect(jsonPath("suicide", is(isEventSuicide)))
							.andExpect(jsonPath("successful", is(isEventSuccessful)))
							.andExpect(jsonPath("partOfMultipleIncidents", is(isEventPartOfMultipleIncidents)))
							.andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
							.andExpect(jsonPath("target.id", is(targetId.intValue())))
							.andExpect(jsonPath("target.target", is(target))),
					() -> verify(eventService, times(1)).delete(eventId), () -> verifyNoMoreInteractions(eventService),
					() -> verify(eventModelAssembler, times(1)).toModel(eventNode),
					() -> verifyNoMoreInteractions(eventModelAssembler));
		}

		@Test
		void when_delete_event_but_event_not_exists_should_return_error_response() {

			Long eventId = 1L;

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.delete(eventId)).thenReturn(Optional.empty());

			assertAll(
					() -> mockMvc.perform(delete(linkWithParameter, eventId)).andExpect(status().isNotFound())
							.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
							.andExpect(jsonPath("timestamp").isNotEmpty()).andExpect(content().json("{'status': 404}"))
							.andExpect(jsonPath("errors[0]", is("Could not find event with id: " + eventId))),
					() -> verify(eventService, times(1)).delete(eventId), () -> verifyNoMoreInteractions(eventService),
					() -> verifyNoInteractions(eventModelAssembler));
		}
	}

	public static String asJsonString(final Object obj) {

		try {

			return new ObjectMapper().writeValueAsString(obj);

		} catch (Exception e) {

			throw new RuntimeException(e);
		}
	}
}