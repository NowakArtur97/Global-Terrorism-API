package com.NowakArtur97.GlobalTerrorismAPI.controller.eventTarget;

import com.NowakArtur97.GlobalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.controller.event.EventTargetController;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.exception.ResourceNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.EventService;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
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
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventTargetController_Tests")
class EventTargetControllerDeleteMethodTest {

    private final String EVENT_BASE_PATH = "http://localhost:8080/api/v1/events";

    private MockMvc mockMvc;

    private EventTargetController eventTargetController;

    @Mock
    private EventService eventService;

    @Mock
    private GenericService<TargetNode, TargetDTO> targetService;

    @Mock
    private RepresentationModelAssemblerSupport<TargetNode, TargetModel> targetModelAssembler;

    private EventBuilder eventBuilder;

    @BeforeEach
    private void setUp() {

        eventTargetController = new EventTargetController(eventService, targetService, targetModelAssembler);

        mockMvc = MockMvcBuilders.standaloneSetup(eventTargetController)
                .setControllerAdvice(new GenericRestControllerAdvice())
                .build();

        eventBuilder = new EventBuilder();
    }

    @Test
    void when_delete_existing_event_target_should_not_return_content() {

        Long eventId = 1L;

        Long targetId = 2L;
        String targetName = "target";
        TargetNode targetNode = new TargetNode(targetId, targetName);
        EventNode eventNode = (EventNode) eventBuilder.withId(eventId).withTarget(targetNode).build(ObjectType.NODE);

        String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}/targets";

        when(eventService.deleteEventTarget(eventId)).thenReturn(Optional.of(eventNode));

        assertAll(() -> mockMvc.perform(delete(linkWithParameter, eventId))
                        .andExpect(status().isNoContent())
                        .andExpect(jsonPath("$").doesNotExist()),
                () -> verify(eventService, times(1)).deleteEventTarget(eventId),
                () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(targetModelAssembler));
    }

    @Test
    void when_delete_event_target_but_event_does_not_exist_should_return_error_response() {

        Long eventId = 1L;

        String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}/targets";

        when(eventService.deleteEventTarget(eventId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(delete(linkWithParameter, eventId))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find EventModel with id: " + eventId))),
                () -> verify(eventService, times(1)).deleteEventTarget(eventId),
                () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(targetModelAssembler));
    }

    @Test
    void when_delete_event_target_but_event_does_not_have_target_should_return_error_response() {

        Long eventId = 1L;

        String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}/targets";

        when(eventService.deleteEventTarget(eventId)).thenThrow(new ResourceNotFoundException("TargetModel"));

        assertAll(
                () -> mockMvc.perform(delete(linkWithParameter, eventId))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find TargetModel"))),
                () -> verify(eventService, times(1)).deleteEventTarget(eventId),
                () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(targetModelAssembler));
    }
}
