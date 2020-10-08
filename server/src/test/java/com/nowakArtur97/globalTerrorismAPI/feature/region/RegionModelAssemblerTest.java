package com.nowakArtur97.globalTerrorismAPI.feature.region;

import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.RegionBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("RegionModelAssembler_Tests")
@DisabledOnOs(OS.LINUX)
class RegionModelAssemblerTest {

    private final String REGION_BASE_PATH = "http://localhost/api/v1/regions";

    private RegionModelAssembler modelAssembler;

    @Mock
    private ModelMapper modelMapper;

    private static RegionBuilder regionBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        regionBuilder = new RegionBuilder();
    }

    @BeforeEach
    private void setUp() {

        modelAssembler = new RegionModelAssembler(modelMapper);
    }

    @Test
    void when_map_region_node_to_model_should_return_region_model() {

        Long regionId = 1L;
        RegionNode regionNode = (RegionNode) regionBuilder.withId(regionId).build(ObjectType.NODE);

        RegionModel regionModelExpected = (RegionModel) regionBuilder.withId(regionId).build(ObjectType.MODEL);

        String pathToRegionLink = REGION_BASE_PATH + "/" + regionId.intValue();

        when(modelMapper.map(regionNode, RegionModel.class)).thenReturn(regionModelExpected);

        RegionModel regionModelActual = modelAssembler.toModel(regionNode);

        assertAll(
                () -> assertEquals(pathToRegionLink, regionModelActual.getLink("self").get().getHref(),
                        () -> "should return region model with self link: " + pathToRegionLink + ", but was: "
                                + regionModelActual.getLink("self").get().getHref()),

                () -> assertNotNull(regionModelActual, () -> "should return not null region model, but was: null"),
                () -> assertEquals(regionModelExpected.getId(), regionModelActual.getId(),
                        () -> "should return region model with id: " + regionModelExpected.getId() + ", but was: "
                                + regionModelActual.getId()),
                () -> assertEquals(regionModelExpected.getName(), regionModelActual.getName(),
                        () -> "should return region model with name: " + regionModelExpected.getName() + ", but was: "
                                + regionModelActual.getName()),

                () -> assertFalse(regionModelActual.getLinks().isEmpty(),
                        () -> "should return region model with links, but wasn't"),
                () -> verify(modelMapper, times(1)).map(regionNode, RegionModel.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }
}
