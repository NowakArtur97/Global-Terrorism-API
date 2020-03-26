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

	private final String BASE_URL = "http://localhost";

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
	@DisplayName("when map target node page to paged model without targets")
	public void when_map_target_node_page_to_paged_model_without_targets_should_return_empty_page() {

		int page = 0;
		int size = 1;
		int listSize = 0;

		List<TargetNode> targetsListExpected = createTargetNodesList(listSize);

		Page<TargetNode> targetsPage = createPageOfTargetNodes(targetsListExpected, page, size);

		PagedModel<TargetModel> targetsPagedModel = pagedResourcesAssembler.toModel(targetsPage, targetModelAssembler);

		String selfLink = getLink(page, size);

		assertAll(
				() -> assertTrue(targetsPagedModel.hasLinks(),
						() -> "should have links, but haven`t: " + targetsPagedModel),
				() -> assertTrue(targetsPagedModel.getLink("first").isEmpty(),
						() -> "should not have first link, but had: "
								+ targetsPagedModel.getLink("first").get().getHref()),
				() -> assertTrue(targetsPagedModel.getLink("self").isPresent(),
						() -> "should have self link, but haven`t: " + targetsPagedModel),
				() -> assertTrue(targetsPagedModel.getLink("self").get().getHref().equals(selfLink),
						() -> "should have self link with url: " + selfLink + ", but had: "
								+ targetsPagedModel.getLink("self").get().getHref()),
				() -> assertTrue(targetsPagedModel.getNextLink().isEmpty(),
						() -> "should not have next link, but had: " + targetsPagedModel.getNextLink().get().getHref()),
				() -> assertTrue(targetsPagedModel.getPreviousLink().isEmpty(),
						() -> "should not have previous link, but had: "
								+ targetsPagedModel.getPreviousLink().get().getHref()),
				() -> assertTrue(targetsPagedModel.getLink("last").isEmpty(),
						() -> "should not have last link, but had: "
								+ targetsPagedModel.getLink("last").get().getHref()));
	}

	@Test
	@DisplayName("when map target node page to paged model on first page")
	public void when_map_target_node_page_to_paged_model_on_first_page_should_return_paged_model_with_links() {

		int page = 0;
		int size = 1;
		int listSize = 3;

		List<TargetNode> targetsListExpected = createTargetNodesList(listSize);

		Page<TargetNode> targetsPage = createPageOfTargetNodes(targetsListExpected, page, size);

		PagedModel<TargetModel> targetsPagedModel = pagedResourcesAssembler.toModel(targetsPage, targetModelAssembler);

		String firstLink = getLink(0, size);
		String selfLink = getLink(page, size);
		String nextLink = getLink(page + 1, size);
		String lastLink = getLink(listSize / size - 1, size);

		assertAll(
				() -> assertTrue(targetsPagedModel.hasLinks(),
						() -> "should have links, but haven`t: " + targetsPagedModel),
				() -> assertTrue(targetsPagedModel.getLink("first").isPresent(),
						() -> "should have first link, but haven`t: " + targetsPagedModel),
				() -> assertTrue(targetsPagedModel.getLink("first").get().getHref().equals(firstLink),
						() -> "should have first link with url: " + firstLink + ", but had: "
								+ targetsPagedModel.getLink("first").get().getHref()),
				() -> assertTrue(targetsPagedModel.getLink("self").isPresent(),
						() -> "should have self link, but haven`t: " + targetsPagedModel),
				() -> assertTrue(targetsPagedModel.getLink("self").get().getHref().equals(selfLink),
						() -> "should have self link with url: " + selfLink + ", but had: "
								+ targetsPagedModel.getLink("self").get().getHref()),
				() -> assertTrue(targetsPagedModel.getNextLink().isPresent(),
						() -> "should have next link, but haven`t: " + targetsPagedModel),
				() -> assertTrue(targetsPagedModel.getNextLink().get().getHref().equals(nextLink),
						() -> "should have next link with url:" + nextLink + ", but had: "
								+ targetsPagedModel.getNextLink().get().getHref()),
				() -> assertTrue(targetsPagedModel.getPreviousLink().isEmpty(),
						() -> "should not have previous link, but had: "
								+ targetsPagedModel.getPreviousLink().get().getHref()),
				() -> assertTrue(targetsPagedModel.getLink("last").isPresent(),
						() -> "should have last link, but haven`t: " + targetsPagedModel),
				() -> assertTrue(targetsPagedModel.getLink("last").get().getHref().equals(lastLink),
						() -> "should have last link with url: " + lastLink + ", but had: "
								+ targetsPagedModel.getLink("last").get().getHref()));
	}

	@Test
	@DisplayName("when map target node page to paged model on lasst page")
	public void when_map_target_node_page_to_paged_model_on_last_page_should_return_paged_model_with_links() {

		int page = 2;
		int size = 1;
		int listSize = 3;

		List<TargetNode> targetsListExpected = createTargetNodesList(listSize);

		Page<TargetNode> targetsPage = createPageOfTargetNodes(targetsListExpected, page, size);

		PagedModel<TargetModel> targetsPagedModel = pagedResourcesAssembler.toModel(targetsPage, targetModelAssembler);

		String firstLink = getLink(0, size);
		String selfLink = getLink(page, size);
		String previousLink = getLink(page - 1, size);
		String lastLink = getLink(listSize / size - 1, size);

		assertAll(
				() -> assertTrue(targetsPagedModel.hasLinks(),
						() -> "should have links, but haven`t: " + targetsPagedModel),
				() -> assertTrue(targetsPagedModel.getLink("first").isPresent(),
						() -> "should have first link, but haven`t: " + targetsPagedModel),
				() -> assertTrue(targetsPagedModel.getLink("first").get().getHref().equals(firstLink),
						() -> "should have first link with url: " + firstLink + ", but had: "
								+ targetsPagedModel.getLink("first").get().getHref()),
				() -> assertTrue(targetsPagedModel.getLink("self").isPresent(),
						() -> "should have self link, but haven`t: " + targetsPagedModel),
				() -> assertTrue(targetsPagedModel.getLink("self").get().getHref().equals(selfLink),
						() -> "should have self link with url: " + selfLink + ", but had: "
								+ targetsPagedModel.getLink("self").get().getHref()),
				() -> assertTrue(targetsPagedModel.getNextLink().isEmpty(),
						() -> "should not have next link, but had: " + targetsPagedModel.getNextLink().get().getHref()),
				() -> assertTrue(targetsPagedModel.getPreviousLink().isPresent(),
						() -> "should have previous link, but haven`t: " + targetsPagedModel),
				() -> assertTrue(targetsPagedModel.getPreviousLink().get().getHref().equals(previousLink),
						() -> "should have previous link with url:" + previousLink + ", but had: "
								+ targetsPagedModel.getPreviousLink().get().getHref()),
				() -> assertTrue(targetsPagedModel.getLink("last").isPresent(),
						() -> "should have last link, but haven`t: " + targetsPagedModel),
				() -> assertTrue(targetsPagedModel.getLink("last").get().getHref().equals(lastLink),
						() -> "should have last link with url: " + lastLink + ", but had: "
								+ targetsPagedModel.getLink("last").get().getHref()));
	}

	@Test
	@DisplayName("when map target node page to paged model with previous and next links")
	public void when_map_target_node_page_to_paged_model_with_previous_and_next_link_should_return_paged_model_with_links() {

		int page = 1;
		int size = 1;
		int listSize = 3;

		List<TargetNode> targetsListExpected = createTargetNodesList(listSize);

		Page<TargetNode> targetsPage = createPageOfTargetNodes(targetsListExpected, page, size);

		PagedModel<TargetModel> targetsPagedModel = pagedResourcesAssembler.toModel(targetsPage, targetModelAssembler);

		String firstLink = getLink(0, size);
		String selfLink = getLink(page, size);
		String previousLink = getLink(page - 1, size);
		String nextLink = getLink(page + 1, size);
		String lastLink = getLink(listSize / size - 1, size);

		assertAll(
				() -> assertTrue(targetsPagedModel.hasLinks(),
						() -> "should have links, but haven`t: " + targetsPagedModel),
				() -> assertTrue(targetsPagedModel.getLink("first").isPresent(),
						() -> "should have first link, but haven`t: " + targetsPagedModel),
				() -> assertTrue(targetsPagedModel.getLink("first").get().getHref().equals(firstLink),
						() -> "should have first link with url: " + firstLink + ", but had: "
								+ targetsPagedModel.getLink("first").get().getHref()),
				() -> assertTrue(targetsPagedModel.getLink("self").isPresent(),
						() -> "should have self link, but haven`t: " + targetsPagedModel),
				() -> assertTrue(targetsPagedModel.getLink("self").get().getHref().equals(selfLink),
						() -> "should have self link with url: " + selfLink + ", but had: "
								+ targetsPagedModel.getLink("self").get().getHref()),
				() -> assertTrue(targetsPagedModel.getNextLink().isPresent(),
						() -> "should have next link, but haven`t: " + targetsPagedModel),
				() -> assertTrue(targetsPagedModel.getNextLink().get().getHref().equals(nextLink),
						() -> "should have next link with url:" + nextLink + ", but had: "
								+ targetsPagedModel.getNextLink().get().getHref()),
				() -> assertTrue(targetsPagedModel.getPreviousLink().isPresent(),
						() -> "should have previous link, but haven`t: " + targetsPagedModel),
				() -> assertTrue(targetsPagedModel.getPreviousLink().get().getHref().equals(previousLink),
						() -> "should have previous link with url:" + previousLink + ", but had: "
								+ targetsPagedModel.getPreviousLink().get().getHref()),
				() -> assertTrue(targetsPagedModel.getLink("last").isPresent(),
						() -> "should have last link, but haven`t: " + targetsPagedModel),
				() -> assertTrue(targetsPagedModel.getLink("last").get().getHref().equals(lastLink),
						() -> "should have last link with url: " + lastLink + ", but had: "
								+ targetsPagedModel.getLink("last").get().getHref()));
	}

	private String getLink(int page, int size) {

		return new StringBuilder(BASE_URL).append("?page=").append(page).append("&size=").append(size).toString();
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
