package com.NowakArtur97.GlobalTerrorismAPI.controller.groupEvents;

import com.NowakArtur97.GlobalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GroupEventsController;
import com.NowakArtur97.GlobalTerrorismAPI.feature.event.Event;
import com.NowakArtur97.GlobalTerrorismAPI.feature.event.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.event.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.GroupModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GroupService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.GroupBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.page.PageHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("GroupEventsController_Tests")
class GroupEventsControllerGetMethodTest {

    private static int counterForUtilMethodsModel = 0;
    private static int counterForUtilMethodsNode = 0;

    private final String GROUP_BASE_PATH = "http://localhost:8080/api/v1/groups";
    private final String EVENT_BASE_PATH = "http://localhost:8080/api/v1/events";

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

    private static EventBuilder eventBuilder;
    private static GroupBuilder groupBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        eventBuilder = new EventBuilder();
        groupBuilder = new GroupBuilder();
    }

    @BeforeEach
    private void setUp() {

        groupEventsController = new GroupEventsController(groupService, groupModelAssembler, eventModelAssembler, eventsPagedResourcesAssembler, pageHelper);

        mockMvc = MockMvcBuilders.standaloneSetup(groupEventsController).setControllerAdvice(new GenericRestControllerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
    }

    @Test
    void when_find_all_group_events_with_default_parameters_in_link_and_events_exist_should_return_group_events() {

        Long groupId = 1L;

        EventNode eventNode1 = (EventNode) createEvent(ObjectType.NODE);
        EventNode eventNode2 = (EventNode) createEvent(ObjectType.NODE);
        EventNode eventNode3 = (EventNode) createEvent(ObjectType.NODE);
        EventNode eventNode4 = (EventNode) createEvent(ObjectType.NODE);

        EventModel eventModel1 = (EventModel) createEvent(ObjectType.MODEL);
        EventModel eventModel2 = (EventModel) createEvent(ObjectType.MODEL);
        EventModel eventModel3 = (EventModel) createEvent(ObjectType.MODEL);
        EventModel eventModel4 = (EventModel) createEvent(ObjectType.MODEL);

        List<EventNode> groupEventNodesListExpected = List.of(eventNode1, eventNode2, eventNode3, eventNode4);
        List<EventModel> groupEventModelsListExpected = List.of(eventModel1, eventModel2, eventModel3, eventModel4);
        List<EventNode> subListOfEvents = List.of(eventNode1, eventNode2, eventNode3, eventNode4);

        GroupNode groupNodeExpected = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode1, eventNode2, eventNode3, eventNode4)).build(ObjectType.NODE);

        int sizeExpected = 20;
        int totalElementsExpected = 4;
        int totalPagesExpected = 1;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);
        PageImpl<EventNode> pageImpl = new PageImpl<>(subListOfEvents, pageable, groupEventNodesListExpected.size());

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = GROUP_BASE_PATH + "/{id}/events/" + urlParameters1;
        String lastPageLink = GROUP_BASE_PATH + "/{id}/events/" + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<EventModel> resources = new PagedModel<>(groupEventModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(groupService.findById(groupId)).thenReturn(Optional.of(groupNodeExpected));
        when(pageHelper.convertListToPage(pageable, groupEventNodesListExpected)).thenReturn(pageImpl);
        when(eventsPagedResourcesAssembler.toModel(pageImpl, eventModelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink, groupId))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content[0].id", is(eventModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].summary", is(eventModel1.getSummary())))
                        .andExpect(jsonPath("content[0].motive", is(eventModel1.getMotive())))
                        .andExpect(jsonPath("content[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel1.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[0].isSuicidal", is(eventModel1.getIsSuicidal())))
                        .andExpect(jsonPath("content[0].isSuccessful", is(eventModel1.getIsSuccessful())))
                        .andExpect(jsonPath("content[0].isPartOfMultipleIncidents",
                                is(eventModel1.getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("content[0].links[0].href", is(eventModel1.getLink("self").get().getHref())))
                        .andExpect(
                                jsonPath("content[0].links[1].href", is(eventModel1.getLink("target").get().getHref())))
                        .andExpect(jsonPath("content[1].id", is(eventModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].summary", is(eventModel2.getSummary())))
                        .andExpect(jsonPath("content[1].motive", is(eventModel2.getMotive())))
                        .andExpect(jsonPath("content[1].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[1].isSuicidal", is(eventModel2.getIsSuicidal())))
                        .andExpect(jsonPath("content[1].isSuccessful", is(eventModel2.getIsSuccessful())))
                        .andExpect(jsonPath("content[1].isPartOfMultipleIncidents",
                                is(eventModel2.getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("content[1].links[0].href", is(eventModel2.getLink("self").get().getHref())))
                        .andExpect(
                                jsonPath("content[1].links[1].href", is(eventModel2.getLink("target").get().getHref())))
                        .andExpect(jsonPath("content[2].id", is(eventModel3.getId().intValue())))
                        .andExpect(jsonPath("content[2].summary", is(eventModel3.getSummary())))
                        .andExpect(jsonPath("content[2].motive", is(eventModel3.getMotive())))
                        .andExpect(jsonPath("content[2].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[2].isSuicidal", is(eventModel3.getIsSuicidal())))
                        .andExpect(jsonPath("content[2].isSuccessful", is(eventModel3.getIsSuccessful())))
                        .andExpect(jsonPath("content[2].isPartOfMultipleIncidents",
                                is(eventModel3.getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("content[2].links[0].href", is(eventModel3.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[3].id", is(eventModel4.getId().intValue())))
                        .andExpect(jsonPath("content[3].summary", is(eventModel4.getSummary())))
                        .andExpect(jsonPath("content[3].motive", is(eventModel4.getMotive())))
                        .andExpect(jsonPath("content[3].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel4.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[3].isSuicidal", is(eventModel4.getIsSuicidal())))
                        .andExpect(jsonPath("content[3].isSuccessful", is(eventModel4.getIsSuccessful())))
                        .andExpect(jsonPath("content[3].isPartOfMultipleIncidents",
                                is(eventModel4.getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("content[3].links[0].href", is(eventModel4.getLink("self").get().getHref())))
                        .andExpect(
                                jsonPath("content[3].links[1].href", is(eventModel4.getLink("target").get().getHref())))
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(groupService, times(1)).findById(groupId),
                () -> verifyNoMoreInteractions(groupService),
                () -> verify(pageHelper, times(1)).convertListToPage(pageable, groupEventNodesListExpected),
                () -> verifyNoMoreInteractions(pageHelper),
                () -> verify(eventsPagedResourcesAssembler, times(1)).toModel(pageImpl, eventModelAssembler),
                () -> verifyNoMoreInteractions(eventsPagedResourcesAssembler),
                () -> verifyNoInteractions(eventModelAssembler),
                () -> verifyNoInteractions(groupModelAssembler));
    }

    @Test
    void when_find_all_group_events_with_changed_parameters_in_link_and_events_exist_should_return_group_events() {

        Long groupId = 1L;

        EventNode eventNode1 = (EventNode) createEvent(ObjectType.NODE);
        EventNode eventNode2 = (EventNode) createEvent(ObjectType.NODE);
        EventNode eventNode3 = (EventNode) createEvent(ObjectType.NODE);
        EventNode eventNode4 = (EventNode) createEvent(ObjectType.NODE);

        EventModel eventModel1 = (EventModel) createEvent(ObjectType.MODEL);
        EventModel eventModel2 = (EventModel) createEvent(ObjectType.MODEL);
        EventModel eventModel3 = (EventModel) createEvent(ObjectType.MODEL);

        List<EventNode> groupEventNodesListExpected = List.of(eventNode1, eventNode2, eventNode3, eventNode4);
        List<EventModel> groupEventModelsListExpected = List.of(eventModel1, eventModel2, eventModel3);
        List<EventNode> subListOfEvents = List.of(eventNode1, eventNode2, eventNode3);

        GroupNode groupNodeExpected = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode1, eventNode2, eventNode3, eventNode4)).build(ObjectType.NODE);

        int sizeExpected = 3;
        int totalElementsExpected = 4;
        int totalPagesExpected = 2;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 1;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);
        PageImpl<EventNode> pageImpl = new PageImpl<>(subListOfEvents, pageable, groupEventNodesListExpected.size());

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = GROUP_BASE_PATH + "/{id}/events/" + urlParameters1;
        String lastPageLink = GROUP_BASE_PATH + "/{id}/events/" + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<EventModel> resources = new PagedModel<>(groupEventModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(groupService.findById(groupId)).thenReturn(Optional.of(groupNodeExpected));
        when(pageHelper.convertListToPage(pageable, groupEventNodesListExpected)).thenReturn(pageImpl);
        when(eventsPagedResourcesAssembler.toModel(pageImpl, eventModelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink, groupId))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content[0].id", is(eventModel1.getId().intValue())))
                        .andExpect(jsonPath("content[0].summary", is(eventModel1.getSummary())))
                        .andExpect(jsonPath("content[0].motive", is(eventModel1.getMotive())))
                        .andExpect(jsonPath("content[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel1.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[0].isSuicidal", is(eventModel1.getIsSuicidal())))
                        .andExpect(jsonPath("content[0].isSuccessful", is(eventModel1.getIsSuccessful())))
                        .andExpect(jsonPath("content[0].isPartOfMultipleIncidents",
                                is(eventModel1.getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("content[0].links[0].href", is(eventModel1.getLink("self").get().getHref())))
                        .andExpect(
                                jsonPath("content[0].links[1].href", is(eventModel1.getLink("target").get().getHref())))
                        .andExpect(jsonPath("content[1].id", is(eventModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].summary", is(eventModel2.getSummary())))
                        .andExpect(jsonPath("content[1].motive", is(eventModel2.getMotive())))
                        .andExpect(jsonPath("content[1].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[1].isSuicidal", is(eventModel2.getIsSuicidal())))
                        .andExpect(jsonPath("content[1].isSuccessful", is(eventModel2.getIsSuccessful())))
                        .andExpect(jsonPath("content[1].isPartOfMultipleIncidents",
                                is(eventModel2.getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("content[1].links[0].href", is(eventModel2.getLink("self").get().getHref())))
                        .andExpect(
                                jsonPath("content[1].links[1].href", is(eventModel2.getLink("target").get().getHref())))
                        .andExpect(jsonPath("content[2].id", is(eventModel3.getId().intValue())))
                        .andExpect(jsonPath("content[2].summary", is(eventModel3.getSummary())))
                        .andExpect(jsonPath("content[2].motive", is(eventModel3.getMotive())))
                        .andExpect(jsonPath("content[2].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[2].isSuicidal", is(eventModel3.getIsSuicidal())))
                        .andExpect(jsonPath("content[2].isSuccessful", is(eventModel3.getIsSuccessful())))
                        .andExpect(jsonPath("content[2].isPartOfMultipleIncidents",
                                is(eventModel3.getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("content[2].links[0].href", is(eventModel3.getLink("self").get().getHref())))
                        .andExpect(
                                jsonPath("content[2].links[1].href", is(eventModel3.getLink("target").get().getHref())))
                        .andExpect(jsonPath("content[3]").doesNotExist())
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(groupService, times(1)).findById(groupId),
                () -> verifyNoMoreInteractions(groupService),
                () -> verify(pageHelper, times(1)).convertListToPage(pageable, groupEventNodesListExpected),
                () -> verifyNoMoreInteractions(pageHelper),
                () -> verify(eventsPagedResourcesAssembler, times(1)).toModel(pageImpl, eventModelAssembler),
                () -> verifyNoMoreInteractions(eventsPagedResourcesAssembler),
                () -> verifyNoInteractions(eventModelAssembler),
                () -> verifyNoInteractions(groupModelAssembler));
    }

    @Test
    void when_find_all_group_events_but_group_does_not_have_events_should_return_empty_list() {

        Long groupId = 1L;

        List<EventNode> groupEventNodesListExpected = new ArrayList<>();
        List<EventModel> groupEventModelsListExpected = new ArrayList<>();
        List<EventNode> subListOfEvents = new ArrayList<>();

        GroupNode groupNodeExpected = (GroupNode) groupBuilder.build(ObjectType.NODE);

        int sizeExpected = 20;
        int totalElementsExpected = 0;
        int totalPagesExpected = 0;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);
        PageImpl<EventNode> pageImpl = new PageImpl<>(subListOfEvents, pageable, groupEventNodesListExpected.size());

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = GROUP_BASE_PATH + "/{id}/events/" + urlParameters1;
        String lastPageLink = GROUP_BASE_PATH + "/{id}/events/" + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<EventModel> resources = new PagedModel<>(groupEventModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(groupService.findById(groupId)).thenReturn(Optional.of(groupNodeExpected));
        when(pageHelper.convertListToPage(pageable, groupEventNodesListExpected)).thenReturn(pageImpl);
        when(eventsPagedResourcesAssembler.toModel(pageImpl, eventModelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink, groupId))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink))).andExpect(jsonPath("content").isEmpty())
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(groupService, times(1)).findById(groupId),
                () -> verifyNoMoreInteractions(groupService),
                () -> verify(pageHelper, times(1)).convertListToPage(pageable, groupEventNodesListExpected),
                () -> verifyNoMoreInteractions(pageHelper),
                () -> verify(eventsPagedResourcesAssembler, times(1)).toModel(pageImpl, eventModelAssembler),
                () -> verifyNoMoreInteractions(eventsPagedResourcesAssembler),
                () -> verifyNoInteractions(eventModelAssembler),
                () -> verifyNoInteractions(groupModelAssembler));
    }

    @Test
    void when_find_all_group_events_but_group_does_not_exist_should_return_error_response() {

        Long groupId = 1L;
        String linkWithParameter = GROUP_BASE_PATH + "/{id}/events";

        when(groupService.findById(groupId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, groupId))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find GroupModel with id: " + groupId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(groupService, times(1)).findById(groupId),
                () -> verifyNoMoreInteractions(groupService),
                () -> verifyNoInteractions(groupModelAssembler),
                () -> verifyNoInteractions(pageHelper),
                () -> verifyNoInteractions(eventModelAssembler),
                () -> verifyNoInteractions(eventsPagedResourcesAssembler));
    }

    private Event createEvent(ObjectType type) {

        String summary = "summary";
        String motive = "motive";
        boolean isPartOfMultipleIncidents = true;
        boolean isSuccessful = true;
        boolean isSuicidal = true;

        switch (type) {

            case NODE:

                counterForUtilMethodsNode++;

                return eventBuilder.withId((long) counterForUtilMethodsNode).withSummary(summary + counterForUtilMethodsNode)
                        .withMotive(motive + counterForUtilMethodsNode).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                        .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal)
                        .build(ObjectType.NODE);

            case MODEL:

                counterForUtilMethodsModel++;

                EventModel eventModel = (EventModel) eventBuilder.withId((long) counterForUtilMethodsModel).withSummary(summary + counterForUtilMethodsModel)
                        .withMotive(motive + counterForUtilMethodsModel).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                        .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal)
                        .build(ObjectType.MODEL);

                String pathToEventLink = EVENT_BASE_PATH + "/" + counterForUtilMethodsModel;
                eventModel.add(new Link(pathToEventLink));
                String pathToTargetEventLink = EVENT_BASE_PATH + "/" + counterForUtilMethodsModel + "/targets";
                eventModel.add(new Link(pathToTargetEventLink, "target"));

                return eventModel;

            default:
                throw new RuntimeException("Invalid type");
        }
    }
}
