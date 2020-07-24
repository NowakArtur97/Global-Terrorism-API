package com.NowakArtur97.GlobalTerrorismAPI.controller.groupEvents;

import com.NowakArtur97.GlobalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.controller.group.GroupEventsController;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.GroupModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GroupService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.GroupBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.page.PageHelper;
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
@Tag("GroupEventsController_Tests")
class GroupEventsControllerDeleteMethodTest {

    private final String GROUP_BASE_PATH = "http://localhost:8080/api/v1/groups";

    private MockMvc mockMvc;

    private GroupEventsController groupEventsController;

    @Mock
    private GroupService groupService;

    @Mock
    private RepresentationModelAssemblerSupport<GroupNode, GroupModel> groupModelAssembler;

    @Mock
    private RepresentationModelAssemblerSupport<EventNode, EventModel> eventModelAssembler;

    @Mock
    private PagedResourcesAssembler<EventNode> eventsPagedResourcesAssembler;

    @Mock
    private PageHelper pageHelper;

    private static GroupBuilder groupBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        groupBuilder = new GroupBuilder();
    }

    @BeforeEach
    private void setUp() {

        groupEventsController = new GroupEventsController(groupService, groupModelAssembler, eventModelAssembler, eventsPagedResourcesAssembler, pageHelper);

        mockMvc = MockMvcBuilders.standaloneSetup(groupEventsController).setControllerAdvice(new GenericRestControllerAdvice())
                .build();
    }

    @Test
    void when_delete_existing_group_events_should_not_return_content() {

        Long groupId = 1L;

        GroupNode groupNode = (GroupNode) groupBuilder.build(ObjectType.NODE);

        String linkWithParameter = GROUP_BASE_PATH + "/{id}/events";

        when(groupService.deleteAllGroupEvents(groupId)).thenReturn(Optional.of(groupNode));

        assertAll(
                () -> mockMvc.perform(delete(linkWithParameter, groupId))
                        .andExpect(status().isNoContent())
                        .andExpect(jsonPath("$").doesNotExist()),
                () -> verify(groupService, times(1)).deleteAllGroupEvents(groupId),
                () -> verifyNoMoreInteractions(groupService),
                () -> verifyNoInteractions(groupModelAssembler),
                () -> verifyNoInteractions(pageHelper),
                () -> verifyNoInteractions(eventModelAssembler),
                () -> verifyNoInteractions(eventsPagedResourcesAssembler));
    }

    @Test
    void when_delete_group_events_but_group_does_not_exist_should_return_error_response() {

        Long groupId = 1L;

        String linkWithParameter = GROUP_BASE_PATH + "/{id}/events";

        when(groupService.deleteAllGroupEvents(groupId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(delete(linkWithParameter, groupId))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty()).andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find GroupModel with id: " + groupId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(groupService, times(1)).deleteAllGroupEvents(groupId),
                () -> verifyNoMoreInteractions(groupService),
                () -> verifyNoInteractions(groupModelAssembler),
                () -> verifyNoInteractions(pageHelper),
                () -> verifyNoInteractions(eventModelAssembler),
                () -> verifyNoInteractions(eventsPagedResourcesAssembler));
    }
}
