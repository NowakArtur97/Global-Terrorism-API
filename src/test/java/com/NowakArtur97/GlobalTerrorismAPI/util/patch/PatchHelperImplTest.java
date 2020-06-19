package com.NowakArtur97.GlobalTerrorismAPI.util.patch;

import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.GroupBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.patch.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.patch.PatchHelperImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.json.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("PatchHelperImpl_Tests")
class PatchHelperImplTest {

    private PatchHelper patchHelper;

    @Mock
    private ObjectMapper objectMapper;

    private TargetBuilder targetBuilder;
    private EventBuilder eventBuilder;
    private GroupBuilder groupBuilder;

    @BeforeEach
    private void setUp() {

        targetBuilder = new TargetBuilder();
        eventBuilder = new EventBuilder();
        groupBuilder = new GroupBuilder();

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
            boolean updatedIsPartOfMultipleIncidents = false;
            boolean updatedIsSuccessful = false;
            boolean updatedIsSuicide = false;

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
        void when_patch_event_nodes_target_should_return_event_node_with_patched_target() {

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
            boolean updatedIsPartOfMultipleIncidents = false;
            boolean updatedIsSuccessful = false;
            boolean updatedIsSuicide = false;

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
        void when_merge_patch_event_nodes_target_should_return_event_node_with_patched_target() {

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);

            String updatedTarget = "updated target";
            TargetNode updatedTargetNode = (TargetNode) targetBuilder.withTarget(updatedTarget).build(ObjectType.NODE);
            EventNode eventNodeExpected = (EventNode) eventBuilder.withTarget(updatedTargetNode).build(ObjectType.NODE);

            JsonMergePatch eventAsJsonMergePatch = Json
                    .createMergePatch(Json.createObjectBuilder().add("/target/target", updatedTarget)
                            .build());

            JsonStructure event = Json.createObjectBuilder()
                    .add("target", Json.createObjectBuilder().add("target", updatedTarget))
                    .build();

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

    @Nested
    @Tag("GroupPatch_Tests")
    class GroupPatchTest {

        @Test
        void when_patch_group_node_should_return_patched_group_node() {

            String updatedName = "updated group name";

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
            GroupNode groupNodeExpected = (GroupNode) groupBuilder.withName(updatedName).withEventsCaused(List.of(eventNode))
                    .build(ObjectType.NODE);

            JsonPatch groupAsJsonPatch = Json.createPatchBuilder().replace("/name", updatedName).build();

            JsonStructure group = Json.createObjectBuilder().add("name", updatedName).build();

            JsonValue patched = groupAsJsonPatch.apply(group);

            when(objectMapper.convertValue(groupNode, JsonStructure.class)).thenReturn(group);
            when(objectMapper.convertValue(patched, GroupNode.class)).thenReturn(groupNodeExpected);

            GroupNode groupNodeActual = patchHelper.patch(groupAsJsonPatch, groupNode, GroupNode.class);

            assertAll(
                    () -> assertEquals(groupNodeExpected.getId(), groupNodeActual.getId(),
                            () -> "should return group with id: " + groupNodeExpected.getId() + ", but was" + groupNodeActual.getId()),
                    () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                            () -> "should return group with name: " + groupNodeExpected.getName() + ", but was"
                                    + groupNodeActual.getName()),

                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getId(), groupNodeActual.getEventsCaused().get(0).getId(), () -> "should return group with event node with id: " + groupNodeExpected.getEventsCaused().get(0).getId() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getId()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group with event node with summary: " + groupNodeExpected.getEventsCaused().get(0).getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(), () -> "should return group with event node with motive: " + groupNodeExpected.getEventsCaused().get(0).getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getDate(), groupNodeActual.getEventsCaused().get(0).getDate(), () -> "should return group with event node with date: " + groupNodeExpected.getEventsCaused().get(0).getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                            groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(), () -> "should return group with event node which was part of multiple incidents: " + groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group with event node which was successful: " + groupNodeExpected.getEventsCaused().get(0).getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuicide(), groupNodeActual.getEventsCaused().get(0).getIsSuicide(), () -> "should return group with event node which was suicide: " + groupNodeExpected.getEventsCaused().get(0).getIsSuicide() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicide()),
                    () -> assertNotNull(groupNodeExpected.getEventsCaused().get(0).getTarget(),
                            () -> "should return group with event node with not null target, but was: null"),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget(), () -> "should return group with event node with target: " + groupNodeExpected.getEventsCaused().get(0).getTarget() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget()),
                    () -> verify(objectMapper, times(1)).convertValue(groupNode, JsonStructure.class),
                    () -> verify(objectMapper, times(1)).convertValue(patched, GroupNode.class),
                    () -> verifyNoMoreInteractions(objectMapper));
        }

        @Test
        void when_patch_group_nodes_events_should_return_group_node_with_patched_events() throws ParseException {

            Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2000-09-01");
            String updatedSummary = "updated summary";
            String updatedMotive = "updated motive";
            Date updatedDate = new SimpleDateFormat("yyyy-MM-dd").parse("2000-10-02");
            boolean updatedIsPartOfMultipleIncidents = false;
            boolean updatedIsSuccessful = false;
            boolean updatedIsSuicide = false;

            String updatedTarget = "updated target";

            String updatedName = "updated group name";

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            TargetNode updatedTargetNode = (TargetNode) targetBuilder.withTarget(updatedTarget).build(ObjectType.NODE);

            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
            EventNode updatedEventNode = (EventNode) eventBuilder.withSummary(updatedSummary).withMotive(updatedMotive)
                    .withDate(updatedDate).withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents)
                    .withIsSuccessful(updatedIsSuccessful).withIsSuicide(updatedIsSuicide).withTarget(updatedTargetNode)
                    .build(ObjectType.NODE);

            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
            GroupNode groupNodeExpected = (GroupNode) groupBuilder.withName(updatedName).withEventsCaused(List.of(updatedEventNode)).build(ObjectType.NODE);

            JsonPatch groupAsJsonPatch = Json.createPatchBuilder()
                    .replace("/name", updatedName)
                    .replace("/eventsCaused[0]/summary", updatedSummary)
                    .replace("/eventsCaused[0]/motive", updatedMotive)
                    .replace("/eventsCaused[0]/date", date.toString())
                    .replace("/eventsCaused[0]/isPartOfMultipleIncidents", updatedIsPartOfMultipleIncidents)
                    .replace("/eventsCaused[0]/isSuccessful", updatedIsSuccessful)
                    .replace("/eventsCaused[0]/isSuicide", updatedIsSuicide)
                    .replace("/eventsCaused[0]/target/target", updatedTarget)
                    .build();

            JsonStructure group = Json.createObjectBuilder()
                    .add("name", updatedName)
                    .add("eventsCaused[0]",
                            Json.createObjectBuilder()
                                    .add("summary", updatedSummary).add("motive", updatedMotive)
                                    .add("date", updatedDate.toString())
                                    .add("isPartOfMultipleIncidents", updatedIsPartOfMultipleIncidents)
                                    .add("isSuccessful", updatedIsSuccessful)
                                    .add("isSuicide", updatedIsSuicide)
                                    .add("target", Json.createObjectBuilder().add("target", updatedTarget)))
                    .build();

            JsonValue patched = groupAsJsonPatch.apply(group);

            when(objectMapper.convertValue(groupNode, JsonStructure.class)).thenReturn(group);
            when(objectMapper.convertValue(patched, GroupNode.class)).thenReturn(groupNodeExpected);

            GroupNode groupNodeActual = patchHelper.patch(groupAsJsonPatch, groupNode, GroupNode.class);

            assertAll(
                    () -> assertEquals(groupNodeExpected.getId(), groupNodeActual.getId(),
                            () -> "should return group with id: " + groupNodeExpected.getId() + ", but was" + groupNodeActual.getId()),
                    () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                            () -> "should return group with name: " + groupNodeExpected.getName() + ", but was"
                                    + groupNodeActual.getName()),

                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getId(), groupNodeActual.getEventsCaused().get(0).getId(), () -> "should return group with event node with id: " + groupNodeExpected.getEventsCaused().get(0).getId() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getId()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group with event node with summary: " + groupNodeExpected.getEventsCaused().get(0).getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(), () -> "should return group with event node with motive: " + groupNodeExpected.getEventsCaused().get(0).getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getDate(), groupNodeActual.getEventsCaused().get(0).getDate(), () -> "should return group with event node with date: " + groupNodeExpected.getEventsCaused().get(0).getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                            groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(), () -> "should return group with event node which was part of multiple incidents: " + groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group with event node which was successful: " + groupNodeExpected.getEventsCaused().get(0).getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuicide(), groupNodeActual.getEventsCaused().get(0).getIsSuicide(), () -> "should return group with event node which was suicide: " + groupNodeExpected.getEventsCaused().get(0).getIsSuicide() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicide()),
                    () -> assertNotNull(groupNodeExpected.getEventsCaused().get(0).getTarget(),
                            () -> "should return group with event node with not null target, but was: null"),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget(), () -> "should return group with event node with target: " + groupNodeExpected.getEventsCaused().get(0).getTarget() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget()),
                    () -> verify(objectMapper, times(1)).convertValue(groupNode, JsonStructure.class),
                    () -> verify(objectMapper, times(1)).convertValue(patched, GroupNode.class),
                    () -> verifyNoMoreInteractions(objectMapper));
        }

        @Test
        void when_merge_patch_group_node_should_return_patched_group_node() {

            String updatedName = "updated group name";

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
            GroupNode groupNodeExpected = (GroupNode) groupBuilder.withName(updatedName).withEventsCaused(List.of(eventNode))
                    .build(ObjectType.NODE);

            JsonMergePatch groupAsJsonMergePatch = Json.createMergePatch(Json.createObjectBuilder()
                    .add("name", updatedName).build());

            JsonStructure group = Json.createObjectBuilder().add("name", updatedName).build();

            JsonValue patched = groupAsJsonMergePatch.apply(group);

            when(objectMapper.convertValue(groupNode, JsonValue.class)).thenReturn(group);
            when(objectMapper.convertValue(patched, GroupNode.class)).thenReturn(groupNodeExpected);

            GroupNode groupNodeActual = patchHelper.mergePatch(groupAsJsonMergePatch, groupNode, GroupNode.class);

            assertAll(
                    () -> assertEquals(groupNodeExpected.getId(), groupNodeActual.getId(),
                            () -> "should return group with id: " + groupNodeExpected.getId() + ", but was" + groupNodeActual.getId()),
                    () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                            () -> "should return group with name: " + groupNodeExpected.getName() + ", but was"
                                    + groupNodeActual.getName()),

                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getId(), groupNodeActual.getEventsCaused().get(0).getId(), () -> "should return group with event node with id: " + groupNodeExpected.getEventsCaused().get(0).getId() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getId()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group with event node with summary: " + groupNodeExpected.getEventsCaused().get(0).getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(), () -> "should return group with event node with motive: " + groupNodeExpected.getEventsCaused().get(0).getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getDate(), groupNodeActual.getEventsCaused().get(0).getDate(), () -> "should return group with event node with date: " + groupNodeExpected.getEventsCaused().get(0).getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                            groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(), () -> "should return group with event node which was part of multiple incidents: " + groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group with event node which was successful: " + groupNodeExpected.getEventsCaused().get(0).getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuicide(), groupNodeActual.getEventsCaused().get(0).getIsSuicide(), () -> "should return group with event node which was suicide: " + groupNodeExpected.getEventsCaused().get(0).getIsSuicide() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicide()),
                    () -> assertNotNull(groupNodeExpected.getEventsCaused().get(0).getTarget(),
                            () -> "should return group with event node with not null target, but was: null"),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget(), () -> "should return group with event node with target: " + groupNodeExpected.getEventsCaused().get(0).getTarget() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget()),
                    () -> verify(objectMapper, times(1)).convertValue(groupNode, JsonValue.class),
                    () -> verify(objectMapper, times(1)).convertValue(patched, GroupNode.class),
                    () -> verifyNoMoreInteractions(objectMapper));
        }

        @Test
        void when_merge_patch_group_nodes_events_should_return_group_node_with_patched_events() throws ParseException {

            Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2000-09-01");
            String updatedSummary = "updated summary";
            String updatedMotive = "updated motive";
            Date updatedDate = new SimpleDateFormat("yyyy-MM-dd").parse("2000-10-02");
            boolean updatedIsPartOfMultipleIncidents = false;
            boolean updatedIsSuccessful = false;
            boolean updatedIsSuicide = false;

            String updatedTarget = "updated target";

            String updatedName = "updated group name";

            TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
            TargetNode updatedTargetNode = (TargetNode) targetBuilder.withTarget(updatedTarget).build(ObjectType.NODE);

            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).build(ObjectType.NODE);
            EventNode updatedEventNode = (EventNode) eventBuilder.withSummary(updatedSummary).withMotive(updatedMotive)
                    .withDate(updatedDate).withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents)
                    .withIsSuccessful(updatedIsSuccessful).withIsSuicide(updatedIsSuicide).withTarget(updatedTargetNode)
                    .build(ObjectType.NODE);

            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
            GroupNode groupNodeExpected = (GroupNode) groupBuilder.withName(updatedName).withEventsCaused(List.of(updatedEventNode)).build(ObjectType.NODE);

            JsonMergePatch groupAsJsonMergePatch = Json.createMergePatch(Json.createObjectBuilder()
                    .add("/name", updatedName)
                    .add("/eventsCaused[0]/summary", updatedSummary)
                    .add("/eventsCaused[0]/motive", updatedMotive)
                    .add("/eventsCaused[0]/date", date.toString())
                    .add("/eventsCaused[0]/isPartOfMultipleIncidents", updatedIsPartOfMultipleIncidents)
                    .add("/eventsCaused[0]/isSuccessful", updatedIsSuccessful)
                    .add("/eventsCaused[0]/isSuicide", updatedIsSuicide)
                    .add("/eventsCaused[0]/target/target", updatedTarget)
                    .build());

            JsonStructure group = Json.createObjectBuilder()
                    .add("name", updatedName)
                    .add("eventsCaused[0]", Json.createObjectBuilder()
                            .add("summary", updatedSummary)
                            .add("motive", updatedMotive)
                            .add("date", updatedDate.toString())
                            .add("isPartOfMultipleIncidents", updatedIsPartOfMultipleIncidents)
                            .add("isSuccessful", updatedIsSuccessful)
                            .add("isSuicide", updatedIsSuicide)
                            .add("target", Json.createObjectBuilder().add("target", updatedTarget)))
                    .build();

            JsonValue patched = groupAsJsonMergePatch.apply(group);

            when(objectMapper.convertValue(groupNode, JsonValue.class)).thenReturn(group);
            when(objectMapper.convertValue(patched, GroupNode.class)).thenReturn(groupNodeExpected);

            GroupNode groupNodeActual = patchHelper.mergePatch(groupAsJsonMergePatch, groupNode, GroupNode.class);

            assertAll(
                    () -> assertEquals(groupNodeExpected.getId(), groupNodeActual.getId(),
                            () -> "should return group with id: " + groupNodeExpected.getId() + ", but was" + groupNodeActual.getId()),
                    () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                            () -> "should return group with name: " + groupNodeExpected.getName() + ", but was"
                                    + groupNodeActual.getName()),

                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getId(), groupNodeActual.getEventsCaused().get(0).getId(), () -> "should return group with event node with id: " + groupNodeExpected.getEventsCaused().get(0).getId() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getId()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group with event node with summary: " + groupNodeExpected.getEventsCaused().get(0).getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(), () -> "should return group with event node with motive: " + groupNodeExpected.getEventsCaused().get(0).getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getDate(), groupNodeActual.getEventsCaused().get(0).getDate(), () -> "should return group with event node with date: " + groupNodeExpected.getEventsCaused().get(0).getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                            groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(), () -> "should return group with event node which was part of multiple incidents: " + groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group with event node which was successful: " + groupNodeExpected.getEventsCaused().get(0).getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuicide(), groupNodeActual.getEventsCaused().get(0).getIsSuicide(), () -> "should return group with event node which was suicide: " + groupNodeExpected.getEventsCaused().get(0).getIsSuicide() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicide()),
                    () -> assertNotNull(groupNodeExpected.getEventsCaused().get(0).getTarget(),
                            () -> "should return group with event node with not null target, but was: null"),
                    () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget(), () -> "should return group with event node with target: " + groupNodeExpected.getEventsCaused().get(0).getTarget() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget()),
                    () -> verify(objectMapper, times(1)).convertValue(groupNode, JsonValue.class),
                    () -> verify(objectMapper, times(1)).convertValue(patched, GroupNode.class),
                    () -> verifyNoMoreInteractions(objectMapper));
        }
    }
}
