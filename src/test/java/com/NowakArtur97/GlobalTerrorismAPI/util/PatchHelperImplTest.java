package com.NowakArtur97.GlobalTerrorismAPI.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import javax.json.Json;
import javax.json.JsonMergePatch;
import javax.json.JsonPatch;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
						() -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: "
								+ targetNodeActual.getTarget()),
				() -> verify(objectMapper, times(1)).convertValue(targetNode, JsonStructure.class),
				() -> verify(objectMapper, times(1)).convertValue(patched, TargetNode.class),
				() -> verifyNoMoreInteractions(objectMapper));
	}

	@Test
	void when_merge_patch_target_node_should_return_patched_target_node() {

		Long targetId = 1L;
		String oldTargetName = "target";
		String updatedTargetName = "updated target";
		TargetNode targetNode = new TargetNode(targetId, oldTargetName);
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
						() -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: "
								+ targetNodeActual.getTarget()),
				() -> verify(objectMapper, times(1)).convertValue(targetNode, JsonValue.class),
				() -> verify(objectMapper, times(1)).convertValue(patched, TargetNode.class),
				() -> verifyNoMoreInteractions(objectMapper));
	}
}
