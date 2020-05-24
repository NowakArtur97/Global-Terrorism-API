package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
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

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("ObjectMapper_Tests")
class ObjectEventMapperTest {

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
    void when_map_event_dto_to_node_should_return_node() throws ParseException {

        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTOExpected = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
        TargetNode targetNode = (TargetNode) targetBuilder.withId(null).build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withId(null).withTarget(targetNode)
                .build(ObjectType.NODE);

        when(modelMapper.map(eventDTOExpected, EventNode.class)).thenReturn(eventNodeExpected);

        EventNode eventNodeActual = objectMapper.map(eventDTOExpected, EventNode.class);

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
                () -> assertEquals(eventNodeExpected.getIsSuicide(), eventNodeActual.getIsSuicide(),
                        () -> "should return event node which was suicide: " + eventNodeExpected.getIsSuicide()
                                + ", but was: " + eventNodeActual.getIsSuicide()),
                () -> assertNotNull(eventNodeActual.getTarget(),
                        () -> "should return event node with not null target, but was: null"),
                () -> assertNull(eventNodeActual.getTarget().getId(),
                        () -> "should return events target node with id as null, but was: "
                                + eventNodeActual.getTarget().getId()),
                () -> assertEquals(eventNodeExpected.getTarget().getTarget(), eventNodeActual.getTarget().getTarget(),
                        () -> "should return event node with target: " + eventNodeExpected.getTarget().getTarget()
                                + ", but was: " + eventNodeActual.getTarget().getTarget()),
                () -> verify(modelMapper, times(1)).map(eventDTOExpected, EventNode.class));
    }

    @Test
    void when_map_event_node_to_dto_should_return_dto() throws ParseException {

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTOExpected = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);

        when(modelMapper.map(eventNodeExpected, EventDTO.class)).thenReturn(eventDTOExpected);

        EventDTO eventDTOActual = objectMapper.map(eventNodeExpected, EventDTO.class);

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
                () -> assertEquals(eventDTOExpected.getIsSuicide(), eventDTOActual.getIsSuicide(),
                        () -> "should return event dto which was suicide: " + eventDTOExpected.getIsSuicide()
                                + ", but was: " + eventDTOActual.getIsSuicide()),
                () -> assertNotNull(eventDTOActual.getTarget(),
                        () -> "should return event dto with not null target, but was: null"),
                () -> assertEquals(eventDTOExpected.getTarget().getTarget(), eventDTOActual.getTarget().getTarget(),
                        () -> "should return event dto with target: " + eventDTOExpected.getTarget().getTarget()
                                + ", but was: " + eventDTOActual.getTarget().getTarget()),
                () -> verify(modelMapper, times(1)).map(eventNodeExpected, EventDTO.class));
    }
}