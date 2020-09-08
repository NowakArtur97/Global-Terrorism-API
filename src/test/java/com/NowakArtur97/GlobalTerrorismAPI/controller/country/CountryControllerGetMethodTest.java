package com.NowakArtur97.GlobalTerrorismAPI.controller.country;

import com.NowakArtur97.GlobalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.CountryModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Country;
import com.NowakArtur97.GlobalTerrorismAPI.controller.BasicGenericRestController;
import com.NowakArtur97.GlobalTerrorismAPI.controller.CountryController;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.CountryModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.RegionModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.RegionNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.BasicGenericService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.CountryBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.RegionBuilder;
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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("CountryController_Tests")
class CountryControllerGetMethodTest {

    private static int counterForUtilMethodsModel = 0;
    private static int counterForUtilMethodsNode = 0;

    private final String COUNTRY_BASE_PATH = "http://localhost:8080/api/v1/countries";

    private MockMvc mockMvc;

    private BasicGenericRestController<CountryModel> countryController;

    @Mock
    private BasicGenericService<CountryNode> countryService;

    @Mock
    private CountryModelAssembler modelAssembler;

    @Mock
    private PagedResourcesAssembler<CountryNode> pagedResourcesAssembler;

    private static RegionBuilder regionBuilder;
    private static CountryBuilder countryBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        regionBuilder = new RegionBuilder();
        countryBuilder = new CountryBuilder();
    }

    @BeforeEach
    private void setUp() {

        countryController = new CountryController(countryService, modelAssembler, pagedResourcesAssembler);

        mockMvc = MockMvcBuilders.standaloneSetup(countryController)
                .setControllerAdvice(new GenericRestControllerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void when_find_all_countries_with_default_parameters_in_link_and_countries_exist_should_return_all_countries() {

        CountryNode countryNode1 = (CountryNode) createCountry(ObjectType.NODE);
        CountryNode countryNode2 = (CountryNode) createCountry(ObjectType.NODE);
        CountryNode countryNode3 = (CountryNode) createCountry(ObjectType.NODE);
        CountryNode countryNode4 = (CountryNode) createCountry(ObjectType.NODE);

        CountryModel countryModel1 = (CountryModel) createCountry(ObjectType.MODEL);
        CountryModel countryModel2 = (CountryModel) createCountry(ObjectType.MODEL);
        CountryModel countryModel3 = (CountryModel) createCountry(ObjectType.MODEL);
        CountryModel countryModel4 = (CountryModel) createCountry(ObjectType.MODEL);

        List<CountryNode> countryNodesListExpected = List.of(countryNode1, countryNode2, countryNode3, countryNode4);
        List<CountryModel> countryModelsListExpected = List.of(countryModel1, countryModel2, countryModel3, countryModel4);
        Page<CountryNode> countriesExpected = new PageImpl<>(countryNodesListExpected);

        int sizeExpected = 20;
        int totalElementsExpected = 4;
        int totalPagesExpected = 1;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = COUNTRY_BASE_PATH + urlParameters1;
        String lastPageLink = COUNTRY_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<CountryModel> resources = new PagedModel<>(countryModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(countryService.findAll(pageable)).thenReturn(countriesExpected);
        when(pagedResourcesAssembler.toModel(countriesExpected, modelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content[0].id", is(countryModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].name", is(countryModel1.getName())))
                        .andExpect(jsonPath("content[0].region.id", is(countryModel1.getRegion().getId().intValue())))
                        .andExpect(jsonPath("content[0].region.name", is(countryModel1.getRegion().getName())))
                        .andExpect(jsonPath("content[0].region.links").isEmpty())
                        .andExpect(jsonPath("content[0].links[0].href",
                                is(countryModel1.getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[1].id", is(countryModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].name", is(countryModel2.getName())))
                        .andExpect(jsonPath("content[1].region.id", is(countryModel2.getRegion().getId().intValue())))
                        .andExpect(jsonPath("content[1].region.name", is(countryModel2.getRegion().getName())))
                        .andExpect(jsonPath("content[1].region.links").isEmpty())
                        .andExpect(jsonPath("content[1].links[0].href",
                                is(countryModel2.getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[2].id", is(countryModel3.getId().intValue())))
                        .andExpect(jsonPath("content[2].name", is(countryModel3.getName())))
                        .andExpect(jsonPath("content[2].region.id", is(countryModel3.getRegion().getId().intValue())))
                        .andExpect(jsonPath("content[2].region.name", is(countryModel3.getRegion().getName())))
                        .andExpect(jsonPath("content[2].region.links").isEmpty())
                        .andExpect(jsonPath("content[2].links[0].href",
                                is(countryModel3.getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[3].id", is(countryModel4.getId().intValue())))
                        .andExpect(jsonPath("content[3].name", is(countryModel4.getName())))
                        .andExpect(jsonPath("content[3].region.id", is(countryModel4.getRegion().getId().intValue())))
                        .andExpect(jsonPath("content[3].region.name", is(countryModel4.getRegion().getName())))
                        .andExpect(jsonPath("content[3].region.links").isEmpty())
                        .andExpect(jsonPath("content[3].links[0].href",
                                is(countryModel4.getLink("self").get().getHref())))

                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(countryService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(countryService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(countriesExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_find_all_countries_with_changed_parameters_in_link_and_countries_exist_should_return_all_countries() {

        CountryNode countryNode1 = (CountryNode) createCountry(ObjectType.NODE);
        CountryNode countryNode2 = (CountryNode) createCountry(ObjectType.NODE);
        CountryNode countryNode3 = (CountryNode) createCountry(ObjectType.NODE);
        CountryNode countryNode4 = (CountryNode) createCountry(ObjectType.NODE);

        CountryModel countryModel1 = (CountryModel) createCountry(ObjectType.MODEL);
        CountryModel countryModel2 = (CountryModel) createCountry(ObjectType.MODEL);
        CountryModel countryModel3 = (CountryModel) createCountry(ObjectType.MODEL);

        List<CountryModel> countryModelsListExpected = List.of(countryModel1, countryModel2, countryModel3);
        List<CountryNode> countryNodesListExpected = List.of(countryNode1, countryNode2, countryNode3, countryNode4);

        int sizeExpected = 3;
        int totalElementsExpected = 4;
        int totalPagesExpected = 2;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 1;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);
        PageImpl<CountryNode> countriesExpected = new PageImpl<>(countryNodesListExpected, pageable, countryNodesListExpected.size());

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = COUNTRY_BASE_PATH + urlParameters1;
        String lastPageLink = COUNTRY_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<CountryModel> resources = new PagedModel<>(countryModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(countryService.findAll(pageable)).thenReturn(countriesExpected);
        when(pagedResourcesAssembler.toModel(countriesExpected, modelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content[0].id", is(countryModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].name", is(countryModel1.getName())))
                        .andExpect(jsonPath("content[0].region.id", is(countryModel1.getRegion().getId().intValue())))
                        .andExpect(jsonPath("content[0].region.name", is(countryModel1.getRegion().getName())))
                        .andExpect(jsonPath("content[0].region.links").isEmpty())
                        .andExpect(jsonPath("content[0].links[0].href",
                                is(countryModel1.getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[1].id", is(countryModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].name", is(countryModel2.getName())))
                        .andExpect(jsonPath("content[1].region.id", is(countryModel2.getRegion().getId().intValue())))
                        .andExpect(jsonPath("content[1].region.name", is(countryModel2.getRegion().getName())))
                        .andExpect(jsonPath("content[1].region.links").isEmpty())
                        .andExpect(jsonPath("content[1].links[0].href",
                                is(countryModel2.getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[2].id", is(countryModel3.getId().intValue())))
                        .andExpect(jsonPath("content[2].name", is(countryModel3.getName())))
                        .andExpect(jsonPath("content[2].region.id", is(countryModel3.getRegion().getId().intValue())))
                        .andExpect(jsonPath("content[2].region.name", is(countryModel3.getRegion().getName())))
                        .andExpect(jsonPath("content[2].region.links").isEmpty())
                        .andExpect(jsonPath("content[2].links[0].href",
                                is(countryModel3.getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[3]").doesNotExist())
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(countryService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(countryService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(countriesExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_find_all_countries_but_countries_not_exist_should_return_empty_list() {

        List<CountryNode> countriesListExpected = new ArrayList<>();

        List<CountryModel> modelsListExpected = new ArrayList<>();

        Page<CountryNode> countriesExpected = new PageImpl<>(countriesListExpected);

        int sizeExpected = 20;
        int totalElementsExpected = 0;
        int totalPagesExpected = 0;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = COUNTRY_BASE_PATH + urlParameters1;
        String lastPageLink = COUNTRY_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<CountryModel> resources = new PagedModel<>(modelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(countryService.findAll(pageable)).thenReturn(countriesExpected);
        when(pagedResourcesAssembler.toModel(countriesExpected, modelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content").isEmpty())
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(countryService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(countryService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(countriesExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_find_existing_country_should_return_country() {

        Long countryId = 2L;

        RegionNode regionNode = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNode = (CountryNode) countryBuilder.withId(countryId).withRegion(regionNode)
                .build(ObjectType.NODE);

        RegionModel regionModel = (RegionModel) regionBuilder.build(ObjectType.MODEL);
        CountryModel countryModel = (CountryModel) countryBuilder.withId(countryId).withRegion(regionModel)
                .build(ObjectType.MODEL);
        String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
        countryModel.add(new Link(pathToCountryLink));

        String linkWithParameter = COUNTRY_BASE_PATH + "/" + "{id}";

        when(countryService.findById(countryId)).thenReturn(Optional.of(countryNode));
        when(modelAssembler.toModel(countryNode)).thenReturn(countryModel);

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, countryId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("links[1].href").doesNotExist())
                        .andExpect(jsonPath("id", is(countryModel.getId().intValue())))
                        .andExpect(jsonPath("name", is(countryModel.getName())))
                        .andExpect(jsonPath("region.id", is(regionModel.getId().intValue())))
                        .andExpect(jsonPath("region.name", is(regionModel.getName())))
                        .andExpect(jsonPath("region.links").isEmpty()),
                () -> verify(countryService, times(1)).findById(countryId),
                () -> verifyNoMoreInteractions(countryService),
                () -> verify(modelAssembler, times(1)).toModel(countryNode),
                () -> verifyNoMoreInteractions(modelAssembler));
    }

    @Test
    void when_find_country_but_country_does_not_exist_should_return_error_response() {

        Long countryId = 1L;

        String linkWithParameter = COUNTRY_BASE_PATH + "/" + "{id}";

        when(countryService.findById(countryId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, countryId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find CountryModel with id: " + countryId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(countryService, times(1)).findById(countryId),
                () -> verifyNoMoreInteractions(countryService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    private Country createCountry(ObjectType type) {

        switch (type) {

            case NODE:

                counterForUtilMethodsNode++;

                RegionNode regionNode = (RegionNode) regionBuilder.withId((long) counterForUtilMethodsNode)
                        .withName("region" + (long) counterForUtilMethodsNode).build(ObjectType.NODE);

                return (CountryNode) countryBuilder.withId((long) counterForUtilMethodsNode)
                        .withName("country" + counterForUtilMethodsNode).withRegion(regionNode)
                        .build(ObjectType.NODE);

            case MODEL:

                counterForUtilMethodsModel++;

                RegionModel regionModel = (RegionModel) regionBuilder.withId((long) counterForUtilMethodsModel)
                        .withName("region" + (long) counterForUtilMethodsModel).build(ObjectType.MODEL);
                
                CountryModel countryModel = (CountryModel) countryBuilder.withId((long) counterForUtilMethodsModel)
                        .withRegion(regionModel).build(ObjectType.MODEL);
                String pathToCountryLink = COUNTRY_BASE_PATH + "/" + counterForUtilMethodsModel;
                countryModel.add(new Link(pathToCountryLink));

                return countryModel;

            default:
                throw new RuntimeException("Invalid type");
        }
    }
}
