package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.node.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.CountryRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.CountryService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.CountryBuilder;
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
@Tag("CountryServiceImpl_Tests")
class CountryServiceImplTest {

    private CountryService countryService;

    @Mock
    private CountryRepository countryRepository;

    private static CountryBuilder countryBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        countryBuilder = new CountryBuilder();
    }

    @BeforeEach
    private void setUp() {

        countryService = new CountryServiceImpl(countryRepository);
    }

    @Test
    void when_find_existing_country_by_name_should_return_country() {

        String countryName = "country";

        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withName(countryName).build(ObjectType.NODE);

        when(countryRepository.findByName(countryName)).thenReturn(Optional.of(countryNodeExpected));

        Optional<CountryNode> countryNodeActualOptional = countryService.findByName(countryName);

        CountryNode countryNodeActual = countryNodeActualOptional.get();

        assertAll(() -> assertEquals(countryNodeExpected.getId(), countryNodeActual.getId(),
                () -> "should return country node with id: " + countryNodeExpected.getId()
                        + ", but was: " + countryNodeActual.getId()),
                () -> assertEquals(countryNodeExpected.getName(), countryNodeActual.getName(),
                        () -> "should return country node with name: " + countryNodeExpected.getName()
                                + ", but was: " + countryNodeActual.getName()),
                () -> verify(countryRepository, times(1)).findByName(countryName),
                () -> verifyNoMoreInteractions(countryRepository));
    }

    @Test
    void when_target_not_exists_and_return_one_target_should_return_empty_optional() {

        String countryName = "country";

        when(countryRepository.findByName(countryName)).thenReturn(Optional.empty());

        Optional<CountryNode> countryNodeActualOptional = countryService.findByName(countryName);

        assertAll(() -> assertTrue(countryNodeActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(countryRepository, times(1)).findByName(countryName),
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

        CountryNode countryNodeExpectedBeforeSave = (CountryNode) countryBuilder.withId(null).build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.build(ObjectType.NODE);

        when(countryRepository.save(countryNodeExpectedBeforeSave)).thenReturn(countryNodeExpected);

        CountryNode countryNodeActual = countryService.save(countryNodeExpected);

        assertAll(() -> assertEquals(countryNodeExpected.getId(), countryNodeActual.getId(),
                () -> "should return country node with id: " + countryNodeExpected.getId()
                        + ", but was: " + countryNodeActual.getId()),
                () -> assertEquals(countryNodeExpected.getName(), countryNodeActual.getName(),
                        () -> "should return country node with name: " + countryNodeExpected.getName()
                                + ", but was: " + countryNodeActual.getName()),
                () -> verify(countryRepository, times(1)).save(countryNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(countryRepository));
    }
}
