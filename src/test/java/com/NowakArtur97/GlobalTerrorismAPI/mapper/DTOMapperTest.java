package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("DTOMapper_Tests")
class DTOMapperTest {

	private DTOMapper dtoMapper;

	@Mock
	private ModelMapper modelMapper;

	private static TargetBuilder targetBuilder;

	@BeforeAll
	private static void init() {

		targetBuilder = new TargetBuilder();
	}

	@BeforeEach
	private void setUp() {

		dtoMapper = new DTOMapperImpl(modelMapper);
	}

	@Test
	void when_map_target_dto_to_node_should_return_target_node() {

		TargetDTO targetDTOExpected = (TargetDTO) targetBuilder.build(ObjectType.DTO);
		TargetNode targetNodeExpected = (TargetNode) targetBuilder.withId(null).build(ObjectType.NODE);

		when(modelMapper.map(targetDTOExpected, TargetNode.class)).thenReturn(targetNodeExpected);

		TargetNode targetNodeActual = dtoMapper.mapToNode(targetDTOExpected, TargetNode.class);

		assertAll(
				() -> assertNull(targetNodeActual.getId(),
						() -> "should return target node with id as null, but was: " + targetNodeActual.getId()),
				() -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
						() -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: "
								+ targetNodeActual.getTarget()),
				() -> verify(modelMapper, times(1)).map(targetDTOExpected, TargetNode.class));
	}

	@Test
	void when_map_target_node_to_dto_should_return_target_dto() {

		TargetNode targetNodeExpected = (TargetNode) targetBuilder.build(ObjectType.NODE);
		TargetDTO targetDTOExpected = (TargetDTO) targetBuilder.build(ObjectType.DTO);

		when(modelMapper.map(targetNodeExpected, TargetDTO.class)).thenReturn(targetDTOExpected);

		TargetDTO targetDTOActual = dtoMapper.mapToDTO(targetNodeExpected, TargetDTO.class);

		assertAll(
				() -> assertEquals(targetDTOExpected.getTarget(), targetDTOActual.getTarget(),
						() -> "should return target dto with target: " + targetDTOActual.getTarget() + ", but was: "
								+ targetDTOActual.getTarget()),
				() -> verify(modelMapper, times(1)).map(targetNodeExpected, TargetDTO.class));
	}

	@Test
	void when_map_event_dto_to_node_should_return_node() throws ParseException {

		String summary = "summary";
		String motive = "motive";
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2000-09-01");
		Boolean isPartOfMultipleIncidents = true;
		Boolean isSuccessful = true;
		Boolean isSuicide = true;

		TargetDTO targetDTO= (TargetDTO) targetBuilder.build(ObjectType.DTO);
		TargetNode targetNode = (TargetNode) targetBuilder.withId(null).build(ObjectType.NODE);

		EventDTO eventDTOExpected = EventDTO.builder().date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(targetDTO).build();

		EventNode eventNodeExpected = EventNode.builder().date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(targetNode).build();

		when(modelMapper.map(eventDTOExpected, EventNode.class)).thenReturn(eventNodeExpected);

		EventNode eventNodeActual = dtoMapper.mapToNode(eventDTOExpected, EventNode.class);

		assertAll(
				() -> assertNull(eventNodeActual.getId(),
						() -> "should return event node with id as null, but was: " + eventNodeActual.getId()),
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
								+ eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was: "
								+ eventNodeActual.getIsPartOfMultipleIncidents()),
				() -> assertEquals(eventNodeExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
						() -> "should return event node which was successful: " + eventNodeExpected.getIsSuccessful()
								+ ", but was: " + eventNodeActual.getIsSuccessful()),
				() -> assertEquals(eventNodeExpected.getIsSuicide(), eventNodeActual.getIsSuicide(),
						() -> "should return event node which was suicide: " + eventNodeExpected.getIsSuicide()
								+ ", but was: " + eventNodeActual.getIsSuicide()),
				() -> assertNotNull(eventNodeActual.getTarget(),
						() -> "should return event node with not null target, but was: null"),
				() -> assertNull(eventNodeActual.getTarget().getId(),
						() -> "should return events target node with id as null, but was: "
								+ eventNodeActual.getTarget().getId()),
				() -> assertEquals(eventNodeExpected.getTarget().getTarget(), eventNodeActual.getTarget().getTarget(),
						() -> "should return event node with target: " + eventNodeExpected.getTarget().getTarget()
								+ ", but was: " + eventNodeActual.getTarget().getTarget()),
				() -> verify(modelMapper, times(1)).map(eventDTOExpected, EventNode.class));
	}

	@Test
	void when_map_event_node_to_dto_should_return_dto() throws ParseException {

		String summary = "summary";
		String motive = "motive";
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2000-09-01");
		Boolean isPartOfMultipleIncidents = true;
		Boolean isSuccessful = true;
		Boolean isSuicide = true;

		TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
		TargetDTO targetDTO= (TargetDTO) targetBuilder.build(ObjectType.DTO);

		EventNode eventNodeExpected = EventNode.builder().date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(targetNode).build();

		EventDTO eventDTOExpected = EventDTO.builder().date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(targetDTO).build();

		when(modelMapper.map(eventNodeExpected, EventDTO.class)).thenReturn(eventDTOExpected);

		EventDTO eventDTOActual = dtoMapper.mapToDTO(eventNodeExpected, EventDTO.class);

		assertAll(
				() -> assertEquals(eventDTOExpected.getSummary(), eventDTOActual.getSummary(),
						() -> "should return event dto with summary: " + eventDTOExpected.getSummary() + ", but was: "
								+ eventDTOActual.getSummary()),
				() -> assertEquals(eventDTOExpected.getMotive(), eventDTOActual.getMotive(),
						() -> "should return event dto with motive: " + eventDTOExpected.getMotive() + ", but was: "
								+ eventDTOActual.getMotive()),
				() -> assertEquals(eventDTOExpected.getDate(), eventDTOActual.getDate(),
						() -> "should return event dto with date: " + eventDTOExpected.getDate() + ", but was: "
								+ eventDTOActual.getDate()),
				() -> assertEquals(eventDTOExpected.getIsPartOfMultipleIncidents(),
						eventDTOActual.getIsPartOfMultipleIncidents(),
						() -> "should return event dto which was part of multiple incidents: "
								+ eventDTOExpected.getIsPartOfMultipleIncidents() + ", but was: "
								+ eventDTOActual.getIsPartOfMultipleIncidents()),
				() -> assertEquals(eventDTOExpected.getIsSuccessful(), eventDTOActual.getIsSuccessful(),
						() -> "should return event dto which was successful: " + eventDTOExpected.getIsSuccessful()
								+ ", but was: " + eventDTOActual.getIsSuccessful()),
				() -> assertEquals(eventDTOExpected.getIsSuicide(), eventDTOActual.getIsSuicide(),
						() -> "should return event dto which was suicide: " + eventDTOExpected.getIsSuicide()
								+ ", but was: " + eventDTOActual.getIsSuicide()),
				() -> assertNotNull(eventDTOActual.getTarget(),
						() -> "should return event dto with not null target, but was: null"),
				() -> assertEquals(eventDTOExpected.getTarget().getTarget(), eventDTOActual.getTarget().getTarget(),
						() -> "should return event dto with target: " + eventDTOExpected.getTarget().getTarget()
								+ ", but was: " + eventDTOActual.getTarget().getTarget()),
				() -> verify(modelMapper, times(1)).map(eventNodeExpected, EventDTO.class));
	}
}