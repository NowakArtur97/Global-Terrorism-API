package com.NowakArtur97.GlobalTerrorismAPI.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.DTOMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.EventRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.EventService;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;
import com.NowakArtur97.GlobalTerrorismAPI.service.impl.EventServiceImpl;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.ibm.icu.util.Calendar;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventServiceImpl_Tests")
public class EventServiceImplTest {

	private EventService eventService;

	@Mock
	private EventRepository eventRepository;

	@Mock
	private DTOMapper dtoMapper;

	@Mock
	private TargetService targetService;

	@BeforeEach
	private void setUp() {

		eventService = new EventServiceImpl(eventRepository, dtoMapper, targetService);
	}

	@Test
	void when_events_exist_and_return_all_events_should_return_events() {

		List<EventNode> eventsListExpected = new ArrayList<>();

		Long eventId = 1L;

		String summary = "summary";
		String motive = "motive";
		Date date = Calendar.getInstance().getTime();
		Boolean isPartOfMultipleIncidents = true;
		Boolean isSuccessful = true;
		Boolean isSuicide = true;

		TargetNode target = new TargetNode(1L, "target");

		EventNode event1 = EventNode.builder().id(eventId++).date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(target).build();

		EventNode event2 = EventNode.builder().id(eventId++).date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(target).build();

		EventNode event3 = EventNode.builder().id(eventId++).date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(target).build();

		eventsListExpected.add(event1);
		eventsListExpected.add(event2);
		eventsListExpected.add(event3);

		Page<EventNode> eventsExpected = new PageImpl<>(eventsListExpected);

		Pageable pageable = PageRequest.of(0, 100);

		when(eventRepository.findAll(pageable)).thenReturn(eventsExpected);

		Page<EventNode> eventsActual = eventService.findAll(pageable);

		assertAll(() -> assertNotNull(eventsActual, () -> "shouldn`t return null"),
				() -> assertEquals(eventsListExpected, eventsActual.getContent(),
						() -> "should contain: " + eventsListExpected + ", but was: " + eventsActual.getContent()),
				() -> assertEquals(eventsExpected.getNumberOfElements(), eventsActual.getNumberOfElements(),
						() -> "should return page with: " + eventsExpected.getNumberOfElements()
								+ " elements, but was: " + eventsActual.getNumberOfElements()),
				() -> verify(eventRepository, times(1)).findAll(pageable),
				() -> verifyNoMoreInteractions(eventRepository), () -> verifyNoInteractions(dtoMapper),
				() -> verifyNoInteractions(targetService));
	}

	@Test
	void when_events_not_exist_and_return_all_events_should_not_return_any_events() {

		List<EventNode> eventsListExpected = new ArrayList<>();

		Page<EventNode> eventsExpected = new PageImpl<>(eventsListExpected);

		Pageable pageable = PageRequest.of(0, 100);

		when(eventRepository.findAll(pageable)).thenReturn(eventsExpected);

		Page<EventNode> eventsActual = eventService.findAll(pageable);

		assertAll(() -> assertNotNull(eventsActual, () -> "shouldn`t return null"),
				() -> assertEquals(eventsListExpected, eventsActual.getContent(),
						() -> "should contain empty list, but was: " + eventsActual.getContent()),
				() -> assertEquals(eventsListExpected, eventsActual.getContent(),
						() -> "should contain: " + eventsListExpected + ", but was: " + eventsActual.getContent()),
				() -> assertEquals(eventsExpected.getNumberOfElements(), eventsActual.getNumberOfElements(),
						() -> "should return empty page, but was: " + eventsActual.getNumberOfElements()),
				() -> verify(eventRepository, times(1)).findAll(pageable),
				() -> verifyNoMoreInteractions(eventRepository), () -> verifyNoInteractions(dtoMapper),
				() -> verifyNoInteractions(targetService));
	}

	@Test
	void when_event_exists_and_return_one_event_should_return_one_event() {

		Long expectedEventId = 1L;

		String summary = "summary";
		String motive = "motive";
		Date date = Calendar.getInstance().getTime();
		Boolean isPartOfMultipleIncidents = true;
		Boolean isSuccessful = true;
		Boolean isSuicide = true;

		TargetNode target = new TargetNode(1L, "target");

		EventNode eventExpected = EventNode.builder().id(expectedEventId).date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(target).build();

		when(eventRepository.findById(expectedEventId)).thenReturn(Optional.of(eventExpected));

		Optional<EventNode> eventActualOptional = eventService.findById(expectedEventId);

		EventNode eventActual = eventActualOptional.get();

		assertAll(
				() -> assertNotNull(eventActual.getId(),
						() -> "should return event node with new id, but was: " + eventActual.getId()),
				() -> assertEquals(eventExpected.getSummary(), eventActual.getSummary(),
						() -> "should return event node with summary: " + eventExpected.getSummary() + ", but was: "
								+ eventActual.getSummary()),
				() -> assertEquals(eventExpected.getMotive(), eventActual.getMotive(),
						() -> "should return event node with motive: " + eventExpected.getMotive() + ", but was: "
								+ eventActual.getMotive()),
				() -> assertEquals(eventExpected.getDate(), eventActual.getDate(),
						() -> "should return event node with date: " + eventExpected.getDate() + ", but was: "
								+ eventActual.getDate()),
				() -> assertEquals(eventExpected.getIsPartOfMultipleIncidents(),
						eventActual.getIsPartOfMultipleIncidents(),
						() -> "should return event node which was part of multiple incidents: "
								+ eventExpected.getIsPartOfMultipleIncidents() + ", but was: "
								+ eventActual.getIsPartOfMultipleIncidents()),
				() -> assertEquals(eventExpected.getIsSuccessful(), eventActual.getIsSuccessful(),
						() -> "should return event node which was successful: " + eventExpected.getIsSuccessful()
								+ ", but was: " + eventActual.getIsSuccessful()),
				() -> assertEquals(eventExpected.getIsSuicide(), eventActual.getIsSuicide(),
						() -> "should return event node which was suicide: " + eventExpected.getIsSuicide()
								+ ", but was: " + eventActual.getIsSuicide()),
				() -> assertNotNull(eventExpected.getTarget(),
						() -> "should return event node with not null target, but was: null"),
				() -> assertEquals(eventExpected.getTarget(), eventActual.getTarget(),
						() -> "should return event node with target: " + eventExpected.getTarget() + ", but was: "
								+ eventActual.getTarget()),
				() -> verify(eventRepository, times(1)).findById(expectedEventId),
				() -> verifyNoMoreInteractions(eventRepository), () -> verifyNoInteractions(dtoMapper),
				() -> verifyNoInteractions(targetService));
	}

	@Test
	void when_event_not_exists_and_return_one_event_should_return_empty_optional() {

		Long expectedEventId = 1L;

		when(eventRepository.findById(expectedEventId)).thenReturn(Optional.empty());

		Optional<EventNode> eventActualOptional = eventService.findById(expectedEventId);

		assertAll(() -> assertTrue(eventActualOptional.isEmpty(), () -> "should return empty optional"),
				() -> verify(eventRepository, times(1)).findById(expectedEventId),
				() -> verifyNoMoreInteractions(eventRepository), () -> verifyNoInteractions(dtoMapper),
				() -> verifyNoInteractions(targetService));
	}

	@Test
	void when_save_event_should_save_and_return_event() {

		Long eventId = 1L;

		String summary = "summary";
		String motive = "motive";
		Date date = Calendar.getInstance().getTime();
		Boolean isPartOfMultipleIncidents = true;
		Boolean isSuccessful = true;
		Boolean isSuicide = true;

		TargetNode target = new TargetNode(1L, "target");

		EventNode eventNodeExpectedBeforeSave = EventNode.builder().date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(target).build();

		EventNode eventNodeExpected = EventNode.builder().id(eventId).date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(target).build();

		when(eventRepository.save(eventNodeExpectedBeforeSave)).thenReturn(eventNodeExpected);

		EventNode eventNodeActual = eventService.save(eventNodeExpectedBeforeSave);

		assertAll(
				() -> assertNotNull(eventNodeActual.getId(),
						() -> "should return event node with new id, but was: " + eventNodeActual.getId()),
				() -> assertEquals(eventNodeExpected.getSummary(), eventNodeActual.getSummary(),
						() -> "should return event node with summary: " + eventNodeExpected.getSummary() + ", but was: "
								+ eventNodeActual.getSummary()),
				() -> assertEquals(eventNodeExpected.getMotive(), eventNodeActual.getMotive(),
						() -> "should return event node with motive: " + eventNodeExpected.getMotive() + ", but was: "
								+ eventNodeActual.getMotive()),
				() -> assertEquals(eventNodeExpected.getDate(), eventNodeActual.getDate(),
						() -> "should return event node with date: " + eventNodeExpected.getDate() + ", but was: "
								+ eventNodeActual.getDate()),
				() -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
						eventNodeActual.getIsPartOfMultipleIncidents(),
						() -> "should return event node which was part of multiple incidents: "
								+ eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was was: "
								+ eventNodeActual.getIsPartOfMultipleIncidents()),
				() -> assertEquals(eventNodeExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
						() -> "should return event node which was successful: " + eventNodeExpected.getIsSuccessful()
								+ ", but was: " + eventNodeActual.getIsSuccessful()),
				() -> assertEquals(eventNodeExpected.getIsSuicide(), eventNodeActual.getIsSuicide(),
						() -> "should return event node which was suicide: " + eventNodeExpected.getIsSuicide()
								+ ", but was: " + eventNodeActual.getIsSuicide()),
				() -> assertNotNull(eventNodeExpected.getTarget(),
						() -> "should return event node with not null target, but was: null"),
				() -> assertEquals(eventNodeExpected.getTarget(), eventNodeActual.getTarget(),
						() -> "should return event node with target: " + eventNodeExpected.getTarget() + ", but was: "
								+ eventNodeActual.getTarget()),
				() -> verify(eventRepository, times(1)).save(eventNodeExpectedBeforeSave),
				() -> verifyNoMoreInteractions(eventRepository), () -> verifyNoInteractions(dtoMapper),
				() -> verifyNoInteractions(targetService));
	}

	@Test
	void when_save_new_event_should_save_and_return_new_event() {

		Long eventId = 1L;

		String summary = "summary";
		String motive = "motive";
		Date date = Calendar.getInstance().getTime();
		Boolean isPartOfMultipleIncidents = true;
		Boolean isSuccessful = true;
		Boolean isSuicide = true;

		TargetNode targetNode = new TargetNode(1L, "target");
		TargetDTO targetDTO = new TargetDTO("target");

		EventDTO dTOExpected = EventDTO.builder().date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(targetDTO).build();

		EventNode eventNodeExpectedBeforeSave = EventNode.builder().date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(targetNode).build();

		EventNode eventNodeExpected = EventNode.builder().id(eventId).date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(targetNode).build();

		when(dtoMapper.mapToNode(dTOExpected, EventNode.class)).thenReturn(eventNodeExpectedBeforeSave);
		when(eventRepository.save(eventNodeExpectedBeforeSave)).thenReturn(eventNodeExpected);

		EventNode eventNodeActual = eventService.saveNew(dTOExpected);

		assertAll(
				() -> assertNotNull(eventNodeActual.getId(),
						() -> "should return event node with new id, but was: " + eventNodeActual.getId()),
				() -> assertEquals(eventNodeExpected.getSummary(), eventNodeActual.getSummary(),
						() -> "should return event node with summary: " + eventNodeExpected.getSummary() + ", but was: "
								+ eventNodeActual.getSummary()),
				() -> assertEquals(eventNodeExpected.getMotive(), eventNodeActual.getMotive(),
						() -> "should return event node with motive: " + eventNodeExpected.getMotive() + ", but was: "
								+ eventNodeActual.getMotive()),
				() -> assertEquals(eventNodeExpected.getDate(), eventNodeActual.getDate(),
						() -> "should return event node with date: " + eventNodeExpected.getDate() + ", but was: "
								+ eventNodeActual.getDate()),
				() -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
						eventNodeActual.getIsPartOfMultipleIncidents(),
						() -> "should return event node which was part of multiple incidents: "
								+ eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was was: "
								+ eventNodeActual.getIsPartOfMultipleIncidents()),
				() -> assertEquals(eventNodeExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
						() -> "should return event node which was successful: " + eventNodeExpected.getIsSuccessful()
								+ ", but was: " + eventNodeActual.getIsSuccessful()),
				() -> assertEquals(eventNodeExpected.getIsSuicide(), eventNodeActual.getIsSuicide(),
						() -> "should return event node which was suicide: " + eventNodeExpected.getIsSuicide()
								+ ", but was: " + eventNodeActual.getIsSuicide()),
				() -> assertNotNull(eventNodeExpected.getTarget(),
						() -> "should return event node with not null target, but was: null"),
				() -> assertEquals(eventNodeExpected.getTarget(), eventNodeActual.getTarget(),
						() -> "should return event node with target: " + eventNodeExpected.getTarget() + ", but was: "
								+ eventNodeActual.getTarget()),
				() -> verify(eventRepository, times(1)).save(eventNodeExpectedBeforeSave),
				() -> verifyNoMoreInteractions(eventRepository),
				() -> verify(dtoMapper, times(1)).mapToNode(dTOExpected, EventNode.class),
				() -> verifyNoMoreInteractions(dtoMapper), () -> verifyNoInteractions(targetService));
	}

	@Test
	void when_update_event_should_update_event_and_target() {

		Long eventId = 1L;

		String summary = "summary";
		String motive = "motive";
		Date date = Calendar.getInstance().getTime();
		Boolean isPartOfMultipleIncidents = true;
		Boolean isSuccessful = true;
		Boolean isSuicide = true;

		Long targetId = 1L;
		String targetName = "target";
		String targetNameUpdated = "target2";
		TargetNode targetNode = new TargetNode(targetId, targetName);
		TargetNode targetNodeUpdated = new TargetNode(targetId, targetNameUpdated);
		TargetDTO targetDTO = new TargetDTO(targetNameUpdated);

		EventDTO dTOExpected = EventDTO.builder().date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(targetDTO).build();

		EventNode eventNodeExpectedBeforeMethod = EventNode.builder().date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(targetNode).build();

		EventNode eventNodeExpectedBeforeSetIdAndTarget = EventNode.builder().date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(targetNode).build();

		EventNode eventNodeExpected = EventNode.builder().id(eventId).date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(targetNodeUpdated).build();

		when(targetService.update(targetId, targetDTO)).thenReturn(targetNodeUpdated);
		when(dtoMapper.mapToNode(dTOExpected, EventNode.class)).thenReturn(eventNodeExpectedBeforeSetIdAndTarget);
		when(eventRepository.save(eventNodeExpectedBeforeSetIdAndTarget)).thenReturn(eventNodeExpected);

		EventNode eventNodeActual = eventService.update(eventNodeExpectedBeforeMethod, dTOExpected);

		assertAll(
				() -> assertNotNull(eventNodeActual.getId(),
						() -> "should return event node with new id, but was: " + eventNodeActual.getId()),
				() -> assertEquals(eventNodeExpected.getSummary(), eventNodeActual.getSummary(),
						() -> "should return event node with summary: " + eventNodeExpected.getSummary() + ", but was: "
								+ eventNodeActual.getSummary()),
				() -> assertEquals(eventNodeExpected.getMotive(), eventNodeActual.getMotive(),
						() -> "should return event node with motive: " + eventNodeExpected.getMotive() + ", but was: "
								+ eventNodeActual.getMotive()),
				() -> assertEquals(eventNodeExpected.getDate(), eventNodeActual.getDate(),
						() -> "should return event node with date: " + eventNodeExpected.getDate() + ", but was: "
								+ eventNodeActual.getDate()),
				() -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
						eventNodeActual.getIsPartOfMultipleIncidents(),
						() -> "should return event node which was part of multiple incidents: "
								+ eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was was: "
								+ eventNodeActual.getIsPartOfMultipleIncidents()),
				() -> assertEquals(eventNodeExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
						() -> "should return event node which was successful: " + eventNodeExpected.getIsSuccessful()
								+ ", but was: " + eventNodeActual.getIsSuccessful()),
				() -> assertEquals(eventNodeExpected.getIsSuicide(), eventNodeActual.getIsSuicide(),
						() -> "should return event node which was suicide: " + eventNodeExpected.getIsSuicide()
								+ ", but was: " + eventNodeActual.getIsSuicide()),
				() -> assertNotNull(eventNodeExpected.getTarget(),
						() -> "should return event node with not null target, but was: null"),
				() -> assertEquals(eventNodeExpected.getTarget(), eventNodeActual.getTarget(),
						() -> "should return event node with target: " + eventNodeExpected.getTarget() + ", but was: "
								+ eventNodeActual.getTarget()),
				() -> verify(targetService, times(1)).update(targetId, targetDTO),
				() -> verifyNoMoreInteractions(targetService),
				() -> verify(eventRepository, times(1)).save(eventNodeExpectedBeforeSetIdAndTarget),
				() -> verifyNoMoreInteractions(eventRepository),
				() -> verify(dtoMapper, times(1)).mapToNode(dTOExpected, EventNode.class),
				() -> verifyNoMoreInteractions(dtoMapper));
	}

	@Test
	void when_delete_event_should_delete_eventand_target() {

		Long eventId = 1L;

		String summary = "summary";
		String motive = "motive";
		Date date = Calendar.getInstance().getTime();
		Boolean isPartOfMultipleIncidents = true;
		Boolean isSuccessful = true;
		Boolean isSuicide = true;

		Long targetId = 1L;
		TargetNode target = new TargetNode(targetId, "target");

		EventNode eventNodeExpected = EventNode.builder().id(eventId).date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(target).build();

		when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventNodeExpected));
		when(targetService.delete(targetId)).thenReturn(Optional.of(target));

		Optional<EventNode> eventNodeOptionalActual = eventService.delete(eventId);

		EventNode eventNodeActual = eventNodeOptionalActual.get();

		assertAll(
				() -> assertNotNull(eventNodeActual.getId(),
						() -> "should return event node with new id, but was: " + eventNodeActual.getId()),
				() -> assertEquals(eventNodeExpected.getSummary(), eventNodeActual.getSummary(),
						() -> "should return event node with summary: " + eventNodeExpected.getSummary() + ", but was: "
								+ eventNodeActual.getSummary()),
				() -> assertEquals(eventNodeExpected.getMotive(), eventNodeActual.getMotive(),
						() -> "should return event node with motive: " + eventNodeExpected.getMotive() + ", but was: "
								+ eventNodeActual.getMotive()),
				() -> assertEquals(eventNodeExpected.getDate(), eventNodeActual.getDate(),
						() -> "should return event node with date: " + eventNodeExpected.getDate() + ", but was: "
								+ eventNodeActual.getDate()),
				() -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
						eventNodeActual.getIsPartOfMultipleIncidents(),
						() -> "should return event node which was part of multiple incidents: "
								+ eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was was: "
								+ eventNodeActual.getIsPartOfMultipleIncidents()),
				() -> assertEquals(eventNodeExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
						() -> "should return event node which was successful: " + eventNodeExpected.getIsSuccessful()
								+ ", but was: " + eventNodeActual.getIsSuccessful()),
				() -> assertEquals(eventNodeExpected.getIsSuicide(), eventNodeActual.getIsSuicide(),
						() -> "should return event node which was suicide: " + eventNodeExpected.getIsSuicide()
								+ ", but was: " + eventNodeActual.getIsSuicide()),
				() -> assertNotNull(eventNodeExpected.getTarget(),
						() -> "should return event node with not null target, but was: null"),
				() -> assertEquals(eventNodeExpected.getTarget(), eventNodeActual.getTarget(),
						() -> "should return event node with target: " + eventNodeExpected.getTarget() + ", but was: "
								+ eventNodeActual.getTarget()),
				() -> verify(eventRepository, times(1)).findById(eventId),
				() -> verify(eventRepository, times(1)).delete(eventNodeExpected),
				() -> verifyNoMoreInteractions(eventRepository), () -> verify(targetService, times(1)).delete(targetId),
				() -> verifyNoMoreInteractions(targetService), () -> verifyNoInteractions(dtoMapper));
	}

	@Test
	void when_delete_event_by_id_not_existing_event_should_return_empty_optional() {

		Long eventId = 1L;

		when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

		Optional<EventNode> eventNodeOptional = eventService.delete(eventId);

		assertAll(
				() -> assertTrue(eventNodeOptional.isEmpty(),
						() -> "should return empty event node optional, but was: " + eventNodeOptional.get()),
				() -> verify(eventRepository, times(1)).findById(eventId),
				() -> verifyNoMoreInteractions(eventRepository), () -> verifyNoInteractions(dtoMapper),
				() -> verifyNoInteractions(targetService));
	}
}
