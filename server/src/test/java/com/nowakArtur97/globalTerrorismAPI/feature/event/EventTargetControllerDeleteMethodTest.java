package com.nowakArtur97.globalTerrorismAPI.feature.event;

import com.nowakArtur97.globalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.nowakArtur97.globalTerrorismAPI.common.exception.ResourceNotFoundException;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetModel;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.EventBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventTargetController_Tests")
class EventTargetControllerDeleteMethodTest {

    private final String EVENT_BASE_PATH = "http://localhost:8080/api/v1/events";
    private final String LINK_WITH_PARAMETER = EVENT_BASE_PATH + "/" + "{id}/targets";

    private MockMvc mockMvc;

    private EventTargetController eventTargetController;

    @Mock
    private EventService eventService;

    @Mock
    private RepresentationModelAssemblerSupport<TargetNode, TargetModel> targetModelAssembler;

    private static TargetBuilder targetBuilder;
    private static EventBuilder eventBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        targetBuilder = new TargetBuilder();
        eventBuilder = new EventBuilder();
    }

    @BeforeEach
    private void setUp() {

        eventTargetController = new EventTargetController(eventService, targetModelAssembler);

        mockMvc = MockMvcBuilders.standaloneSetup(eventTargetController)
                .setControllerAdvice(new GenericRestControllerAdvice())
                .build();
    }

    @Test
    void when_delete_existing_event_target_should_not_return_content() {

        Long eventId = 1L;
        EventNode eventNode = (EventNode) eventBuilder.withId(eventId).build(ObjectType.NODE);

        when(eventService.deleteEventTarget(eventId)).thenReturn(Optional.of(eventNode));

        assertAll(() -> mockMvc.perform(delete(LINK_WITH_PARAMETER, eventId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNoContent())
                        .andExpect(jsonPath("$").doesNotExist()),
                () -> verify(eventService, times(1)).deleteEventTarget(eventId),
                () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(targetModelAssembler));
    }

    @Test
    void when_delete_event_target_but_event_does_not_exist_should_return_error_response() {

        Long eventId = 1L;

        when(eventService.deleteEventTarget(eventId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(delete(LINK_WITH_PARAMETER, eventId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find EventModel with id: " + eventId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(eventService, times(1)).deleteEventTarget(eventId),
                () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(targetModelAssembler));
    }

    @Test
    void when_delete_event_target_but_event_does_not_have_target_should_return_error_response() {

        Long eventId = 1L;

        when(eventService.deleteEventTarget(eventId)).thenThrow(new ResourceNotFoundException("TargetModel"));

        assertAll(
                () -> mockMvc.perform(delete(LINK_WITH_PARAMETER, eventId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find TargetModel")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(eventService, times(1)).deleteEventTarget(eventId),
                () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(targetModelAssembler));
    }
}
