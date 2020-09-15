package com.NowakArtur97.GlobalTerrorismAPI.feature.group;

import com.NowakArtur97.GlobalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestController;
import com.NowakArtur97.GlobalTerrorismAPI.feature.group.GroupController;
import com.NowakArtur97.GlobalTerrorismAPI.feature.group.GroupDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.group.GroupModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.group.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.GroupBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.patch.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.violation.ViolationHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
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
@Tag("GroupController_Tests")
class GroupControllerDeleteMethodTest {

    private final String GROUP_BASE_PATH = "http://localhost:8080/api/v1/groups";

    private MockMvc mockMvc;

    private GenericRestController<GroupModel, GroupDTO> groupController;

    @Mock
    private GenericService<GroupNode, GroupDTO> groupService;

    @Mock
    private RepresentationModelAssemblerSupport<GroupNode, GroupModel> modelAssembler;

    @Mock
    private PagedResourcesAssembler<GroupNode> pagedResourcesAssembler;

    @Mock
    private PatchHelper patchHelper;

    @Mock
    private ViolationHelper<GroupNode, GroupDTO> violationHelper;

    private static GroupBuilder groupBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        groupBuilder = new GroupBuilder();
    }

    @BeforeEach
    private void setUp() {

        groupController = new GroupController(groupService, modelAssembler, pagedResourcesAssembler,
                patchHelper, violationHelper);

        mockMvc = MockMvcBuilders.standaloneSetup(groupController).setControllerAdvice(new GenericRestControllerAdvice())
                .build();
    }

    @Test
    void when_delete_existing_group_should_not_return_content() {

        Long groupId = 1L;

        GroupNode groupNode = (GroupNode) groupBuilder.build(ObjectType.NODE);

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        when(groupService.delete(groupId)).thenReturn(Optional.of(groupNode));

        assertAll(
                () -> mockMvc.perform(delete(linkWithParameter, groupId))
                        .andExpect(status().isNoContent())
                        .andExpect(jsonPath("$").doesNotExist()),
                () -> verify(groupService, times(1)).delete(groupId),
                () -> verifyNoMoreInteractions(groupService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_delete_group_but_group_does_not_exist_should_return_error_response() {

        Long groupId = 1L;

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        when(groupService.delete(groupId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(delete(linkWithParameter, groupId))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty()).andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find GroupModel with id: " + groupId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(groupService, times(1)).delete(groupId),
                () -> verifyNoMoreInteractions(groupService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }
}
