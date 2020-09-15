package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.CountryDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.ProvinceDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.CountryModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.ProvinceModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.ProvinceNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.CountryBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.ProvinceBuilder;
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
class ProvinceMapperTest {

    private ObjectMapper objectMapper;

    @Mock
    private ModelMapper modelMapper;

    private static RegionBuilder regionBuilder;
    private static CountryBuilder countryBuilder;
    private static ProvinceBuilder provinceBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        regionBuilder = new RegionBuilder();
        countryBuilder = new CountryBuilder();
        provinceBuilder = new ProvinceBuilder();
    }

    @BeforeEach
    private void setUp() {

        objectMapper = new ObjectMapperImpl(modelMapper);
    }

    @Test
    void when_map_province_dto_to_node_should_return_node() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withId(null).withCountry(countryNodeExpected)
                .build(ObjectType.NODE);

        when(modelMapper.map(provinceDTO, ProvinceNode.class)).thenReturn(provinceNodeExpected);

        ProvinceNode provinceNodeActual = objectMapper.map(provinceDTO, ProvinceNode.class);

        assertAll(
                () -> assertNotNull(provinceNodeActual, () -> "should return not null province, but was: null"),
                () -> assertEquals(provinceNodeExpected, provinceNodeActual,
                        () -> "should return province node: " + provinceNodeExpected + ", but was: "
                                + provinceNodeActual),
                () -> assertEquals(provinceNodeExpected.getId(), provinceNodeActual.getId(),
                        () -> "should return province node id: " + provinceNodeExpected.getId()
                                + ", but was: " + provinceNodeActual.getId()),
                () -> assertEquals(provinceNodeExpected.getName(), provinceNodeActual.getName(),
                        () -> "should return province node name: " + provinceNodeExpected.getName() + ", but was: "
                                + provinceNodeActual.getName()),
                () -> assertNotNull(provinceNodeActual.getCountry(),
                        () -> "should return province node with not null country, but was: null"),
                () -> assertEquals(countryNodeExpected, provinceNodeActual.getCountry(),
                        () -> "should return province node with country: " + countryNodeExpected + ", but was: " +
                                provinceNodeActual.getCountry()),
                () -> assertEquals(countryNodeExpected.getId(), provinceNodeActual.getCountry().getId(),
                        () -> "should return province node with country id: " + countryNodeExpected.getId()
                                + ", but was: " + provinceNodeActual.getCountry().getId()),
                () -> assertEquals(countryNodeExpected.getName(), provinceNodeActual.getCountry().getName(),
                        () -> "should return province node with country name: " + countryNodeExpected.getName()
                                + ", but was: " + provinceNodeActual.getCountry()),
                () -> assertNotNull(provinceNodeActual.getCountry().getRegion(),
                        () -> "should return province node with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected, provinceNodeActual.getCountry().getRegion(),
                        () -> "should return province node with region: " + regionNodeExpected + ", but was: "
                                + provinceNodeActual.getCountry().getRegion()),
                () -> assertEquals(regionNodeExpected.getId(), provinceNodeActual.getCountry().getRegion().getId(),
                        () -> "should return province node with region id: " + regionNodeExpected.getId()
                                + ", but was: " + provinceNodeActual.getCountry().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), provinceNodeActual.getCountry().getRegion().getName(),
                        () -> "should return province node with region name: " + regionNodeExpected.getName() + ", but was: "
                                + provinceNodeActual.getCountry().getRegion().getName()),
                () -> verify(modelMapper, times(1)).map(provinceDTO, ProvinceNode.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @Test
    void when_map_province_node_to_dto_should_return_dto() {

        CountryNode countryNode = (CountryNode) countryBuilder.build(ObjectType.NODE);
        ProvinceNode provinceNode = (ProvinceNode) provinceBuilder.withCountry(countryNode)
                .build(ObjectType.NODE);

        CountryDTO countryDTOExpected = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        ProvinceDTO provinceDTOExpected = (ProvinceDTO) provinceBuilder.withCountry(countryDTOExpected).build(ObjectType.DTO);

        when(modelMapper.map(provinceNode, ProvinceDTO.class)).thenReturn(provinceDTOExpected);

        ProvinceDTO provinceDTOActual = objectMapper.map(provinceNode, ProvinceDTO.class);

        assertAll(
                () -> assertNotNull(provinceDTOActual, () -> "should return null province, but was: null"),
                () -> assertEquals(provinceDTOExpected, provinceDTOActual,
                        () -> "should return province dto: " + provinceDTOExpected + ", but was: "
                                + provinceDTOActual),
                () -> assertEquals(provinceDTOExpected.getName(), provinceDTOActual.getName(),
                        () -> "should return province dto name: " + provinceDTOExpected.getName() + ", but was: "
                                + provinceDTOActual.getName()),
                () -> assertNotNull(provinceDTOActual.getCountry(),
                        () -> "should return province dto with not null country, but was: null"),
                () -> assertEquals(countryDTOExpected, provinceDTOActual.getCountry(),
                        () -> "should return province dto with country: " + countryDTOExpected + ", but was: " +
                                provinceDTOActual.getCountry()),
                () -> assertEquals(countryDTOExpected.getName(), provinceDTOActual.getCountry().getName(),
                        () -> "should return province dto with country name: " + countryDTOExpected.getName()
                                + ", but was: " + provinceDTOActual.getCountry()),
                () -> verify(modelMapper, times(1)).map(provinceNode, ProvinceDTO.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @Test
    void when_map_province_node_to_model_should_return_model() {

        RegionNode regionNode = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNode = (CountryNode) countryBuilder.withRegion(regionNode).build(ObjectType.NODE);
        ProvinceNode provinceNode = (ProvinceNode) provinceBuilder.withCountry(countryNode)
                .build(ObjectType.NODE);

        RegionModel regionModelExpected = (RegionModel) regionBuilder.build(ObjectType.MODEL);
        CountryModel countryModelExpected = (CountryModel) countryBuilder.withRegion(regionModelExpected).build(ObjectType.MODEL);
        ProvinceModel provinceModelExpected = (ProvinceModel) provinceBuilder.withCountry(countryModelExpected)
                .build(ObjectType.MODEL);

        when(modelMapper.map(provinceNode, ProvinceModel.class)).thenReturn(provinceModelExpected);

        ProvinceModel provinceModelActual = objectMapper.map(provinceNode, ProvinceModel.class);

        assertAll(
                () -> assertNotNull(provinceModelActual, () -> "should return not null province, but was: null"),
                () -> assertEquals(provinceModelExpected, provinceModelActual,
                        () -> "should return province model: " + provinceModelExpected + ", but was: "
                                + provinceModelActual),
                () -> assertEquals(provinceModelExpected.getId(), provinceModelActual.getId(),
                        () -> "should return province model id: " + provinceModelExpected.getId()
                                + ", but was: " + provinceModelActual.getId()),
                () -> assertEquals(provinceModelExpected.getName(), provinceModelActual.getName(),
                        () -> "should return province model name: " + provinceModelExpected.getName() + ", but was: "
                                + provinceModelActual.getName()),
                () -> assertNotNull(provinceModelActual.getCountry(),
                        () -> "should return province model with not null country, but was: null"),
                () -> assertEquals(countryModelExpected, provinceModelActual.getCountry(),
                        () -> "should return province model with country: " + countryModelExpected + ", but was: " +
                                provinceModelActual.getCountry()),
                () -> assertEquals(countryModelExpected.getId(), provinceModelActual.getCountry().getId(),
                        () -> "should return province model with country id: " + countryModelExpected.getId()
                                + ", but was: " + provinceModelActual.getCountry().getId()),
                () -> assertEquals(countryModelExpected.getName(), provinceModelActual.getCountry().getName(),
                        () -> "should return province model with country name: " + countryModelExpected.getName()
                                + ", but was: " + provinceModelActual.getCountry()),
                () -> assertNotNull(provinceModelActual.getCountry().getRegion(),
                        () -> "should return province model with not null region, but was: null"),
                () -> assertEquals(regionModelExpected, provinceModelActual.getCountry().getRegion(),
                        () -> "should return province model with region: " + regionModelExpected + ", but was: "
                                + provinceModelActual.getCountry().getRegion()),
                () -> assertEquals(regionModelExpected.getId(), provinceModelActual.getCountry().getRegion().getId(),
                        () -> "should return province model with region id: " + regionModelExpected.getId()
                                + ", but was: " + provinceModelActual.getCountry().getRegion().getId()),
                () -> assertEquals(regionModelExpected.getName(), provinceModelActual.getCountry().getRegion().getName(),
                        () -> "should return province model with region name: " + regionModelExpected.getName() + ", but was: "
                                + provinceModelActual.getCountry().getRegion().getName()),
                () -> verify(modelMapper, times(1)).map(provinceNode, ProvinceModel.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }
}