package com.NowakArtur97.GlobalTerrorismAPI.controller.group;

import com.NowakArtur97.GlobalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestController;
import com.NowakArtur97.GlobalTerrorismAPI.dto.GroupDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.GroupModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.GroupBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.ViolationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("GroupController_Tests")
class GroupControllerDeleteMethodTest {

    private final String GROUP_BASE_PATH = "http://localhost:8080/api/groups";

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

    private GroupBuilder groupBuilder;
    private EventBuilder eventBuilder;
    private TargetBuilder targetBuilder;

    @BeforeEach
    private void setUp() {

        groupController = new GroupController(groupService, modelAssembler, pagedResourcesAssembler,
                patchHelper, violationHelper);

        mockMvc = MockMvcBuilders.standaloneSetup(groupController).setControllerAdvice(new GenericRestControllerAdvice())
                .build();

        groupBuilder = new GroupBuilder();
        eventBuilder = new EventBuilder();
        targetBuilder = new TargetBuilder();
    }

    @Test
    void when_delete_existing_group_should_not_return_content() {

        Long groupId = 1L;

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
        GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        when(groupService.delete(groupId)).thenReturn(Optional.of(groupNode));

        assertAll(
                () -> mockMvc.perform(delete(linkWithParameter, groupId)).andExpect(status().isNoContent())
                        .andExpect(jsonPath("$").doesNotExist()),
                () -> verify(groupService, times(1)).delete(groupId), () -> verifyNoMoreInteractions(groupService),
                () -> verifyNoInteractions(modelAssembler), () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper), () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_delete_group_but_group_not_exists_should_return_error_response() {

        Long groupId = 1L;

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        when(groupService.delete(groupId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(delete(linkWithParameter, groupId)).andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty()).andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find GroupModel with id: " + groupId))),
                () -> verify(groupService, times(1)).delete(groupId), () -> verifyNoMoreInteractions(groupService),
                () -> verifyNoInteractions(modelAssembler), () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper), () -> verifyNoInteractions(pagedResourcesAssembler));
    }
}
