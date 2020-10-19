package com.nowakArtur97.globalTerrorismAPI.feature.target;

import com.nowakArtur97.globalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.nowakArtur97.globalTerrorismAPI.common.controller.GenericRestController;
import com.nowakArtur97.globalTerrorismAPI.common.service.GenericService;
import com.nowakArtur97.globalTerrorismAPI.common.util.PatchUtil;
import com.nowakArtur97.globalTerrorismAPI.common.util.ViolationUtil;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
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
@Tag("TargetController_Tests")
class TargetControllerDeleteMethodTest {

    private final String TARGET_BASE_PATH = "http://localhost:8080/api/v1/targets";
    private final String LINK_WITH_PARAMETER = TARGET_BASE_PATH + "/" + "{id}";

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

    private static TargetBuilder targetBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        targetBuilder = new TargetBuilder();
    }

    @BeforeEach
    private void setUp() {

        GenericRestController<TargetModel, TargetDTO> targetController
                = new TargetController(targetService, targetModelAssembler, pagedResourcesAssembler, patchUtil,
                violationUtil);

        mockMvc = MockMvcBuilders.standaloneSetup(targetController).setControllerAdvice(new GenericRestControllerAdvice())
                .build();
    }

    @Test
    void when_delete_existing_target_should_not_return_content() {

        Long targetId = 1L;

        TargetNode targetNode = (TargetNode) targetBuilder.withId(targetId).build(ObjectType.NODE);

        when(targetService.delete(targetId)).thenReturn(Optional.of(targetNode));

        assertAll(() -> mockMvc.perform(delete(LINK_WITH_PARAMETER, targetId))
                        .andExpect(status().isNoContent())
                        .andExpect(jsonPath("$").doesNotExist()),
                () -> verify(targetService, times(1)).delete(targetId),
                () -> verifyNoMoreInteractions(targetService),
                () -> verifyNoInteractions(targetModelAssembler),
                () -> verifyNoInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil));
    }

    @Test
    void when_delete_target_but_target_does_not_exist_should_return_error_response() {

        Long targetId = 1L;

        when(targetService.delete(targetId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(delete(LINK_WITH_PARAMETER, targetId))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find TargetModel with id: " + targetId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(targetService, times(1)).delete(targetId),
                () -> verifyNoMoreInteractions(targetService),
                () -> verifyNoInteractions(targetModelAssembler),
                () -> verifyNoInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchUtil),
                () -> verifyNoInteractions(violationUtil));
    }
}