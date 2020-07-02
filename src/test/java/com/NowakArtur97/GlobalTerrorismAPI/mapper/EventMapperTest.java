package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("ObjectMapper_Tests")
class EventMapperTest {

    private ObjectMapper objectMapper;

    @Mock
    private ModelMapper modelMapper;

    private static TargetBuilder targetBuilder;
    private static EventBuilder eventBuilder;

    @BeforeAll
    private static void init() {

        targetBuilder = new TargetBuilder();
        eventBuilder = new EventBuilder();
    }

    @BeforeEach
    private void setUp() {

        objectMapper = new ObjectMapperImpl(modelMapper);
    }

    @Test
    void when_map_event_dto_to_node_should_return_node() {

        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
        TargetNode targetNode = (TargetNode) targetBuilder.withId(null).build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withId(null).withTarget(targetNode)
                .build(ObjectType.NODE);

        when(modelMapper.map(eventDTO, EventNode.class)).thenReturn(eventNodeExpected);

        EventNode eventNodeActual = objectMapper.map(eventDTO, EventNode.class);

        assertAll(
                () -> assertNull(eventNodeActual.getId(),
                        () -> "should return event node with id as null, but was: " + eventNodeActual.getId()),
                () -> assertEquals(eventNodeExpected.getSummary(), eventNodeActual.getSummary(),
                        () -> "should return event node with summary: " + eventNodeExpected.getSummary() + ", but was: "
                                + eventNodeActual.getSummary()),
                () -> assertEquals(eventNodeExpected.getMotive(), eventNodeActual.getMotive(),
                        () -> "should return event node with motive: " + eventNodeExpected.getMotive() + ", but was: "
                                + eventNodeActual.getMotive()),
                () -> assertEquals(eventNodeExpected.getDate(), eventNodeActual.getDate(),
                        () -> "should return event node with date: " + eventNodeExpected.getDate() + ", but was: "
                                + eventNodeActual.getDate()),
                () -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
                        eventNodeActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event node which was part of multiple incidents: "
                                + eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was: "
                                + eventNodeActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
                        () -> "should return event node which was successful: " + eventNodeExpected.getIsSuccessful()
                                + ", but was: " + eventNodeActual.getIsSuccessful()),
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), eventNodeActual.getIsSuicidal(),
                        () -> "should return event node which was suicidal: " + eventNodeExpected.getIsSuicidal()
                                + ", but was: " + eventNodeActual.getIsSuicidal()),
                () -> assertNotNull(eventNodeActual.getTarget(),
                        () -> "should return event node with not null target, but was: null"),
                () -> assertNull(eventNodeActual.getTarget().getId(),
                        () -> "should return events target node with id as null, but was: "
                                + eventNodeActual.getTarget().getId()),
                () -> assertEquals(eventNodeExpected.getTarget().getTarget(), eventNodeActual.getTarget().getTarget(),
                        () -> "should return event node with target: " + eventNodeExpected.getTarget().getTarget()
                                + ", but was: " + eventNodeActual.getTarget().getTarget()),
                () -> verify(modelMapper, times(1)).map(eventDTO, EventNode.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @Test
    void when_map_event_node_to_dto_should_return_dto() {

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTOExpected = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);

        when(modelMapper.map(eventNode, EventDTO.class)).thenReturn(eventDTOExpected);

        EventDTO eventDTOActual = objectMapper.map(eventNode, EventDTO.class);

        assertAll(
                () -> assertEquals(eventDTOExpected.getSummary(), eventDTOActual.getSummary(),
                        () -> "should return event dto with summary: " + eventDTOExpected.getSummary() + ", but was: "
                                + eventDTOActual.getSummary()),
                () -> assertEquals(eventDTOExpected.getMotive(), eventDTOActual.getMotive(),
                        () -> "should return event dto with motive: " + eventDTOExpected.getMotive() + ", but was: "
                                + eventDTOActual.getMotive()),
                () -> assertEquals(eventDTOExpected.getDate(), eventDTOActual.getDate(),
                        () -> "should return event dto with date: " + eventDTOExpected.getDate() + ", but was: "
                                + eventDTOActual.getDate()),
                () -> assertEquals(eventDTOExpected.getIsPartOfMultipleIncidents(),
                        eventDTOActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event dto which was part of multiple incidents: "
                                + eventDTOExpected.getIsPartOfMultipleIncidents() + ", but was: "
                                + eventDTOActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventDTOExpected.getIsSuccessful(), eventDTOActual.getIsSuccessful(),
                        () -> "should return event dto which was successful: " + eventDTOExpected.getIsSuccessful()
                                + ", but was: " + eventDTOActual.getIsSuccessful()),
                () -> assertEquals(eventDTOExpected.getIsSuicidal(), eventDTOActual.getIsSuicidal(),
                        () -> "should return event dto which was suicidal: " + eventDTOExpected.getIsSuicidal()
                                + ", but was: " + eventDTOActual.getIsSuicidal()),
                () -> assertNotNull(eventDTOActual.getTarget(),
                        () -> "should return event dto with not null target, but was: null"),
                () -> assertEquals(eventDTOExpected.getTarget().getTarget(), eventDTOActual.getTarget().getTarget(),
                        () -> "should return event dto with target: " + eventDTOExpected.getTarget().getTarget()
                                + ", but was: " + eventDTOActual.getTarget().getTarget()),
                () -> verify(modelMapper, times(1)).map(eventNode, EventDTO.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @Test
    void when_map_event_node_to_model_should_return_model() {

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
        TargetModel targetModel = (TargetModel) targetBuilder.build(ObjectType.MODEL);
        EventModel eventModelExpected = (EventModel) eventBuilder.withTarget(targetModel)
                .build(ObjectType.MODEL);

        when(modelMapper.map(eventNode, EventModel.class)).thenReturn(eventModelExpected);

        EventModel eventModelActual = objectMapper.map(eventNode, EventModel.class);

        assertAll(
                () -> assertEquals(eventModelExpected.getId(), eventModelActual.getId(),
                        () -> "should return event model with id: " + eventModelExpected.getId() + ", but was: "
                                + eventModelActual.getId()),
                () -> assertEquals(eventModelExpected.getSummary(), eventModelActual.getSummary(),
                        () -> "should return event model with summary: " + eventModelExpected.getSummary() + ", but was: "
                                + eventModelActual.getSummary()),
                () -> assertEquals(eventModelExpected.getMotive(), eventModelActual.getMotive(),
                        () -> "should return event model with motive: " + eventModelExpected.getMotive() + ", but was: "
                                + eventModelActual.getMotive()),
                () -> assertEquals(eventModelExpected.getDate(), eventModelActual.getDate(),
                        () -> "should return event model with date: " + eventModelExpected.getDate() + ", but was: "
                                + eventModelActual.getDate()),
                () -> assertEquals(eventModelExpected.getIsPartOfMultipleIncidents(),
                        eventModelActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event model which was part of multiple incidents: "
                                + eventModelExpected.getIsPartOfMultipleIncidents() + ", but was: "
                                + eventModelActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventModelExpected.getIsSuccessful(), eventModelActual.getIsSuccessful(),
                        () -> "should return event model which was successful: " + eventModelExpected.getIsSuccessful()
                                + ", but was: " + eventModelActual.getIsSuccessful()),
                () -> assertEquals(eventModelExpected.getIsSuicidal(), eventModelActual.getIsSuicidal(),
                        () -> "should return event model which was suicidal: " + eventModelExpected.getIsSuicidal()
                                + ", but was: " + eventModelActual.getIsSuicidal()),
                () -> assertNotNull(eventModelActual.getTarget(),
                        () -> "should return event model with not null target, but was: null"),
                () -> assertEquals(eventModelExpected.getTarget().getId(), eventModelActual.getTarget().getId(),
                        () -> "should return event model target with id: " + eventModelExpected.getTarget().getId() + ", but was: "
                                + eventModelActual.getTarget().getId()),
                () -> assertEquals(eventModelExpected.getTarget().getTarget(), eventModelActual.getTarget().getTarget(),
                        () -> "should return event model with target: " + eventModelExpected.getTarget().getTarget()
                                + ", but was: " + eventModelActual.getTarget().getTarget()),
                () -> verify(modelMapper, times(1)).map(eventNode, EventModel.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }
}