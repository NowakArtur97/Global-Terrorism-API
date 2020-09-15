package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.CountryModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.CountryBuilder;
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
class CountryPagedResourcesAssemblerTest {

    private final String BASE_URL = "http://localhost";

    private PagedResourcesAssembler<CountryNode> pagedResourcesAssembler;

    private HateoasPageableHandlerMethodArgumentResolver resolver;

    private CountryModelAssembler countryModelAssembler;

    @Mock
    private RegionModelAssembler regionModelAssembler;

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

        countryModelAssembler = new CountryModelAssembler(regionModelAssembler, objectMapper);

        pagedResourcesAssembler = new PagedResourcesAssembler<>(resolver, null);
    }

    @Test
    void when_map_country_node_page_to_paged_model_without_countries_should_return_empty_page() {

        int page = 0;
        int size = 1;
        int listSize = 0;

        List<CountryNode> countriesListExpected = createCountryNodesList(listSize);

        Page<CountryNode> countriesPage = createPageOfCountryNodes(countriesListExpected, page, size);

        PagedModel<CountryModel> countriesPagedModel = pagedResourcesAssembler.toModel(countriesPage, countryModelAssembler);

        String selfLink = getLink(page, size);

        assertAll(
                () -> assertTrue(countriesPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + countriesPagedModel),
                () -> assertTrue(countriesPagedModel.getLink("first").isEmpty(),
                        () -> "should not have first link, but had: "
                                + countriesPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(countriesPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + countriesPagedModel),
                () -> assertEquals(countriesPagedModel.getLink("self").get().getHref(), selfLink,
                        () -> "should have self link with url: " + selfLink + ", but had: "
                                + countriesPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(countriesPagedModel.getNextLink().isEmpty(),
                        () -> "should not have next link, but had: " + countriesPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(countriesPagedModel.getPreviousLink().isEmpty(),
                        () -> "should not have previous link, but had: "
                                + countriesPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(countriesPagedModel.getLink("last").isEmpty(),
                        () -> "should not have last link, but had: "
                                + countriesPagedModel.getLink("last").get().getHref()));
    }

    @Test
    void when_map_country_node_page_to_paged_model_on_first_page_should_return_paged_model_with_links() {

        int page = 0;
        int size = 1;
        int listSize = 3;

        List<CountryNode> countriesListExpected = createCountryNodesList(listSize);

        Page<CountryNode> countriesPage = createPageOfCountryNodes(countriesListExpected, page, size);

        PagedModel<CountryModel> countriesPagedModel = pagedResourcesAssembler.toModel(countriesPage, countryModelAssembler);

        String firstLink = getLink(0, size);
        String selfLink = getLink(page, size);
        String nextLink = getLink(page + 1, size);
        String lastLink = getLink(listSize / size - 1, size);

        assertAll(
                () -> assertTrue(countriesPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + countriesPagedModel),
                () -> assertTrue(countriesPagedModel.getLink("first").isPresent(),
                        () -> "should have first link, but haven`t: " + countriesPagedModel),
                () -> assertEquals(countriesPagedModel.getLink("first").get().getHref(), firstLink,
                        () -> "should have first link with url: " + firstLink + ", but had: "
                                + countriesPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(countriesPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + countriesPagedModel),
                () -> assertEquals(countriesPagedModel.getLink("self").get().getHref(), selfLink,
                        () -> "should have self link with url: " + selfLink + ", but had: "
                                + countriesPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(countriesPagedModel.getNextLink().isPresent(),
                        () -> "should have next link, but haven`t: " + countriesPagedModel),
                () -> assertEquals(countriesPagedModel.getNextLink().get().getHref(), nextLink,
                        () -> "should have next link with url:" + nextLink + ", but had: "
                                + countriesPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(countriesPagedModel.getPreviousLink().isEmpty(),
                        () -> "should not have previous link, but had: "
                                + countriesPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(countriesPagedModel.getLink("last").isPresent(),
                        () -> "should have last link, but haven`t: " + countriesPagedModel),
                () -> assertEquals(countriesPagedModel.getLink("last").get().getHref(), lastLink,
                        () -> "should have last link with url: " + lastLink + ", but had: "
                                + countriesPagedModel.getLink("last").get().getHref()));
    }

    @Test
    void when_map_country_node_page_to_paged_model_on_last_page_should_return_paged_model_with_links() {

        int page = 2;
        int size = 1;
        int listSize = 3;

        List<CountryNode> countriesListExpected = createCountryNodesList(listSize);

        Page<CountryNode> countriesPage = createPageOfCountryNodes(countriesListExpected, page, size);

        PagedModel<CountryModel> countriesPagedModel = pagedResourcesAssembler.toModel(countriesPage, countryModelAssembler);

        String firstLink = getLink(0, size);
        String selfLink = getLink(page, size);
        String previousLink = getLink(page - 1, size);
        String lastLink = getLink(listSize / size - 1, size);

        assertAll(
                () -> assertTrue(countriesPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + countriesPagedModel),
                () -> assertTrue(countriesPagedModel.getLink("first").isPresent(),
                        () -> "should have first link, but haven`t: " + countriesPagedModel),
                () -> assertEquals(countriesPagedModel.getLink("first").get().getHref(), firstLink,
                        () -> "should have first link with url: " + firstLink + ", but had: "
                                + countriesPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(countriesPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + countriesPagedModel),
                () -> assertEquals(countriesPagedModel.getLink("self").get().getHref(), selfLink,
                        () -> "should have self link with url: " + selfLink + ", but had: "
                                + countriesPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(countriesPagedModel.getNextLink().isEmpty(),
                        () -> "should not have next link, but had: " + countriesPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(countriesPagedModel.getPreviousLink().isPresent(),
                        () -> "should have previous link, but haven`t: " + countriesPagedModel),
                () -> assertEquals(countriesPagedModel.getPreviousLink().get().getHref(), previousLink,
                        () -> "should have previous link with url:" + previousLink + ", but had: "
                                + countriesPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(countriesPagedModel.getLink("last").isPresent(),
                        () -> "should have last link, but haven`t: " + countriesPagedModel),
                () -> assertEquals(countriesPagedModel.getLink("last").get().getHref(), lastLink,
                        () -> "should have last link with url: " + lastLink + ", but had: "
                                + countriesPagedModel.getLink("last").get().getHref()));
    }

    @Test
    void when_map_country_node_page_to_paged_model_with_previous_and_next_link_should_return_paged_model_with_links() {

        int page = 1;
        int size = 1;
        int listSize = 3;

        List<CountryNode> countriesListExpected = createCountryNodesList(listSize);

        Page<CountryNode> countriesPage = createPageOfCountryNodes(countriesListExpected, page, size);

        PagedModel<CountryModel> countriesPagedModel = pagedResourcesAssembler.toModel(countriesPage, countryModelAssembler);

        String firstLink = getLink(0, size);
        String selfLink = getLink(page, size);
        String previousLink = getLink(page - 1, size);
        String nextLink = getLink(page + 1, size);
        String lastLink = getLink(listSize / size - 1, size);

        assertAll(
                () -> assertTrue(countriesPagedModel.hasLinks(),
                        () -> "should have links, but haven`t: " + countriesPagedModel),
                () -> assertTrue(countriesPagedModel.getLink("first").isPresent(),
                        () -> "should have first link, but haven`t: " + countriesPagedModel),
                () -> assertEquals(countriesPagedModel.getLink("first").get().getHref(), firstLink,
                        () -> "should have first link with url: " + firstLink + ", but had: "
                                + countriesPagedModel.getLink("first").get().getHref()),
                () -> assertTrue(countriesPagedModel.getLink("self").isPresent(),
                        () -> "should have self link, but haven`t: " + countriesPagedModel),
                () -> assertEquals(countriesPagedModel.getLink("self").get().getHref(), selfLink,
                        () -> "should have self link with url: " + selfLink + ", but had: "
                                + countriesPagedModel.getLink("self").get().getHref()),
                () -> assertTrue(countriesPagedModel.getNextLink().isPresent(),
                        () -> "should have next link, but haven`t: " + countriesPagedModel),
                () -> assertEquals(countriesPagedModel.getNextLink().get().getHref(), nextLink,
                        () -> "should have next link with url:" + nextLink + ", but had: "
                                + countriesPagedModel.getNextLink().get().getHref()),
                () -> assertTrue(countriesPagedModel.getPreviousLink().isPresent(),
                        () -> "should have previous link, but haven`t: " + countriesPagedModel),
                () -> assertEquals(countriesPagedModel.getPreviousLink().get().getHref(), previousLink,
                        () -> "should have previous link with url:" + previousLink + ", but had: "
                                + countriesPagedModel.getPreviousLink().get().getHref()),
                () -> assertTrue(countriesPagedModel.getLink("last").isPresent(),
                        () -> "should have last link, but haven`t: " + countriesPagedModel),
                () -> assertEquals(countriesPagedModel.getLink("last").get().getHref(), lastLink,
                        () -> "should have last link with url: " + lastLink + ", but had: "
                                + countriesPagedModel.getLink("last").get().getHref()));
    }

    private String getLink(int page, int size) {

        return new StringBuilder(BASE_URL).append("?page=").append(page).append("&size=").append(size).toString();
    }

    private List<CountryNode> createCountryNodesList(int listSize) {

        CountryBuilder countryBuilder = new CountryBuilder();

        List<CountryNode> countriesListExpected = new ArrayList<>();

        int count = 0;

        while (count < listSize) {

            CountryNode countryNode = (CountryNode) countryBuilder.build(ObjectType.NODE);

            when(objectMapper.map(countryNode, CountryModel.class))
                    .thenReturn((CountryModel) countryBuilder.build(ObjectType.MODEL));

            countriesListExpected.add(countryNode);

            count++;
        }

        return countriesListExpected;
    }

    private Page<CountryNode> createPageOfCountryNodes(List<CountryNode> countriesListExpected, int page, int size) {

        Pageable pageRequest = PageRequest.of(page, size);

        return new PageImpl<>(countriesListExpected, pageRequest, countriesListExpected.size());
    }
}
