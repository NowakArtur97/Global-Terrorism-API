package com.NowakArtur97.GlobalTerrorismAPI.feature.region;

import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.RegionBuilder;
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
@Tag("RegionModelAssembler_Tests")
class RegionModelAssemblerTest {

    private final String REGION_BASE_PATH = "http://localhost/api/v1/regions";

    private RegionModelAssembler modelAssembler;

    @Mock
    private ObjectMapper objectMapper;

    private static RegionBuilder regionBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        regionBuilder = new RegionBuilder();
    }

    @BeforeEach
    private void setUp() {

        modelAssembler = new RegionModelAssembler(objectMapper);
    }

    @Test
    void when_map_region_node_to_model_should_return_region_model() {

        Long regionId = 1L;
        RegionNode regionNode = (RegionNode) regionBuilder.withId(regionId).build(ObjectType.NODE);

        RegionModel regionModelExpected = (RegionModel) regionBuilder.withId(regionId).build(ObjectType.MODEL);

        String pathToRegionLink = REGION_BASE_PATH + "/" + regionId.intValue();

        when(objectMapper.map(regionNode, RegionModel.class)).thenReturn(regionModelExpected);

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
                () -> verify(objectMapper, times(1)).map(regionNode, RegionModel.class),
                () -> verifyNoMoreInteractions(objectMapper));
    }
}
