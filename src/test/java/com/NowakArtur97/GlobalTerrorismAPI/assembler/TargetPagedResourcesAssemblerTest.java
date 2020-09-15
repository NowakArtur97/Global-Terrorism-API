package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("PagedResourcesAssembler_Tests")
class TargetPagedResourcesAssemblerTest {

    private final String BASE_URL = "http://localhost";

    private PagedResourcesAssembler<TargetNode> pagedResourcesAssembler;

    private HateoasPageableHandlerMethodArgumentResolver resolver;

    private TargetModelAssembler targetModelAssembler;

    @Mock
    private CountryModelAssembler countryModelAssembler;

    @Mock
    private ObjectMapper objectMapper;

    private static TargetBuilder targetBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        targetBuilder = new TargetBuilder();
    }

    @BeforeAll
    private static void init() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @BeforeEach
    private void setUp() {

        resolver = new HateoasPageableHandlerMethodArgumentResolver();

        targetModelAssembler = new TargetModelAssembler(countryModelAssembler, objectMapper);

        pagedResourcesAssembler = new PagedResourcesAssembler<>(resolver, null);
    }

    @Test
    void when_map_target_node_page_to_paged_model_without_targets_should_return_empty_page() {

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
                () -> assertEquals(targetsPagedModel.getLink("self").get().getHref(), selfLink, () -> "should have self link with url: " + selfLink + ", but had: "
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
    void when_map_target_node_page_to_paged_model_on_first_page_should_return_paged_model_with_links() {

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
                () -> assertEquals(targetsPagedModel.getLink("first").get().getHref(), firstLink, () -> "should have first link with url: " + firstLink + ", but had: "
                        + targetsPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(targetsPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + targetsPagedModel),
                () -> assertEquals(targetsPagedModel.getLink("self").get().getHref(), selfLink, () -> "should have self link with url: " + selfLink + ", but had: "
                        + targetsPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(targetsPagedModel.getNextLink().isPresent(),
                        () -> "should have next link, but haven`t: " + targetsPagedModel),
                () -> assertEquals(targetsPagedModel.getNextLink().get().getHref(), nextLink, () -> "should have next link with url:" + nextLink + ", but had: "
                        + targetsPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(targetsPagedModel.getPreviousLink().isEmpty(),
                        () -> "should not have previous link, but had: "
                                + targetsPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(targetsPagedModel.getLink("last").isPresent(),
                        () -> "should have last link, but haven`t: " + targetsPagedModel),
                () -> assertEquals(targetsPagedModel.getLink("last").get().getHref(), lastLink, () -> "should have last link with url: " + lastLink + ", but had: "
                        + targetsPagedModel.getLink("last").get().getHref()));
    }

    @Test
    void when_map_target_node_page_to_paged_model_on_last_page_should_return_paged_model_with_links() {

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
                () -> assertEquals(targetsPagedModel.getLink("first").get().getHref(), firstLink, () -> "should have first link with url: " + firstLink + ", but had: "
                        + targetsPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(targetsPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + targetsPagedModel),
                () -> assertEquals(targetsPagedModel.getLink("self").get().getHref(), selfLink, () -> "should have self link with url: " + selfLink + ", but had: "
                        + targetsPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(targetsPagedModel.getNextLink().isEmpty(),
                        () -> "should not have next link, but had: " + targetsPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(targetsPagedModel.getPreviousLink().isPresent(),
                        () -> "should have previous link, but haven`t: " + targetsPagedModel),
                () -> assertEquals(targetsPagedModel.getPreviousLink().get().getHref(), previousLink, () -> "should have previous link with url:" + previousLink + ", but had: "
                        + targetsPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(targetsPagedModel.getLink("last").isPresent(),
                        () -> "should have last link, but haven`t: " + targetsPagedModel),
                () -> assertEquals(targetsPagedModel.getLink("last").get().getHref(), lastLink, () -> "should have last link with url: " + lastLink + ", but had: "
                        + targetsPagedModel.getLink("last").get().getHref()));
    }

    @Test
    void when_map_target_node_page_to_paged_model_with_previous_and_next_link_should_return_paged_model_with_links() {

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
                () -> assertEquals(targetsPagedModel.getLink("first").get().getHref(), firstLink, () -> "should have first link with url: " + firstLink + ", but had: "
                        + targetsPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(targetsPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + targetsPagedModel),
                () -> assertEquals(targetsPagedModel.getLink("self").get().getHref(), selfLink, () -> "should have self link with url: " + selfLink + ", but had: "
                        + targetsPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(targetsPagedModel.getNextLink().isPresent(),
                        () -> "should have next link, but haven`t: " + targetsPagedModel),
                () -> assertEquals(targetsPagedModel.getNextLink().get().getHref(), nextLink, () -> "should have next link with url:" + nextLink + ", but had: "
                        + targetsPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(targetsPagedModel.getPreviousLink().isPresent(),
                        () -> "should have previous link, but haven`t: " + targetsPagedModel),
                () -> assertEquals(targetsPagedModel.getPreviousLink().get().getHref(), previousLink, () -> "should have previous link with url:" + previousLink + ", but had: "
                        + targetsPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(targetsPagedModel.getLink("last").isPresent(),
                        () -> "should have last link, but haven`t: " + targetsPagedModel),
                () -> assertEquals(targetsPagedModel.getLink("last").get().getHref(), lastLink, () -> "should have last link with url: " + lastLink + ", but had: "
                        + targetsPagedModel.getLink("last").get().getHref()));
    }

    private String getLink(int page, int size) {

        return new StringBuilder(BASE_URL).append("?page=").append(page).append("&size=").append(size).toString();
    }

    private List<TargetNode> createTargetNodesList(int listSize) {

        String targetName = "target";

        List<TargetNode> targetsListExpected = new ArrayList<>();

        int count = 0;

        while (count < listSize) {

            TargetNode targetNode = (TargetNode) targetBuilder.withId((long) count).withTarget(targetName)
                    .build(ObjectType.NODE);

            targetsListExpected.add(targetNode);

            when(objectMapper.map(targetNode, TargetModel.class)).thenReturn(
                    (TargetModel) targetBuilder.withId((long) count).withTarget(targetName)
                            .build(ObjectType.MODEL));

            count++;
        }

        return targetsListExpected;
    }

    private Page<TargetNode> createPageOfTargetNodes(List<TargetNode> targetsListExpected, int page, int size) {

        Pageable pageRequest = PageRequest.of(page, size);

        return new PageImpl<>(targetsListExpected, pageRequest, targetsListExpected.size());
    }
}
