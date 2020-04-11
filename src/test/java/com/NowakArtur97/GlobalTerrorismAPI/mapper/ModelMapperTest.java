package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
	public void when_map_target_dto_to_node_should_return_valid_node() {

		String targetName = "Target";

		TargetDTO targetDTOExpected = new TargetDTO(targetName);

		TargetNode targetNodeActual = modelMapper.map(targetDTOExpected, TargetNode.class);

		assertAll(
				() -> assertNull(targetNodeActual.getId(),
						() -> "should return target node with id as null, but was: " + targetNodeActual.getId()),
				() -> assertEquals(targetName, targetNodeActual.getTarget(),
						() -> "should return target node with target: " + targetName + ", but was: "
								+ targetNodeActual.getTarget()));
	}
}
