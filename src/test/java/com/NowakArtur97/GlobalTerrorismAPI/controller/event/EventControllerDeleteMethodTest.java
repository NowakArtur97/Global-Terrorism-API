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

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
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
	private EventModelAssembler modelAssembler;

	@Mock
	private PagedResourcesAssembler<EventNode> pagedResourcesAssembler;

	@Mock
	private PatchHelper patchHelper;

	@Mock
	private ViolationHelper violationHelper;

	private static TargetBuilder targetBuilder;
	private static EventBuilder eventBuilder;

	@BeforeEach
	private void setUp() {

		eventController = new EventController(eventService, modelAssembler, pagedResourcesAssembler, patchHelper,
				violationHelper);

		mockMvc = MockMvcBuilders.standaloneSetup(eventController).setControllerAdvice(new EventControllerAdvice())
				.build();

		targetBuilder = new TargetBuilder();
		eventBuilder = new EventBuilder();
	}

	@Test
	void when_delete_existing_event_should_return_event() {

		TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
		TargetModel targetModel = (TargetModel) targetBuilder.build(ObjectType.MODEL);
		String pathToTargetLink = TARGET_BASE_PATH + "/" + targetModel.getId().intValue();
		targetModel.add(new Link(pathToTargetLink));
		EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
		EventModel eventModel = (EventModel) eventBuilder.withTarget(targetModel).build(ObjectType.MODEL);
		Long eventId = 1L;
		String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
		Link eventLink = new Link(pathToEventLink);
		eventModel.add(eventLink);

		String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

		when(eventService.delete(eventId)).thenReturn(Optional.of(eventNode));
		when(modelAssembler.toModel(eventNode)).thenReturn(eventModel);

		assertAll(
				() -> mockMvc.perform(delete(linkWithParameter, eventId)).andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(pathToEventLink)))
						.andExpect(jsonPath("id", is(eventId.intValue())))
						.andExpect(jsonPath("summary", is(eventNode.getSummary())))
						.andExpect(jsonPath("motive", is(eventNode.getMotive())))
						.andExpect(jsonPath("date",
								is(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(
										eventNode.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
						.andExpect(jsonPath("isSuicide", is(eventNode.getIsSuicide())))
						.andExpect(jsonPath("isSuccessful", is(eventNode.getIsSuccessful())))
						.andExpect(jsonPath("isPartOfMultipleIncidents", is(eventNode.getIsPartOfMultipleIncidents())))
						.andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
						.andExpect(jsonPath("target.id", is(targetNode.getId().intValue())))
						.andExpect(jsonPath("target.target", is(targetNode.getTarget()))),
				() -> verify(eventService, times(1)).delete(eventId), () -> verifyNoMoreInteractions(eventService),
				() -> verify(modelAssembler, times(1)).toModel(eventNode),
				() -> verifyNoMoreInteractions(modelAssembler), () -> verifyNoInteractions(patchHelper),
				() -> verifyNoInteractions(violationHelper), () -> verifyNoInteractions(pagedResourcesAssembler));
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
				() -> verifyNoInteractions(modelAssembler), () -> verifyNoInteractions(patchHelper),
				() -> verifyNoInteractions(violationHelper), () -> verifyNoInteractions(pagedResourcesAssembler));
	}
}
