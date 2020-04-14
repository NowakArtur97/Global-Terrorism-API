package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;

@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("TargetModelAssembler_Tests")
public class TargetModelAssemblerTest {

	private TargetModelAssembler targetModelAssembler;

	@BeforeEach
	public void setUp() {

		targetModelAssembler = new TargetModelAssembler();
	}

	@Test
	public void when_map_target_node_to_model_should_return_target_model() {

		Long targetId = 1L;
		String targetName = "target1";
		TargetNode targetNode = new TargetNode(targetId, targetName);

		TargetModel targetModel = targetModelAssembler.toModel(targetNode);

		assertAll(
				() -> assertEquals(targetNode.getId(), targetModel.getId(),
						() -> "should return model with id: " + targetNode.getId() + ", but was: "
								+ targetModel.getId()),
				() -> assertEquals(targetNode.getTarget(), targetModel.getTarget(),
						() -> "should return model with target: " + targetNode.getTarget() + ", but was: "
								+ targetModel.getTarget()),
				() -> assertNotNull(targetModel.getLinks(),
						() -> "should return model with links, but was: " + targetModel),
				() -> assertFalse(targetModel.getLinks().isEmpty(),
						() -> "should return model with links, but was: " + targetModel));
	}
}
