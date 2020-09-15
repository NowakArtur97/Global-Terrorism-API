package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.CityDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.CityNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.CityRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.CityService;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
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
    void when_cities_exist_and_return_all_cities_should_return_cities() {

        List<CityNode> citiesListExpected = new ArrayList<>();

        CityNode city1 = (CityNode) cityBuilder.withName("city1").build(ObjectType.NODE);
        CityNode city2 = (CityNode) cityBuilder.withName("city2").build(ObjectType.NODE);
        CityNode city3 = (CityNode) cityBuilder.withName("city2").build(ObjectType.NODE);

        citiesListExpected.add(city1);
        citiesListExpected.add(city2);
        citiesListExpected.add(city3);

        Page<CityNode> citiesExpected = new PageImpl<>(citiesListExpected);

        Pageable pageable = PageRequest.of(0, 100);

        when(cityRepository.findAll(pageable)).thenReturn(citiesExpected);

        Page<CityNode> citiesActual = cityService.findAll(pageable);

        assertAll(() -> assertNotNull(citiesActual, () -> "shouldn't return null"),
                () -> assertEquals(citiesListExpected, citiesActual.getContent(),
                        () -> "should contain: " + citiesListExpected + ", but was: " + citiesActual.getContent()),
                () -> assertEquals(citiesExpected.getNumberOfElements(), citiesActual.getNumberOfElements(),
                        () -> "should return page with: " + citiesExpected.getNumberOfElements()
                                + " elements, but was: " + citiesActual.getNumberOfElements()),
                () -> verify(cityRepository, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(cityRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(provinceService));
    }

    @Test
    void when_cities_not_exist_and_return_all_cities_should_not_return_any_cities() {

        List<CityNode> citiesListExpected = new ArrayList<>();

        Page<CityNode> citiesExpected = new PageImpl<>(citiesListExpected);

        Pageable pageable = PageRequest.of(0, 100);

        when(cityRepository.findAll(pageable)).thenReturn(citiesExpected);

        Page<CityNode> citiesActual = cityService.findAll(pageable);

        assertAll(() -> assertNotNull(citiesActual, () -> "shouldn't return null"),
                () -> assertEquals(citiesListExpected, citiesActual.getContent(),
                        () -> "should contain empty list, but was: " + citiesActual.getContent()),
                () -> assertEquals(citiesListExpected, citiesActual.getContent(),
                        () -> "should contain: " + citiesListExpected + ", but was: " + citiesActual.getContent()),
                () -> assertEquals(citiesExpected.getNumberOfElements(), citiesActual.getNumberOfElements(),
                        () -> "should return empty page, but was: " + citiesActual.getNumberOfElements()),
                () -> verify(cityRepository, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(cityRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(provinceService));
    }

    @Test
    void when_find_existing_city_by_id_should_return_city() {

        long cityId = 1L;

        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.withId(cityId).withProvince(provinceNodeExpected)
                .build(ObjectType.NODE);

        when(cityRepository.findById(cityId)).thenReturn(Optional.of(cityNodeExpected));

        Optional<CityNode> cityNodeActualOptional = cityService.findById(cityId);

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
                () -> assertNull(cityNodeActual.getProvince().getCountry(),
                        () -> "should return city node with null region, but was: " + cityNodeActual.getProvince().getCountry()),
                () -> verify(cityRepository, times(1)).findById(cityId),
                () -> verifyNoMoreInteractions(cityRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(provinceService));
    }

    @Test
    void when_find_not_existing_city_by_id_should_return_empty_optional() {

        long cityId = 1L;

        when(cityRepository.findById(cityId)).thenReturn(Optional.empty());

        Optional<CityNode> cityNodeActualOptional = cityService.findById(cityId);

        assertAll(() -> assertTrue(cityNodeActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(cityRepository, times(1)).findById(cityId),
                () -> verifyNoMoreInteractions(cityRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(provinceService));
    }

    @Test
    void when_find_existing_city_by_id_with_depth_should_return_city() {

        long cityId = 1L;

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.withId(cityId).withProvince(provinceNodeExpected)
                .build(ObjectType.NODE);

        when(cityRepository.findById(cityId, DEFAULT_DEPTH_FOR_CITY_NODE)).thenReturn(Optional.of(cityNodeExpected));

        Optional<CityNode> cityNodeActualOptional = cityService.findById(cityId, DEFAULT_DEPTH_FOR_CITY_NODE);

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
                () -> assertEquals(countryNodeExpected, cityNodeActual.getProvince().getCountry(),
                        () -> "should return city node with country: " + countryNodeExpected + ", but was: " + cityNodeActual.getProvince().getCountry()),
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
                () -> verify(cityRepository, times(1)).findById(cityId, DEFAULT_DEPTH_FOR_CITY_NODE),
                () -> verifyNoMoreInteractions(cityRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(provinceService));
    }

    @Test
    void when_find_not_existing_city_by_id_with_depth_should_return_empty_optional() {

        long cityId = 1L;

        when(cityRepository.findById(cityId, DEFAULT_DEPTH_FOR_CITY_NODE)).thenReturn(Optional.empty());

        Optional<CityNode> cityNodeActualOptional = cityService.findById(cityId, DEFAULT_DEPTH_FOR_CITY_NODE);

        assertAll(() -> assertTrue(cityNodeActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(cityRepository, times(1)).findById(cityId, DEFAULT_DEPTH_FOR_CITY_NODE),
                () -> verifyNoMoreInteractions(cityRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(provinceService));
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
                () -> assertEquals(countryNodeExpected, cityNodeActual.getProvince().getCountry(),
                        () -> "should return city node with country: " + countryNodeExpected + ", but was: " + cityNodeActual.getProvince().getCountry()),
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
    void when_save_city_with_not_existing_province_should_return_saved_city_with_new_province() {

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeBeforeSave = (ProvinceNode) provinceBuilder.withId(null).withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpectedBeforeSave = (CityNode) cityBuilder.withId(null).withProvince(provinceNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.withProvince(provinceNodeExpected).build(ObjectType.NODE);

        when(provinceService.findByNameAndCountryName(provinceNodeExpected.getName(), countryNodeExpected.getName()))
                .thenReturn(Optional.empty());
        when(provinceService.save(provinceNodeBeforeSave)).thenReturn(provinceNodeExpected);
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
                () -> assertEquals(countryNodeExpected, cityNodeActual.getProvince().getCountry(),
                        () -> "should return city node with country: " + countryNodeExpected + ", but was: " + cityNodeActual.getProvince().getCountry()),
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
                () -> verify(provinceService, times(1))
                        .findByNameAndCountryName(provinceNodeExpected.getName(), countryNodeExpected.getName()),
                () -> verify(provinceService, times(1)).save(provinceNodeBeforeSave),
                () -> verifyNoMoreInteractions(provinceService),
                () -> verify(cityRepository, times(1)).save(cityNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(cityRepository),
                () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_save_city_with_existing_province_should_return_saved_city() {

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
                () -> assertEquals(countryNodeExpected, cityNodeActual.getProvince().getCountry(),
                        () -> "should return city node with country: " + countryNodeExpected + ", but was: " + cityNodeActual.getProvince().getCountry()),
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
                () -> verify(provinceService, times(1))
                        .findByNameAndCountryName(provinceNodeExpected.getName(), countryNodeExpected.getName()),
                () -> verifyNoMoreInteractions(provinceService),
                () -> verify(cityRepository, times(1)).save(cityNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(cityRepository),
                () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_save_new_city_with_not_existing_province_should_return_new_city_with_new_province() {

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
                () -> assertEquals(countryNodeExpected, cityNodeActual.getProvince().getCountry(),
                        () -> "should return city node with country: " + countryNodeExpected + ", but was: " + cityNodeActual.getProvince().getCountry()),
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
                () -> verify(provinceService, times(1))
                        .findByNameAndCountryName(provinceNodeExpected.getName(), countryNodeExpected.getName()),
                () -> verify(provinceService, times(1)).saveNew(provinceDTO),
                () -> verifyNoMoreInteractions(provinceService),
                () -> verify(cityRepository, times(1)).save(cityNodeExpected),
                () -> verifyNoMoreInteractions(cityRepository),
                () -> verify(objectMapper, times(1)).map(cityDTO, CityNode.class),
                () -> verifyNoMoreInteractions(objectMapper));
    }

    @Test
    void when_save_new_city_with_existing_province_should_return_new_city() {

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
                .thenReturn(Optional.of(provinceNodeExpected));
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
                () -> assertEquals(countryNodeExpected, cityNodeActual.getProvince().getCountry(),
                        () -> "should return city node with country: " + countryNodeExpected + ", but was: " + cityNodeActual.getProvince().getCountry()),
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
                () -> verify(provinceService, times(1))
                        .findByNameAndCountryName(provinceNodeExpected.getName(), countryNodeExpected.getName()),
                () -> verifyNoMoreInteractions(provinceService),
                () -> verify(cityRepository, times(1)).save(cityNodeExpected),
                () -> verifyNoMoreInteractions(cityRepository),
                () -> verify(objectMapper, times(1)).map(cityDTO, CityNode.class),
                () -> verifyNoMoreInteractions(objectMapper));
    }

    @Test
    void when_update_city_with_not_existing_province_should_return_updated_city_with_new_province() {

        String updatedCityName = "updated city";
        Double updatedCityLatitude = 11.0;
        Double updatedCityLongitude = 1.0;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withName(updatedCityName).withLatitude(updatedCityLatitude)
                .withLongitude(updatedCityLongitude).withProvince(provinceDTO).build(ObjectType.DTO);

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeToUpdate = (CityNode) cityBuilder.withProvince(provinceNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpectedBeforeSave = (CityNode) cityBuilder.withId(null).withName(updatedCityName)
                .withLatitude(updatedCityLatitude).withLongitude(updatedCityLongitude).withProvince(provinceNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.withName(updatedCityName)
                .withLatitude(updatedCityLatitude).withLongitude(updatedCityLongitude)
                .withProvince(provinceNodeExpected).build(ObjectType.NODE);

        when(objectMapper.map(cityDTO, CityNode.class)).thenReturn(cityNodeExpectedBeforeSave);
        when(provinceService.findByNameAndCountryName(provinceNodeExpected.getName(), countryNodeExpected.getName()))
                .thenReturn(Optional.empty());
        when(provinceService.update(provinceNodeExpected, provinceDTO)).thenReturn(provinceNodeExpected);
        when(cityRepository.save(cityNodeExpectedBeforeSave)).thenReturn(cityNodeExpected);

        CityNode cityNodeActual = cityService.update(cityNodeToUpdate, cityDTO);

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
                () -> assertEquals(countryNodeExpected, cityNodeActual.getProvince().getCountry(),
                        () -> "should return city node with country: " + countryNodeExpected + ", but was: " + cityNodeActual.getProvince().getCountry()),
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
                () -> verify(provinceService, times(1))
                        .findByNameAndCountryName(provinceNodeExpected.getName(), countryNodeExpected.getName()),
                () -> verify(provinceService, times(1)).update(provinceNodeExpected, provinceDTO),
                () -> verifyNoMoreInteractions(provinceService),
                () -> verify(cityRepository, times(1)).save(cityNodeExpected),
                () -> verifyNoMoreInteractions(cityRepository),
                () -> verify(objectMapper, times(1)).map(cityDTO, CityNode.class),
                () -> verifyNoMoreInteractions(objectMapper));
    }

    @Test
    void when_update_city_with_existing_province_should_return_updated_city() {

        String updatedCityName = "updated city";
        Double updatedCityLatitude = 11.0;
        Double updatedCityLongitude = 1.0;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withName(updatedCityName).withLatitude(updatedCityLatitude)
                .withLongitude(updatedCityLongitude).withProvince(provinceDTO).build(ObjectType.DTO);

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeToUpdate = (CityNode) cityBuilder.withProvince(provinceNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpectedBeforeSave = (CityNode) cityBuilder.withId(null).withName(updatedCityName)
                .withLatitude(updatedCityLatitude).withLongitude(updatedCityLongitude).withProvince(provinceNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.withName(updatedCityName)
                .withLatitude(updatedCityLatitude).withLongitude(updatedCityLongitude)
                .withProvince(provinceNodeExpected).build(ObjectType.NODE);

        when(objectMapper.map(cityDTO, CityNode.class)).thenReturn(cityNodeExpectedBeforeSave);
        when(provinceService.findByNameAndCountryName(provinceNodeExpected.getName(), countryNodeExpected.getName()))
                .thenReturn(Optional.of(provinceNodeExpected));
        when(cityRepository.save(cityNodeExpectedBeforeSave)).thenReturn(cityNodeExpected);

        CityNode cityNodeActual = cityService.update(cityNodeToUpdate, cityDTO);

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
                () -> assertEquals(countryNodeExpected, cityNodeActual.getProvince().getCountry(),
                        () -> "should return city node with country: " + countryNodeExpected + ", but was: " + cityNodeActual.getProvince().getCountry()),
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
                () -> verify(provinceService, times(1))
                        .findByNameAndCountryName(provinceNodeExpected.getName(), countryNodeExpected.getName()),
                () -> verifyNoMoreInteractions(provinceService),
                () -> verify(cityRepository, times(1)).save(cityNodeExpected),
                () -> verifyNoMoreInteractions(cityRepository),
                () -> verify(objectMapper, times(1)).map(cityDTO, CityNode.class),
                () -> verifyNoMoreInteractions(objectMapper));
    }

    @Test
    void when_delete_existing_city_by_id_should_return_city() {

        Long cityId = 1L;

        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder .build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.withId(cityId).withProvince(provinceNodeExpected)
                .build(ObjectType.NODE);

        when(cityRepository.findById(cityId)).thenReturn(Optional.of(cityNodeExpected));

        Optional<CityNode> cityNodeActualOptional = cityService.delete(cityId);

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
                () -> assertNull(cityNodeActual.getProvince().getCountry(),
                        () -> "should return city node with null country, but was: " + cityNodeActual.getProvince().getCountry()),
                () -> verify(cityRepository, times(1)).findById(cityId),
                () -> verify(cityRepository, times(1)).delete(cityNodeExpected),
                () -> verifyNoMoreInteractions(cityRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(provinceService));
    }

    @Test
    void when_delete_not_existing_city_by_id_should_return_empty_optional() {

        Long cityId = 1L;

        when(cityRepository.findById(cityId)).thenReturn(Optional.empty());

        Optional<CityNode> cityNodeActualOptional = cityService.delete(cityId);

        assertAll(() -> assertTrue(cityNodeActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(cityRepository, times(1)).findById(cityId),
                () -> verifyNoMoreInteractions(cityRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(provinceService));
    }
}
