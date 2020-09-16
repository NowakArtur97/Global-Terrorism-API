package com.nowakArtur97.globalTerrorismAPI.feature.city;

import com.nowakArtur97.globalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.nowakArtur97.globalTerrorismAPI.common.controller.GenericRestController;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceNode;
import com.nowakArtur97.globalTerrorismAPI.common.service.GenericService;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.CityBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.ProvinceBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.nowakArtur97.globalTerrorismAPI.common.util.PatchUtil;
import com.nowakArtur97.globalTerrorismAPI.common.util.ViolationUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("CityController_Tests")
class CityControllerDeleteMethodTest {

    private final String PROVINCE_BASE_PATH = "http://localhost:8080/api/v1/cities";
    private final String LINK_WITH_PARAMETER = PROVINCE_BASE_PATH + "/" + "{id}";

    private MockMvc mockMvc;

    private GenericRestController<CityModel, CityDTO> cityController;

    @Mock
    private GenericService<CityNode, CityDTO> cityService;

    @Mock
    private CityModelAssembler modelAssembler;

    @Mock
    private PagedResourcesAssembler<CityNode> pagedResourcesAssembler;

    @Mock
    private PatchUtil patchUtil;

    @Mock
    private ViolationUtil<CityNode, CityDTO> violationUtil;

    private static ProvinceBuilder provinceBuilder;
    private static CityBuilder cityBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        provinceBuilder = new ProvinceBuilder();
        cityBuilder = new CityBuilder();
    }

    @BeforeEach
    private void setUp() {

        cityController = new CityController(cityService, modelAssembler, pagedResourcesAssembler, patchUtil,
                violationUtil);

        mockMvc = MockMvcBuilders.standaloneSetup(cityController).setControllerAdvice(new GenericRestControllerAdvice())
                .build();
    }

    @Test
    void when_delete_existing_city_should_not_return_content() {

        Long cityId = 1L;

        CityNode cityNode = (CityNode) cityBuilder.build(ObjectType.NODE);

        when(cityService.delete(cityId)).thenReturn(Optional.of(cityNode));

        assertAll(
                () -> mockMvc.perform(delete(LINK_WITH_PARAMETER, cityId))
                        .andExpect(status().isNoContent())
                        .andExpect(jsonPath("$").doesNotExist()),
                () -> verify(cityService, times(1)).delete(cityId),
                () -> verifyNoMoreInteractions(cityService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_delete_existing_city_with_province_should_not_return_content() {

        Long cityId = 2L;

        ProvinceNode provinceNode = (ProvinceNode) provinceBuilder.build(ObjectType.NODE);
        CityNode cityNode = (CityNode) cityBuilder.withId(cityId).withProvince(provinceNode).build(ObjectType.NODE);

        when(cityService.delete(cityId)).thenReturn(Optional.of(cityNode));

        assertAll(
                () -> mockMvc.perform(delete(LINK_WITH_PARAMETER, cityId))
                        .andExpect(status().isNoContent())
                        .andExpect(jsonPath("$").doesNotExist()),
                () -> verify(cityService, times(1)).delete(cityId),
                () -> verifyNoMoreInteractions(cityService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_delete_city_but_city_does_not_exist_should_return_error_response() {

        Long cityId = 1L;

        when(cityService.delete(cityId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(delete(LINK_WITH_PARAMETER, cityId)).andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find CityModel with id: " + cityId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(cityService, times(1)).delete(cityId),
                () -> verifyNoMoreInteractions(cityService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }
}
