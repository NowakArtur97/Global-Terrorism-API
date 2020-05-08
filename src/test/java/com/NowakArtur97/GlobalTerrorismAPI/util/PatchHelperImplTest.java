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
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("PatchHelperImpl_Tests")
class PatchHelperImplTest {

	private PatchHelper patchHelper;

	@Mock
	private ObjectMapper objectMapper;

	private TargetBuilder targetBuilder;
	private EventBuilder eventBuilder;

	@BeforeEach
	private void setUp() {

		targetBuilder = new TargetBuilder();
		eventBuilder = new EventBuilder();

		patchHelper = new PatchHelperImpl(objectMapper);
	}

	@Nested
	@Tag("TargetPatch_Tests")
	class TargetPatchTest {

		@Test
		void when_patch_target_node_should_return_patched_target_node() {

			String updatedTargetName = "updated target";
			TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			TargetNode targetNodeExpected = (TargetNode) targetBuilder.withTarget("updated target")
					.build(ObjectType.NODE);

			JsonPatch targetAsJsonPatch = Json.createPatchBuilder().replace("/target", updatedTargetName).build();

			JsonStructure target = Json.createObjectBuilder().add("target", updatedTargetName).build();

			JsonValue patched = targetAsJsonPatch.apply(target);

			when(objectMapper.convertValue(targetNode, JsonStructure.class)).thenReturn(target);
			when(objectMapper.convertValue(patched, TargetNode.class)).thenReturn(targetNodeExpected);

			TargetNode targetNodeActual = patchHelper.patch(targetAsJsonPatch, targetNode, TargetNode.class);

			assertAll(
					() -> assertEquals(targetNodeExpected.getId(), targetNodeActual.getId(),
							() -> "should return target node with id: " + targetNodeExpected.getId() + ", but was: "
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

			String updatedTargetName = "updated target";
			TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			TargetNode targetNodeExpected = (TargetNode) targetBuilder.withTarget("updated target")
					.build(ObjectType.NODE);

			JsonMergePatch targetAsJsonMergePatch = Json
					.createMergePatch(Json.createObjectBuilder().add("target", updatedTargetName).build());

			JsonValue target = Json.createObjectBuilder().add("target", updatedTargetName).build();

			JsonValue patched = targetAsJsonMergePatch.apply(target);

			when(objectMapper.convertValue(targetNode, JsonValue.class)).thenReturn(target);
			when(objectMapper.convertValue(patched, TargetNode.class)).thenReturn(targetNodeExpected);

			TargetNode targetNodeActual = patchHelper.mergePatch(targetAsJsonMergePatch, targetNode, TargetNode.class);

			assertAll(
					() -> assertEquals(targetNodeExpected.getId(), targetNodeActual.getId(),
							() -> "should return target node with id: " + targetNodeExpected.getId() + ", but was: "
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
		
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2000-09-01");
			TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);

			String updatedSummary = "updated summary";
			String updatedMotive = "updated motive";
			Date updatedDate = new SimpleDateFormat("yyyy-MM-dd").parse("2000-10-02");
			Boolean updatedIsPartOfMultipleIncidents = false;
			Boolean updatedIsSuccessful = false;
			Boolean updatedIsSuicide = false;

			EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

			EventNode eventNodeExpected = (EventNode) eventBuilder.withSummary(updatedSummary).withMotive(updatedMotive)
					.withDate(updatedDate).withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents)
					.withIsSuccessful(updatedIsSuccessful).withIsSuicide(updatedIsSuicide).withTarget(targetNode)
					.build(ObjectType.NODE);

			JsonPatch eventAsJsonPatch = Json.createPatchBuilder().replace("/summary", updatedSummary)
					.replace("/motive", updatedMotive).replace("/date", date.toString())
					.replace("/isPartOfMultipleIncidents", updatedIsPartOfMultipleIncidents)
					.replace("/isSuccessful", updatedIsSuccessful).replace("/isSuicide", updatedIsSuicide).build();

			JsonStructure event = Json.createObjectBuilder().add("summary", updatedSummary).add("motive", updatedMotive)
					.add("date", updatedDate.toString())
					.add("isPartOfMultipleIncidents", updatedIsPartOfMultipleIncidents)
					.add("isSuccessful", updatedIsSuccessful).add("isSuicide", updatedIsSuicide).build();

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
					() -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
							eventNodeActual.getIsPartOfMultipleIncidents(),
							() -> "should return event node which was part of multiple incidents: "
									+ eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was: "
									+ eventNodeActual.getIsPartOfMultipleIncidents()),
					() -> assertEquals(eventNodeExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
							() -> "should return event node which was successful: "
									+ eventNodeExpected.getIsSuccessful() + ", but was: "
									+ eventNodeActual.getIsSuccessful()),
					() -> assertEquals(eventNodeExpected.getIsSuicide(), eventNodeActual.getIsSuicide(),
							() -> "should return event node which was suicide: " + eventNodeExpected.getIsSuicide()
									+ ", but was: " + eventNodeActual.getIsSuicide()),
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

			TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

			String updatedTarget = "updated target";
			TargetNode updatedTargetNode = (TargetNode) targetBuilder.withTarget(updatedTarget).build(ObjectType.NODE);
			EventNode eventNodeExpected = (EventNode) eventBuilder.withTarget(updatedTargetNode).build(ObjectType.NODE);

			JsonPatch eventAsJsonPatch = Json.createPatchBuilder().replace("/target/target", updatedTarget).build();

			JsonStructure event = Json.createObjectBuilder()
					.add("target", Json.createObjectBuilder().add("target", updatedTarget)).build();

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
					() -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
							eventNodeActual.getIsPartOfMultipleIncidents(),
							() -> "should return event node which was part of multiple incidents: "
									+ eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was: "
									+ eventNodeActual.getIsPartOfMultipleIncidents()),
					() -> assertEquals(eventNodeExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
							() -> "should return event node which was successful: "
									+ eventNodeExpected.getIsSuccessful() + ", but was: "
									+ eventNodeActual.getIsSuccessful()),
					() -> assertEquals(eventNodeExpected.getIsSuicide(), eventNodeActual.getIsSuicide(),
							() -> "should return event node which was suicide: " + eventNodeExpected.getIsSuicide()
									+ ", but was: " + eventNodeActual.getIsSuicide()),
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
		void when_merge_patch_event_node_should_return_patched_event_node() throws ParseException {

			Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2000-09-01");
			TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);

			String updatedSummary = "updated summary";
			String updatedMotive = "updated motive";
			Date updatedDate = new SimpleDateFormat("yyyy-MM-dd").parse("2000-10-02");
			Boolean updatedIsPartOfMultipleIncidents = false;
			Boolean updatedIsSuccessful = false;
			Boolean updatedIsSuicide = false;

			EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

			EventNode eventNodeExpected = (EventNode) eventBuilder.withSummary(updatedSummary).withMotive(updatedMotive)
					.withDate(updatedDate).withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents)
					.withIsSuccessful(updatedIsSuccessful).withIsSuicide(updatedIsSuicide).withTarget(targetNode)
					.build(ObjectType.NODE);

			JsonMergePatch eventAsJsonMergePatch = Json.createMergePatch(Json.createObjectBuilder()
					.add("summary", updatedSummary).add("motive", updatedMotive).add("date", date.toString())
					.add("isPartOfMultipleIncidents", updatedIsPartOfMultipleIncidents)
					.add("isSuccessful", updatedIsSuccessful).add("isSuicide", updatedIsSuicide).build());

			JsonStructure event = Json.createObjectBuilder().add("summary", updatedSummary).add("motive", updatedMotive)
					.add("date", updatedDate.toString())
					.add("isPartOfMultipleIncidents", updatedIsPartOfMultipleIncidents)
					.add("isSuccessful", updatedIsSuccessful).add("isSuicide", updatedIsSuicide).build();

			JsonValue patched = eventAsJsonMergePatch.apply(event);

			when(objectMapper.convertValue(eventNode, JsonValue.class)).thenReturn(event);
			when(objectMapper.convertValue(patched, EventNode.class)).thenReturn(eventNodeExpected);

			EventNode eventNodeActual = patchHelper.mergePatch(eventAsJsonMergePatch, eventNode, EventNode.class);

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
					() -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
							eventNodeActual.getIsPartOfMultipleIncidents(),
							() -> "should return event node which was part of multiple incidents: "
									+ eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was: "
									+ eventNodeActual.getIsPartOfMultipleIncidents()),
					() -> assertEquals(eventNodeExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
							() -> "should return event node which was successful: "
									+ eventNodeExpected.getIsSuccessful() + ", but was: "
									+ eventNodeActual.getIsSuccessful()),
					() -> assertEquals(eventNodeExpected.getIsSuicide(), eventNodeActual.getIsSuicide(),
							() -> "should return event node which was suicide: " + eventNodeExpected.getIsSuicide()
									+ ", but was: " + eventNodeActual.getIsSuicide()),
					() -> assertNotNull(eventNodeExpected.getTarget(),
							() -> "should return event node with not null target, but was: null"),
					() -> assertEquals(eventNodeExpected.getTarget(), eventNodeActual.getTarget(),
							() -> "should return event node with target: " + eventNodeExpected.getTarget()
									+ ", but was: " + eventNodeActual.getTarget()),
					() -> verify(objectMapper, times(1)).convertValue(eventNode, JsonValue.class),
					() -> verify(objectMapper, times(1)).convertValue(patched, EventNode.class),
					() -> verifyNoMoreInteractions(objectMapper));
		}

		@Test
		void when_merge_patch_event_nodes_target_should_return_event_node_with_patched_target() throws ParseException {

			TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
			EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

			String updatedTarget = "updated target";
			TargetNode updatedTargetNode = (TargetNode) targetBuilder.withTarget(updatedTarget).build(ObjectType.NODE);
			EventNode eventNodeExpected = (EventNode) eventBuilder.withTarget(updatedTargetNode).build(ObjectType.NODE);

			JsonMergePatch eventAsJsonMergePatch = Json
					.createMergePatch(Json.createObjectBuilder().add("/target/target", updatedTarget).build());

			JsonStructure event = Json.createObjectBuilder()
					.add("target", Json.createObjectBuilder().add("target", updatedTarget)).build();

			JsonValue patched = eventAsJsonMergePatch.apply(event);

			when(objectMapper.convertValue(eventNode, JsonValue.class)).thenReturn(event);
			when(objectMapper.convertValue(patched, EventNode.class)).thenReturn(eventNodeExpected);

			EventNode eventNodeActual = patchHelper.mergePatch(eventAsJsonMergePatch, eventNode, EventNode.class);

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
					() -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
							eventNodeActual.getIsPartOfMultipleIncidents(),
							() -> "should return event node which was part of multiple incidents: "
									+ eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was: "
									+ eventNodeActual.getIsPartOfMultipleIncidents()),
					() -> assertEquals(eventNodeExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
							() -> "should return event node which was successful: "
									+ eventNodeExpected.getIsSuccessful() + ", but was: "
									+ eventNodeActual.getIsSuccessful()),
					() -> assertEquals(eventNodeExpected.getIsSuicide(), eventNodeActual.getIsSuicide(),
							() -> "should return event node which was suicide: " + eventNodeExpected.getIsSuicide()
									+ ", but was: " + eventNodeActual.getIsSuicide()),
					() -> assertNotNull(eventNodeExpected.getTarget(),
							() -> "should return event node with not null target, but was: null"),
					() -> assertEquals(eventNodeExpected.getTarget(), eventNodeActual.getTarget(),
							() -> "should return event node with target: " + eventNodeExpected.getTarget()
									+ ", but was: " + eventNodeActual.getTarget()),
					() -> verify(objectMapper, times(1)).convertValue(eventNode, JsonValue.class),
					() -> verify(objectMapper, times(1)).convertValue(patched, EventNode.class),
					() -> verifyNoMoreInteractions(objectMapper));
		}
	}
}
