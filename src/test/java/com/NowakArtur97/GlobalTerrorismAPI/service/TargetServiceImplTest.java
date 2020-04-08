package com.NowakArtur97.GlobalTerrorismAPI.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.TargetMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.TargetRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;
import com.NowakArtur97.GlobalTerrorismAPI.service.impl.TargetServiceImpl;
import com.NowakArtur97.GlobalTerrorismAPI.testUtils.NameWithSpacesGenerator;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("TargetServiceImpl_Tests")
public class TargetServiceImplTest {

	private TargetService targetService;

	@Mock
	private TargetRepository targetRepository;

	@Mock
	private TargetMapper targetMapper;

	@BeforeEach
	void setUp() {

		targetService = new TargetServiceImpl(targetRepository, targetMapper);
	}

	@Test
	public void when_targets_exist_and_return_all_targets_should_return_targets() {

		List<TargetNode> targetsListExpected = new ArrayList<>();

		TargetNode target1 = new TargetNode("target1");
		TargetNode target2 = new TargetNode("target2");
		TargetNode target3 = new TargetNode("target3");

		targetsListExpected.add(target1);
		targetsListExpected.add(target2);
		targetsListExpected.add(target3);

		Page<TargetNode> targetsExpected = new PageImpl<>(targetsListExpected);

		Pageable pageable = PageRequest.of(0, 100);

		when(targetRepository.findAll(pageable)).thenReturn(targetsExpected);

		Page<TargetNode> targetsActual = targetService.findAll(pageable);

		assertAll(() -> assertNotNull(targetsActual, () -> "shouldn`t return null"),
				() -> assertEquals(targetsListExpected, targetsActual.getContent(),
						() -> "should contain: " + targetsListExpected + ", but was: " + targetsActual.getContent()),
				() -> assertEquals(targetsExpected.getNumberOfElements(), targetsActual.getNumberOfElements(),
						() -> "should return page with: " + targetsExpected.getNumberOfElements()
								+ " elements, but was: " + targetsActual.getNumberOfElements()),
				() -> verify(targetRepository, times(1)).findAll(pageable));
	}

	@Test
	public void when_targets_not_exist_and_return_all_targets_should_not_return_any_targets() {

		List<TargetNode> targetsListExpected = new ArrayList<>();

		Page<TargetNode> targetsExpected = new PageImpl<>(targetsListExpected);

		Pageable pageable = PageRequest.of(0, 100);

		when(targetRepository.findAll(pageable)).thenReturn(targetsExpected);

		Page<TargetNode> targetsActual = targetService.findAll(pageable);

		assertAll(() -> assertNotNull(targetsActual, () -> "shouldn`t return null"),
				() -> assertEquals(targetsListExpected, targetsActual.getContent(),
						() -> "should contain empty list, but was: " + targetsActual.getContent()),
				() -> assertEquals(targetsListExpected, targetsActual.getContent(),
						() -> "should contain: " + targetsListExpected + ", but was: " + targetsActual.getContent()),
				() -> assertEquals(targetsExpected.getNumberOfElements(), targetsActual.getNumberOfElements(),
						() -> "should return empty page, but was: " + targetsActual.getNumberOfElements()),
				() -> verify(targetRepository, times(1)).findAll(pageable));
	}

	@Test
	public void when_targets_exists_and_return_one_target_should_return_one_target() {

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
				() -> verify(targetRepository, times(1)).findById(expectedTargetId));
	}

	@Test
	public void when_targets_not_exists_and_return_one_target_should_return_empty_optional() {

		Long expectedTargetId = 1L;

		when(targetRepository.findById(expectedTargetId)).thenReturn(Optional.empty());

		Optional<TargetNode> targetActualOptional = targetService.findById(expectedTargetId);

		assertAll(() -> assertTrue(targetActualOptional.isEmpty(), () -> "should return empty optional"),
				() -> verify(targetRepository, times(1)).findById(expectedTargetId));
	}

	@Test
	public void when_save_new_target_should_save_target() {

		String targetName = "Target";

		TargetDTO targetDTOExpected = new TargetDTO(null, targetName);

		TargetNode targetNodeExpected = new TargetNode(targetName);

		when(targetMapper.mapDTOToNode(targetDTOExpected)).thenReturn(targetNodeExpected);
		when(targetRepository.save(targetNodeExpected)).thenReturn(targetNodeExpected);

		TargetNode targetNodeActual = targetService.save(targetDTOExpected);

		assertAll(
				() -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
						() -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: "
								+ targetNodeActual.getTarget()),
				() -> assertNotNull(targetNodeActual.getTarget(),
						() -> "should return target node with new id, but was: " + targetNodeActual.getId()),
				() -> verify(targetMapper, times(1)).mapDTOToNode(targetDTOExpected),
				() -> verify(targetRepository, times(1)).save(targetNodeExpected));
	}

	@Test
	public void when_delete_target_by_id_target_should_delete_and_return_target() {

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
				() -> verify(targetRepository, times(1)).delete(targetNodeActual));
	}

	@Test
	public void when_delete_target_by_id_not_existing_target_should_return_empty_optional() {

		Long targetId = 1L;

		when(targetRepository.findById(targetId)).thenReturn(Optional.empty());

		Optional<TargetNode> targetNodeOptional = targetService.delete(targetId);

		assertAll(
				() -> assertTrue(targetNodeOptional.isEmpty(),
						() -> "should return empty target node optional, but was: " + targetNodeOptional.get()),
				() -> verify(targetRepository, times(1)).findById(targetId),
				() -> verifyNoMoreInteractions(targetRepository));
	}
}
