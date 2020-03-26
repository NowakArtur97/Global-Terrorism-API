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
	@DisplayName("when map target node page to paged model with previous and next links")
	public void when_map_target_node_page_to_paged_with_previous_and_next_link_model_should_returnpaged_model_with_links() {

		int page = 1;
		int size = 1;

		List<TargetNode> targetsListExpected = createTargetNodesList(3);

		Page<TargetNode> targetsPage = createPageOfTargetNodes(targetsListExpected, page, size);

		PagedModel<TargetModel> targetsPagedModel = pagedResourcesAssembler.toModel(targetsPage, targetModelAssembler);
		System.out.println(targetsPagedModel);
		assertAll(
				() -> assertTrue(targetsPagedModel.hasLinks(),
						() -> "should have links, but haven`t: " + targetsPagedModel),
				() -> assertTrue(targetsPagedModel.getNextLink().isPresent(),
						() -> "should have next link, but haven`t: " + targetsPagedModel),
				() -> assertTrue(targetsPagedModel.getPreviousLink().isPresent(),
						() -> "should have prevoius link, but haven`t: " + targetsPagedModel));
	}

	private List<TargetNode> createTargetNodesList(int listSize) {

		Long targetId = 1L;
		String targetName = "target";

		List<TargetNode> targetsListExpected = new ArrayList<>();

		int count = 0;

		while (count < listSize) {

			TargetNode targetNode = new TargetNode(targetId, targetName + targetId);

			targetsListExpected.add(targetNode);

			targetId++;
			count++;
		}

		return targetsListExpected;
	}

	private Page<TargetNode> createPageOfTargetNodes(List<TargetNode> targetsListExpected, int page, int size) {

		Pageable pageRequest = PageRequest.of(page, size);

		Page<TargetNode> targetsExpected = new PageImpl<>(targetsListExpected, pageRequest, targetsListExpected.size());

		return targetsExpected;
	}
}
