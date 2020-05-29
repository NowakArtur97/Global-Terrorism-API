package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("TargetModelAssembler_Tests")
class TargetModelAssemblerTest {

	private TargetModelAssembler targetModelAssembler;

	private static TargetBuilder targetBuilder;

    @Mock
    private ObjectMapper objectMapper;

	@BeforeAll
	private static void init() {

		targetBuilder = new TargetBuilder();
	}

	@BeforeEach
	private void setUp() {

		targetModelAssembler = new TargetModelAssembler(objectMapper);
	}

	@Test
	void when_map_target_node_to_model_should_return_target_model() {

		TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
		TargetModel targetModelExpected = (TargetModel) targetBuilder.build(ObjectType.MODEL);

        when(objectMapper.map(targetNode, TargetModel.class)).thenReturn(targetModelExpected);

        TargetModel targetModelActual = targetModelAssembler.toModel(targetNode);

		assertAll(
				() -> assertEquals(targetNode.getId(), targetModelActual.getId(),
						() -> "should return model with id: " + targetNode.getId() + ", but was: "
								+ targetModelActual.getId()),
				() -> assertEquals(targetNode.getTarget(), targetModelActual.getTarget(),
						() -> "should return model with target: " + targetNode.getTarget() + ", but was: "
								+ targetModelActual.getTarget()),
				() -> assertNotNull(targetModelActual.getLinks(),
						() -> "should return model with links, but was: " + targetModelActual),
				() -> assertFalse(targetModelActual.getLinks().isEmpty(),
						() -> "should return model with links, but was: " + targetModelActual),
                () -> verify(objectMapper, times(1)).map(targetNode, TargetModel.class),
                () -> verifyNoMoreInteractions(objectMapper));
	}
}
