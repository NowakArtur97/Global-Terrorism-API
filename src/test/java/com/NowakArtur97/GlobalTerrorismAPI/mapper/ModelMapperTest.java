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
	public void when_map_target_dto_to_node_should_return_valid_node() {

		String targetName = "Target";

		TargetDTO targetDTOExpected = new TargetDTO(targetName);

		TargetNode targetNodeExpected = new TargetNode(targetName);

		TargetNode targetNodeActual = modelMapper.map(targetDTOExpected, TargetNode.class);

		assertAll(() -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
				() -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: "
						+ targetNodeActual.getTarget()));
	}
}
