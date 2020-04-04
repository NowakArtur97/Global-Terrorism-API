package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.NowakArtur97.GlobalTerrorismAPI.testUtils.NameWithSpacesGenerator;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("TargetMapper_Tests")
public class TargetMapperTest {

	private TargetMapper targetMapper;

	@Mock
	private ModelMapper modelMapper;

	@BeforeEach
	public void setUp() {

		targetMapper = new TargetMapperImpl(modelMapper);
	}

	@Test
	public void when_map_target_dto_to_node_should_return_target_node() {

		String targetName = "Target";

		TargetDTO targetDTOExpected = new TargetDTO(targetName);

		TargetNode targetNodeExpected = new TargetNode(targetName);

		when(targetMapper.mapDTOToNode(targetDTOExpected)).thenReturn(targetNodeExpected);

		TargetNode targetNodeActual = targetMapper.mapDTOToNode(targetDTOExpected);

		assertAll(
				() -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
						() -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: "
								+ targetNodeActual.getTarget()),
				() -> verify(modelMapper, times(1)).map(targetDTOExpected, TargetNode.class));
	}
}
