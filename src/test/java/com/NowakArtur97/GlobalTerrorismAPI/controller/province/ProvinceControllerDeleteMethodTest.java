package com.NowakArtur97.GlobalTerrorismAPI.controller.province;

import com.NowakArtur97.GlobalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestController;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceController;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.ProvinceBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.patch.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.violation.ViolationHelper;
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
@Tag("ProvinceController_Tests")
class ProvinceControllerDeleteMethodTest {

    private final String PROVINCE_BASE_PATH = "http://localhost:8080/api/v1/provinces";
    private final String LINK_WITH_PARAMETER = PROVINCE_BASE_PATH + "/" + "{id}";

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

    private static ProvinceBuilder provinceBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        provinceBuilder = new ProvinceBuilder();
    }

    @BeforeEach
    private void setUp() {

        provinceController = new ProvinceController(provinceService, modelAssembler, pagedResourcesAssembler, patchHelper,
                violationHelper);

        mockMvc = MockMvcBuilders.standaloneSetup(provinceController).setControllerAdvice(new GenericRestControllerAdvice())
                .build();
    }

    @Test
    void when_delete_existing_province_should_not_return_content() {

        Long provinceId = 1L;

        ProvinceNode provinceNode = (ProvinceNode) provinceBuilder.build(ObjectType.NODE);

        when(provinceService.delete(provinceId)).thenReturn(Optional.of(provinceNode));

        assertAll(
                () -> mockMvc.perform(delete(LINK_WITH_PARAMETER, provinceId))
                        .andExpect(status().isNoContent())
                        .andExpect(jsonPath("$").doesNotExist()),
                () -> verify(provinceService, times(1)).delete(provinceId),
                () -> verifyNoMoreInteractions(provinceService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_delete_province_but_province_does_not_exist_should_return_error_response() {

        Long provinceId = 1L;

        when(provinceService.delete(provinceId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(delete(LINK_WITH_PARAMETER, provinceId)).andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find ProvinceModel with id: " + provinceId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(provinceService, times(1)).delete(provinceId),
                () -> verifyNoMoreInteractions(provinceService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }
}
