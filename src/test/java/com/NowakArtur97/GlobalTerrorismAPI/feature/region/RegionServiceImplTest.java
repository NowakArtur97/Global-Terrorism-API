package com.NowakArtur97.GlobalTerrorismAPI.feature.region;

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
@Tag("RegionServiceImpl_Tests")
class RegionServiceImplTest {

    private RegionService regionService;

    @Mock
    private RegionRepository regionRepository;

    private static RegionBuilder regionBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        regionBuilder = new RegionBuilder();
    }

    @BeforeEach
    private void setUp() {

        regionService = new RegionServiceImpl(regionRepository);
    }

    @Test
    void when_regions_exist_and_return_all_regions_should_return_regions() {

        List<RegionNode> regionsListExpected = new ArrayList<>();

        RegionNode region1 = (RegionNode) regionBuilder.withName("region1").build(ObjectType.NODE);
        RegionNode region2 = (RegionNode) regionBuilder.withName("region2").build(ObjectType.NODE);
        RegionNode region3 = (RegionNode) regionBuilder.withName("region2").build(ObjectType.NODE);

        regionsListExpected.add(region1);
        regionsListExpected.add(region2);
        regionsListExpected.add(region3);

        Page<RegionNode> regionsExpected = new PageImpl<>(regionsListExpected);

        Pageable pageable = PageRequest.of(0, 100);

        when(regionRepository.findAll(pageable)).thenReturn(regionsExpected);

        Page<RegionNode> regionsActual = regionService.findAll(pageable);

        assertAll(() -> assertNotNull(regionsActual, () -> "shouldn't return null"),
                () -> assertEquals(regionsListExpected, regionsActual.getContent(),
                        () -> "should contain: " + regionsListExpected + ", but was: " + regionsActual.getContent()),
                () -> assertEquals(regionsExpected.getNumberOfElements(), regionsActual.getNumberOfElements(),
                        () -> "should return page with: " + regionsExpected.getNumberOfElements()
                                + " elements, but was: " + regionsActual.getNumberOfElements()),
                () -> verify(regionRepository, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(regionRepository));
    }

    @Test
    void when_regions_not_exist_and_return_all_regions_should_not_return_any_regions() {

        List<RegionNode> regionsListExpected = new ArrayList<>();

        Page<RegionNode> regionsExpected = new PageImpl<>(regionsListExpected);

        Pageable pageable = PageRequest.of(0, 100);

        when(regionRepository.findAll(pageable)).thenReturn(regionsExpected);

        Page<RegionNode> regionsActual = regionService.findAll(pageable);

        assertAll(() -> assertNotNull(regionsActual, () -> "shouldn't return null"),
                () -> assertEquals(regionsListExpected, regionsActual.getContent(),
                        () -> "should contain empty list, but was: " + regionsActual.getContent()),
                () -> assertEquals(regionsListExpected, regionsActual.getContent(),
                        () -> "should contain: " + regionsListExpected + ", but was: " + regionsActual.getContent()),
                () -> assertEquals(regionsExpected.getNumberOfElements(), regionsActual.getNumberOfElements(),
                        () -> "should return empty page, but was: " + regionsActual.getNumberOfElements()),
                () -> verify(regionRepository, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(regionRepository));
    }

    @Test
    void when_find_existing_region_by_name_should_return_region() {

        String regionName = "region";

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.withName(regionName).build(ObjectType.NODE);

        when(regionRepository.findByName(regionName)).thenReturn(Optional.of(regionNodeExpected));

        Optional<RegionNode> regionNodeActualOptional = regionService.findByName(regionName);

        RegionNode regionNodeActual = regionNodeActualOptional.get();

        assertAll(() -> assertEquals(regionNodeExpected.getId(), regionNodeActual.getId(),
                () -> "should return region node with id: " + regionNodeExpected.getId()
                        + ", but was: " + regionNodeActual.getId()),
                () -> assertEquals(regionNodeExpected.getName(), regionNodeActual.getName(),
                        () -> "should return region node with name: " + regionNodeExpected.getName()
                                + ", but was: " + regionNodeActual.getName()),
                () -> verify(regionRepository, times(1)).findByName(regionName),
                () -> verifyNoMoreInteractions(regionRepository));
    }

    @Test
    void when_find_not_existing_region_by_name_should_return_empty_optional() {

        String regionName = "region";

        when(regionRepository.findByName(regionName)).thenReturn(Optional.empty());

        Optional<RegionNode> regionNodeActualOptional = regionService.findByName(regionName);

        assertAll(() -> assertTrue(regionNodeActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(regionRepository, times(1)).findByName(regionName),
                () -> verifyNoMoreInteractions(regionRepository));
    }

    @Test
    void when_find_existing_region_by_id_should_return_region() {

        Long regionId = 2L;

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.withId(regionId).build(ObjectType.NODE);

        when(regionRepository.findById(regionId)).thenReturn(Optional.of(regionNodeExpected));

        Optional<RegionNode> regionNodeActualOptional = regionService.findById(regionId);

        RegionNode regionNodeActual = regionNodeActualOptional.get();

        assertAll(() -> assertEquals(regionNodeExpected.getId(), regionNodeActual.getId(),
                () -> "should return region node with id: " + regionNodeExpected.getId()
                        + ", but was: " + regionNodeActual.getId()),
                () -> assertEquals(regionNodeExpected.getName(), regionNodeActual.getName(),
                        () -> "should return region node with name: " + regionNodeExpected.getName()
                                + ", but was: " + regionNodeActual.getName()),
                () -> verify(regionRepository, times(1)).findById(regionId),
                () -> verifyNoMoreInteractions(regionRepository));
    }

    @Test
    void when_find_not_existing_region_by_id_should_return_empty_optional() {

        Long regionId = 1L;

        when(regionRepository.findById(regionId)).thenReturn(Optional.empty());

        Optional<RegionNode> regionNodeActualOptional = regionService.findById(regionId);

        assertAll(() -> assertTrue(regionNodeActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(regionRepository, times(1)).findById(regionId),
                () -> verifyNoMoreInteractions(regionRepository));
    }
    
    @Test
    void when_check_by_name_if_existing_region_exists_should_return_true() {

        String regionName = "region";

        when(regionRepository.existsByName(regionName)).thenReturn(true);

        boolean isRegionExisting = regionService.existsByName(regionName);

        assertAll(() -> assertTrue(isRegionExisting, () -> "should return true, but was: false"),
                () -> verify(regionRepository, times(1)).existsByName(regionName),
                () -> verifyNoMoreInteractions(regionRepository));
    }

    @Test
    void when_check_by_name_if_not_existing_region_exists_should_return_false() {

        String notExistingRegionName = "not existing region";

        when(regionRepository.existsByName(notExistingRegionName)).thenReturn(false);

        boolean isRegionExisting = regionService.existsByName(notExistingRegionName);

        assertAll(() -> assertFalse(isRegionExisting, () -> "should return false, but was: true"),
                () -> verify(regionRepository, times(1)).existsByName(notExistingRegionName),
                () -> verifyNoMoreInteractions(regionRepository));
    }

    @Test
    void when_save_region_should_return_saved_region() {

        RegionNode regionNodeExpectedBeforeSave = (RegionNode) regionBuilder.withId(null).build(ObjectType.NODE);
        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);

        when(regionRepository.save(regionNodeExpectedBeforeSave)).thenReturn(regionNodeExpected);

        RegionNode regionNodeActual = regionService.save(regionNodeExpected);

        assertAll(() -> assertEquals(regionNodeExpected.getId(), regionNodeActual.getId(),
                () -> "should return region node with id: " + regionNodeExpected.getId()
                        + ", but was: " + regionNodeActual.getId()),
                () -> assertEquals(regionNodeExpected.getName(), regionNodeActual.getName(),
                        () -> "should return region node with name: " + regionNodeExpected.getName()
                                + ", but was: " + regionNodeActual.getName()),
                () -> verify(regionRepository, times(1)).save(regionNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(regionRepository));
    }
}
