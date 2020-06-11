package com.NowakArtur97.GlobalTerrorismAPI.service;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.GroupDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.GroupRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.service.impl.GroupServiceImpl;
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

    private GenericService<GroupNode, GroupDTO> groupService;

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

        GroupNode groupExpected = new GroupNode("group");

        when(groupRepository.findById(expectedGroupId)).thenReturn(Optional.of(groupExpected));

        Optional<GroupNode> groupActualOptional = groupService.findById(expectedGroupId);

        GroupNode groupActual = groupActualOptional.get();

        assertAll(() -> assertTrue(groupActualOptional.isPresent(), () -> "shouldn`t return empty optional"),
                () -> assertEquals(groupExpected.getId(), groupActual.getId(),
                        () -> "should return group with id: " + expectedGroupId + ", but was" + groupActual.getId()),
                () -> assertEquals(groupExpected.getName(), groupActual.getName(),
                        () -> "should return group with name: " + groupExpected.getName() + ", but was"
                                + groupActual.getName()),
                () -> verify(groupRepository, times(1)).findById(expectedGroupId),
                () -> verifyNoMoreInteractions(objectMapper), () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_group_not_exists_and_return_one_group_should_return_empty_optional() {

        Long expectedGroupId = 1L;

        when(groupRepository.findById(expectedGroupId)).thenReturn(Optional.empty());

        Optional<GroupNode> groupActualOptional = groupService.findById(expectedGroupId);

        assertAll(() -> assertTrue(groupActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(groupRepository, times(1)).findById(expectedGroupId),
                () -> verifyNoMoreInteractions(groupRepository), () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_delete_group_should_delete_group_with_event_and_target() {

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
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuicide(), groupNodeActual.getEventsCaused().get(0).getIsSuicide(), () -> "should return group event node which was suicide: " + groupNodeExpected.getEventsCaused().get(0).getIsSuicide() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicide()),
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
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getIsSuicide(), groupNodeActual.getEventsCaused().get(1).getIsSuicide(), () -> "should return group event node which was suicide: " + groupNodeExpected.getEventsCaused().get(1).getIsSuicide() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuicide()),
                () -> assertNotNull(groupNodeExpected.getEventsCaused().get(1).getTarget(),
                        () -> "should return group event node with not null target, but was: null"),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getTarget(), groupNodeActual.getEventsCaused().get(1).getTarget(), () -> "should return group event node with target: " + groupNodeExpected.getEventsCaused().get(1).getTarget() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget()),

                () -> verify(groupRepository, times(1)).findById(eventId),
                () -> verify(groupRepository, times(1)).delete(groupNodeExpected),
                () -> verifyNoMoreInteractions(groupRepository), () -> verify(eventService, times(1)).delete(eventId), () -> verify(eventService, times(1)).delete(eventId2),
                () -> verifyNoMoreInteractions(eventService), () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_delete_group_by_id_but_group_not_exists_should_return_empty_optional() {

        Long groupId = 1L;

        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        Optional<GroupNode> groupNodeOptional = groupService.delete(groupId);

        assertAll(
                () -> assertTrue(groupNodeOptional.isEmpty(),
                        () -> "should return empty group node optional, but was: " + groupNodeOptional.get()),
                () -> verify(groupRepository, times(1)).findById(groupId),
                () -> verifyNoMoreInteractions(groupRepository), () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(eventService));
    }
}
