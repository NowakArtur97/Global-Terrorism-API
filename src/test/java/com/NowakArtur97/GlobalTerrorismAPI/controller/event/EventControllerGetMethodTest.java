package com.NowakArtur97.GlobalTerrorismAPI.controller.event;

import com.NowakArtur97.GlobalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.EventModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Event;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestController;
import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.ViolationHelper;
import org.hamcrest.core.IsNull;
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
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventController_Tests")
class EventControllerGetMethodTest {

    private static int counterForUtilMethods = 0;

    private final String EVENT_BASE_PATH = "http://localhost:8080/api/events";
    private final String TARGET_BASE_PATH = "http://localhost:8080/api/targets";

    private MockMvc mockMvc;

    private GenericRestController<EventModel, EventDTO> eventController;

    @Mock
    private GenericService<EventNode, EventDTO> eventService;

    @Mock
    private EventModelAssembler modelAssembler;

    @Mock
    private PagedResourcesAssembler<EventNode> pagedResourcesAssembler;

    @Mock
    private PatchHelper patchHelper;

    @Mock
    private ViolationHelper<EventNode, EventDTO> violationHelper;

    private static TargetBuilder targetBuilder;
    private static EventBuilder eventBuilder;

    @BeforeEach
    private void setUp() {

        eventController = new EventController(eventService, modelAssembler, pagedResourcesAssembler, patchHelper,
                violationHelper);

        mockMvc = MockMvcBuilders.standaloneSetup(eventController).setControllerAdvice(new GenericRestControllerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();

        targetBuilder = new TargetBuilder();
        eventBuilder = new EventBuilder();
    }

    @Test
    void when_find_all_events_with_default_parameters_in_link_and_events_exist_should_return_all_events() {

        EventNode eventNode1 = (EventNode) createEvent(ObjectType.NODE);
        EventNode eventNode2 = (EventNode) createEvent(ObjectType.NODE);
        EventNode eventNode3 = (EventNode) createEvent(ObjectType.NODE);
        EventNode eventNode4 = (EventNode) createEvent(ObjectType.NODE);

        EventModel eventModel1 = (EventModel) createEvent(ObjectType.MODEL);
        EventModel eventModel2 = (EventModel) createEvent(ObjectType.MODEL);
        EventModel eventModel3 = (EventModel) createEvent(ObjectType.MODEL);
        EventModel eventModel4 = (EventModel) createEvent(ObjectType.MODEL);

        List<EventNode> eventNodesListExpected = List.of(eventNode1, eventNode2, eventNode3, eventNode4);
        List<EventModel> eventModelsListExpected = List.of(eventModel1, eventModel2, eventModel3, eventModel4);
        Page<EventNode> eventsExpected = new PageImpl<>(eventNodesListExpected);

        int sizeExpected = 3;
        int totalElementsExpected = 4;
        int totalPagesExpected = 2;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 1;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = EVENT_BASE_PATH + urlParameters1;
        String lastPageLink = EVENT_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PagedModel.PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<EventModel> resources = new PagedModel<>(eventModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(eventService.findAll(pageable)).thenReturn(eventsExpected);
        when(pagedResourcesAssembler.toModel(eventsExpected, modelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink)).andExpect(status().isOk())
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
                        .andExpect(jsonPath("content[0].isSuicide", is(eventModel1.getIsSuicide())))
                        .andExpect(jsonPath("content[0].isSuccessful", is(eventModel1.getIsSuccessful())))
                        .andExpect(jsonPath("content[0].isPartOfMultipleIncidents",
                                is(eventModel1.getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("content[0].links[0].href", is(eventModel1.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[0].target.links[0].href",
                                is(eventModel1.getTarget().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[0].target.id", is(eventModel1.getTarget().getId().intValue())))
                        .andExpect(jsonPath("content[0].target.target", is(eventModel1.getTarget().getTarget())))
                        .andExpect(jsonPath("content[1].id", is(eventModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].summary", is(eventModel2.getSummary())))
                        .andExpect(jsonPath("content[1].motive", is(eventModel2.getMotive())))
                        .andExpect(jsonPath("content[1].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[1].isSuicide", is(eventModel2.getIsSuicide())))
                        .andExpect(jsonPath("content[1].isSuccessful", is(eventModel2.getIsSuccessful())))
                        .andExpect(jsonPath("content[1].isPartOfMultipleIncidents",
                                is(eventModel2.getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("content[1].links[0].href", is(eventModel2.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].target.links[0].href",
                                is(eventModel2.getTarget().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].target.id", is(eventModel2.getTarget().getId().intValue())))
                        .andExpect(jsonPath("content[1].target.target", is(eventModel2.getTarget().getTarget())))
                        .andExpect(jsonPath("content[2].id", is(eventModel3.getId().intValue())))
                        .andExpect(jsonPath("content[2].summary", is(eventModel3.getSummary())))
                        .andExpect(jsonPath("content[2].motive", is(eventModel3.getMotive())))
                        .andExpect(jsonPath("content[2].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[2].isSuicide", is(eventModel3.getIsSuicide())))
                        .andExpect(jsonPath("content[2].isSuccessful", is(eventModel3.getIsSuccessful())))
                        .andExpect(jsonPath("content[2].isPartOfMultipleIncidents",
                                is(eventModel3.getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("content[2].links[0].href", is(eventModel3.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].target.links[0].href",
                                is(eventModel3.getTarget().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].target.id", is(eventModel3.getTarget().getId().intValue())))
                        .andExpect(jsonPath("content[2].target.target", is(eventModel3.getTarget().getTarget())))
                        .andExpect(jsonPath("content[3].id", is(eventModel4.getId().intValue())))
                        .andExpect(jsonPath("content[3].summary", is(eventModel4.getSummary())))
                        .andExpect(jsonPath("content[3].motive", is(eventModel4.getMotive())))
                        .andExpect(jsonPath("content[3].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel4.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[3].isSuicide", is(eventModel4.getIsSuicide())))
                        .andExpect(jsonPath("content[3].isSuccessful", is(eventModel4.getIsSuccessful())))
                        .andExpect(jsonPath("content[3].isPartOfMultipleIncidents",
                                is(eventModel4.getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("content[3].links[0].href", is(eventModel4.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[3].target.links[0].href",
                                is(eventModel4.getTarget().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[3].target.id", is(eventModel4.getTarget().getId().intValue())))
                        .andExpect(jsonPath("content[3].target.target", is(eventModel4.getTarget().getTarget())))
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(eventService, times(1)).findAll(pageable), () -> verifyNoMoreInteractions(eventService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(eventsExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler), () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    void when_find_all_events_with_changed_parameters_in_link_and_events_exist_should_return_all_events() {

        EventNode eventNode1 = (EventNode) createEvent(ObjectType.NODE);
        EventNode eventNode2 = (EventNode) createEvent(ObjectType.NODE);
        EventNode eventNode3 = (EventNode) createEvent(ObjectType.NODE);
        EventNode eventNode4 = (EventNode) createEvent(ObjectType.NODE);

        EventModel eventModel1 = (EventModel) createEvent(ObjectType.MODEL);
        EventModel eventModel2 = (EventModel) createEvent(ObjectType.MODEL);
        EventModel eventModel3 = (EventModel) createEvent(ObjectType.MODEL);
        EventModel eventModel4 = (EventModel) createEvent(ObjectType.MODEL);

        List<EventNode> eventNodesListExpected = List.of(eventNode1, eventNode2, eventNode3, eventNode4);
        List<EventModel> eventModelsListExpected = List.of(eventModel1, eventModel2, eventModel3, eventModel4);
        Page<EventNode> eventsExpected = new PageImpl<>(eventNodesListExpected);

        int sizeExpected = 100;
        int totalElementsExpected = 4;
        int totalPagesExpected = 1;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = EVENT_BASE_PATH + urlParameters1;
        String lastPageLink = EVENT_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PagedModel.PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<EventModel> resources = new PagedModel<>(eventModelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(eventService.findAll(pageable)).thenReturn(eventsExpected);
        when(pagedResourcesAssembler.toModel(eventsExpected, modelAssembler)).thenReturn(resources);

        assertAll(
                () -> mockMvc.perform(get(firstPageLink)).andExpect(status().isOk())
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
                        .andExpect(jsonPath("content[0].isSuicide", is(eventModel1.getIsSuicide())))
                        .andExpect(jsonPath("content[0].isSuccessful", is(eventModel1.getIsSuccessful())))
                        .andExpect(jsonPath("content[0].isPartOfMultipleIncidents",
                                is(eventModel1.getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("content[0].links[0].href", is(eventModel1.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[0].target.links[0].href",
                                is(eventModel1.getTarget().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[0].target.id", is(eventModel1.getTarget().getId().intValue())))
                        .andExpect(jsonPath("content[0].target.target", is(eventModel1.getTarget().getTarget())))
                        .andExpect(jsonPath("content[1].id", is(eventModel2.getId().intValue())))
                        .andExpect(jsonPath("content[1].summary", is(eventModel2.getSummary())))
                        .andExpect(jsonPath("content[1].motive", is(eventModel2.getMotive())))
                        .andExpect(jsonPath("content[1].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[1].isSuicide", is(eventModel2.getIsSuicide())))
                        .andExpect(jsonPath("content[1].isSuccessful", is(eventModel2.getIsSuccessful())))
                        .andExpect(jsonPath("content[1].isPartOfMultipleIncidents",
                                is(eventModel2.getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("content[1].links[0].href", is(eventModel2.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].target.links[0].href",
                                is(eventModel2.getTarget().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].target.id", is(eventModel2.getTarget().getId().intValue())))
                        .andExpect(jsonPath("content[1].target.target", is(eventModel2.getTarget().getTarget())))
                        .andExpect(jsonPath("content[2].id", is(eventModel3.getId().intValue())))
                        .andExpect(jsonPath("content[2].summary", is(eventModel3.getSummary())))
                        .andExpect(jsonPath("content[2].motive", is(eventModel3.getMotive())))
                        .andExpect(jsonPath("content[2].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[2].isSuicide", is(eventModel3.getIsSuicide())))
                        .andExpect(jsonPath("content[2].isSuccessful", is(eventModel3.getIsSuccessful())))
                        .andExpect(jsonPath("content[2].isPartOfMultipleIncidents",
                                is(eventModel3.getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("content[2].links[0].href", is(eventModel3.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].target.links[0].href",
                                is(eventModel3.getTarget().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].target.id", is(eventModel3.getTarget().getId().intValue())))
                        .andExpect(jsonPath("content[2].target.target", is(eventModel3.getTarget().getTarget())))
                        .andExpect(jsonPath("content[3].id", is(eventModel4.getId().intValue())))
                        .andExpect(jsonPath("content[3].summary", is(eventModel4.getSummary())))
                        .andExpect(jsonPath("content[3].motive", is(eventModel4.getMotive())))
                        .andExpect(jsonPath("content[3].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel4.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("content[3].isSuicide", is(eventModel4.getIsSuicide())))
                        .andExpect(jsonPath("content[3].isSuccessful", is(eventModel4.getIsSuccessful())))
                        .andExpect(jsonPath("content[3].isPartOfMultipleIncidents",
                                is(eventModel4.getIsPartOfMultipleIncidents())))
                        .andExpect(
                                jsonPath("content[3].links[0].href", is(eventModel4.getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[3].target.links[0].href",
                                is(eventModel4.getTarget().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[3].target.id", is(eventModel4.getTarget().getId().intValue())))
                        .andExpect(jsonPath("content[3].target.target", is(eventModel4.getTarget().getTarget())))
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(eventService, times(1)).findAll(pageable), () -> verifyNoMoreInteractions(eventService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(eventsExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler), () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    void when_find_all_events_but_events_not_exist_should_return_empty_list() {

        List<EventNode> eventsListExpected = new ArrayList<>();

        List<EventModel> modelsListExpected = new ArrayList<>();

        Page<EventNode> eventsExpected = new PageImpl<>(eventsListExpected);

        int sizeExpected = 100;
        int totalElementsExpected = 0;
        int totalPagesExpected = 0;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 0;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        String urlParameters1 = "?page=" + pageExpected + "&size=" + sizeExpected;
        String urlParameters2 = "?page=" + lastPageExpected + "&size=" + sizeExpected;
        String firstPageLink = EVENT_BASE_PATH + urlParameters1;
        String lastPageLink = EVENT_BASE_PATH + urlParameters2;

        Link pageLink1 = new Link(firstPageLink, "first");
        Link pageLink2 = new Link(firstPageLink, "self");
        Link pageLink3 = new Link(lastPageLink, "next");
        Link pageLink4 = new Link(lastPageLink, "last");

        PageMetadata metadata = new PagedModel.PageMetadata(sizeExpected, numberExpected, totalElementsExpected);
        PagedModel<EventModel> resources = new PagedModel<>(modelsListExpected, metadata, pageLink1, pageLink2,
                pageLink3, pageLink4);

        when(eventService.findAll(pageable)).thenReturn(eventsExpected);
        when(pagedResourcesAssembler.toModel(eventsExpected, modelAssembler)).thenReturn(resources);

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
                () -> verify(eventService, times(1)).findAll(pageable), () -> verifyNoMoreInteractions(eventService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(eventsExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler), () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    void when_find_existing_event_should_return_event() {

        Long eventId = 1L;

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
        TargetModel targetModel = (TargetModel) targetBuilder.build(ObjectType.MODEL);
        EventModel eventModel = (EventModel) eventBuilder.withTarget(targetModel).build(ObjectType.MODEL);
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
        eventModel.add(new Link(pathToEventLink));

        String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

        when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
        when(modelAssembler.toModel(eventNode)).thenReturn(eventModel);

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, eventId)).andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToEventLink)))
                        .andExpect(jsonPath("id", is(eventId.intValue())))
                        .andExpect(jsonPath("summary", is(eventModel.getSummary())))
                        .andExpect(jsonPath("motive", is(eventModel.getMotive())))
                        .andExpect(jsonPath("date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("isSuicide", is(eventModel.getIsSuicide())))
                        .andExpect(jsonPath("isSuccessful", is(eventModel.getIsSuccessful())))
                        .andExpect(jsonPath("isPartOfMultipleIncidents", is(eventModel.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("target.id", is(targetModel.getId().intValue())))
                        .andExpect(jsonPath("target.target", is(targetModel.getTarget()))),
                () -> verify(eventService, times(1)).findById(eventId), () -> verifyNoMoreInteractions(eventService),
                () -> verify(modelAssembler, times(1)).toModel(eventNode),
                () -> verifyNoMoreInteractions(modelAssembler), () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    void when_find_existing_event_without_target_should_return_event_without_target() {

        Long eventId = 1L;

        EventNode eventNode = (EventNode) eventBuilder.build(ObjectType.NODE);
        EventModel eventModel = (EventModel) eventBuilder.build(ObjectType.MODEL);

        String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
        Link eventLink = new Link(pathToEventLink);
        eventModel.add(eventLink);

        String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

        when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
        when(modelAssembler.toModel(eventNode)).thenReturn(eventModel);

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, eventId)).andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToEventLink)))
                        .andExpect(jsonPath("id", is(eventId.intValue())))
                        .andExpect(jsonPath("summary", is(eventModel.getSummary())))
                        .andExpect(jsonPath("motive", is(eventModel.getMotive())))
                        .andExpect(jsonPath("date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("isSuicide", is(eventModel.getIsSuicide())))
                        .andExpect(jsonPath("isSuccessful", is(eventModel.getIsSuccessful())))
                        .andExpect(jsonPath("isPartOfMultipleIncidents", is(eventModel.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("target").value(IsNull.nullValue())),
                () -> verify(eventService, times(1)).findById(eventId), () -> verifyNoMoreInteractions(eventService),
                () -> verify(modelAssembler, times(1)).toModel(eventNode),
                () -> verifyNoMoreInteractions(modelAssembler), () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    void when_find_event_but_event_not_exists_should_return_error_response() {

        Long eventId = 1L;

        String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

        when(eventService.findById(eventId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, eventId)).andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty()).andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find EventModel with id: " + eventId))),
                () -> verify(eventService, times(1)).findById(eventId), () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(modelAssembler), () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    private Event createEvent(ObjectType type) {

        String summary = "summary";
        String motive = "motive";
        boolean isPartOfMultipleIncidents = true;
        boolean isSuccessful = true;
        boolean isSuicide = true;

        switch (type) {

            case NODE:

                TargetNode targetNode = new TargetNode((long) counterForUtilMethods, "target" + counterForUtilMethods);
                EventNode eventNode = (EventNode) eventBuilder.withId((long) counterForUtilMethods).withSummary(summary + counterForUtilMethods)
                        .withMotive(motive + counterForUtilMethods).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                        .withIsSuccessful(isSuccessful).withIsSuicide(isSuicide).withTarget(targetNode)
                        .build(ObjectType.NODE);

                return eventNode;

            case MODEL:

                TargetModel targetModel = new TargetModel((long) counterForUtilMethods, "target" + counterForUtilMethods);
                String pathToTargetLink = TARGET_BASE_PATH + "/" + counterForUtilMethods;
                targetModel.add(new Link(pathToTargetLink));

                EventModel eventModel = (EventModel) eventBuilder.withId((long) counterForUtilMethods).withSummary(summary + counterForUtilMethods)
                        .withMotive(motive + counterForUtilMethods).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                        .withIsSuccessful(isSuccessful).withIsSuicide(isSuicide).withTarget(targetModel)
                        .build(ObjectType.MODEL);
                String pathToEventLink = EVENT_BASE_PATH + "/" + counterForUtilMethods;
                eventModel.add(new Link(pathToEventLink));

                return eventModel;

            default:
                throw new RuntimeException("Invalid type");
        }
    }
}
