package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.CountryDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.CountryModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.CountryBuilder;
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
class CountryMapperTest {

    private ObjectMapper objectMapper;

    @Mock
    private ModelMapper modelMapper;

    private static RegionBuilder regionBuilder;
    private static CountryBuilder countryBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        regionBuilder = new RegionBuilder();
        countryBuilder = new CountryBuilder();
    }

    @BeforeEach
    private void setUp() {

        objectMapper = new ObjectMapperImpl(modelMapper);
    }

    @Test
    void when_map_country_dto_to_node_should_return_node() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.build(ObjectType.DTO);

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);

        when(modelMapper.map(countryDTO, CountryNode.class)).thenReturn(countryNodeExpected);

        CountryNode countryNodeActual = objectMapper.map(countryDTO, CountryNode.class);

        assertAll(
                () -> assertNotNull(countryNodeActual,
                        () -> "should return country node with not null country, but was: null"),
                () -> assertEquals(countryNodeExpected, countryNodeActual,
                        () -> "should return country node with country: " + countryNodeExpected + ", but was: " +
                                countryNodeActual),
                () -> assertEquals(countryNodeExpected.getId(), countryNodeActual.getId(),
                        () -> "should return country node with id: " + countryNodeExpected.getId()
                                + ", but was: " + countryNodeActual.getId()),
                () -> assertEquals(countryNodeExpected.getName(), countryNodeActual.getName(),
                        () -> "should return country node with name: " + countryNodeExpected.getName()
                                + ", but was: " + countryNodeActual),
                () -> assertNotNull(countryNodeActual.getRegion(),
                        () -> "should return country node with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected, countryNodeActual.getRegion(),
                        () -> "should return country node with region: " + regionNodeExpected + ", but was: "
                                + countryNodeActual.getRegion()),
                () -> assertEquals(regionNodeExpected.getId(), countryNodeActual.getRegion().getId(),
                        () -> "should return country node with region id: " + regionNodeExpected.getId()
                                + ", but was: " + countryNodeActual.getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), countryNodeActual.getRegion().getName(),
                        () -> "should return country node with region name: " + regionNodeExpected.getName() + ", but was: "
                                + countryNodeActual.getRegion().getName()),
                () -> verify(modelMapper, times(1)).map(countryDTO, CountryNode.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @Test
    void when_map_country_node_to_dto_should_return_dto() {

        CountryNode countryNode = (CountryNode) countryBuilder.build(ObjectType.NODE);

        CountryDTO countryDTOExpected = (CountryDTO) countryBuilder.build(ObjectType.DTO);

        when(modelMapper.map(countryNode, CountryDTO.class)).thenReturn(countryDTOExpected);

        CountryDTO countryDTOActual = objectMapper.map(countryNode, CountryDTO.class);

        assertAll(
                () -> assertNotNull(countryDTOActual, () -> "should return null country, but was: null"),
                () -> assertEquals(countryDTOExpected, countryDTOActual,
                        () -> "should return country dto: " + countryDTOExpected + ", but was: "
                                + countryDTOActual),
                () -> assertEquals(countryDTOExpected.getName(), countryDTOActual.getName(),
                        () -> "should return country dto with name: " + countryDTOExpected.getName() + ", but was: "
                                + countryDTOActual.getName()),
                () -> verify(modelMapper, times(1)).map(countryNode, CountryDTO.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @Test
    void when_map_country_node_to_model_should_return_model() {

        RegionNode regionNode = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNode = (CountryNode) countryBuilder.withRegion(regionNode).build(ObjectType.NODE);

        RegionModel regionModelExpected = (RegionModel) regionBuilder.build(ObjectType.MODEL);
        CountryModel countryModelExpected = (CountryModel) countryBuilder.withRegion(regionModelExpected).build(ObjectType.MODEL);

        when(modelMapper.map(countryNode, CountryModel.class)).thenReturn(countryModelExpected);

        CountryModel countryModelActual = objectMapper.map(countryNode, CountryModel.class);

        assertAll(
                () -> assertNotNull(countryModelActual, () -> "should return not null country, but was: null"),
                () -> assertEquals(countryModelExpected, countryModelActual,
                        () -> "should return country model: " + countryModelExpected + ", but was: "
                                + countryModelActual),
                () -> assertEquals(countryModelExpected.getId(), countryModelActual.getId(),
                        () -> "should return country model with id: " + countryModelExpected.getId()
                                + ", but was: " + countryModelActual.getId()),
                () -> assertEquals(countryModelExpected.getName(), countryModelActual.getName(),
                        () -> "should return country model with name: " + countryModelExpected.getName() + ", but was: "
                                + countryModelActual.getName()),

                () -> assertNotNull(countryModelActual.getRegion(),
                        () -> "should return country model with not null region, but was: null"),
                () -> assertEquals(regionModelExpected, countryModelActual.getRegion(),
                        () -> "should return country model with region: " + regionModelExpected + ", but was: "
                                + countryModelActual.getRegion()),
                () -> assertEquals(regionModelExpected.getId(), countryModelActual.getRegion().getId(),
                        () -> "should return country model with region id: " + regionModelExpected.getId()
                                + ", but was: " + countryModelActual.getRegion().getId()),
                () -> assertEquals(regionModelExpected.getName(), countryModelActual.getRegion().getName(),
                        () -> "should return country model with region name: " + regionModelExpected.getName() + ", but was: "
                                + countryModelActual.getRegion().getName()),
                () -> verify(modelMapper, times(1)).map(countryNode, CountryModel.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }
}