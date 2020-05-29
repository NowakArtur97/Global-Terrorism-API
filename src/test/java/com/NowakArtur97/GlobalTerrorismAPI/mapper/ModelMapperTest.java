package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("ModelMapper_Tests")
class ModelMapperTest {

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

        modelMapper = new ModelMapper();
    }

    @Test
    void when_map_target_dto_to_node_should_return_valid_node() {

        TargetDTO targetDTOExpected = (TargetDTO) targetBuilder.build(ObjectType.DTO);

        TargetNode targetNodeActual = modelMapper.map(targetDTOExpected, TargetNode.class);

        assertAll(
                () -> assertNull(targetNodeActual.getId(),
                        () -> "should return target node with id as null, but was: " + targetNodeActual.getId()),
                () -> assertEquals(targetDTOExpected.getTarget(), targetNodeActual.getTarget(),
                        () -> "should return target node with target: " + targetDTOExpected.getTarget() + ", but was: "
                                + targetNodeActual.getTarget()));
    }

    @Test
    void when_map_target_node_to_dto_should_return_valid_dto() {

        TargetNode targetNodeExpected = (TargetNode) targetBuilder.build(ObjectType.NODE);

        TargetDTO targetDTOActual = modelMapper.map(targetNodeExpected, TargetDTO.class);

        assertAll(() -> assertEquals(targetNodeExpected.getTarget(), targetDTOActual.getTarget(),
                () -> "should return target dto with target: " + targetDTOActual.getTarget() + ", but was: "
                        + targetNodeExpected.getTarget()));
    }

    @Test
    void when_map_target_node_to_model_should_return_vali_model() {

        TargetNode targetNodeExpected = (TargetNode) targetBuilder.build(ObjectType.NODE);

        TargetModel targetModelActual = modelMapper.map(targetNodeExpected, TargetModel.class);

        assertAll(
                () -> assertEquals(targetNodeExpected.getTarget(), targetModelActual.getTarget(),
                        () -> "should return target model with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + targetModelActual.getTarget()));
    }

    @Test
    void when_map_event_dto_to_node_should_return_valid_node() throws ParseException {

        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withId(null).withTarget(targetDTO).build(ObjectType.DTO);

        EventNode eventNodeActual = modelMapper.map(eventDTO, EventNode.class);

        assertAll(
                () -> assertNull(eventNodeActual.getId(),
                        () -> "should return event node with id as null, but was: " + eventNodeActual.getId()),
                () -> assertEquals(eventDTO.getSummary(), eventNodeActual.getSummary(),
                        () -> "should return event node with summary: " + eventDTO.getSummary() + ", but was: "
                                + eventNodeActual.getSummary()),
                () -> assertEquals(eventDTO.getMotive(), eventNodeActual.getMotive(),
                        () -> "should return event node with motive: " + eventDTO.getMotive() + ", but was: "
                                + eventNodeActual.getMotive()),
                () -> assertEquals(eventDTO.getDate(), eventNodeActual.getDate(),
                        () -> "should return event node with date: " + eventDTO.getDate() + ", but was: "
                                + eventNodeActual.getDate()),
                () -> assertEquals(eventDTO.getIsPartOfMultipleIncidents(),
                        eventNodeActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event node which was part of multiple incidents: "
                                + eventDTO.getIsPartOfMultipleIncidents() + ", but was: "
                                + eventNodeActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventDTO.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
                        () -> "should return event node which was successful: " + eventDTO.getIsSuccessful()
                                + ", but was: " + eventNodeActual.getIsSuccessful()),
                () -> assertEquals(eventDTO.getIsSuicide(), eventNodeActual.getIsSuicide(),
                        () -> "should return event node which was suicide: " + eventDTO.getIsSuicide() + ", but was: "
                                + eventNodeActual.getIsSuicide()),
                () -> assertNotNull(eventNodeActual.getTarget(),
                        () -> "should return event node with not null target, but was: null"),
                () -> assertNull(eventNodeActual.getTarget().getId(),
                        () -> "should return events target node with id as null, but was: "
                                + eventNodeActual.getTarget().getId()),
                () -> assertEquals(eventDTO.getTarget().getTarget(), eventNodeActual.getTarget().getTarget(),
                        () -> "should return event node with target: " + eventDTO.getTarget().getTarget()
                                + ", but was: " + eventNodeActual.getTarget().getTarget()));
    }

    @Test
    void when_map_event_node_to_dto_should_return_valid_dto() throws ParseException {

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

        EventDTO eventDTOActual = modelMapper.map(eventNode, EventDTO.class);

        assertAll(
                () -> assertEquals(eventNode.getSummary(), eventDTOActual.getSummary(),
                        () -> "should return event dto with summary: " + eventNode.getSummary() + ", but was: "
                                + eventDTOActual.getSummary()),
                () -> assertEquals(eventNode.getMotive(), eventDTOActual.getMotive(),
                        () -> "should return event dto with motive: " + eventNode.getMotive() + ", but was: "
                                + eventDTOActual.getMotive()),
                () -> assertEquals(eventNode.getDate(), eventDTOActual.getDate(),
                        () -> "should return event dto with date: " + eventNode.getDate() + ", but was: "
                                + eventDTOActual.getDate()),
                () -> assertEquals(eventNode.getIsPartOfMultipleIncidents(),
                        eventDTOActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event dto which was part of multiple incidents: "
                                + eventNode.getIsPartOfMultipleIncidents() + ", but was: "
                                + eventDTOActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNode.getIsSuccessful(), eventDTOActual.getIsSuccessful(),
                        () -> "should return event dto which was successful: " + eventNode.getIsSuccessful()
                                + ", but was: " + eventDTOActual.getIsSuccessful()),
                () -> assertEquals(eventNode.getIsSuicide(), eventDTOActual.getIsSuicide(),
                        () -> "should return event dto which was suicide: " + eventNode.getIsSuicide() + ", but was: "
                                + eventDTOActual.getIsSuicide()),
                () -> assertNotNull(eventNode.getTarget(),
                        () -> "should return event dto with not null target, but was: null"),
                () -> assertEquals(eventNode.getTarget().getTarget(), eventDTOActual.getTarget().getTarget(),
                        () -> "should return event dto with target: " + eventNode.getTarget().getTarget()
                                + ", but was: " + eventDTOActual.getTarget().getTarget()));
    }

    @Test
    void when_map_event_node_to_model_should_return_valid_model() {

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

        EventModel eventModelActual = modelMapper.map(eventNodeExpected, EventModel.class);

        assertAll(
                () -> assertEquals(eventNodeExpected.getId(), eventModelActual.getId(),
                        () -> "should return event model with id: " + eventNodeExpected.getId() + ", but was: "
                                + eventModelActual.getId()),
                () -> assertEquals(eventNodeExpected.getSummary(), eventModelActual.getSummary(),
                        () -> "should return event model with summary: " + eventNodeExpected.getSummary() + ", but was: "
                                + eventModelActual.getSummary()),
                () -> assertEquals(eventNodeExpected.getMotive(), eventModelActual.getMotive(),
                        () -> "should return event model with motive: " + eventNodeExpected.getMotive() + ", but was: "
                                + eventModelActual.getMotive()),
                () -> assertEquals(eventNodeExpected.getDate(), eventModelActual.getDate(),
                        () -> "should return event model with date: " + eventNodeExpected.getDate() + ", but was: "
                                + eventModelActual.getDate()),
                () -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
                        eventModelActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event model which was part of multiple incidents: "
                                + eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was: "
                                + eventModelActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected.getIsSuccessful(), eventModelActual.getIsSuccessful(),
                        () -> "should return event model which was successful: " + eventNodeExpected.getIsSuccessful()
                                + ", but was: " + eventModelActual.getIsSuccessful()),
                () -> assertEquals(eventNodeExpected.getIsSuicide(), eventModelActual.getIsSuicide(),
                        () -> "should return event model which was suicide: " + eventNodeExpected.getIsSuicide()
                                + ", but was: " + eventModelActual.getIsSuicide()),
                () -> assertNotNull(eventModelActual.getTarget(),
                        () -> "should return event model with not null target, but was: null"),
                () -> assertEquals(eventNodeExpected.getTarget().getId(), eventModelActual.getTarget().getId(),
                        () -> "should return event model target with id: " + eventNodeExpected.getTarget().getId() + ", but was: "
                                + eventModelActual.getTarget().getId()),
                () -> assertEquals(eventNodeExpected.getTarget().getTarget(), eventModelActual.getTarget().getTarget(),
                        () -> "should return event model with target: " + eventNodeExpected.getTarget().getTarget()
                                + ", but was: " + eventModelActual.getTarget().getTarget()));
    }
}
