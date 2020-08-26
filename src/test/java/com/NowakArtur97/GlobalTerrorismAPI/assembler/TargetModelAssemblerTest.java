package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.CountryModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.RegionModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.RegionNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.CountryBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.RegionBuilder;
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

    private final String TARGET_BASE_PATH = "/api/v1/targets";

    private TargetModelAssembler targetModelAssembler;

    private static RegionBuilder regionBuilder;
    private static CountryBuilder countryBuilder;
    private static TargetBuilder targetBuilder;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeAll
    private static void setUpBuilders() {

        regionBuilder = new RegionBuilder();
        countryBuilder = new CountryBuilder();
        targetBuilder = new TargetBuilder();
    }

    @BeforeEach
    private void setUp() {

        targetModelAssembler = new TargetModelAssembler(objectMapper);
    }

    @Test
    void when_map_target_node_to_model_should_return_target_model() {

        Long targetId = 1L;

        RegionNode regionNode = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNode = (CountryNode) countryBuilder.withRegion(regionNode).build(ObjectType.NODE);
        TargetNode targetNode = (TargetNode) targetBuilder.withId(targetId).withCountry(countryNode).build(ObjectType.NODE);

        RegionModel regionModel = (RegionModel) regionBuilder.build(ObjectType.MODEL);
        CountryModel countryModel = (CountryModel) countryBuilder.withRegion(regionModel).build(ObjectType.MODEL);
        TargetModel targetModel = (TargetModel) targetBuilder.withId(targetId).withCountry(countryModel).build(ObjectType.MODEL);
        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetId.intValue();

        when(objectMapper.map(targetNode, TargetModel.class)).thenReturn(targetModel);
        when(objectMapper.map(countryNode.getRegion(), RegionModel.class)).thenReturn(regionModel);
        when(objectMapper.map(targetNode.getCountryOfOrigin(), CountryModel.class)).thenReturn(countryModel);

        TargetModel targetModelActual = targetModelAssembler.toModel(targetNode);

        assertAll(
                () -> assertEquals(pathToTargetLink, targetModelActual.getLink("self").get().getHref(),
                        () -> "should return target model with self link: " + pathToTargetLink + ", but was: "
                                + targetModelActual.getLink("self").get().getHref()),
                () -> assertTrue(targetModelActual.getLink("country").isEmpty(),
                        () -> "should return target model without country link, but was: "
                                + targetModelActual.getLink("country").get().getHref()),
                () -> assertEquals(targetNode.getId(), targetModelActual.getId(),
                        () -> "should return target model with id: " + targetNode.getId() + ", but was: "
                                + targetModelActual.getId()),
                () -> assertEquals(targetNode.getTarget(), targetModelActual.getTarget(),
                        () -> "should return target model with target: " + targetNode.getTarget() + ", but was: "
                                + targetModelActual.getTarget()),
                () -> assertNotNull(targetModelActual.getLinks(),
                        () -> "should return target model with links, but was: " + targetModelActual),
                () -> assertFalse(targetModelActual.getLinks().isEmpty(),
                        () -> "should return target model with links, but was: " + targetModelActual),
                () -> assertEquals(countryModel.getId(), targetModelActual.getCountryOfOrigin().getId(),
                        () -> "should return target model with country id: " + targetModelActual.getCountryOfOrigin().getId()
                                + ", but was: " + targetModelActual.getId()),
                () -> assertEquals(countryModel.getName(), targetModelActual.getCountryOfOrigin().getName(),
                        () -> "should return target model with country name: " + targetModelActual.getCountryOfOrigin().getName()
                                + ", but was: " + targetModelActual.getCountryOfOrigin()),
                () -> assertTrue(countryModel.getLinks().isEmpty(),
                        () -> "should return target country model without links, but was: " + targetModelActual.getCountryOfOrigin()),
                () -> assertEquals(regionModel.getId(), targetModelActual.getCountryOfOrigin().getRegion().getId(),
                        () -> "should return target model with region id: " + regionModel.getId()
                                + ", but was: " + targetModelActual.getCountryOfOrigin().getRegion().getId()),
                () -> assertEquals(regionModel.getName(), targetModelActual.getCountryOfOrigin().getRegion().getName(),
                        () -> "should return target model with region name: " + regionModel.getName()
                                + ", but was: " + targetModelActual.getCountryOfOrigin().getRegion().getName()),
                () -> assertTrue(targetModelActual.getCountryOfOrigin().getRegion().getLinks().isEmpty(),
                        () -> "should return target with region model without links, but was: " + targetModelActual.getCountryOfOrigin().getRegion().getLinks()),
                () -> verify(objectMapper, times(1)).map(targetNode, TargetModel.class),
                () -> verify(objectMapper, times(1)).map(targetNode.getCountryOfOrigin(), CountryModel.class),
                () -> verify(objectMapper, times(1)).map(targetNode.getCountryOfOrigin(), CountryModel.class),
                () -> verifyNoMoreInteractions(objectMapper));
    }
}
