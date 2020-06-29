package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.GroupDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.GroupRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GroupService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("GroupServiceImpl_Tests")
class GroupServiceImplTest {

    private static final int DEFAULT_SEARCHING_DEPTH = 1;

    private GroupService groupService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private GenericService<EventNode, EventDTO> eventService;

    private GroupBuilder groupBuilder;
    private EventBuilder eventBuilder;
    private TargetBuilder targetBuilder;

    @BeforeEach
    private void setUp() {

        groupService = new GroupServiceImpl(groupRepository, objectMapper, eventService);

        groupBuilder = new GroupBuilder();
        eventBuilder = new EventBuilder();
        targetBuilder = new TargetBuilder();
    }


    @Test
    void when_groups_exist_and_return_all_groups_should_return_groups() {

        List<GroupNode> groupsListExpected = new ArrayList<>();

        GroupNode group1 = new GroupNode("group1");
        GroupNode group2 = new GroupNode("group2");
        GroupNode group3 = new GroupNode("group3");

        groupsListExpected.add(group1);
        groupsListExpected.add(group2);
        groupsListExpected.add(group3);

        Page<GroupNode> groupsExpected = new PageImpl<>(groupsListExpected);

        Pageable pageable = PageRequest.of(0, 100);

        when(groupRepository.findAll(pageable, DEFAULT_SEARCHING_DEPTH)).thenReturn(groupsExpected);

        Page<GroupNode> groupsActual = groupService.findAll(pageable);

        assertAll(() -> assertNotNull(groupsActual, () -> "shouldn`t return null"),
                () -> assertEquals(groupsListExpected, groupsActual.getContent(),
                        () -> "should contain: " + groupsListExpected + ", but was: " + groupsActual.getContent()),
                () -> assertEquals(groupsExpected.getNumberOfElements(), groupsActual.getNumberOfElements(),
                        () -> "should return page with: " + groupsExpected.getNumberOfElements()
                                + " elements, but was: " + groupsActual.getNumberOfElements()),
                () -> verify(groupRepository, times(1)).findAll(pageable, DEFAULT_SEARCHING_DEPTH),
                () -> verifyNoMoreInteractions(groupRepository), () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_groups_not_exist_and_return_all_groups_should_not_return_any_groups() {

        List<GroupNode> groupsListExpected = new ArrayList<>();

        Page<GroupNode> groupsExpected = new PageImpl<>(groupsListExpected);

        Pageable pageable = PageRequest.of(0, 100);

        when(groupRepository.findAll(pageable, DEFAULT_SEARCHING_DEPTH)).thenReturn(groupsExpected);

        Page<GroupNode> groupsActual = groupService.findAll(pageable);

        assertAll(() -> assertNotNull(groupsActual, () -> "shouldn`t return null"),
                () -> assertEquals(groupsListExpected, groupsActual.getContent(),
                        () -> "should contain empty list, but was: " + groupsActual.getContent()),
                () -> assertEquals(groupsListExpected, groupsActual.getContent(),
                        () -> "should contain: " + groupsListExpected + ", but was: " + groupsActual.getContent()),
                () -> assertEquals(groupsExpected.getNumberOfElements(), groupsActual.getNumberOfElements(),
                        () -> "should return empty page, but was: " + groupsActual.getNumberOfElements()),
                () -> verify(groupRepository, times(1)).findAll(pageable, DEFAULT_SEARCHING_DEPTH),
                () -> verifyNoMoreInteractions(groupRepository), () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_group_exists_and_return_one_group_should_return_one_group() {

        Long expectedGroupId = 1L;

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
        GroupNode groupNodeExpected = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);

        when(groupRepository.findById(expectedGroupId)).thenReturn(Optional.of(groupNodeExpected));

        Optional<GroupNode> groupActualOptional = groupService.findById(expectedGroupId);

        GroupNode groupNodeActual = groupActualOptional.get();

        assertAll(() -> assertEquals(groupNodeExpected.getId(), groupNodeActual.getId(),
                () -> "should return group with id: " + expectedGroupId + ", but was" + groupNodeActual.getId()),
                () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                        () -> "should return group with name: " + groupNodeExpected.getName() + ", but was"
                                + groupNodeActual.getName()),

                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getId(), groupNodeActual.getEventsCaused().get(0).getId(), () -> "should return group with event node with id: " + groupNodeExpected.getEventsCaused().get(0).getId() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getId()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group with event node with summary: " + groupNodeExpected.getEventsCaused().get(0).getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(), () -> "should return group with event node with motive: " + groupNodeExpected.getEventsCaused().get(0).getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getDate(), groupNodeActual.getEventsCaused().get(0).getDate(), () -> "should return group with event node with date: " + groupNodeExpected.getEventsCaused().get(0).getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(), () -> "should return group with event node which was part of multiple incidents: " + groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group with event node which was successful: " + groupNodeExpected.getEventsCaused().get(0).getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuicidal(), groupNodeActual.getEventsCaused().get(0).getIsSuicidal(), () -> "should return group with event node which was suicidal: " + groupNodeExpected.getEventsCaused().get(0).getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupNodeExpected.getEventsCaused().get(0).getTarget(),
                        () -> "should return group with event node with not null target, but was: null"),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget(), () -> "should return group with event node with target: " + groupNodeExpected.getEventsCaused().get(0).getTarget() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget()),
                () -> verify(groupRepository, times(1)).findById(expectedGroupId),
                () -> verifyNoMoreInteractions(objectMapper), () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_group_does_not_exist_and_return_one_group_should_return_empty_optional() {

        Long expectedGroupId = 1L;

        when(groupRepository.findById(expectedGroupId)).thenReturn(Optional.empty());

        Optional<GroupNode> groupActualOptional = groupService.findById(expectedGroupId);

        assertAll(() -> assertTrue(groupActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(groupRepository, times(1)).findById(expectedGroupId),
                () -> verifyNoMoreInteractions(groupRepository), () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_save_group_with_events_should_save_group_and_events() {

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
        GroupNode groupNodeExpectedBeforeSave = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
        GroupNode groupNodeExpected = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);

        when(groupRepository.save(groupNodeExpectedBeforeSave)).thenReturn(groupNodeExpected);

        GroupNode groupNodeActual = groupService.save(groupNodeExpectedBeforeSave);

        assertAll(
                () -> assertNotNull(groupNodeActual.getId(),
                        () -> "should return group node with id, but was null"),
                () -> assertEquals(groupNodeExpected.getId(), groupNodeActual.getId(),
                        () -> "should return group with id: " + groupNodeExpected.getId() + ", but was" + groupNodeActual.getId()),
                () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                        () -> "should return group with name: " + groupNodeExpected.getName() + ", but was"
                                + groupNodeActual.getName()),

                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getId(), groupNodeActual.getEventsCaused().get(0).getId(), () -> "should return group with event node with id: " + groupNodeExpected.getEventsCaused().get(0).getId() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getId()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group with event node with summary: " + groupNodeExpected.getEventsCaused().get(0).getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(), () -> "should return group with event node with motive: " + groupNodeExpected.getEventsCaused().get(0).getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getDate(), groupNodeActual.getEventsCaused().get(0).getDate(), () -> "should return group with event node with date: " + groupNodeExpected.getEventsCaused().get(0).getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(), () -> "should return group with event node which was part of multiple incidents: " + groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group with event node which was successful: " + groupNodeExpected.getEventsCaused().get(0).getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuicidal(), groupNodeActual.getEventsCaused().get(0).getIsSuicidal(), () -> "should return group with event node which was suicidal: " + groupNodeExpected.getEventsCaused().get(0).getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupNodeExpected.getEventsCaused().get(0).getTarget(),
                        () -> "should return group with event node with not null target, but was: null"),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget(), () -> "should return group with event node with target: " + groupNodeExpected.getEventsCaused().get(0).getTarget() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget()),
                () -> verify(groupRepository, times(1)).save(groupNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(groupRepository), () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(eventService));
    }

    @Test
    void when_save_new_group_with_events_should_save_new_group_and_events() {

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
        GroupDTO groupDTOExpected = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.DTO);
        GroupNode groupNodeExpectedBeforeSave = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
        GroupNode groupNodeExpected = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);

        when(objectMapper.map(groupDTOExpected, GroupNode.class)).thenReturn(groupNodeExpectedBeforeSave);
        when(groupRepository.save(groupNodeExpectedBeforeSave)).thenReturn(groupNodeExpected);

        GroupNode groupNodeActual = groupService.saveNew(groupDTOExpected);

        assertAll(
                () -> assertNotNull(groupNodeActual.getId(),
                        () -> "should return group node with id, but was null"),
                () -> assertEquals(groupNodeExpected.getId(), groupNodeActual.getId(),
                        () -> "should return group with id: " + groupNodeExpected.getId() + ", but was" + groupNodeActual.getId()),
                () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                        () -> "should return group with name: " + groupNodeExpected.getName() + ", but was"
                                + groupNodeActual.getName()),

                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getId(), groupNodeActual.getEventsCaused().get(0).getId(), () -> "should return group with event node with id: " + groupNodeExpected.getEventsCaused().get(0).getId() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getId()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group with event node with summary: " + groupNodeExpected.getEventsCaused().get(0).getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(), () -> "should return group with event node with motive: " + groupNodeExpected.getEventsCaused().get(0).getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getDate(), groupNodeActual.getEventsCaused().get(0).getDate(), () -> "should return group with event node with date: " + groupNodeExpected.getEventsCaused().get(0).getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(), () -> "should return group with event node which was part of multiple incidents: " + groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group with event node which was successful: " + groupNodeExpected.getEventsCaused().get(0).getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuicidal(), groupNodeActual.getEventsCaused().get(0).getIsSuicidal(), () -> "should return group with event node which was suicidal: " + groupNodeExpected.getEventsCaused().get(0).getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupNodeExpected.getEventsCaused().get(0).getTarget(),
                        () -> "should return group with event node with not null target, but was: null"),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget(), () -> "should return group with event node with target: " + groupNodeExpected.getEventsCaused().get(0).getTarget() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget()),
                () -> verify(groupRepository, times(1)).save(groupNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(groupRepository),
                () -> verify(objectMapper, times(1)).map(groupDTOExpected, GroupNode.class),
                () -> verifyNoMoreInteractions(objectMapper), () -> verifyNoInteractions(eventService));
    }

    @Test
    void when_update_group_with_events_should_update_group_and_events() {

        String targetNameUpdated = "target2";
        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        TargetNode targetNodeUpdated = (TargetNode) targetBuilder.withTarget(targetNameUpdated).build(ObjectType.NODE);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget(targetNameUpdated).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
        EventNode eventNodeUpdated = (EventNode) eventBuilder.withTarget(targetNodeUpdated).build(ObjectType.NODE);

        String targetNameUpdated2 = "target3";
        TargetNode targetNode2 = (TargetNode) targetBuilder.build(ObjectType.NODE);
        TargetNode targetNodeUpdated2 = (TargetNode) targetBuilder.withTarget(targetNameUpdated2).build(ObjectType.NODE);
        TargetDTO targetDTO2 = (TargetDTO) targetBuilder.withTarget(targetNameUpdated2).build(ObjectType.DTO);
        EventDTO eventDTO2 = (EventDTO) eventBuilder.withTarget(targetDTO2).build(ObjectType.DTO);
        EventNode eventNode2 = (EventNode) eventBuilder.withTarget(targetNode2).build(ObjectType.NODE);
        EventNode eventNodeUpdated2 = (EventNode) eventBuilder.withTarget(targetNodeUpdated2).build(ObjectType.NODE);

        String groupNameUpdated = "new group name";
        GroupDTO groupDTOExpected = (GroupDTO) groupBuilder.withName(groupNameUpdated).withEventsCaused(List.of(eventDTO, eventDTO2)).build(ObjectType.DTO);
        GroupNode groupNodeExpectedBeforeMethod = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
        GroupNode groupNodeExpectedBeforeSetId = (GroupNode) groupBuilder.withId(null).withName(groupNameUpdated).withEventsCaused(List.of(eventNode, eventNode2)).build(ObjectType.NODE);
        GroupNode groupNodeExpected = (GroupNode) groupBuilder.withName(groupNameUpdated).withEventsCaused(List.of(eventNodeUpdated, eventNodeUpdated2)).build(ObjectType.NODE);

        when(eventService.saveNew(eventDTO)).thenReturn(eventNodeUpdated);
        when(eventService.saveNew(eventDTO2)).thenReturn(eventNodeUpdated2);
        when(objectMapper.map(groupDTOExpected, GroupNode.class)).thenReturn(groupNodeExpectedBeforeSetId);
        when(groupRepository.save(groupNodeExpectedBeforeSetId)).thenReturn(groupNodeExpected);

        GroupNode groupNodeActual = groupService.update(groupNodeExpectedBeforeMethod, groupDTOExpected);

        assertAll(
                () -> assertNotNull(groupNodeActual.getId(),
                        () -> "should return group node with id, but was null"),
                () -> assertEquals(groupNodeExpected.getId(), groupNodeActual.getId(),
                        () -> "should return group with id: " + groupNodeExpected.getId() + ", but was" + groupNodeActual.getId()),
                () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                        () -> "should return group with name: " + groupNodeExpected.getName() + ", but was"
                                + groupNodeActual.getName()),

                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getId(), groupNodeActual.getEventsCaused().get(0).getId(), () -> "should return group with event node with id: " + groupNodeExpected.getEventsCaused().get(0).getId() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getId()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group with event node with summary: " + groupNodeExpected.getEventsCaused().get(0).getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(), () -> "should return group with event node with motive: " + groupNodeExpected.getEventsCaused().get(0).getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getDate(), groupNodeActual.getEventsCaused().get(0).getDate(), () -> "should return group with event node with date: " + groupNodeExpected.getEventsCaused().get(0).getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(), () -> "should return group with event node which was part of multiple incidents: " + groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group with event node which was successful: " + groupNodeExpected.getEventsCaused().get(0).getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuicidal(), groupNodeActual.getEventsCaused().get(0).getIsSuicidal(), () -> "should return group with event node which was suicidal: " + groupNodeExpected.getEventsCaused().get(0).getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupNodeExpected.getEventsCaused().get(0).getTarget(),
                        () -> "should return group with event node with not null target, but was: null"),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget(), () -> "should return group with event node with target: " + groupNodeExpected.getEventsCaused().get(0).getTarget() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget()),

                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getId(), groupNodeActual.getEventsCaused().get(1).getId(), () -> "should return group with event node with id: " + groupNodeExpected.getEventsCaused().get(1).getId() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getId()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getSummary(), groupNodeActual.getEventsCaused().get(1).getSummary(), () -> "should return group with event node with summary: " + groupNodeExpected.getEventsCaused().get(1).getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getSummary()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getMotive(), groupNodeActual.getEventsCaused().get(1).getMotive(), () -> "should return group with event node with motive: " + groupNodeExpected.getEventsCaused().get(1).getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getMotive()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getDate(), groupNodeActual.getEventsCaused().get(1).getDate(), () -> "should return group with event node with date: " + groupNodeExpected.getEventsCaused().get(1).getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getDate()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents(), () -> "should return group with event node which was part of multiple incidents: " + groupNodeExpected.getEventsCaused().get(1).getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getIsSuccessful(), groupNodeActual.getEventsCaused().get(1).getIsSuccessful(), () -> "should return group with event node which was successful: " + groupNodeExpected.getEventsCaused().get(1).getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuccessful()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getIsSuicidal(), groupNodeActual.getEventsCaused().get(1).getIsSuicidal(), () -> "should return group with event node which was suicidal: " + groupNodeExpected.getEventsCaused().get(1).getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuicidal()),
                () -> assertNotNull(groupNodeExpected.getEventsCaused().get(1).getTarget(),
                        () -> "should return group with event node with not null target, but was: null"),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getTarget(), groupNodeActual.getEventsCaused().get(1).getTarget(), () -> "should return group with event node with target: " + groupNodeExpected.getEventsCaused().get(1).getTarget() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget()),

                () -> verify(eventService, times(1)).delete(eventNode.getId()),
                () -> verify(eventService, times(1)).saveNew(eventDTO),
                () -> verify(eventService, times(1)).saveNew(eventDTO2),
                () -> verifyNoMoreInteractions(eventService),
                () -> verify(groupRepository, times(1)).save(groupNodeExpectedBeforeSetId),
                () -> verifyNoMoreInteractions(groupRepository),
                () -> verify(objectMapper, times(1)).map(groupDTOExpected, GroupNode.class),
                () -> verifyNoMoreInteractions(objectMapper));
    }

    @Test
    void when_delete_group_should_delete_group_with_events_and_targets() {

        Long groupId = 1L;
        Long eventId = 1L;
        Long eventId2 = 2L;

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        TargetNode targetNode2 = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withId(eventId).withTarget(targetNode).build(ObjectType.NODE);
        EventNode eventNodeExpected2 = (EventNode) eventBuilder.withId(eventId2).withTarget(targetNode2).build(ObjectType.NODE);
        GroupNode groupNodeExpected = (GroupNode) groupBuilder.withId(groupId).withEventsCaused(List.of(eventNodeExpected, eventNodeExpected2)).build(ObjectType.NODE);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(groupNodeExpected));
        when(eventService.delete(eventId)).thenReturn(Optional.of(eventNodeExpected));
        when(eventService.delete(eventId2)).thenReturn(Optional.of(eventNodeExpected2));

        Optional<GroupNode> groupNodeOptionalActual = groupService.delete(eventId);

        GroupNode groupNodeActual = groupNodeOptionalActual.get();

        assertAll(
                () -> assertNotNull(groupNodeActual.getId(),
                        () -> "should return group node with id, but was: " + groupNodeActual.getId()),
                () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                        () -> "should return group node with name: " + groupNodeExpected.getName() + ", but was: "
                                + groupNodeActual.getName()),

                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getId(), groupNodeActual.getEventsCaused().get(0).getId(), () -> "should return group event node with id: " + groupNodeExpected.getEventsCaused().get(0).getId() + ", but was: "
                        + groupNodeActual.getEventsCaused().get(0).getId()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group event node with summary: " + groupNodeExpected.getEventsCaused().get(0).getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(), () -> "should return group event node with motive: " + groupNodeExpected.getEventsCaused().get(0).getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getDate(), groupNodeActual.getEventsCaused().get(0).getDate(), () -> "should return group event node with date: " + groupNodeExpected.getEventsCaused().get(0).getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(), () -> "should return group event node which was part of multiple incidents: " + groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group event node which was successful: " + groupNodeExpected.getEventsCaused().get(0).getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuicidal(), groupNodeActual.getEventsCaused().get(0).getIsSuicidal(), () -> "should return group event node which was suicidal: " + groupNodeExpected.getEventsCaused().get(0).getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupNodeExpected.getEventsCaused().get(0).getTarget(),
                        () -> "should return group event node with not null target, but was: null"),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget(), () -> "should return group event node with target: " + groupNodeExpected.getEventsCaused().get(0).getTarget() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget()),

                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getId(), groupNodeActual.getEventsCaused().get(1).getId(), () -> "should return group event node with id: " + groupNodeExpected.getEventsCaused().get(1).getId() + ", but was: "
                        + groupNodeActual.getEventsCaused().get(1).getId()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getSummary(), groupNodeActual.getEventsCaused().get(1).getSummary(), () -> "should return group event node with summary: " + groupNodeExpected.getEventsCaused().get(1).getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getSummary()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getMotive(), groupNodeActual.getEventsCaused().get(1).getMotive(), () -> "should return group event node with motive: " + groupNodeExpected.getEventsCaused().get(1).getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getMotive()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getDate(), groupNodeActual.getEventsCaused().get(1).getDate(), () -> "should return group event node with date: " + groupNodeExpected.getEventsCaused().get(1).getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getDate()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents(), () -> "should return group event node which was part of multiple incidents: " + groupNodeExpected.getEventsCaused().get(1).getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getIsSuccessful(), groupNodeActual.getEventsCaused().get(1).getIsSuccessful(), () -> "should return group event node which was successful: " + groupNodeExpected.getEventsCaused().get(1).getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuccessful()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getIsSuicidal(), groupNodeActual.getEventsCaused().get(1).getIsSuicidal(), () -> "should return group event node which was suicidal: " + groupNodeExpected.getEventsCaused().get(1).getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuicidal()),
                () -> assertNotNull(groupNodeExpected.getEventsCaused().get(1).getTarget(),
                        () -> "should return group event node with not null target, but was: null"),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getTarget(), groupNodeActual.getEventsCaused().get(1).getTarget(), () -> "should return group event node with target: " + groupNodeExpected.getEventsCaused().get(1).getTarget() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget()),
                () -> verify(groupRepository, times(1)).findById(eventId),
                () -> verify(groupRepository, times(1)).delete(groupNodeExpected),
                () -> verifyNoMoreInteractions(groupRepository),
                () -> verify(eventService, times(1)).delete(eventId),
                () -> verify(eventService, times(1)).delete(eventId2),
                () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_delete_group_by_id_but_group_does_not_exist_should_return_empty_optional() {

        Long groupId = 1L;

        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        Optional<GroupNode> groupNodeOptional = groupService.delete(groupId);

        assertAll(
                () -> assertTrue(groupNodeOptional.isEmpty(),
                        () -> "should return empty group node optional, but was: " + groupNodeOptional.get()),
                () -> verify(groupRepository, times(1)).findById(groupId),
                () -> verifyNoMoreInteractions(groupRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(eventService));
    }

    @Test
    void when_add_event_to_group_should_return_group_with_new_event() {

        Long groupId = 1L;
        Long eventId = 1L;
        Long eventId2 = 2L;

        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);

        TargetNode targetNodeExpected = (TargetNode) targetBuilder.build(ObjectType.NODE);
        TargetNode newTargetNodeExpected = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withId(eventId).withTarget(targetNodeExpected).build(ObjectType.NODE);
        EventNode newEventNodeExpected = (EventNode) eventBuilder.withId(eventId2).withTarget(newTargetNodeExpected).build(ObjectType.NODE);
        GroupNode groupNodeExpectedBeforeSave = (GroupNode) groupBuilder.withId(groupId).withEventsCaused(List.of(eventNodeExpected)).build(ObjectType.NODE);
        GroupNode groupNodeExpected = (GroupNode) groupBuilder.withId(groupId).withEventsCaused(List.of(eventNodeExpected, newEventNodeExpected)).build(ObjectType.NODE);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(groupNodeExpectedBeforeSave));
        when(eventService.saveNew(eventDTO)).thenReturn(newEventNodeExpected);
        when(groupRepository.save(groupNodeExpectedBeforeSave)).thenReturn(groupNodeExpected);

        Optional<GroupNode> groupNodeActualOptional = groupService.addEventToGroup(groupId, eventDTO);

        GroupNode groupNodeActual = groupNodeActualOptional.get();

        assertAll(
                () -> assertNotNull(groupNodeActual.getId(),
                        () -> "should return group node with id, but was: " + groupNodeActual.getId()),
                () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                        () -> "should return group node with name: " + groupNodeExpected.getName() + ", but was: "
                                + groupNodeActual.getName()),

                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getId(), groupNodeActual.getEventsCaused().get(0).getId(), () -> "should return group event node with id: " + groupNodeExpected.getEventsCaused().get(0).getId() + ", but was: "
                        + groupNodeActual.getEventsCaused().get(0).getId()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group event node with summary: " + groupNodeExpected.getEventsCaused().get(0).getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(), () -> "should return group event node with motive: " + groupNodeExpected.getEventsCaused().get(0).getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getDate(), groupNodeActual.getEventsCaused().get(0).getDate(), () -> "should return group event node with date: " + groupNodeExpected.getEventsCaused().get(0).getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(), () -> "should return group event node which was part of multiple incidents: " + groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group event node which was successful: " + groupNodeExpected.getEventsCaused().get(0).getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuicidal(), groupNodeActual.getEventsCaused().get(0).getIsSuicidal(), () -> "should return group event node which was suicidal: " + groupNodeExpected.getEventsCaused().get(0).getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupNodeExpected.getEventsCaused().get(0).getTarget(),
                        () -> "should return group event node with not null target, but was: null"),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget(), () -> "should return group event node with target: " + groupNodeExpected.getEventsCaused().get(0).getTarget() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget()),

                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getId(), groupNodeActual.getEventsCaused().get(1).getId(), () -> "should return group event node with id: " + groupNodeExpected.getEventsCaused().get(1).getId() + ", but was: "
                        + groupNodeActual.getEventsCaused().get(1).getId()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getSummary(), groupNodeActual.getEventsCaused().get(1).getSummary(), () -> "should return group event node with summary: " + groupNodeExpected.getEventsCaused().get(1).getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getSummary()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getMotive(), groupNodeActual.getEventsCaused().get(1).getMotive(), () -> "should return group event node with motive: " + groupNodeExpected.getEventsCaused().get(1).getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getMotive()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getDate(), groupNodeActual.getEventsCaused().get(1).getDate(), () -> "should return group event node with date: " + groupNodeExpected.getEventsCaused().get(1).getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getDate()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents(), () -> "should return group event node which was part of multiple incidents: " + groupNodeExpected.getEventsCaused().get(1).getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getIsSuccessful(), groupNodeActual.getEventsCaused().get(1).getIsSuccessful(), () -> "should return group event node which was successful: " + groupNodeExpected.getEventsCaused().get(1).getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuccessful()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getIsSuicidal(), groupNodeActual.getEventsCaused().get(1).getIsSuicidal(), () -> "should return group event node which was suicidal: " + groupNodeExpected.getEventsCaused().get(1).getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuicidal()),
                () -> assertNotNull(groupNodeExpected.getEventsCaused().get(1).getTarget(),
                        () -> "should return group event node with not null target, but was: null"),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getTarget(), groupNodeActual.getEventsCaused().get(1).getTarget(), () -> "should return group event node with target: " + groupNodeExpected.getEventsCaused().get(1).getTarget() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget()),
                () -> verify(groupRepository, times(1)).findById(groupId),
                () -> verify(groupRepository, times(1)).save(groupNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(groupRepository),
                () -> verify(eventService, times(1)).saveNew(eventDTO),
                () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_add_event_to_group_but_group_does_not_exist_should_throw_error() {

        Long groupId = 1L;

        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);

        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        Optional<GroupNode> groupNodeOptional = groupService.addEventToGroup(groupId, eventDTO);

        assertAll(
                () -> assertTrue(groupNodeOptional.isEmpty(),
                        () -> "should return empty group node optional, but was: " + groupNodeOptional.get()),
                () -> verify(groupRepository, times(1)).findById(groupId),
                () -> verifyNoMoreInteractions(groupRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(eventService));
    }

    @Test
    void when_delete_group_events_should_delete_only_group_events_and_targets() {

        Long groupId = 1L;
        Long eventId = 1L;
        Long eventId2 = 2L;

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        TargetNode targetNode2 = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withId(eventId).withTarget(targetNode).build(ObjectType.NODE);
        EventNode eventNodeExpected2 = (EventNode) eventBuilder.withId(eventId2).withTarget(targetNode2).build(ObjectType.NODE);
        GroupNode groupNodeExpected = (GroupNode) groupBuilder.withId(groupId).withEventsCaused(List.of(eventNodeExpected, eventNodeExpected2)).build(ObjectType.NODE);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(groupNodeExpected));
        when(eventService.delete(eventId)).thenReturn(Optional.of(eventNodeExpected));
        when(eventService.delete(eventId2)).thenReturn(Optional.of(eventNodeExpected2));

        Optional<GroupNode> groupNodeOptionalActual = groupService.deleteAllGroupEvents(eventId);

        GroupNode groupNodeActual = groupNodeOptionalActual.get();

        assertAll(
                () -> assertNotNull(groupNodeActual.getId(),
                        () -> "should return group node with id, but was: " + groupNodeActual.getId()),
                () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                        () -> "should return group node with name: " + groupNodeExpected.getName() + ", but was: "
                                + groupNodeActual.getName()),

                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getId(), groupNodeActual.getEventsCaused().get(0).getId(), () -> "should return group event node with id: " + groupNodeExpected.getEventsCaused().get(0).getId() + ", but was: "
                        + groupNodeActual.getEventsCaused().get(0).getId()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group event node with summary: " + groupNodeExpected.getEventsCaused().get(0).getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(), () -> "should return group event node with motive: " + groupNodeExpected.getEventsCaused().get(0).getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getDate(), groupNodeActual.getEventsCaused().get(0).getDate(), () -> "should return group event node with date: " + groupNodeExpected.getEventsCaused().get(0).getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(), () -> "should return group event node which was part of multiple incidents: " + groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group event node which was successful: " + groupNodeExpected.getEventsCaused().get(0).getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuicidal(), groupNodeActual.getEventsCaused().get(0).getIsSuicidal(), () -> "should return group event node which was suicidal: " + groupNodeExpected.getEventsCaused().get(0).getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupNodeExpected.getEventsCaused().get(0).getTarget(),
                        () -> "should return group event node with not null target, but was: null"),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget(), () -> "should return group event node with target: " + groupNodeExpected.getEventsCaused().get(0).getTarget() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget()),

                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getId(), groupNodeActual.getEventsCaused().get(1).getId(), () -> "should return group event node with id: " + groupNodeExpected.getEventsCaused().get(1).getId() + ", but was: "
                        + groupNodeActual.getEventsCaused().get(1).getId()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getSummary(), groupNodeActual.getEventsCaused().get(1).getSummary(), () -> "should return group event node with summary: " + groupNodeExpected.getEventsCaused().get(1).getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getSummary()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getMotive(), groupNodeActual.getEventsCaused().get(1).getMotive(), () -> "should return group event node with motive: " + groupNodeExpected.getEventsCaused().get(1).getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getMotive()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getDate(), groupNodeActual.getEventsCaused().get(1).getDate(), () -> "should return group event node with date: " + groupNodeExpected.getEventsCaused().get(1).getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getDate()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents(), () -> "should return group event node which was part of multiple incidents: " + groupNodeExpected.getEventsCaused().get(1).getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getIsSuccessful(), groupNodeActual.getEventsCaused().get(1).getIsSuccessful(), () -> "should return group event node which was successful: " + groupNodeExpected.getEventsCaused().get(1).getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuccessful()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getIsSuicidal(), groupNodeActual.getEventsCaused().get(1).getIsSuicidal(), () -> "should return group event node which was suicidal: " + groupNodeExpected.getEventsCaused().get(1).getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuicidal()),
                () -> assertNotNull(groupNodeExpected.getEventsCaused().get(1).getTarget(),
                        () -> "should return group event node with not null target, but was: null"),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getTarget(), groupNodeActual.getEventsCaused().get(1).getTarget(), () -> "should return group event node with target: " + groupNodeExpected.getEventsCaused().get(1).getTarget() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget()),
                () -> verify(groupRepository, times(1)).findById(eventId),
                () -> verifyNoMoreInteractions(groupRepository),
                () -> verify(eventService, times(1)).delete(eventId),
                () -> verify(eventService, times(1)).delete(eventId2),
                () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_delete_group_events_by_id_but_group_does_not_exist_should_return_empty_optional() {

        Long groupId = 1L;

        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        Optional<GroupNode> groupNodeOptional = groupService.deleteAllGroupEvents(groupId);

        assertAll(
                () -> assertTrue(groupNodeOptional.isEmpty(),
                        () -> "should return empty group node optional, but was: " + groupNodeOptional.get()),
                () -> verify(groupRepository, times(1)).findById(groupId),
                () -> verifyNoMoreInteractions(groupRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(eventService));
    }
}
