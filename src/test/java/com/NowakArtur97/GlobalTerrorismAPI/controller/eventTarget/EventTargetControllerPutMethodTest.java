package com.NowakArtur97.GlobalTerrorismAPI.controller.eventTarget;

import com.NowakArtur97.GlobalTerrorismAPI.advice.RestResponseGlobalEntityExceptionHandler;
import com.NowakArtur97.GlobalTerrorismAPI.controller.event.EventTargetController;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.EventService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.mapper.ObjectTestMapper;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("TargetController_Tests")
class EventTargetControllerPutMethodTest {

    private final String EVENT_BASE_PATH = "http://localhost:8080/api/v1/events";
    private final String TARGET_BASE_PATH = "http://localhost:8080/api/v1/targets";

    private MockMvc mockMvc;

    private EventTargetController eventTargetController;

    private RestResponseGlobalEntityExceptionHandler restResponseGlobalEntityExceptionHandler;

    @Mock
    private EventService eventService;

    @Mock
    private RepresentationModelAssemblerSupport<TargetNode, TargetModel> targetModelAssembler;

    private EventBuilder eventBuilder;

    @BeforeEach
    private void setUp() {

        eventTargetController = new EventTargetController(eventService, targetModelAssembler);

        restResponseGlobalEntityExceptionHandler = new RestResponseGlobalEntityExceptionHandler();

        mockMvc = MockMvcBuilders.standaloneSetup(eventTargetController, restResponseGlobalEntityExceptionHandler).build();

        eventBuilder = new EventBuilder();
    }

    @Test
    void when_add_valid_event_to_target_should_return_new_target_as_model() {

        Long eventId = 1L;

        Long targetId = 2L;
        String targetName = "updated target";
        TargetDTO targetDTO = new TargetDTO(targetName);
        TargetNode newTargetNode = new TargetNode(targetId, targetName);
        TargetModel targetModel = new TargetModel(targetId, targetName);

        String pathToLink = TARGET_BASE_PATH + "/" + targetId.intValue();
        Link link = new Link(pathToLink);
        targetModel.add(link);

        EventNode eventNode = (EventNode) eventBuilder.withId(eventId).build(ObjectType.NODE);
        EventNode updatedEventNode = (EventNode) eventBuilder.withId(eventId)
                .withTarget(newTargetNode).build(ObjectType.NODE);

        String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}/targets";

        when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
        when(eventService.addOrUpdateEventTarget(eventNode, targetDTO)).thenReturn(updatedEventNode);
        when(targetModelAssembler.toModel(newTargetNode)).thenReturn(targetModel);

        assertAll(
                () -> mockMvc
                        .perform(put(linkWithParameter, eventId).content(ObjectTestMapper.asJsonString(targetDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToLink)))
                        .andExpect(jsonPath("id", is(targetId.intValue())))
                        .andExpect(jsonPath("target", is(targetName))),
                () -> verify(eventService, times(1)).findById(eventId),
                () -> verify(eventService, times(1)).addOrUpdateEventTarget(eventNode, targetDTO),
                () -> verifyNoMoreInteractions(eventService),
                () -> verify(targetModelAssembler, times(1)).toModel(newTargetNode),
                () -> verifyNoMoreInteractions(targetModelAssembler));
    }

    @Test
    void when_update_valid_event_target_should_return_updated_target_as_model() {

        Long eventId = 1L;

        Long targetId = 2L;
        String oldTargetName = "target";
        String updatedTargetName = "updated target";
        TargetDTO targetDTO = new TargetDTO(oldTargetName);
        TargetNode targetNode = new TargetNode(targetId, oldTargetName);
        TargetNode updatedTargetNode = new TargetNode(targetId, updatedTargetName);
        TargetModel targetModel = new TargetModel(targetId, updatedTargetName);

        String pathToLink = TARGET_BASE_PATH + "/" + targetId.intValue();
        Link link = new Link(pathToLink);
        targetModel.add(link);

        EventNode eventNode = (EventNode) eventBuilder.withId(eventId).withTarget(targetNode).build(ObjectType.NODE);
        EventNode updatedEventNode = (EventNode) eventBuilder.withId(eventId).withTarget(updatedTargetNode).build(ObjectType.NODE);

        String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}/targets";

        when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
        when(eventService.addOrUpdateEventTarget(eventNode, targetDTO)).thenReturn(updatedEventNode);
        when(targetModelAssembler.toModel(updatedTargetNode)).thenReturn(targetModel);

        assertAll(
                () -> mockMvc
                        .perform(put(linkWithParameter, eventId).content(ObjectTestMapper.asJsonString(targetDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToLink)))
                        .andExpect(jsonPath("id", is(targetId.intValue())))
                        .andExpect(jsonPath("target", is(updatedTargetName))),
                () -> verify(eventService, times(1)).findById(eventId),
                () -> verify(eventService, times(1)).addOrUpdateEventTarget(eventNode, targetDTO),
                () -> verifyNoMoreInteractions(eventService),
                () -> verify(targetModelAssembler, times(1)).toModel(updatedTargetNode),
                () -> verifyNoMoreInteractions(targetModelAssembler));
    }

    @ParameterizedTest(name = "{index}: Target Name: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_invalid_event_target_should_return_errors(String targetName) {

        Long eventId = 1L;

        TargetDTO targetDTO = new TargetDTO(targetName);

        String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}/targets";

        assertAll(
                () -> mockMvc
                        .perform(put(linkWithParameter, eventId).content(ObjectTestMapper.asJsonString(targetDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest()).andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("{target.target.notBlank}")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verifyNoInteractions(eventService),
                () -> verifyNoInteractions(targetModelAssembler));
    }
}