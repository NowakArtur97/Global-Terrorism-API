package com.nowakArtur97.globalTerrorismAPI.feature.region;

import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.RegionBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
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
class RegionPagedResourcesAssemblerTest {

    private final String BASE_URL = "http://localhost";

    private PagedResourcesAssembler<RegionNode> pagedResourcesAssembler;

    private HateoasPageableHandlerMethodArgumentResolver resolver;

    private RegionModelAssembler regionModelAssembler;

    @Mock
    private ModelMapper modelMapper;

    @BeforeAll
    private static void init() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @BeforeEach
    private void setUp() {

        resolver = new HateoasPageableHandlerMethodArgumentResolver();

        regionModelAssembler = new RegionModelAssembler(modelMapper);

        pagedResourcesAssembler = new PagedResourcesAssembler<>(resolver, null);
    }

    @Test
    void when_map_region_node_page_to_paged_model_without_regions_should_return_empty_page() {

        int page = 0;
        int size = 1;
        int listSize = 0;

        List<RegionNode> regionsListExpected = createRegionNodesList(listSize);

        Page<RegionNode> regionsPage = createPageOfRegionNodes(regionsListExpected, page, size);

        PagedModel<RegionModel> regionsPagedModel = pagedResourcesAssembler.toModel(regionsPage, regionModelAssembler);

        String selfLink = getLink(page, size);

        assertAll(
                () -> assertTrue(regionsPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + regionsPagedModel),
                () -> assertTrue(regionsPagedModel.getLink("first").isEmpty(),
                        () -> "should not have first link, but had: "
                                + regionsPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(regionsPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + regionsPagedModel),
                () -> assertEquals(regionsPagedModel.getLink("self").get().getHref(), selfLink,
                        () -> "should have self link with url: " + selfLink + ", but had: "
                                + regionsPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(regionsPagedModel.getNextLink().isEmpty(),
                        () -> "should not have next link, but had: " + regionsPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(regionsPagedModel.getPreviousLink().isEmpty(),
                        () -> "should not have previous link, but had: "
                                + regionsPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(regionsPagedModel.getLink("last").isEmpty(),
                        () -> "should not have last link, but had: "
                                + regionsPagedModel.getLink("last").get().getHref()));
    }

    @Test
    void when_map_region_node_page_to_paged_model_on_first_page_should_return_paged_model_with_links() {

        int page = 0;
        int size = 1;
        int listSize = 3;

        List<RegionNode> regionsListExpected = createRegionNodesList(listSize);

        Page<RegionNode> regionsPage = createPageOfRegionNodes(regionsListExpected, page, size);

        PagedModel<RegionModel> regionsPagedModel = pagedResourcesAssembler.toModel(regionsPage, regionModelAssembler);

        String firstLink = getLink(0, size);
        String selfLink = getLink(page, size);
        String nextLink = getLink(page + 1, size);
        String lastLink = getLink(listSize / size - 1, size);

        assertAll(
                () -> assertTrue(regionsPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + regionsPagedModel),
                () -> assertTrue(regionsPagedModel.getLink("first").isPresent(),
                        () -> "should have first link, but haven`t: " + regionsPagedModel),
                () -> assertEquals(regionsPagedModel.getLink("first").get().getHref(), firstLink,
                        () -> "should have first link with url: " + firstLink + ", but had: "
                                + regionsPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(regionsPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + regionsPagedModel),
                () -> assertEquals(regionsPagedModel.getLink("self").get().getHref(), selfLink,
                        () -> "should have self link with url: " + selfLink + ", but had: "
                                + regionsPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(regionsPagedModel.getNextLink().isPresent(),
                        () -> "should have next link, but haven`t: " + regionsPagedModel),
                () -> assertEquals(regionsPagedModel.getNextLink().get().getHref(), nextLink,
                        () -> "should have next link with url:" + nextLink + ", but had: "
                                + regionsPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(regionsPagedModel.getPreviousLink().isEmpty(),
                        () -> "should not have previous link, but had: "
                                + regionsPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(regionsPagedModel.getLink("last").isPresent(),
                        () -> "should have last link, but haven`t: " + regionsPagedModel),
                () -> assertEquals(regionsPagedModel.getLink("last").get().getHref(), lastLink,
                        () -> "should have last link with url: " + lastLink + ", but had: "
                                + regionsPagedModel.getLink("last").get().getHref()));
    }

    @Test
    void when_map_region_node_page_to_paged_model_on_last_page_should_return_paged_model_with_links() {

        int page = 2;
        int size = 1;
        int listSize = 3;

        List<RegionNode> regionsListExpected = createRegionNodesList(listSize);

        Page<RegionNode> regionsPage = createPageOfRegionNodes(regionsListExpected, page, size);

        PagedModel<RegionModel> regionsPagedModel = pagedResourcesAssembler.toModel(regionsPage, regionModelAssembler);

        String firstLink = getLink(0, size);
        String selfLink = getLink(page, size);
        String previousLink = getLink(page - 1, size);
        String lastLink = getLink(listSize / size - 1, size);

        assertAll(
                () -> assertTrue(regionsPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + regionsPagedModel),
                () -> assertTrue(regionsPagedModel.getLink("first").isPresent(),
                        () -> "should have first link, but haven`t: " + regionsPagedModel),
                () -> assertEquals(regionsPagedModel.getLink("first").get().getHref(), firstLink,
                        () -> "should have first link with url: " + firstLink + ", but had: "
                                + regionsPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(regionsPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + regionsPagedModel),
                () -> assertEquals(regionsPagedModel.getLink("self").get().getHref(), selfLink,
                        () -> "should have self link with url: " + selfLink + ", but had: "
                                + regionsPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(regionsPagedModel.getNextLink().isEmpty(),
                        () -> "should not have next link, but had: " + regionsPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(regionsPagedModel.getPreviousLink().isPresent(),
                        () -> "should have previous link, but haven`t: " + regionsPagedModel),
                () -> assertEquals(regionsPagedModel.getPreviousLink().get().getHref(), previousLink,
                        () -> "should have previous link with url:" + previousLink + ", but had: "
                                + regionsPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(regionsPagedModel.getLink("last").isPresent(),
                        () -> "should have last link, but haven`t: " + regionsPagedModel),
                () -> assertEquals(regionsPagedModel.getLink("last").get().getHref(), lastLink,
                        () -> "should have last link with url: " + lastLink + ", but had: "
                                + regionsPagedModel.getLink("last").get().getHref()));
    }

    @Test
    void when_map_region_node_page_to_paged_model_with_previous_and_next_link_should_return_paged_model_with_links() {

        int page = 1;
        int size = 1;
        int listSize = 3;

        List<RegionNode> regionsListExpected = createRegionNodesList(listSize);

        Page<RegionNode> regionsPage = createPageOfRegionNodes(regionsListExpected, page, size);

        PagedModel<RegionModel> regionsPagedModel = pagedResourcesAssembler.toModel(regionsPage, regionModelAssembler);

        String firstLink = getLink(0, size);
        String selfLink = getLink(page, size);
        String previousLink = getLink(page - 1, size);
        String nextLink = getLink(page + 1, size);
        String lastLink = getLink(listSize / size - 1, size);

        assertAll(
                () -> assertTrue(regionsPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + regionsPagedModel),
                () -> assertTrue(regionsPagedModel.getLink("first").isPresent(),
                        () -> "should have first link, but haven`t: " + regionsPagedModel),
                () -> assertEquals(regionsPagedModel.getLink("first").get().getHref(), firstLink,
                        () -> "should have first link with url: " + firstLink + ", but had: "
                                + regionsPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(regionsPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + regionsPagedModel),
                () -> assertEquals(regionsPagedModel.getLink("self").get().getHref(), selfLink,
                        () -> "should have self link with url: " + selfLink + ", but had: "
                                + regionsPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(regionsPagedModel.getNextLink().isPresent(),
                        () -> "should have next link, but haven`t: " + regionsPagedModel),
                () -> assertEquals(regionsPagedModel.getNextLink().get().getHref(), nextLink,
                        () -> "should have next link with url:" + nextLink + ", but had: "
                                + regionsPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(regionsPagedModel.getPreviousLink().isPresent(),
                        () -> "should have previous link, but haven`t: " + regionsPagedModel),
                () -> assertEquals(regionsPagedModel.getPreviousLink().get().getHref(), previousLink,
                        () -> "should have previous link with url:" + previousLink + ", but had: "
                                + regionsPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(regionsPagedModel.getLink("last").isPresent(),
                        () -> "should have last link, but haven`t: " + regionsPagedModel),
                () -> assertEquals(regionsPagedModel.getLink("last").get().getHref(), lastLink,
                        () -> "should have last link with url: " + lastLink + ", but had: "
                                + regionsPagedModel.getLink("last").get().getHref()));
    }

    private String getLink(int page, int size) {

        return new StringBuilder(BASE_URL).append("?page=").append(page).append("&size=").append(size).toString();
    }

    private List<RegionNode> createRegionNodesList(int listSize) {

        RegionBuilder regionBuilder = new RegionBuilder();

        List<RegionNode> regionsListExpected = new ArrayList<>();

        int count = 0;

        while (count < listSize) {

            RegionNode regionNode = (RegionNode) regionBuilder.build(ObjectType.NODE);

            when(modelMapper.map(regionNode, RegionModel.class))
                    .thenReturn((RegionModel) regionBuilder.build(ObjectType.MODEL));

            regionsListExpected.add(regionNode);

            count++;
        }

        return regionsListExpected;
    }

    private Page<RegionNode> createPageOfRegionNodes(List<RegionNode> regionsListExpected, int page, int size) {

        Pageable pageRequest = PageRequest.of(page, size);

        return new PageImpl<>(regionsListExpected, pageRequest, regionsListExpected.size());
    }
}
