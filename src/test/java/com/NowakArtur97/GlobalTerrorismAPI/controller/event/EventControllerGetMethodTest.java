package com.NowakArtur97.GlobalTerrorismAPI.controller.event;

import com.NowakArtur97.GlobalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.EventModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Event;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestController;
import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.CityModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.CityNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.CityBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.patch.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.violation.ViolationHelper;
import org.junit.jupiter.api.*;
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
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventController_Tests")
class EventControllerGetMethodTest {

    private static int counterForUtilMethodsModel = 0;
    private static int counterForUtilMethodsNode = 0;

    private final String EVENT_BASE_PATH = "http://localhost:8080/api/v1/events";
    private final String CITY_BASE_PATH = "http://localhost:8080/api/v1/citites";
    private final String TARGET_BASE_PATH = "http://localhost:8080/api/v1/targets";

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

    private static CityBuilder cityBuilder;
    private static TargetBuilder targetBuilder;
    private static EventBuilder eventBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        cityBuilder = new CityBuilder();
        targetBuilder = new TargetBuilder();
        eventBuilder = new EventBuilder();
    }

    @BeforeEach
    private void setUp() {

        eventController = new EventController(eventService, modelAssembler, pagedResourcesAssembler, patchHelper,
                violationHelper);

        mockMvc = MockMvcBuilders.standaloneSetup(eventController)
                .setControllerAdvice(new GenericRestControllerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
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

        int sizeExpected = 20;
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
                () -> mockMvc.perform(get(firstPageLink).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
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
                        .andExpect(jsonPath("content[0].links[1].href",
                                is(eventModel1.getLink("target").get().getHref())))
                        .andExpect(jsonPath("content[0].target.links[0].href",
                                is(eventModel1.getTarget().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[0].city.links[0].href",
                                is(eventModel1.getCity().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[0].target.id", is(eventModel1.getTarget().getId().intValue())))
                        .andExpect(jsonPath("content[0].target.target", is(eventModel1.getTarget().getTarget())))
                        .andExpect(jsonPath("content[0].city.id", is(eventModel1.getCity().getId().intValue())))
                        .andExpect(jsonPath("content[0].city.name", is(eventModel1.getCity().getName())))
                        .andExpect(jsonPath("content[0].city.latitude", is(eventModel1.getCity().getLatitude())))
                        .andExpect(jsonPath("content[0].city.longitude", is(eventModel1.getCity().getLongitude())))

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
                        .andExpect(jsonPath("content[1].links[1].href",
                                is(eventModel2.getLink("target").get().getHref())))
                        .andExpect(jsonPath("content[1].target.links[0].href",
                                is(eventModel2.getTarget().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].city.links[0].href",
                                is(eventModel2.getCity().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].target.id", is(eventModel2.getTarget().getId().intValue())))
                        .andExpect(jsonPath("content[1].target.target", is(eventModel2.getTarget().getTarget())))
                        .andExpect(jsonPath("content[1].city.id", is(eventModel2.getCity().getId().intValue())))
                        .andExpect(jsonPath("content[1].city.name", is(eventModel2.getCity().getName())))
                        .andExpect(jsonPath("content[1].city.latitude", is(eventModel2.getCity().getLatitude())))
                        .andExpect(jsonPath("content[1].city.longitude", is(eventModel2.getCity().getLongitude())))

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
                        .andExpect(jsonPath("content[2].links[1].href",
                                is(eventModel3.getLink("target").get().getHref())))
                        .andExpect(jsonPath("content[2].target.links[0].href",
                                is(eventModel3.getTarget().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].city.links[0].href",
                                is(eventModel3.getCity().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].target.id", is(eventModel3.getTarget().getId().intValue())))
                        .andExpect(jsonPath("content[2].target.target", is(eventModel3.getTarget().getTarget())))
                        .andExpect(jsonPath("content[2].city.id", is(eventModel3.getCity().getId().intValue())))
                        .andExpect(jsonPath("content[2].city.name", is(eventModel3.getCity().getName())))
                        .andExpect(jsonPath("content[2].city.latitude", is(eventModel3.getCity().getLatitude())))
                        .andExpect(jsonPath("content[2].city.longitude", is(eventModel3.getCity().getLongitude())))

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
                        .andExpect(jsonPath("content[3].links[1].href",
                                is(eventModel4.getLink("target").get().getHref())))
                        .andExpect(jsonPath("content[3].target.links[0].href",
                                is(eventModel4.getTarget().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[3].city.links[0].href",
                                is(eventModel4.getCity().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[3].target.id", is(eventModel4.getTarget().getId().intValue())))
                        .andExpect(jsonPath("content[3].target.target", is(eventModel4.getTarget().getTarget())))
                        .andExpect(jsonPath("content[3].city.id", is(eventModel4.getCity().getId().intValue())))
                        .andExpect(jsonPath("content[3].city.name", is(eventModel4.getCity().getName())))
                        .andExpect(jsonPath("content[3].city.latitude", is(eventModel4.getCity().getLatitude())))
                        .andExpect(jsonPath("content[3].city.longitude", is(eventModel4.getCity().getLongitude())))

                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(eventService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(eventService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(eventsExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchHelper),
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

        List<EventModel> eventModelsListExpected = List.of(eventModel1, eventModel2, eventModel3);
        List<EventNode> eventNodesListExpected = List.of(eventNode1, eventNode2, eventNode3, eventNode4);

        int sizeExpected = 3;
        int totalElementsExpected = 4;
        int totalPagesExpected = 2;
        int numberExpected = 0;
        int pageExpected = 0;
        int lastPageExpected = 1;

        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);
        PageImpl<EventNode> eventsExpected = new PageImpl<>(eventNodesListExpected, pageable, eventNodesListExpected.size());

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
                () -> mockMvc.perform(get(firstPageLink)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
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
                        .andExpect(jsonPath("content[0].links[1].href", is(eventModel1.getLink("target").get().getHref())))
                        .andExpect(jsonPath("content[0].target.links[0].href",
                                is(eventModel1.getTarget().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[0].city.links[0].href",
                                is(eventModel1.getCity().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[0].target.id", is(eventModel1.getTarget().getId().intValue())))
                        .andExpect(jsonPath("content[0].target.target", is(eventModel1.getTarget().getTarget())))
                        .andExpect(jsonPath("content[0].city.id", is(eventModel1.getCity().getId().intValue())))
                        .andExpect(jsonPath("content[0].city.name", is(eventModel1.getCity().getName())))
                        .andExpect(jsonPath("content[0].city.latitude", is(eventModel1.getCity().getLatitude())))
                        .andExpect(jsonPath("content[0].city.longitude", is(eventModel1.getCity().getLongitude())))

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
                        .andExpect(jsonPath("content[1].links[1].href", is(eventModel2.getLink("target").get().getHref())))
                        .andExpect(jsonPath("content[1].target.links[0].href",
                                is(eventModel2.getTarget().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].city.links[0].href",
                                is(eventModel2.getCity().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[1].target.id", is(eventModel2.getTarget().getId().intValue())))
                        .andExpect(jsonPath("content[1].target.target", is(eventModel2.getTarget().getTarget())))
                        .andExpect(jsonPath("content[1].city.id", is(eventModel2.getCity().getId().intValue())))
                        .andExpect(jsonPath("content[1].city.name", is(eventModel2.getCity().getName())))
                        .andExpect(jsonPath("content[1].city.latitude", is(eventModel2.getCity().getLatitude())))
                        .andExpect(jsonPath("content[1].city.longitude", is(eventModel2.getCity().getLongitude())))

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
                        .andExpect(jsonPath("content[2].links[1].href",
                                is(eventModel3.getLink("target").get().getHref())))
                        .andExpect(jsonPath("content[2].target.links[0].href",
                                is(eventModel3.getTarget().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].city.links[0].href",
                                is(eventModel3.getCity().getLink("self").get().getHref())))
                        .andExpect(jsonPath("content[2].target.id", is(eventModel3.getTarget().getId().intValue())))
                        .andExpect(jsonPath("content[2].target.target", is(eventModel3.getTarget().getTarget())))
                        .andExpect(jsonPath("content[2].city.id", is(eventModel3.getCity().getId().intValue())))
                        .andExpect(jsonPath("content[2].city.name", is(eventModel3.getCity().getName())))
                        .andExpect(jsonPath("content[2].city.latitude", is(eventModel3.getCity().getLatitude())))
                        .andExpect(jsonPath("content[2].city.longitude", is(eventModel3.getCity().getLongitude())))

                        .andExpect(jsonPath("content[3]").doesNotExist())
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(eventService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(eventService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(eventsExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    void when_find_all_events_but_events_not_exist_should_return_empty_list() {

        List<EventNode> eventsListExpected = new ArrayList<>();

        List<EventModel> modelsListExpected = new ArrayList<>();

        Page<EventNode> eventsExpected = new PageImpl<>(eventsListExpected);

        int sizeExpected = 20;
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
                () -> mockMvc.perform(get(firstPageLink)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[1].href", is(firstPageLink)))
                        .andExpect(jsonPath("links[2].href", is(lastPageLink)))
                        .andExpect(jsonPath("links[3].href", is(lastPageLink)))
                        .andExpect(jsonPath("content").isEmpty())
                        .andExpect(jsonPath("page.size", is(sizeExpected)))
                        .andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
                        .andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
                        .andExpect(jsonPath("page.number", is(numberExpected))),
                () -> verify(eventService, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(eventService),
                () -> verify(pagedResourcesAssembler, times(1)).toModel(eventsExpected, modelAssembler),
                () -> verifyNoMoreInteractions(pagedResourcesAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper));
    }

    @Test
    void when_find_existing_event_should_return_event() {

        Long eventId = 1L;

        CityNode cityNode = (CityNode) cityBuilder.build(ObjectType.NODE);
        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).withCity(cityNode).build(ObjectType.NODE);
        CityModel cityModel = (CityModel) cityBuilder.build(ObjectType.MODEL);
        String pathToCityLink = CITY_BASE_PATH + "/" + counterForUtilMethodsModel;
        cityModel.add(new Link(pathToCityLink));
        TargetModel targetModel = (TargetModel) targetBuilder.build(ObjectType.MODEL);
        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetModel.getId();
        targetModel.add(new Link(pathToTargetLink));

        EventModel eventModel = (EventModel) eventBuilder.withTarget(targetModel).withCity(cityModel).build(ObjectType.MODEL);
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
        eventModel.add(new Link(pathToEventLink));
        String pathToTargetEventLink = EVENT_BASE_PATH + "/" + eventModel.getId().intValue() + "/targets";
        eventModel.add(new Link(pathToTargetEventLink, "target"));

        String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

        when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
        when(modelAssembler.toModel(eventNode)).thenReturn(eventModel);

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, eventId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToEventLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToTargetEventLink)))
                        .andExpect(jsonPath("id", is(eventId.intValue())))
                        .andExpect(jsonPath("summary", is(eventModel.getSummary())))
                        .andExpect(jsonPath("motive", is(eventModel.getMotive())))
                        .andExpect(jsonPath("date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventModel.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("isSuicidal", is(eventModel.getIsSuicidal())))
                        .andExpect(jsonPath("isSuccessful", is(eventModel.getIsSuccessful())))
                        .andExpect(jsonPath("isPartOfMultipleIncidents", is(eventModel.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("target.id", is(targetModel.getId().intValue())))
                        .andExpect(jsonPath("target.target", is(targetModel.getTarget())))
                        .andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
                        .andExpect(jsonPath("city.id", is(cityModel.getId().intValue())))
                        .andExpect(jsonPath("city.name", is(cityModel.getName())))
                        .andExpect(jsonPath("city.latitude", is(cityModel.getLatitude())))
                        .andExpect(jsonPath("city.longitude", is(cityModel.getLongitude())))
                        .andExpect(jsonPath("city.links[0].href", is(pathToCityLink))),
                () -> verify(eventService, times(1)).findById(eventId),
                () -> verifyNoMoreInteractions(eventService),
                () -> verify(modelAssembler, times(1)).toModel(eventNode),
                () -> verifyNoMoreInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_find_event_but_event_does_not_exist_should_return_error_response() {

        Long eventId = 1L;

        String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

        when(eventService.findById(eventId)).thenReturn(Optional.empty());

        assertAll(
                () -> mockMvc.perform(get(linkWithParameter, eventId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find EventModel with id: " + eventId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(eventService, times(1)).findById(eventId),
                () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(modelAssembler),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(violationHelper),
                () -> verifyNoInteractions(pagedResourcesAssembler));
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

                CityNode cityNode = (CityNode) cityBuilder
                        .withId((long) counterForUtilMethodsModel)
                        .withName("city" + counterForUtilMethodsModel)
                        .withLatitude((double) (20 + counterForUtilMethodsModel))
                        .withLongitude((double) (40 + counterForUtilMethodsModel))
                        .build(ObjectType.NODE);

                TargetNode targetNode = (TargetNode) targetBuilder.withId((long) counterForUtilMethodsNode).withTarget("target" + counterForUtilMethodsNode)
                        .build(ObjectType.NODE);

                return eventBuilder.withId((long) counterForUtilMethodsNode).withSummary(summary + counterForUtilMethodsNode)
                        .withMotive(motive + counterForUtilMethodsNode).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                        .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetNode).withCity(cityNode)
                        .build(ObjectType.NODE);

            case MODEL:

                counterForUtilMethodsModel++;

                CityModel cityModel = (CityModel) cityBuilder
                        .withId((long) counterForUtilMethodsModel)
                        .withName("city" + counterForUtilMethodsModel)
                        .withLatitude((double) (20 + counterForUtilMethodsModel))
                        .withLongitude((double) (40 + counterForUtilMethodsModel))
                        .build(ObjectType.MODEL);
                String pathToCityLink = CITY_BASE_PATH + "/" + counterForUtilMethodsModel;
                cityModel.add(new Link(pathToCityLink));

                TargetModel targetModel = (TargetModel) targetBuilder.withId((long) counterForUtilMethodsModel)
                        .withTarget("target" + counterForUtilMethodsModel)
                        .build(ObjectType.MODEL);

                String pathToTargetLink = TARGET_BASE_PATH + "/" + counterForUtilMethodsModel;
                targetModel.add(new Link(pathToTargetLink));

                EventModel eventModel = (EventModel) eventBuilder.withId((long) counterForUtilMethodsModel)
                        .withSummary(summary + counterForUtilMethodsModel)
                        .withMotive(motive + counterForUtilMethodsModel)
                        .withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                        .withIsSuccessful(isSuccessful)
                        .withIsSuicidal(isSuicidal)
                        .withTarget(targetModel)
                        .withCity(cityModel)
                        .build(ObjectType.MODEL);
                String pathToEventLink = EVENT_BASE_PATH + "/" + counterForUtilMethodsModel;
                eventModel.add(new Link(pathToEventLink, "self"));
                String pathToTargetEventLink = EVENT_BASE_PATH + "/" + eventModel.getId().intValue() + "/targets";
                eventModel.add(new Link(pathToTargetEventLink, "target"));

                return eventModel;

            default:
                throw new RuntimeException("Invalid type");
        }
    }
}
