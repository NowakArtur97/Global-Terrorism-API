package com.nowakArtur97.globalTerrorismAPI.feature.target;

import com.nowakArtur97.globalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.nowakArtur97.globalTerrorismAPI.common.controller.GenericRestController;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryModel;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryNode;
import com.nowakArtur97.globalTerrorismAPI.feature.region.RegionModel;
import com.nowakArtur97.globalTerrorismAPI.feature.region.RegionNode;
import com.nowakArtur97.globalTerrorismAPI.common.service.GenericService;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.CountryBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.RegionBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.nowakArtur97.globalTerrorismAPI.common.util.PatchUtil;
import com.nowakArtur97.globalTerrorismAPI.common.util.ViolationUtil;
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
@Tag("TargetController_Tests")
class TargetControllerGetMethodTest {

    private static int counterForUtilMethodsNode = 0;
    private static int counterForUtilMethodsModel = 0;

    private final String TARGET_BASE_PATH = "http://localhost:8080/api/v1/targets";

    private MockMvc mockMvc;

    @Mock
    private GenericService<TargetNode, TargetDTO> targetService;

    @Mock
    private TargetModelAssembler targetModelAssembler;

    @Mock
    private PagedResourcesAssembler<TargetNode> pagedResourcesAssembler;

    @Mock
    private PatchUtil patchUtil;

    @Mock
    private ViolationUtil<TargetNode, TargetDTO> violationUtil;

    private static RegionBuilder regionBuilder;
    private static CountryBuilder countryBuilder;
    private static TargetBuilder targetBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        regionBuilder = new RegionBuilder();
        countryBuilder = new CountryBuilder();
        targetBuilder = new TargetBuilder();
    }

    @BeforeEach
    private void setUp() {

        GenericRestController<TargetModel, TargetDTO> targetController
                = new TargetController(targetService, targetModelAssembler, pagedResourcesAssembler,
                patchUtil, violationUtil);

        mockMvc = MockMvcBuilders.standaloneSetup(targetController).setControllerAdvice(new GenericRestControllerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
    }

    @Test
    void when_find_all_targets_with_default_parameters_in_link_and_targets_exist_should_return_all_targets() {

        TargetNode targetNode1 = (TargetNode) createTarget(ObjectType.NODE);
        TargetModel targetModel1 = (TargetModel) createTarget(ObjectType.MODEL);

        TargetNode targetNode2 = (TargetNode) createTarget(ObjectType.NODE);
        TargetModel targetModel2 = (TargetModel) createTarget(ObjectType.MODEL);

        TargetNode targetNode3 = (TargetNode) createTarget(ObjectType.NODE);
        TargetModel targetModel3 = (TargetModel) createTarget(ObjectType.MODEL);

        TargetNode targetNode4 = (TargetNode) createTarget(ObjectType.NODE);
        TargetModel targetModel4 = (TargetModel) createTarget(ObjectType.MODEL);

        List<TargetNode> targetsListExpected = List.of(targetNode1, targetNode2, targetNode3, targetNode4);
        List<TargetModel> targetModelsListExpected = List.of(targetModel1, targetModel2, targetModel3, targetModel4);
        Page<TargetNode> targetsExpected = new PageImpl<>(targetsListExpected);

        int sizeExpected = 20;
        int totalElementsExpected = 4;
        int totalPagesExpected = 1;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = TARGET_BASE_PATH + urlParameters1;
        String lastPageLink = TARGET_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PagedModel.PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<TargetModel> resources = new PagedModel<>(targetModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(targetService.findAll(pageable)).thenReturn(targetsExpected);
        when(pagedResourcesAssembler.toModel(targetsExpected, targetModelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content[0].id", is(targetModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].target", is(targetModel1.getTarget())))
                        .andExpect(jsonPath("content[0].countryOfOrigin.id", is(targetModel1.getCountryOfOrigin().getId().intValue())))
                        .andExpect(jsonPath("content[0].countryOfOrigin.name", is(targetModel1.getCountryOfOrigin().getName())))
                        .andExpect(jsonPath("content[0].countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("content[0].countryOfOrigin.region.id",
                                is(targetModel1.getCountryOfOrigin().getRegion().getId().intValue())))
                        .andExpect(jsonPath("content[0].countryOfOrigin.region.name",
                                is(targetModel1.getCountryOfOrigin().getRegion().getName())))
                        .andExpect(jsonPath("content[0].countryOfOrigin.region.links").isEmpty())
                        .andExpect(
                                jsonPath("content[0].links[0].href", is(targetModel1.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].id", is(targetModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].target", is(targetModel2.getTarget())))
                        .andExpect(jsonPath("content[1].countryOfOrigin.id", is(targetModel2.getCountryOfOrigin().getId().intValue())))
                        .andExpect(jsonPath("content[1].countryOfOrigin.name", is(targetModel2.getCountryOfOrigin().getName())))
                        .andExpect(jsonPath("content[1].countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("content[1].countryOfOrigin.region.id",
                                is(targetModel2.getCountryOfOrigin().getRegion().getId().intValue())))
                        .andExpect(jsonPath("content[1].countryOfOrigin.region.name",
                                is(targetModel2.getCountryOfOrigin().getRegion().getName())))
                        .andExpect(jsonPath("content[1].countryOfOrigin.region.links").isEmpty())
                        .andExpect(
                                jsonPath("content[1].links[0].href", is(targetModel2.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].id", is(targetModel3.getId().intValue())))
                        .andExpect(jsonPath("content[2].target", is(targetModel3.getTarget())))
                        .andExpect(jsonPath("content[2].countryOfOrigin.id", is(targetModel3.getCountryOfOrigin().getId().intValue())))
                        .andExpect(jsonPath("content[2].countryOfOrigin.name", is(targetModel3.getCountryOfOrigin().getName())))
                        .andExpect(jsonPath("content[2].countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("content[2].countryOfOrigin.region.id",
                                is(targetModel3.getCountryOfOrigin().getRegion().getId().intValue())))
                        .andExpect(jsonPath("content[2].countryOfOrigin.region.name",
                                is(targetModel3.getCountryOfOrigin().getRegion().getName())))
                        .andExpect(jsonPath("content[2].countryOfOrigin.region.links").isEmpty())
                        .andExpect(
                                jsonPath("content[2].links[0].href", is(targetModel3.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[3].id", is(targetModel4.getId().intValue())))
                        .andExpect(jsonPath("content[3].target", is(targetModel4.getTarget())))
                        .andExpect(jsonPath("content[3].countryOfOrigin.id", is(targetModel4.getCountryOfOrigin().getId().intValue())))
                        .andExpect(jsonPath("content[3].countryOfOrigin.name", is(targetModel4.getCountryOfOrigin().getName())))
                        .andExpect(jsonPath("content[3].countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("content[3].countryOfOrigin.region.id",
                                is(targetModel4.getCountryOfOrigin().getRegion().getId().intValue())))
                        .andExpect(jsonPath("content[3].countryOfOrigin.region.name",
                                is(targetModel4.getCountryOfOrigin().getRegion().getName())))
                        .andExpect(jsonPath("content[3].countryOfOrigin.region.links").isEmpty())
                        .andExpect(
                                jsonPath("content[3].links[0].href", is(targetModel4.getLink("self").get().getHref())))
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(targetService, times(1)).findAll(pageable), () -> verifyNoMoreInteractions(targetService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(targetsExpected, targetModelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil));
    }

    @Test
    void when_find_all_targets_with_changed_parameters_in_link_and_targets_exist_should_return_all_targets() {

        TargetNode targetNode1 = (TargetNode) createTarget(ObjectType.NODE);
        TargetModel targetModel1 = (TargetModel) createTarget(ObjectType.MODEL);

        TargetNode targetNode2 = (TargetNode) createTarget(ObjectType.NODE);
        TargetModel targetModel2 = (TargetModel) createTarget(ObjectType.MODEL);

        TargetNode targetNode3 = (TargetNode) createTarget(ObjectType.NODE);
        TargetModel targetModel3 = (TargetModel) createTarget(ObjectType.MODEL);

        TargetNode targetNode4 = (TargetNode) createTarget(ObjectType.NODE);

        List<TargetNode> targetsListExpected = List.of(targetNode1, targetNode2, targetNode3, targetNode4);
        List<TargetModel> targetModelsListExpected = List.of(targetModel1, targetModel2, targetModel3);

        Page<TargetNode> targetsExpected = new PageImpl<>(targetsListExpected);

        int sizeExpected = 3;
        int totalElementsExpected = 4;
        int totalPagesExpected = 2;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 1;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = TARGET_BASE_PATH + urlParameters1;
        String lastPageLink = TARGET_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PagedModel.PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<TargetModel> resources = new PagedModel<>(targetModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(targetService.findAll(pageable)).thenReturn(targetsExpected);
        when(pagedResourcesAssembler.toModel(targetsExpected, targetModelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content[0].id", is(targetModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].target", is(targetModel1.getTarget())))
                        .andExpect(jsonPath("content[0].countryOfOrigin.id", is(targetModel1.getCountryOfOrigin().getId().intValue())))
                        .andExpect(jsonPath("content[0].countryOfOrigin.name", is(targetModel1.getCountryOfOrigin().getName())))
                        .andExpect(jsonPath("content[0].countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("content[0].countryOfOrigin.region.id",
                                is(targetModel1.getCountryOfOrigin().getRegion().getId().intValue())))
                        .andExpect(jsonPath("content[0].countryOfOrigin.region.name",
                                is(targetModel1.getCountryOfOrigin().getRegion().getName())))
                        .andExpect(jsonPath("content[1].countryOfOrigin.region.links").isEmpty())
                        .andExpect(
                                jsonPath("content[0].links[0].href", is(targetModel1.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].id", is(targetModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].target", is(targetModel2.getTarget())))
                        .andExpect(jsonPath("content[1].countryOfOrigin.id", is(targetModel2.getCountryOfOrigin().getId().intValue())))
                        .andExpect(jsonPath("content[1].countryOfOrigin.name", is(targetModel2.getCountryOfOrigin().getName())))
                        .andExpect(jsonPath("content[1].countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("content[1].countryOfOrigin.region.id",
                                is(targetModel2.getCountryOfOrigin().getRegion().getId().intValue())))
                        .andExpect(jsonPath("content[1].countryOfOrigin.region.name",
                                is(targetModel2.getCountryOfOrigin().getRegion().getName())))
                        .andExpect(jsonPath("content[1].countryOfOrigin.region.links").isEmpty())
                        .andExpect(
                                jsonPath("content[1].links[0].href", is(targetModel2.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].id", is(targetModel3.getId().intValue())))
                        .andExpect(jsonPath("content[2].target", is(targetModel3.getTarget())))
                        .andExpect(jsonPath("content[2].countryOfOrigin.id", is(targetModel3.getCountryOfOrigin().getId().intValue())))
                        .andExpect(jsonPath("content[2].countryOfOrigin.name", is(targetModel3.getCountryOfOrigin().getName())))
                        .andExpect(jsonPath("content[2].countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("content[2].countryOfOrigin.region.id",
                                is(targetModel3.getCountryOfOrigin().getRegion().getId().intValue())))
                        .andExpect(jsonPath("content[2].countryOfOrigin.region.name",
                                is(targetModel3.getCountryOfOrigin().getRegion().getName())))
                        .andExpect(jsonPath("content[2].countryOfOrigin.region.links").isEmpty())
                        .andExpect(
                                jsonPath("content[2].links[0].href", is(targetModel3.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[3]").doesNotExist())
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(targetService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(targetService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(targetsExpected, targetModelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(targetModelAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil));
    }

    @Test
    void when_find_all_targets_but_targets_not_exist_should_return_empty_list() {

        List<TargetNode> targetsListExpected = new ArrayList<>();

        List<TargetModel> targetModelsListExpected = new ArrayList<>();

        Page<TargetNode> targetsExpected = new PageImpl<>(targetsListExpected);

        int sizeExpected = 20;
        int totalElementsExpected = 0;
        int totalPagesExpected = 0;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = TARGET_BASE_PATH + urlParameters1;
        String lastPageLink = TARGET_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PagedModel.PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<TargetModel> resources = new PagedModel<>(targetModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(targetService.findAll(pageable)).thenReturn(targetsExpected);
        when(pagedResourcesAssembler.toModel(targetsExpected, targetModelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink))
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
                () -> verify(targetService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(targetService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(targetsExpected, targetModelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(targetModelAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil));
    }

    @Test
    void when_find_existing_target_should_return_target() {

        Long regionId = 1L;
        Long countryId = 2L;
        Long targetId = 3L;

        RegionNode regionNode = (RegionNode) regionBuilder.withId(regionId).build(ObjectType.NODE);
        RegionModel regionModel = (RegionModel) regionBuilder.withId(regionId).build(ObjectType.MODEL);

        CountryNode countryNode = (CountryNode) countryBuilder.withId(countryId).withRegion(regionNode)
                .build(ObjectType.NODE);
        CountryModel countryModel = (CountryModel) countryBuilder.withId(countryId).withRegion(regionModel)
                .build(ObjectType.MODEL);

        TargetNode targetNode = (TargetNode) targetBuilder.withId(targetId).withCountry(countryNode)
                .build(ObjectType.NODE);
        TargetModel targetModel = (TargetModel) targetBuilder.withId(targetId).withCountry(countryModel)
                .build(ObjectType.MODEL);

        String pathToLink = TARGET_BASE_PATH + "/" + targetId.intValue();
        Link link = new Link(pathToLink);
        targetModel.add(link);

        String linkWithParameter = TARGET_BASE_PATH + "/" + "{id}";

        when(targetService.findById(targetId)).thenReturn(Optional.of(targetNode));
        when(targetModelAssembler.toModel(targetNode)).thenReturn(targetModel);

        assertAll(() -> mockMvc.perform(get(linkWithParameter, targetId))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToLink)))
                        .andExpect(jsonPath("id", is(targetId.intValue())))
                        .andExpect(jsonPath("target", is(targetNode.getTarget())))
                        .andExpect(jsonPath("countryOfOrigin.id", is(countryId.intValue())))
                        .andExpect(jsonPath("countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("countryOfOrigin.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("countryOfOrigin.region.links").isEmpty()),
                () -> verify(targetService, times(1)).findById(targetId),
                () -> verifyNoMoreInteractions(targetService),
                () -> verify(targetModelAssembler, times(1)).toModel(targetNode),
                () -> verifyNoMoreInteractions(targetModelAssembler),
                () -> verifyNoInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil));
    }

    @Test
    void when_find_target_but_target_does_not_exist_should_return_error_response() {

        Long targetId = 1L;

        String linkWithParameter = TARGET_BASE_PATH + "/" + "{id}";

        when(targetService.findById(targetId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, targetId))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]",
                                is("Could not find TargetModel with id: " + targetId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(targetService, times(1)).findById(targetId),
                () -> verifyNoMoreInteractions(targetService),
                () -> verifyNoInteractions(targetModelAssembler),
                () -> verifyNoInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil));
    }

    private Target createTarget(ObjectType type) {

        switch (type) {

            case NODE:

                counterForUtilMethodsNode++;

                return targetBuilder.withId((long) counterForUtilMethodsNode)
                        .withTarget("target" + counterForUtilMethodsNode)
                        .withCountry(countryBuilder.withName("country" + counterForUtilMethodsNode)
                                .withRegion(regionBuilder.withName("region" + counterForUtilMethodsNode)
                                        .build(ObjectType.NODE))
                                .build(ObjectType.NODE))
                        .build(ObjectType.NODE);

            case MODEL:

                counterForUtilMethodsModel++;

                TargetModel targetModel = (TargetModel) targetBuilder.withId((long) counterForUtilMethodsModel).withTarget("target" + counterForUtilMethodsModel)
                        .withCountry(countryBuilder.withName("country" + counterForUtilMethodsModel)
                                .withRegion(regionBuilder.withName("region" + counterForUtilMethodsNode)
                                        .build(ObjectType.MODEL))
                                .build(ObjectType.MODEL)
                        )
                        .build(ObjectType.MODEL);

                String pathToTargetLink = TARGET_BASE_PATH + counterForUtilMethodsModel;
                targetModel.add(new Link(pathToTargetLink));

                return targetModel;

            default:
                throw new RuntimeException("Invalid type");
        }
    }
}