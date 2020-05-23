package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("DTOMapper_Tests")
class DTOTargetMapperTest {

    private DTOMapper<TargetNode, TargetDTO> dtoMapper;

    @Mock
    private ModelMapper modelMapper;

    private static TargetBuilder targetBuilder;

    @BeforeAll
    private static void init() {

        targetBuilder = new TargetBuilder();
    }

    @BeforeEach
    private void setUp() {

        dtoMapper = new DTOMapperImpl<>(modelMapper);
    }

    @Test
    void when_map_target_dto_to_node_should_return_target_node() {

        TargetDTO targetDTOExpected = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withId(null).build(ObjectType.NODE);

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
    void when_map_target_node_to_dto_should_return_target_dto() {

        TargetNode targetNodeExpected = (TargetNode) targetBuilder.build(ObjectType.NODE);
        TargetDTO targetDTOExpected = (TargetDTO) targetBuilder.build(ObjectType.DTO);

        when(modelMapper.map(targetNodeExpected, TargetDTO.class)).thenReturn(targetDTOExpected);

        TargetDTO targetDTOActual = dtoMapper.mapToDTO(targetNodeExpected, TargetDTO.class);

        assertAll(
                () -> assertEquals(targetDTOExpected.getTarget(), targetDTOActual.getTarget(),
                        () -> "should return target dto with target: " + targetDTOActual.getTarget() + ", but was: "
                                + targetDTOActual.getTarget()),
                () -> verify(modelMapper, times(1)).map(targetNodeExpected, TargetDTO.class));
    }
}