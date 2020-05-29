package com.NowakArtur97.GlobalTerrorismAPI.service;

import com.NowakArtur97.GlobalTerrorismAPI.dto.GroupDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.GroupRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.service.impl.GroupServiceImpl;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.GroupBuilder;
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

    private GroupBuilder groupBuilder;
    private EventBuilder eventBuilder;

    @BeforeEach
    private void setUp() {

        groupService = new GroupServiceImpl(groupRepository, objectMapper);

        groupBuilder = new GroupBuilder();
        eventBuilder = new EventBuilder();
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
}
