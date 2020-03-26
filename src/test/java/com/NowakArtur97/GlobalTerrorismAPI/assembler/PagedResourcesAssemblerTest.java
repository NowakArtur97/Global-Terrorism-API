package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;

@DisplayName("Paged Resources Assembler Tests")
@Tag("PagedResourcesAssembler_Tests")
public class PagedResourcesAssemblerTest {

	private PagedResourcesAssembler<TargetNode> pagedResourcesAssembler;

	private HateoasPageableHandlerMethodArgumentResolver resolver;

	private TargetModelAssembler targetModelAssembler;

	@BeforeAll
	public static void innit() {

		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
	}

	@BeforeEach
	public void setUp() {

		resolver = new HateoasPageableHandlerMethodArgumentResolver();

		targetModelAssembler = new TargetModelAssembler();

		pagedResourcesAssembler = new PagedResourcesAssembler<>(resolver, null);
	}

	@AfterAll
	public static void tearDown() {

		RequestContextHolder.resetRequestAttributes();
	}

	@Test
	@DisplayName("when map target node page to paged model")
	public void when_map_target_node_page_to_paged_model() {

		Long targetId1 = 1L;
		String targetName1 = "target1";
		TargetNode targetNode1 = new TargetNode(targetId1, targetName1);

		Long targetId2 = 2L;
		String targetName2 = "target2";
		TargetNode targetNode2 = new TargetNode(targetId2, targetName2);

		Long targetId3 = 3L;
		String targetName3 = "target3";
		TargetNode targetNode3 = new TargetNode(targetId3, targetName3);

		Long targetId4 = 4L;
		String targetName4 = "target4";
		TargetNode targetNode4 = new TargetNode(targetId4, targetName4);

		List<TargetNode> targetsListExpected = new ArrayList<>();
		targetsListExpected.add(targetNode1);
		targetsListExpected.add(targetNode2);
		targetsListExpected.add(targetNode3);
		targetsListExpected.add(targetNode4);

		Pageable request = PageRequest.of(1, 1);

		Page<TargetNode> targetsExpected = new PageImpl<>(targetsListExpected, request, 4);

		PagedModel<TargetModel> targetsPage = pagedResourcesAssembler.toModel(targetsExpected, targetModelAssembler);

		assertAll(() -> assertTrue(targetsPage.hasLinks(), () -> "should have links, but haven`t: " + targetsPage),
				() -> assertTrue(targetsPage.getNextLink().isPresent(),
						() -> "should have next link, but haven`t: " + targetsPage),
				() -> assertTrue(targetsPage.getPreviousLink().isPresent(),
						() -> "should have prevoius link, but haven`t: " + targetsPage));
	}
}
