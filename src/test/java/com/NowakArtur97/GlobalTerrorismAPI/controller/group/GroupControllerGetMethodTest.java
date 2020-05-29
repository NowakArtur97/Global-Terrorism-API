package com.NowakArtur97.GlobalTerrorismAPI.controller.group;

import com.NowakArtur97.GlobalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.GroupModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Group;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestController;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GroupController;
import com.NowakArtur97.GlobalTerrorismAPI.dto.GroupDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.GroupModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.GroupBuilder;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("GroupController_Tests")
class GroupControllerGetMethodTest {

    private static int counterForUtilMethods = 0;

    private final String GROUP_BASE_PATH = "http://localhost:8080/api/groups";
    private final String EVENT_BASE_PATH = "http://localhost:8080/api/events";

    private MockMvc mockMvc;

    private GenericRestController<GroupModel, GroupDTO> groupController;

    @Mock
    private GenericService<GroupNode, GroupDTO> groupService;

    @Mock
    private GroupModelAssembler groupModelAssembler;

    @Mock
    private PagedResourcesAssembler<GroupNode> pagedResourcesAssembler;

    @Mock
    private PatchHelper patchHelper;

    @Mock
    private ViolationHelper<GroupNode, GroupDTO> violationHelper;

    private GroupBuilder groupBuilder;
    private EventBuilder eventBuilder;

    @BeforeEach
    private void setUp() {

        groupController = new GroupController(groupService, groupModelAssembler, pagedResourcesAssembler,
                patchHelper, violationHelper);

        mockMvc = MockMvcBuilders.standaloneSetup(groupController).setControllerAdvice(new GenericRestControllerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();

        groupBuilder = new GroupBuilder();
        eventBuilder = new EventBuilder();
    }

    @Test
    void when_find_all_groups_with_default_parameters_in_link_and_groups_exist_should_return_all_groups() {

        GroupNode groupNode1 = (GroupNode) createGroupWithEvents(ObjectType.NODE);
        GroupModel groupModel1 = (GroupModel) createGroupWithEvents(ObjectType.MODEL);

        GroupNode groupNode2 = (GroupNode) createGroupWithEvents(ObjectType.NODE);
        GroupModel groupModel2 = (GroupModel) createGroupWithEvents(ObjectType.MODEL);

        GroupNode groupNode3 = (GroupNode) createGroupWithEvents(ObjectType.NODE);
        GroupModel groupModel3 = (GroupModel) createGroupWithEvents(ObjectType.MODEL);

        GroupNode groupNode4 = (GroupNode) createGroupWithEvents(ObjectType.NODE);
        GroupModel groupModel4 = (GroupModel) createGroupWithEvents(ObjectType.MODEL);

        List<GroupNode> groupsListExpected = List.of(groupNode1, groupNode2, groupNode3, groupNode4);
        List<GroupModel> groupModelsListExpected = List.of(groupModel1, groupModel2, groupModel3, groupModel4);
        Page<GroupNode> groupsExpected = new PageImpl<>(groupsListExpected);

        int sizeExpected = 100;
        int totalElementsExpected = 4;
        int totalPagesExpected = 1;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = GROUP_BASE_PATH + urlParameters1;
        String lastPageLink = GROUP_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<GroupModel> resources = new PagedModel<>(groupModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(groupService.findAll(pageable)).thenReturn(groupsExpected);
        when(pagedResourcesAssembler.toModel(groupsExpected, groupModelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink)).andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content[0].id", is(groupModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].name", is(groupModel1.getName())))
                        .andExpect(
                                jsonPath("content[0].links[0].href", is(groupModel1.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[0].eventsCaused", hasSize(2)))
                        .andExpect(jsonPath("content[1].id", is(groupModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].name", is(groupModel2.getName())))
                        .andExpect(
                                jsonPath("content[1].links[0].href", is(groupModel2.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].eventsCaused", hasSize(2)))
                        .andExpect(jsonPath("content[2].id", is(groupModel3.getId().intValue())))
                        .andExpect(jsonPath("content[2].name", is(groupModel3.getName())))
                        .andExpect(
                                jsonPath("content[2].links[0].href", is(groupModel3.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].eventsCaused", hasSize(2)))
                        .andExpect(jsonPath("content[3].id", is(groupModel4.getId().intValue())))
                        .andExpect(jsonPath("content[3].name", is(groupModel4.getName())))
                        .andExpect(
                                jsonPath("content[3].links[0].href", is(groupModel4.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[3].eventsCaused", hasSize(2)))
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(groupService, times(1)).findAll(pageable), () -> verifyNoMoreInteractions(groupService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(groupsExpected, groupModelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler), () -> verifyNoInteractions(groupModelAssembler), () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    void when_find_all_groups_with_changed_parameters_in_link_and_groups_exist_should_return_all_groups() {

        GroupNode groupNode1 = (GroupNode) createGroupWithEvents(ObjectType.NODE);
        GroupModel groupModel1 = (GroupModel) createGroupWithEvents(ObjectType.MODEL);

        GroupNode groupNode2 = (GroupNode) createGroupWithEvents(ObjectType.NODE);
        GroupModel groupModel2 = (GroupModel) createGroupWithEvents(ObjectType.MODEL);

        GroupNode groupNode3 = (GroupNode) createGroupWithEvents(ObjectType.NODE);
        GroupModel groupModel3 = (GroupModel) createGroupWithEvents(ObjectType.MODEL);

        GroupNode groupNode4 = (GroupNode) createGroupWithEvents(ObjectType.NODE);
        GroupModel groupModel4 = (GroupModel) createGroupWithEvents(ObjectType.MODEL);

        List<GroupNode> groupsListExpected = List.of(groupNode1, groupNode2, groupNode3, groupNode4);
        List<GroupModel> groupModelsListExpected = List.of(groupModel1, groupModel2, groupModel3, groupModel4);

        Page<GroupNode> groupsExpected = new PageImpl<>(groupsListExpected);

        int sizeExpected = 3;
        int totalElementsExpected = 4;
        int totalPagesExpected = 2;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 1;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = GROUP_BASE_PATH + urlParameters1;
        String lastPageLink = GROUP_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<GroupModel> resources = new PagedModel<>(groupModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(groupService.findAll(pageable)).thenReturn(groupsExpected);
        when(pagedResourcesAssembler.toModel(groupsExpected, groupModelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink)).andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content[0].id", is(groupModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].name", is(groupModel1.getName())))
                        .andExpect(
                                jsonPath("content[0].links[0].href", is(groupModel1.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[0].eventsCaused", hasSize(2)))
                        .andExpect(jsonPath("content[1].id", is(groupModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].name", is(groupModel2.getName())))
                        .andExpect(
                                jsonPath("content[1].links[0].href", is(groupModel2.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].eventsCaused", hasSize(2)))
                        .andExpect(jsonPath("content[2].id", is(groupModel3.getId().intValue())))
                        .andExpect(jsonPath("content[2].name", is(groupModel3.getName())))
                        .andExpect(
                                jsonPath("content[2].links[0].href", is(groupModel3.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].eventsCaused", hasSize(2)))
                        .andExpect(jsonPath("content[3].id", is(groupModel4.getId().intValue())))
                        .andExpect(jsonPath("content[3].name", is(groupModel4.getName())))
                        .andExpect(
                                jsonPath("content[3].links[0].href", is(groupModel4.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[3].eventsCaused", hasSize(2)))
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(groupService, times(1)).findAll(pageable), () -> verifyNoMoreInteractions(groupService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(groupsExpected, groupModelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(groupModelAssembler), () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    void when_find_all_groups_but_groups_not_exist_should_return_empty_list() {

        List<GroupNode> groupsListExpected = new ArrayList<>();

        List<GroupModel> groupModelsListExpected = new ArrayList<>();

        Page<GroupNode> groupsExpected = new PageImpl<>(groupsListExpected);

        int sizeExpected = 100;
        int totalElementsExpected = 0;
        int totalPagesExpected = 0;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = GROUP_BASE_PATH + urlParameters1;
        String lastPageLink = GROUP_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<GroupModel> resources = new PagedModel<>(groupModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(groupService.findAll(pageable)).thenReturn(groupsExpected);
        when(pagedResourcesAssembler.toModel(groupsExpected, groupModelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink)).andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink))).andExpect(jsonPath("content").isEmpty())
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(groupService, times(1)).findAll(pageable), () -> verifyNoMoreInteractions(groupService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(groupsExpected, groupModelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(groupModelAssembler), () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    void when_find_existing_group_should_return_group_with_events() {

        GroupNode groupNode = (GroupNode) createGroupWithEvents(ObjectType.NODE);
        GroupModel groupModel = (GroupModel) createGroupWithEvents(ObjectType.MODEL);

        String pathToLink = GROUP_BASE_PATH + "/" + groupModel.getId().intValue();
        Link link = new Link(pathToLink);
        groupModel.add(link);

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        when(groupService.findById(groupModel.getId())).thenReturn(Optional.of(groupNode));
        when(groupModelAssembler.toModel(groupNode)).thenReturn(groupModel);

        assertAll(() -> mockMvc.perform(get(linkWithParameter, groupModel.getId()))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToLink))).andExpect(jsonPath("id", is(groupModel.getId().intValue())))
                        .andExpect(jsonPath("name", is(groupModel.getName())))
                        .andExpect(jsonPath("eventsCaused[0].id", is(groupModel.getEventsCaused().get(0).getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].summary", is(groupModel.getEventsCaused().get(0).getSummary())))
                        .andExpect(jsonPath("eventsCaused[0].motive", is(groupModel.getEventsCaused().get(0).getMotive())))
                        .andExpect(jsonPath("eventsCaused[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(groupModel.getEventsCaused().get(0).getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[0].isSuicide", is(groupModel.getEventsCaused().get(0).getIsSuicide())))
                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(groupModel.getEventsCaused().get(0).getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents",
                                is(groupModel.getEventsCaused().get(0).getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("eventsCaused[0].links[0].href", is(groupModel.getEventsCaused().get(0).getLink("self").get().getHref())))
                        .andExpect(jsonPath("eventsCaused[1].id", is(groupModel.getEventsCaused().get(1).getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].summary", is(groupModel.getEventsCaused().get(1).getSummary())))
                        .andExpect(jsonPath("eventsCaused[1].motive", is(groupModel.getEventsCaused().get(1).getMotive())))
                        .andExpect(jsonPath("eventsCaused[1].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(groupModel.getEventsCaused().get(1).getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[1].isSuicide", is(groupModel.getEventsCaused().get(1).getIsSuicide())))
                        .andExpect(jsonPath("eventsCaused[1].isSuccessful", is(groupModel.getEventsCaused().get(1).getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[1].isPartOfMultipleIncidents",
                                is(groupModel.getEventsCaused().get(1).getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("eventsCaused[1].links[0].href", is(groupModel.getEventsCaused().get(1).getLink("self").get().getHref()))),
                () -> verify(groupService, times(1)).findById(groupModel.getId()), () -> verifyNoMoreInteractions(groupService),
                () -> verify(groupModelAssembler, times(1)).toModel(groupNode),
                () -> verifyNoMoreInteractions(groupModelAssembler),
                () -> verifyNoInteractions(pagedResourcesAssembler), () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    void when_find_group_but_group_not_exists_should_return_error_response() {

        Long groupId = 1L;

        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";

        when(groupService.findById(groupId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, groupId)).andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty()).andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find GroupModel with id: " + groupId))),
                () -> verify(groupService, times(1)).findById(groupId), () -> verifyNoMoreInteractions(groupService),
                () -> verifyNoInteractions(groupModelAssembler), () -> verifyNoInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchHelper), () -> verifyNoInteractions(violationHelper));
    }

    private Group createGroupWithEvents(ObjectType type) {

        String group = "group";

        String summary = "summary";
        String motive = "motive";
        boolean isPartOfMultipleIncidents = true;
        boolean isSuccessful = true;
        boolean isSuicide = true;

        switch (type) {

            case NODE:

                EventNode eventNode = (EventNode) eventBuilder.withId((long) counterForUtilMethods).withSummary(summary + counterForUtilMethods).withMotive(motive + counterForUtilMethods).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                        .withIsSuccessful(isSuccessful).withIsSuicide(isSuicide)
                        .build(ObjectType.NODE);

                EventNode eventNode2 = (EventNode) eventBuilder.withId((long) counterForUtilMethods).withSummary(summary + counterForUtilMethods).withMotive(motive + counterForUtilMethods).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                        .withIsSuccessful(isSuccessful).withIsSuicide(isSuicide)
                        .build(ObjectType.NODE);

                GroupNode groupNode = (GroupNode) groupBuilder.withId((long) counterForUtilMethods).withName(group + counterForUtilMethods).withEventsCaused(List.of(eventNode, eventNode2)).build(ObjectType.NODE);

                return groupNode;

            case MODEL:

                EventModel eventModel = (EventModel) eventBuilder.withId((long) counterForUtilMethods).withSummary(summary + counterForUtilMethods).withMotive(motive + counterForUtilMethods).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                        .withIsSuccessful(isSuccessful).withIsSuicide(isSuicide)
                        .build(ObjectType.MODEL);
                String pathToEventLink = EVENT_BASE_PATH + "/" + counterForUtilMethods;
                eventModel.add(new Link(pathToEventLink));

                EventModel eventModel2 = (EventModel) eventBuilder.withId((long) counterForUtilMethods).withSummary(summary + counterForUtilMethods).withMotive(motive + counterForUtilMethods).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                        .withIsSuccessful(isSuccessful).withIsSuicide(isSuicide)
                        .build(ObjectType.MODEL);
                String pathToEventLink2 = EVENT_BASE_PATH + "/" + counterForUtilMethods;
                eventModel2.add(new Link(pathToEventLink2));

                GroupModel groupModel = (GroupModel) groupBuilder.withId((long) counterForUtilMethods).withName(group + counterForUtilMethods).withEventsCaused(List.of(eventModel, eventModel2)).build(ObjectType.MODEL);
                String pathToGroupLink = GROUP_BASE_PATH + "/" + counterForUtilMethods;
                groupModel.add(new Link(pathToGroupLink));

                return groupModel;

            default:
                throw new RuntimeException("Invalid type");
        }
    }
}