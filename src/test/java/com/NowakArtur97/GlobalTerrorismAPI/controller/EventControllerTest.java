package com.NowakArtur97.GlobalTerrorismAPI.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.NowakArtur97.GlobalTerrorismAPI.advice.EventControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.advice.RestResponseGlobalEntityExceptionHandler;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.EventModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.EventService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.icu.util.Calendar;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventController_Tests")
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

	@BeforeEach
	private void setUp() {

		eventController = new EventController(eventService, eventModelAssembler, pagedResourcesAssembler);

		restResponseGlobalEntityExceptionHandler = new RestResponseGlobalEntityExceptionHandler();

		mockMvc = MockMvcBuilders.standaloneSetup(eventController, restResponseGlobalEntityExceptionHandler)
				.setControllerAdvice(new EventControllerAdvice())
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
//				.setMessageConverters(new JsonMergePatchHttpMessageConverter(), new JsonPatchHttpMessageConverter(),
//						new MappingJackson2HttpMessageConverter())
				.build();
	}

	@Nested
	@Tag("GetEventRequest_Tests")
	class GetEventRequestTest {

		@Test
		void when_find_existing_event_should_return_event() {

			Long eventId = 1L;

			String eventSummary = "summary";
			String eventMotive = "motive";
			Date eventDate = Calendar.getInstance().getTime();
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
			String linkExpected = EVENT_BASE_PATH + "/" + eventId;

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(eventModelAssembler.toModel(eventNode)).thenReturn(eventModel);

			assertAll(
					() -> mockMvc.perform(get(linkWithParameter, eventId)).andExpect(status().isOk())
							.andDo(MockMvcResultHandlers.print())
							.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
							.andExpect(jsonPath("links[0].href", is(linkExpected)))
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
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(eventModelAssembler, times(1)).toModel(eventNode),
					() -> verifyNoMoreInteractions(eventModelAssembler));
		}

		@Test
		void when_find_event_but_event_not_exists_should_return_error_response() {

			Long eventId = 1L;

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

			when(eventService.findById(eventId)).thenReturn(Optional.empty());

			assertAll(
					() -> mockMvc.perform(get(linkWithParameter, eventId)).andExpect(status().isNotFound())
							.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
							.andExpect(jsonPath("timestamp",
									is(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(LocalDateTime.now()))))
							.andExpect(content().json("{'status': 404}"))
							.andExpect(jsonPath("errors[0]", is("Could not find event with id: " + eventId))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verifyNoMoreInteractions(eventService), () -> verifyNoInteractions(eventModelAssembler));
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