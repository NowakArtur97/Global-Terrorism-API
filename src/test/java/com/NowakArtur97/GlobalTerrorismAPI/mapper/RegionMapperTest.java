package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.model.response.RegionModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.RegionNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.RegionBuilder;
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
class RegionMapperTest {

    private ObjectMapper objectMapper;

    @Mock
    private ModelMapper modelMapper;

    private static RegionBuilder regionBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        regionBuilder = new RegionBuilder();
    }

    @BeforeEach
    private void setUp() {

        objectMapper = new ObjectMapperImpl(modelMapper);
    }

    @Test
    void when_map_region_node_to_model_should_return_model() {

        RegionNode regionNode = (RegionNode) regionBuilder.build(ObjectType.NODE);

        RegionModel regionModelExpected = (RegionModel) regionBuilder.build(ObjectType.MODEL);

        when(modelMapper.map(regionNode, RegionModel.class)).thenReturn(regionModelExpected);

        RegionModel regionModelActual = objectMapper.map(regionNode, RegionModel.class);

        assertAll(
                () -> assertNotNull(regionModelActual, () -> "should return not null region, but was: null"),
                () -> assertEquals(regionModelExpected, regionModelActual,
                        () -> "should return region model: " + regionModelExpected + ", but was: "
                                + regionModelActual),
                () -> assertEquals(regionModelExpected.getId(), regionModelActual.getId(),
                        () -> "should return region model with id: " + regionModelExpected.getId()
                                + ", but was: " + regionModelActual.getId()),
                () -> assertEquals(regionModelExpected.getName(), regionModelActual.getName(),
                        () -> "should return region model with name: " + regionModelExpected.getName() + ", but was: "
                                + regionModelActual.getName()),
                () -> verify(modelMapper, times(1)).map(regionNode, RegionModel.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }
}