package com.NowakArtur97.GlobalTerrorismAPI.feature.group;

import com.NowakArtur97.GlobalTerrorismAPI.feature.event.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.event.EventModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.feature.event.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.target.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.target.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.GroupBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("GroupModelAssembler_Tests")
class GroupModelAssemblerTest {

    private final String GROUP_BASE_PATH = "http://localhost/api/v1/groups";
    private final String EVENT_BASE_PATH = "http://localhost/api/v1/events";
    private final String TARGET_BASE_PATH = "http://localhost/api/v1/targets";

    private GroupModelAssembler modelAssembler;

    @Mock
    private EventModelAssembler eventModelAssembler;

    @Mock
    private ModelMapper modelMapper;

    private static GroupBuilder groupBuilder;
    private static EventBuilder eventBuilder;
    private static TargetBuilder targetBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        groupBuilder = new GroupBuilder();
        eventBuilder = new EventBuilder();
        targetBuilder = new TargetBuilder();
    }

    @BeforeEach
    private void setUp() {

        modelAssembler = new GroupModelAssembler(eventModelAssembler, modelMapper);
    }

    @Test
    void when_map_group_node_to_model_should_return_group_model() {

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
        GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);

        TargetModel targetModel = (TargetModel) targetBuilder.build(ObjectType.MODEL);
        String pathToTargetLink = TARGET_BASE_PATH + targetNode.getId().intValue();
        Link targetLink = new Link(pathToTargetLink);
        targetModel.add(targetLink);
        EventModel eventModel = (EventModel) eventBuilder.withTarget(targetModel).build(ObjectType.MODEL);
        String pathToEventLink = EVENT_BASE_PATH + eventNode.getId().intValue();
        Link eventLink = new Link(pathToEventLink);
        eventModel.add(eventLink);
        String pathToEventTargetLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue() + "/targets";
        Link eventTargetLink = new Link(pathToEventTargetLink, "target");
        eventModel.add(eventTargetLink);

        String pathToGroupLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue();
        String pathToGroupEventsLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue() + "/events";

        GroupModel groupModel = (GroupModel) groupBuilder.withEventsCaused(List.of(eventModel)).build(ObjectType.MODEL);

        when(modelMapper.map(groupNode, GroupModel.class)).thenReturn(groupModel);
        when(eventModelAssembler.toModel(eventNode)).thenReturn(eventModel);

        GroupModel groupModelActual = modelAssembler.toModel(groupNode);

        assertAll(
                () -> assertEquals(pathToGroupLink, groupModelActual.getLink("self").get().getHref(),
                        () -> "should return group model with self link: " + pathToGroupLink + ", but was: "
                                + groupModelActual.getLink("self").get().getHref()),
                () -> assertEquals(pathToGroupEventsLink, groupModelActual.getLink("eventsCaused").get().getHref(),
                        () -> "should return group model with events link: " + pathToGroupEventsLink + ", but was: "
                                + groupModelActual.getLink("eventsCaused").get().getHref()),

                () -> assertEquals(pathToEventLink, groupModelActual.getEventsCaused().get(0).getLink("self").get().getHref(),
                        () -> "should return group event model with self link: " + pathToEventLink + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getLink("self").get().getHref()),
                () -> assertEquals(pathToEventTargetLink, groupModelActual.getEventsCaused().get(0).getLink("target").get().getHref(),
                        () -> "should return group event model with target link: " + pathToEventTargetLink + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getLink("target").get().getHref()),
                () -> assertTrue(groupModelActual.getEventsCaused().get(0).getLink("city").isEmpty(),
                        () -> "should return group event model without city link, but was: "
                                + groupModelActual.getEventsCaused().get(0).getLink("city").get().getHref()),
                () -> assertEquals(pathToTargetLink, groupModelActual.getEventsCaused().get(0).getTarget().getLink("self").get().getHref(),
                        () -> "should return event group target model with self link: " + pathToTargetLink + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getTarget().getLink("self").get().getHref()),

                () -> assertNotNull(groupModelActual.getId(),
                        () -> "should return event node with new id, but was: " + groupModelActual.getId()),
                () -> assertEquals(groupModel.getId(), groupModelActual.getId(),
                        () -> "should return group model with id: " + groupModel.getId() + ", but was: "
                                + groupModelActual.getId()),
                () -> assertEquals(groupModel.getName(), groupModelActual.getName(),
                        () -> "should return group model with name: " + groupModel.getName() + ", but was: "
                                + groupModelActual.getName()),
                () -> assertEquals(eventModel.getSummary(), groupModelActual.getEventsCaused().get(0).getSummary(),
                        () -> "should return group's event model with summary: " + eventModel.getSummary() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(eventModel.getMotive(), groupModelActual.getEventsCaused().get(0).getMotive(),
                        () -> "should return group's event model with motive: " + eventModel.getMotive() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(eventModel.getDate(), groupModelActual.getEventsCaused().get(0).getDate(),
                        () -> "should return group's event model with date: " + eventModel.getDate() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(eventModel.getIsPartOfMultipleIncidents(),
                        groupModelActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        () -> "should return group's event model which was part of multiple incidents: "
                                + eventModel.getIsPartOfMultipleIncidents() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventModel.getIsSuccessful(), groupModelActual.getEventsCaused().get(0).getIsSuccessful(),
                        () -> "should return group's event model which was successful: " + eventModel.getIsSuccessful()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(eventModel.getIsSuicidal(), groupModelActual.getEventsCaused().get(0).getIsSuicidal(),
                        () -> "should return group's event model which was suicidal: " + eventModel.getIsSuicidal()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupModelActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group's event model with not null target, but was: null"),
                () -> assertEquals(targetModel.getId(), groupModelActual.getEventsCaused().get(0).getTarget().getId(),
                        () -> "should return group's event model target with id: " + targetModel.getId() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getTarget().getId()),
                () -> assertEquals(targetModel.getTarget(), groupModelActual.getEventsCaused().get(0).getTarget().getTarget(),
                        () -> "should return group's event model with target: " + targetModel.getTarget()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getTarget().getTarget()),

                () -> assertNotNull(groupModelActual.getLinks(), () -> "should return model with links, but was: " + groupModelActual),
                () -> assertFalse(groupModelActual.getLinks().isEmpty(),
                        () -> "should return model with links, but was: " + groupModelActual),
                () -> assertNotNull(groupModelActual.getEventsCaused().get(0).getLinks(),
                        () -> "should return model with event with links, but was: " + groupModelActual.getEventsCaused().get(0)),
                () -> assertFalse(groupModelActual.getEventsCaused().get(0).getLinks().isEmpty(),
                        () -> "should return model with event with links, but was: " + groupModelActual.getEventsCaused().get(0)),
                () -> assertNotNull(groupModelActual.getEventsCaused().get(0).getTarget().getLinks(),
                        () -> "should return model with target with links, but was: " + groupModelActual.getEventsCaused().get(0)),
                () -> assertFalse(groupModelActual.getEventsCaused().get(0).getTarget().getLinks().isEmpty(),
                        () -> "should return model with event with links, but was: " + groupModelActual.getEventsCaused().get(0)),
                () -> verify(modelMapper, times(1)).map(groupNode, GroupModel.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(eventModelAssembler, times(1)).toModel(eventNode),
                () -> verifyNoMoreInteractions(eventModelAssembler));
    }

    @Test
    void when_map_group_node_to_model_without_events_should_return_group_model_without_events() {

        GroupNode groupNode = (GroupNode) groupBuilder.build(ObjectType.NODE);

        GroupModel groupModel = (GroupModel) groupBuilder.build(ObjectType.MODEL);

        String pathToGroupLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue();
        String pathToGroupEventsLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue() + "/events";

        when(modelMapper.map(groupNode, GroupModel.class)).thenReturn(groupModel);

        GroupModel groupModelActual = modelAssembler.toModel(groupNode);

        assertAll(
                () -> assertEquals(pathToGroupLink, groupModelActual.getLink("self").get().getHref(),
                        () -> "should return group model with self link: " + pathToGroupLink + ", but was: "
                                + groupModelActual.getLink("self").get().getHref()),
                () -> assertEquals(pathToGroupEventsLink, groupModelActual.getLink("eventsCaused").get().getHref(),
                        () -> "should return group model with events link: " + pathToGroupEventsLink + ", but was: "
                                + groupModelActual.getLink("eventsCaused").get().getHref()),
                () -> assertNotNull(groupModelActual.getId(),
                        () -> "should return event node with new id, but was: " + groupModelActual.getId()),
                () -> assertEquals(groupModel.getId(), groupModelActual.getId(),
                        () -> "should return group model with id: " + groupModel.getId() + ", but was: "
                                + groupModelActual.getId()),
                () -> assertEquals(groupModel.getName(), groupModelActual.getName(),
                        () -> "should return group model with name: " + groupModel.getName() + ", but was: "
                                + groupModelActual.getName()),
                () -> assertTrue(groupModel.getEventsCaused().isEmpty(),
                        () -> "should return group model with empty events list, but was: " + groupModel.getEventsCaused()),
                () -> assertNotNull(groupModelActual.getLinks(), () -> "should return model with links, but was: " + groupModelActual),
                () -> assertFalse(groupModelActual.getLinks().isEmpty(),
                        () -> "should return model with links, but was: " + groupModelActual),
                () -> verify(modelMapper, times(1)).map(groupNode, GroupModel.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verifyNoInteractions(eventModelAssembler));
    }
}
