package com.NowakArtur97.GlobalTerrorismAPI.controller.city;

import com.NowakArtur97.GlobalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.CityModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.baseModel.City;
import com.NowakArtur97.GlobalTerrorismAPI.controller.CityController;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestController;
import com.NowakArtur97.GlobalTerrorismAPI.dto.CityDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.CityModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.CityNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.CityBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.ProvinceBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.patch.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.violation.ViolationHelper;
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
@Tag("CityController_Tests")
class CityControllerGetMethodTest {

    private static int counterForUtilMethodsModel = 0;
    private static int counterForUtilMethodsNode = 0;

    private final String PROVINCE_BASE_PATH = "http://localhost:8080/api/v1/provinces";
    private final String CITY_BASE_PATH = "http://localhost:8080/api/v1/cities";

    private MockMvc mockMvc;

    private GenericRestController<CityModel, CityDTO> cityController;

    @Mock
    private GenericService<CityNode, CityDTO> cityService;

    @Mock
    private CityModelAssembler modelAssembler;

    @Mock
    private PagedResourcesAssembler<CityNode> pagedResourcesAssembler;

    @Mock
    private PatchHelper patchHelper;

    @Mock
    private ViolationHelper<CityNode, CityDTO> violationHelper;

    private static ProvinceBuilder provinceBuilder;
    private static CityBuilder cityBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        provinceBuilder = new ProvinceBuilder();
        cityBuilder = new CityBuilder();
    }

    @BeforeEach
    private void setUp() {

        cityController = new CityController(cityService, modelAssembler, pagedResourcesAssembler, patchHelper,
                violationHelper);

        mockMvc = MockMvcBuilders.standaloneSetup(cityController)
                .setControllerAdvice(new GenericRestControllerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void when_find_all_cities_with_default_parameters_in_link_and_cities_exist_should_return_all_cities() {

        CityNode cityNode1 = (CityNode) createCity(ObjectType.NODE);
        CityNode cityNode2 = (CityNode) createCity(ObjectType.NODE);
        CityNode cityNode3 = (CityNode) createCity(ObjectType.NODE);
        CityNode cityNode4 = (CityNode) createCity(ObjectType.NODE);

        CityModel cityModel1 = (CityModel) createCity(ObjectType.MODEL);
        CityModel cityModel2 = (CityModel) createCity(ObjectType.MODEL);
        CityModel cityModel3 = (CityModel) createCity(ObjectType.MODEL);
        CityModel cityModel4 = (CityModel) createCity(ObjectType.MODEL);

        List<CityNode> cityNodesListExpected = List.of(cityNode1, cityNode2, cityNode3, cityNode4);
        List<CityModel> cityModelsListExpected = List.of(cityModel1, cityModel2, cityModel3, cityModel4);
        Page<CityNode> citiesExpected = new PageImpl<>(cityNodesListExpected);

        int sizeExpected = 20;
        int totalElementsExpected = 4;
        int totalPagesExpected = 1;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = CITY_BASE_PATH + urlParameters1;
        String lastPageLink = CITY_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<CityModel> resources = new PagedModel<>(cityModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(cityService.findAll(pageable)).thenReturn(citiesExpected);
        when(pagedResourcesAssembler.toModel(citiesExpected, modelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content[0].id", is(cityModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].name", is(cityModel1.getName())))
                        .andExpect(jsonPath("content[0].latitude", is(cityModel1.getLatitude())))
                        .andExpect(jsonPath("content[0].longitude", is(cityModel1.getLongitude())))
                        .andExpect(jsonPath("content[0].province.id", is(cityModel1.getProvince().getId().intValue())))
                        .andExpect(jsonPath("content[0].province.name", is(cityModel1.getProvince().getName())))
                        .andExpect(jsonPath("content[0].links[0].href",
                                is(cityModel1.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[0].province.links[0].href",
                                is(cityModel1.getProvince().getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[1].id", is(cityModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].name", is(cityModel2.getName())))
                        .andExpect(jsonPath("content[1].latitude", is(cityModel2.getLatitude())))
                        .andExpect(jsonPath("content[1].longitude", is(cityModel2.getLongitude())))
                        .andExpect(jsonPath("content[1].province.id", is(cityModel2.getProvince().getId().intValue())))
                        .andExpect(jsonPath("content[1].province.name", is(cityModel2.getProvince().getName())))
                        .andExpect(jsonPath("content[1].links[0].href",
                                is(cityModel2.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].province.links[0].href",
                                is(cityModel2.getProvince().getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[2].id", is(cityModel3.getId().intValue())))
                        .andExpect(jsonPath("content[2].name", is(cityModel3.getName())))
                        .andExpect(jsonPath("content[2].latitude", is(cityModel3.getLatitude())))
                        .andExpect(jsonPath("content[2].longitude", is(cityModel3.getLongitude())))
                        .andExpect(jsonPath("content[2].province.id", is(cityModel3.getProvince().getId().intValue())))
                        .andExpect(jsonPath("content[2].province.name", is(cityModel3.getProvince().getName())))
                        .andExpect(jsonPath("content[2].links[0].href",
                                is(cityModel3.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].province.links[0].href",
                                is(cityModel3.getProvince().getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[3].id", is(cityModel4.getId().intValue())))
                        .andExpect(jsonPath("content[3].name", is(cityModel4.getName())))
                        .andExpect(jsonPath("content[3].latitude", is(cityModel4.getLatitude())))
                        .andExpect(jsonPath("content[3].longitude", is(cityModel4.getLongitude())))
                        .andExpect(jsonPath("content[3].province.id", is(cityModel4.getProvince().getId().intValue())))
                        .andExpect(jsonPath("content[3].province.name", is(cityModel4.getProvince().getName())))
                        .andExpect(jsonPath("content[3].links[0].href",
                                is(cityModel4.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[3].province.links[0].href",
                                is(cityModel4.getProvince().getLink("self").get().getHref())))

                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(cityService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(cityService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(citiesExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    void when_find_all_cities_with_changed_parameters_in_link_and_cities_exist_should_return_all_cities() {

        CityNode cityNode1 = (CityNode) createCity(ObjectType.NODE);
        CityNode cityNode2 = (CityNode) createCity(ObjectType.NODE);
        CityNode cityNode3 = (CityNode) createCity(ObjectType.NODE);
        CityNode cityNode4 = (CityNode) createCity(ObjectType.NODE);

        CityModel cityModel1 = (CityModel) createCity(ObjectType.MODEL);
        CityModel cityModel2 = (CityModel) createCity(ObjectType.MODEL);
        CityModel cityModel3 = (CityModel) createCity(ObjectType.MODEL);

        List<CityModel> cityModelsListExpected = List.of(cityModel1, cityModel2, cityModel3);
        List<CityNode> cityNodesListExpected = List.of(cityNode1, cityNode2, cityNode3, cityNode4);

        int sizeExpected = 3;
        int totalElementsExpected = 4;
        int totalPagesExpected = 2;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 1;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);
        PageImpl<CityNode> citiesExpected = new PageImpl<>(cityNodesListExpected, pageable, cityNodesListExpected.size());

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = CITY_BASE_PATH + urlParameters1;
        String lastPageLink = CITY_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<CityModel> resources = new PagedModel<>(cityModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(cityService.findAll(pageable)).thenReturn(citiesExpected);
        when(pagedResourcesAssembler.toModel(citiesExpected, modelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content[0].id", is(cityModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].name", is(cityModel1.getName())))
                        .andExpect(jsonPath("content[0].latitude", is(cityModel1.getLatitude())))
                        .andExpect(jsonPath("content[0].longitude", is(cityModel1.getLongitude())))
                        .andExpect(jsonPath("content[0].province.id", is(cityModel1.getProvince().getId().intValue())))
                        .andExpect(jsonPath("content[0].province.name", is(cityModel1.getProvince().getName())))
                        .andExpect(jsonPath("content[0].links[0].href",
                                is(cityModel1.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[0].province.links[0].href",
                                is(cityModel1.getProvince().getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[1].id", is(cityModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].name", is(cityModel2.getName())))
                        .andExpect(jsonPath("content[1].latitude", is(cityModel2.getLatitude())))
                        .andExpect(jsonPath("content[1].longitude", is(cityModel2.getLongitude())))
                        .andExpect(jsonPath("content[1].province.id", is(cityModel2.getProvince().getId().intValue())))
                        .andExpect(jsonPath("content[1].province.name", is(cityModel2.getProvince().getName())))
                        .andExpect(jsonPath("content[1].links[0].href",
                                is(cityModel2.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].province.links[0].href",
                                is(cityModel2.getProvince().getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[2].id", is(cityModel3.getId().intValue())))
                        .andExpect(jsonPath("content[2].name", is(cityModel3.getName())))
                        .andExpect(jsonPath("content[2].latitude", is(cityModel3.getLatitude())))
                        .andExpect(jsonPath("content[2].longitude", is(cityModel3.getLongitude())))
                        .andExpect(jsonPath("content[2].province.id", is(cityModel3.getProvince().getId().intValue())))
                        .andExpect(jsonPath("content[2].province.name", is(cityModel3.getProvince().getName())))
                        .andExpect(jsonPath("content[2].links[0].href",
                                is(cityModel3.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].province.links[0].href",
                                is(cityModel3.getProvince().getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[3]").doesNotExist())
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(cityService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(cityService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(citiesExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    void when_find_all_cities_but_cities_not_exist_should_return_empty_list() {

        List<CityNode> citiesListExpected = new ArrayList<>();

        List<CityModel> modelsListExpected = new ArrayList<>();

        Page<CityNode> citiesExpected = new PageImpl<>(citiesListExpected);

        int sizeExpected = 20;
        int totalElementsExpected = 0;
        int totalPagesExpected = 0;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = CITY_BASE_PATH + urlParameters1;
        String lastPageLink = CITY_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<CityModel> resources = new PagedModel<>(modelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(cityService.findAll(pageable)).thenReturn(citiesExpected);
        when(pagedResourcesAssembler.toModel(citiesExpected, modelAssembler)).thenReturn(resources);

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
                () -> verify(cityService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(cityService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(citiesExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    void when_find_existing_city_should_return_city() {

        Long cityId = 1L;

        ProvinceNode provinceNode = (ProvinceNode) provinceBuilder.build(ObjectType.NODE);
        CityNode cityNode = (CityNode) cityBuilder.withProvince(provinceNode).build(ObjectType.NODE);

        ProvinceModel provinceModel = (ProvinceModel) provinceBuilder.build(ObjectType.MODEL);
        String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + provinceNode.getId().intValue();
        provinceModel.add(new Link(pathToProvinceLink));
        CityModel cityModel = (CityModel) cityBuilder.withProvince(provinceModel).build(ObjectType.MODEL);
        String pathToCityLink = CITY_BASE_PATH + "/" + cityId.intValue();
        cityModel.add(new Link(pathToCityLink));

        String linkWithParameter = CITY_BASE_PATH + "/" + "{id}";

        when(cityService.findById(cityId)).thenReturn(Optional.of(cityNode));
        when(modelAssembler.toModel(cityNode)).thenReturn(cityModel);

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, cityId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToCityLink)))
                        .andExpect(jsonPath("links[1].href").doesNotExist())
                        .andExpect(jsonPath("id", is(cityModel.getId().intValue())))
                        .andExpect(jsonPath("name", is(cityModel.getName())))
                        .andExpect(jsonPath("latitude", is(cityModel.getLatitude())))
                        .andExpect(jsonPath("longitude", is(cityModel.getLongitude())))
                        .andExpect(jsonPath("province.links[0].href", is(pathToProvinceLink)))
                        .andExpect(jsonPath("province.id", is(cityModel.getProvince().getId().intValue())))
                        .andExpect(jsonPath("province.name", is(cityModel.getProvince().getName()))),
                () -> verify(cityService, times(1)).findById(cityId),
                () -> verifyNoMoreInteractions(cityService),
                () -> verify(modelAssembler, times(1)).toModel(cityNode),
                () -> verifyNoMoreInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_find_city_but_city_does_not_exist_should_return_error_response() {

        Long cityId = 1L;

        String linkWithParameter = CITY_BASE_PATH + "/" + "{id}";

        when(cityService.findById(cityId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, cityId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find CityModel with id: " + cityId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(cityService, times(1)).findById(cityId),
                () -> verifyNoMoreInteractions(cityService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    private City createCity(ObjectType type) {

        switch (type) {

            case NODE:

                counterForUtilMethodsNode++;

                ProvinceNode provinceNode = (ProvinceNode) provinceBuilder.withId((long) counterForUtilMethodsNode)
                        .build(ObjectType.NODE);

                return cityBuilder
                        .withId((long) counterForUtilMethodsNode)
                        .withName("city" + counterForUtilMethodsNode)
                        .withLatitude((double) (20 + counterForUtilMethodsNode))
                        .withLongitude((double) (40 + counterForUtilMethodsNode))
                        .withProvince(provinceNode)
                        .build(ObjectType.NODE);

            case MODEL:

                counterForUtilMethodsModel++;

                ProvinceModel provinceModel = (ProvinceModel) provinceBuilder.withId((long) counterForUtilMethodsModel)
                        .build(ObjectType.MODEL);
                String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + counterForUtilMethodsModel;
                provinceModel.add(new Link(pathToProvinceLink));

                CityModel cityModel = (CityModel) cityBuilder
                        .withId((long) counterForUtilMethodsModel)
                        .withName("city" + counterForUtilMethodsModel)
                        .withLatitude((double) (20 + counterForUtilMethodsModel))
                        .withLongitude((double) (40 + counterForUtilMethodsModel))
                        .withProvince(provinceModel)
                        .build(ObjectType.MODEL);

                String pathToCityLink = CITY_BASE_PATH + "/" + counterForUtilMethodsModel;
                cityModel.add(new Link(pathToCityLink));

                return cityModel;

            default:
                throw new RuntimeException("Invalid type");
        }
    }
}
