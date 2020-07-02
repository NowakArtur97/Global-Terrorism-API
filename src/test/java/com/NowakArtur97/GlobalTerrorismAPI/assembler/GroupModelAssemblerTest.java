package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.GroupModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.GroupBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.Link;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("GroupModelAssembler_Tests")
class GroupModelAssemblerTest {

    private final String GROUP_BASE_PATH = "http://localhost:8080/api/groups";
    private final String EVENT_BASE_PATH = "http://localhost:8080/api/events";
    private final String TARGET_BASE_PATH = "http://localhost:8080/api/targets";

    private GroupModelAssembler modelAssembler;

    @Mock
    private EventModelAssembler eventModelAssembler;

    @Mock
    private ObjectMapper objectMapper;

    private GroupBuilder groupBuilder;
    private EventBuilder eventBuilder;
    private TargetBuilder targetBuilder;

    @BeforeEach
    private void setUp() {

        modelAssembler = new GroupModelAssembler(eventModelAssembler, objectMapper);

        groupBuilder = new GroupBuilder();
        eventBuilder = new EventBuilder();
        targetBuilder = new TargetBuilder();
    }

    @Test
    void when_map_group_node_to_model_should_return_group_model() {

        Long targetId = 1L;
        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
        GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);

        TargetModel targetModel = (TargetModel) targetBuilder.build(ObjectType.MODEL);
        String pathToTargetLink = TARGET_BASE_PATH + targetId.intValue();
        Link targetLink = new Link(pathToTargetLink);
        targetModel.add(targetLink);
        EventModel eventModel = (EventModel) eventBuilder.withTarget(targetModel).build(ObjectType.MODEL);
        String pathToEventLink = TARGET_BASE_PATH + targetId.intValue();
        Link eventLink = new Link(pathToEventLink);
        eventModel.add(eventLink);

        GroupModel groupModel = (GroupModel) groupBuilder.withEventsCaused(List.of(eventModel)).build(ObjectType.MODEL);

        when(objectMapper.map(groupNode, GroupModel.class)).thenReturn(groupModel);
        when(eventModelAssembler.toModel(eventNode)).thenReturn(eventModel);

        GroupModel model = modelAssembler.toModel(groupNode);

        assertAll(
                () -> assertNotNull(model.getId(),
                        () -> "should return event node with new id, but was: " + model.getId()),
                () -> assertEquals(groupModel.getId(), model.getId(),
                        () -> "should return group model with id: " + groupModel.getId() + ", but was: "
                                + model.getId()),
                () -> assertEquals(groupModel.getName(), model.getName(),
                        () -> "should return group model with name: " + groupModel.getName() + ", but was: "
                                + model.getName()),

                () -> assertEquals(groupModel.getEventsCaused().get(0).getSummary(), model.getEventsCaused().get(0).getSummary(),
                        () -> "should return group's event model with summary: " + groupModel.getEventsCaused().get(0).getSummary() + ", but was: "
                                + model.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(groupModel.getEventsCaused().get(0).getMotive(), model.getEventsCaused().get(0).getMotive(),
                        () -> "should return group's event model with motive: " + groupModel.getEventsCaused().get(0).getMotive() + ", but was: "
                                + model.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(groupModel.getEventsCaused().get(0).getDate(), model.getEventsCaused().get(0).getDate(),
                        () -> "should return group's event model with date: " + groupModel.getEventsCaused().get(0).getDate() + ", but was: "
                                + model.getEventsCaused().get(0).getDate()),
                () -> assertEquals(groupModel.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        model.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        () -> "should return group's event model which was part of multiple incidents: "
                                + groupModel.getEventsCaused().get(0).getIsPartOfMultipleIncidents() + ", but was: "
                                + model.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupModel.getEventsCaused().get(0).getIsSuccessful(), model.getEventsCaused().get(0).getIsSuccessful(),
                        () -> "should return group's event model which was successful: " + groupModel.getEventsCaused().get(0).getIsSuccessful()
                                + ", but was: " + model.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(groupModel.getEventsCaused().get(0).getIsSuicidal(), model.getEventsCaused().get(0).getIsSuicidal(),
                        () -> "should return group's event model which was suicidal: " + groupModel.getEventsCaused().get(0).getIsSuicidal()
                                + ", but was: " + model.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(model.getEventsCaused().get(0).getTarget(),
                        () -> "should return group's event model with not null target, but was: null"),
                () -> assertEquals(groupModel.getEventsCaused().get(0).getTarget().getId(), model.getEventsCaused().get(0).getTarget().getId(),
                        () -> "should return group's event model target with id: " + groupModel.getEventsCaused().get(0).getTarget().getId() + ", but was: "
                                + model.getEventsCaused().get(0).getTarget().getId()),
                () -> assertEquals(groupModel.getEventsCaused().get(0).getTarget().getTarget(), model.getEventsCaused().get(0).getTarget().getTarget(),
                        () -> "should return group's event model with target: " + groupModel.getEventsCaused().get(0).getTarget().getTarget()
                                + ", but was: " + model.getEventsCaused().get(0).getTarget().getTarget()),

                () -> assertNotNull(model.getLinks(), () -> "should return model with links, but was: " + model),
                () -> assertFalse(model.getLinks().isEmpty(),
                        () -> "should return model with links, but was: " + model),
                () -> assertNotNull(model.getEventsCaused().get(0).getLinks(), () -> "should return model with event with links, but was: " + model.getEventsCaused().get(0)),
                () -> assertFalse(model.getEventsCaused().get(0).getLinks().isEmpty(),
                        () -> "should return model with event with links, but was: " + model.getEventsCaused().get(0)),
                () -> assertNotNull(model.getEventsCaused().get(0).getTarget().getLinks(), () -> "should return model with target with links, but was: " + model.getEventsCaused().get(0)),
                () -> assertFalse(model.getEventsCaused().get(0).getTarget().getLinks().isEmpty(),
                        () -> "should return model with event with links, but was: " + model.getEventsCaused().get(0)),
                () -> verify(objectMapper, times(1)).map(groupNode, GroupModel.class),
                () -> verifyNoMoreInteractions(objectMapper),
                () -> verify(eventModelAssembler, times(1)).toModel(eventNode),
                () -> verifyNoMoreInteractions(eventModelAssembler));
    }

    @Test
    void when_map_group_node_to_model_without_events_should_return_group_model_without_events() {

        GroupNode groupNode = (GroupNode) groupBuilder.build(ObjectType.NODE);

        GroupModel groupModel = (GroupModel) groupBuilder.build(ObjectType.MODEL);

        when(objectMapper.map(groupNode, GroupModel.class)).thenReturn(groupModel);

        GroupModel model = modelAssembler.toModel(groupNode);

        assertAll(
                () -> assertNotNull(model.getId(),
                        () -> "should return event node with new id, but was: " + model.getId()),
                () -> assertEquals(groupModel.getId(), model.getId(),
                        () -> "should return group model with id: " + groupModel.getId() + ", but was: "
                                + model.getId()),
                () -> assertEquals(groupModel.getName(), model.getName(),
                        () -> "should return group model with name: " + groupModel.getName() + ", but was: "
                                + model.getName()),
                () -> assertTrue(groupModel.getEventsCaused().isEmpty(),
                        () -> "should return group model with empty events list, but was: " + groupModel.getEventsCaused()),
                () -> assertNotNull(model.getLinks(), () -> "should return model with links, but was: " + model),
                () -> assertFalse(model.getLinks().isEmpty(),
                        () -> "should return model with links, but was: " + model),
                () -> verify(objectMapper, times(1)).map(groupNode, GroupModel.class),
                () -> verifyNoMoreInteractions(objectMapper),
                () -> verifyNoInteractions(eventModelAssembler));
    }
}
