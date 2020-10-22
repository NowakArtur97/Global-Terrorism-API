package com.nowakArtur97.globalTerrorismAPI.feature.victim;

import com.nowakArtur97.globalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.nowakArtur97.globalTerrorismAPI.common.controller.BasicGenericRestController;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.VictimBuilder;
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
@Tag("VictimController_Tests")
class VictimControllerGetMethodTest {

    private static int counterForUtilMethodsModel = 0;
    private static int counterForUtilMethodsNode = 0;

    private final String VICTIM_BASE_PATH = "http://localhost:8080/api/v1/victims";

    private MockMvc mockMvc;

    @Mock
    private VictimService victimService;

    @Mock
    private VictimModelAssembler modelAssembler;

    @Mock
    private PagedResourcesAssembler<VictimNode> pagedResourcesAssembler;

    private static VictimBuilder victimBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        victimBuilder = new VictimBuilder();
    }

    @BeforeEach
    private void setUp() {

        BasicGenericRestController<VictimModel> victimController
                = new VictimController(victimService, modelAssembler, pagedResourcesAssembler);

        mockMvc = MockMvcBuilders.standaloneSetup(victimController)
                .setControllerAdvice(new GenericRestControllerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void when_find_all_victims_with_default_parameters_in_link_and_victims_exist_should_return_all_victims() {

        VictimNode victimNode1 = (VictimNode) createVictim(ObjectType.NODE);
        VictimNode victimNode2 = (VictimNode) createVictim(ObjectType.NODE);
        VictimNode victimNode3 = (VictimNode) createVictim(ObjectType.NODE);
        VictimNode victimNode4 = (VictimNode) createVictim(ObjectType.NODE);

        VictimModel victimModel1 = (VictimModel) createVictim(ObjectType.MODEL);
        VictimModel victimModel2 = (VictimModel) createVictim(ObjectType.MODEL);
        VictimModel victimModel3 = (VictimModel) createVictim(ObjectType.MODEL);
        VictimModel victimModel4 = (VictimModel) createVictim(ObjectType.MODEL);

        List<VictimNode> victimNodesListExpected = List.of(victimNode1, victimNode2, victimNode3, victimNode4);
        List<VictimModel> victimModelsListExpected = List.of(victimModel1, victimModel2, victimModel3, victimModel4);
        Page<VictimNode> victimsExpected = new PageImpl<>(victimNodesListExpected);

        int sizeExpected = 20;
        int totalElementsExpected = 4;
        int totalPagesExpected = 1;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = VICTIM_BASE_PATH + urlParameters1;
        String lastPageLink = VICTIM_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<VictimModel> resources = new PagedModel<>(victimModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(victimService.findAll(pageable)).thenReturn(victimsExpected);
        when(pagedResourcesAssembler.toModel(victimsExpected, modelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content[0].id", is(victimModel1.getId().intValue())))
                        .andExpect(
                                jsonPath("content[0].links[0].href",
                                        is(victimModel1.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[0].totalNumberOfFatalities",
                                is(victimModel1.getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("content[0].numberOfPerpetratorFatalities",
                                is(victimModel1.getNumberOfPerpetratorFatalities().intValue())))
                        .andExpect(jsonPath("content[0].totalNumberOfInjured",
                                is(victimModel1.getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("content[0].numberOfPerpetratorInjured",
                                is(victimModel1.getNumberOfPerpetratorInjured().intValue())))
                        .andExpect(jsonPath("content[0].valueOfPropertyDamage",
                                is(victimModel1.getValueOfPropertyDamage().intValue())))

                        .andExpect(jsonPath("content[1].id", is(victimModel2.getId().intValue())))
                        .andExpect(
                                jsonPath("content[1].links[0].href",
                                        is(victimModel2.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].totalNumberOfFatalities",
                                is(victimModel2.getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("content[1].numberOfPerpetratorFatalities",
                                is(victimModel2.getNumberOfPerpetratorFatalities().intValue())))
                        .andExpect(jsonPath("content[1].totalNumberOfInjured",
                                is(victimModel2.getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("content[1].numberOfPerpetratorInjured",
                                is(victimModel2.getNumberOfPerpetratorInjured().intValue())))
                        .andExpect(jsonPath("content[1].valueOfPropertyDamage",
                                is(victimModel2.getValueOfPropertyDamage().intValue())))

                        .andExpect(jsonPath("content[2].id", is(victimModel3.getId().intValue())))
                        .andExpect(
                                jsonPath("content[2].links[0].href",
                                        is(victimModel3.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].totalNumberOfFatalities",
                                is(victimModel3.getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("content[2].numberOfPerpetratorFatalities",
                                is(victimModel3.getNumberOfPerpetratorFatalities().intValue())))
                        .andExpect(jsonPath("content[2].totalNumberOfInjured",
                                is(victimModel3.getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("content[2].numberOfPerpetratorInjured",
                                is(victimModel3.getNumberOfPerpetratorInjured().intValue())))
                        .andExpect(jsonPath("content[2].valueOfPropertyDamage",
                                is(victimModel3.getValueOfPropertyDamage().intValue())))

                        .andExpect(jsonPath("content[3].id", is(victimModel4.getId().intValue())))
                        .andExpect(
                                jsonPath("content[3].links[0].href",
                                        is(victimModel4.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[3].totalNumberOfFatalities",
                                is(victimModel4.getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("content[3].numberOfPerpetratorFatalities",
                                is(victimModel4.getNumberOfPerpetratorFatalities().intValue())))
                        .andExpect(jsonPath("content[3].totalNumberOfInjured",
                                is(victimModel4.getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("content[3].numberOfPerpetratorInjured",
                                is(victimModel4.getNumberOfPerpetratorInjured().intValue())))
                        .andExpect(jsonPath("content[3].valueOfPropertyDamage",
                                is(victimModel4.getValueOfPropertyDamage().intValue())))

                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(victimService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(victimService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(victimsExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_find_all_victims_with_changed_parameters_in_link_and_victims_exist_should_return_all_victims() {

        VictimNode victimNode1 = (VictimNode) createVictim(ObjectType.NODE);
        VictimNode victimNode2 = (VictimNode) createVictim(ObjectType.NODE);
        VictimNode victimNode3 = (VictimNode) createVictim(ObjectType.NODE);
        VictimNode victimNode4 = (VictimNode) createVictim(ObjectType.NODE);

        VictimModel victimModel1 = (VictimModel) createVictim(ObjectType.MODEL);
        VictimModel victimModel2 = (VictimModel) createVictim(ObjectType.MODEL);
        VictimModel victimModel3 = (VictimModel) createVictim(ObjectType.MODEL);

        List<VictimModel> victimModelsListExpected = List.of(victimModel1, victimModel2, victimModel3);
        List<VictimNode> victimNodesListExpected = List.of(victimNode1, victimNode2, victimNode3, victimNode4);

        int sizeExpected = 3;
        int totalElementsExpected = 4;
        int totalPagesExpected = 2;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 1;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);
        PageImpl<VictimNode> victimsExpected = new PageImpl<>(victimNodesListExpected, pageable, victimNodesListExpected.size());

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = VICTIM_BASE_PATH + urlParameters1;
        String lastPageLink = VICTIM_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<VictimModel> resources = new PagedModel<>(victimModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(victimService.findAll(pageable)).thenReturn(victimsExpected);
        when(pagedResourcesAssembler.toModel(victimsExpected, modelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content[0].id", is(victimModel1.getId().intValue())))
                        .andExpect(
                                jsonPath("content[0].links[0].href",
                                        is(victimModel1.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[0].totalNumberOfFatalities",
                                is(victimModel1.getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("content[0].numberOfPerpetratorFatalities",
                                is(victimModel1.getNumberOfPerpetratorFatalities().intValue())))
                        .andExpect(jsonPath("content[0].totalNumberOfInjured",
                                is(victimModel1.getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("content[0].numberOfPerpetratorInjured",
                                is(victimModel1.getNumberOfPerpetratorInjured().intValue())))
                        .andExpect(jsonPath("content[0].valueOfPropertyDamage",
                                is(victimModel1.getValueOfPropertyDamage().intValue())))

                        .andExpect(jsonPath("content[1].id", is(victimModel2.getId().intValue())))
                        .andExpect(
                                jsonPath("content[1].links[0].href",
                                        is(victimModel2.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].totalNumberOfFatalities",
                                is(victimModel2.getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("content[1].numberOfPerpetratorFatalities",
                                is(victimModel2.getNumberOfPerpetratorFatalities().intValue())))
                        .andExpect(jsonPath("content[1].totalNumberOfInjured",
                                is(victimModel2.getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("content[1].numberOfPerpetratorInjured",
                                is(victimModel2.getNumberOfPerpetratorInjured().intValue())))
                        .andExpect(jsonPath("content[1].valueOfPropertyDamage",
                                is(victimModel2.getValueOfPropertyDamage().intValue())))

                        .andExpect(jsonPath("content[2].id", is(victimModel3.getId().intValue())))
                        .andExpect(
                                jsonPath("content[2].links[0].href",
                                        is(victimModel3.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].totalNumberOfFatalities",
                                is(victimModel3.getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("content[2].numberOfPerpetratorFatalities",
                                is(victimModel3.getNumberOfPerpetratorFatalities().intValue())))
                        .andExpect(jsonPath("content[2].totalNumberOfInjured",
                                is(victimModel3.getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("content[2].numberOfPerpetratorInjured",
                                is(victimModel3.getNumberOfPerpetratorInjured().intValue())))
                        .andExpect(jsonPath("content[2].valueOfPropertyDamage",
                                is(victimModel3.getValueOfPropertyDamage().intValue())))

                        .andExpect(jsonPath("content[3]").doesNotExist())
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(victimService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(victimService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(victimsExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_find_all_victims_but_victims_not_exist_should_return_empty_list() {

        List<VictimNode> victimsListExpected = new ArrayList<>();

        List<VictimModel> modelsListExpected = new ArrayList<>();

        Page<VictimNode> victimsExpected = new PageImpl<>(victimsListExpected);

        int sizeExpected = 20;
        int totalElementsExpected = 0;
        int totalPagesExpected = 0;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = VICTIM_BASE_PATH + urlParameters1;
        String lastPageLink = VICTIM_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<VictimModel> resources = new PagedModel<>(modelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(victimService.findAll(pageable)).thenReturn(victimsExpected);
        when(pagedResourcesAssembler.toModel(victimsExpected, modelAssembler)).thenReturn(resources);

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
                () -> verify(victimService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(victimService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(victimsExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_find_existing_victim_should_return_victim() {

        Long victimId = 1L;

        VictimNode victimNode = (VictimNode) victimBuilder.withId(victimId).build(ObjectType.NODE);
        VictimModel victimModel = (VictimModel) victimBuilder.build(ObjectType.MODEL);
        String pathToVictimLink = VICTIM_BASE_PATH + "/" + victimId;
        victimModel.add(new Link(pathToVictimLink));

        String linkWithParameter = VICTIM_BASE_PATH + "/" + "{id}";

        when(victimService.findById(victimId)).thenReturn(Optional.of(victimNode));
        when(modelAssembler.toModel(victimNode)).thenReturn(victimModel);

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, victimId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("id", is(victimModel.getId().intValue())))
                        .andExpect(jsonPath("links[0].href", is(pathToVictimLink)))
                        .andExpect(jsonPath("totalNumberOfFatalities",
                                is(victimModel.getTotalNumberOfFatalities().intValue())))
                        .andExpect(jsonPath("numberOfPerpetratorFatalities",
                                is(victimModel.getNumberOfPerpetratorFatalities().intValue())))
                        .andExpect(jsonPath("totalNumberOfInjured",
                                is(victimModel.getTotalNumberOfInjured().intValue())))
                        .andExpect(jsonPath("numberOfPerpetratorInjured",
                                is(victimModel.getNumberOfPerpetratorInjured().intValue())))
                        .andExpect(jsonPath("valueOfPropertyDamage",
                                is(victimModel.getValueOfPropertyDamage().intValue()))),
                () -> verify(victimService, times(1)).findById(victimId),
                () -> verifyNoMoreInteractions(victimService),
                () -> verify(modelAssembler, times(1)).toModel(victimNode),
                () -> verifyNoMoreInteractions(modelAssembler),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_find_victim_but_victim_does_not_exist_should_return_error_response() {

        Long victimId = 1L;

        String linkWithParameter = VICTIM_BASE_PATH + "/" + "{id}";

        when(victimService.findById(victimId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, victimId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]",
                                is("Could not find VictimModel with id: " + victimId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(victimService, times(1)).findById(victimId),
                () -> verifyNoMoreInteractions(victimService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    private Victim createVictim(ObjectType type) {

        long totalNumberOfFatalities = 20L;
        long numberOfPerpetratorFatalities = 10L;
        long totalNumberOfInjured = 14L;
        long numberOfPerpetratorInjured = 3L;
        long valueOfPropertyDamage = 10000L;

        switch (type) {

            case NODE:

                counterForUtilMethodsNode++;

                return victimBuilder.withId((long) counterForUtilMethodsNode)
                        .withTotalNumberOfFatalities(totalNumberOfFatalities + counterForUtilMethodsNode)
                        .withNumberOfPerpetratorFatalities(numberOfPerpetratorFatalities + counterForUtilMethodsNode)
                        .withTotalNumberOfInjured(totalNumberOfInjured + counterForUtilMethodsNode)
                        .withNumberOfPerpetratorInjured(numberOfPerpetratorInjured + counterForUtilMethodsNode)
                        .withValueOfPropertyDamage(valueOfPropertyDamage + counterForUtilMethodsNode)
                        .build(ObjectType.NODE);

            case MODEL:

                counterForUtilMethodsModel++;

                VictimModel victimModel = (VictimModel) victimBuilder.withId((long) counterForUtilMethodsNode)
                        .withTotalNumberOfFatalities(totalNumberOfFatalities + counterForUtilMethodsNode)
                        .withNumberOfPerpetratorFatalities(numberOfPerpetratorFatalities + counterForUtilMethodsNode)
                        .withTotalNumberOfInjured(totalNumberOfInjured + counterForUtilMethodsNode)
                        .withNumberOfPerpetratorInjured(numberOfPerpetratorInjured + counterForUtilMethodsNode)
                        .withValueOfPropertyDamage(valueOfPropertyDamage + counterForUtilMethodsNode)
                        .build(ObjectType.MODEL);

                String pathToVictimLink = VICTIM_BASE_PATH + "/" + counterForUtilMethodsModel;
                victimModel.add(new Link(pathToVictimLink));

                return victimModel;

            default:
                throw new RuntimeException("Invalid type");
        }
    }
}
