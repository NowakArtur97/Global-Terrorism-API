package com.NowakArtur97.GlobalTerrorismAPI.feature.city;

import com.NowakArtur97.GlobalTerrorismAPI.feature.city.CityModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.feature.city.CityController;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestController;
import com.NowakArtur97.GlobalTerrorismAPI.feature.city.CityDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.city.CityModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.city.CityNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.patch.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.violation.ViolationHelper;
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

    @BeforeEach
    private void setUp() {

        cityController = new CityController(cityService, modelAssembler, pagedResourcesAssembler, patchHelper,
                violationHelper);

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
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
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
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }
}
