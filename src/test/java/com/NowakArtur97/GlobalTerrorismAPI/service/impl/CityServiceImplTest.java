package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.CityDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.CityNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.CityRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.CityService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.CityBuilder;
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
public class CityServiceImplTest {

    private CityService cityService;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private ObjectMapper objectMapper;

    private static CityBuilder cityBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        cityBuilder = new CityBuilder();
    }

    @BeforeEach
    private void setUp() {

        cityService = new CityServiceImpl(cityRepository, objectMapper);
    }

    @Test
    void when_find_existing_city_by_name_should_return_city() {

        String cityName = "city";

        CityNode cityNodeExpected = (CityNode) cityBuilder.withName(cityName).build(ObjectType.NODE);

        when(cityRepository.findByName(cityName)).thenReturn(Optional.of(cityNodeExpected));

        Optional<CityNode> cityNodeActualOptional = cityService.findByName(cityName);

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
                () -> verify(cityRepository, times(1)).findByName(cityName),
                () -> verifyNoMoreInteractions(cityRepository));
    }

    @Test
    void when_target_not_exists_and_return_one_target_should_return_empty_optional() {

        String cityName = "city";

        when(cityRepository.findByName(cityName)).thenReturn(Optional.empty());

        Optional<CityNode> cityNodeActualOptional = cityService.findByName(cityName);

        assertAll(() -> assertTrue(cityNodeActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(cityRepository, times(1)).findByName(cityName),
                () -> verifyNoMoreInteractions(cityRepository));
    }

    @Test
    void when_save_city_should_return_saved_city() {

        CityNode cityNodeExpectedBeforeSave = (CityNode) cityBuilder.withId(null).build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.build(ObjectType.NODE);

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
                () -> verify(cityRepository, times(1)).save(cityNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(cityRepository));
    }

    @Test
    void when_save_new_city_should_return_new_city() {

        CityDTO cityDTO = (CityDTO) cityBuilder.build(ObjectType.DTO);

        CityNode cityNodeExpectedBeforeSave = (CityNode) cityBuilder.withId(null).build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.build(ObjectType.NODE);

        when(objectMapper.map(cityDTO, CityNode.class)).thenReturn(cityNodeExpectedBeforeSave);
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
                () -> verify(cityRepository, times(1)).save(cityNodeExpected),
                () -> verifyNoMoreInteractions(cityRepository),
                () -> verify(objectMapper, times(1)).map(cityDTO, CityNode.class),
                () -> verifyNoMoreInteractions(objectMapper));
    }
}
