package com.NowakArtur97.GlobalTerrorismAPI.controller.province;

import com.NowakArtur97.GlobalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.ProvinceModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Province;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestController;
import com.NowakArtur97.GlobalTerrorismAPI.dto.ProvinceDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.CountryModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.ProvinceModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.ProvinceNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.CountryBuilder;
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
@Tag("ProvinceController_Tests")
class ProvinceControllerGetMethodTest {

    private static int counterForUtilMethodsModel = 0;
    private static int counterForUtilMethodsNode = 0;

    private final String PROVINCE_BASE_PATH = "http://localhost:8080/api/v1/provinces";

    private MockMvc mockMvc;

    private GenericRestController<ProvinceModel, ProvinceDTO> provinceController;

    @Mock
    private GenericService<ProvinceNode, ProvinceDTO> provinceService;

    @Mock
    private ProvinceModelAssembler modelAssembler;

    @Mock
    private PagedResourcesAssembler<ProvinceNode> pagedResourcesAssembler;

    @Mock
    private PatchHelper patchHelper;

    @Mock
    private ViolationHelper<ProvinceNode, ProvinceDTO> violationHelper;

    private static CountryBuilder countryBuilder;
    private static ProvinceBuilder provinceBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        countryBuilder = new CountryBuilder();
        provinceBuilder = new ProvinceBuilder();
    }

    @BeforeEach
    private void setUp() {

        provinceController = new ProvinceController(provinceService, modelAssembler, pagedResourcesAssembler, patchHelper,
                violationHelper);

        mockMvc = MockMvcBuilders.standaloneSetup(provinceController)
                .setControllerAdvice(new GenericRestControllerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void when_find_all_provinces_with_default_parameters_in_link_and_provinces_exist_should_return_all_provinces() {

        ProvinceNode provinceNode1 = (ProvinceNode) createProvince(ObjectType.NODE);
        ProvinceNode provinceNode2 = (ProvinceNode) createProvince(ObjectType.NODE);
        ProvinceNode provinceNode3 = (ProvinceNode) createProvince(ObjectType.NODE);
        ProvinceNode provinceNode4 = (ProvinceNode) createProvince(ObjectType.NODE);

        ProvinceModel provinceModel1 = (ProvinceModel) createProvince(ObjectType.MODEL);
        ProvinceModel provinceModel2 = (ProvinceModel) createProvince(ObjectType.MODEL);
        ProvinceModel provinceModel3 = (ProvinceModel) createProvince(ObjectType.MODEL);
        ProvinceModel provinceModel4 = (ProvinceModel) createProvince(ObjectType.MODEL);

        List<ProvinceNode> provinceNodesListExpected = List.of(provinceNode1, provinceNode2, provinceNode3, provinceNode4);
        List<ProvinceModel> provinceModelsListExpected = List.of(provinceModel1, provinceModel2, provinceModel3, provinceModel4);
        Page<ProvinceNode> provincesExpected = new PageImpl<>(provinceNodesListExpected);

        int sizeExpected = 20;
        int totalElementsExpected = 4;
        int totalPagesExpected = 1;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = PROVINCE_BASE_PATH + urlParameters1;
        String lastPageLink = PROVINCE_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<ProvinceModel> resources = new PagedModel<>(provinceModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(provinceService.findAll(pageable)).thenReturn(provincesExpected);
        when(pagedResourcesAssembler.toModel(provincesExpected, modelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content[0].id", is(provinceModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].name", is(provinceModel1.getName())))
                        .andExpect(jsonPath("content[0].country.id", is(provinceModel1.getCountry().getId().intValue())))
                        .andExpect(jsonPath("content[0].country.name", is(provinceModel1.getCountry().getName())))
                        .andExpect(jsonPath("content[0].links[0].href",
                                is(provinceModel1.getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[1].id", is(provinceModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].name", is(provinceModel2.getName())))
                        .andExpect(jsonPath("content[1].country.id", is(provinceModel2.getCountry().getId().intValue())))
                        .andExpect(jsonPath("content[1].country.name", is(provinceModel2.getCountry().getName())))
                        .andExpect(jsonPath("content[1].links[0].href",
                                is(provinceModel2.getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[2].id", is(provinceModel3.getId().intValue())))
                        .andExpect(jsonPath("content[2].name", is(provinceModel3.getName())))
                        .andExpect(jsonPath("content[2].country.id", is(provinceModel3.getCountry().getId().intValue())))
                        .andExpect(jsonPath("content[2].country.name", is(provinceModel3.getCountry().getName())))
                        .andExpect(jsonPath("content[2].links[0].href",
                                is(provinceModel3.getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[3].id", is(provinceModel4.getId().intValue())))
                        .andExpect(jsonPath("content[3].name", is(provinceModel4.getName())))
                        .andExpect(jsonPath("content[3].country.id", is(provinceModel4.getCountry().getId().intValue())))
                        .andExpect(jsonPath("content[3].country.name", is(provinceModel4.getCountry().getName())))
                        .andExpect(jsonPath("content[3].links[0].href",
                                is(provinceModel4.getLink("self").get().getHref())))

                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(provinceService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(provinceService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(provincesExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    void when_find_all_provinces_with_changed_parameters_in_link_and_provinces_exist_should_return_all_provinces() {

        ProvinceNode provinceNode1 = (ProvinceNode) createProvince(ObjectType.NODE);
        ProvinceNode provinceNode2 = (ProvinceNode) createProvince(ObjectType.NODE);
        ProvinceNode provinceNode3 = (ProvinceNode) createProvince(ObjectType.NODE);
        ProvinceNode provinceNode4 = (ProvinceNode) createProvince(ObjectType.NODE);

        ProvinceModel provinceModel1 = (ProvinceModel) createProvince(ObjectType.MODEL);
        ProvinceModel provinceModel2 = (ProvinceModel) createProvince(ObjectType.MODEL);
        ProvinceModel provinceModel3 = (ProvinceModel) createProvince(ObjectType.MODEL);

        List<ProvinceModel> provinceModelsListExpected = List.of(provinceModel1, provinceModel2, provinceModel3);
        List<ProvinceNode> provinceNodesListExpected = List.of(provinceNode1, provinceNode2, provinceNode3, provinceNode4);

        int sizeExpected = 3;
        int totalElementsExpected = 4;
        int totalPagesExpected = 2;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 1;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);
        PageImpl<ProvinceNode> provincesExpected = new PageImpl<>(provinceNodesListExpected, pageable, provinceNodesListExpected.size());

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = PROVINCE_BASE_PATH + urlParameters1;
        String lastPageLink = PROVINCE_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<ProvinceModel> resources = new PagedModel<>(provinceModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(provinceService.findAll(pageable)).thenReturn(provincesExpected);
        when(pagedResourcesAssembler.toModel(provincesExpected, modelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content[0].id", is(provinceModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].name", is(provinceModel1.getName())))
                        .andExpect(jsonPath("content[0].country.id", is(provinceModel1.getCountry().getId().intValue())))
                        .andExpect(jsonPath("content[0].country.name", is(provinceModel1.getCountry().getName())))
                        .andExpect(jsonPath("content[0].links[0].href",
                                is(provinceModel1.getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[1].id", is(provinceModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].name", is(provinceModel2.getName())))
                        .andExpect(jsonPath("content[1].country.id", is(provinceModel2.getCountry().getId().intValue())))
                        .andExpect(jsonPath("content[1].country.name", is(provinceModel2.getCountry().getName())))
                        .andExpect(jsonPath("content[1].links[0].href",
                                is(provinceModel2.getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[2].id", is(provinceModel3.getId().intValue())))
                        .andExpect(jsonPath("content[2].name", is(provinceModel3.getName())))
                        .andExpect(jsonPath("content[2].country.id", is(provinceModel3.getCountry().getId().intValue())))
                        .andExpect(jsonPath("content[2].country.name", is(provinceModel3.getCountry().getName())))
                        .andExpect(jsonPath("content[2].links[0].href",
                                is(provinceModel3.getLink("self").get().getHref())))

                        .andExpect(jsonPath("content[3]").doesNotExist())
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(provinceService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(provinceService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(provincesExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    void when_find_all_provinces_but_provinces_not_exist_should_return_empty_list() {

        List<ProvinceNode> provincesListExpected = new ArrayList<>();

        List<ProvinceModel> modelsListExpected = new ArrayList<>();

        Page<ProvinceNode> provincesExpected = new PageImpl<>(provincesListExpected);

        int sizeExpected = 20;
        int totalElementsExpected = 0;
        int totalPagesExpected = 0;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = PROVINCE_BASE_PATH + urlParameters1;
        String lastPageLink = PROVINCE_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<ProvinceModel> resources = new PagedModel<>(modelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(provinceService.findAll(pageable)).thenReturn(provincesExpected);
        when(pagedResourcesAssembler.toModel(provincesExpected, modelAssembler)).thenReturn(resources);

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
                () -> verify(provinceService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(provinceService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(provincesExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    void when_find_existing_province_should_return_province() {

        Long provinceId = 2L;

        CountryNode countryNode = (CountryNode) countryBuilder.build(ObjectType.NODE);
        ProvinceNode provinceNode = (ProvinceNode) provinceBuilder.withId(provinceId).withCountry(countryNode)
                .build(ObjectType.NODE);

        CountryModel countryModel = (CountryModel) countryBuilder.build(ObjectType.MODEL);
        ProvinceModel provinceModel = (ProvinceModel) provinceBuilder.withId(provinceId).withCountry(countryModel)
                .build(ObjectType.MODEL);
        String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + provinceNode.getId().intValue();
        provinceModel.add(new Link(pathToProvinceLink));

        String linkWithParameter = PROVINCE_BASE_PATH + "/" + "{id}";

        when(provinceService.findById(provinceId)).thenReturn(Optional.of(provinceNode));
        when(modelAssembler.toModel(provinceNode)).thenReturn(provinceModel);

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, provinceId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToProvinceLink)))
                        .andExpect(jsonPath("links[1].href").doesNotExist())
                        .andExpect(jsonPath("id", is(provinceModel.getId().intValue())))
                        .andExpect(jsonPath("name", is(provinceModel.getName())))
                        .andExpect(jsonPath("country.id", is(countryModel.getId().intValue())))
                        .andExpect(jsonPath("country.name", is(countryModel.getName()))),
                () -> verify(provinceService, times(1)).findById(provinceId),
                () -> verifyNoMoreInteractions(provinceService),
                () -> verify(modelAssembler, times(1)).toModel(provinceNode),
                () -> verifyNoMoreInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_find_province_but_province_does_not_exist_should_return_error_response() {

        Long provinceId = 1L;

        String linkWithParameter = PROVINCE_BASE_PATH + "/" + "{id}";

        when(provinceService.findById(provinceId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, provinceId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find ProvinceModel with id: " + provinceId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(provinceService, times(1)).findById(provinceId),
                () -> verifyNoMoreInteractions(provinceService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    private Province createProvince(ObjectType type) {

        switch (type) {

            case NODE:

                counterForUtilMethodsNode++;

                CountryNode countryNode = (CountryNode) countryBuilder.withId((long) counterForUtilMethodsNode)
                        .withName("country" + counterForUtilMethodsNode)
                        .build(ObjectType.NODE);

                return provinceBuilder.withId((long) counterForUtilMethodsNode).withName("province" + counterForUtilMethodsNode)
                        .withCountry(countryNode).build(ObjectType.NODE);

            case MODEL:

                counterForUtilMethodsModel++;

                CountryModel countryModel = (CountryModel) countryBuilder.withId((long) counterForUtilMethodsModel)
                        .withName("country" + counterForUtilMethodsModel)
                        .build(ObjectType.MODEL);

                ProvinceModel provinceModel = (ProvinceModel) provinceBuilder.withId((long) counterForUtilMethodsModel)
                        .withCountry(countryModel).build(ObjectType.MODEL);
                String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + counterForUtilMethodsModel;
                provinceModel.add(new Link(pathToProvinceLink));

                return provinceModel;

            default:
                throw new RuntimeException("Invalid type");
        }
    }
}
