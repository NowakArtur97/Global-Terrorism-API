package com.nowakArtur97.globalTerrorismAPI.feature.region;

import com.nowakArtur97.globalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.nowakArtur97.globalTerrorismAPI.common.controller.BasicGenericRestController;
import com.nowakArtur97.globalTerrorismAPI.common.service.BasicGenericService;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.RegionBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
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
@Tag("RegionController_Tests")
class RegionControllerGetMethodTest {

    private static int counterForUtilMethodsModel = 0;
    private static int counterForUtilMethodsNode = 0;

    private final String REGION_BASE_PATH = "http://localhost:8080/api/v1/regions";

    private MockMvc mockMvc;

    @Mock
    private BasicGenericService<RegionNode> regionService;

    @Mock
    private RegionModelAssembler modelAssembler;

    @Mock
    private PagedResourcesAssembler<RegionNode> pagedResourcesAssembler;

    private static RegionBuilder regionBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        regionBuilder = new RegionBuilder();
    }

    @BeforeEach
    private void setUp() {

        BasicGenericRestController<RegionModel> regionController
                = new RegionController(regionService, modelAssembler, pagedResourcesAssembler);

        mockMvc = MockMvcBuilders.standaloneSetup(regionController)
                .setControllerAdvice(new GenericRestControllerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void when_find_all_regions_with_default_parameters_in_link_and_regions_exist_should_return_all_regions() {

        RegionNode regionNode1 = (RegionNode) createRegion(ObjectType.NODE);
        RegionNode regionNode2 = (RegionNode) createRegion(ObjectType.NODE);
        RegionNode regionNode3 = (RegionNode) createRegion(ObjectType.NODE);
        RegionNode regionNode4 = (RegionNode) createRegion(ObjectType.NODE);

        RegionModel regionModel1 = (RegionModel) createRegion(ObjectType.MODEL);
        RegionModel regionModel2 = (RegionModel) createRegion(ObjectType.MODEL);
        RegionModel regionModel3 = (RegionModel) createRegion(ObjectType.MODEL);
        RegionModel regionModel4 = (RegionModel) createRegion(ObjectType.MODEL);

        List<RegionNode> regionNodesListExpected = List.of(regionNode1, regionNode2, regionNode3, regionNode4);
        List<RegionModel> regionModelsListExpected = List.of(regionModel1, regionModel2, regionModel3, regionModel4);
        Page<RegionNode> regionsExpected = new PageImpl<>(regionNodesListExpected);

        int sizeExpected = 20;
        int totalElementsExpected = 4;
        int totalPagesExpected = 1;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = REGION_BASE_PATH + urlParameters1;
        String lastPageLink = REGION_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<RegionModel> resources = new PagedModel<>(regionModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(regionService.findAll(pageable)).thenReturn(regionsExpected);
        when(pagedResourcesAssembler.toModel(regionsExpected, modelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content[0].id", is(regionModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].name", is(regionModel1.getName())))
                        .andExpect(jsonPath("content[0].links[0].href",
                                is(regionModel1.getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[1].id", is(regionModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].name", is(regionModel2.getName())))
                        .andExpect(jsonPath("content[1].links[0].href",
                                is(regionModel2.getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[2].id", is(regionModel3.getId().intValue())))
                        .andExpect(jsonPath("content[2].name", is(regionModel3.getName())))
                        .andExpect(jsonPath("content[2].links[0].href",
                                is(regionModel3.getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[3].id", is(regionModel4.getId().intValue())))
                        .andExpect(jsonPath("content[3].name", is(regionModel4.getName())))
                        .andExpect(jsonPath("content[3].links[0].href",
                                is(regionModel4.getLink("self").get().getHref())))

                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(regionService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(regionService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(regionsExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_find_all_regions_with_changed_parameters_in_link_and_regions_exist_should_return_all_regions() {

        RegionNode regionNode1 = (RegionNode) createRegion(ObjectType.NODE);
        RegionNode regionNode2 = (RegionNode) createRegion(ObjectType.NODE);
        RegionNode regionNode3 = (RegionNode) createRegion(ObjectType.NODE);
        RegionNode regionNode4 = (RegionNode) createRegion(ObjectType.NODE);

        RegionModel regionModel1 = (RegionModel) createRegion(ObjectType.MODEL);
        RegionModel regionModel2 = (RegionModel) createRegion(ObjectType.MODEL);
        RegionModel regionModel3 = (RegionModel) createRegion(ObjectType.MODEL);

        List<RegionModel> regionModelsListExpected = List.of(regionModel1, regionModel2, regionModel3);
        List<RegionNode> regionNodesListExpected = List.of(regionNode1, regionNode2, regionNode3, regionNode4);

        int sizeExpected = 3;
        int totalElementsExpected = 4;
        int totalPagesExpected = 2;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 1;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);
        PageImpl<RegionNode> regionsExpected = new PageImpl<>(regionNodesListExpected, pageable, regionNodesListExpected.size());

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = REGION_BASE_PATH + urlParameters1;
        String lastPageLink = REGION_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<RegionModel> resources = new PagedModel<>(regionModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(regionService.findAll(pageable)).thenReturn(regionsExpected);
        when(pagedResourcesAssembler.toModel(regionsExpected, modelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content[0].id", is(regionModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].name", is(regionModel1.getName())))
                        .andExpect(jsonPath("content[0].links[0].href",
                                is(regionModel1.getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[1].id", is(regionModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].name", is(regionModel2.getName())))
                        .andExpect(jsonPath("content[1].links[0].href",
                                is(regionModel2.getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[2].id", is(regionModel3.getId().intValue())))
                        .andExpect(jsonPath("content[2].name", is(regionModel3.getName())))
                        .andExpect(jsonPath("content[2].links[0].href",
                                is(regionModel3.getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[3]").doesNotExist())
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(regionService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(regionService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(regionsExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_find_all_regions_but_regions_not_exist_should_return_empty_list() {

        List<RegionNode> regionsListExpected = new ArrayList<>();

        List<RegionModel> modelsListExpected = new ArrayList<>();

        Page<RegionNode> regionsExpected = new PageImpl<>(regionsListExpected);

        int sizeExpected = 20;
        int totalElementsExpected = 0;
        int totalPagesExpected = 0;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = REGION_BASE_PATH + urlParameters1;
        String lastPageLink = REGION_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<RegionModel> resources = new PagedModel<>(modelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(regionService.findAll(pageable)).thenReturn(regionsExpected);
        when(pagedResourcesAssembler.toModel(regionsExpected, modelAssembler)).thenReturn(resources);

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
                () -> verify(regionService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(regionService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(regionsExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_find_existing_region_should_return_region() {

        Long regionId = 1L;

        RegionNode regionNode = (RegionNode) regionBuilder.withId(regionId).build(ObjectType.NODE);
        RegionModel regionModel = (RegionModel) regionBuilder.withId(regionId).build(ObjectType.MODEL);
        String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
        regionModel.add(new Link(pathToRegionLink));

        String linkWithParameter = REGION_BASE_PATH + "/" + "{id}";

        when(regionService.findById(regionId)).thenReturn(Optional.of(regionNode));
        when(modelAssembler.toModel(regionNode)).thenReturn(regionModel);

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, regionId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("links[1].href").doesNotExist())
                        .andExpect(jsonPath("id", is(regionModel.getId().intValue())))
                        .andExpect(jsonPath("name", is(regionModel.getName()))),
                () -> verify(regionService, times(1)).findById(regionId),
                () -> verifyNoMoreInteractions(regionService),
                () -> verify(modelAssembler, times(1)).toModel(regionNode),
                () -> verifyNoMoreInteractions(modelAssembler));
    }

    @Test
    void when_find_region_but_region_does_not_exist_should_return_error_response() {

        Long regionId = 1L;

        String linkWithParameter = REGION_BASE_PATH + "/" + "{id}";

        when(regionService.findById(regionId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, regionId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find RegionModel with id: " + regionId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(regionService, times(1)).findById(regionId),
                () -> verifyNoMoreInteractions(regionService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    private Region createRegion(ObjectType type) {

        switch (type) {

            case NODE:

                counterForUtilMethodsNode++;

                return regionBuilder.withId((long) counterForUtilMethodsNode)
                        .withName("region" + (long) counterForUtilMethodsNode).build(ObjectType.NODE);

            case MODEL:

                counterForUtilMethodsModel++;

                RegionModel regionModel = (RegionModel) regionBuilder.withId((long) counterForUtilMethodsModel)
                        .withName("region" + (long) counterForUtilMethodsModel).build(ObjectType.MODEL);

                String pathToRegionLink = REGION_BASE_PATH + "/" + counterForUtilMethodsModel;
                regionModel.add(new Link(pathToRegionLink));

                return regionModel;

            default:
                throw new RuntimeException("Invalid type");
        }
    }
}
