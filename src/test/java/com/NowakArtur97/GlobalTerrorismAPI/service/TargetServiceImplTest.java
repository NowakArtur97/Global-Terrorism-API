package com.NowakArtur97.GlobalTerrorismAPI.service;

import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.TargetRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;
import com.NowakArtur97.GlobalTerrorismAPI.service.impl.TargetServiceImpl;
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
@Tag("TargetServiceImpl_Tests")
class TargetServiceImplTest {

    private static final int DEFAULT_SEARCHING_DEPTH = 1;

    private TargetService targetService;

    @Mock
    private TargetRepository targetRepository;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    private void setUp() {

        targetService = new TargetServiceImpl(targetRepository, objectMapper);
    }

    @Test
    void when_targets_exist_and_return_all_targets_should_return_targets() {

        List<TargetNode> targetsListExpected = new ArrayList<>();

        TargetNode target1 = new TargetNode("target1");
        TargetNode target2 = new TargetNode("target2");
        TargetNode target3 = new TargetNode("target3");

        targetsListExpected.add(target1);
        targetsListExpected.add(target2);
        targetsListExpected.add(target3);

        Page<TargetNode> targetsExpected = new PageImpl<>(targetsListExpected);

        Pageable pageable = PageRequest.of(0, 100);

        when(targetRepository.findAll(pageable, DEFAULT_SEARCHING_DEPTH)).thenReturn(targetsExpected);

        Page<TargetNode> targetsActual = targetService.findAll(pageable);

        assertAll(() -> assertNotNull(targetsActual, () -> "shouldn`t return null"),
                () -> assertEquals(targetsListExpected, targetsActual.getContent(),
                        () -> "should contain: " + targetsListExpected + ", but was: " + targetsActual.getContent()),
                () -> assertEquals(targetsExpected.getNumberOfElements(), targetsActual.getNumberOfElements(),
                        () -> "should return page with: " + targetsExpected.getNumberOfElements()
                                + " elements, but was: " + targetsActual.getNumberOfElements()),
                () -> verify(targetRepository, times(1)).findAll(pageable, DEFAULT_SEARCHING_DEPTH),
                () -> verifyNoMoreInteractions(targetRepository), () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_targets_not_exist_and_return_all_targets_should_not_return_any_targets() {

        List<TargetNode> targetsListExpected = new ArrayList<>();

        Page<TargetNode> targetsExpected = new PageImpl<>(targetsListExpected);

        Pageable pageable = PageRequest.of(0, 100);

        when(targetRepository.findAll(pageable, DEFAULT_SEARCHING_DEPTH)).thenReturn(targetsExpected);

        Page<TargetNode> targetsActual = targetService.findAll(pageable);

        assertAll(() -> assertNotNull(targetsActual, () -> "shouldn`t return null"),
                () -> assertEquals(targetsListExpected, targetsActual.getContent(),
                        () -> "should contain empty list, but was: " + targetsActual.getContent()),
                () -> assertEquals(targetsListExpected, targetsActual.getContent(),
                        () -> "should contain: " + targetsListExpected + ", but was: " + targetsActual.getContent()),
                () -> assertEquals(targetsExpected.getNumberOfElements(), targetsActual.getNumberOfElements(),
                        () -> "should return empty page, but was: " + targetsActual.getNumberOfElements()),
                () -> verify(targetRepository, times(1)).findAll(pageable, DEFAULT_SEARCHING_DEPTH),
                () -> verifyNoMoreInteractions(targetRepository), () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_target_exists_and_return_one_target_should_return_one_target() {

        Long expectedTargetId = 1L;

        TargetNode targetExpected = new TargetNode("target");

        when(targetRepository.findById(expectedTargetId)).thenReturn(Optional.of(targetExpected));

        Optional<TargetNode> targetActualOptional = targetService.findById(expectedTargetId);

        TargetNode targetActual = targetActualOptional.get();

        assertAll(() -> assertTrue(targetActualOptional.isPresent(), () -> "shouldn`t return empty optional"),
                () -> assertEquals(targetExpected.getId(), targetActual.getId(),
                        () -> "should return target with id: " + expectedTargetId + ", but was" + targetActual.getId()),
                () -> assertEquals(targetExpected.getTarget(), targetActual.getTarget(),
                        () -> "should return target with target: " + targetExpected.getTarget() + ", but was"
                                + targetActual.getTarget()),
                () -> verify(targetRepository, times(1)).findById(expectedTargetId),
                () -> verifyNoMoreInteractions(objectMapper), () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_target_not_exists_and_return_one_target_should_return_empty_optional() {

        Long expectedTargetId = 1L;

        when(targetRepository.findById(expectedTargetId)).thenReturn(Optional.empty());

        Optional<TargetNode> targetActualOptional = targetService.findById(expectedTargetId);

        assertAll(() -> assertTrue(targetActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(targetRepository, times(1)).findById(expectedTargetId),
                () -> verifyNoMoreInteractions(targetRepository), () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_save_new_target_should_save_target() {

        Long targetId = 1L;

        String targetName = "Target";

        TargetDTO targetDTOExpected = new TargetDTO(targetName);

        TargetNode targetNodeExpectedBeforeSave = new TargetNode(null, targetName);
        TargetNode targetNodeExpected = new TargetNode(targetId, targetName);

        when(objectMapper.map(targetDTOExpected, TargetNode.class)).thenReturn(targetNodeExpectedBeforeSave);
        when(targetRepository.save(targetNodeExpectedBeforeSave)).thenReturn(targetNodeExpected);

        TargetNode targetNodeActual = targetService.saveNew(targetDTOExpected);

        assertAll(
                () -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
                        () -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + targetNodeActual.getTarget()),
                () -> assertNotNull(targetNodeActual.getId(),
                        () -> "should return target node with new id, but was: " + targetNodeActual.getId()),
                () -> verify(objectMapper, times(1)).map(targetDTOExpected, TargetNode.class),
                () -> verifyNoMoreInteractions(objectMapper),
                () -> verify(targetRepository, times(1)).save(targetNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(targetRepository));
    }

    @Test
    void when_update_target_should_update_target() {

        Long targetId = 1L;

        String targetName = "Target";
        String targetNameUpdated = "Target";

        TargetDTO targetDTOExpected = new TargetDTO(targetName);

        TargetNode targetNodeExpectedAfterMapping = new TargetNode(null, targetName);
        TargetNode targetNodeExpectedAfterUpdate = new TargetNode(targetId, targetNameUpdated);

        when(objectMapper.map(targetDTOExpected, TargetNode.class)).thenReturn(targetNodeExpectedAfterMapping);
        when(targetRepository.save(targetNodeExpectedAfterMapping)).thenReturn(targetNodeExpectedAfterUpdate);

        TargetNode targetNodeActual = targetService.update(targetNodeExpectedAfterMapping, targetDTOExpected);

        assertAll(
                () -> assertEquals(targetNodeExpectedAfterUpdate.getId(), targetNodeActual.getId(),
                        () -> "should return target node with id: " + targetNodeExpectedAfterUpdate.getId() + ", but was: "
                                + targetNodeActual.getId()),
                () -> assertEquals(targetNodeExpectedAfterUpdate.getTarget(), targetNodeActual.getTarget(),
                        () -> "should return target node with target: " + targetNodeExpectedAfterUpdate.getTarget() + ", but was: "
                                + targetNodeActual.getTarget()),
                () -> verify(objectMapper, times(1)).map(targetDTOExpected, TargetNode.class),
                () -> verifyNoMoreInteractions(objectMapper),
                () -> verify(targetRepository, times(1)).save(targetNodeExpectedAfterMapping),
                () -> verifyNoMoreInteractions(targetRepository));
    }

    @Test
    void when_save_target_should_save_target() {

        Long targetId = 1L;

        String targetName = "Target";

        TargetNode targetNodeExpectedBeforeSave = new TargetNode(null, targetName);
        TargetNode targetNodeExpected = new TargetNode(targetId, targetName);

        when(targetRepository.save(targetNodeExpectedBeforeSave)).thenReturn(targetNodeExpected);

        TargetNode targetNodeActual = targetService.save(targetNodeExpectedBeforeSave);

        assertAll(
                () -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
                        () -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + targetNodeActual.getTarget()),
                () -> assertNotNull(targetNodeActual.getId(),
                        () -> "should return target node with new id, but was: " + targetNodeActual.getId()),
                () -> verify(targetRepository, times(1)).save(targetNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(targetRepository), () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_delete_target_by_id_target_should_delete_and_return_target() {

        String targetName = "Target";

        Long targetId = 1L;

        TargetNode targetNodeExpected = new TargetNode(targetId, targetName);

        when(targetRepository.findById(targetId)).thenReturn(Optional.of(targetNodeExpected));

        Optional<TargetNode> targetNodeOptional = targetService.delete(targetId);

        TargetNode targetNodeActual = targetNodeOptional.get();

        assertAll(
                () -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
                        () -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + targetNodeActual.getTarget()),
                () -> verify(targetRepository, times(1)).findById(targetId),
                () -> verify(targetRepository, times(1)).delete(targetNodeActual),
                () -> verifyNoMoreInteractions(targetRepository), () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_delete_target_by_id_not_existing_target_should_return_empty_optional() {

        Long targetId = 1L;

        when(targetRepository.findById(targetId)).thenReturn(Optional.empty());

        Optional<TargetNode> targetNodeOptional = targetService.delete(targetId);

        assertAll(
                () -> assertTrue(targetNodeOptional.isEmpty(),
                        () -> "should return empty target node optional, but was: " + targetNodeOptional.get()),
                () -> verify(targetRepository, times(1)).findById(targetId),
                () -> verifyNoMoreInteractions(targetRepository), () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_checking_if_database_is_empty_and_it_is_empty_should_return_true() {

        Long databaseSize = 0L;

        when(targetRepository.count()).thenReturn(databaseSize);

        boolean isDatabaseEmpty = targetService.isDatabaseEmpty();

        assertAll(() -> assertTrue(isDatabaseEmpty, () -> "should database be empty, but that was: " + isDatabaseEmpty),
                () -> verify(targetRepository, times(1)).count(), () -> verifyNoMoreInteractions(targetRepository),
                () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_checking_if_database_is_empty_and_it_is_not_empty_should_return_false() {

        Long databaseSize = 10L;

        when(targetRepository.count()).thenReturn(databaseSize);

        boolean isDatabaseEmpty = targetService.isDatabaseEmpty();

        assertAll(
                () -> assertFalse(isDatabaseEmpty,
                        () -> "should not database be empty, but that was: " + isDatabaseEmpty),
                () -> verify(targetRepository, times(1)).count(), () -> verifyNoMoreInteractions(targetRepository),
                () -> verifyNoInteractions(objectMapper));
    }
}
