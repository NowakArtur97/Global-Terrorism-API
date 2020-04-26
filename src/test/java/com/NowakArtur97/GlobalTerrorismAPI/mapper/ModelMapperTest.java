package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;

@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("ModelMapper_Tests")
class ModelMapperTest {

	private ModelMapper modelMapper;

	@BeforeEach
	private void setUp() {

		modelMapper = new ModelMapper();
	}

	@Test
	void when_map_target_dto_to_node_should_return_valid_node() {

		String targetName = "Target";

		TargetDTO targetDTOExpected = new TargetDTO(targetName);

		TargetNode targetNodeActual = modelMapper.map(targetDTOExpected, TargetNode.class);

		assertAll(
				() -> assertNull(targetNodeActual.getId(),
						() -> "should return target node with id as null, but was: " + targetNodeActual.getId()),
				() -> assertEquals(targetDTOExpected.getTarget(), targetNodeActual.getTarget(),
						() -> "should return target node with target: " + targetDTOExpected.getTarget() + ", but was: "
								+ targetNodeActual.getTarget()));
	}

	@Test
	void when_map_event_dto_to_node_should_return_valid_node() throws ParseException {

		String summary = "summary";
		String motive = "motive";
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2000-09-01");
		boolean isPartOfMultipleIncidents = true;
		boolean isSuccessful = true;
		boolean isSuicide = true;

		Long targetId = 1L;
		String target = "target";
		TargetDTO targetDTO = new TargetDTO(target);
		TargetNode targetNode = new TargetNode(targetId, target);

		EventDTO eventDTO = EventDTO.builder().date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(targetDTO).build();

		EventNode eventNodeExpected = EventNode.builder().date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful).isSuicide(isSuicide)
				.motive(motive).target(targetNode).build();

		EventNode eventNodeActual = modelMapper.map(eventDTO, EventNode.class);

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
				() -> assertEquals(eventNodeExpected.isPartOfMultipleIncidents(),
						eventNodeActual.isPartOfMultipleIncidents(),
						() -> "should return event node which was part of multiple incidents: "
								+ eventNodeExpected.isPartOfMultipleIncidents() + ", but was: "
								+ eventNodeActual.isPartOfMultipleIncidents()),
				() -> assertEquals(eventNodeExpected.isSuccessful(), eventNodeActual.isSuccessful(),
						() -> "should return event node which was successful: " + eventNodeExpected.isSuccessful()
								+ ", but was: " + eventNodeActual.isSuccessful()),
				() -> assertEquals(eventNodeExpected.isSuicide(), eventNodeActual.isSuicide(),
						() -> "should return event node which was suicide: " + eventNodeExpected.isSuicide()
								+ ", but was: " + eventNodeActual.isSuicide()),
				() -> assertNotNull(eventNodeExpected.getTarget(),
						() -> "should return event node with not null target, but was: null"),
				() -> assertNull(eventNodeActual.getTarget().getId(),
						() -> "should return events target node with id as null, but was: "
								+ eventNodeActual.getTarget().getId()),
				() -> assertEquals(eventNodeExpected.getTarget().getTarget(), eventNodeActual.getTarget().getTarget(),
						() -> "should return event node with target: " + eventNodeExpected.getTarget().getTarget()
								+ ", but was: " + eventNodeActual.getTarget().getTarget()));
	}
}
