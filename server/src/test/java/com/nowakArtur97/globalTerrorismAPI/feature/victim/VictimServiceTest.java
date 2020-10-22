package com.nowakArtur97.globalTerrorismAPI.feature.victim;

import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.VictimBuilder;
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
@Tag("VictimService_Tests")
class VictimServiceTest {

    private final int DEFAULT_DEPTH_FOR_JSON_PATCH = 5;

    private VictimService victimService;

    @Mock
    private VictimRepository victimRepository;

    @Mock
    private ModelMapper modelMapper;

    private static VictimBuilder victimBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        victimBuilder = new VictimBuilder();
    }

    @BeforeEach
    private void setUp() {

        victimService = new VictimService(victimRepository, modelMapper);
    }

    @Test
    void when_victims_exist_and_return_all_victims_should_return_victims() {

        List<VictimNode> victimsListExpected = createVictimNodeList(3);

        Page<VictimNode> victimsExpected = new PageImpl<>(victimsListExpected);

        Pageable pageable = PageRequest.of(0, 100);

        when(victimRepository.findAll(pageable)).thenReturn(victimsExpected);

        Page<VictimNode> victimsActual = victimService.findAll(pageable);

        assertAll(() -> assertNotNull(victimsActual, () -> "shouldn't return null"),
                () -> assertEquals(victimsListExpected, victimsActual.getContent(),
                        () -> "should contain: " + victimsListExpected + ", but was: " + victimsActual.getContent()),
                () -> assertEquals(victimsExpected.getNumberOfElements(), victimsActual.getNumberOfElements(),
                        () -> "should return page with: " + victimsExpected.getNumberOfElements()
                                + " elements, but was: " + victimsActual.getNumberOfElements()),
                () -> verify(victimRepository, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(victimRepository),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_victims_not_exist_and_return_all_victims_should_not_return_any_victims() {

        List<VictimNode> victimsListExpected = new ArrayList<>();

        Page<VictimNode> victimsExpected = new PageImpl<>(victimsListExpected);

        Pageable pageable = PageRequest.of(0, 100);

        when(victimRepository.findAll(pageable)).thenReturn(victimsExpected);

        Page<VictimNode> victimsActual = victimService.findAll(pageable);

        assertAll(() -> assertNotNull(victimsActual, () -> "shouldn't return null"),
                () -> assertEquals(victimsListExpected, victimsActual.getContent(),
                        () -> "should contain empty list, but was: " + victimsActual.getContent()),
                () -> assertEquals(victimsListExpected, victimsActual.getContent(),
                        () -> "should contain: " + victimsListExpected + ", but was: " + victimsActual.getContent()),
                () -> assertEquals(victimsExpected.getNumberOfElements(), victimsActual.getNumberOfElements(),
                        () -> "should return empty page, but was: " + victimsActual.getNumberOfElements()),
                () -> verify(victimRepository, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(victimRepository),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_victim_exists_and_return_one_victim_should_return_one_victim() {

        Long expectedVictimId = 1L;
        VictimNode victimNodeExpected = (VictimNode) victimBuilder.withId(expectedVictimId).build(ObjectType.NODE);

        when(victimRepository.findById(expectedVictimId)).thenReturn(Optional.of(victimNodeExpected));

        Optional<VictimNode> victimNodeActualOptional = victimService.findById(expectedVictimId);

        VictimNode victimNodeActual = victimNodeActualOptional.get();

        assertAll(
                () -> assertNotNull(victimNodeActual,
                        () -> "should return victim node: +" + victimNodeExpected + " , but was: null"),
                () -> assertNotNull(victimNodeActual.getId(),
                        () -> "should return victim node with new id, but was: " + victimNodeActual.getId()),
                () -> assertEquals(victimNodeExpected.getTotalNumberOfFatalities(),
                        victimNodeActual.getTotalNumberOfFatalities(),
                        () -> "should return victim node with total number of fatalities: "
                                + victimNodeExpected.getTotalNumberOfFatalities() + ", but was: "
                                + victimNodeActual.getTotalNumberOfFatalities()),
                () -> assertEquals(victimNodeExpected.getNumberOfPerpetratorFatalities(),
                        victimNodeActual.getNumberOfPerpetratorFatalities(),
                        () -> "should return victim node with number of perpetrator fatalities: "
                                + victimNodeExpected.getNumberOfPerpetratorFatalities() + ", but was: "
                                + victimNodeActual.getNumberOfPerpetratorFatalities()),
                () -> assertEquals(victimNodeExpected.getTotalNumberOfInjured(),
                        victimNodeActual.getTotalNumberOfInjured(),
                        () -> "should return victim node with total number of injured: "
                                + victimNodeExpected.getTotalNumberOfInjured() + ", but was: "
                                + victimNodeActual.getTotalNumberOfInjured()),
                () -> assertEquals(victimNodeExpected.getNumberOfPerpetratorInjured(),
                        victimNodeActual.getNumberOfPerpetratorInjured(),
                        () -> "should return victim node with number of perpetrator injured: "
                                + victimNodeExpected.getNumberOfPerpetratorInjured() + ", but was: "
                                + victimNodeActual.getNumberOfPerpetratorInjured()),
                () -> assertEquals(victimNodeExpected.getValueOfPropertyDamage(),
                        victimNodeActual.getValueOfPropertyDamage(),
                        () -> "should return victim node with value of property damage: "
                                + victimNodeExpected.getValueOfPropertyDamage() + ", but was: "
                                + victimNodeActual.getValueOfPropertyDamage()),
                () -> verify(victimRepository, times(1)).findById(expectedVictimId),
                () -> verifyNoMoreInteractions(victimRepository),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_victim_not_exists_and_return_one_victim_should_return_empty_optional() {

        Long expectedVictimId = 1L;

        when(victimRepository.findById(expectedVictimId)).thenReturn(Optional.empty());

        Optional<VictimNode> victimNodeActualOptional = victimService.findById(expectedVictimId);

        assertAll(() -> assertTrue(victimNodeActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(victimRepository, times(1)).findById(expectedVictimId),
                () -> verifyNoMoreInteractions(victimRepository),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_victim_exists_and_return_one_victim_with_depth_should_return_one_victim_with_all_nested_nodes() {

        Long expectedVictimId = 1L;
        VictimNode victimNodeExpected = (VictimNode) victimBuilder.withId(expectedVictimId).build(ObjectType.NODE);

        when(victimRepository.findById(expectedVictimId, DEFAULT_DEPTH_FOR_JSON_PATCH))
                .thenReturn(Optional.of(victimNodeExpected));

        Optional<VictimNode> victimNodeActualOptional = victimService.findById(expectedVictimId, DEFAULT_DEPTH_FOR_JSON_PATCH);

        VictimNode victimNodeActual = victimNodeActualOptional.get();

        assertAll(
                () -> assertNotNull(victimNodeActual,
                        () -> "should return victim node: +" + victimNodeExpected + " , but was: null"),
                () -> assertNotNull(victimNodeActual.getId(),
                        () -> "should return victim node with new id, but was: " + victimNodeActual.getId()),
                () -> assertEquals(victimNodeExpected.getTotalNumberOfFatalities(),
                        victimNodeActual.getTotalNumberOfFatalities(),
                        () -> "should return victim node with total number of fatalities: "
                                + victimNodeExpected.getTotalNumberOfFatalities() + ", but was: "
                                + victimNodeActual.getTotalNumberOfFatalities()),
                () -> assertEquals(victimNodeExpected.getNumberOfPerpetratorFatalities(),
                        victimNodeActual.getNumberOfPerpetratorFatalities(),
                        () -> "should return victim node with number of perpetrator fatalities: "
                                + victimNodeExpected.getNumberOfPerpetratorFatalities() + ", but was: "
                                + victimNodeActual.getNumberOfPerpetratorFatalities()),
                () -> assertEquals(victimNodeExpected.getTotalNumberOfInjured(),
                        victimNodeActual.getTotalNumberOfInjured(),
                        () -> "should return victim node with total number of injured: "
                                + victimNodeExpected.getTotalNumberOfInjured() + ", but was: "
                                + victimNodeActual.getTotalNumberOfInjured()),
                () -> assertEquals(victimNodeExpected.getNumberOfPerpetratorInjured(),
                        victimNodeActual.getNumberOfPerpetratorInjured(),
                        () -> "should return victim node with number of perpetrator injured: "
                                + victimNodeExpected.getNumberOfPerpetratorInjured() + ", but was: "
                                + victimNodeActual.getNumberOfPerpetratorInjured()),
                () -> assertEquals(victimNodeExpected.getValueOfPropertyDamage(),
                        victimNodeActual.getValueOfPropertyDamage(),
                        () -> "should return victim node with value of property damage: "
                                + victimNodeExpected.getValueOfPropertyDamage() + ", but was: "
                                + victimNodeActual.getValueOfPropertyDamage()),
                () -> verify(victimRepository, times(1))
                        .findById(expectedVictimId, DEFAULT_DEPTH_FOR_JSON_PATCH),
                () -> verifyNoMoreInteractions(victimRepository),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_victim_not_exists_and_return_one_victim_with_depth_should_return_empty_optional() {

        Long expectedVictimId = 1L;

        when(victimRepository.findById(expectedVictimId, DEFAULT_DEPTH_FOR_JSON_PATCH)).thenReturn(Optional.empty());

        Optional<VictimNode> victimNodeActualOptional = victimService.findById(expectedVictimId, DEFAULT_DEPTH_FOR_JSON_PATCH);

        assertAll(() -> assertTrue(victimNodeActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(victimRepository, times(1))
                        .findById(expectedVictimId, DEFAULT_DEPTH_FOR_JSON_PATCH),
                () -> verifyNoMoreInteractions(victimRepository),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_save_victim_should_save_and_return_victim() {

        VictimNode victimNodeExpectedBeforeSave = (VictimNode) victimBuilder.withId(null).build(ObjectType.NODE);
        VictimNode victimNodeExpected = (VictimNode) victimBuilder.build(ObjectType.NODE);

        when(victimRepository.save(victimNodeExpectedBeforeSave)).thenReturn(victimNodeExpected);

        VictimNode victimNodeActual = victimService.save(victimNodeExpectedBeforeSave);

        assertAll(
                () -> assertNotNull(victimNodeActual,
                        () -> "should return victim node: +" + victimNodeExpected + " , but was: null"),
                () -> assertNotNull(victimNodeActual.getId(),
                        () -> "should return victim node with new id, but was: " + victimNodeActual.getId()),
                () -> assertEquals(victimNodeExpected.getTotalNumberOfFatalities(),
                        victimNodeActual.getTotalNumberOfFatalities(),
                        () -> "should return victim node with total number of fatalities: "
                                + victimNodeExpected.getTotalNumberOfFatalities() + ", but was: "
                                + victimNodeActual.getTotalNumberOfFatalities()),
                () -> assertEquals(victimNodeExpected.getNumberOfPerpetratorFatalities(),
                        victimNodeActual.getNumberOfPerpetratorFatalities(),
                        () -> "should return victim node with number of perpetrator fatalities: "
                                + victimNodeExpected.getNumberOfPerpetratorFatalities() + ", but was: "
                                + victimNodeActual.getNumberOfPerpetratorFatalities()),
                () -> assertEquals(victimNodeExpected.getTotalNumberOfInjured(),
                        victimNodeActual.getTotalNumberOfInjured(),
                        () -> "should return victim node with total number of injured: "
                                + victimNodeExpected.getTotalNumberOfInjured() + ", but was: "
                                + victimNodeActual.getTotalNumberOfInjured()),
                () -> assertEquals(victimNodeExpected.getNumberOfPerpetratorInjured(),
                        victimNodeActual.getNumberOfPerpetratorInjured(),
                        () -> "should return victim node with number of perpetrator injured: "
                                + victimNodeExpected.getNumberOfPerpetratorInjured() + ", but was: "
                                + victimNodeActual.getNumberOfPerpetratorInjured()),
                () -> assertEquals(victimNodeExpected.getValueOfPropertyDamage(),
                        victimNodeActual.getValueOfPropertyDamage(),
                        () -> "should return victim node with value of property damage: "
                                + victimNodeExpected.getValueOfPropertyDamage() + ", but was: "
                                + victimNodeActual.getValueOfPropertyDamage()),
                () -> verify(victimRepository, times(1)).save(victimNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(victimRepository),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_save_new_victim_should_save_and_return_new_victim() {

        VictimDTO victimDTOExpected = (VictimDTO) victimBuilder.build(ObjectType.DTO);
        VictimNode victimNodeExpectedBeforeSave = (VictimNode) victimBuilder.withId(null).build(ObjectType.NODE);
        VictimNode victimNodeExpected = (VictimNode) victimBuilder.build(ObjectType.NODE);

        when(modelMapper.map(victimDTOExpected, VictimNode.class)).thenReturn(victimNodeExpectedBeforeSave);
        when(victimRepository.save(victimNodeExpectedBeforeSave)).thenReturn(victimNodeExpected);

        VictimNode victimNodeActual = victimService.saveNew(victimDTOExpected);

        assertAll(
                () -> assertNotNull(victimNodeActual,
                        () -> "should return victim node: +" + victimNodeExpected + " , but was: null"),
                () -> assertNotNull(victimNodeActual.getId(),
                        () -> "should return victim node with new id, but was: " + victimNodeActual.getId()),
                () -> assertEquals(victimNodeExpected.getTotalNumberOfFatalities(),
                        victimNodeActual.getTotalNumberOfFatalities(),
                        () -> "should return victim node with total number of fatalities: "
                                + victimNodeExpected.getTotalNumberOfFatalities() + ", but was: "
                                + victimNodeActual.getTotalNumberOfFatalities()),
                () -> assertEquals(victimNodeExpected.getNumberOfPerpetratorFatalities(),
                        victimNodeActual.getNumberOfPerpetratorFatalities(),
                        () -> "should return victim node with number of perpetrator fatalities: "
                                + victimNodeExpected.getNumberOfPerpetratorFatalities() + ", but was: "
                                + victimNodeActual.getNumberOfPerpetratorFatalities()),
                () -> assertEquals(victimNodeExpected.getTotalNumberOfInjured(),
                        victimNodeActual.getTotalNumberOfInjured(),
                        () -> "should return victim node with total number of injured: "
                                + victimNodeExpected.getTotalNumberOfInjured() + ", but was: "
                                + victimNodeActual.getTotalNumberOfInjured()),
                () -> assertEquals(victimNodeExpected.getNumberOfPerpetratorInjured(),
                        victimNodeActual.getNumberOfPerpetratorInjured(),
                        () -> "should return victim node with number of perpetrator injured: "
                                + victimNodeExpected.getNumberOfPerpetratorInjured() + ", but was: "
                                + victimNodeActual.getNumberOfPerpetratorInjured()),
                () -> assertEquals(victimNodeExpected.getValueOfPropertyDamage(),
                        victimNodeActual.getValueOfPropertyDamage(),
                        () -> "should return victim node with value of property damage: "
                                + victimNodeExpected.getValueOfPropertyDamage() + ", but was: "
                                + victimNodeActual.getValueOfPropertyDamage()),
                () -> verify(modelMapper, times(1)).map(victimDTOExpected, VictimNode.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(victimRepository, times(1)).save(victimNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(victimRepository));
    }

    @Test
    void when_update_victim_should_update_victim() {

        Long updatedVictimTotalNumberOfFatalities = 20L;
        Long updatedVictimNumberOfPerpetratorFatalities = 10L;
        Long updatedVictimTotalNumberOfInjured = 14L;
        Long updatedVictimNumberOfPerpetratorInjured = 3L;
        Long updatedVictimValueOfPropertyDamage = 10000L;
        VictimDTO victimDTOExpected = (VictimDTO) victimBuilder
                .withTotalNumberOfFatalities(updatedVictimTotalNumberOfFatalities)
                .withNumberOfPerpetratorFatalities(updatedVictimNumberOfPerpetratorFatalities)
                .withTotalNumberOfInjured(updatedVictimTotalNumberOfInjured)
                .withNumberOfPerpetratorInjured(updatedVictimNumberOfPerpetratorInjured)
                .withValueOfPropertyDamage(updatedVictimValueOfPropertyDamage)
                .build(ObjectType.DTO);

        VictimNode victimNodeExpectedBeforeMethod = (VictimNode) victimBuilder
                .withTotalNumberOfFatalities(updatedVictimTotalNumberOfFatalities)
                .withNumberOfPerpetratorFatalities(updatedVictimNumberOfPerpetratorFatalities)
                .withTotalNumberOfInjured(updatedVictimTotalNumberOfInjured)
                .withNumberOfPerpetratorInjured(updatedVictimNumberOfPerpetratorInjured)
                .withValueOfPropertyDamage(updatedVictimValueOfPropertyDamage).build(ObjectType.NODE);

        VictimNode victimNodeExpected = (VictimNode) victimBuilder
                .withTotalNumberOfFatalities(updatedVictimTotalNumberOfFatalities)
                .withNumberOfPerpetratorFatalities(updatedVictimNumberOfPerpetratorFatalities)
                .withTotalNumberOfInjured(updatedVictimTotalNumberOfInjured)
                .withNumberOfPerpetratorInjured(updatedVictimNumberOfPerpetratorInjured)
                .withValueOfPropertyDamage(updatedVictimValueOfPropertyDamage).build(ObjectType.NODE);

        when(modelMapper.map(victimDTOExpected, VictimNode.class)).thenReturn(victimNodeExpectedBeforeMethod);
        when(victimRepository.save(victimNodeExpectedBeforeMethod)).thenReturn(victimNodeExpected);

        VictimNode victimNodeActual = victimService.update(victimNodeExpectedBeforeMethod, victimDTOExpected);

        assertAll(
                () -> assertNotNull(victimNodeActual,
                        () -> "should return victim node: +" + victimNodeExpected + " , but was: null"),
                () -> assertNotNull(victimNodeActual.getId(),
                        () -> "should return victim node with new id, but was: " + victimNodeActual.getId()),
                () -> assertEquals(victimNodeExpected.getTotalNumberOfFatalities(),
                        victimNodeActual.getTotalNumberOfFatalities(),
                        () -> "should return victim node with total number of fatalities: "
                                + victimNodeExpected.getTotalNumberOfFatalities() + ", but was: "
                                + victimNodeActual.getTotalNumberOfFatalities()),
                () -> assertEquals(victimNodeExpected.getNumberOfPerpetratorFatalities(),
                        victimNodeActual.getNumberOfPerpetratorFatalities(),
                        () -> "should return victim node with number of perpetrator fatalities: "
                                + victimNodeExpected.getNumberOfPerpetratorFatalities() + ", but was: "
                                + victimNodeActual.getNumberOfPerpetratorFatalities()),
                () -> assertEquals(victimNodeExpected.getTotalNumberOfInjured(),
                        victimNodeActual.getTotalNumberOfInjured(),
                        () -> "should return victim node with total number of injured: "
                                + victimNodeExpected.getTotalNumberOfInjured() + ", but was: "
                                + victimNodeActual.getTotalNumberOfInjured()),
                () -> assertEquals(victimNodeExpected.getNumberOfPerpetratorInjured(),
                        victimNodeActual.getNumberOfPerpetratorInjured(),
                        () -> "should return victim node with number of perpetrator injured: "
                                + victimNodeExpected.getNumberOfPerpetratorInjured() + ", but was: "
                                + victimNodeActual.getNumberOfPerpetratorInjured()),
                () -> assertEquals(victimNodeExpected.getValueOfPropertyDamage(),
                        victimNodeActual.getValueOfPropertyDamage(),
                        () -> "should return victim node with value of property damage: "
                                + victimNodeExpected.getValueOfPropertyDamage() + ", but was: "
                                + victimNodeActual.getValueOfPropertyDamage()),
                () -> verify(modelMapper, times(1)).map(victimDTOExpected, VictimNode.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(victimRepository, times(1)).save(victimNodeExpectedBeforeMethod),
                () -> verifyNoMoreInteractions(victimRepository));
    }

    @Test
    void when_delete_victim_should_delete_victim() {

        Long victimId = 1L;

        VictimNode victimNodeExpected = (VictimNode) victimBuilder.withId(victimId).build(ObjectType.NODE);

        when(victimRepository.findById(victimId)).thenReturn(Optional.of(victimNodeExpected));

        Optional<VictimNode> victimNodeOptionalActual = victimService.delete(victimId);

        VictimNode victimNodeActual = victimNodeOptionalActual.get();

        assertAll(
                () -> assertNotNull(victimNodeActual,
                        () -> "should return victim node: +" + victimNodeExpected + " , but was: null"),
                () -> assertNotNull(victimNodeActual.getId(),
                        () -> "should return victim node with new id, but was: " + victimNodeActual.getId()),
                () -> assertEquals(victimNodeExpected.getTotalNumberOfFatalities(),
                        victimNodeActual.getTotalNumberOfFatalities(),
                        () -> "should return victim node with total number of fatalities: "
                                + victimNodeExpected.getTotalNumberOfFatalities() + ", but was: "
                                + victimNodeActual.getTotalNumberOfFatalities()),
                () -> assertEquals(victimNodeExpected.getNumberOfPerpetratorFatalities(),
                        victimNodeActual.getNumberOfPerpetratorFatalities(),
                        () -> "should return victim node with number of perpetrator fatalities: "
                                + victimNodeExpected.getNumberOfPerpetratorFatalities() + ", but was: "
                                + victimNodeActual.getNumberOfPerpetratorFatalities()),
                () -> assertEquals(victimNodeExpected.getTotalNumberOfInjured(),
                        victimNodeActual.getTotalNumberOfInjured(),
                        () -> "should return victim node with total number of injured: "
                                + victimNodeExpected.getTotalNumberOfInjured() + ", but was: "
                                + victimNodeActual.getTotalNumberOfInjured()),
                () -> assertEquals(victimNodeExpected.getNumberOfPerpetratorInjured(),
                        victimNodeActual.getNumberOfPerpetratorInjured(),
                        () -> "should return victim node with number of perpetrator injured: "
                                + victimNodeExpected.getNumberOfPerpetratorInjured() + ", but was: "
                                + victimNodeActual.getNumberOfPerpetratorInjured()),
                () -> assertEquals(victimNodeExpected.getValueOfPropertyDamage(),
                        victimNodeActual.getValueOfPropertyDamage(),
                        () -> "should return victim node with value of property damage: "
                                + victimNodeExpected.getValueOfPropertyDamage() + ", but was: "
                                + victimNodeActual.getValueOfPropertyDamage()),
                () -> verify(victimRepository, times(1)).findById(victimId),
                () -> verify(victimRepository, times(1)).delete(victimNodeExpected),
                () -> verifyNoMoreInteractions(victimRepository),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_delete_victim_by_id_not_existing_victim_should_return_empty_optional() {

        Long victimId = 1L;

        when(victimRepository.findById(victimId)).thenReturn(Optional.empty());

        Optional<VictimNode> victimNodeOptional = victimService.delete(victimId);

        assertAll(
                () -> assertTrue(victimNodeOptional.isEmpty(),
                        () -> "should return empty victim node optional, but was: " + victimNodeOptional.get()),
                () -> verify(victimRepository, times(1)).findById(victimId),
                () -> verifyNoMoreInteractions(victimRepository),
                () -> verifyNoInteractions(modelMapper));
    }

    private List<VictimNode> createVictimNodeList(int listSize) {

        VictimBuilder victimBuilder = new VictimBuilder();

        List<VictimNode> victimsListExpected = new ArrayList<>();

        int count = 0;

        while (count < listSize) {

            VictimNode victimNode = (VictimNode) victimBuilder.build(ObjectType.NODE);

            victimsListExpected.add(victimNode);

            count++;
        }

        return victimsListExpected;
    }
}
