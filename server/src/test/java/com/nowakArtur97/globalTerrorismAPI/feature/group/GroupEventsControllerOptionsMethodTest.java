package com.nowakArtur97.globalTerrorismAPI.feature.group;

import com.nowakArtur97.globalTerrorismAPI.feature.event.EventModel;
import com.nowakArtur97.globalTerrorismAPI.feature.event.EventNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.nowakArtur97.globalTerrorismAPI.common.util.PageUtil;
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
@Tag("GroupEventsController_Tests")
class GroupEventsControllerOptionsMethodTest {

    private final String GROUP_BASE_PATH = "http://localhost:8080/api/v1/groups";

    private MockMvc mockMvc;

    @Mock
    private GroupService groupService;

    @Mock
    private RepresentationModelAssemblerSupport<GroupNode, GroupModel> groupModelAssembler;

    @Mock
    private RepresentationModelAssemblerSupport<EventNode, EventModel> eventModelAssembler;

    @Mock
    private PagedResourcesAssembler<EventNode> eventsPagedResourcesAssembler;

    @Mock
    private PageUtil pageUtil;

    @BeforeEach
    private void setUp() {

        GroupEventsController groupEventsController = new GroupEventsController(groupService, groupModelAssembler,
                eventModelAssembler, eventsPagedResourcesAssembler, pageUtil);


        mockMvc = MockMvcBuilders.standaloneSetup(groupEventsController).build();
    }

    @Test
    @SneakyThrows
    void when_show_endpoint_options_for_group_events_should_show_possible_request_methods() {

        Long groupId = 1L;
        String linkWithParameter = GROUP_BASE_PATH + "/{id}/events";

        MvcResult mvcResult = mockMvc.perform(options(linkWithParameter, groupId)).andReturn();
        String allowedMethods = mvcResult.getResponse().getHeader("allow");

        assertAll(
                () -> assertNotNull(allowedMethods, () -> "should header contain allowed methods, but wasn't"),
                () -> assertTrue(allowedMethods.contains("GET"), () -> "should contain GET option, but was: " + allowedMethods),
                () -> assertTrue(allowedMethods.contains("POST"), () -> "should contain POST option, but was: " + allowedMethods),
                () -> assertTrue(allowedMethods.contains("DELETE"), () -> "should contain DELETE option, but was: " + allowedMethods),
                () -> assertTrue(allowedMethods.contains("OPTIONS"), () -> "should contain OPTIONS option, but was: " + allowedMethods),
                () -> verifyNoInteractions(groupService),
                () -> verifyNoInteractions(groupModelAssembler),
                () -> verifyNoInteractions(pageUtil),
                () -> verifyNoInteractions(eventModelAssembler),
                () -> verifyNoInteractions(eventsPagedResourcesAssembler));
    }
}