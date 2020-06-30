package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.exception.ResourceNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.EventRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.EventService;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventServiceImpl_Tests")
class EventServiceImplTest {

    private static final int DEFAULT_SEARCHING_DEPTH = 1;

    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TargetService targetService;

    private TargetBuilder targetBuilder;
    private EventBuilder eventBuilder;

    @BeforeEach
    private void setUp() {

        eventService = new EventServiceImpl(eventRepository, objectMapper, targetService);

        targetBuilder = new TargetBuilder();
        eventBuilder = new EventBuilder();
    }

    @Test
    void when_events_exist_and_return_all_events_should_return_events() {

        List<EventNode> eventsListExpected = createEventNodeList(3);

        Page<EventNode> eventsExpected = new PageImpl<>(eventsListExpected);

        Pageable pageable = PageRequest.of(0, 100);

        when(eventRepository.findAll(pageable, DEFAULT_SEARCHING_DEPTH)).thenReturn(eventsExpected);

        Page<EventNode> eventsActual = eventService.findAll(pageable);

        assertAll(() -> assertNotNull(eventsActual, () -> "shouldn`t return null"),
                () -> assertEquals(eventsListExpected, eventsActual.getContent(),
                        () -> "should contain: " + eventsListExpected + ", but was: " + eventsActual.getContent()),
                () -> assertEquals(eventsExpected.getNumberOfElements(), eventsActual.getNumberOfElements(),
                        () -> "should return page with: " + eventsExpected.getNumberOfElements()
                                + " elements, but was: " + eventsActual.getNumberOfElements()),
                () -> verify(eventRepository, times(1)).findAll(pageable, DEFAULT_SEARCHING_DEPTH),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(targetService));
    }

    @Test
    void when_events_not_exist_and_return_all_events_should_not_return_any_events() {

        List<EventNode> eventsListExpected = new ArrayList<>();

        Page<EventNode> eventsExpected = new PageImpl<>(eventsListExpected);

        Pageable pageable = PageRequest.of(0, 100);

        when(eventRepository.findAll(pageable, DEFAULT_SEARCHING_DEPTH)).thenReturn(eventsExpected);

        Page<EventNode> eventsActual = eventService.findAll(pageable);

        assertAll(() -> assertNotNull(eventsActual, () -> "shouldn`t return null"),
                () -> assertEquals(eventsListExpected, eventsActual.getContent(),
                        () -> "should contain empty list, but was: " + eventsActual.getContent()),
                () -> assertEquals(eventsListExpected, eventsActual.getContent(),
                        () -> "should contain: " + eventsListExpected + ", but was: " + eventsActual.getContent()),
                () -> assertEquals(eventsExpected.getNumberOfElements(), eventsActual.getNumberOfElements(),
                        () -> "should return empty page, but was: " + eventsActual.getNumberOfElements()),
                () -> verify(eventRepository, times(1)).findAll(pageable, DEFAULT_SEARCHING_DEPTH),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(targetService));
    }

    @Test
    void when_event_exists_and_return_one_event_should_return_one_event() {

        Long expectedEventId = 1L;
        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventExpected = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

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
                () -> assertEquals(eventExpected.getIsSuicidal(), eventActual.getIsSuicidal(),
                        () -> "should return event node which was suicidal: " + eventExpected.getIsSuicidal()
                                + ", but was: " + eventActual.getIsSuicidal()),
                () -> assertNotNull(eventExpected.getTarget(),
                        () -> "should return event node with not null target, but was: null"),
                () -> assertEquals(eventExpected.getTarget(), eventActual.getTarget(),
                        () -> "should return event node with target: " + eventExpected.getTarget() + ", but was: "
                                + eventActual.getTarget()),
                () -> verify(eventRepository, times(1)).findById(expectedEventId),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(targetService));
    }

    @Test
    void when_event_not_exists_and_return_one_event_should_return_empty_optional() {

        Long expectedEventId = 1L;

        when(eventRepository.findById(expectedEventId)).thenReturn(Optional.empty());

        Optional<EventNode> eventActualOptional = eventService.findById(expectedEventId);

        assertAll(() -> assertTrue(eventActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(eventRepository, times(1)).findById(expectedEventId),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(targetService));
    }

    @Test
    void when_save_event_should_save_and_return_event() {

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNodeExpectedBeforeSave = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

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
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), eventNodeActual.getIsSuicidal(),
                        () -> "should return event node which was suicidal: " + eventNodeExpected.getIsSuicidal()
                                + ", but was: " + eventNodeActual.getIsSuicidal()),
                () -> assertNotNull(eventNodeExpected.getTarget(),
                        () -> "should return event node with not null target, but was: null"),
                () -> assertEquals(eventNodeExpected.getTarget(), eventNodeActual.getTarget(),
                        () -> "should return event node with target: " + eventNodeExpected.getTarget() + ", but was: "
                                + eventNodeActual.getTarget()),
                () -> verify(eventRepository, times(1)).save(eventNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(targetService));
    }

    @Test
    void when_save_new_event_should_save_and_return_new_event() {

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        EventDTO eventDTOExpected = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
        EventNode eventNodeExpectedBeforeSave = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

        when(objectMapper.map(eventDTOExpected, EventNode.class)).thenReturn(eventNodeExpectedBeforeSave);
        when(eventRepository.save(eventNodeExpectedBeforeSave)).thenReturn(eventNodeExpected);

        EventNode eventNodeActual = eventService.saveNew(eventDTOExpected);

        assertAll(
                () -> assertNotNull(eventNodeActual.getId(),
                        () -> "should return event node with id, but was: " + eventNodeActual.getId()),
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
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), eventNodeActual.getIsSuicidal(),
                        () -> "should return event node which was suicidal: " + eventNodeExpected.getIsSuicidal()
                                + ", but was: " + eventNodeActual.getIsSuicidal()),
                () -> assertNotNull(eventNodeExpected.getTarget(),
                        () -> "should return event node with not null target, but was: null"),
                () -> assertEquals(eventNodeExpected.getTarget(), eventNodeActual.getTarget(),
                        () -> "should return event node with target: " + eventNodeExpected.getTarget() + ", but was: "
                                + eventNodeActual.getTarget()),
                () -> verify(eventRepository, times(1)).save(eventNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verify(objectMapper, times(1)).map(eventDTOExpected, EventNode.class),
                () -> verifyNoMoreInteractions(objectMapper),
                () -> verifyNoInteractions(targetService));
    }

    @Test
    void when_update_event_should_update_event_and_target() throws ParseException {

        String updatedTargetName = "target2";
        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        TargetNode updatedTargetNode = (TargetNode) targetBuilder.withTarget(updatedTargetName).build(ObjectType.NODE);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget(updatedTargetName).build(ObjectType.DTO);

        String updatedSummary = "summary updated";
        String updatedMotive = "motive updated";
        Date updatedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("01/08/2010 02:00:00:000");
        boolean updatedIsPartOfMultipleIncidents = false;
        boolean updatedIsSuccessful = false;
        boolean updatedIsSuicidal = false;
        EventDTO eventDTOExpected = (EventDTO) eventBuilder.withDate(updatedDate).withSummary(updatedSummary)
                .withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents).withIsSuccessful(updatedIsSuccessful)
                .withIsSuicidal(updatedIsSuicidal).withMotive(updatedMotive).withTarget(targetDTO)
                .build(ObjectType.DTO);
        EventNode eventNodeExpectedBeforeMethod = (EventNode) eventBuilder.withTarget(targetNode)
                .build(ObjectType.NODE);
        EventNode eventNodeExpectedBeforeSetIdAndTarget = (EventNode) eventBuilder.withDate(updatedDate).withSummary(updatedSummary)
                .withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents).withIsSuccessful(updatedIsSuccessful)
                .withIsSuicidal(updatedIsSuicidal).withMotive(updatedMotive).withTarget(targetNode)
                .build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withDate(updatedDate).withSummary(updatedSummary)
                .withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents).withIsSuccessful(updatedIsSuccessful)
                .withIsSuicidal(updatedIsSuicidal).withMotive(updatedMotive).withTarget(updatedTargetNode).build(ObjectType.NODE);

        when(targetService.update(targetNode, targetDTO)).thenReturn(updatedTargetNode);
        when(objectMapper.map(eventDTOExpected, EventNode.class)).thenReturn(eventNodeExpectedBeforeSetIdAndTarget);
        when(eventRepository.save(eventNodeExpectedBeforeSetIdAndTarget)).thenReturn(eventNodeExpected);

        EventNode eventNodeActual = eventService.update(eventNodeExpectedBeforeMethod, eventDTOExpected);

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
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), eventNodeActual.getIsSuicidal(),
                        () -> "should return event node which was suicidal: " + eventNodeExpected.getIsSuicidal()
                                + ", but was: " + eventNodeActual.getIsSuicidal()),
                () -> assertNotNull(eventNodeExpected.getTarget(),
                        () -> "should return event node with not null target, but was: null"),
                () -> assertEquals(eventNodeExpected.getTarget(), eventNodeActual.getTarget(),
                        () -> "should return event node with target: " + eventNodeExpected.getTarget() + ", but was: "
                                + eventNodeActual.getTarget()),
                () -> verify(targetService, times(1)).update(targetNode, targetDTO),
                () -> verifyNoMoreInteractions(targetService),
                () -> verify(eventRepository, times(1)).save(eventNodeExpectedBeforeSetIdAndTarget),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verify(objectMapper, times(1)).map(eventDTOExpected, EventNode.class),
                () -> verifyNoMoreInteractions(objectMapper));
    }

    @Test
    void when_delete_event_without_target_should_delete_event() {

        Long eventId = 1L;

        EventNode eventNodeExpected = (EventNode) eventBuilder.build(ObjectType.NODE);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventNodeExpected));

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
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), eventNodeActual.getIsSuicidal(),
                        () -> "should return event node which was suicidal: " + eventNodeExpected.getIsSuicidal()
                                + ", but was: " + eventNodeActual.getIsSuicidal()),
                () -> assertNull(eventNodeExpected.getTarget(),
                        () -> "should return event node with null target, but was: " + eventNodeExpected.getTarget()),
                () -> verify(eventRepository, times(1)).findById(eventId),
                () -> verify(eventRepository, times(1)).delete(eventNodeExpected),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verifyNoInteractions(targetService),
                () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_delete_event_should_delete_event_and_target() {

        Long eventId = 1L;
        Long targetId = 1L;

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventNodeExpected));
        when(targetService.delete(targetId)).thenReturn(Optional.of(targetNode));

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
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), eventNodeActual.getIsSuicidal(),
                        () -> "should return event node which was suicidal: " + eventNodeExpected.getIsSuicidal()
                                + ", but was: " + eventNodeActual.getIsSuicidal()),
                () -> assertNotNull(eventNodeExpected.getTarget(),
                        () -> "should return event node with not null target, but was: null"),
                () -> assertEquals(eventNodeExpected.getTarget(), eventNodeActual.getTarget(),
                        () -> "should return event node with target: " + eventNodeExpected.getTarget() + ", but was: "
                                + eventNodeActual.getTarget()),
                () -> verify(eventRepository, times(1)).findById(eventId),
                () -> verify(eventRepository, times(1)).delete(eventNodeExpected),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verify(targetService, times(1)).delete(targetId),
                () -> verifyNoMoreInteractions(targetService),
                () -> verifyNoInteractions(objectMapper));
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
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(targetService));
    }

    @Test
    void when_delete_event_target_should_delete_target() {

        Long eventId = 1L;
        Long targetId = 2L;

        TargetNode targetNode = (TargetNode) targetBuilder.withId(targetId).build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withId(eventId).withTarget(targetNode).build(ObjectType.NODE);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventNodeExpected));
        when(targetService.delete(targetId)).thenReturn(Optional.of(targetNode));

        Optional<EventNode> eventNodeOptionalActual = eventService.deleteEventTarget(eventId);

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
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), eventNodeActual.getIsSuicidal(),
                        () -> "should return event node which was suicidal: " + eventNodeExpected.getIsSuicidal()
                                + ", but was: " + eventNodeActual.getIsSuicidal()),
                () -> assertNotNull(eventNodeExpected.getTarget(),
                        () -> "should return event node with not null target, but was: null"),
                () -> assertEquals(eventNodeExpected.getTarget(), eventNodeActual.getTarget(),
                        () -> "should return event node with target: " + eventNodeExpected.getTarget() + ", but was: "
                                + eventNodeActual.getTarget()),
                () -> verify(eventRepository, times(1)).findById(eventId),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verify(targetService, times(1)).delete(targetId),
                () -> verifyNoMoreInteractions(targetService),
                () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_delete_event_target_but_event_does_not_have_target_should_throw_exception() {

        Long eventId = 1L;

        EventNode eventNodeExpected = (EventNode) eventBuilder.withId(eventId).build(ObjectType.NODE);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventNodeExpected));

        assertAll(
                () -> assertThrows(ResourceNotFoundException.class, () -> eventService.deleteEventTarget(eventId), "should throw ResourceNotFoundException but wasn't"),
                () -> verify(eventRepository, times(1)).findById(eventId),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(targetService));
    }

    @Test
    void when_delete_event_target_but_event_does_not_exist_should_return_empty_optional() {

        Long eventId = 1L;

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        Optional<EventNode> eventNodeOptional = eventService.deleteEventTarget(eventId);

        assertAll(
                () -> assertTrue(eventNodeOptional.isEmpty(),
                        () -> "should return empty event node optional, but was: " + eventNodeOptional.get()),
                () -> verify(eventRepository, times(1)).findById(eventId),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(targetService));
    }

    private List<EventNode> createEventNodeList(int listSize) {

        EventBuilder eventBuilder = new EventBuilder();

        List<EventNode> eventsListExpected = new ArrayList<>();

        int count = 0;

        while (count < listSize) {

            TargetNode targetNode = new TargetNode((long) count, "target" + count);

            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

            eventsListExpected.add(eventNode);

            count++;
        }

        return eventsListExpected;
    }
}
