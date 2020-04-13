package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtils.nameGenerator.NameWithSpacesGenerator;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("DTOMapper_Tests")
public class DTOMapperTest {

	private DTOMapper dtoMapper;

	@Mock
	private ModelMapper modelMapper;

	@BeforeEach
	public void setUp() {

		dtoMapper = new DTOMapperImpl(modelMapper);
	}

	@Test
	public void when_map_target_dto_to_node_should_return_target_node() {

		String targetName = "Target";

		TargetDTO targetDTOExpected = new TargetDTO(targetName);

		TargetNode targetNodeExpected = new TargetNode(targetName);

		when(modelMapper.map(targetDTOExpected, TargetNode.class)).thenReturn(targetNodeExpected);

		TargetNode targetNodeActual = dtoMapper.mapToNode(targetDTOExpected, TargetNode.class);

		assertAll(
				() -> assertNull(targetNodeActual.getId(),
						() -> "should return target node with id as null, but was: " + targetNodeActual.getId()),
				() -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
						() -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: "
								+ targetNodeActual.getTarget()),
				() -> verify(modelMapper, times(1)).map(targetDTOExpected, TargetNode.class));
	}

	@Test
	public void when_map_target_node_to_dto_should_return_target_dto() {

		String targetName = "Target";

		TargetDTO targetDTOExpected = new TargetDTO(targetName);

		TargetNode targetNodeExpected = new TargetNode(targetName);

		when(modelMapper.map(targetNodeExpected, TargetDTO.class)).thenReturn(targetDTOExpected);

		TargetDTO targetDTOActual = dtoMapper.mapToDTO(targetNodeExpected, TargetDTO.class);

		assertAll(
				() -> assertEquals(targetDTOExpected.getTarget(), targetDTOActual.getTarget(),
						() -> "should return target dto with target: " + targetDTOActual.getTarget() + ", but was: "
								+ targetDTOActual.getTarget()),
				() -> verify(modelMapper, times(1)).map(targetNodeExpected, TargetDTO.class));
	}
}