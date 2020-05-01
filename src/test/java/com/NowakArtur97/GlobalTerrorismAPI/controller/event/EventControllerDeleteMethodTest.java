package com.NowakArtur97.GlobalTerrorismAPI.controller.event;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.NowakArtur97.GlobalTerrorismAPI.advice.EventControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.EventModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.controller.EventController;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.EventService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.ViolationHelper;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventController_Tests")
public class EventControllerDeleteMethodTest {

	private final String EVENT_BASE_PATH = "http://localhost:8080/api/events";
	private final String TARGET_BASE_PATH = "http://localhost:8080/api/targets";

	private MockMvc mockMvc;

	private EventController eventController;

	@Mock
	private EventService eventService;

	@Mock
	private EventModelAssembler eventModelAssembler;

	@Mock
	private PagedResourcesAssembler<EventNode> pagedResourcesAssembler;

	@Mock
	private PatchHelper patchHelper;

	@Mock
	private ViolationHelper violationHelper;

	@BeforeEach
	private void setUp() {

		eventController = new EventController(eventService, eventModelAssembler, pagedResourcesAssembler, patchHelper,
				violationHelper);

		mockMvc = MockMvcBuilders.standaloneSetup(eventController).setControllerAdvice(new EventControllerAdvice())
				.build();
	}

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
		when(eventModelAssembler.toModel(ArgumentMatchers.any(EventNode.class))).thenReturn(eventModel);

		assertAll(
				() -> mockMvc.perform(delete(linkWithParameter, eventId)).andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(pathToEventLink)))
						.andExpect(jsonPath("id", is(eventId.intValue())))
						.andExpect(jsonPath("summary", is(eventSummary))).andExpect(jsonPath("motive", is(eventMotive)))
						.andExpect(jsonPath("date",
								is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
										.format(eventDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
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
