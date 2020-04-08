package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtils.NameWithSpacesGenerator;

@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("ModelMapper_Tests")
public class ModelMapperTest {

	private ModelMapper modelMapper;

	@BeforeEach
	public void setUp() {

		modelMapper = new ModelMapper();
	}

	@Test
	public void when_map_target_dto_without_id_to_node_should_return_valid_node() {

		Long targetId = null;
		String targetName = "Target";

		TargetDTO targetDTOExpected = new TargetDTO(targetId, targetName);

		TargetNode targetNodeActual = modelMapper.map(targetDTOExpected, TargetNode.class);

		assertAll(
				() -> assertEquals(targetId, targetNodeActual.getId(),
						() -> "should return target node with id: " + targetId + ", but was: "
								+ targetNodeActual.getId()),
				() -> assertEquals(targetName, targetNodeActual.getTarget(),
						() -> "should return target node with target: " + targetName + ", but was: "
								+ targetNodeActual.getTarget()));
	}

	@Test
	public void when_map_target_dto_with_id_to_node_should_return_valid_node() {

		Long targetId = 1L;
		String targetName = "Target";

		TargetDTO targetDTOExpected = new TargetDTO(targetId, targetName);

		TargetNode targetNodeActual = modelMapper.map(targetDTOExpected, TargetNode.class);

		assertAll(
				() -> assertEquals(targetId, targetNodeActual.getId(),
						() -> "should return target node with id: " + targetId + ", but was: "
								+ targetNodeActual.getId()),
				() -> assertEquals(targetName, targetNodeActual.getTarget(),
						() -> "should return target node with target: " + targetName + ", but was: "
								+ targetNodeActual.getTarget()));
	}
}
