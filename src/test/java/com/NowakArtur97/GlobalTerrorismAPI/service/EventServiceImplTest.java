package com.NowakArtur97.GlobalTerrorismAPI.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.EventRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.EventService;
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

	@BeforeEach
	void setUp() {

		eventService = new EventServiceImpl(eventRepository);
	}

	@Test
	public void when_save_new_event_should_save_and_return_event() {

		Long eventId = 1L;

		String eventSummary = "summary";
		String eventMotive = "motive";
		Date eventDate = Calendar.getInstance().getTime();
		boolean isEventPartOfMultipleIncidents = true;
		boolean isEventSuccessful = true;
		boolean isEventSuicide = true;

		TargetNode target = new TargetNode(1L, "target");

		EventNode eventNodeExpectedBeforeSave = EventNode.builder().date(eventDate).summary(eventSummary)
				.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents).isSuccessful(isEventSuccessful)
				.isSuicide(isEventSuicide).motive(eventMotive).target(target).build();

		EventNode eventNodeExpected = EventNode.builder().id(eventId).date(eventDate).summary(eventSummary)
				.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents).isSuccessful(isEventSuccessful)
				.isSuicide(isEventSuicide).motive(eventMotive).target(target).build();

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
				() -> assertEquals(eventNodeExpected.isPartOfMultipleIncidents(),
						eventNodeActual.isPartOfMultipleIncidents(),
						() -> "should return event node which was part of multiple incidents: "
								+ eventNodeExpected.isPartOfMultipleIncidents() + ", but that was: "
								+ eventNodeActual.isPartOfMultipleIncidents()),
				() -> assertEquals(eventNodeExpected.isSuccessful(), eventNodeActual.isSuccessful(),
						() -> "should return event node which was successful: " + eventNodeExpected.isSuccessful()
								+ ", but that was: " + eventNodeActual.isSuccessful()),
				() -> assertEquals(eventNodeExpected.isSuicide(), eventNodeActual.isSuicide(),
						() -> "should return event node which was suicide: " + eventNodeExpected.isSuicide()
								+ ", but that was: " + eventNodeActual.isSuicide()),
				() -> assertNotNull(eventNodeExpected.getTarget(),
						() -> "should return event node with not null target, but was: null"),
				() -> assertEquals(eventNodeExpected.getTarget(), eventNodeActual.getTarget(),
						() -> "should return event node with target: " + eventNodeExpected.getTarget() + ", but was: "
								+ eventNodeActual.getTarget()),
				() -> verify(eventRepository, times(1)).save(eventNodeExpectedBeforeSave),
				() -> verifyNoMoreInteractions(eventRepository));
	}
}
