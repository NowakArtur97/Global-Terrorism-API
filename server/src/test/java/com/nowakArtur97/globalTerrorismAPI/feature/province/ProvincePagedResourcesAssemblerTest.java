package com.nowakArtur97.globalTerrorismAPI.feature.province;

import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryModelAssembler;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.ProvinceBuilder;
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
class ProvincePagedResourcesAssemblerTest {

    private final String BASE_URL = "http://localhost";

    private PagedResourcesAssembler<ProvinceNode> pagedResourcesAssembler;

    private HateoasPageableHandlerMethodArgumentResolver resolver;

    private ProvinceModelAssembler provinceModelAssembler;

    @Mock
    private CountryModelAssembler countryModelAssembler;

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

        provinceModelAssembler = new ProvinceModelAssembler(countryModelAssembler, modelMapper);

        pagedResourcesAssembler = new PagedResourcesAssembler<>(resolver, null);
    }

    @Test
    void when_map_province_node_page_to_paged_model_without_provinces_should_return_empty_page() {

        int page = 0;
        int size = 1;
        int listSize = 0;

        List<ProvinceNode> provincesListExpected = createProvinceNodesList(listSize);

        Page<ProvinceNode> provincesPage = createPageOfProvinceNodes(provincesListExpected, page, size);

        PagedModel<ProvinceModel> provincesPagedModel = pagedResourcesAssembler.toModel(provincesPage, provinceModelAssembler);

        String selfLink = getLink(page, size);

        assertAll(
                () -> assertTrue(provincesPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + provincesPagedModel),
                () -> assertTrue(provincesPagedModel.getLink("first").isEmpty(),
                        () -> "should not have first link, but had: "
                                + provincesPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(provincesPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + provincesPagedModel),
                () -> assertEquals(provincesPagedModel.getLink("self").get().getHref(), selfLink,
                        () -> "should have self link with url: " + selfLink + ", but had: "
                                + provincesPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(provincesPagedModel.getNextLink().isEmpty(),
                        () -> "should not have next link, but had: " + provincesPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(provincesPagedModel.getPreviousLink().isEmpty(),
                        () -> "should not have previous link, but had: "
                                + provincesPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(provincesPagedModel.getLink("last").isEmpty(),
                        () -> "should not have last link, but had: "
                                + provincesPagedModel.getLink("last").get().getHref()));
    }

    @Test
    void when_map_province_node_page_to_paged_model_on_first_page_should_return_paged_model_with_links() {

        int page = 0;
        int size = 1;
        int listSize = 3;

        List<ProvinceNode> provincesListExpected = createProvinceNodesList(listSize);

        Page<ProvinceNode> provincesPage = createPageOfProvinceNodes(provincesListExpected, page, size);

        PagedModel<ProvinceModel> provincesPagedModel = pagedResourcesAssembler.toModel(provincesPage, provinceModelAssembler);

        String firstLink = getLink(0, size);
        String selfLink = getLink(page, size);
        String nextLink = getLink(page + 1, size);
        String lastLink = getLink(listSize / size - 1, size);

        assertAll(
                () -> assertTrue(provincesPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + provincesPagedModel),
                () -> assertTrue(provincesPagedModel.getLink("first").isPresent(),
                        () -> "should have first link, but haven`t: " + provincesPagedModel),
                () -> assertEquals(provincesPagedModel.getLink("first").get().getHref(), firstLink,
                        () -> "should have first link with url: " + firstLink + ", but had: "
                                + provincesPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(provincesPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + provincesPagedModel),
                () -> assertEquals(provincesPagedModel.getLink("self").get().getHref(), selfLink,
                        () -> "should have self link with url: " + selfLink + ", but had: "
                                + provincesPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(provincesPagedModel.getNextLink().isPresent(),
                        () -> "should have next link, but haven`t: " + provincesPagedModel),
                () -> assertEquals(provincesPagedModel.getNextLink().get().getHref(), nextLink,
                        () -> "should have next link with url:" + nextLink + ", but had: "
                                + provincesPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(provincesPagedModel.getPreviousLink().isEmpty(),
                        () -> "should not have previous link, but had: "
                                + provincesPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(provincesPagedModel.getLink("last").isPresent(),
                        () -> "should have last link, but haven`t: " + provincesPagedModel),
                () -> assertEquals(provincesPagedModel.getLink("last").get().getHref(), lastLink,
                        () -> "should have last link with url: " + lastLink + ", but had: "
                                + provincesPagedModel.getLink("last").get().getHref()));
    }

    @Test
    void when_map_province_node_page_to_paged_model_on_last_page_should_return_paged_model_with_links() {

        int page = 2;
        int size = 1;
        int listSize = 3;

        List<ProvinceNode> provincesListExpected = createProvinceNodesList(listSize);

        Page<ProvinceNode> provincesPage = createPageOfProvinceNodes(provincesListExpected, page, size);

        PagedModel<ProvinceModel> provincesPagedModel = pagedResourcesAssembler.toModel(provincesPage, provinceModelAssembler);

        String firstLink = getLink(0, size);
        String selfLink = getLink(page, size);
        String previousLink = getLink(page - 1, size);
        String lastLink = getLink(listSize / size - 1, size);

        assertAll(
                () -> assertTrue(provincesPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + provincesPagedModel),
                () -> assertTrue(provincesPagedModel.getLink("first").isPresent(),
                        () -> "should have first link, but haven`t: " + provincesPagedModel),
                () -> assertEquals(provincesPagedModel.getLink("first").get().getHref(), firstLink,
                        () -> "should have first link with url: " + firstLink + ", but had: "
                                + provincesPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(provincesPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + provincesPagedModel),
                () -> assertEquals(provincesPagedModel.getLink("self").get().getHref(), selfLink,
                        () -> "should have self link with url: " + selfLink + ", but had: "
                                + provincesPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(provincesPagedModel.getNextLink().isEmpty(),
                        () -> "should not have next link, but had: " + provincesPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(provincesPagedModel.getPreviousLink().isPresent(),
                        () -> "should have previous link, but haven`t: " + provincesPagedModel),
                () -> assertEquals(provincesPagedModel.getPreviousLink().get().getHref(), previousLink,
                        () -> "should have previous link with url:" + previousLink + ", but had: "
                                + provincesPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(provincesPagedModel.getLink("last").isPresent(),
                        () -> "should have last link, but haven`t: " + provincesPagedModel),
                () -> assertEquals(provincesPagedModel.getLink("last").get().getHref(), lastLink,
                        () -> "should have last link with url: " + lastLink + ", but had: "
                                + provincesPagedModel.getLink("last").get().getHref()));
    }

    @Test
    void when_map_province_node_page_to_paged_model_with_previous_and_next_link_should_return_paged_model_with_links() {

        int page = 1;
        int size = 1;
        int listSize = 3;

        List<ProvinceNode> provincesListExpected = createProvinceNodesList(listSize);

        Page<ProvinceNode> provincesPage = createPageOfProvinceNodes(provincesListExpected, page, size);

        PagedModel<ProvinceModel> provincesPagedModel = pagedResourcesAssembler.toModel(provincesPage, provinceModelAssembler);

        String firstLink = getLink(0, size);
        String selfLink = getLink(page, size);
        String previousLink = getLink(page - 1, size);
        String nextLink = getLink(page + 1, size);
        String lastLink = getLink(listSize / size - 1, size);

        assertAll(
                () -> assertTrue(provincesPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + provincesPagedModel),
                () -> assertTrue(provincesPagedModel.getLink("first").isPresent(),
                        () -> "should have first link, but haven`t: " + provincesPagedModel),
                () -> assertEquals(provincesPagedModel.getLink("first").get().getHref(), firstLink,
                        () -> "should have first link with url: " + firstLink + ", but had: "
                                + provincesPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(provincesPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + provincesPagedModel),
                () -> assertEquals(provincesPagedModel.getLink("self").get().getHref(), selfLink,
                        () -> "should have self link with url: " + selfLink + ", but had: "
                                + provincesPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(provincesPagedModel.getNextLink().isPresent(),
                        () -> "should have next link, but haven`t: " + provincesPagedModel),
                () -> assertEquals(provincesPagedModel.getNextLink().get().getHref(), nextLink,
                        () -> "should have next link with url:" + nextLink + ", but had: "
                                + provincesPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(provincesPagedModel.getPreviousLink().isPresent(),
                        () -> "should have previous link, but haven`t: " + provincesPagedModel),
                () -> assertEquals(provincesPagedModel.getPreviousLink().get().getHref(), previousLink,
                        () -> "should have previous link with url:" + previousLink + ", but had: "
                                + provincesPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(provincesPagedModel.getLink("last").isPresent(),
                        () -> "should have last link, but haven`t: " + provincesPagedModel),
                () -> assertEquals(provincesPagedModel.getLink("last").get().getHref(), lastLink,
                        () -> "should have last link with url: " + lastLink + ", but had: "
                                + provincesPagedModel.getLink("last").get().getHref()));
    }

    private String getLink(int page, int size) {

        return new StringBuilder(BASE_URL).append("?page=").append(page).append("&size=").append(size).toString();
    }

    private List<ProvinceNode> createProvinceNodesList(int listSize) {

        ProvinceBuilder provinceBuilder = new ProvinceBuilder();

        List<ProvinceNode> provincesListExpected = new ArrayList<>();

        int count = 0;

        while (count < listSize) {

            ProvinceNode provinceNode = (ProvinceNode) provinceBuilder.build(ObjectType.NODE);

            when(modelMapper.map(provinceNode, ProvinceModel.class))
                    .thenReturn((ProvinceModel) provinceBuilder.build(ObjectType.MODEL));

            provincesListExpected.add(provinceNode);

            count++;
        }

        return provincesListExpected;
    }

    private Page<ProvinceNode> createPageOfProvinceNodes(List<ProvinceNode> provincesListExpected, int page, int size) {

        Pageable pageRequest = PageRequest.of(page, size);

        return new PageImpl<>(provincesListExpected, pageRequest, provincesListExpected.size());
    }
}
