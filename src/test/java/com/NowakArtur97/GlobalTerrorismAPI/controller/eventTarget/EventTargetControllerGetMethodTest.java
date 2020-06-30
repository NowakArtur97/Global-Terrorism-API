package com.NowakArtur97.GlobalTerrorismAPI.controller.eventTarget;

import com.NowakArtur97.GlobalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.controller.event.EventTargetController;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.EventService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventTargetController_Tests")
class EventTargetControllerGetMethodTest {

    private final String EVENT_BASE_PATH = "http://localhost:8080/api/v1/events";
    private final String TARGET_BASE_PATH = "http://localhost:8080/api/v1/targets";

    private MockMvc mockMvc;

    private EventTargetController eventTargetController;

    @Mock
    private EventService eventService;

    @Mock
    private RepresentationModelAssemblerSupport<TargetNode, TargetModel> targetModelAssembler;

    private EventBuilder eventBuilder;

    @BeforeEach
    private void setUp() {

        eventTargetController = new EventTargetController(eventService, targetModelAssembler);

        mockMvc = MockMvcBuilders.standaloneSetup(eventTargetController).setControllerAdvice(new GenericRestControllerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();

        eventBuilder = new EventBuilder();
    }

    @Test
    void when_find_existing_event_target_should_return_target() {

        Long eventId = 1L;

        Long targetId = 2L;
        String targetName = "target";
        TargetNode targetNode = new TargetNode(targetId, targetName);
        EventNode eventNode = (EventNode) eventBuilder.withId(eventId).withTarget(targetNode).build(ObjectType.NODE);
        TargetModel targetModel = new TargetModel(targetId, targetName);

        String pathToLink = TARGET_BASE_PATH + "/" + targetId.intValue();
        Link link = new Link(pathToLink);
        targetModel.add(link);

        String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}/targets";

        when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
        when(targetModelAssembler.toModel(targetNode)).thenReturn(targetModel);

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, eventId))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToLink)))
                        .andExpect(jsonPath("id", is(targetId.intValue())))
                        .andExpect(jsonPath("target", is(targetName))),
                () -> verify(eventService, times(1)).findById(eventId),
                () -> verifyNoMoreInteractions(eventService),
                () -> verify(targetModelAssembler, times(1)).toModel(targetNode),
                () -> verifyNoMoreInteractions(targetModelAssembler));
    }

    @Test
    void when_find_event_target_but_event_not_exists_should_return_error_response() {

        Long eventId = 1L;

        String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}/targets";

        when(eventService.findById(eventId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, eventId))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find EventModel with id: " + eventId))),
                () -> verify(eventService, times(1)).findById(eventId),
                () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(targetModelAssembler));
    }

    @Test
    void when_find_event_target_but_event_exists_without_target_should_return_error_response() {

        Long eventId = 1L;

        EventNode eventNode = (EventNode) eventBuilder.withId(eventId).withTarget(null).build(ObjectType.NODE);

        String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}/targets";

        when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, eventId))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find TargetModel"))),
                () -> verify(eventService, times(1)).findById(eventId),
                () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(targetModelAssembler));
    }
}
