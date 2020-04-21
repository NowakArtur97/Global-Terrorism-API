package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import org.springframework.hateoas.Link;

import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.ibm.icu.util.Calendar;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventModelAssembler_Tests")
public class EventModelAssemblerTest {

	private final String BASE_PATH = "http://localhost:8080/api/targets";

	private EventModelAssembler eventModelAssembler;

	@Mock
	private TargetModelAssembler targetModelAssembler;

	@BeforeEach
	public void setUp() {

		eventModelAssembler = new EventModelAssembler(targetModelAssembler);
	}

	@Test
	public void when_map_event_node_to_model_should_return_event_model() {

		Long eventId = 1L;

		String eventSummary = "summary";
		String eventMotive = "motive";
		Date eventDate = Calendar.getInstance().getTime();
		boolean isEventPartOfMultipleIncidents = true;
		boolean isEventSuccessful = true;
		boolean isEventSuicide = true;

		Long targetId1 = 1L;
		String targetName1 = "target1";
		TargetNode targetNode = new TargetNode(targetId1, targetName1);
		TargetModel targetModel = new TargetModel(targetId1, targetName1);

		String pathToLink = BASE_PATH + targetId1.intValue();
		Link link = new Link(pathToLink);
		targetModel.add(link);

		EventNode eventNode = EventNode.builder().id(eventId).date(eventDate).summary(eventSummary)
				.isPartOfMultipleIncidents(isEventPartOfMultipleIncidents).isSuccessful(isEventSuccessful)
				.isSuicide(isEventSuicide).motive(eventMotive).target(targetNode).build();

		when(targetModelAssembler.toModel(eventNode.getTarget())).thenReturn(targetModel);

		EventModel eventModel = eventModelAssembler.toModel(eventNode);

		assertAll(
				() -> assertNotNull(eventModel.getId(),
						() -> "should return event node with new id, but was: " + eventModel.getId()),
				() -> assertEquals(eventNode.getSummary(), eventModel.getSummary(),
						() -> "should return event node with summary: " + eventNode.getSummary() + ", but was: "
								+ eventModel.getSummary()),
				() -> assertEquals(eventNode.getMotive(), eventModel.getMotive(),
						() -> "should return event node with motive: " + eventNode.getMotive() + ", but was: "
								+ eventModel.getMotive()),
				() -> assertEquals(eventNode.getDate(), eventModel.getDate(),
						() -> "should return event node with date: " + eventNode.getDate() + ", but was: "
								+ eventModel.getDate()),
				() -> assertEquals(eventNode.isPartOfMultipleIncidents(), eventModel.isPartOfMultipleIncidents(),
						() -> "should return event node which was part of multiple incidents: "
								+ eventNode.isPartOfMultipleIncidents() + ", but that was: "
								+ eventModel.isPartOfMultipleIncidents()),
				() -> assertEquals(eventNode.isSuccessful(), eventModel.isSuccessful(),
						() -> "should return event node which was successful: " + eventNode.isSuccessful()
								+ ", but that was: " + eventModel.isSuccessful()),
				() -> assertEquals(eventNode.isSuicide(), eventModel.isSuicide(),
						() -> "should return event node which was suicide: " + eventNode.isSuicide()
								+ ", but that was: " + eventModel.isSuicide()),
				() -> assertNotNull(eventNode.getTarget(),
						() -> "should return event node with not null target, but was: null"),
				() -> assertEquals(targetModel, eventModel.getTarget(),
						() -> "should return event node with target model: " + targetModel + ", but was: "
								+ eventModel.getTarget()),
				() -> assertNotNull(eventModel.getLinks(),
						() -> "should return model with links, but was: " + eventModel),
				() -> assertFalse(eventModel.getLinks().isEmpty(),
						() -> "should return model with links, but was: " + eventModel),
				() -> verify(targetModelAssembler, times(1)).toModel(eventNode.getTarget()),
				() -> verifyNoMoreInteractions(targetModelAssembler));
	}
}
