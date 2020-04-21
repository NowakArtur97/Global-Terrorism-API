package com.NowakArtur97.GlobalTerrorismAPI.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.NowakArtur97.GlobalTerrorismAPI.advice.EventControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.advice.RestResponseGlobalEntityExceptionHandler;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.EventModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.EventService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.icu.util.Calendar;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventController_Tests")
class EventControllerTest {

	private final String EVENT_BASE_PATH = "http://localhost:8080/api/events";
	private final String TARGET_BASE_PATH = "http://localhost:8080/api/targets";

	private MockMvc mockMvc;

	private EventController eventController;

	private RestResponseGlobalEntityExceptionHandler restResponseGlobalEntityExceptionHandler;

	@Mock
	private EventService eventService;

	@Mock
	private EventModelAssembler eventModelAssembler;

	@Mock
	private PagedResourcesAssembler<EventNode> pagedResourcesAssembler;

	@BeforeEach
	private void setUp() {

		eventController = new EventController(eventService, eventModelAssembler, pagedResourcesAssembler);

		restResponseGlobalEntityExceptionHandler = new RestResponseGlobalEntityExceptionHandler();

		mockMvc = MockMvcBuilders.standaloneSetup(eventController, restResponseGlobalEntityExceptionHandler)
				.setControllerAdvice(new EventControllerAdvice())
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
//				.setMessageConverters(new JsonMergePatchHttpMessageConverter(), new JsonPatchHttpMessageConverter(),
//						new MappingJackson2HttpMessageConverter())
				.build();
	}

	@Nested
	@Tag("GetEventRequest_Tests")
	class GetEventRequestTest {

		@Test
		void when_find_all_events_with_default_parameters_in_link_and_events_exist_should_return_all_events() {

			Long eventId1 = 1L;
			String eventSummary1 = "summary1";
			String eventMotive1 = "motive1";
			Date eventDate1 = Calendar.getInstance().getTime();
			boolean isEventPartOfMultipleIncidents1 = true;
			boolean isEventSuccessful1 = true;
			boolean isEventSuicide1 = true;

			Long targetId1 = 1L;
			String target1 = "target1";
			TargetNode targetNode1 = new TargetNode(targetId1, target1);
			TargetModel targetModel1 = new TargetModel(targetId1, target1);
			String pathToTargetLink1 = TARGET_BASE_PATH + "/" + targetId1.intValue();
			Link targetLink1 = new Link(pathToTargetLink1);
			targetModel1.add(targetLink1);

			EventNode eventNode1 = EventNode.builder().id(eventId1).date(eventDate1).summary(eventSummary1)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents1).isSuccessful(isEventSuccessful1)
					.isSuicide(isEventSuicide1).motive(eventMotive1).target(targetNode1).build();

			EventModel eventModel1 = EventModel.builder().id(eventId1).date(eventDate1).summary(eventSummary1)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents1).isSuccessful(isEventSuccessful1)
					.isSuicide(isEventSuicide1).motive(eventMotive1).target(targetModel1).build();

			String pathToLink1 = EVENT_BASE_PATH + eventId1.intValue();
			Link link1 = new Link(pathToLink1);
			eventModel1.add(link1);

			Long eventId2 = 2L;
			String eventSummary2 = "summary2";
			String eventMotive2 = "motive2";
			Date eventDate2 = Calendar.getInstance().getTime();
			boolean isEventPartOfMultipleIncidents2 = true;
			boolean isEventSuccessful2 = true;
			boolean isEventSuicide2 = true;

			Long targetId2 = 2L;
			String target2 = "target2";
			TargetNode targetNode2 = new TargetNode(targetId2, target2);
			TargetModel targetModel2 = new TargetModel(targetId2, target2);
			String pathToTargetLink2 = TARGET_BASE_PATH + "/" + targetId2.intValue();
			Link targetLink2 = new Link(pathToTargetLink2);
			targetModel2.add(targetLink2);

			EventNode eventNode2 = EventNode.builder().id(eventId2).date(eventDate2).summary(eventSummary1)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents2).isSuccessful(isEventSuccessful2)
					.isSuicide(isEventSuicide2).motive(eventMotive2).target(targetNode2).build();

			EventModel eventModel2 = EventModel.builder().id(eventId2).date(eventDate2).summary(eventSummary2)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents2).isSuccessful(isEventSuccessful2)
					.isSuicide(isEventSuicide2).motive(eventMotive2).target(targetModel2).build();

			String pathToLink2 = EVENT_BASE_PATH + eventId2.intValue();
			Link link2 = new Link(pathToLink2);
			eventModel2.add(link2);

			Long eventId3 = 3L;
			String eventSummary3 = "summary3";
			String eventMotive3 = "motive3";
			Date eventDate3 = Calendar.getInstance().getTime();
			boolean isEventPartOfMultipleIncidents3 = true;
			boolean isEventSuccessful3 = true;
			boolean isEventSuicide3 = true;

			Long targetId3 = 3L;
			String target3 = "target3";
			TargetNode targetNode3 = new TargetNode(targetId3, target3);
			TargetModel targetModel3 = new TargetModel(targetId3, target3);
			String pathToTargetLink3 = TARGET_BASE_PATH + "/" + targetId3.intValue();
			Link targetLink3 = new Link(pathToTargetLink3);
			targetModel3.add(targetLink3);

			EventNode eventNode3 = EventNode.builder().id(eventId3).date(eventDate3).summary(eventSummary3)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents3).isSuccessful(isEventSuccessful3)
					.isSuicide(isEventSuicide3).motive(eventMotive3).target(targetNode3).build();

			EventModel eventModel3 = EventModel.builder().id(eventId3).date(eventDate3).summary(eventSummary3)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents3).isSuccessful(isEventSuccessful3)
					.isSuicide(isEventSuicide3).motive(eventMotive3).target(targetModel3).build();

			String pathToLink3 = EVENT_BASE_PATH + eventId3.intValue();
			Link link3 = new Link(pathToLink3);
			eventModel3.add(link3);

			Long eventId4 = 4L;
			String eventSummary4 = "summary4";
			String eventMotive4 = "motive4";
			Date eventDate4 = Calendar.getInstance().getTime();
			boolean isEventPartOfMultipleIncidents4 = true;
			boolean isEventSuccessful4 = true;
			boolean isEventSuicide4 = true;

			Long targetId4 = 4L;
			String target4 = "target4";
			TargetNode targetNode4 = new TargetNode(targetId4, target4);
			TargetModel targetModel4 = new TargetModel(targetId4, target4);
			String pathToTargetLink4 = TARGET_BASE_PATH + "/" + targetId4.intValue();
			Link targetLink4 = new Link(pathToTargetLink4);
			targetModel4.add(targetLink4);

			EventNode eventNode4 = EventNode.builder().id(eventId4).date(eventDate4).summary(eventSummary1)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents4).isSuccessful(isEventSuccessful4)
					.isSuicide(isEventSuicide4).motive(eventMotive4).target(targetNode4).build();

			EventModel eventModel4 = EventModel.builder().id(eventId4).date(eventDate4).summary(eventSummary4)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents4).isSuccessful(isEventSuccessful4)
					.isSuicide(isEventSuicide4).motive(eventMotive4).target(targetModel4).build();

			String pathToLink4 = EVENT_BASE_PATH + eventId4.intValue();
			Link link4 = new Link(pathToLink4);
			eventModel4.add(link4);

			List<EventNode> eventsListExpected = new ArrayList<>();
			eventsListExpected.add(eventNode1);
			eventsListExpected.add(eventNode2);
			eventsListExpected.add(eventNode3);
			eventsListExpected.add(eventNode4);

			List<EventModel> eventModelsListExpected = new ArrayList<>();
			eventModelsListExpected.add(eventModel1);
			eventModelsListExpected.add(eventModel2);
			eventModelsListExpected.add(eventModel3);
			eventModelsListExpected.add(eventModel4);

			Page<EventNode> eventsExpected = new PageImpl<>(eventsListExpected);

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
			when(pagedResourcesAssembler.toModel(eventsExpected, eventModelAssembler)).thenReturn(resources);

			assertAll(
					() -> mockMvc.perform(get(firstPageLink)).andExpect(status().isOk())
							.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
							.andExpect(jsonPath("links[0].href", is(firstPageLink)))
							.andExpect(jsonPath("links[1].href", is(firstPageLink)))
							.andExpect(jsonPath("links[2].href", is(lastPageLink)))
							.andExpect(jsonPath("links[3].href", is(lastPageLink)))
							.andExpect(jsonPath("content[0].links[0].href", is(pathToLink1)))
							.andExpect(jsonPath("content[0].id", is(eventId1.intValue())))
							.andExpect(jsonPath("content[0].summary", is(eventSummary1)))
							.andExpect(jsonPath("content[0].motive", is(eventMotive1)))
							.andExpect(jsonPath("content[0].date",
									is(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(
											eventDate1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
							.andExpect(jsonPath("content[0].suicide", is(isEventSuicide1)))
							.andExpect(jsonPath("content[0].successful", is(isEventSuccessful1)))
							.andExpect(
									jsonPath("content[0].partOfMultipleIncidents", is(isEventPartOfMultipleIncidents1)))
							.andExpect(jsonPath("content[0].target.links[0].href", is(pathToTargetLink1)))
							.andExpect(jsonPath("content[0].target.id", is(targetId1.intValue())))
							.andExpect(jsonPath("content[0].target.target", is(target1)))
							.andExpect(jsonPath("content[1].links[0].href", is(pathToLink2)))
							.andExpect(jsonPath("content[1].id", is(eventId2.intValue())))
							.andExpect(jsonPath("content[1].summary", is(eventSummary2)))
							.andExpect(jsonPath("content[1].motive", is(eventMotive2)))
							.andExpect(jsonPath("content[1].date",
									is(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(
											eventDate2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
							.andExpect(jsonPath("content[1].suicide", is(isEventSuicide2)))
							.andExpect(jsonPath("content[1].successful", is(isEventSuccessful2)))
							.andExpect(
									jsonPath("content[1].partOfMultipleIncidents", is(isEventPartOfMultipleIncidents2)))
							.andExpect(jsonPath("content[1].target.links[0].href", is(pathToTargetLink2)))
							.andExpect(jsonPath("content[1].target.id", is(targetId2.intValue())))
							.andExpect(jsonPath("content[1].target.target", is(target2)))
							.andExpect(jsonPath("content[2].links[0].href", is(pathToLink3)))
							.andExpect(jsonPath("content[2].id", is(eventId3.intValue())))
							.andExpect(jsonPath("content[2].summary", is(eventSummary3)))
							.andExpect(jsonPath("content[2].motive", is(eventMotive3)))
							.andExpect(jsonPath("content[2].date",
									is(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(
											eventDate3.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
							.andExpect(jsonPath("content[2].suicide", is(isEventSuicide3)))
							.andExpect(jsonPath("content[2].successful", is(isEventSuccessful3)))
							.andExpect(
									jsonPath("content[2].partOfMultipleIncidents", is(isEventPartOfMultipleIncidents3)))
							.andExpect(jsonPath("content[2].target.links[0].href", is(pathToTargetLink3)))
							.andExpect(jsonPath("content[2].target.id", is(targetId3.intValue())))
							.andExpect(jsonPath("content[2].target.target", is(target3)))
							.andExpect(jsonPath("content[3].links[0].href", is(pathToLink4)))
							.andExpect(jsonPath("content[3].id", is(eventId4.intValue())))
							.andExpect(jsonPath("content[3].summary", is(eventSummary4)))
							.andExpect(jsonPath("content[3].motive", is(eventMotive4)))
							.andExpect(jsonPath("content[3].date",
									is(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(
											eventDate4.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
							.andExpect(jsonPath("content[3].suicide", is(isEventSuicide4)))
							.andExpect(jsonPath("content[3].successful", is(isEventSuccessful4)))
							.andExpect(
									jsonPath("content[3].partOfMultipleIncidents", is(isEventPartOfMultipleIncidents4)))
							.andExpect(jsonPath("content[3].target.links[0].href", is(pathToTargetLink4)))
							.andExpect(jsonPath("content[3].target.id", is(targetId4.intValue())))
							.andExpect(jsonPath("content[3].target.target", is(target4)))
							.andExpect(jsonPath("page.size", is(sizeExpected)))
							.andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
							.andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
							.andExpect(jsonPath("page.number", is(numberExpected))),
					() -> verify(eventService, times(1)).findAll(pageable),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(pagedResourcesAssembler, times(1)).toModel(eventsExpected, eventModelAssembler),
					() -> verifyNoMoreInteractions(pagedResourcesAssembler));
		}

		@Test
		void when_find_all_events_with_changed_parameters_in_link_and_events_exist_should_return_all_events() {

			Long eventId1 = 1L;
			String eventSummary1 = "summary1";
			String eventMotive1 = "motive1";
			Date eventDate1 = Calendar.getInstance().getTime();
			boolean isEventPartOfMultipleIncidents1 = true;
			boolean isEventSuccessful1 = true;
			boolean isEventSuicide1 = true;

			Long targetId1 = 1L;
			String target1 = "target1";
			TargetNode targetNode1 = new TargetNode(targetId1, target1);
			TargetModel targetModel1 = new TargetModel(targetId1, target1);
			String pathToTargetLink1 = TARGET_BASE_PATH + "/" + targetId1.intValue();
			Link targetLink1 = new Link(pathToTargetLink1);
			targetModel1.add(targetLink1);

			EventNode eventNode1 = EventNode.builder().id(eventId1).date(eventDate1).summary(eventSummary1)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents1).isSuccessful(isEventSuccessful1)
					.isSuicide(isEventSuicide1).motive(eventMotive1).target(targetNode1).build();

			EventModel eventModel1 = EventModel.builder().id(eventId1).date(eventDate1).summary(eventSummary1)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents1).isSuccessful(isEventSuccessful1)
					.isSuicide(isEventSuicide1).motive(eventMotive1).target(targetModel1).build();

			String pathToLink1 = EVENT_BASE_PATH + eventId1.intValue();
			Link link1 = new Link(pathToLink1);
			eventModel1.add(link1);

			Long eventId2 = 2L;
			String eventSummary2 = "summary2";
			String eventMotive2 = "motive2";
			Date eventDate2 = Calendar.getInstance().getTime();
			boolean isEventPartOfMultipleIncidents2 = true;
			boolean isEventSuccessful2 = true;
			boolean isEventSuicide2 = true;

			Long targetId2 = 2L;
			String target2 = "target2";
			TargetNode targetNode2 = new TargetNode(targetId2, target2);
			TargetModel targetModel2 = new TargetModel(targetId2, target2);
			String pathToTargetLink2 = TARGET_BASE_PATH + "/" + targetId2.intValue();
			Link targetLink2 = new Link(pathToTargetLink2);
			targetModel2.add(targetLink2);

			EventNode eventNode2 = EventNode.builder().id(eventId2).date(eventDate2).summary(eventSummary1)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents2).isSuccessful(isEventSuccessful2)
					.isSuicide(isEventSuicide2).motive(eventMotive2).target(targetNode2).build();

			EventModel eventModel2 = EventModel.builder().id(eventId2).date(eventDate2).summary(eventSummary2)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents2).isSuccessful(isEventSuccessful2)
					.isSuicide(isEventSuicide2).motive(eventMotive2).target(targetModel2).build();

			String pathToLink2 = EVENT_BASE_PATH + eventId2.intValue();
			Link link2 = new Link(pathToLink2);
			eventModel2.add(link2);

			Long eventId3 = 3L;
			String eventSummary3 = "summary3";
			String eventMotive3 = "motive3";
			Date eventDate3 = Calendar.getInstance().getTime();
			boolean isEventPartOfMultipleIncidents3 = true;
			boolean isEventSuccessful3 = true;
			boolean isEventSuicide3 = true;

			Long targetId3 = 3L;
			String target3 = "target3";
			TargetNode targetNode3 = new TargetNode(targetId3, target3);
			TargetModel targetModel3 = new TargetModel(targetId3, target3);
			String pathToTargetLink3 = TARGET_BASE_PATH + "/" + targetId3.intValue();
			Link targetLink3 = new Link(pathToTargetLink3);
			targetModel3.add(targetLink3);

			EventNode eventNode3 = EventNode.builder().id(eventId3).date(eventDate3).summary(eventSummary3)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents3).isSuccessful(isEventSuccessful3)
					.isSuicide(isEventSuicide3).motive(eventMotive3).target(targetNode3).build();

			EventModel eventModel3 = EventModel.builder().id(eventId3).date(eventDate3).summary(eventSummary3)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents3).isSuccessful(isEventSuccessful3)
					.isSuicide(isEventSuicide3).motive(eventMotive3).target(targetModel3).build();

			String pathToLink3 = EVENT_BASE_PATH + eventId3.intValue();
			Link link3 = new Link(pathToLink3);
			eventModel3.add(link3);

			Long eventId4 = 4L;
			String eventSummary4 = "summary4";
			String eventMotive4 = "motive4";
			Date eventDate4 = Calendar.getInstance().getTime();
			boolean isEventPartOfMultipleIncidents4 = true;
			boolean isEventSuccessful4 = true;
			boolean isEventSuicide4 = true;

			Long targetId4 = 4L;
			String target4 = "target4";
			TargetNode targetNode4 = new TargetNode(targetId4, target4);
			TargetModel targetModel4 = new TargetModel(targetId4, target4);
			String pathToTargetLink4 = TARGET_BASE_PATH + "/" + targetId4.intValue();
			Link targetLink4 = new Link(pathToTargetLink4);
			targetModel4.add(targetLink4);

			EventNode eventNode4 = EventNode.builder().id(eventId4).date(eventDate4).summary(eventSummary1)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents4).isSuccessful(isEventSuccessful4)
					.isSuicide(isEventSuicide4).motive(eventMotive4).target(targetNode4).build();

			EventModel eventModel4 = EventModel.builder().id(eventId4).date(eventDate4).summary(eventSummary4)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents4).isSuccessful(isEventSuccessful4)
					.isSuicide(isEventSuicide4).motive(eventMotive4).target(targetModel4).build();

			String pathToLink4 = EVENT_BASE_PATH + eventId4.intValue();
			Link link4 = new Link(pathToLink4);
			eventModel4.add(link4);

			List<EventNode> eventsListExpected = new ArrayList<>();
			eventsListExpected.add(eventNode1);
			eventsListExpected.add(eventNode2);
			eventsListExpected.add(eventNode3);
			eventsListExpected.add(eventNode4);

			List<EventModel> eventModelsListExpected = new ArrayList<>();
			eventModelsListExpected.add(eventModel1);
			eventModelsListExpected.add(eventModel2);
			eventModelsListExpected.add(eventModel3);
			eventModelsListExpected.add(eventModel4);

			Page<EventNode> eventsExpected = new PageImpl<>(eventsListExpected);

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
			when(pagedResourcesAssembler.toModel(eventsExpected, eventModelAssembler)).thenReturn(resources);

			assertAll(
					() -> mockMvc.perform(get(firstPageLink)).andExpect(status().isOk())
							.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
							.andExpect(jsonPath("links[0].href", is(firstPageLink)))
							.andExpect(jsonPath("links[1].href", is(firstPageLink)))
							.andExpect(jsonPath("links[2].href", is(lastPageLink)))
							.andExpect(jsonPath("links[3].href", is(lastPageLink)))
							.andExpect(jsonPath("content[0].links[0].href", is(pathToLink1)))
							.andExpect(jsonPath("content[0].id", is(eventId1.intValue())))
							.andExpect(jsonPath("content[0].summary", is(eventSummary1)))
							.andExpect(jsonPath("content[0].motive", is(eventMotive1)))
							.andExpect(jsonPath("content[0].date",
									is(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(
											eventDate1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
							.andExpect(jsonPath("content[0].suicide", is(isEventSuicide1)))
							.andExpect(jsonPath("content[0].successful", is(isEventSuccessful1)))
							.andExpect(
									jsonPath("content[0].partOfMultipleIncidents", is(isEventPartOfMultipleIncidents1)))
							.andExpect(jsonPath("content[0].target.links[0].href", is(pathToTargetLink1)))
							.andExpect(jsonPath("content[0].target.id", is(targetId1.intValue())))
							.andExpect(jsonPath("content[0].target.target", is(target1)))
							.andExpect(jsonPath("content[1].links[0].href", is(pathToLink2)))
							.andExpect(jsonPath("content[1].id", is(eventId2.intValue())))
							.andExpect(jsonPath("content[1].summary", is(eventSummary2)))
							.andExpect(jsonPath("content[1].motive", is(eventMotive2)))
							.andExpect(jsonPath("content[1].date",
									is(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(
											eventDate2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
							.andExpect(jsonPath("content[1].suicide", is(isEventSuicide2)))
							.andExpect(jsonPath("content[1].successful", is(isEventSuccessful2)))
							.andExpect(
									jsonPath("content[1].partOfMultipleIncidents", is(isEventPartOfMultipleIncidents2)))
							.andExpect(jsonPath("content[1].target.links[0].href", is(pathToTargetLink2)))
							.andExpect(jsonPath("content[1].target.id", is(targetId2.intValue())))
							.andExpect(jsonPath("content[1].target.target", is(target2)))
							.andExpect(jsonPath("content[2].links[0].href", is(pathToLink3)))
							.andExpect(jsonPath("content[2].id", is(eventId3.intValue())))
							.andExpect(jsonPath("content[2].summary", is(eventSummary3)))
							.andExpect(jsonPath("content[2].motive", is(eventMotive3)))
							.andExpect(jsonPath("content[2].date",
									is(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(
											eventDate3.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
							.andExpect(jsonPath("content[2].suicide", is(isEventSuicide3)))
							.andExpect(jsonPath("content[2].successful", is(isEventSuccessful3)))
							.andExpect(
									jsonPath("content[2].partOfMultipleIncidents", is(isEventPartOfMultipleIncidents3)))
							.andExpect(jsonPath("content[2].target.links[0].href", is(pathToTargetLink3)))
							.andExpect(jsonPath("content[2].target.id", is(targetId3.intValue())))
							.andExpect(jsonPath("content[2].target.target", is(target3)))
							.andExpect(jsonPath("content[3].links[0].href", is(pathToLink4)))
							.andExpect(jsonPath("content[3].id", is(eventId4.intValue())))
							.andExpect(jsonPath("content[3].summary", is(eventSummary4)))
							.andExpect(jsonPath("content[3].motive", is(eventMotive4)))
							.andExpect(jsonPath("content[3].date",
									is(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(
											eventDate4.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
							.andExpect(jsonPath("content[3].suicide", is(isEventSuicide4)))
							.andExpect(jsonPath("content[3].successful", is(isEventSuccessful4)))
							.andExpect(
									jsonPath("content[3].partOfMultipleIncidents", is(isEventPartOfMultipleIncidents4)))
							.andExpect(jsonPath("content[3].target.links[0].href", is(pathToTargetLink4)))
							.andExpect(jsonPath("content[3].target.id", is(targetId4.intValue())))
							.andExpect(jsonPath("content[3].target.target", is(target4)))
							.andExpect(jsonPath("page.size", is(sizeExpected)))
							.andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
							.andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
							.andExpect(jsonPath("page.number", is(numberExpected))),
					() -> verify(eventService, times(1)).findAll(pageable),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(pagedResourcesAssembler, times(1)).toModel(eventsExpected, eventModelAssembler),
					() -> verifyNoMoreInteractions(pagedResourcesAssembler));
		}

		@Test
		void when_find_all_events_but_events_not_exist_should_return_empty_list() {

			List<EventNode> eventsListExpected = new ArrayList<>();

			List<EventModel> eventModelsListExpected = new ArrayList<>();

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
			PagedModel<EventModel> resources = new PagedModel<>(eventModelsListExpected, metadata, pageLink1, pageLink2,
					pageLink3, pageLink4);

			when(eventService.findAll(pageable)).thenReturn(eventsExpected);
			when(pagedResourcesAssembler.toModel(eventsExpected, eventModelAssembler)).thenReturn(resources);

			assertAll(
					() -> mockMvc.perform(get(firstPageLink)).andExpect(status().isOk())
							.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
							.andExpect(jsonPath("links[0].href", is(firstPageLink)))
							.andExpect(jsonPath("links[1].href", is(firstPageLink)))
							.andExpect(jsonPath("links[2].href", is(lastPageLink)))
							.andExpect(jsonPath("links[3].href", is(lastPageLink)))
							.andExpect(jsonPath("content").isEmpty()).andExpect(jsonPath("page.size", is(sizeExpected)))
							.andExpect(jsonPath("page.totalElements", is(totalElementsExpected)))
							.andExpect(jsonPath("page.totalPages", is(totalPagesExpected)))
							.andExpect(jsonPath("page.number", is(numberExpected))),
					() -> verify(eventService, times(1)).findAll(pageable),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(pagedResourcesAssembler, times(1)).toModel(eventsExpected, eventModelAssembler),
					() -> verifyNoMoreInteractions(pagedResourcesAssembler));
		}

		@Test
		void when_find_existing_event_should_return_event() {

			Long eventId = 1L;

			String eventSummary = "summary";
			String eventMotive = "motive";
			Date eventDate = Calendar.getInstance().getTime();
			boolean isEventPartOfMultipleIncidents = true;
			boolean isEventSuccessful = true;
			boolean isEventSuicide = true;

			Long targetId = 1L;
			String target = "target";
			TargetNode targetNode = new TargetNode(targetId, target);
			TargetModel targetModel = new TargetModel(targetId, target);
			String pathToTargetLink = TARGET_BASE_PATH + "/" + targetId.intValue();
			Link targetLink = new Link(pathToTargetLink);
			targetModel.add(targetLink);

			EventNode eventNode = EventNode.builder().id(eventId).date(eventDate).summary(eventSummary)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents).isSuccessful(isEventSuccessful)
					.isSuicide(isEventSuicide).motive(eventMotive).target(targetNode).build();

			EventModel eventModel = EventModel.builder().id(eventId).date(eventDate).summary(eventSummary)
					.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents).isSuccessful(isEventSuccessful)
					.isSuicide(isEventSuicide).motive(eventMotive).target(targetModel).build();

			String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
			Link eventLink = new Link(pathToEventLink);
			eventModel.add(eventLink);

			String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";
			String linkExpected = EVENT_BASE_PATH + "/" + eventId;

			when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
			when(eventModelAssembler.toModel(eventNode)).thenReturn(eventModel);

			assertAll(
					() -> mockMvc.perform(get(linkWithParameter, eventId)).andExpect(status().isOk())
							.andDo(MockMvcResultHandlers.print())
							.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
							.andExpect(jsonPath("links[0].href", is(linkExpected)))
							.andExpect(jsonPath("id", is(eventId.intValue())))
							.andExpect(jsonPath("summary", is(eventSummary)))
							.andExpect(jsonPath("motive", is(eventMotive)))
							.andExpect(jsonPath("date",
									is(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(
											eventDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
							.andExpect(jsonPath("suicide", is(isEventSuicide)))
							.andExpect(jsonPath("successful", is(isEventSuccessful)))
							.andExpect(jsonPath("partOfMultipleIncidents", is(isEventPartOfMultipleIncidents)))
							.andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
							.andExpect(jsonPath("target.id", is(targetId.intValue())))
							.andExpect(jsonPath("target.target", is(target))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verifyNoMoreInteractions(eventService),
					() -> verify(eventModelAssembler, times(1)).toModel(eventNode),
					() -> verifyNoMoreInteractions(eventModelAssembler));
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
							.andExpect(jsonPath("errors[0]", is("Could not find event with id: " + eventId))),
					() -> verify(eventService, times(1)).findById(eventId),
					() -> verifyNoMoreInteractions(eventService), () -> verifyNoInteractions(eventModelAssembler));
		}
	}

	public static String asJsonString(final Object obj) {

		try {

			return new ObjectMapper().writeValueAsString(obj);

		} catch (Exception e) {

			throw new RuntimeException(e);
		}
	}
}