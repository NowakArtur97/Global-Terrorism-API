package com.NowakArtur97.GlobalTerrorismAPI.controller.event;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

import com.NowakArtur97.GlobalTerrorismAPI.advice.EventControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.EventModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.controller.EventController;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.EventService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.ViolationHelper;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventController_Tests")
public class EventControllerGetMethodTest {

	private final String EVENT_BASE_PATH = "http://localhost:8080/api/events";
	private final String TARGET_BASE_PATH = "http://localhost:8080/api/targets";

	private MockMvc mockMvc;

	private EventController eventController;

	@Mock
	private EventService eventService;

	@Mock
	private EventModelAssembler modelAssembler;

	@Mock
	private PagedResourcesAssembler<EventNode> pagedResourcesAssembler;

	@Mock
	private PatchHelper patchHelper;

	@Mock
	private ViolationHelper violationHelper;

	@BeforeEach
	private void setUp() {

		eventController = new EventController(eventService, modelAssembler, pagedResourcesAssembler, patchHelper,
				violationHelper);

		mockMvc = MockMvcBuilders.standaloneSetup(eventController).setControllerAdvice(new EventControllerAdvice())
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
	}

	@Test
	void when_find_all_events_with_default_parameters_in_link_and_events_exist_should_return_all_events()
			throws ParseException {

		Long eventId1 = 1L;
		String summary1 = "summary1";
		String motive1 = "motive1";
		Date date1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("03/07/2000 02:00:00:000");
		boolean isPartOfMultipleIncidents1 = true;
		boolean isSuccessful1 = true;
		boolean isSuicide1 = true;

		Long targetId1 = 1L;
		String target1 = "target1";
		TargetNode targetNode1 = new TargetNode(targetId1, target1);
		TargetModel targetModel1 = new TargetModel(targetId1, target1);
		String pathToTargetLink1 = TARGET_BASE_PATH + "/" + targetId1.intValue();
		Link targetLink1 = new Link(pathToTargetLink1);
		targetModel1.add(targetLink1);

		EventNode eventNode1 = EventNode.builder().id(eventId1).date(date1).summary(summary1)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents1).isSuccessful(isSuccessful1)
				.isSuicide(isSuicide1).motive(motive1).target(targetNode1).build();

		EventModel model1 = EventModel.builder().id(eventId1).date(date1).summary(summary1)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents1).isSuccessful(isSuccessful1)
				.isSuicide(isSuicide1).motive(motive1).target(targetModel1).build();

		String pathToLink1 = EVENT_BASE_PATH + eventId1.intValue();
		Link link1 = new Link(pathToLink1);
		model1.add(link1);

		Long eventId2 = 2L;
		String summary2 = "summary2";
		String motive2 = "motive2";
		Date date2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("03/07/2000 02:00:00:000");
		boolean isPartOfMultipleIncidents2 = true;
		boolean isSuccessful2 = true;
		boolean isSuicide2 = true;

		Long targetId2 = 2L;
		String target2 = "target2";
		TargetNode targetNode2 = new TargetNode(targetId2, target2);
		TargetModel targetModel2 = new TargetModel(targetId2, target2);
		String pathToTargetLink2 = TARGET_BASE_PATH + "/" + targetId2.intValue();
		Link targetLink2 = new Link(pathToTargetLink2);
		targetModel2.add(targetLink2);

		EventNode eventNode2 = EventNode.builder().id(eventId2).date(date2).summary(summary1)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents2).isSuccessful(isSuccessful2)
				.isSuicide(isSuicide2).motive(motive2).target(targetNode2).build();

		EventModel model2 = EventModel.builder().id(eventId2).date(date2).summary(summary2)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents2).isSuccessful(isSuccessful2)
				.isSuicide(isSuicide2).motive(motive2).target(targetModel2).build();

		String pathToLink2 = EVENT_BASE_PATH + eventId2.intValue();
		Link link2 = new Link(pathToLink2);
		model2.add(link2);

		Long eventId3 = 3L;
		String summary3 = "summary3";
		String motive3 = "motive3";
		Date date3 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("03/07/2000 02:00:00:000");
		boolean isPartOfMultipleIncidents3 = true;
		boolean isSuccessful3 = true;
		boolean isSuicide3 = true;

		Long targetId3 = 3L;
		String target3 = "target3";
		TargetNode targetNode3 = new TargetNode(targetId3, target3);
		TargetModel targetModel3 = new TargetModel(targetId3, target3);
		String pathToTargetLink3 = TARGET_BASE_PATH + "/" + targetId3.intValue();
		Link targetLink3 = new Link(pathToTargetLink3);
		targetModel3.add(targetLink3);

		EventNode eventNode3 = EventNode.builder().id(eventId3).date(date3).summary(summary3)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents3).isSuccessful(isSuccessful3)
				.isSuicide(isSuicide3).motive(motive3).target(targetNode3).build();

		EventModel model3 = EventModel.builder().id(eventId3).date(date3).summary(summary3)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents3).isSuccessful(isSuccessful3)
				.isSuicide(isSuicide3).motive(motive3).target(targetModel3).build();

		String pathToLink3 = EVENT_BASE_PATH + eventId3.intValue();
		Link link3 = new Link(pathToLink3);
		model3.add(link3);

		Long eventId4 = 4L;
		String summary4 = "summary4";
		String motive4 = "motive4";
		Date date4 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("03/07/2000 02:00:00:000");
		boolean isPartOfMultipleIncidents4 = true;
		boolean isSuccessful4 = true;
		boolean isSuicide4 = true;

		Long targetId4 = 4L;
		String target4 = "target4";
		TargetNode targetNode4 = new TargetNode(targetId4, target4);
		TargetModel targetModel4 = new TargetModel(targetId4, target4);
		String pathToTargetLink4 = TARGET_BASE_PATH + "/" + targetId4.intValue();
		Link targetLink4 = new Link(pathToTargetLink4);
		targetModel4.add(targetLink4);

		EventNode eventNode4 = EventNode.builder().id(eventId4).date(date4).summary(summary1)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents4).isSuccessful(isSuccessful4)
				.isSuicide(isSuicide4).motive(motive4).target(targetNode4).build();

		EventModel model4 = EventModel.builder().id(eventId4).date(date4).summary(summary4)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents4).isSuccessful(isSuccessful4)
				.isSuicide(isSuicide4).motive(motive4).target(targetModel4).build();

		String pathToLink4 = EVENT_BASE_PATH + eventId4.intValue();
		Link link4 = new Link(pathToLink4);
		model4.add(link4);

		List<EventNode> eventsListExpected = new ArrayList<>();
		eventsListExpected.add(eventNode1);
		eventsListExpected.add(eventNode2);
		eventsListExpected.add(eventNode3);
		eventsListExpected.add(eventNode4);

		List<EventModel> modelsListExpected = new ArrayList<>();
		modelsListExpected.add(model1);
		modelsListExpected.add(model2);
		modelsListExpected.add(model3);
		modelsListExpected.add(model4);

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
						.andExpect(jsonPath("links[3].href", is(lastPageLink)))
						.andExpect(jsonPath("content[0].links[0].href", is(pathToLink1)))
						.andExpect(jsonPath("content[0].id", is(eventId1.intValue())))
						.andExpect(jsonPath("content[0].summary", is(summary1)))
						.andExpect(jsonPath("content[0].motive", is(motive1)))
						.andExpect(jsonPath("content[0].date",
								is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
										.format(date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
						.andExpect(jsonPath("content[0].suicide", is(isSuicide1)))
						.andExpect(jsonPath("content[0].successful", is(isSuccessful1)))
						.andExpect(jsonPath("content[0].partOfMultipleIncidents", is(isPartOfMultipleIncidents1)))
						.andExpect(jsonPath("content[0].target.links[0].href", is(pathToTargetLink1)))
						.andExpect(jsonPath("content[0].target.id", is(targetId1.intValue())))
						.andExpect(jsonPath("content[0].target.target", is(target1)))
						.andExpect(jsonPath("content[1].links[0].href", is(pathToLink2)))
						.andExpect(jsonPath("content[1].id", is(eventId2.intValue())))
						.andExpect(jsonPath("content[1].summary", is(summary2)))
						.andExpect(jsonPath("content[1].motive", is(motive2)))
						.andExpect(jsonPath("content[1].date",
								is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
										.format(date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
						.andExpect(jsonPath("content[1].suicide", is(isSuicide2)))
						.andExpect(jsonPath("content[1].successful", is(isSuccessful2)))
						.andExpect(jsonPath("content[1].partOfMultipleIncidents", is(isPartOfMultipleIncidents2)))
						.andExpect(jsonPath("content[1].target.links[0].href", is(pathToTargetLink2)))
						.andExpect(jsonPath("content[1].target.id", is(targetId2.intValue())))
						.andExpect(jsonPath("content[1].target.target", is(target2)))
						.andExpect(jsonPath("content[2].links[0].href", is(pathToLink3)))
						.andExpect(jsonPath("content[2].id", is(eventId3.intValue())))
						.andExpect(jsonPath("content[2].summary", is(summary3)))
						.andExpect(jsonPath("content[2].motive", is(motive3)))
						.andExpect(jsonPath("content[2].date",
								is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
										.format(date3.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
						.andExpect(jsonPath("content[2].suicide", is(isSuicide3)))
						.andExpect(jsonPath("content[2].successful", is(isSuccessful3)))
						.andExpect(jsonPath("content[2].partOfMultipleIncidents", is(isPartOfMultipleIncidents3)))
						.andExpect(jsonPath("content[2].target.links[0].href", is(pathToTargetLink3)))
						.andExpect(jsonPath("content[2].target.id", is(targetId3.intValue())))
						.andExpect(jsonPath("content[2].target.target", is(target3)))
						.andExpect(jsonPath("content[3].links[0].href", is(pathToLink4)))
						.andExpect(jsonPath("content[3].id", is(eventId4.intValue())))
						.andExpect(jsonPath("content[3].summary", is(summary4)))
						.andExpect(jsonPath("content[3].motive", is(motive4)))
						.andExpect(jsonPath("content[3].date",
								is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
										.format(date4.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
						.andExpect(jsonPath("content[3].suicide", is(isSuicide4)))
						.andExpect(jsonPath("content[3].successful", is(isSuccessful4)))
						.andExpect(jsonPath("content[3].partOfMultipleIncidents", is(isPartOfMultipleIncidents4)))
						.andExpect(jsonPath("content[3].target.links[0].href", is(pathToTargetLink4)))
						.andExpect(jsonPath("content[3].target.id", is(targetId4.intValue())))
						.andExpect(jsonPath("content[3].target.target", is(target4)))
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
	void when_find_all_events_with_changed_parameters_in_link_and_events_exist_should_return_all_events()
			throws ParseException {

		Long eventId1 = 1L;
		String summary1 = "summary1";
		String motive1 = "motive1";
		Date date1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("03/07/2000 02:00:00:000");
		boolean isPartOfMultipleIncidents1 = true;
		boolean isSuccessful1 = true;
		boolean isSuicide1 = true;

		Long targetId1 = 1L;
		String target1 = "target1";
		TargetNode targetNode1 = new TargetNode(targetId1, target1);
		TargetModel targetModel1 = new TargetModel(targetId1, target1);
		String pathToTargetLink1 = TARGET_BASE_PATH + "/" + targetId1.intValue();
		Link targetLink1 = new Link(pathToTargetLink1);
		targetModel1.add(targetLink1);

		EventNode eventNode1 = EventNode.builder().id(eventId1).date(date1).summary(summary1)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents1).isSuccessful(isSuccessful1)
				.isSuicide(isSuicide1).motive(motive1).target(targetNode1).build();

		EventModel model1 = EventModel.builder().id(eventId1).date(date1).summary(summary1)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents1).isSuccessful(isSuccessful1)
				.isSuicide(isSuicide1).motive(motive1).target(targetModel1).build();

		String pathToLink1 = EVENT_BASE_PATH + eventId1.intValue();
		Link link1 = new Link(pathToLink1);
		model1.add(link1);

		Long eventId2 = 2L;
		String summary2 = "summary2";
		String motive2 = "motive2";
		Date date2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("03/07/2000 02:00:00:000");
		boolean isPartOfMultipleIncidents2 = true;
		boolean isSuccessful2 = true;
		boolean isSuicide2 = true;

		Long targetId2 = 2L;
		String target2 = "target2";
		TargetNode targetNode2 = new TargetNode(targetId2, target2);
		TargetModel targetModel2 = new TargetModel(targetId2, target2);
		String pathToTargetLink2 = TARGET_BASE_PATH + "/" + targetId2.intValue();
		Link targetLink2 = new Link(pathToTargetLink2);
		targetModel2.add(targetLink2);

		EventNode eventNode2 = EventNode.builder().id(eventId2).date(date2).summary(summary1)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents2).isSuccessful(isSuccessful2)
				.isSuicide(isSuicide2).motive(motive2).target(targetNode2).build();

		EventModel model2 = EventModel.builder().id(eventId2).date(date2).summary(summary2)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents2).isSuccessful(isSuccessful2)
				.isSuicide(isSuicide2).motive(motive2).target(targetModel2).build();

		String pathToLink2 = EVENT_BASE_PATH + eventId2.intValue();
		Link link2 = new Link(pathToLink2);
		model2.add(link2);

		Long eventId3 = 3L;
		String summary3 = "summary3";
		String motive3 = "motive3";
		Date date3 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("03/07/2000 02:00:00:000");
		boolean isPartOfMultipleIncidents3 = true;
		boolean isSuccessful3 = true;
		boolean isSuicide3 = true;

		Long targetId3 = 3L;
		String target3 = "target3";
		TargetNode targetNode3 = new TargetNode(targetId3, target3);
		TargetModel targetModel3 = new TargetModel(targetId3, target3);
		String pathToTargetLink3 = TARGET_BASE_PATH + "/" + targetId3.intValue();
		Link targetLink3 = new Link(pathToTargetLink3);
		targetModel3.add(targetLink3);

		EventNode eventNode3 = EventNode.builder().id(eventId3).date(date3).summary(summary3)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents3).isSuccessful(isSuccessful3)
				.isSuicide(isSuicide3).motive(motive3).target(targetNode3).build();

		EventModel model3 = EventModel.builder().id(eventId3).date(date3).summary(summary3)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents3).isSuccessful(isSuccessful3)
				.isSuicide(isSuicide3).motive(motive3).target(targetModel3).build();

		String pathToLink3 = EVENT_BASE_PATH + eventId3.intValue();
		Link link3 = new Link(pathToLink3);
		model3.add(link3);

		Long eventId4 = 4L;
		String summary4 = "summary4";
		String motive4 = "motive4";
		Date date4 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("03/07/2000 02:00:00:000");
		boolean isPartOfMultipleIncidents4 = true;
		boolean isSuccessful4 = true;
		boolean isSuicide4 = true;

		Long targetId4 = 4L;
		String target4 = "target4";
		TargetNode targetNode4 = new TargetNode(targetId4, target4);
		TargetModel targetModel4 = new TargetModel(targetId4, target4);
		String pathToTargetLink4 = TARGET_BASE_PATH + "/" + targetId4.intValue();
		Link targetLink4 = new Link(pathToTargetLink4);
		targetModel4.add(targetLink4);

		EventNode eventNode4 = EventNode.builder().id(eventId4).date(date4).summary(summary1)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents4).isSuccessful(isSuccessful4)
				.isSuicide(isSuicide4).motive(motive4).target(targetNode4).build();

		EventModel model4 = EventModel.builder().id(eventId4).date(date4).summary(summary4)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents4).isSuccessful(isSuccessful4)
				.isSuicide(isSuicide4).motive(motive4).target(targetModel4).build();

		String pathToLink4 = EVENT_BASE_PATH + eventId4.intValue();
		Link link4 = new Link(pathToLink4);
		model4.add(link4);

		List<EventNode> eventsListExpected = new ArrayList<>();
		eventsListExpected.add(eventNode1);
		eventsListExpected.add(eventNode2);
		eventsListExpected.add(eventNode3);
		eventsListExpected.add(eventNode4);

		List<EventModel> modelsListExpected = new ArrayList<>();
		modelsListExpected.add(model1);
		modelsListExpected.add(model2);
		modelsListExpected.add(model3);
		modelsListExpected.add(model4);

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
						.andExpect(jsonPath("links[3].href", is(lastPageLink)))
						.andExpect(jsonPath("content[0].links[0].href", is(pathToLink1)))
						.andExpect(jsonPath("content[0].id", is(eventId1.intValue())))
						.andExpect(jsonPath("content[0].summary", is(summary1)))
						.andExpect(jsonPath("content[0].motive", is(motive1)))
						.andExpect(jsonPath("content[0].date",
								is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
										.format(date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
						.andExpect(jsonPath("content[0].suicide", is(isSuicide1)))
						.andExpect(jsonPath("content[0].successful", is(isSuccessful1)))
						.andExpect(jsonPath("content[0].partOfMultipleIncidents", is(isPartOfMultipleIncidents1)))
						.andExpect(jsonPath("content[0].target.links[0].href", is(pathToTargetLink1)))
						.andExpect(jsonPath("content[0].target.id", is(targetId1.intValue())))
						.andExpect(jsonPath("content[0].target.target", is(target1)))
						.andExpect(jsonPath("content[1].links[0].href", is(pathToLink2)))
						.andExpect(jsonPath("content[1].id", is(eventId2.intValue())))
						.andExpect(jsonPath("content[1].summary", is(summary2)))
						.andExpect(jsonPath("content[1].motive", is(motive2)))
						.andExpect(jsonPath("content[1].date",
								is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
										.format(date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
						.andExpect(jsonPath("content[1].suicide", is(isSuicide2)))
						.andExpect(jsonPath("content[1].successful", is(isSuccessful2)))
						.andExpect(jsonPath("content[1].partOfMultipleIncidents", is(isPartOfMultipleIncidents2)))
						.andExpect(jsonPath("content[1].target.links[0].href", is(pathToTargetLink2)))
						.andExpect(jsonPath("content[1].target.id", is(targetId2.intValue())))
						.andExpect(jsonPath("content[1].target.target", is(target2)))
						.andExpect(jsonPath("content[2].links[0].href", is(pathToLink3)))
						.andExpect(jsonPath("content[2].id", is(eventId3.intValue())))
						.andExpect(jsonPath("content[2].summary", is(summary3)))
						.andExpect(jsonPath("content[2].motive", is(motive3)))
						.andExpect(jsonPath("content[2].date",
								is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
										.format(date3.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
						.andExpect(jsonPath("content[2].suicide", is(isSuicide3)))
						.andExpect(jsonPath("content[2].successful", is(isSuccessful3)))
						.andExpect(jsonPath("content[2].partOfMultipleIncidents", is(isPartOfMultipleIncidents3)))
						.andExpect(jsonPath("content[2].target.links[0].href", is(pathToTargetLink3)))
						.andExpect(jsonPath("content[2].target.id", is(targetId3.intValue())))
						.andExpect(jsonPath("content[2].target.target", is(target3)))
						.andExpect(jsonPath("content[3].links[0].href", is(pathToLink4)))
						.andExpect(jsonPath("content[3].id", is(eventId4.intValue())))
						.andExpect(jsonPath("content[3].summary", is(summary4)))
						.andExpect(jsonPath("content[3].motive", is(motive4)))
						.andExpect(jsonPath("content[3].date",
								is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
										.format(date4.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
						.andExpect(jsonPath("content[3].suicide", is(isSuicide4)))
						.andExpect(jsonPath("content[3].successful", is(isSuccessful4)))
						.andExpect(jsonPath("content[3].partOfMultipleIncidents", is(isPartOfMultipleIncidents4)))
						.andExpect(jsonPath("content[3].target.links[0].href", is(pathToTargetLink4)))
						.andExpect(jsonPath("content[3].target.id", is(targetId4.intValue())))
						.andExpect(jsonPath("content[3].target.target", is(target4)))
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
	void when_find_existing_event_should_return_event() throws ParseException {

		Long eventId = 1L;

		String summary = "summary";
		String motive = "motive";
		Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("03/07/2000 02:00:00:000");
		boolean isPartOfMultipleIncidents = true;
		boolean isSuccessful = true;
		boolean isSuicide = true;

		Long targetId = 1L;
		String target = "target";
		TargetNode targetNode = new TargetNode(targetId, target);
		TargetModel targetModel = new TargetModel(targetId, target);
		String pathToTargetLink = TARGET_BASE_PATH + "/" + targetId.intValue();
		Link targetLink = new Link(pathToTargetLink);
		targetModel.add(targetLink);

		EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
				.isSuicide(isSuicide).motive(motive).target(targetNode).build();

		EventModel model = EventModel.builder().id(eventId).date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
				.isSuicide(isSuicide).motive(motive).target(targetModel).build();

		String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
		Link eventLink = new Link(pathToEventLink);
		model.add(eventLink);

		String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

		when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
		when(modelAssembler.toModel(eventNode)).thenReturn(model);

		assertAll(
				() -> mockMvc.perform(get(linkWithParameter, eventId)).andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(pathToEventLink)))
						.andExpect(jsonPath("id", is(eventId.intValue())))
						.andExpect(jsonPath("summary", is(summary))).andExpect(jsonPath("motive", is(motive)))
						.andExpect(jsonPath("date",
								is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
										.format(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
						.andExpect(jsonPath("suicide", is(isSuicide)))
						.andExpect(jsonPath("successful", is(isSuccessful)))
						.andExpect(jsonPath("partOfMultipleIncidents", is(isPartOfMultipleIncidents)))
						.andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
						.andExpect(jsonPath("target.id", is(targetId.intValue())))
						.andExpect(jsonPath("target.target", is(target))),
				() -> verify(eventService, times(1)).findById(eventId), () -> verifyNoMoreInteractions(eventService),
				() -> verify(modelAssembler, times(1)).toModel(eventNode),
				() -> verifyNoMoreInteractions(modelAssembler), () -> verifyNoInteractions(patchHelper),
				() -> verifyNoInteractions(violationHelper));
	}

	@Test
	void when_find_existing_event_without_target_should_return_event_withput_target() throws ParseException {

		Long eventId = 1L;

		String summary = "summary";
		String motive = "motive";
		Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("03/07/2000 02:00:00:000");
		boolean isPartOfMultipleIncidents = true;
		boolean isSuccessful = true;
		boolean isSuicide = true;

		EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
				.isSuicide(isSuicide).motive(motive).build();

		EventModel model = EventModel.builder().id(eventId).date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
				.isSuicide(isSuicide).motive(motive).build();

		String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
		Link eventLink = new Link(pathToEventLink);
		model.add(eventLink);

		String linkWithParameter = EVENT_BASE_PATH + "/" + "{id}";

		when(eventService.findById(eventId)).thenReturn(Optional.of(eventNode));
		when(modelAssembler.toModel(eventNode)).thenReturn(model);

		assertAll(
				() -> mockMvc.perform(get(linkWithParameter, eventId)).andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("links[0].href", is(pathToEventLink)))
						.andExpect(jsonPath("id", is(eventId.intValue())))
						.andExpect(jsonPath("summary", is(summary))).andExpect(jsonPath("motive", is(motive)))
						.andExpect(jsonPath("date",
								is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
										.format(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))))
						.andExpect(jsonPath("suicide", is(isSuicide)))
						.andExpect(jsonPath("successful", is(isSuccessful)))
						.andExpect(jsonPath("partOfMultipleIncidents", is(isPartOfMultipleIncidents)))
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
						.andExpect(jsonPath("errors[0]", is("Could not find event with id: " + eventId))),
				() -> verify(eventService, times(1)).findById(eventId), () -> verifyNoMoreInteractions(eventService),
				() -> verifyNoInteractions(modelAssembler), () -> verifyNoInteractions(patchHelper),
				() -> verifyNoInteractions(violationHelper));
	}
}
