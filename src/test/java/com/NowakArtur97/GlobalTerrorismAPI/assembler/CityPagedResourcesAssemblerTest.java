package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.CityModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.CityNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.CityBuilder;
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
class CityPagedResourcesAssemblerTest {

    private final String BASE_URL = "http://localhost";

    private PagedResourcesAssembler<CityNode> pagedResourcesAssembler;

    private HateoasPageableHandlerMethodArgumentResolver resolver;

    private CityModelAssembler cityModelAssembler;

    @Mock
    private ProvinceModelAssembler provinceModelAssembler;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeAll
    private static void init() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @BeforeEach
    private void setUp() {

        resolver = new HateoasPageableHandlerMethodArgumentResolver();

        cityModelAssembler = new CityModelAssembler(provinceModelAssembler, objectMapper);

        pagedResourcesAssembler = new PagedResourcesAssembler<>(resolver, null);
    }

    @Test
    void when_map_city_node_page_to_paged_model_without_cities_should_return_empty_page() {

        int page = 0;
        int size = 1;
        int listSize = 0;

        List<CityNode> citiesListExpected = createCityNodesList(listSize);

        Page<CityNode> citiesPage = createPageOfCityNodes(citiesListExpected, page, size);

        PagedModel<CityModel> citiesPagedModel = pagedResourcesAssembler.toModel(citiesPage, cityModelAssembler);

        String selfLink = getLink(page, size);

        assertAll(
                () -> assertTrue(citiesPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + citiesPagedModel),
                () -> assertTrue(citiesPagedModel.getLink("first").isEmpty(),
                        () -> "should not have first link, but had: "
                                + citiesPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(citiesPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + citiesPagedModel),
                () -> assertEquals(citiesPagedModel.getLink("self").get().getHref(), selfLink,
                        () -> "should have self link with url: " + selfLink + ", but had: "
                                + citiesPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(citiesPagedModel.getNextLink().isEmpty(),
                        () -> "should not have next link, but had: " + citiesPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(citiesPagedModel.getPreviousLink().isEmpty(),
                        () -> "should not have previous link, but had: "
                                + citiesPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(citiesPagedModel.getLink("last").isEmpty(),
                        () -> "should not have last link, but had: "
                                + citiesPagedModel.getLink("last").get().getHref()));
    }

    @Test
    void when_map_city_node_page_to_paged_model_on_first_page_should_return_paged_model_with_links() {

        int page = 0;
        int size = 1;
        int listSize = 3;

        List<CityNode> citiesListExpected = createCityNodesList(listSize);

        Page<CityNode> citiesPage = createPageOfCityNodes(citiesListExpected, page, size);

        PagedModel<CityModel> citiesPagedModel = pagedResourcesAssembler.toModel(citiesPage, cityModelAssembler);

        String firstLink = getLink(0, size);
        String selfLink = getLink(page, size);
        String nextLink = getLink(page + 1, size);
        String lastLink = getLink(listSize / size - 1, size);

        assertAll(
                () -> assertTrue(citiesPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + citiesPagedModel),
                () -> assertTrue(citiesPagedModel.getLink("first").isPresent(),
                        () -> "should have first link, but haven`t: " + citiesPagedModel),
                () -> assertEquals(citiesPagedModel.getLink("first").get().getHref(), firstLink,
                        () -> "should have first link with url: " + firstLink + ", but had: "
                                + citiesPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(citiesPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + citiesPagedModel),
                () -> assertEquals(citiesPagedModel.getLink("self").get().getHref(), selfLink,
                        () -> "should have self link with url: " + selfLink + ", but had: "
                                + citiesPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(citiesPagedModel.getNextLink().isPresent(),
                        () -> "should have next link, but haven`t: " + citiesPagedModel),
                () -> assertEquals(citiesPagedModel.getNextLink().get().getHref(), nextLink,
                        () -> "should have next link with url:" + nextLink + ", but had: "
                                + citiesPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(citiesPagedModel.getPreviousLink().isEmpty(),
                        () -> "should not have previous link, but had: "
                                + citiesPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(citiesPagedModel.getLink("last").isPresent(),
                        () -> "should have last link, but haven`t: " + citiesPagedModel),
                () -> assertEquals(citiesPagedModel.getLink("last").get().getHref(), lastLink,
                        () -> "should have last link with url: " + lastLink + ", but had: "
                                + citiesPagedModel.getLink("last").get().getHref()));
    }

    @Test
    void when_map_city_node_page_to_paged_model_on_last_page_should_return_paged_model_with_links() {

        int page = 2;
        int size = 1;
        int listSize = 3;

        List<CityNode> citiesListExpected = createCityNodesList(listSize);

        Page<CityNode> citiesPage = createPageOfCityNodes(citiesListExpected, page, size);

        PagedModel<CityModel> citiesPagedModel = pagedResourcesAssembler.toModel(citiesPage, cityModelAssembler);

        String firstLink = getLink(0, size);
        String selfLink = getLink(page, size);
        String previousLink = getLink(page - 1, size);
        String lastLink = getLink(listSize / size - 1, size);

        assertAll(
                () -> assertTrue(citiesPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + citiesPagedModel),
                () -> assertTrue(citiesPagedModel.getLink("first").isPresent(),
                        () -> "should have first link, but haven`t: " + citiesPagedModel),
                () -> assertEquals(citiesPagedModel.getLink("first").get().getHref(), firstLink,
                        () -> "should have first link with url: " + firstLink + ", but had: "
                                + citiesPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(citiesPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + citiesPagedModel),
                () -> assertEquals(citiesPagedModel.getLink("self").get().getHref(), selfLink,
                        () -> "should have self link with url: " + selfLink + ", but had: "
                                + citiesPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(citiesPagedModel.getNextLink().isEmpty(),
                        () -> "should not have next link, but had: " + citiesPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(citiesPagedModel.getPreviousLink().isPresent(),
                        () -> "should have previous link, but haven`t: " + citiesPagedModel),
                () -> assertEquals(citiesPagedModel.getPreviousLink().get().getHref(), previousLink,
                        () -> "should have previous link with url:" + previousLink + ", but had: "
                                + citiesPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(citiesPagedModel.getLink("last").isPresent(),
                        () -> "should have last link, but haven`t: " + citiesPagedModel),
                () -> assertEquals(citiesPagedModel.getLink("last").get().getHref(), lastLink,
                        () -> "should have last link with url: " + lastLink + ", but had: "
                                + citiesPagedModel.getLink("last").get().getHref()));
    }

    @Test
    void when_map_city_node_page_to_paged_model_with_previous_and_next_link_should_return_paged_model_with_links() {

        int page = 1;
        int size = 1;
        int listSize = 3;

        List<CityNode> citiesListExpected = createCityNodesList(listSize);

        Page<CityNode> citiesPage = createPageOfCityNodes(citiesListExpected, page, size);

        PagedModel<CityModel> citiesPagedModel = pagedResourcesAssembler.toModel(citiesPage, cityModelAssembler);

        String firstLink = getLink(0, size);
        String selfLink = getLink(page, size);
        String previousLink = getLink(page - 1, size);
        String nextLink = getLink(page + 1, size);
        String lastLink = getLink(listSize / size - 1, size);

        assertAll(
                () -> assertTrue(citiesPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + citiesPagedModel),
                () -> assertTrue(citiesPagedModel.getLink("first").isPresent(),
                        () -> "should have first link, but haven`t: " + citiesPagedModel),
                () -> assertEquals(citiesPagedModel.getLink("first").get().getHref(), firstLink,
                        () -> "should have first link with url: " + firstLink + ", but had: "
                                + citiesPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(citiesPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + citiesPagedModel),
                () -> assertEquals(citiesPagedModel.getLink("self").get().getHref(), selfLink,
                        () -> "should have self link with url: " + selfLink + ", but had: "
                                + citiesPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(citiesPagedModel.getNextLink().isPresent(),
                        () -> "should have next link, but haven`t: " + citiesPagedModel),
                () -> assertEquals(citiesPagedModel.getNextLink().get().getHref(), nextLink,
                        () -> "should have next link with url:" + nextLink + ", but had: "
                                + citiesPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(citiesPagedModel.getPreviousLink().isPresent(),
                        () -> "should have previous link, but haven`t: " + citiesPagedModel),
                () -> assertEquals(citiesPagedModel.getPreviousLink().get().getHref(), previousLink,
                        () -> "should have previous link with url:" + previousLink + ", but had: "
                                + citiesPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(citiesPagedModel.getLink("last").isPresent(),
                        () -> "should have last link, but haven`t: " + citiesPagedModel),
                () -> assertEquals(citiesPagedModel.getLink("last").get().getHref(), lastLink,
                        () -> "should have last link with url: " + lastLink + ", but had: "
                                + citiesPagedModel.getLink("last").get().getHref()));
    }

    private String getLink(int page, int size) {

        return new StringBuilder(BASE_URL).append("?page=").append(page).append("&size=").append(size).toString();
    }

    private List<CityNode> createCityNodesList(int listSize) {

        CityBuilder cityBuilder = new CityBuilder();

        List<CityNode> citiesListExpected = new ArrayList<>();

        int count = 0;

        while (count < listSize) {

            CityNode cityNode = (CityNode) cityBuilder.build(ObjectType.NODE);

            when(objectMapper.map(cityNode, CityModel.class)).thenReturn((CityModel) cityBuilder.build(ObjectType.MODEL));

            citiesListExpected.add(cityNode);

            count++;
        }

        return citiesListExpected;
    }

    private Page<CityNode> createPageOfCityNodes(List<CityNode> citiesListExpected, int page, int size) {

        Pageable pageRequest = PageRequest.of(page, size);

        return new PageImpl<>(citiesListExpected, pageRequest, citiesListExpected.size());
    }
}
