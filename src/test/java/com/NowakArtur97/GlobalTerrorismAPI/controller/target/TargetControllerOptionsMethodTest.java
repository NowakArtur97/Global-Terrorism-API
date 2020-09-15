package com.NowakArtur97.GlobalTerrorismAPI.controller.target;

import com.NowakArtur97.GlobalTerrorismAPI.feature.target.TargetModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestController;
import com.NowakArtur97.GlobalTerrorismAPI.feature.target.TargetController;
import com.NowakArtur97.GlobalTerrorismAPI.feature.target.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.target.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.target.TargetNode;
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
@Tag("TargetController_Tests")
class TargetControllerOptionsMethodTest {

    private final String TARGET_BASE_PATH = "http://localhost:8080/api/v1/targets";

    private MockMvc mockMvc;

    private GenericRestController<TargetModel, TargetDTO> targetController;

    @Mock
    private GenericService<TargetNode, TargetDTO> targetService;

    @Mock
    private TargetModelAssembler modelAssembler;

    @Mock
    private PagedResourcesAssembler<TargetNode> pagedResourcesAssembler;

    @Mock
    private PatchHelper patchHelper;

    @Mock
    private ViolationHelper<TargetNode, TargetDTO> violationHelper;

    @BeforeEach
    private void setUp() {

        targetController = new TargetController(targetService, modelAssembler, pagedResourcesAssembler,
                patchHelper, violationHelper);

        mockMvc = MockMvcBuilders.standaloneSetup(targetController).build();
    }

    @Test
    @SneakyThrows
    void when_show_endpoint_options_for_collection_should_show_possible_request_methods() {

        MvcResult mvcResult = mockMvc.perform(options(TARGET_BASE_PATH)).andReturn();
        String allowedMethods = mvcResult.getResponse().getHeader("allow");

        System.out.println(allowedMethods);

        assertAll(
                () -> assertNotNull(allowedMethods, () -> "should header contain allowed methods, but wasn't"),
                () -> assertTrue(allowedMethods.contains("GET"), () -> "should contain GET option, but was: " + allowedMethods),
                () -> assertTrue(allowedMethods.contains("POST"), () -> "should contain POST option, but was: " + allowedMethods),
                () -> assertTrue(allowedMethods.contains("OPTIONS"), () -> "should contain OPTIONS option, but was: " + allowedMethods),
                () -> verifyNoInteractions(targetService),
                () -> verifyNoInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    @SneakyThrows
    void when_show_endpoint_options_for_singular_resource_should_show_possible_request_methods() {

        Long eventId = 1L;

        String linkWithParameter = TARGET_BASE_PATH + "/" + "{id}";

        MvcResult mvcResult = mockMvc.perform(options(linkWithParameter, eventId)).andReturn();
        String allowedMethods = mvcResult.getResponse().getHeader("allow");

        assertAll(
                () -> assertNotNull(allowedMethods, () -> "should header contain allowed methods, but wasn't"),
                () -> assertTrue(allowedMethods.contains("GET"), () -> "should contain GET option, but was: " + allowedMethods),
                () -> assertTrue(allowedMethods.contains("PUT"), () -> "should contain PUT option, but was: " + allowedMethods),
                () -> assertTrue(allowedMethods.contains("PATCH"), () -> "should contain PATCH option, but was: " + allowedMethods),
                () -> assertTrue(allowedMethods.contains("DELETE"), () -> "should contain DELETE option, but was: " + allowedMethods),
                () -> assertTrue(allowedMethods.contains("OPTIONS"), () -> "should contain OPTIONS option, but was: " + allowedMethods),
                () -> verifyNoInteractions(targetService),
                () -> verifyNoInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }
}