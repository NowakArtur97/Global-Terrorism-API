package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
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
@Tag("ObjectMapper_Tests")
class ObjectTargetMapperTest {

    private ObjectMapper dtoMapper;

    @Mock
    private ModelMapper modelMapper;

    private static TargetBuilder targetBuilder;

    @BeforeAll
    private static void init() {

        targetBuilder = new TargetBuilder();
    }

    @BeforeEach
    private void setUp() {

        dtoMapper = new ObjectMapperImpl(modelMapper);
    }

    @Test
    void when_map_target_dto_to_node_should_return_target_node() {

        TargetDTO targetDTO = (TargetDTO) targetBuilder.build(ObjectType.DTO);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withId(null).build(ObjectType.NODE);

        when(modelMapper.map(targetDTO, TargetNode.class)).thenReturn(targetNodeExpected);

        TargetNode targetNodeActual = dtoMapper.map(targetDTO, TargetNode.class);

        assertAll(
                () -> assertNull(targetNodeActual.getId(),
                        () -> "should return target node with id as null, but was: " + targetNodeActual.getId()),
                () -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
                        () -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + targetNodeActual.getTarget()),
                () -> verify(modelMapper, times(1)).map(targetDTO, TargetNode.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @Test
    void when_map_target_node_to_dto_should_return_target_dto() {

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        TargetDTO targetDTOExpected = (TargetDTO) targetBuilder.build(ObjectType.DTO);

        when(modelMapper.map(targetNode, TargetDTO.class)).thenReturn(targetDTOExpected);

        TargetDTO targetDTOActual = dtoMapper.map(targetNode, TargetDTO.class);

        assertAll(
                () -> assertEquals(targetDTOExpected.getTarget(), targetDTOActual.getTarget(),
                        () -> "should return target dto with target: " + targetDTOActual.getTarget() + ", but was: "
                                + targetDTOActual.getTarget()),
                () -> verify(modelMapper, times(1)).map(targetNode, TargetDTO.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @Test
    void when_map_target_node_to_model_should_return_target_model() {

        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        TargetModel targetModelExpected = (TargetModel) targetBuilder.build(ObjectType.MODEL);

        when(modelMapper.map(targetNode, TargetModel.class)).thenReturn(targetModelExpected);

        TargetModel targetModelActual = dtoMapper.map(targetNode, TargetModel.class);

        assertAll(
                () -> assertEquals(targetModelExpected.getTarget(), targetModelActual.getTarget(),
                        () -> "should return target model with target: " + targetModelExpected.getTarget() + ", but was: "
                                + targetModelActual.getTarget()),
                () -> verify(modelMapper, times(1)).map(targetNode, TargetModel.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }
}