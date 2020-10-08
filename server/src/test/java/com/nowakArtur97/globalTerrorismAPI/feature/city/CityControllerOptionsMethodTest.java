package com.nowakArtur97.globalTerrorismAPI.feature.city;

import com.nowakArtur97.globalTerrorismAPI.common.controller.GenericRestController;
import com.nowakArtur97.globalTerrorismAPI.common.service.GenericService;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.nowakArtur97.globalTerrorismAPI.common.util.PatchUtil;
import com.nowakArtur97.globalTerrorismAPI.common.util.ViolationUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("CityController_Tests")
class CityControllerOptionsMethodTest {

    private final String CITY_BASE_PATH = "http://localhost:8080/api/v1/cities";

    private MockMvc mockMvc;

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

    @BeforeEach
    private void setUp() {

        GenericRestController<CityModel, CityDTO> cityController =
                new CityController(cityService, modelAssembler, pagedResourcesAssembler, patchUtil, violationUtil);

        mockMvc = MockMvcBuilders.standaloneSetup(cityController).build();
    }

    @Test
    @SneakyThrows
    void when_show_endpoint_options_for_collection_should_show_possible_request_methods() {

        MvcResult mvcResult = mockMvc.perform(options(CITY_BASE_PATH)).andReturn();
        String allowedMethods = mvcResult.getResponse().getHeader("allow");

        assertAll(
                () -> assertNotNull(allowedMethods, () -> "should header contain allowed methods, but wasn't"),
                () -> assertTrue(allowedMethods.contains("GET"), () -> "should contain GET option, but was: " + allowedMethods),
                () -> assertTrue(allowedMethods.contains("POST"), () -> "should contain POST option, but was: " + allowedMethods),
                () -> assertTrue(allowedMethods.contains("OPTIONS"), () -> "should contain OPTIONS option, but was: " + allowedMethods),
                () -> verifyNoInteractions(cityService),
                () -> verifyNoInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil));
    }

    @Test
    @SneakyThrows
    void when_show_endpoint_options_for_singular_resource_should_show_possible_request_methods() {

        Long cityId = 1L;

        String linkWithParameter = CITY_BASE_PATH + "/" + "{id}";

        MvcResult mvcResult = mockMvc.perform(options(linkWithParameter, cityId)).andReturn();
        String allowedMethods = mvcResult.getResponse().getHeader("allow");

        assertAll(
                () -> assertNotNull(allowedMethods, () -> "should header contain allowed methods, but wasn't"),
                () -> assertTrue(allowedMethods.contains("GET"), () -> "should contain GET option, but was: " + allowedMethods),
                () -> assertTrue(allowedMethods.contains("PUT"), () -> "should contain PUT option, but was: " + allowedMethods),
                () -> assertTrue(allowedMethods.contains("PATCH"), () -> "should contain PATCH option, but was: " + allowedMethods),
                () -> assertTrue(allowedMethods.contains("DELETE"), () -> "should contain DELETE option, but was: " + allowedMethods),
                () -> assertTrue(allowedMethods.contains("OPTIONS"), () -> "should contain OPTIONS option, but was: " + allowedMethods),
                () -> verifyNoInteractions(cityService),
                () -> verifyNoInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil));
    }
}
