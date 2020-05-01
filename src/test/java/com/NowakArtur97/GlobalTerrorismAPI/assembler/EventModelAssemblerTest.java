package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
class EventModelAssemblerTest {

	private final String BASE_PATH = "http://localhost:8080/api/targets";

	private EventModelAssembler modelAssembler;

	@Mock
	private TargetModelAssembler targetModelAssembler;

	@BeforeEach
	private void setUp() {

		modelAssembler = new EventModelAssembler(targetModelAssembler);
	}

	@Test
	void when_map_event_node_to_model_should_return_event_model() {

		Long eventId = 1L;

		String summary = "summary";
		String motive = "motive";
		Date date = Calendar.getInstance().getTime();
		boolean isPartOfMultipleIncidents = true;
		boolean isSuccessful = true;
		boolean isSuicide = true;

		Long targetId1 = 1L;
		String targetName1 = "target1";
		TargetNode targetNode = new TargetNode(targetId1, targetName1);
		TargetModel targetModel = new TargetModel(targetId1, targetName1);

		String pathToLink = BASE_PATH + targetId1.intValue();
		Link link = new Link(pathToLink);
		targetModel.add(link);

		EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
				.isSuicide(isSuicide).motive(motive).target(targetNode).build();

		when(targetModelAssembler.toModel(eventNode.getTarget())).thenReturn(targetModel);

		EventModel model = modelAssembler.toModel(eventNode);

		assertAll(
				() -> assertNotNull(model.getId(),
						() -> "should return event node with new id, but was: " + model.getId()),
				() -> assertEquals(eventNode.getSummary(), model.getSummary(),
						() -> "should return event node with summary: " + eventNode.getSummary() + ", but was: "
								+ model.getSummary()),
				() -> assertEquals(eventNode.getMotive(), model.getMotive(),
						() -> "should return event node with motive: " + eventNode.getMotive() + ", but was: "
								+ model.getMotive()),
				() -> assertEquals(eventNode.getDate(), model.getDate(),
						() -> "should return event node with date: " + eventNode.getDate() + ", but was: "
								+ model.getDate()),
				() -> assertEquals(eventNode.isPartOfMultipleIncidents(), model.isPartOfMultipleIncidents(),
						() -> "should return event node which was part of multiple incidents: "
								+ eventNode.isPartOfMultipleIncidents() + ", but was: "
								+ model.isPartOfMultipleIncidents()),
				() -> assertEquals(eventNode.isSuccessful(), model.isSuccessful(),
						() -> "should return event node which was successful: " + eventNode.isSuccessful()
								+ ", but was: " + model.isSuccessful()),
				() -> assertEquals(eventNode.isSuicide(), model.isSuicide(),
						() -> "should return event node which was suicide: " + eventNode.isSuicide()
								+ ", but was: " + model.isSuicide()),
				() -> assertNotNull(eventNode.getTarget(),
						() -> "should return event node with not null target, but was: null"),
				() -> assertEquals(targetModel, model.getTarget(),
						() -> "should return event node with target model: " + targetModel + ", but was: "
								+ model.getTarget()),
				() -> assertNotNull(model.getLinks(),
						() -> "should return model with links, but was: " + model),
				() -> assertFalse(model.getLinks().isEmpty(),
						() -> "should return model with links, but was: " + model),
				() -> verify(targetModelAssembler, times(1)).toModel(eventNode.getTarget()),
				() -> verifyNoMoreInteractions(targetModelAssembler));
	}
	
	@Test
	void when_map_event_node_to_model_without_target_should_return_event_model_without_target() {

		Long eventId = 1L;

		String summary = "summary";
		String motive = "motive";
		Date date = Calendar.getInstance().getTime();
		boolean isPartOfMultipleIncidents = true;
		boolean isSuccessful = true;
		boolean isSuicide = true;

		EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
				.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
				.isSuicide(isSuicide).motive(motive).build();

		EventModel model = modelAssembler.toModel(eventNode);

		assertAll(
				() -> assertNotNull(model.getId(),
						() -> "should return event node with new id, but was: " + model.getId()),
				() -> assertEquals(eventNode.getSummary(), model.getSummary(),
						() -> "should return event node with summary: " + eventNode.getSummary() + ", but was: "
								+ model.getSummary()),
				() -> assertEquals(eventNode.getMotive(), model.getMotive(),
						() -> "should return event node with motive: " + eventNode.getMotive() + ", but was: "
								+ model.getMotive()),
				() -> assertEquals(eventNode.getDate(), model.getDate(),
						() -> "should return event node with date: " + eventNode.getDate() + ", but was: "
								+ model.getDate()),
				() -> assertEquals(eventNode.isPartOfMultipleIncidents(), model.isPartOfMultipleIncidents(),
						() -> "should return event node which was part of multiple incidents: "
								+ eventNode.isPartOfMultipleIncidents() + ", but was: "
								+ model.isPartOfMultipleIncidents()),
				() -> assertEquals(eventNode.isSuccessful(), model.isSuccessful(),
						() -> "should return event node which was successful: " + eventNode.isSuccessful()
								+ ", but was: " + model.isSuccessful()),
				() -> assertEquals(eventNode.isSuicide(), model.isSuicide(),
						() -> "should return event node which was suicide: " + eventNode.isSuicide()
								+ ", but was: " + model.isSuicide()),
				() -> assertNull(eventNode.getTarget(),
						() -> "should return event node with null target, but wasn't: null"),
				() -> assertNotNull(model.getLinks(),
						() -> "should return model with links, but was: " + model),
				() -> assertFalse(model.getLinks().isEmpty(),
						() -> "should return model with links, but was: " + model),
				() -> verifyNoInteractions(targetModelAssembler));
	}
}
