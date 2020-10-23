package com.nowakArtur97.globalTerrorismAPI.feature.province;

import com.nowakArtur97.globalTerrorismAPI.common.exception.ResourceNotFoundException;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryNode;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryService;
import com.nowakArtur97.globalTerrorismAPI.feature.region.RegionNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.CountryBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.ProvinceBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.RegionBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
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
@Tag("ProvinceService_Tests")
class ProvinceServiceTest {

    private final int DEFAULT_DEPTH_FOR_PROVINCE_NODE = 2;

    private ProvinceService provinceService;

    @Mock
    private ProvinceRepository provinceRepository;

    @Mock
    private ModelMapper modelMapper;

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

        provinceService = new ProvinceService(provinceRepository, modelMapper, countryService);
    }

    @Test
    void when_provinces_exist_and_return_all_provinces_should_return_provinces() {

        List<ProvinceNode> provincesListExpected = new ArrayList<>();

        ProvinceNode province1 = (ProvinceNode) provinceBuilder.withName("province1").build(ObjectType.NODE);
        ProvinceNode province2 = (ProvinceNode) provinceBuilder.withName("province2").build(ObjectType.NODE);
        ProvinceNode province3 = (ProvinceNode) provinceBuilder.withName("province2").build(ObjectType.NODE);

        provincesListExpected.add(province1);
        provincesListExpected.add(province2);
        provincesListExpected.add(province3);

        Page<ProvinceNode> provincesExpected = new PageImpl<>(provincesListExpected);

        Pageable pageable = PageRequest.of(0, 100);

        when(provinceRepository.findAll(pageable)).thenReturn(provincesExpected);

        Page<ProvinceNode> provincesActual = provinceService.findAll(pageable);

        assertAll(() -> assertNotNull(provincesActual, () -> "shouldn't return null"),
                () -> assertEquals(provincesListExpected, provincesActual.getContent(),
                        () -> "should contain: " + provincesListExpected + ", but was: " + provincesActual.getContent()),
                () -> assertEquals(provincesExpected.getNumberOfElements(), provincesActual.getNumberOfElements(),
                        () -> "should return page with: " + provincesExpected.getNumberOfElements()
                                + " elements, but was: " + provincesActual.getNumberOfElements()),
                () -> verify(provinceRepository, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(provinceRepository),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(countryService));
    }

    @Test
    void when_provinces_not_exist_and_return_all_provinces_should_not_return_any_provinces() {

        List<ProvinceNode> provincesListExpected = new ArrayList<>();

        Page<ProvinceNode> provincesExpected = new PageImpl<>(provincesListExpected);

        Pageable pageable = PageRequest.of(0, 100);

        when(provinceRepository.findAll(pageable)).thenReturn(provincesExpected);

        Page<ProvinceNode> provincesActual = provinceService.findAll(pageable);

        assertAll(() -> assertNotNull(provincesActual, () -> "shouldn't return null"),
                () -> assertEquals(provincesListExpected, provincesActual.getContent(),
                        () -> "should contain empty list, but was: " + provincesActual.getContent()),
                () -> assertEquals(provincesListExpected, provincesActual.getContent(),
                        () -> "should contain: " + provincesListExpected + ", but was: " + provincesActual.getContent()),
                () -> assertEquals(provincesExpected.getNumberOfElements(), provincesActual.getNumberOfElements(),
                        () -> "should return empty page, but was: " + provincesActual.getNumberOfElements()),
                () -> verify(provinceRepository, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(provinceRepository),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(countryService));
    }

    @Test
    void when_find_existing_province_by_id_should_return_province() {

        Long expectedProvinceId = 1L;

        CountryNode countryNodeExpected = (CountryNode) countryBuilder.build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withId(expectedProvinceId)
                .withCountry(countryNodeExpected).build(ObjectType.NODE);

        when(provinceRepository.findById(expectedProvinceId)).thenReturn(Optional.of(provinceNodeExpected));

        Optional<ProvinceNode> provinceActualOptional = provinceService.findById(expectedProvinceId);

        ProvinceNode provinceNodeActual = provinceActualOptional.get();

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
                () -> assertNull(provinceNodeActual.getCountry().getRegion(),
                        () -> "should return province node with null region, but was: "
                                + provinceNodeActual.getCountry().getRegion()),
                () -> verify(provinceRepository, times(1)).findById(expectedProvinceId),
                () -> verifyNoMoreInteractions(provinceRepository),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(countryService));
    }

    @Test
    void when_find_not_existing_province_by_id_return_empty_optional() {

        Long expectedProvinceId = 1L;

        when(provinceRepository.findById(expectedProvinceId)).thenReturn(Optional.empty());

        Optional<ProvinceNode> provinceActualOptional = provinceService.findById(expectedProvinceId);

        assertAll(() -> assertTrue(provinceActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(provinceRepository, times(1)).findById(expectedProvinceId),
                () -> verifyNoMoreInteractions(provinceRepository),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(countryService));
    }

    @Test
    void when_find_existing_province_by_id_with_depth_should_return_province() {

        Long provinceId = 1L;

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                .build(ObjectType.NODE);

        when(provinceRepository.findById(provinceId, DEFAULT_DEPTH_FOR_PROVINCE_NODE))
                .thenReturn(Optional.of(provinceNodeExpected));

        Optional<ProvinceNode> provinceNodeActualOptional = provinceService.findById(provinceId, DEFAULT_DEPTH_FOR_PROVINCE_NODE);

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
                () -> verify(provinceRepository, times(1))
                        .findById(provinceId, DEFAULT_DEPTH_FOR_PROVINCE_NODE),
                () -> verifyNoMoreInteractions(provinceRepository),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(countryService));
    }

    @Test
    void when_find_not_existing_province_by_id_with_depth_should_return_empty_optional() {

        Long provinceId = 1L;

        when(provinceRepository.findById(provinceId, DEFAULT_DEPTH_FOR_PROVINCE_NODE)).thenReturn(Optional.empty());

        Optional<ProvinceNode> provinceNodeActualOptional = provinceService.findById(provinceId, DEFAULT_DEPTH_FOR_PROVINCE_NODE);

        assertAll(() -> assertTrue(provinceNodeActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(provinceRepository, times(1))
                        .findById(provinceId, DEFAULT_DEPTH_FOR_PROVINCE_NODE),
                () -> verifyNoMoreInteractions(provinceRepository),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(countryService));
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

        Optional<ProvinceNode> provinceNodeActualOptional = provinceService
                .findByNameAndCountryName(provinceNodeExpected.getName(), countryNodeExpected.getName());

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
                        provinceNodeExpected.getName(), provinceNodeExpected.getCountry().getName(),
                        DEFAULT_DEPTH_FOR_PROVINCE_NODE),
                () -> verifyNoMoreInteractions(provinceRepository),
                () -> verifyNoInteractions(modelMapper),
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

        Optional<ProvinceNode> provinceNodeActualOptional = provinceService
                .findByNameAndCountryName(provinceNodeExpected.getName(), countryNodeExpected.getName());


        assertAll(() -> assertTrue(provinceNodeActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(provinceRepository, times(1))
                        .findByNameAndCountry_Name(
                                provinceNodeExpected.getName(), provinceNodeExpected.getCountry().getName(),
                                DEFAULT_DEPTH_FOR_PROVINCE_NODE),
                () -> verifyNoMoreInteractions(provinceRepository),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(countryService));
    }

    @Test
    void when_save_province_with_existing_country_should_return_saved_province() {

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
                () -> verifyNoInteractions(modelMapper));
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
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_save_new_province_with_existing_country_should_return_new_province() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpectedBeforeSave = (ProvinceNode) provinceBuilder.withId(null)
                .withCountry(countryNodeExpected).build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                .build(ObjectType.NODE);

        when(modelMapper.map(provinceDTO, ProvinceNode.class)).thenReturn(provinceNodeExpectedBeforeSave);
        when(countryService.findByName(countryDTO.getName())).thenReturn(Optional.of(countryNodeExpected));
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
                () -> verify(modelMapper, times(1)).map(provinceDTO, ProvinceNode.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(countryService, times(1)).findByName(countryDTO.getName()),
                () -> verifyNoMoreInteractions(countryService),
                () -> verify(provinceRepository, times(1)).save(provinceNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(provinceRepository));
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

        when(modelMapper.map(provinceDTO, ProvinceNode.class)).thenReturn(provinceNodeExpectedBeforeSave);
        when(countryService.findByName(countryDTO.getName())).thenReturn(Optional.empty());

        assertAll(
                () -> assertThrows(ResourceNotFoundException.class,
                        () -> provinceService.saveNew(provinceDTO),
                        () -> "should throw ResourceNotFoundException but wasn't"),
                () -> verify(modelMapper, times(1)).map(provinceDTO, ProvinceNode.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(countryService, times(1)).findByName(countryDTO.getName()),
                () -> verifyNoMoreInteractions(countryService),
                () -> verifyNoInteractions(provinceRepository));
    }

    @Test
    void when_update_province_with_existing_country_should_return_saved_province() {

        String updatedProvinceName = "province name updated";

        CountryDTO countryDTO = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withName(updatedProvinceName).withCountry(countryDTO)
                .build(ObjectType.DTO);

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeToUpdate = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpectedBeforeSave = (ProvinceNode) provinceBuilder.withId(null).withName(updatedProvinceName)
                .withCountry(countryNodeExpected).build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withName(updatedProvinceName)
                .withCountry(countryNodeExpected).build(ObjectType.NODE);

        when(modelMapper.map(provinceDTO, ProvinceNode.class)).thenReturn(provinceNodeExpectedBeforeSave);
        when(countryService.findByName(countryDTO.getName())).thenReturn(Optional.of(countryNodeExpected));
        when(provinceRepository.save(provinceNodeExpectedBeforeSave)).thenReturn(provinceNodeExpected);

        ProvinceNode provinceNodeActual = provinceService.update(provinceNodeToUpdate, provinceDTO);

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
                () -> verify(modelMapper, times(1)).map(provinceDTO, ProvinceNode.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(countryService, times(1)).findByName(countryDTO.getName()),
                () -> verifyNoMoreInteractions(countryService),
                () -> verify(provinceRepository, times(1)).save(provinceNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(provinceRepository));
    }

    @Test
    void when_update_province_with_not_existing_country_should_throw_exception() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpectedBeforeSave = (ProvinceNode) provinceBuilder.withId(null)
                .withCountry(countryNodeExpected).build(ObjectType.NODE);

        when(modelMapper.map(provinceDTO, ProvinceNode.class)).thenReturn(provinceNodeExpectedBeforeSave);
        when(countryService.findByName(countryDTO.getName())).thenReturn(Optional.empty());

        assertAll(
                () -> assertThrows(ResourceNotFoundException.class,
                        () -> provinceService.update(provinceNodeExpectedBeforeSave, provinceDTO),
                        () -> "should throw ResourceNotFoundException but wasn't"),
                () -> verify(modelMapper, times(1)).map(provinceDTO, ProvinceNode.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(countryService, times(1)).findByName(countryDTO.getName()),
                () -> verifyNoMoreInteractions(countryService),
                () -> verifyNoInteractions(provinceRepository));
    }
}
