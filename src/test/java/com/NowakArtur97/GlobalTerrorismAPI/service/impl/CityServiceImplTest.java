package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.CityDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.CountryDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.ProvinceDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.CityNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.ProvinceNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.RegionNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.CityRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.CityService;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.ProvinceService;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("CityServiceImpl_Tests")
class CityServiceImplTest {

    private final int DEFAULT_DEPTH_FOR_CITY_NODE = 2;

    private CityService cityService;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ProvinceService provinceService;

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

        cityService = new CityServiceImpl(cityRepository, objectMapper, provinceService);
    }

    @Test
    void when_find_existing_city_by_name_latitude_and_longitude_should_return_city() {

        String cityName = "city";
        Double cityLatitude = 21.0;
        Double cityLongitude = 21.0;

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.withName(cityName).withLatitude(cityLatitude)
                .withLongitude(cityLongitude).withProvince(provinceNodeExpected).build(ObjectType.NODE);

        when(cityRepository.findByNameAndLatitudeAndLongitude(cityName, cityLatitude, cityLongitude,
                DEFAULT_DEPTH_FOR_CITY_NODE))
                .thenReturn(Optional.of(cityNodeExpected));

        Optional<CityNode> cityNodeActualOptional = cityService
                .findByNameAndLatitudeAndLongitude(cityName, cityLatitude, cityLongitude);

        CityNode cityNodeActual = cityNodeActualOptional.get();

        assertAll(() -> assertEquals(cityNodeExpected.getId(), cityNodeActual.getId(),
                () -> "should return city node with id: " + cityNodeExpected.getId()
                        + ", but was: " + cityNodeActual.getId()),
                () -> assertEquals(cityNodeExpected.getName(), cityNodeActual.getName(),
                        () -> "should return city node with name: " + cityNodeExpected.getName()
                                + ", but was: " + cityNodeExpected.getName()),
                () -> assertEquals(cityNodeExpected.getLatitude(), cityNodeActual.getLatitude(),
                        () -> "should return city node with latitude: " + cityNodeExpected.getLatitude()
                                + ", but was: " + cityNodeActual.getLatitude()),
                () -> assertEquals(cityNodeExpected.getLongitude(), cityNodeActual.getLongitude(),
                        () -> "should return city node with longitude: " + cityNodeExpected.getLongitude()
                                + ", but was: " + cityNodeActual.getLongitude()),

                () -> assertNotNull(cityNodeActual.getProvince(),
                        () -> "should return event node with not null province, but was: null"),
                () -> assertEquals(provinceNodeExpected, cityNodeActual.getProvince(),
                        () -> "should return event node with province: " + provinceNodeExpected + ", but was: "
                                + cityNodeActual.getProvince()),
                () -> assertEquals(provinceNodeExpected.getId(), cityNodeActual.getProvince().getId(),
                        () -> "should return event node with province id: " + provinceNodeExpected.getId()
                                + ", but was: " + cityNodeActual.getProvince().getId()),
                () -> assertEquals(provinceNodeExpected.getName(), cityNodeActual.getProvince().getName(),
                        () -> "should return event node with province name: " + provinceNodeExpected.getName() + ", but was: "
                                + cityNodeActual.getProvince().getName()),
                () -> assertEquals(countryNodeExpected, cityNodeActual.getProvince().getCountry(),
                        () -> "should return event node with country: " + countryNodeExpected + ", but was: " + cityNodeActual.getProvince().getCountry()),
                () -> assertEquals(countryNodeExpected.getId(), cityNodeActual.getProvince().getCountry().getId(),
                        () -> "should return event node with country id: " + countryNodeExpected.getId()
                                + ", but was: " + cityNodeActual.getProvince().getCountry().getId()),
                () -> assertEquals(countryNodeExpected.getName(), cityNodeActual.getProvince().getCountry().getName(),
                        () -> "should return event node with country name: " + countryNodeExpected.getName()
                                + ", but was: " + cityNodeActual.getProvince().getCountry()),
                () -> assertNotNull(cityNodeActual.getProvince().getCountry().getRegion(),
                        () -> "should return event node with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected, cityNodeActual.getProvince().getCountry().getRegion(),
                        () -> "should return event node with region: " + regionNodeExpected + ", but was: "
                                + cityNodeActual.getProvince().getCountry().getRegion()),
                () -> assertEquals(regionNodeExpected.getId(), cityNodeActual.getProvince().getCountry().getRegion().getId(),
                        () -> "should return event node with region id: " + regionNodeExpected.getId()
                                + ", but was: " + cityNodeActual.getProvince().getCountry().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), cityNodeActual.getProvince().getCountry().getRegion().getName(),
                        () -> "should return event node with region name: " + regionNodeExpected.getName() + ", but was: "
                                + cityNodeActual.getProvince().getCountry().getRegion().getName()),
                () -> verify(cityRepository, times(1))
                        .findByNameAndLatitudeAndLongitude(cityName, cityLatitude, cityLongitude, DEFAULT_DEPTH_FOR_CITY_NODE),
                () -> verifyNoMoreInteractions(cityRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(provinceService));
    }

    @Test
    void when_find_not_existing_city_by_name_latitude_and_longitude_should_return_empty_optional() {

        String cityName = "city";
        Double cityLatitude = 21.0;
        Double cityLongitude = 21.0;

        when(cityRepository.findByNameAndLatitudeAndLongitude(cityName, cityLatitude, cityLongitude,
                DEFAULT_DEPTH_FOR_CITY_NODE)).thenReturn(Optional.empty());

        Optional<CityNode> cityNodeActualOptional = cityService
                .findByNameAndLatitudeAndLongitude(cityName, cityLatitude, cityLongitude);

        assertAll(() -> assertTrue(cityNodeActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(cityRepository, times(1))
                        .findByNameAndLatitudeAndLongitude(cityName, cityLatitude, cityLongitude, DEFAULT_DEPTH_FOR_CITY_NODE),
                () -> verifyNoMoreInteractions(cityRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(provinceService));
    }

    @Test
    void when_save_city_should_return_saved_city() {

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpectedBeforeSave = (CityNode) cityBuilder.withId(null).withProvince(provinceNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.withProvince(provinceNodeExpected).build(ObjectType.NODE);

        when(provinceService.findByNameAndCountryName(provinceNodeExpected.getName(), countryNodeExpected.getName()))
                .thenReturn(Optional.of(provinceNodeExpected));
        when(cityRepository.save(cityNodeExpectedBeforeSave)).thenReturn(cityNodeExpected);

        CityNode cityNodeActual = cityService.save(cityNodeExpected);

        assertAll(() -> assertEquals(cityNodeExpected.getId(), cityNodeActual.getId(),
                () -> "should return city node with id: " + cityNodeExpected.getId()
                        + ", but was: " + cityNodeActual.getId()),
                () -> assertEquals(cityNodeExpected.getName(), cityNodeActual.getName(),
                        () -> "should return city node with name: " + cityNodeExpected.getName()
                                + ", but was: " + cityNodeExpected.getName()),
                () -> assertEquals(cityNodeExpected.getLatitude(), cityNodeActual.getLatitude(),
                        () -> "should return city node with latitude: " + cityNodeExpected.getLatitude()
                                + ", but was: " + cityNodeActual.getLatitude()),
                () -> assertEquals(cityNodeExpected.getLongitude(), cityNodeActual.getLongitude(),
                        () -> "should return city node with longitude: " + cityNodeExpected.getLongitude()
                                + ", but was: " + cityNodeActual.getLongitude()),

                () -> assertNotNull(cityNodeActual.getProvince(),
                        () -> "should return event node with not null province, but was: null"),
                () -> assertEquals(provinceNodeExpected, cityNodeActual.getProvince(),
                        () -> "should return event node with province: " + provinceNodeExpected + ", but was: "
                                + cityNodeActual.getProvince()),
                () -> assertEquals(provinceNodeExpected.getId(), cityNodeActual.getProvince().getId(),
                        () -> "should return event node with province id: " + provinceNodeExpected.getId()
                                + ", but was: " + cityNodeActual.getProvince().getId()),
                () -> assertEquals(provinceNodeExpected.getName(), cityNodeActual.getProvince().getName(),
                        () -> "should return event node with province name: " + provinceNodeExpected.getName() + ", but was: "
                                + cityNodeActual.getProvince().getName()),
                () -> assertEquals(countryNodeExpected, cityNodeActual.getProvince().getCountry(),
                        () -> "should return event node with country: " + countryNodeExpected + ", but was: " + cityNodeActual.getProvince().getCountry()),
                () -> assertEquals(countryNodeExpected.getId(), cityNodeActual.getProvince().getCountry().getId(),
                        () -> "should return event node with country id: " + countryNodeExpected.getId()
                                + ", but was: " + cityNodeActual.getProvince().getCountry().getId()),
                () -> assertEquals(countryNodeExpected.getName(), cityNodeActual.getProvince().getCountry().getName(),
                        () -> "should return event node with country name: " + countryNodeExpected.getName()
                                + ", but was: " + cityNodeActual.getProvince().getCountry()),
                () -> assertNotNull(cityNodeActual.getProvince().getCountry().getRegion(),
                        () -> "should return event node with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected, cityNodeActual.getProvince().getCountry().getRegion(),
                        () -> "should return event node with region: " + regionNodeExpected + ", but was: "
                                + cityNodeActual.getProvince().getCountry().getRegion()),
                () -> assertEquals(regionNodeExpected.getId(), cityNodeActual.getProvince().getCountry().getRegion().getId(),
                        () -> "should return event node with region id: " + regionNodeExpected.getId()
                                + ", but was: " + cityNodeActual.getProvince().getCountry().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), cityNodeActual.getProvince().getCountry().getRegion().getName(),
                        () -> "should return event node with region name: " + regionNodeExpected.getName() + ", but was: "
                                + cityNodeActual.getProvince().getCountry().getRegion().getName()),
                () -> verify(provinceService, times(1))
                        .findByNameAndCountryName(provinceNodeExpected.getName(), countryNodeExpected.getName()),
                () -> verifyNoMoreInteractions(provinceService),
                () -> verify(cityRepository, times(1)).save(cityNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(cityRepository),
                () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_save_new_city_should_return_new_city() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpectedBeforeSave = (CityNode) cityBuilder.withId(null).withProvince(provinceNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.withProvince(provinceNodeExpected).build(ObjectType.NODE);

        when(objectMapper.map(cityDTO, CityNode.class)).thenReturn(cityNodeExpectedBeforeSave);
        when(provinceService.findByNameAndCountryName(provinceNodeExpected.getName(), countryNodeExpected.getName()))
                .thenReturn(Optional.empty());
        when(provinceService.saveNew(provinceDTO)).thenReturn(provinceNodeExpected);
        when(cityRepository.save(cityNodeExpectedBeforeSave)).thenReturn(cityNodeExpected);

        CityNode cityNodeActual = cityService.saveNew(cityDTO);

        assertAll(() -> assertEquals(cityNodeExpected.getId(), cityNodeActual.getId(),
                () -> "should return city node with id: " + cityNodeExpected.getId()
                        + ", but was: " + cityNodeActual.getId()),
                () -> assertEquals(cityNodeExpected.getName(), cityNodeActual.getName(),
                        () -> "should return city node with name: " + cityNodeExpected.getName()
                                + ", but was: " + cityNodeExpected.getName()),
                () -> assertEquals(cityNodeExpected.getLatitude(), cityNodeActual.getLatitude(),
                        () -> "should return city node with latitude: " + cityNodeExpected.getLatitude()
                                + ", but was: " + cityNodeActual.getLatitude()),
                () -> assertEquals(cityNodeExpected.getLongitude(), cityNodeActual.getLongitude(),
                        () -> "should return city node with longitude: " + cityNodeExpected.getLongitude()
                                + ", but was: " + cityNodeActual.getLongitude()),

                () -> assertNotNull(cityNodeActual.getProvince(),
                        () -> "should return event node with not null province, but was: null"),
                () -> assertEquals(provinceNodeExpected, cityNodeActual.getProvince(),
                        () -> "should return event node with province: " + provinceNodeExpected + ", but was: "
                                + cityNodeActual.getProvince()),
                () -> assertEquals(provinceNodeExpected.getId(), cityNodeActual.getProvince().getId(),
                        () -> "should return event node with province id: " + provinceNodeExpected.getId()
                                + ", but was: " + cityNodeActual.getProvince().getId()),
                () -> assertEquals(provinceNodeExpected.getName(), cityNodeActual.getProvince().getName(),
                        () -> "should return event node with province name: " + provinceNodeExpected.getName() + ", but was: "
                                + cityNodeActual.getProvince().getName()),
                () -> assertEquals(countryNodeExpected, cityNodeActual.getProvince().getCountry(),
                        () -> "should return event node with country: " + countryNodeExpected + ", but was: " + cityNodeActual.getProvince().getCountry()),
                () -> assertEquals(countryNodeExpected.getId(), cityNodeActual.getProvince().getCountry().getId(),
                        () -> "should return event node with country id: " + countryNodeExpected.getId()
                                + ", but was: " + cityNodeActual.getProvince().getCountry().getId()),
                () -> assertEquals(countryNodeExpected.getName(), cityNodeActual.getProvince().getCountry().getName(),
                        () -> "should return event node with country name: " + countryNodeExpected.getName()
                                + ", but was: " + cityNodeActual.getProvince().getCountry()),
                () -> assertNotNull(cityNodeActual.getProvince().getCountry().getRegion(),
                        () -> "should return event node with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected, cityNodeActual.getProvince().getCountry().getRegion(),
                        () -> "should return event node with region: " + regionNodeExpected + ", but was: "
                                + cityNodeActual.getProvince().getCountry().getRegion()),
                () -> assertEquals(regionNodeExpected.getId(), cityNodeActual.getProvince().getCountry().getRegion().getId(),
                        () -> "should return event node with region id: " + regionNodeExpected.getId()
                                + ", but was: " + cityNodeActual.getProvince().getCountry().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), cityNodeActual.getProvince().getCountry().getRegion().getName(),
                        () -> "should return event node with region name: " + regionNodeExpected.getName() + ", but was: "
                                + cityNodeActual.getProvince().getCountry().getRegion().getName()),
                () -> verify(provinceService, times(1))
                        .findByNameAndCountryName(provinceNodeExpected.getName(), countryNodeExpected.getName()),
                () -> verify(provinceService, times(1)).saveNew(provinceDTO),
                () -> verifyNoMoreInteractions(provinceService),
                () -> verify(cityRepository, times(1)).save(cityNodeExpected),
                () -> verifyNoMoreInteractions(cityRepository),
                () -> verify(objectMapper, times(1)).map(cityDTO, CityNode.class),
                () -> verifyNoMoreInteractions(objectMapper));
    }
}
