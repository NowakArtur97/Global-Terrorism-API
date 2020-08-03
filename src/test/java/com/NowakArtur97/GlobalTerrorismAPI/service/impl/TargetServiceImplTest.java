package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.CountryDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.TargetRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.CountryService;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.CountryBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
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
@Tag("TargetServiceImpl_Tests")
class TargetServiceImplTest {

    private final int DEFAULT_DEPTH_FOR_JSON_PATCH = 3;

    private TargetService targetService;

    @Mock
    private TargetRepository targetRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CountryService countryService;

    private static CountryBuilder countryBuilder;
    private static TargetBuilder targetBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        countryBuilder = new CountryBuilder();
        targetBuilder = new TargetBuilder();
    }

    @BeforeEach
    private void setUp() {

        targetService = new TargetServiceImpl(targetRepository, objectMapper, countryService);
    }

    @Test
    void when_targets_exist_and_return_all_targets_should_return_targets() {

        List<TargetNode> targetsListExpected = new ArrayList<>();

        TargetNode target1 = (TargetNode) targetBuilder.withTarget("target1").build(ObjectType.NODE);
        TargetNode target2 = (TargetNode) targetBuilder.withTarget("target2").build(ObjectType.NODE);
        TargetNode target3 = (TargetNode) targetBuilder.withTarget("target2").build(ObjectType.NODE);

        targetsListExpected.add(target1);
        targetsListExpected.add(target2);
        targetsListExpected.add(target3);

        Page<TargetNode> targetsExpected = new PageImpl<>(targetsListExpected);

        Pageable pageable = PageRequest.of(0, 100);

        when(targetRepository.findAll(pageable)).thenReturn(targetsExpected);

        Page<TargetNode> targetsActual = targetService.findAll(pageable);

        assertAll(() -> assertNotNull(targetsActual, () -> "shouldn't return null"),
                () -> assertEquals(targetsListExpected, targetsActual.getContent(),
                        () -> "should contain: " + targetsListExpected + ", but was: " + targetsActual.getContent()),
                () -> assertEquals(targetsExpected.getNumberOfElements(), targetsActual.getNumberOfElements(),
                        () -> "should return page with: " + targetsExpected.getNumberOfElements()
                                + " elements, but was: " + targetsActual.getNumberOfElements()),
                () -> verify(targetRepository, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(targetRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(countryService));
    }

    @Test
    void when_targets_not_exist_and_return_all_targets_should_not_return_any_targets() {

        List<TargetNode> targetsListExpected = new ArrayList<>();

        Page<TargetNode> targetsExpected = new PageImpl<>(targetsListExpected);

        Pageable pageable = PageRequest.of(0, 100);

        when(targetRepository.findAll(pageable)).thenReturn(targetsExpected);

        Page<TargetNode> targetsActual = targetService.findAll(pageable);

        assertAll(() -> assertNotNull(targetsActual, () -> "shouldn't return null"),
                () -> assertEquals(targetsListExpected, targetsActual.getContent(),
                        () -> "should contain empty list, but was: " + targetsActual.getContent()),
                () -> assertEquals(targetsListExpected, targetsActual.getContent(),
                        () -> "should contain: " + targetsListExpected + ", but was: " + targetsActual.getContent()),
                () -> assertEquals(targetsExpected.getNumberOfElements(), targetsActual.getNumberOfElements(),
                        () -> "should return empty page, but was: " + targetsActual.getNumberOfElements()),
                () -> verify(targetRepository, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(targetRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(countryService));
    }

    @Test
    void when_target_exists_and_return_one_target_should_return_one_target() {

        Long expectedTargetId = 1L;

        TargetNode targetExpected = (TargetNode) targetBuilder.withId(expectedTargetId).build(ObjectType.NODE);

        when(targetRepository.findById(expectedTargetId)).thenReturn(Optional.of(targetExpected));

        Optional<TargetNode> targetActualOptional = targetService.findById(expectedTargetId);

        TargetNode targetActual = targetActualOptional.get();

        assertAll(  () -> assertEquals(targetExpected.getId(), targetActual.getId(),
                        () -> "should return target with id: " + expectedTargetId + ", but was" + targetActual.getId()),
                () -> assertEquals(targetExpected.getTarget(), targetActual.getTarget(),
                        () -> "should return target with target: " + targetExpected.getTarget() + ", but was"
                                + targetActual.getTarget()),
                () -> verify(targetRepository, times(1)).findById(expectedTargetId),
                () -> verifyNoMoreInteractions(targetRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(countryService));
    }

    @Test
    void when_target_not_exists_and_return_one_target_should_return_empty_optional() {

        Long expectedTargetId = 1L;

        when(targetRepository.findById(expectedTargetId)).thenReturn(Optional.empty());

        Optional<TargetNode> targetActualOptional = targetService.findById(expectedTargetId);

        assertAll(() -> assertTrue(targetActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(targetRepository, times(1)).findById(expectedTargetId),
                () -> verifyNoMoreInteractions(targetRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(countryService));
    }

    @Test
    void when_target_exists_and_return_one_target_with_depth_should_return_target_with_country() {

        Long expectedTargetId = 1L;

        CountryNode countryNodeExpected = (CountryNode) countryBuilder.build(ObjectType.NODE);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withId(expectedTargetId).withCountry(countryNodeExpected).build(ObjectType.NODE);

        when(targetRepository.findById(expectedTargetId, DEFAULT_DEPTH_FOR_JSON_PATCH)).thenReturn(Optional.of(targetNodeExpected));

        Optional<TargetNode> targetActualOptional = targetService.findById(expectedTargetId, DEFAULT_DEPTH_FOR_JSON_PATCH);

        TargetNode targetNodeActual = targetActualOptional.get();

        assertAll(() -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
                () -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: "
                        + targetNodeActual.getTarget()),
                () -> assertNotNull(targetNodeActual.getId(),
                        () -> "should return target node with new id, but was: " + targetNodeActual.getId()),
                () -> assertEquals(targetNodeExpected.getCountryOfOrigin(), targetNodeActual.getCountryOfOrigin(),
                        () -> "should return target node with country: " + targetNodeExpected.getCountryOfOrigin() + ", but was: " + targetNodeActual.getCountryOfOrigin()),
                () -> assertEquals(targetNodeExpected.getCountryOfOrigin().getId(), targetNodeActual.getCountryOfOrigin().getId(),
                        () -> "should return target node with country id: " + targetNodeExpected.getCountryOfOrigin().getId()
                                + ", but was: " + targetNodeActual.getId()),
                () -> assertEquals(targetNodeExpected.getCountryOfOrigin().getName(), targetNodeActual.getCountryOfOrigin().getName(),
                        () -> "should return target node with country name: " + targetNodeExpected.getCountryOfOrigin().getName()
                                + ", but was: " + targetNodeActual.getCountryOfOrigin()),
                () -> verify(targetRepository, times(1)).findById(expectedTargetId, DEFAULT_DEPTH_FOR_JSON_PATCH),
                () -> verifyNoMoreInteractions(targetRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(countryService));
    }

    @Test
    void when_target_not_exists_and_return_one_target_with_depth_should_return_empty_optional() {

        Long expectedTargetId = 1L;

        when(targetRepository.findById(expectedTargetId, DEFAULT_DEPTH_FOR_JSON_PATCH)).thenReturn(Optional.empty());

        Optional<TargetNode> targetActualOptional = targetService.findById(expectedTargetId, DEFAULT_DEPTH_FOR_JSON_PATCH);

        assertAll(() -> assertTrue(targetActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(targetRepository, times(1)).findById(expectedTargetId, DEFAULT_DEPTH_FOR_JSON_PATCH),
                () -> verifyNoMoreInteractions(targetRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(countryService));
    }

    @Test
    void when_save_new_target_should_return_new_target() {

        Long targetId = 1L;

        CountryDTO countryDTOExpected = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        TargetDTO targetDTOExpected = (TargetDTO) targetBuilder.withCountry(countryDTOExpected).build(ObjectType.DTO);

        CountryNode countryNodeExpected = (CountryNode) countryBuilder.build(ObjectType.NODE);
        TargetNode targetNodeExpectedBeforeSetCountry = (TargetNode) targetBuilder.withId(null).withCountry(null).build(ObjectType.NODE);
        TargetNode targetNodeExpectedBeforeSave = (TargetNode) targetBuilder.withId(null).withCountry(countryNodeExpected).build(ObjectType.NODE);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withId(targetId).withCountry(countryNodeExpected).build(ObjectType.NODE);

        when(objectMapper.map(targetDTOExpected, TargetNode.class)).thenReturn(targetNodeExpectedBeforeSetCountry);
        when(countryService.findByName(countryDTOExpected.getName())).thenReturn(Optional.of(countryNodeExpected));
        when(targetRepository.save(targetNodeExpectedBeforeSave)).thenReturn(targetNodeExpected);

        TargetNode targetNodeActual = targetService.saveNew(targetDTOExpected);

        assertAll(
                () -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
                        () -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + targetNodeActual.getTarget()),
                () -> assertNotNull(targetNodeActual.getId(),
                        () -> "should return target node with new id, but was: " + targetNodeActual.getId()),
                () -> assertEquals(targetNodeExpected.getCountryOfOrigin(), targetNodeActual.getCountryOfOrigin(),
                        () -> "should return target node with country: " + targetNodeExpected.getCountryOfOrigin() + ", but was: " + targetNodeActual.getCountryOfOrigin()),
                () -> assertEquals(targetNodeExpected.getCountryOfOrigin().getId(), targetNodeActual.getCountryOfOrigin().getId(),
                        () -> "should return target node with country id: " + targetNodeExpected.getCountryOfOrigin().getId()
                                + ", but was: " + targetNodeActual.getId()),
                () -> assertEquals(targetNodeExpected.getCountryOfOrigin().getName(), targetNodeActual.getCountryOfOrigin().getName(),
                        () -> "should return target node with country name: " + targetNodeExpected.getCountryOfOrigin().getName()
                                + ", but was: " + targetNodeActual.getCountryOfOrigin()),
                () -> verify(objectMapper, times(1)).map(targetDTOExpected, TargetNode.class),
                () -> verifyNoMoreInteractions(objectMapper),
                () -> verify(countryService, times(1)).findByName(countryDTOExpected.getName()),
                () -> verifyNoMoreInteractions(countryService),
                () -> verify(targetRepository, times(1)).save(targetNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(targetRepository));
    }

    @Test
    void when_update_target_should_return_updated_target() {

        Long targetId = 1L;

        String targetName = "Target";
        String countryName = "Country";
        String updatedTargetName = "Updated Target";
        String updatedCountryName = "Another Country";

        CountryDTO countryDTOExpected = (CountryDTO) countryBuilder.withName(updatedCountryName).build(ObjectType.DTO);
        TargetDTO targetDTOExpected = (TargetDTO) targetBuilder.withTarget(updatedTargetName).withCountry(countryDTOExpected)
                .build(ObjectType.DTO);

        CountryNode countryNode = (CountryNode) countryBuilder.withName(countryName).build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withName(updatedCountryName).build(ObjectType.NODE);

        TargetNode targetNodeToUpdate = (TargetNode) targetBuilder.withId(targetId).withTarget(targetName).withCountry(countryNode)
                .build(ObjectType.NODE);
        TargetNode targetNodeExpectedBeforeSetCountry = (TargetNode) targetBuilder.withId(null).withTarget(updatedTargetName)
                .withCountry(null).build(ObjectType.NODE);
        TargetNode targetNodeExpectedBeforeSave = (TargetNode) targetBuilder.withId(null).withTarget(updatedTargetName)
                .withCountry(countryNodeExpected).build(ObjectType.NODE);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withId(targetId).withTarget(updatedTargetName)
                .withCountry(countryNodeExpected).build(ObjectType.NODE);

        when(objectMapper.map(targetDTOExpected, TargetNode.class)).thenReturn(targetNodeExpectedBeforeSetCountry);
        when(countryService.findByName(countryDTOExpected.getName())).thenReturn(Optional.of(countryNodeExpected));
        when(targetRepository.save(targetNodeExpectedBeforeSave)).thenReturn(targetNodeExpected);

        TargetNode targetNodeActual = targetService.update(targetNodeToUpdate, targetDTOExpected);

        assertAll(
                () -> assertEquals(targetNodeExpected.getId(), targetNodeActual.getId(),
                        () -> "should return target node with id: " + targetNodeExpected.getId() + ", but was: "
                                + targetNodeActual.getId()),
                () -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
                        () -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: " + targetNodeActual.getTarget()),
                () -> assertEquals(targetNodeExpected.getCountryOfOrigin(), targetNodeActual.getCountryOfOrigin(),
                        () -> "should return target node with country: " + targetNodeExpected.getCountryOfOrigin() + ", but was: " + targetNodeActual.getCountryOfOrigin()),
                () -> assertEquals(targetNodeExpected.getCountryOfOrigin().getId(), targetNodeActual.getCountryOfOrigin().getId(),
                        () -> "should return target node with country id: " + targetNodeExpected.getCountryOfOrigin().getId()
                                + ", but was: " + targetNodeActual.getId()),
                () -> assertEquals(targetNodeExpected.getCountryOfOrigin().getName(), targetNodeActual.getCountryOfOrigin().getName(),
                        () -> "should return target node with country name: " + targetNodeExpected.getCountryOfOrigin().getName()
                                + ", but was: " + targetNodeActual.getCountryOfOrigin()),
                () -> verify(objectMapper, times(1)).map(targetDTOExpected, TargetNode.class),
                () -> verifyNoMoreInteractions(objectMapper),
                () -> verify(countryService, times(1)).findByName(countryDTOExpected.getName()),
                () -> verifyNoMoreInteractions(countryService),
                () -> verify(targetRepository, times(1)).save(targetNodeExpectedBeforeSave));
    }

    @Test
    void when_save_target_should_return_saved_target() {

        Long targetId = 1L;
        String targetName = "Target";

        TargetNode targetNodeExpectedBeforeSave = (TargetNode) targetBuilder.withId(null).withTarget(targetName).build(ObjectType.NODE);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withId(targetId).withTarget(targetName).build(ObjectType.NODE);

        when(targetRepository.save(targetNodeExpectedBeforeSave)).thenReturn(targetNodeExpected);

        TargetNode targetNodeActual = targetService.save(targetNodeExpectedBeforeSave);

        assertAll(
                () -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
                        () -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + targetNodeActual.getTarget()),
                () -> assertNotNull(targetNodeActual.getId(),
                        () -> "should return target node with new id, but was: " + targetNodeActual.getId()),
                () -> verify(targetRepository, times(1)).save(targetNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(targetRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(countryService));
    }

    @Test
    void when_delete_target_by_id_target_should_delete_and_return_target() {

        String targetName = "Target";

        Long targetId = 1L;

        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withId(targetId).withTarget(targetName)
                .build(ObjectType.NODE);

        when(targetRepository.findById(targetId)).thenReturn(Optional.of(targetNodeExpected));

        Optional<TargetNode> targetNodeOptional = targetService.delete(targetId);

        TargetNode targetNodeActual = targetNodeOptional.get();

        assertAll(
                () -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
                        () -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + targetNodeActual.getTarget()),
                () -> verify(targetRepository, times(1)).findById(targetId),
                () -> verify(targetRepository, times(1)).delete(targetNodeActual),
                () -> verifyNoMoreInteractions(targetRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(countryService));
    }

    @Test
    void when_delete_target_by_id_not_existing_target_should_return_empty_optional() {

        Long targetId = 1L;

        when(targetRepository.findById(targetId)).thenReturn(Optional.empty());

        Optional<TargetNode> targetNodeOptional = targetService.delete(targetId);

        assertAll(
                () -> assertTrue(targetNodeOptional.isEmpty(),
                        () -> "should return empty target node optional, but was: " + targetNodeOptional.get()),
                () -> verify(targetRepository, times(1)).findById(targetId),
                () -> verifyNoMoreInteractions(targetRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(countryService));
    }

    @Test
    void when_checking_if_database_is_empty_and_it_is_empty_should_return_true() {

        Long databaseSize = 0L;

        when(targetRepository.count()).thenReturn(databaseSize);

        boolean isDatabaseEmpty = targetService.isDatabaseEmpty();

        assertAll(() -> assertTrue(isDatabaseEmpty, () -> "should database be empty, but that was: " + isDatabaseEmpty),
                () -> verify(targetRepository, times(1)).count(),
                () -> verifyNoMoreInteractions(targetRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(countryService));
    }

    @Test
    void when_checking_if_database_is_empty_and_it_is_not_empty_should_return_false() {

        Long databaseSize = 10L;

        when(targetRepository.count()).thenReturn(databaseSize);

        boolean isDatabaseEmpty = targetService.isDatabaseEmpty();

        assertAll(
                () -> assertFalse(isDatabaseEmpty,
                        () -> "should not database be empty, but that was: " + isDatabaseEmpty),
                () -> verify(targetRepository, times(1)).count(),
                () -> verifyNoMoreInteractions(targetRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(countryService));
    }
}
