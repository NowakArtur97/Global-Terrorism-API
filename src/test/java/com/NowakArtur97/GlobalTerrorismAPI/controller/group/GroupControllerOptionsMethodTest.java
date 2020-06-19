package com.NowakArtur97.GlobalTerrorismAPI.controller.group;

import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestController;
import com.NowakArtur97.GlobalTerrorismAPI.dto.GroupDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.GroupModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.ViolationHelper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("GroupController_Tests")
class GroupControllerOptionsMethodTest {

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

    @BeforeEach
    private void setUp() {

        groupController = new GroupController(groupService, modelAssembler, pagedResourcesAssembler,
                patchHelper, violationHelper);

        mockMvc = MockMvcBuilders.standaloneSetup(groupController).build();
    }

    @Test
    @SneakyThrows
    void when_show_endpoint_options_for_collection_should_show_possible_request_methods() {

        MvcResult mvcResult = mockMvc.perform(options(GROUP_BASE_PATH)).andReturn();
        String allowedMethods = mvcResult.getResponse().getHeader("allow");

        System.out.println(allowedMethods);

        assertAll(
                () -> assertNotNull(allowedMethods, () -> "should header contain allowed methods, but wasn't"),
                () -> assertTrue(allowedMethods.contains("GET"), () -> "should contain GET option, but was: " + allowedMethods),
                () -> assertTrue(allowedMethods.contains("POST"), () -> "should contain POST option, but was: " + allowedMethods),
                () -> assertTrue(allowedMethods.contains("OPTIONS"), () -> "should contain OPTIONS option, but was: " + allowedMethods),
                () -> verifyNoInteractions(groupService), () -> verifyNoInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(modelAssembler), () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    @SneakyThrows
    void when_show_endpoint_options_for_singular_resource_should_show_possible_request_methods() {

        Long eventId = 1L;

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        MvcResult mvcResult = mockMvc.perform(options(linkWithParameter, eventId)).andReturn();
        String allowedMethods = mvcResult.getResponse().getHeader("allow");

        assertAll(
                () -> assertNotNull(allowedMethods, () -> "should header contain allowed methods, but wasn't"),
                () -> assertTrue(allowedMethods.contains("GET"), () -> "should contain GET option, but was: " + allowedMethods),
                () -> assertTrue(allowedMethods.contains("PUT"), () -> "should contain PUT option, but was: " + allowedMethods),
                () -> assertTrue(allowedMethods.contains("PATCH"), () -> "should contain PATCH option, but was: " + allowedMethods),
                () -> assertTrue(allowedMethods.contains("DELETE"), () -> "should contain DELETE option, but was: " + allowedMethods),
                () -> assertTrue(allowedMethods.contains("OPTIONS"), () -> "should contain OPTIONS option, but was: " + allowedMethods),
                () -> verifyNoInteractions(groupService), () -> verifyNoInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(modelAssembler), () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }
}