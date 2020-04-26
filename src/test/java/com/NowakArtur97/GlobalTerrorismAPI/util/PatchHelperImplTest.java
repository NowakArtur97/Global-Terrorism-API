package com.NowakArtur97.GlobalTerrorismAPI.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonMergePatch;
import javax.json.JsonPatch;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("PatchHelperImpl_Tests")
class PatchHelperImplTest {

	private PatchHelper patchHelper;

	@Mock
	private ObjectMapper objectMapper;

	@BeforeEach
	private void setUp() {

		patchHelper = new PatchHelperImpl(objectMapper);
	}

	@Nested
	@Tag("TargetPatch_Tests")
	class TargetPatchTest {

		@Test
		void when_patch_target_node_should_return_patched_target_node() {

			Long targetId = 1L;
			String oldTargetName = "target";
			String updatedTargetName = "updated target";
			TargetNode targetNode = new TargetNode(targetId, oldTargetName);
			TargetNode targetNodeExpected = new TargetNode(targetId, updatedTargetName);

			JsonPatch targetAsJsonPatch = Json.createPatchBuilder().replace("/target", updatedTargetName).build();

			JsonStructure target = Json.createObjectBuilder().add("target", updatedTargetName).build();

			JsonValue patched = targetAsJsonPatch.apply(target);

			when(objectMapper.convertValue(targetNode, JsonStructure.class)).thenReturn(target);
			when(objectMapper.convertValue(patched, TargetNode.class)).thenReturn(targetNodeExpected);

			TargetNode targetNodeActual = patchHelper.patch(targetAsJsonPatch, targetNode, TargetNode.class);

			assertAll(
					() -> assertEquals(targetId, targetNodeActual.getId(),
							() -> "should return target node with id: " + targetId + ", but was: "
									+ targetNodeActual.getId()),
					() -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
							() -> "should return target node with target: " + targetNodeExpected.getTarget()
									+ ", but was: " + targetNodeActual.getTarget()),
					() -> verify(objectMapper, times(1)).convertValue(targetNode, JsonStructure.class),
					() -> verify(objectMapper, times(1)).convertValue(patched, TargetNode.class),
					() -> verifyNoMoreInteractions(objectMapper));
		}

		@Test
		void when_merge_patch_target_node_should_return_patched_target_node() {

			Long targetId = 1L;
			String targetName = "target";
			String updatedTargetName = "updated target";
			TargetNode targetNode = new TargetNode(targetId, targetName);
			TargetNode targetNodeExpected = new TargetNode(targetId, updatedTargetName);

			JsonMergePatch targetAsJsonMergePatch = Json
					.createMergePatch(Json.createObjectBuilder().add("target", updatedTargetName).build());

			JsonValue target = Json.createObjectBuilder().add("target", updatedTargetName).build();

			JsonValue patched = targetAsJsonMergePatch.apply(target);

			when(objectMapper.convertValue(targetNode, JsonValue.class)).thenReturn(target);
			when(objectMapper.convertValue(patched, TargetNode.class)).thenReturn(targetNodeExpected);

			TargetNode targetNodeActual = patchHelper.mergePatch(targetAsJsonMergePatch, targetNode, TargetNode.class);

			assertAll(
					() -> assertEquals(targetId, targetNodeActual.getId(),
							() -> "should return target node with id: " + targetId + ", but was: "
									+ targetNodeActual.getId()),
					() -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
							() -> "should return target node with target: " + targetNodeExpected.getTarget()
									+ ", but was: " + targetNodeActual.getTarget()),
					() -> verify(objectMapper, times(1)).convertValue(targetNode, JsonValue.class),
					() -> verify(objectMapper, times(1)).convertValue(patched, TargetNode.class),
					() -> verifyNoMoreInteractions(objectMapper));
		}
	}

	@Nested
	@Tag("EventPatch_Tests")
	class EventPatchTest {

		@Test
		void when_patch_event_node_should_return_patched_event_node() throws ParseException {

			Long eventId = 1L;

			String summary = "summary";
			String motive = "motive";
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2000-09-01");
			boolean isPartOfMultipleIncidents = true;
			boolean isSuccessful = true;
			boolean isSuicide = true;

			Long targetId = 1L;
			String target = "target";
			TargetNode targetNode = new TargetNode(targetId, target);

			String updatedSummary = "summary";
			String updatedMotive = "motive";
			Date updatedDate = new SimpleDateFormat("yyyy-MM-dd").parse("2000-10-02");
			boolean updatedIsPartOfMultipleIncidents = true;
			boolean updatedIsSuccessful = true;
			boolean updatedIsSuicide = true;

			EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(targetNode).build();

			EventNode eventNodeExpected = EventNode.builder().id(eventId).date(updatedDate).summary(updatedSummary)
					.isPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents).isSuccessful(updatedIsSuccessful)
					.isSuicide(updatedIsSuicide).motive(updatedMotive).target(targetNode).build();

			JsonPatch eventAsJsonPatch = Json.createPatchBuilder()
						.replace("/summary", updatedSummary)
						.replace("/motive", updatedMotive)
						.replace("/date", date.toString())
						.replace("/isPartOfMultipleIncidents", updatedIsPartOfMultipleIncidents)
						.replace("/isSuccessful", updatedIsSuccessful)
						.replace("/isSuicide", updatedIsSuicide)
					.build();

			JsonStructure event = Json.createObjectBuilder().add("summary", updatedSummary).add("motive", updatedMotive)
					.add("date", updatedDate.toString())
					.add("isPartOfMultipleIncidents", updatedIsPartOfMultipleIncidents)
					.add("isSuccessful", updatedIsSuccessful)
					.add("isSuicide", updatedIsSuicide)
				.build();

			JsonValue patched = eventAsJsonPatch.apply(event);

			when(objectMapper.convertValue(eventNode, JsonStructure.class)).thenReturn(event);
			when(objectMapper.convertValue(patched, EventNode.class)).thenReturn(eventNodeExpected);

			EventNode eventNodeActual = patchHelper.patch(eventAsJsonPatch, eventNode, EventNode.class);

			assertAll(
					() -> assertEquals(eventNodeExpected.getId(), eventNodeActual.getId(),
							() -> "should return event node with idd: " + eventNodeExpected.getId() + ", but was: "
									+ eventNodeActual.getId()),
					() -> assertEquals(eventNodeExpected.getSummary(), eventNodeActual.getSummary(),
							() -> "should return event node with summary: " + eventNodeExpected.getSummary()
									+ ", but was: " + eventNodeActual.getSummary()),
					() -> assertEquals(eventNodeExpected.getMotive(), eventNodeActual.getMotive(),
							() -> "should return event node with motive: " + eventNodeExpected.getMotive()
									+ ", but was: " + eventNodeActual.getMotive()),
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
					() -> assertEquals(eventNodeExpected.getTarget(), eventNodeActual.getTarget(),
							() -> "should return event node with target: " + eventNodeExpected.getTarget()
									+ ", but was: " + eventNodeActual.getTarget()),
					() -> verify(objectMapper, times(1)).convertValue(eventNode, JsonStructure.class),
					() -> verify(objectMapper, times(1)).convertValue(patched, EventNode.class),
					() -> verifyNoMoreInteractions(objectMapper));
		}

		@Test
		void when_patch_event_nodes_target_should_return_event_node_with_patched_target() throws ParseException {

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

			String updatedTarget = "updated target";
			TargetNode updatedTargetNode = new TargetNode(targetId, updatedTarget);

			EventNode eventNode = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(targetNode).build();

			EventNode eventNodeExpected = EventNode.builder().id(eventId).date(date).summary(summary)
					.isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
					.isSuicide(isSuicide).motive(motive).target(updatedTargetNode).build();

			JsonPatch eventAsJsonPatch = Json.createPatchBuilder().replace("/target/target", updatedTarget).build();

			JsonStructure event = Json.createObjectBuilder()
					.add("target", Json.createObjectBuilder()
							.add("target", updatedTarget))
					.build();

			JsonValue patched = eventAsJsonPatch.apply(event);

			when(objectMapper.convertValue(eventNode, JsonStructure.class)).thenReturn(event);
			when(objectMapper.convertValue(patched, EventNode.class)).thenReturn(eventNodeExpected);

			EventNode eventNodeActual = patchHelper.patch(eventAsJsonPatch, eventNode, EventNode.class);

			assertAll(
					() -> assertEquals(eventNodeExpected.getId(), eventNodeActual.getId(),
							() -> "should return event node with idd: " + eventNodeExpected.getId() + ", but was: "
									+ eventNodeActual.getId()),
					() -> assertEquals(eventNodeExpected.getSummary(), eventNodeActual.getSummary(),
							() -> "should return event node with summary: " + eventNodeExpected.getSummary()
									+ ", but was: " + eventNodeActual.getSummary()),
					() -> assertEquals(eventNodeExpected.getMotive(), eventNodeActual.getMotive(),
							() -> "should return event node with motive: " + eventNodeExpected.getMotive()
									+ ", but was: " + eventNodeActual.getMotive()),
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
					() -> assertEquals(eventNodeExpected.getTarget(), eventNodeActual.getTarget(),
							() -> "should return event node with target: " + eventNodeExpected.getTarget()
									+ ", but was: " + eventNodeActual.getTarget()),
					() -> verify(objectMapper, times(1)).convertValue(eventNode, JsonStructure.class),
					() -> verify(objectMapper, times(1)).convertValue(patched, EventNode.class),
					() -> verifyNoMoreInteractions(objectMapper));
		}
	}
}
