package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.CityDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.CountryDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.ProvinceDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.CityModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.CountryModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.ProvinceModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.CityNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.ProvinceNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.CityBuilder;
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
class CityMapperTest {

    private ObjectMapper objectMapper;

    @Mock
    private ModelMapper modelMapper;

    private static RegionBuilder regionBuilder;
    private static CountryBuilder countryBuilder;
    private static ProvinceBuilder provinceBuilder;
    private static CityBuilder cityBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        regionBuilder = new RegionBuilder();
        countryBuilder = new CountryBuilder();
        provinceBuilder = new ProvinceBuilder();
        cityBuilder = new CityBuilder();
    }

    @BeforeEach
    private void setUp() {

        objectMapper = new ObjectMapperImpl(modelMapper);
    }

    @Test
    void when_map_city_dto_to_node_should_return_node() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withId(null).withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.withId(null).withProvince(provinceNodeExpected).build(ObjectType.NODE);

        when(modelMapper.map(cityDTO, CityNode.class)).thenReturn(cityNodeExpected);

        CityNode cityNodeActual = objectMapper.map(cityDTO, CityNode.class);

        assertAll(
                () -> assertNotNull(cityNodeActual,
                        () -> "should return city node with not null city, but was: null"),
                () -> assertNull(cityNodeActual.getId(),
                        () -> "should return city city node with id as null, but was: " + cityNodeActual.getId()),
                () -> assertEquals(cityNodeExpected.getId(), cityNodeActual.getId(),
                        () -> "should return city node with id: " + cityNodeExpected.getId() + ", but was: "
                                + cityNodeActual.getId()),
                () -> assertEquals(cityNodeExpected.getName(), cityNodeActual.getName(),
                        () -> "should return city node with name: " + cityNodeExpected.getName()
                                + ", but was: " + cityNodeActual.getName()),
                () -> assertEquals(cityNodeExpected.getLatitude(), cityNodeActual.getLatitude(),
                        () -> "should return city node with latitude: " + cityNodeExpected.getLatitude()
                                + ", but was: " + cityNodeActual.getLatitude()),
                () -> assertEquals(cityNodeExpected.getLongitude(), cityNodeActual.getLongitude(),
                        () -> "should return city node with longitude: " + cityNodeExpected.getLongitude()
                                + ", but was: " + cityNodeActual.getLongitude()),

                () -> assertNotNull(cityNodeActual.getProvince(),
                        () -> "should return city node with not null province, but was: null"),
                () -> assertEquals(provinceNodeExpected, cityNodeActual.getProvince(),
                        () -> "should return city node with province: " + provinceNodeExpected + ", but was: "
                                + cityNodeActual.getProvince()),
                () -> assertEquals(provinceNodeExpected.getId(), cityNodeActual.getProvince().getId(),
                        () -> "should return city node with province id: " + provinceNodeExpected.getId()
                                + ", but was: " + cityNodeActual.getProvince().getId()),
                () -> assertEquals(provinceNodeExpected.getName(), cityNodeActual.getProvince().getName(),
                        () -> "should return city node with province name: " + provinceNodeExpected.getName() + ", but was: "
                                + cityNodeActual.getProvince().getName()),
                () -> assertNotNull(cityNodeActual.getProvince().getCountry(),
                        () -> "should return city node with not null country, but was: null"),
                () -> assertEquals(countryNodeExpected, cityNodeActual.getProvince().getCountry(),
                        () -> "should return city node with country: " + countryNodeExpected + ", but was: " +
                                cityNodeActual.getProvince().getCountry()),
                () -> assertEquals(countryNodeExpected.getId(), cityNodeActual.getProvince().getCountry().getId(),
                        () -> "should return city node with country id: " + countryNodeExpected.getId()
                                + ", but was: " + cityNodeActual.getProvince().getCountry().getId()),
                () -> assertEquals(countryNodeExpected.getName(), cityNodeActual.getProvince().getCountry().getName(),
                        () -> "should return city node with country name: " + countryNodeExpected.getName()
                                + ", but was: " + cityNodeActual.getProvince().getCountry()),
                () -> assertNotNull(cityNodeActual.getProvince().getCountry().getRegion(),
                        () -> "should return city node with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected, cityNodeActual.getProvince().getCountry().getRegion(),
                        () -> "should return city node with region: " + regionNodeExpected + ", but was: "
                                + cityNodeActual.getProvince().getCountry().getRegion()),
                () -> assertEquals(regionNodeExpected.getId(), cityNodeActual.getProvince().getCountry().getRegion().getId(),
                        () -> "should return city node with region id: " + regionNodeExpected.getId()
                                + ", but was: " + cityNodeActual.getProvince().getCountry().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), cityNodeActual.getProvince().getCountry().getRegion().getName(),
                        () -> "should return city node with region name: " + regionNodeExpected.getName() + ", but was: "
                                + cityNodeActual.getProvince().getCountry().getRegion().getName()),
                () -> verify(modelMapper, times(1)).map(cityDTO, CityNode.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @Test
    void when_map_city_node_to_dto_should_return_dto() {

        CountryNode countryNode = (CountryNode) countryBuilder.build(ObjectType.NODE);
        ProvinceNode provinceNode = (ProvinceNode) provinceBuilder.withCountry(countryNode)
                .build(ObjectType.NODE);
        CityNode cityNode = (CityNode) cityBuilder.withProvince(provinceNode).build(ObjectType.NODE);

        CountryDTO countryDTOExpected = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        ProvinceDTO provinceDTOExpected = (ProvinceDTO) provinceBuilder.withCountry(countryDTOExpected).build(ObjectType.DTO);
        CityDTO cityDTOExpected = (CityDTO) cityBuilder.withProvince(provinceDTOExpected).build(ObjectType.DTO);

        when(modelMapper.map(cityNode, CityDTO.class)).thenReturn(cityDTOExpected);

        CityDTO cityDTOActual = objectMapper.map(cityNode, CityDTO.class);

        assertAll(() -> assertNotNull(cityDTOActual,
                () -> "should return city dto dto not null city, but was: null"),
                () -> assertEquals(cityDTOExpected.getName(), cityDTOActual.getName(),
                        () -> "should return city dto with name: " + cityDTOExpected.getName()
                                + ", but was: " + cityDTOActual.getName()),
                () -> assertEquals(cityDTOExpected.getLatitude(), cityDTOActual.getLatitude(),
                        () -> "should return city dto with latitude: " + cityDTOExpected.getLatitude()
                                + ", but was: " + cityDTOActual.getLatitude()),
                () -> assertEquals(cityDTOExpected.getLongitude(), cityDTOActual.getLongitude(),
                        () -> "should return city dto with longitude: " + cityDTOExpected.getLongitude()
                                + ", but was: " + cityDTOActual.getLongitude()),

                () -> assertNotNull(cityDTOActual.getProvince(),
                        () -> "should return city dto with not null province, but was: null"),
                () -> assertEquals(provinceDTOExpected, cityDTOActual.getProvince(),
                        () -> "should return city dto with province: " + provinceDTOExpected + ", but was: "
                                + cityDTOActual.getProvince()),
                () -> assertEquals(provinceDTOExpected.getName(), cityDTOActual.getProvince().getName(),
                        () -> "should return city dto with province name: " + provinceDTOExpected.getName() + ", but was: "
                                + cityDTOActual.getProvince().getName()),
                () -> assertNotNull(cityDTOActual.getProvince().getCountry(),
                        () -> "should return city dto with not null country, but was: null"),
                () -> assertEquals(countryDTOExpected, cityDTOActual.getProvince().getCountry(),
                        () -> "should return city dto with country: " + countryDTOExpected + ", but was: " +
                                cityDTOActual.getProvince().getCountry()),
                () -> assertEquals(countryDTOExpected.getName(), cityDTOActual.getProvince().getCountry().getName(),
                        () -> "should return city dto with country name: " + countryDTOExpected.getName()
                                + ", but was: " + cityDTOActual.getProvince().getCountry()),
                () -> verify(modelMapper, times(1)).map(cityNode, CityDTO.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @Test
    void when_map_city_node_to_model_should_return_model() {

        RegionNode regionNode = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNode = (CountryNode) countryBuilder.withRegion(regionNode).build(ObjectType.NODE);
        ProvinceNode provinceNode = (ProvinceNode) provinceBuilder.withCountry(countryNode)
                .build(ObjectType.NODE);
        CityNode cityNode = (CityNode) cityBuilder.withProvince(provinceNode).build(ObjectType.NODE);

        RegionModel regionModelExpected = (RegionModel) regionBuilder.build(ObjectType.MODEL);
        CountryModel countryModelExpected = (CountryModel) countryBuilder.withRegion(regionModelExpected).build(ObjectType.MODEL);
        ProvinceModel provinceModelExpected = (ProvinceModel) provinceBuilder.withCountry(countryModelExpected)
                .build(ObjectType.MODEL);
        CityModel cityModelExpected = (CityModel) cityBuilder.withProvince(provinceModelExpected).build(ObjectType.MODEL);

        when(modelMapper.map(cityNode, CityModel.class)).thenReturn(cityModelExpected);

        CityModel cityModelActual = objectMapper.map(cityNode, CityModel.class);

        assertAll(
                () -> assertNotNull(cityModelActual,
                        () -> "should return city model not null city, but was: null"),
                () -> assertEquals(cityModelExpected.getId(), cityModelActual.getId(),
                        () -> "should return city model with id: " + cityModelExpected.getId() + ", but was: "
                                + cityModelActual.getId()),
                () -> assertEquals(cityModelExpected.getName(), cityModelActual.getName(),
                        () -> "should return city model with name: " + cityModelExpected.getName()
                                + ", but was: " + cityModelActual.getName()),
                () -> assertEquals(cityModelExpected.getLatitude(), cityModelActual.getLatitude(),
                        () -> "should return city model with latitude: " + cityModelExpected.getLatitude()
                                + ", but was: " + cityModelActual.getLatitude()),
                () -> assertEquals(cityModelExpected.getLongitude(), cityModelActual.getLongitude(),
                        () -> "should return city model with longitude: " + cityModelExpected.getLongitude()
                                + ", but was: " + cityModelActual.getLongitude()),

                () -> assertNotNull(cityModelActual.getProvince(),
                        () -> "should return city model with not null province, but was: null"),
                () -> assertEquals(provinceModelExpected, cityModelActual.getProvince(),
                        () -> "should return city model with province: " + provinceModelExpected + ", but was: "
                                + cityModelActual.getProvince()),
                () -> assertEquals(provinceModelExpected.getId(), cityModelActual.getProvince().getId(),
                        () -> "should return city model with province id: " + provinceModelExpected.getId()
                                + ", but was: " + cityModelActual.getProvince().getId()),
                () -> assertEquals(provinceModelExpected.getName(), cityModelActual.getProvince().getName(),
                        () -> "should return city model with province name: " + provinceModelExpected.getName() + ", but was: "
                                + cityModelActual.getProvince().getName()),
                () -> assertNotNull(cityModelActual.getProvince().getCountry(),
                        () -> "should return city model with not null country, but was: null"),
                () -> assertEquals(countryModelExpected, cityModelActual.getProvince().getCountry(),
                        () -> "should return city model with country: " + countryModelExpected + ", but was: " +
                                cityModelActual.getProvince().getCountry()),
                () -> assertEquals(countryModelExpected.getId(), cityModelActual.getProvince().getCountry().getId(),
                        () -> "should return city model with country id: " + countryModelExpected.getId()
                                + ", but was: " + cityModelActual.getProvince().getCountry().getId()),
                () -> assertEquals(countryModelExpected.getName(), cityModelActual.getProvince().getCountry().getName(),
                        () -> "should return city model with country name: " + countryModelExpected.getName()
                                + ", but was: " + cityModelActual.getProvince().getCountry()),
                () -> assertNotNull(cityModelActual.getProvince().getCountry().getRegion(),
                        () -> "should return city model with not null region, but was: null"),
                () -> assertEquals(regionModelExpected, cityModelActual.getProvince().getCountry().getRegion(),
                        () -> "should return city model with region: " + regionModelExpected + ", but was: "
                                + cityModelActual.getProvince().getCountry().getRegion()),
                () -> assertEquals(regionModelExpected.getId(), cityModelActual.getProvince().getCountry().getRegion().getId(),
                        () -> "should return city model with region id: " + regionModelExpected.getId()
                                + ", but was: " + cityModelActual.getProvince().getCountry().getRegion().getId()),
                () -> assertEquals(regionModelExpected.getName(), cityModelActual.getProvince().getCountry().getRegion().getName(),
                        () -> "should return city model with region name: " + regionModelExpected.getName() + ", but was: "
                                + cityModelActual.getProvince().getCountry().getRegion().getName()),
                () -> verify(modelMapper, times(1)).map(cityNode, CityModel.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }
}