package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.CountryDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.ProvinceDTO;
import com.NowakArtur97.GlobalTerrorismAPI.exception.ResourceNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.ProvinceNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.RegionNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.ProvinceRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.CountryService;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.ProvinceService;
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
@Tag("ProvinceServiceImpl_Tests")
class ProvinceServiceImplTest {

    private final int DEFAULT_DEPTH_FOR_PROVINCE_NODE = 2;

    private ProvinceService provinceService;

    @Mock
    private ProvinceRepository provinceRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CountryService countryService;

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

        provinceService = new ProvinceServiceImpl(provinceRepository, objectMapper, countryService);
    }

    @Test
    void when_find_existing_province_by_name_and_country_name_should_return_province() {

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                .build(ObjectType.NODE);

        when(provinceRepository.findByNameAndCountry_Name(
                provinceNodeExpected.getName(), provinceNodeExpected.getCountry().getName(), DEFAULT_DEPTH_FOR_PROVINCE_NODE))
                .thenReturn(Optional.of(provinceNodeExpected));

        Optional<ProvinceNode> provinceNodeActualOptional = provinceService.findByNameAndCountryName(provinceNodeExpected);

        ProvinceNode provinceNodeActual = provinceNodeActualOptional.get();

        assertAll(
                () -> assertEquals(provinceNodeExpected.getId(), provinceNodeActual.getId(),
                        () -> "should return province node with id: " + provinceNodeExpected.getId()
                                + ", but was: " + provinceNodeActual.getId()),
                () -> assertEquals(provinceNodeExpected.getName(), provinceNodeActual.getName(),
                        () -> "should return province node with name: " + provinceNodeExpected.getName() + ", but was: "
                                + provinceNodeActual.getName()),
                () -> assertEquals(countryNodeExpected, provinceNodeActual.getCountry(),
                        () -> "should return province node with country: " + countryNodeExpected + ", but was: " + provinceNodeActual.getCountry()),
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
                () -> verify(provinceRepository, times(1)).findByNameAndCountry_Name(
                        provinceNodeExpected.getName(), provinceNodeExpected.getCountry().getName(), DEFAULT_DEPTH_FOR_PROVINCE_NODE),
                () -> verifyNoMoreInteractions(provinceRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(countryService));
    }

    @Test
    void when_find_not_existing_province_by_name_and_country_name_should_return_empty_optional() {

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                .build(ObjectType.NODE);

        when(provinceRepository.findByNameAndCountry_Name(
                provinceNodeExpected.getName(), provinceNodeExpected.getCountry().getName(), DEFAULT_DEPTH_FOR_PROVINCE_NODE))
                .thenReturn(Optional.empty());

        Optional<ProvinceNode> provinceNodeActualOptional = provinceService.findByNameAndCountryName(provinceNodeExpected);

        assertAll(() -> assertTrue(provinceNodeActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(provinceRepository, times(1))
                        .findByNameAndCountry_Name(
                                provinceNodeExpected.getName(), provinceNodeExpected.getCountry().getName(), DEFAULT_DEPTH_FOR_PROVINCE_NODE),
                () -> verifyNoMoreInteractions(provinceRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(countryService));
    }

    @Test
    void when_save_province_should_return_saved_province() {

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpectedBeforeSave = (ProvinceNode) provinceBuilder.withId(null)
                .withCountry(countryNodeExpected).build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                .build(ObjectType.NODE);

        when(countryService.findByName(countryNodeExpected.getName())).thenReturn(Optional.of(countryNodeExpected));
        when(provinceRepository.save(provinceNodeExpectedBeforeSave)).thenReturn(provinceNodeExpected);

        ProvinceNode provinceNodeActual = provinceService.save(provinceNodeExpectedBeforeSave);

        assertAll(
                () -> assertEquals(provinceNodeExpected, provinceNodeActual,
                        () -> "should return province node : " + provinceNodeExpected + ", but was: "
                                + provinceNodeActual),
                () -> assertEquals(provinceNodeExpected.getId(), provinceNodeActual.getId(),
                        () -> "should return province node with id: " + provinceNodeExpected.getId()
                                + ", but was: " + provinceNodeActual.getId()),
                () -> assertEquals(provinceNodeExpected.getName(), provinceNodeActual.getName(),
                        () -> "should return province node with name: " + provinceNodeExpected.getName() + ", but was: "
                                + provinceNodeActual.getName()),
                () -> assertEquals(countryNodeExpected, provinceNodeActual.getCountry(),
                        () -> "should return province node with country: " + countryNodeExpected + ", but was: " + provinceNodeActual.getCountry()),
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
                () -> verify(countryService, times(1)).findByName(countryNodeExpected.getName()),
                () -> verifyNoMoreInteractions(countryService),
                () -> verify(provinceRepository, times(1)).save(provinceNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(provinceRepository),
                () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_save_new_province_should_return_new_province() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpectedBeforeSave = (ProvinceNode) provinceBuilder.withId(null)
                .withCountry(countryNodeExpected).build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                .build(ObjectType.NODE);

        when(objectMapper.map(provinceDTO, ProvinceNode.class)).thenReturn(provinceNodeExpectedBeforeSave);
        when(countryService.findByName(countryNodeExpected.getName())).thenReturn(Optional.of(countryNodeExpected));
        when(provinceRepository.save(provinceNodeExpectedBeforeSave)).thenReturn(provinceNodeExpected);

        ProvinceNode provinceNodeActual = provinceService.saveNew(provinceDTO);

        assertAll(() -> assertEquals(provinceNodeExpected.getId(), provinceNodeActual.getId(),
                () -> "should return province node with id: " + provinceNodeExpected.getId()
                        + ", but was: " + provinceNodeActual.getId()),
                () -> assertEquals(provinceNodeExpected.getName(), provinceNodeActual.getName(),
                        () -> "should return province node with name: " + provinceNodeExpected.getName() + ", but was: "
                                + provinceNodeActual.getName()),
                () -> assertEquals(countryNodeExpected, provinceNodeActual.getCountry(),
                        () -> "should return province node with country: " + countryNodeExpected + ", but was: " + provinceNodeActual.getCountry()),
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
                () -> verify(objectMapper, times(1)).map(provinceDTO, ProvinceNode.class),
                () -> verifyNoMoreInteractions(objectMapper),
                () -> verify(countryService, times(1)).findByName(countryNodeExpected.getName()),
                () -> verifyNoMoreInteractions(countryService),
                () -> verify(provinceRepository, times(1)).save(provinceNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(provinceRepository));
    }

    @Test
    void when_save_province_with_not_existing_country_should_throw_exception() {

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpectedBeforeSave = (ProvinceNode) provinceBuilder.withId(null)
                .withCountry(countryNodeExpected).build(ObjectType.NODE);

        when(countryService.findByName(countryNodeExpected.getName())).thenReturn(Optional.empty());

        assertAll(
                () -> assertThrows(ResourceNotFoundException.class,
                        () -> provinceService.save(provinceNodeExpectedBeforeSave),
                        () -> "should throw ResourceNotFoundException but wasn't"),
                () -> verify(countryService, times(1)).findByName(countryNodeExpected.getName()),
                () -> verifyNoMoreInteractions(countryService),
                () -> verifyNoInteractions(provinceRepository),
                () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_save_new_province_with_not_existing_country_should_throw_exception() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpectedBeforeSave = (ProvinceNode) provinceBuilder.withId(null)
                .withCountry(countryNodeExpected).build(ObjectType.NODE);

        when(objectMapper.map(provinceDTO, ProvinceNode.class)).thenReturn(provinceNodeExpectedBeforeSave);
        when(countryService.findByName(countryNodeExpected.getName())).thenReturn(Optional.empty());

        assertAll(
                () -> assertThrows(ResourceNotFoundException.class,
                        () -> provinceService.saveNew(provinceDTO),
                        () -> "should throw ResourceNotFoundException but wasn't"),
                () -> verify(objectMapper, times(1)).map(provinceDTO, ProvinceNode.class),
                () -> verifyNoMoreInteractions(objectMapper),
                () -> verify(countryService, times(1)).findByName(countryNodeExpected.getName()),
                () -> verifyNoMoreInteractions(countryService),
                () -> verifyNoInteractions(provinceRepository));
    }
}
