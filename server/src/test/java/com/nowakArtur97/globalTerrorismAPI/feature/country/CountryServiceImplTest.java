package com.nowakArtur97.globalTerrorismAPI.feature.country;

import com.nowakArtur97.globalTerrorismAPI.feature.region.RegionNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.CountryBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.RegionBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
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
@Tag("CountryServiceImpl_Tests")
class CountryServiceImplTest {

    private CountryService countryService;

    @Mock
    private CountryRepository countryRepository;

    private static RegionBuilder regionBuilder;
    private static CountryBuilder countryBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        regionBuilder = new RegionBuilder();
        countryBuilder = new CountryBuilder();
    }

    @BeforeEach
    private void setUp() {

        countryService = new CountryServiceImpl(countryRepository);
    }

    @Test
    void when_countries_exist_and_return_all_countries_should_return_countries() {

        List<CountryNode> countriesListExpected = new ArrayList<>();

        CountryNode country1 = (CountryNode) countryBuilder.withName("country1").build(ObjectType.NODE);
        CountryNode country2 = (CountryNode) countryBuilder.withName("country2").build(ObjectType.NODE);
        CountryNode country3 = (CountryNode) countryBuilder.withName("country2").build(ObjectType.NODE);

        countriesListExpected.add(country1);
        countriesListExpected.add(country2);
        countriesListExpected.add(country3);

        Page<CountryNode> countriesExpected = new PageImpl<>(countriesListExpected);

        Pageable pageable = PageRequest.of(0, 100);

        when(countryRepository.findAll(pageable)).thenReturn(countriesExpected);

        Page<CountryNode> countriesActual = countryService.findAll(pageable);

        assertAll(() -> assertNotNull(countriesActual, () -> "shouldn't return null"),
                () -> assertEquals(countriesListExpected, countriesActual.getContent(),
                        () -> "should contain: " + countriesListExpected + ", but was: " + countriesActual.getContent()),
                () -> assertEquals(countriesExpected.getNumberOfElements(), countriesActual.getNumberOfElements(),
                        () -> "should return page with: " + countriesExpected.getNumberOfElements()
                                + " elements, but was: " + countriesActual.getNumberOfElements()),
                () -> verify(countryRepository, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(countryRepository));
    }

    @Test
    void when_countries_not_exist_and_return_all_countries_should_not_return_any_countries() {

        List<CountryNode> countriesListExpected = new ArrayList<>();

        Page<CountryNode> countriesExpected = new PageImpl<>(countriesListExpected);

        Pageable pageable = PageRequest.of(0, 100);

        when(countryRepository.findAll(pageable)).thenReturn(countriesExpected);

        Page<CountryNode> countriesActual = countryService.findAll(pageable);

        assertAll(() -> assertNotNull(countriesActual, () -> "shouldn't return null"),
                () -> assertEquals(countriesListExpected, countriesActual.getContent(),
                        () -> "should contain empty list, but was: " + countriesActual.getContent()),
                () -> assertEquals(countriesListExpected, countriesActual.getContent(),
                        () -> "should contain: " + countriesListExpected + ", but was: " + countriesActual.getContent()),
                () -> assertEquals(countriesExpected.getNumberOfElements(), countriesActual.getNumberOfElements(),
                        () -> "should return empty page, but was: " + countriesActual.getNumberOfElements()),
                () -> verify(countryRepository, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(countryRepository));
    }

    @Test
    void when_find_existing_country_by_name_should_return_country() {

        String countryName = "country";

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withName(countryName).withRegion(regionNodeExpected)
                .build(ObjectType.NODE);

        when(countryRepository.findByName(countryName)).thenReturn(Optional.of(countryNodeExpected));

        Optional<CountryNode> countryNodeActualOptional = countryService.findByName(countryName);

        CountryNode countryNodeActual = countryNodeActualOptional.get();

        assertAll(() -> assertEquals(countryNodeExpected.getId(), countryNodeActual.getId(),
                () -> "should return country node with id: " + countryNodeExpected.getId()
                        + ", but was: " + countryNodeActual.getId()),
                () -> assertEquals(countryNodeExpected.getName(), countryNodeActual.getName(),
                        () -> "should return country node with name: " + countryNodeExpected.getName()
                                + ", but was: " + countryNodeActual.getName()),
                () -> assertEquals(regionNodeExpected.getId(),
                        countryNodeActual.getRegion().getId(),
                        () -> "should return country node with region id: " +
                                regionNodeExpected.getId()
                                + ", but was: " + countryNodeActual.getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), countryNodeActual.getRegion().getName(),
                        () -> "should return country node with region name: " + regionNodeExpected.getName()
                                + ", but was: " + countryNodeActual.getRegion().getName()),
                () -> verify(countryRepository, times(1)).findByName(countryName),
                () -> verifyNoMoreInteractions(countryRepository));
    }

    @Test
    void when_country_not_exists_and_return_one_country_should_return_empty_optional() {

        String countryName = "country";

        when(countryRepository.findByName(countryName)).thenReturn(Optional.empty());

        Optional<CountryNode> countryNodeActualOptional = countryService.findByName(countryName);

        assertAll(() -> assertTrue(countryNodeActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(countryRepository, times(1)).findByName(countryName),
                () -> verifyNoMoreInteractions(countryRepository));
    }

    @Test
    void when_find_existing_country_by_id_should_return_country() {

        Long countryId = 2L;

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withId(countryId).withRegion(regionNodeExpected)
                .build(ObjectType.NODE);

        when(countryRepository.findById(countryId)).thenReturn(Optional.of(countryNodeExpected));

        Optional<CountryNode> countryNodeActualOptional = countryService.findById(countryId);

        CountryNode countryNodeActual = countryNodeActualOptional.get();

        assertAll(() -> assertEquals(countryNodeExpected.getId(), countryNodeActual.getId(),
                () -> "should return country node with id: " + countryNodeExpected.getId()
                        + ", but was: " + countryNodeActual.getId()),
                () -> assertEquals(countryNodeExpected.getName(), countryNodeActual.getName(),
                        () -> "should return country node with name: " + countryNodeExpected.getName()
                                + ", but was: " + countryNodeActual.getName()),
                () -> assertEquals(regionNodeExpected.getId(),
                        countryNodeActual.getRegion().getId(),
                        () -> "should return country node with region id: " +
                                regionNodeExpected.getId()
                                + ", but was: " + countryNodeActual.getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), countryNodeActual.getRegion().getName(),
                        () -> "should return country node with region name: " + regionNodeExpected.getName()
                                + ", but was: " + countryNodeActual.getRegion().getName()),
                () -> verify(countryRepository, times(1)).findById(countryId),
                () -> verifyNoMoreInteractions(countryRepository));
    }

    @Test
    void when_find_not_existing_country_by_id_should_return_empty_optional() {

        Long countryId = 1L;

        when(countryRepository.findById(countryId)).thenReturn(Optional.empty());

        Optional<CountryNode> countryNodeActualOptional = countryService.findById(countryId);

        assertAll(() -> assertTrue(countryNodeActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(countryRepository, times(1)).findById(countryId),
                () -> verifyNoMoreInteractions(countryRepository));
    }

    @Test
    void when_check_by_name_if_existing_country_exists_should_return_true() {

        String countryName = "country";

        when(countryRepository.existsByName(countryName)).thenReturn(true);

        boolean isCountryExisting = countryService.existsByName(countryName);

        assertAll(() -> assertTrue(isCountryExisting, () -> "should return true, but was: false"),
                () -> verify(countryRepository, times(1)).existsByName(countryName),
                () -> verifyNoMoreInteractions(countryRepository));
    }

    @Test
    void when_check_by_name_if_not_existing_country_exists_should_return_true() {

        String notExistingCountryName = "not existing country";

        when(countryRepository.existsByName(notExistingCountryName)).thenReturn(false);

        boolean isCountryExisting = countryService.existsByName(notExistingCountryName);

        assertAll(() -> assertFalse(isCountryExisting, () -> "should return false, but was: true"),
                () -> verify(countryRepository, times(1)).existsByName(notExistingCountryName),
                () -> verifyNoMoreInteractions(countryRepository));
    }

    @Test
    void when_save_country_should_return_saved_country() {

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpectedBeforeSave = (CountryNode) countryBuilder.withId(null).withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);

        when(countryRepository.save(countryNodeExpectedBeforeSave)).thenReturn(countryNodeExpected);

        CountryNode countryNodeActual = countryService.save(countryNodeExpected);

        assertAll(() -> assertEquals(countryNodeExpected.getId(), countryNodeActual.getId(),
                () -> "should return country node with id: " + countryNodeExpected.getId()
                        + ", but was: " + countryNodeActual.getId()),
                () -> assertEquals(countryNodeExpected.getName(), countryNodeActual.getName(),
                        () -> "should return country node with name: " + countryNodeExpected.getName()
                                + ", but was: " + countryNodeActual.getName()),
                () -> assertEquals(regionNodeExpected.getId(),
                        countryNodeActual.getRegion().getId(),
                        () -> "should return country node with region id: " +
                                regionNodeExpected.getId()
                                + ", but was: " + countryNodeActual.getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), countryNodeActual.getRegion().getName(),
                        () -> "should return country node with region name: " + regionNodeExpected.getName()
                                + ", but was: " + countryNodeActual.getRegion().getName()),
                () -> verify(countryRepository, times(1)).save(countryNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(countryRepository));
    }
}
