package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.GroupModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.GroupBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("ObjectMapper_Tests")
class GroupMapperTest {

    private static GroupBuilder groupBuilder;
    private static EventBuilder eventBuilder;

    private ObjectMapper objectMapper;

    @Mock
    private ModelMapper modelMapper;

    @BeforeAll
    private static void init() {

        groupBuilder = new GroupBuilder();
        eventBuilder = new EventBuilder();
    }

    @BeforeEach
    private void setUp() {

        objectMapper = new ObjectMapperImpl(modelMapper);
    }

    @Test
    void when_map_group_node_to_model_should_return_model() {

        Long targetId = 1L;
        Long eventId = 1L;
        Long groupId = 1L;

        String group = "group";

        String summary = "summary";
        String motive = "motive";
        boolean isPartOfMultipleIncidents = true;
        boolean isSuccessful = true;
        boolean isSuicidal = true;

        TargetNode targetNode1 = new TargetNode(targetId, "target" + targetId);
        TargetModel targetModel1 = new TargetModel(targetId, "target" + targetId);

        EventNode eventNode1 = (EventNode) eventBuilder.withId(eventId).withSummary(summary + eventId)
                .withMotive(motive + eventId).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetNode1)
                .build(ObjectType.NODE);

        EventModel eventModel1 = (EventModel) eventBuilder.withId(eventId).withSummary(summary + eventId)
                .withMotive(motive + eventId).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetModel1)
                .build(ObjectType.MODEL);

        targetId++;
        eventId++;
        isPartOfMultipleIncidents = false;
        isSuccessful = false;
        isSuicidal = false;

        TargetNode targetNode2 = new TargetNode(targetId, "target" + targetId);
        TargetModel targetModel2 = new TargetModel(targetId, "target" + targetId);

        EventNode eventNode2 = (EventNode) eventBuilder.withId(eventId).withSummary(summary + eventId)
                .withMotive(motive + eventId).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetNode2)
                .build(ObjectType.NODE);

        EventModel eventModel2 = (EventModel) eventBuilder.withId(eventId).withSummary(summary + eventId)
                .withMotive(motive + eventId).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetModel2)
                .build(ObjectType.MODEL);

        GroupNode groupNode = (GroupNode) groupBuilder.withId(groupId).withName(group).withEventsCaused(List.of(eventNode1, eventNode2)).build(ObjectType.NODE);

        GroupModel groupModelExpected = (GroupModel) groupBuilder.withEventsCaused(List.of(eventModel1, eventModel2)).build(ObjectType.MODEL);

        when(modelMapper.map(groupNode, GroupModel.class)).thenReturn(groupModelExpected);

        GroupModel groupModelActual = objectMapper.map(groupNode, GroupModel.class);

        assertAll(
                () -> assertEquals(groupModelExpected.getId(), groupModelActual.getId(),
                        () -> "should return group model with id: " + groupModelExpected.getId() + ", but was: "
                                + groupModelActual.getId()),
                () -> assertEquals(groupModelExpected.getName(), groupModelActual.getName(),
                        () -> "should return group model with name: " + groupModelExpected.getName() + ", but was: "
                                + groupModelActual.getName()),

                () -> assertEquals(groupModelExpected.getEventsCaused().get(0).getSummary(), groupModelActual.getEventsCaused().get(0).getSummary(),
                        () -> "should return group's event model with summary: " + groupModelExpected.getEventsCaused().get(0).getSummary() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(0).getMotive(), groupModelActual.getEventsCaused().get(0).getMotive(),
                        () -> "should return group's event model with motive: " + groupModelExpected.getEventsCaused().get(0).getMotive() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(0).getDate(), groupModelActual.getEventsCaused().get(0).getDate(),
                        () -> "should return group's event model with date: " + groupModelExpected.getEventsCaused().get(0).getDate() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        groupModelActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        () -> "should return group's event model which was part of multiple incidents: "
                                + groupModelExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(0).getIsSuccessful(), groupModelActual.getEventsCaused().get(0).getIsSuccessful(),
                        () -> "should return group's event model which was successful: " + groupModelExpected.getEventsCaused().get(0).getIsSuccessful()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(0).getIsSuicidal(), groupModelActual.getEventsCaused().get(0).getIsSuicidal(),
                        () -> "should return group's event model which was suicidal: " + groupModelExpected.getEventsCaused().get(0).getIsSuicidal()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupModelActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group's event model with not null target, but was: null"),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(0).getTarget().getId(), groupModelActual.getEventsCaused().get(0).getTarget().getId(),
                        () -> "should return group's event model target with id: " + groupModelExpected.getEventsCaused().get(0).getTarget().getId() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getTarget().getId()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(0).getTarget().getTarget(), groupModelActual.getEventsCaused().get(0).getTarget().getTarget(),
                        () -> "should return group's event model with target: " + groupModelExpected.getEventsCaused().get(0).getTarget().getTarget()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getTarget().getTarget()),

                () -> assertEquals(groupModelExpected.getEventsCaused().get(1).getSummary(), groupModelActual.getEventsCaused().get(1).getSummary(),
                        () -> "should return group's event model with summary: " + groupModelExpected.getEventsCaused().get(1).getSummary() + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getSummary()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(1).getMotive(), groupModelActual.getEventsCaused().get(1).getMotive(),
                        () -> "should return group's event model with motive: " + groupModelExpected.getEventsCaused().get(1).getMotive() + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getMotive()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(1).getDate(), groupModelActual.getEventsCaused().get(1).getDate(),
                        () -> "should return group's event model with date: " + groupModelExpected.getEventsCaused().get(1).getDate() + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getDate()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(1).getIsPartOfMultipleIncidents(),
                        groupModelActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents(),
                        () -> "should return group's event model which was part of multiple incidents: "
                                + groupModelExpected.getEventsCaused().get(1).getIsPartOfMultipleIncidents() + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(1).getIsSuccessful(), groupModelActual.getEventsCaused().get(1).getIsSuccessful(),
                        () -> "should return group's event model which was successful: " + groupModelExpected.getEventsCaused().get(1).getIsSuccessful()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getIsSuccessful()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(1).getIsSuicidal(), groupModelActual.getEventsCaused().get(1).getIsSuicidal(),
                        () -> "should return group's event model which was suicidal: " + groupModelExpected.getEventsCaused().get(1).getIsSuicidal()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getIsSuicidal()),
                () -> assertNotNull(groupModelActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group's event model with not null target, but was: null"),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(1).getTarget().getId(), groupModelActual.getEventsCaused().get(1).getTarget().getId(),
                        () -> "should return group's event model target with id: " + groupModelExpected.getEventsCaused().get(1).getTarget().getId() + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getTarget().getId()),
                () -> assertEquals(groupModelExpected.getEventsCaused().get(1).getTarget().getTarget(), groupModelActual.getEventsCaused().get(1).getTarget().getTarget(),
                        () -> "should return group's event model with target: " + groupModelExpected.getEventsCaused().get(1).getTarget().getTarget()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getTarget().getTarget()),
                () -> verify(modelMapper, times(1)).map(groupNode, GroupModel.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }
}