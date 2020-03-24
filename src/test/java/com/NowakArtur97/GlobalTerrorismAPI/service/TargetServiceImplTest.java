package com.NowakArtur97.GlobalTerrorismAPI.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.TargetRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;
import com.NowakArtur97.GlobalTerrorismAPI.service.impl.TargetServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("TargetNode Service Impl Tests")
@Tag("TargetServiceImpl_Tests")
public class TargetServiceImplTest {

	private TargetService targetService;

	@Mock
	private TargetRepository targetRepository;

	@BeforeEach
	void setUp() {

		targetService = new TargetServiceImpl(targetRepository);
	}

	@Test
	@DisplayName("when targets exist and return all targets")
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
				() -> assertEquals(targetsActual.getContent(), targetsListExpected,
						() -> "should contain: " + targetsListExpected + ", but was: " + targetsActual.getContent()),
				() -> assertEquals(targetsExpected.getNumberOfElements(), targetsActual.getNumberOfElements(),
						() -> "should return page with: " + targetsExpected.getNumberOfElements()
								+ " elements, but was: " + targetsActual.getNumberOfElements()));
	}

	@Test
	@DisplayName("when targets not exist and return all targets")
	public void when_targets_not_exist_and_return_all_targets_should_not_return_any_targets() {

		List<TargetNode> targetsListExpected = new ArrayList<>();

		Page<TargetNode> targetsExpected = new PageImpl<>(targetsListExpected);
		
		Pageable pageable = PageRequest.of(0, 100);

		when(targetRepository.findAll(pageable)).thenReturn(targetsExpected);

		Page<TargetNode> targetsActual = targetService.findAll(pageable);

		assertAll(() -> assertNotNull(targetsActual, () -> "shouldn`t return null"),
				() -> assertEquals(targetsActual.getContent(), targetsListExpected,
						() -> "should contain empty list, but was: " + targetsActual.getContent()),
				() -> assertEquals(targetsActual.getContent(), targetsListExpected,
						() -> "should contain: " + targetsListExpected + ", but was: " + targetsActual.getContent()),
				() -> assertEquals(targetsExpected.getNumberOfElements(), targetsActual.getNumberOfElements(),
						() -> "should return empty page, but was: " + targetsActual.getNumberOfElements()));
	}
}
