package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.*;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.*;
import com.NowakArtur97.GlobalTerrorismAPI.repository.GroupRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GroupService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.*;
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
@Tag("GroupServiceImpl_Tests")
class GroupServiceImplTest {

    private final int DEFAULT_DEPTH_FOR_JSON_PATCH = 4;

    private GroupService groupService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private GenericService<EventNode, EventDTO> eventService;

    private static RegionBuilder regionBuilder;
    private static CountryBuilder countryBuilder;
    private static TargetBuilder targetBuilder;
    private static ProvinceBuilder provinceBuilder;
    private static CityBuilder cityBuilder;
    private static EventBuilder eventBuilder;
    private static GroupBuilder groupBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        regionBuilder = new RegionBuilder();
        countryBuilder = new CountryBuilder();
        targetBuilder = new TargetBuilder();
        provinceBuilder = new ProvinceBuilder();
        cityBuilder = new CityBuilder();
        eventBuilder = new EventBuilder();
        groupBuilder = new GroupBuilder();
    }

    @BeforeEach
    private void setUp() {

        groupService = new GroupServiceImpl(groupRepository, objectMapper, eventService);
    }

    @Test
    void when_groups_exist_and_return_all_groups_should_return_groups() {

        List<GroupNode> groupsListExpected = new ArrayList<>();

        GroupNode group1 = new GroupNode("group1");
        GroupNode group2 = new GroupNode("group2");
        GroupNode group3 = new GroupNode("group3");

        groupsListExpected.add(group1);
        groupsListExpected.add(group2);
        groupsListExpected.add(group3);

        Page<GroupNode> groupsExpected = new PageImpl<>(groupsListExpected);

        Pageable pageable = PageRequest.of(0, 100);

        when(groupRepository.findAll(pageable)).thenReturn(groupsExpected);

        Page<GroupNode> groupsActual = groupService.findAll(pageable);

        assertAll(() -> assertNotNull(groupsActual, () -> "shouldn't return null"),
                () -> assertEquals(groupsListExpected, groupsActual.getContent(),
                        () -> "should contain: " + groupsListExpected + ", but was: " + groupsActual.getContent()),
                () -> assertEquals(groupsExpected.getNumberOfElements(), groupsActual.getNumberOfElements(),
                        () -> "should return page with: " + groupsExpected.getNumberOfElements()
                                + " elements, but was: " + groupsActual.getNumberOfElements()),
                () -> verify(groupRepository, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(groupRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(eventService));
    }

    @Test
    void when_groups_not_exist_and_return_all_groups_should_not_return_any_groups() {

        List<GroupNode> groupsListExpected = new ArrayList<>();

        Page<GroupNode> groupsExpected = new PageImpl<>(groupsListExpected);

        Pageable pageable = PageRequest.of(0, 100);

        when(groupRepository.findAll(pageable)).thenReturn(groupsExpected);

        Page<GroupNode> groupsActual = groupService.findAll(pageable);

        assertAll(() -> assertNotNull(groupsActual, () -> "shouldn't return null"),
                () -> assertEquals(groupsListExpected, groupsActual.getContent(),
                        () -> "should contain empty list, but was: " + groupsActual.getContent()),
                () -> assertEquals(groupsListExpected, groupsActual.getContent(),
                        () -> "should contain: " + groupsListExpected + ", but was: " + groupsActual.getContent()),
                () -> assertEquals(groupsExpected.getNumberOfElements(), groupsActual.getNumberOfElements(),
                        () -> "should return empty page, but was: " + groupsActual.getNumberOfElements()),
                () -> verify(groupRepository, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(groupRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(eventService));
    }

    @Test
    void when_group_exists_and_return_one_group_should_return_one_group() {

        Long expectedGroupId = 1L;

        EventNode eventNode = (EventNode) eventBuilder.build(ObjectType.NODE);
        GroupNode groupNodeExpected = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);

        when(groupRepository.findById(expectedGroupId)).thenReturn(Optional.of(groupNodeExpected));

        Optional<GroupNode> groupActualOptional = groupService.findById(expectedGroupId);

        GroupNode groupNodeActual = groupActualOptional.get();

        assertAll(() -> assertEquals(groupNodeExpected.getId(), groupNodeActual.getId(),
                () -> "should return group node with id: " + expectedGroupId + ", but was" + groupNodeActual.getId()),
                () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                        () -> "should return group node with name: " + groupNodeExpected.getName() + ", but was"
                                + groupNodeActual.getName()),

                () -> assertEquals(eventNode.getId(), groupNodeActual.getEventsCaused().get(0).getId(), () -> "should return group node with event node with id: " + eventNode.getId() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getId()),
                () -> assertEquals(eventNode.getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group node with event node with summary: " + eventNode.getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(eventNode.getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(), () -> "should return group node with event node with motive: " + eventNode.getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(eventNode.getDate(), groupNodeActual.getEventsCaused().get(0).getDate(), () -> "should return group node with event node with date: " + eventNode.getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(eventNode.getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(), () -> "should return group node with event node which was part of multiple incidents: " + eventNode.getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNode.getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group node with event node which was successful: " + eventNode.getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(eventNode.getIsSuicidal(), groupNodeActual.getEventsCaused().get(0).getIsSuicidal(), () -> "should return group node with event node which was suicidal: " + eventNode.getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNull(groupNodeActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group node with event node with null target, but was: " + groupNodeActual.getEventsCaused().get(0).getTarget()),
                () -> assertNull(groupNodeActual.getEventsCaused().get(0).getCity(),
                        () -> "should return group node with event node with null city, but was: " + groupNodeActual.getEventsCaused().get(0).getCity()),
                () -> verify(groupRepository, times(1)).findById(expectedGroupId),
                () -> verifyNoMoreInteractions(groupRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(eventService));
    }

    @Test
    void when_group_does_not_exist_and_return_one_group_should_return_empty_optional() {

        Long expectedGroupId = 1L;

        when(groupRepository.findById(expectedGroupId)).thenReturn(Optional.empty());

        Optional<GroupNode> groupActualOptional = groupService.findById(expectedGroupId);

        assertAll(() -> assertTrue(groupActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(groupRepository, times(1)).findById(expectedGroupId),
                () -> verifyNoMoreInteractions(groupRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(eventService));
    }

    @Test
    void when_group_exists_and_return_one_group_with_depth_should_return_one_group_with_events() {

        Long expectedGroupId = 1L;

        RegionNode regionNode = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNode = (CountryNode) countryBuilder.withRegion(regionNode).build(ObjectType.NODE);
        TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNode).build(ObjectType.NODE);
        CityNode cityNode = (CityNode) cityBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).withCity(cityNode).build(ObjectType.NODE);
        GroupNode groupNodeExpected = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);

        when(groupRepository.findById(expectedGroupId, DEFAULT_DEPTH_FOR_JSON_PATCH)).thenReturn(Optional.of(groupNodeExpected));

        Optional<GroupNode> groupActualOptional = groupService.findById(expectedGroupId, DEFAULT_DEPTH_FOR_JSON_PATCH);

        GroupNode groupNodeActual = groupActualOptional.get();

        assertAll(() -> assertEquals(groupNodeExpected.getId(), groupNodeActual.getId(),
                () -> "should return group node with id: " + expectedGroupId + ", but was" + groupNodeActual.getId()),
                () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                        () -> "should return group node with name: " + groupNodeExpected.getName() + ", but was"
                                + groupNodeActual.getName()),

                () -> assertEquals(eventNode.getId(), groupNodeActual.getEventsCaused().get(0).getId(), () -> "should return group node with event node with id: " + eventNode.getId() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getId()),
                () -> assertEquals(eventNode.getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group node with event node with summary: " + eventNode.getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(eventNode.getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(), () -> "should return group node with event node with motive: " + eventNode.getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(eventNode.getDate(), groupNodeActual.getEventsCaused().get(0).getDate(), () -> "should return group node with event node with date: " + eventNode.getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(eventNode.getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(), () -> "should return group node with event node which was part of multiple incidents: " + eventNode.getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNode.getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group node with event node which was successful: " + eventNode.getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(eventNode.getIsSuicidal(), groupNodeActual.getEventsCaused().get(0).getIsSuicidal(), () -> "should return group node with event node which was suicidal: " + eventNode.getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicidal()),

                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group node with event node with not null target, but was: null"),
                () -> assertEquals(targetNode, groupNodeActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group node with event target: " + groupNodeActual.getEventsCaused().get(0).getTarget() + ", but was: "
                                + targetNode),
                () -> assertEquals(targetNode.getId(), groupNodeActual.getEventsCaused().get(0).getTarget().getId(),
                        () -> "should return group node with event target id: " + groupNodeActual.getEventsCaused().get(0).getTarget().getId() + ", but was: "
                                + targetNode.getId()),
                () -> assertEquals(targetNode.getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget().getTarget(),
                        () -> "should return group node with event target name: " + groupNodeActual.getEventsCaused().get(0).getTarget().getTarget() + ", but was: "
                                + targetNode.getTarget()),

                () -> assertEquals(countryNode, groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin(),
                        () -> "should return group node with event node with country: " + countryNode + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),
                () -> assertEquals(countryNode.getId(), groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getId(),
                        () -> "should return group node with event node with country id: " + countryNode.getId()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getId()),
                () -> assertEquals(countryNode.getName(), groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getName(),
                        () -> "should return group node with event node with country name: " + countryNode.getName()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),

                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getCity(),
                        () -> "should return group node with event node with not null city, but was: null"),
                () -> assertEquals(cityNode, groupNodeActual.getEventsCaused().get(0).getCity(),
                        () -> "should return group node with event city: " + groupNodeActual.getEventsCaused().get(0).getCity() + ", but was: "
                                + cityNode),
                () -> assertEquals(cityNode.getId(), groupNodeActual.getEventsCaused().get(0).getCity().getId(),
                        () -> "should return group node with event city id: " + groupNodeActual.getEventsCaused().get(0).getCity().getId() + ", but was: "
                                + cityNode.getId()),
                () -> assertEquals(cityNode.getName(), groupNodeActual.getEventsCaused().get(0).getCity().getName(),
                        () -> "should return group node with event city name: " + groupNodeActual.getEventsCaused().get(0).getCity().getName() + ", but was: "
                                + cityNode.getName()),
                () -> assertEquals(cityNode.getLatitude(), groupNodeActual.getEventsCaused().get(0).getCity().getLatitude(),
                        () -> "should return group node with event city latitude: " + groupNodeActual.getEventsCaused().get(0).getCity().getLatitude() + ", but was: "
                                + cityNode.getLatitude()),
                () -> assertEquals(cityNode.getLongitude(), groupNodeActual.getEventsCaused().get(0).getCity().getLongitude(),
                        () -> "should return group node with event city longitude: " + groupNodeActual.getEventsCaused().get(0).getCity().getLongitude() + ", but was: "
                                + cityNode.getLongitude()),
                () -> verify(groupRepository, times(1))
                        .findById(expectedGroupId, DEFAULT_DEPTH_FOR_JSON_PATCH),
                () -> verifyNoMoreInteractions(groupRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(eventService));
    }

    @Test
    void when_save_group_with_events_should_save_group_and_events() {

        CountryNode countryNode = (CountryNode) countryBuilder.build(ObjectType.NODE);
        TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNode).build(ObjectType.NODE);
        CityNode cityNode = (CityNode) cityBuilder.build(ObjectType.NODE);
        EventNode eventNodeBeforeSave = (EventNode) eventBuilder.withId(null).withTarget(targetNode).withCity(cityNode)
                .build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).withCity(cityNode)
                .build(ObjectType.NODE);
        GroupNode groupNodeExpectedBeforeSave = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
        GroupNode groupNodeExpected = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);

        when(eventService.save(eventNodeBeforeSave)).thenReturn(eventNode);
        when(groupRepository.save(groupNodeExpectedBeforeSave)).thenReturn(groupNodeExpected);

        GroupNode groupNodeActual = groupService.save(groupNodeExpectedBeforeSave);

        assertAll(
                () -> assertNotNull(groupNodeActual.getId(),
                        () -> "should return group node with id, but was null"),
                () -> assertEquals(groupNodeExpected.getId(), groupNodeActual.getId(),
                        () -> "should return group node with id: " + groupNodeExpected.getId() + ", but was" + groupNodeActual.getId()),
                () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                        () -> "should return group node with name: " + groupNodeExpected.getName() + ", but was"
                                + groupNodeActual.getName()),

                () -> assertEquals(eventNode.getId(), groupNodeActual.getEventsCaused().get(0).getId(), () -> "should return group node with event node with id: " + eventNode.getId() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getId()),
                () -> assertEquals(eventNode.getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group node with event node with summary: " + eventNode.getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(eventNode.getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(), () -> "should return group node with event node with motive: " + eventNode.getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(eventNode.getDate(), groupNodeActual.getEventsCaused().get(0).getDate(), () -> "should return group node with event node with date: " + eventNode.getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(eventNode.getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(), () -> "should return group node with event node which was part of multiple incidents: " + eventNode.getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNode.getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group node with event node which was successful: " + eventNode.getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(eventNode.getIsSuicidal(), groupNodeActual.getEventsCaused().get(0).getIsSuicidal(), () -> "should return group node with event node which was suicidal: " + eventNode.getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicidal()),

                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group node with event node with not null target, but was: null"),
                () -> assertEquals(targetNode, groupNodeActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group node with event target: " + groupNodeActual.getEventsCaused().get(0).getTarget() + ", but was: "
                                + targetNode),
                () -> assertEquals(targetNode.getId(), groupNodeActual.getEventsCaused().get(0).getTarget().getId(),
                        () -> "should return group node with event target id: " + groupNodeActual.getEventsCaused().get(0).getTarget().getId() + ", but was: "
                                + targetNode.getId()),
                () -> assertEquals(targetNode.getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget().getTarget(),
                        () -> "should return group node with event target name: " + groupNodeActual.getEventsCaused().get(0).getTarget().getTarget() + ", but was: "
                                + targetNode.getTarget()),

                () -> assertEquals(countryNode, groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin(),
                        () -> "should return group node with event node with country: " + countryNode + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),
                () -> assertEquals(countryNode.getId(), groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getId(),
                        () -> "should return group node with event node with country id: " + countryNode.getId()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getId()),
                () -> assertEquals(countryNode.getName(), groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getName(),
                        () -> "should return group node with event node with country name: " + countryNode.getName()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),

                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getCity(),
                        () -> "should return group node with event node with not null city, but was: null"),
                () -> assertEquals(cityNode, groupNodeActual.getEventsCaused().get(0).getCity(),
                        () -> "should return group node with event city: " + groupNodeActual.getEventsCaused().get(0).getCity() + ", but was: "
                                + cityNode),
                () -> assertEquals(cityNode.getId(), groupNodeActual.getEventsCaused().get(0).getCity().getId(),
                        () -> "should return group node with event city id: " + groupNodeActual.getEventsCaused().get(0).getCity().getId() + ", but was: "
                                + cityNode.getId()),
                () -> assertEquals(cityNode.getName(), groupNodeActual.getEventsCaused().get(0).getCity().getName(),
                        () -> "should return group node with event city name: " + groupNodeActual.getEventsCaused().get(0).getCity().getName() + ", but was: "
                                + cityNode.getName()),
                () -> assertEquals(cityNode.getLatitude(), groupNodeActual.getEventsCaused().get(0).getCity().getLatitude(),
                        () -> "should return group node with event city latitude: " + groupNodeActual.getEventsCaused().get(0).getCity().getLatitude() + ", but was: "
                                + cityNode.getLatitude()),
                () -> assertEquals(cityNode.getLongitude(), groupNodeActual.getEventsCaused().get(0).getCity().getLongitude(),
                        () -> "should return group node with event city longitude: " + groupNodeActual.getEventsCaused().get(0).getCity().getLongitude() + ", but was: "
                                + cityNode.getLongitude()),
                () -> verify(eventService, times(1)).save(eventNodeBeforeSave),
                () -> verifyNoMoreInteractions(eventService),
                () -> verify(groupRepository, times(1)).save(groupNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(groupRepository),
                () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_save_new_group_with_events_should_save_new_group_and_events() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);
        GroupDTO groupDTOExpected = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        CountryNode countryNode = (CountryNode) countryBuilder.build(ObjectType.NODE);
        TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNode).build(ObjectType.NODE);
        CityNode cityNode = (CityNode) cityBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).withCity(cityNode).build(ObjectType.NODE);
        GroupNode groupNodeExpectedBeforeSave = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
        GroupNode groupNodeExpected = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);

        when(objectMapper.map(groupDTOExpected, GroupNode.class)).thenReturn(groupNodeExpectedBeforeSave);
        when(eventService.saveNew(eventDTO)).thenReturn(eventNode);
        when(groupRepository.save(groupNodeExpectedBeforeSave)).thenReturn(groupNodeExpected);

        GroupNode groupNodeActual = groupService.saveNew(groupDTOExpected);

        assertAll(
                () -> assertNotNull(groupNodeActual.getId(),
                        () -> "should return group node with id, but was null"),
                () -> assertEquals(groupNodeExpected.getId(), groupNodeActual.getId(),
                        () -> "should return group node with id: " + groupNodeExpected.getId() + ", but was" + groupNodeActual.getId()),
                () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                        () -> "should return group node with name: " + groupNodeExpected.getName() + ", but was"
                                + groupNodeActual.getName()),

                () -> assertEquals(eventNode.getId(), groupNodeActual.getEventsCaused().get(0).getId(), () -> "should return group node with event node with id: " + eventNode.getId() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getId()),
                () -> assertEquals(eventNode.getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group node with event node with summary: " + eventNode.getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(eventNode.getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(), () -> "should return group node with event node with motive: " + eventNode.getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(eventNode.getDate(), groupNodeActual.getEventsCaused().get(0).getDate(), () -> "should return group node with event node with date: " + eventNode.getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(eventNode.getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(), () -> "should return group node with event node which was part of multiple incidents: " + eventNode.getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNode.getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group node with event node which was successful: " + eventNode.getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(eventNode.getIsSuicidal(), groupNodeActual.getEventsCaused().get(0).getIsSuicidal(), () -> "should return group node with event node which was suicidal: " + eventNode.getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicidal()),

                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group node with event node with not null target, but was: null"),
                () -> assertEquals(targetNode, groupNodeActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group node with event target: " + groupNodeActual.getEventsCaused().get(0).getTarget() + ", but was: "
                                + targetNode),
                () -> assertEquals(targetNode.getId(), groupNodeActual.getEventsCaused().get(0).getTarget().getId(),
                        () -> "should return group node with event target id: " + groupNodeActual.getEventsCaused().get(0).getTarget().getId() + ", but was: "
                                + targetNode.getId()),
                () -> assertEquals(targetNode.getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget().getTarget(),
                        () -> "should return group node with event target name: " + groupNodeActual.getEventsCaused().get(0).getTarget().getTarget() + ", but was: "
                                + targetNode.getTarget()),

                () -> assertEquals(countryNode, groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin(),
                        () -> "should return group node with event node with country: " + countryNode + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),
                () -> assertEquals(countryNode.getId(), groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getId(),
                        () -> "should return group node with event node with country id: " + countryNode.getId()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getId()),
                () -> assertEquals(countryNode.getName(), groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getName(),
                        () -> "should return group node with event node with country name: " + countryNode.getName()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),

                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getCity(),
                        () -> "should return group node with event node with not null city, but was: null"),
                () -> assertEquals(cityNode, groupNodeActual.getEventsCaused().get(0).getCity(),
                        () -> "should return group node with event city: " + groupNodeActual.getEventsCaused().get(0).getCity() + ", but was: "
                                + cityNode),
                () -> assertEquals(cityNode.getId(), groupNodeActual.getEventsCaused().get(0).getCity().getId(),
                        () -> "should return group node with event city id: " + groupNodeActual.getEventsCaused().get(0).getCity().getId() + ", but was: "
                                + cityNode.getId()),
                () -> assertEquals(cityNode.getName(), groupNodeActual.getEventsCaused().get(0).getCity().getName(),
                        () -> "should return group node with event city name: " + groupNodeActual.getEventsCaused().get(0).getCity().getName() + ", but was: "
                                + cityNode.getName()),
                () -> assertEquals(cityNode.getLatitude(), groupNodeActual.getEventsCaused().get(0).getCity().getLatitude(),
                        () -> "should return group node with event city latitude: " + groupNodeActual.getEventsCaused().get(0).getCity().getLatitude() + ", but was: "
                                + cityNode.getLatitude()),
                () -> assertEquals(cityNode.getLongitude(), groupNodeActual.getEventsCaused().get(0).getCity().getLongitude(),
                        () -> "should return group node with event city longitude: " + groupNodeActual.getEventsCaused().get(0).getCity().getLongitude() + ", but was: "
                                + cityNode.getLongitude()),
                () -> verify(objectMapper, times(1)).map(groupDTOExpected, GroupNode.class),
                () -> verifyNoMoreInteractions(objectMapper),
                () -> verify(eventService, times(1)).saveNew(eventDTO),
                () -> verifyNoMoreInteractions(eventService),
                () -> verify(groupRepository, times(1)).save(groupNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(groupRepository));
    }

    @Test
    void when_update_group_with_events_should_update_group_and_events() {

        String updatedCountryName = "country updated";
        String updatedTargetName = "target2";
        String updatedCityName = "city2";
        double updatedCityLatitude = 13.0;
        double updatedCityLongitude = -11.0;

        CountryNode countryNode = (CountryNode) countryBuilder.build(ObjectType.NODE);
        TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNode).build(ObjectType.NODE);
        CountryNode updatedCountryNode = (CountryNode) countryBuilder.withName(updatedCountryName).build(ObjectType.NODE);
        TargetNode updatedTargetNode = (TargetNode) targetBuilder.withTarget(updatedTargetName)
                .withCountry(updatedCountryNode).build(ObjectType.NODE);
        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(updatedCountryName).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget(updatedTargetName).withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withName(updatedCityName).withLatitude(updatedCityLatitude)
                .withLongitude(updatedCityLongitude).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);
        CityNode cityNode = (CityNode) cityBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).withCity(cityNode).build(ObjectType.NODE);
        CityNode updatedCityNode = (CityNode) cityBuilder.withName(updatedCityName).withLatitude(updatedCityLatitude)
                .withLongitude(updatedCityLongitude).build(ObjectType.NODE);
        EventNode updatedEventNode = (EventNode) eventBuilder.withTarget(updatedTargetNode).withCity(updatedCityNode).build(ObjectType.NODE);

        String updatedCountryName2 = "country updated 2";
        String updatedTargetName2 = "target3";
        String updatedCityName2 = "city2";
        double updatedCityLatitude2 = 23.0;
        double updatedCityLongitude2 = -21.0;
        CountryNode countryNode2 = (CountryNode) countryBuilder.build(ObjectType.NODE);
        TargetNode targetNode2 = (TargetNode) targetBuilder.withCountry(countryNode2).build(ObjectType.NODE);
        CountryNode updatedCountryNode2 = (CountryNode) countryBuilder.withName(updatedCountryName2).build(ObjectType.NODE);
        TargetNode updatedTargetNode2 = (TargetNode) targetBuilder.withTarget(updatedTargetName2)
                .withCountry(updatedCountryNode2).build(ObjectType.NODE);
        CountryDTO countryDTO2 = (CountryDTO) countryBuilder.withName(updatedCountryName2).build(ObjectType.DTO);
        TargetDTO targetDTO2 = (TargetDTO) targetBuilder.withTarget(updatedTargetName2).withCountry(countryDTO2).build(ObjectType.DTO);
        CityDTO cityDTO2 = (CityDTO) cityBuilder.withName(updatedCityName2).withLatitude(updatedCityLatitude2)
                .withLongitude(updatedCityLongitude2).build(ObjectType.DTO);
        EventDTO eventDTO2 = (EventDTO) eventBuilder.withTarget(targetDTO2).withCity(cityDTO2).build(ObjectType.DTO);
        CityNode cityNode2 = (CityNode) cityBuilder.build(ObjectType.NODE);
        EventNode eventNode2 = (EventNode) eventBuilder.withTarget(targetNode2).withCity(cityNode2).build(ObjectType.NODE);
        CityNode updatedCityNode2 = (CityNode) cityBuilder.withName(updatedCityName2).withLatitude(updatedCityLatitude2)
                .withLongitude(updatedCityLongitude2).build(ObjectType.NODE);
        EventNode updatedEventNode2 = (EventNode) eventBuilder.withTarget(updatedTargetNode2).withCity(updatedCityNode2).build(ObjectType.NODE);

        String updatedGroupName = "new group name";
        GroupDTO groupDTOExpected = (GroupDTO) groupBuilder.withName(updatedGroupName).withEventsCaused(List.of(eventDTO, eventDTO2)).build(ObjectType.DTO);
        GroupNode groupNodeExpectedBeforeMethod = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
        GroupNode groupNodeExpectedBeforeSetId = (GroupNode) groupBuilder.withId(null).withName(updatedGroupName).withEventsCaused(List.of(eventNode, eventNode2)).build(ObjectType.NODE);
        GroupNode groupNodeExpected = (GroupNode) groupBuilder.withName(updatedGroupName).withEventsCaused(List.of(updatedEventNode, updatedEventNode2)).build(ObjectType.NODE);

        when(eventService.saveNew(eventDTO)).thenReturn(updatedEventNode);
        when(eventService.saveNew(eventDTO2)).thenReturn(updatedEventNode2);
        when(objectMapper.map(groupDTOExpected, GroupNode.class)).thenReturn(groupNodeExpectedBeforeSetId);
        when(groupRepository.save(groupNodeExpectedBeforeSetId)).thenReturn(groupNodeExpected);

        GroupNode groupNodeActual = groupService.update(groupNodeExpectedBeforeMethod, groupDTOExpected);

        assertAll(
                () -> assertNotNull(groupNodeActual.getId(),
                        () -> "should return group node with id, but was null"),
                () -> assertEquals(groupNodeExpected.getId(), groupNodeActual.getId(),
                        () -> "should return group node with id: " + groupNodeExpected.getId() + ", but was" + groupNodeActual.getId()),
                () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                        () -> "should return group node with name: " + groupNodeExpected.getName() + ", but was"
                                + groupNodeActual.getName()),

                () -> assertEquals(updatedEventNode.getId(), groupNodeActual.getEventsCaused().get(0).getId(), () -> "should return group node with event node with id: " + updatedEventNode.getId() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getId()),
                () -> assertEquals(updatedEventNode.getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group node with event node with summary: " + updatedEventNode.getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(updatedEventNode.getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(), () -> "should return group node with event node with motive: " + updatedEventNode.getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(updatedEventNode.getDate(), groupNodeActual.getEventsCaused().get(0).getDate(), () -> "should return group node with event node with date: " + updatedEventNode.getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(updatedEventNode.getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(), () -> "should return group node with event node which was part of multiple incidents: " + updatedEventNode.getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(updatedEventNode.getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group node with event node which was successful: " + updatedEventNode.getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(updatedEventNode.getIsSuicidal(), groupNodeActual.getEventsCaused().get(0).getIsSuicidal(), () -> "should return group node with event node which was suicidal: " + updatedEventNode.getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group node with event node with not null target, but was: null"),
                () -> assertEquals(updatedTargetNode, groupNodeActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group node with event target: " + groupNodeActual.getEventsCaused().get(0).getTarget() + ", but was: "
                                + updatedTargetNode),
                () -> assertEquals(updatedTargetNode.getId(), groupNodeActual.getEventsCaused().get(0).getTarget().getId(),
                        () -> "should return group node with event target id: " + groupNodeActual.getEventsCaused().get(0).getTarget().getId() + ", but was: "
                                + updatedTargetNode.getId()),
                () -> assertEquals(updatedTargetNode.getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget().getTarget(),
                        () -> "should return group node with event target name: " + groupNodeActual.getEventsCaused().get(0).getTarget().getTarget() + ", but was: "
                                + updatedTargetNode.getTarget()),

                () -> assertEquals(updatedCountryNode, groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin(),
                        () -> "should return group node with event node with country: " + updatedCountryNode + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),
                () -> assertEquals(updatedCountryNode.getId(), groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getId(),
                        () -> "should return group node with event node with country id: " + updatedCountryNode.getId()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getId()),
                () -> assertEquals(updatedCountryNode.getName(), groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getName(),
                        () -> "should return group node with event node with country name: " + updatedCountryNode.getName()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),

                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getCity(),
                        () -> "should return group node with event node with not null city, but was: null"),
                () -> assertEquals(updatedCityNode, groupNodeActual.getEventsCaused().get(0).getCity(),
                        () -> "should return group node with event city: " + groupNodeActual.getEventsCaused().get(0).getCity() + ", but was: "
                                + updatedCityNode),
                () -> assertEquals(updatedCityNode.getId(), groupNodeActual.getEventsCaused().get(0).getCity().getId(),
                        () -> "should return group node with event city id: " + groupNodeActual.getEventsCaused().get(0).getCity().getId() + ", but was: "
                                + updatedCityNode.getId()),
                () -> assertEquals(updatedCityNode.getName(), groupNodeActual.getEventsCaused().get(0).getCity().getName(),
                        () -> "should return group node with event city name: " + groupNodeActual.getEventsCaused().get(0).getCity().getName() + ", but was: "
                                + updatedCityNode.getName()),
                () -> assertEquals(updatedCityNode.getLatitude(), groupNodeActual.getEventsCaused().get(0).getCity().getLatitude(),
                        () -> "should return group node with event city latitude: " + groupNodeActual.getEventsCaused().get(0).getCity().getLatitude() + ", but was: "
                                + updatedCityNode.getLatitude()),
                () -> assertEquals(updatedCityNode.getLongitude(), groupNodeActual.getEventsCaused().get(0).getCity().getLongitude(),
                        () -> "should return group node with event city longitude: " + groupNodeActual.getEventsCaused().get(0).getCity().getLongitude() + ", but was: "
                                + updatedCityNode.getLongitude()),

                () -> assertEquals(updatedEventNode2.getId(), groupNodeActual.getEventsCaused().get(1).getId(), () -> "should return group node with event node with id: " + updatedEventNode2.getId() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getId()),
                () -> assertEquals(updatedEventNode2.getSummary(), groupNodeActual.getEventsCaused().get(1).getSummary(), () -> "should return group node with event node with summary: " + updatedEventNode2.getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getSummary()),
                () -> assertEquals(updatedEventNode2.getMotive(), groupNodeActual.getEventsCaused().get(1).getMotive(), () -> "should return group node with event node with motive: " + updatedEventNode2.getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getMotive()),
                () -> assertEquals(updatedEventNode2.getDate(), groupNodeActual.getEventsCaused().get(1).getDate(), () -> "should return group node with event node with date: " + updatedEventNode2.getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getDate()),
                () -> assertEquals(updatedEventNode2.getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents(), () -> "should return group node with event node which was part of multiple incidents: " + updatedEventNode2.getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents()),
                () -> assertEquals(updatedEventNode2.getIsSuccessful(), groupNodeActual.getEventsCaused().get(1).getIsSuccessful(), () -> "should return group node with event node which was successful: " + updatedEventNode2.getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuccessful()),
                () -> assertEquals(updatedEventNode2.getIsSuicidal(), groupNodeActual.getEventsCaused().get(1).getIsSuicidal(), () -> "should return group node with event node which was suicidal: " + updatedEventNode2.getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuicidal()),
                () -> assertNotNull(groupNodeActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group node with event node with not null target, but was: null"),
                () -> assertEquals(updatedTargetNode2, groupNodeActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group node with event target: " + groupNodeActual.getEventsCaused().get(1).getTarget() + ", but was: "
                                + updatedTargetNode2),
                () -> assertEquals(updatedTargetNode2.getId(), groupNodeActual.getEventsCaused().get(1).getTarget().getId(),
                        () -> "should return group node with event target id: " + groupNodeActual.getEventsCaused().get(1).getTarget().getId() + ", but was: "
                                + updatedTargetNode2.getId()),
                () -> assertEquals(updatedTargetNode2.getTarget(), groupNodeActual.getEventsCaused().get(1).getTarget().getTarget(),
                        () -> "should return group node with event target name: " + groupNodeActual.getEventsCaused().get(1).getTarget().getTarget() + ", but was: "
                                + updatedTargetNode2.getTarget()),

                () -> assertEquals(updatedCountryNode2, groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin(),
                        () -> "should return group node with event node with country: " + updatedCountryNode2 + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin()),
                () -> assertEquals(updatedCountryNode2.getId(), groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getId(),
                        () -> "should return group node with event node with country id: " + updatedCountryNode2.getId()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget().getId()),
                () -> assertEquals(updatedCountryNode2.getName(), groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getName(),
                        () -> "should return group node with event node with country name: " + updatedCountryNode2.getName()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin()),

                () -> assertNotNull(groupNodeActual.getEventsCaused().get(1).getCity(),
                        () -> "should return group node with event node with not null city, but was: null"),
                () -> assertEquals(updatedCityNode2, groupNodeActual.getEventsCaused().get(1).getCity(),
                        () -> "should return group node with event city: " + groupNodeActual.getEventsCaused().get(1).getCity() + ", but was: "
                                + updatedCityNode2),
                () -> assertEquals(updatedCityNode2.getId(), groupNodeActual.getEventsCaused().get(1).getCity().getId(),
                        () -> "should return group node with event city id: " + groupNodeActual.getEventsCaused().get(1).getCity().getId() + ", but was: "
                                + updatedCityNode2.getId()),
                () -> assertEquals(updatedCityNode2.getName(), groupNodeActual.getEventsCaused().get(1).getCity().getName(),
                        () -> "should return group node with event city name: " + groupNodeActual.getEventsCaused().get(1).getCity().getName() + ", but was: "
                                + updatedCityNode2.getName()),
                () -> assertEquals(updatedCityNode2.getLatitude(), groupNodeActual.getEventsCaused().get(1).getCity().getLatitude(),
                        () -> "should return group node with event city latitude: " + groupNodeActual.getEventsCaused().get(1).getCity().getLatitude() + ", but was: "
                                + updatedCityNode2.getLatitude()),
                () -> assertEquals(updatedCityNode2.getLongitude(), groupNodeActual.getEventsCaused().get(1).getCity().getLongitude(),
                        () -> "should return group node with event city longitude: " + groupNodeActual.getEventsCaused().get(1).getCity().getLongitude() + ", but was: "
                                + updatedCityNode2.getLongitude()),
                () -> verify(eventService, times(1)).delete(eventNode.getId()),
                () -> verify(eventService, times(1)).saveNew(eventDTO),
                () -> verify(eventService, times(1)).saveNew(eventDTO2),
                () -> verifyNoMoreInteractions(eventService),
                () -> verify(objectMapper, times(1)).map(groupDTOExpected, GroupNode.class),
                () -> verifyNoMoreInteractions(objectMapper),
                () -> verify(groupRepository, times(1)).save(groupNodeExpectedBeforeSetId),
                () -> verifyNoMoreInteractions(groupRepository));
    }

    @Test
    void when_delete_group_should_delete_group_with_events_and_targets() {

        Long groupId = 1L;
        Long eventId = 1L;
        Long eventId2 = 2L;

        CountryNode countryNode = (CountryNode) countryBuilder.build(ObjectType.NODE);
        TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNode).build(ObjectType.NODE);
        CountryNode countryNode2 = (CountryNode) countryBuilder.build(ObjectType.NODE);
        TargetNode targetNode2 = (TargetNode) targetBuilder.withCountry(countryNode2).build(ObjectType.NODE);
        CityNode cityNode = (CityNode) cityBuilder.build(ObjectType.NODE);
        CityNode cityNode2 = (CityNode) cityBuilder.build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withId(eventId).withTarget(targetNode).withCity(cityNode)
                .build(ObjectType.NODE);
        EventNode eventNodeExpected2 = (EventNode) eventBuilder.withId(eventId2).withTarget(targetNode2).withCity(cityNode2)
                .build(ObjectType.NODE);
        GroupNode groupNodeExpected = (GroupNode) groupBuilder.withId(groupId).withEventsCaused(List.of(eventNodeExpected, eventNodeExpected2)).build(ObjectType.NODE);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(groupNodeExpected));
        when(eventService.delete(eventId)).thenReturn(Optional.of(eventNodeExpected));
        when(eventService.delete(eventId2)).thenReturn(Optional.of(eventNodeExpected2));

        Optional<GroupNode> groupNodeOptionalActual = groupService.delete(eventId);

        GroupNode groupNodeActual = groupNodeOptionalActual.get();

        assertAll(
                () -> assertNotNull(groupNodeActual.getId(),
                        () -> "should return group node with id, but was: " + groupNodeActual.getId()),
                () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                        () -> "should return group node with name: " + groupNodeExpected.getName() + ", but was: "
                                + groupNodeActual.getName()),

                () -> assertEquals(eventNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getId(), () -> "should return group event node with id: " + eventNodeExpected.getId() + ", but was: "
                        + groupNodeActual.getEventsCaused().get(0).getId()),
                () -> assertEquals(eventNodeExpected.getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group event node with summary: " + eventNodeExpected.getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(eventNodeExpected.getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(), () -> "should return group event node with motive: " + eventNodeExpected.getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(eventNodeExpected.getDate(), groupNodeActual.getEventsCaused().get(0).getDate(), () -> "should return group event node with date: " + eventNodeExpected.getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(), () -> "should return group event node which was part of multiple incidents: " + eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected.getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group event node which was successful: " + eventNodeExpected.getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), groupNodeActual.getEventsCaused().get(0).getIsSuicidal(), () -> "should return group event node which was suicidal: " + eventNodeExpected.getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group node with event node with not null target, but was: null"),
                () -> assertEquals(targetNode, groupNodeActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group node with event target: " + groupNodeActual.getEventsCaused().get(0).getTarget() + ", but was: "
                                + targetNode),
                () -> assertEquals(targetNode.getId(), groupNodeActual.getEventsCaused().get(0).getTarget().getId(),
                        () -> "should return group node with event target id: " + groupNodeActual.getEventsCaused().get(0).getTarget().getId() + ", but was: "
                                + targetNode.getId()),
                () -> assertEquals(targetNode.getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget().getTarget(),
                        () -> "should return group node with event target name: " + groupNodeActual.getEventsCaused().get(0).getTarget().getTarget() + ", but was: "
                                + targetNode.getTarget()),

                () -> assertEquals(countryNode, groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin(),
                        () -> "should return group node with event node with country: " + countryNode + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),
                () -> assertEquals(countryNode.getId(), groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getId(),
                        () -> "should return group node with event node with country id: " + countryNode.getId()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getId()),
                () -> assertEquals(countryNode.getName(), groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getName(),
                        () -> "should return group node with event node with country name: " + countryNode.getName()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),

                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getCity(),
                        () -> "should return group node with event node with not null city, but was: null"),
                () -> assertEquals(cityNode, groupNodeActual.getEventsCaused().get(0).getCity(),
                        () -> "should return group node with event city: " + groupNodeActual.getEventsCaused().get(0).getCity() + ", but was: "
                                + cityNode),
                () -> assertEquals(cityNode.getId(), groupNodeActual.getEventsCaused().get(0).getCity().getId(),
                        () -> "should return group node with event city id: " + groupNodeActual.getEventsCaused().get(0).getCity().getId() + ", but was: "
                                + cityNode.getId()),
                () -> assertEquals(cityNode.getName(), groupNodeActual.getEventsCaused().get(0).getCity().getName(),
                        () -> "should return group node with event city name: " + groupNodeActual.getEventsCaused().get(0).getCity().getName() + ", but was: "
                                + cityNode.getName()),
                () -> assertEquals(cityNode.getLatitude(), groupNodeActual.getEventsCaused().get(0).getCity().getLatitude(),
                        () -> "should return group node with event city latitude: " + groupNodeActual.getEventsCaused().get(0).getCity().getLatitude() + ", but was: "
                                + cityNode.getLatitude()),
                () -> assertEquals(cityNode.getLongitude(), groupNodeActual.getEventsCaused().get(0).getCity().getLongitude(),
                        () -> "should return group node with event city longitude: " + groupNodeActual.getEventsCaused().get(0).getCity().getLongitude() + ", but was: "
                                + cityNode.getLongitude()),

                () -> assertEquals(eventNodeExpected2.getId(), groupNodeActual.getEventsCaused().get(1).getId(), () -> "should return group event node with id: " + eventNodeExpected2.getId() + ", but was: "
                        + groupNodeActual.getEventsCaused().get(1).getId()),
                () -> assertEquals(eventNodeExpected2.getSummary(), groupNodeActual.getEventsCaused().get(1).getSummary(), () -> "should return group event node with summary: " + eventNodeExpected2.getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getSummary()),
                () -> assertEquals(eventNodeExpected2.getMotive(), groupNodeActual.getEventsCaused().get(1).getMotive(), () -> "should return group event node with motive: " + eventNodeExpected2.getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getMotive()),
                () -> assertEquals(eventNodeExpected2.getDate(), groupNodeActual.getEventsCaused().get(1).getDate(), () -> "should return group event node with date: " + eventNodeExpected2.getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getDate()),
                () -> assertEquals(eventNodeExpected2.getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents(), () -> "should return group event node which was part of multiple incidents: " + eventNodeExpected2.getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected2.getIsSuccessful(), groupNodeActual.getEventsCaused().get(1).getIsSuccessful(), () -> "should return group event node which was successful: " + eventNodeExpected2.getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuccessful()),
                () -> assertEquals(eventNodeExpected2.getIsSuicidal(), groupNodeActual.getEventsCaused().get(1).getIsSuicidal(), () -> "should return group event node which was suicidal: " + eventNodeExpected2.getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuicidal()),
                () -> assertNotNull(targetNode2,
                        () -> "should return group event node with not null target, but was: null"),
                () -> assertEquals(targetNode2, groupNodeActual.getEventsCaused().get(1).getTarget(), () -> "should return group event node with target: " + targetNode2 + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget()),
                () -> assertNotNull(groupNodeActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group node with event node with not null target, but was: null"),
                () -> assertEquals(targetNode2, groupNodeActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group node with event target: " + groupNodeActual.getEventsCaused().get(1).getTarget() + ", but was: "
                                + targetNode2),
                () -> assertEquals(targetNode2.getId(), groupNodeActual.getEventsCaused().get(1).getTarget().getId(),
                        () -> "should return group node with event target id: " + groupNodeActual.getEventsCaused().get(1).getTarget().getId() + ", but was: "
                                + targetNode2.getId()),
                () -> assertEquals(targetNode2.getTarget(), groupNodeActual.getEventsCaused().get(1).getTarget().getTarget(),
                        () -> "should return group node with event target name: " + groupNodeActual.getEventsCaused().get(1).getTarget().getTarget() + ", but was: "
                                + targetNode2.getTarget()),

                () -> assertEquals(countryNode2, groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin(),
                        () -> "should return group node with event node with country: " + countryNode2 + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin()),
                () -> assertEquals(countryNode2.getId(), groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getId(),
                        () -> "should return group node with event node with country id: " + countryNode.getId()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget().getId()),
                () -> assertEquals(countryNode2.getName(), groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getName(),
                        () -> "should return group node with event node with country name: " + countryNode.getName()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin()),

                () -> assertNotNull(groupNodeActual.getEventsCaused().get(1).getCity(),
                        () -> "should return group node with event node with not null city, but was: null"),
                () -> assertEquals(cityNode2, groupNodeActual.getEventsCaused().get(1).getCity(),
                        () -> "should return group node with event city: " + groupNodeActual.getEventsCaused().get(1).getCity() + ", but was: "
                                + cityNode2),
                () -> assertEquals(cityNode2.getId(), groupNodeActual.getEventsCaused().get(1).getCity().getId(),
                        () -> "should return group node with event city id: " + groupNodeActual.getEventsCaused().get(1).getCity().getId() + ", but was: "
                                + cityNode2.getId()),
                () -> assertEquals(cityNode2.getName(), groupNodeActual.getEventsCaused().get(1).getCity().getName(),
                        () -> "should return group node with event city name: " + groupNodeActual.getEventsCaused().get(1).getCity().getName() + ", but was: "
                                + cityNode2.getName()),
                () -> assertEquals(cityNode2.getLatitude(), groupNodeActual.getEventsCaused().get(1).getCity().getLatitude(),
                        () -> "should return group node with event city latitude: " + groupNodeActual.getEventsCaused().get(1).getCity().getLatitude() + ", but was: "
                                + cityNode2.getLatitude()),
                () -> assertEquals(cityNode2.getLongitude(), groupNodeActual.getEventsCaused().get(1).getCity().getLongitude(),
                        () -> "should return group node with event city longitude: " + groupNodeActual.getEventsCaused().get(1).getCity().getLongitude() + ", but was: "
                                + cityNode2.getLongitude()),
                () -> verify(groupRepository, times(1)).findById(eventId),
                () -> verify(groupRepository, times(1)).delete(groupNodeExpected),
                () -> verifyNoMoreInteractions(groupRepository),
                () -> verify(eventService, times(1)).delete(eventId),
                () -> verify(eventService, times(1)).delete(eventId2),
                () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_delete_group_by_id_but_group_does_not_exist_should_return_empty_optional() {

        Long groupId = 1L;

        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        Optional<GroupNode> groupNodeOptional = groupService.delete(groupId);

        assertAll(
                () -> assertTrue(groupNodeOptional.isEmpty(),
                        () -> "should return empty group node optional, but was: " + groupNodeOptional.get()),
                () -> verify(groupRepository, times(1)).findById(groupId),
                () -> verifyNoMoreInteractions(groupRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(eventService));
    }

    @Test
    void when_add_event_to_group_should_return_group_with_new_event() {

        Long groupId = 1L;
        Long eventId = 1L;
        Long eventId2 = 2L;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName("new country").build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);

        CountryNode countryNodeExpected = (CountryNode) countryBuilder.build(ObjectType.NODE);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withCountry(countryNodeExpected).build(ObjectType.NODE);
        TargetNode newTargetNodeExpected = (TargetNode) targetBuilder.withCountry(countryNodeExpected).build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withId(eventId).withTarget(targetNodeExpected)
                .withCity(cityNodeExpected).build(ObjectType.NODE);
        EventNode newEventNodeExpected = (EventNode) eventBuilder.withId(eventId2).withTarget(newTargetNodeExpected)
                .withCity(cityNodeExpected).build(ObjectType.NODE);
        GroupNode groupNodeExpectedBeforeSave = (GroupNode) groupBuilder.withId(groupId).withEventsCaused(List.of(eventNodeExpected)).build(ObjectType.NODE);
        GroupNode groupNodeExpected = (GroupNode) groupBuilder.withId(groupId).withEventsCaused(List.of(eventNodeExpected, newEventNodeExpected)).build(ObjectType.NODE);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(groupNodeExpectedBeforeSave));
        when(eventService.saveNew(eventDTO)).thenReturn(newEventNodeExpected);
        when(groupRepository.save(groupNodeExpectedBeforeSave)).thenReturn(groupNodeExpected);

        Optional<GroupNode> groupNodeActualOptional = groupService.addEventToGroup(groupId, eventDTO);

        GroupNode groupNodeActual = groupNodeActualOptional.get();

        assertAll(
                () -> assertNotNull(groupNodeActual.getId(),
                        () -> "should return group node with id, but was: " + groupNodeActual.getId()),
                () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                        () -> "should return group node with name: " + groupNodeExpected.getName() + ", but was: "
                                + groupNodeActual.getName()),

                () -> assertEquals(eventNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getId(), () -> "should return group event node with id: " + eventNodeExpected.getId() + ", but was: "
                        + groupNodeActual.getEventsCaused().get(0).getId()),
                () -> assertEquals(eventNodeExpected.getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group event node with summary: " + eventNodeExpected.getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(eventNodeExpected.getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(), () -> "should return group event node with motive: " + eventNodeExpected.getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(eventNodeExpected.getDate(), groupNodeActual.getEventsCaused().get(0).getDate(), () -> "should return group event node with date: " + eventNodeExpected.getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(), () -> "should return group event node which was part of multiple incidents: " + eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected.getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group event node which was successful: " + eventNodeExpected.getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), groupNodeActual.getEventsCaused().get(0).getIsSuicidal(), () -> "should return group event node which was suicidal: " + eventNodeExpected.getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group node with event node with not null target, but was: null"),
                () -> assertEquals(targetNodeExpected, groupNodeActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group node with event target: " + groupNodeActual.getEventsCaused().get(0).getTarget() + ", but was: "
                                + targetNodeExpected),
                () -> assertEquals(targetNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getTarget().getId(),
                        () -> "should return group node with event target id: " + groupNodeActual.getEventsCaused().get(0).getTarget().getId() + ", but was: "
                                + targetNodeExpected.getId()),
                () -> assertEquals(targetNodeExpected.getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget().getTarget(),
                        () -> "should return group node with event target name: " + groupNodeActual.getEventsCaused().get(0).getTarget().getTarget() + ", but was: "
                                + targetNodeExpected.getTarget()),

                () -> assertEquals(countryNodeExpected, groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin(),
                        () -> "should return group node with event node with country: " + countryNodeExpected + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),
                () -> assertEquals(countryNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getId(),
                        () -> "should return group node with event node with country id: " + countryNodeExpected.getId()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getId()),
                () -> assertEquals(countryNodeExpected.getName(), groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getName(),
                        () -> "should return group node with event node with country name: " + countryNodeExpected.getName()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),

                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getCity(),
                        () -> "should return group node with event node with not null city, but was: null"),
                () -> assertEquals(cityNodeExpected, groupNodeActual.getEventsCaused().get(0).getCity(),
                        () -> "should return group node with event city: " + groupNodeActual.getEventsCaused().get(0).getCity() + ", but was: "
                                + cityNodeExpected),
                () -> assertEquals(cityNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getCity().getId(),
                        () -> "should return group node with event city id: " + groupNodeActual.getEventsCaused().get(0).getCity().getId() + ", but was: "
                                + cityNodeExpected.getId()),
                () -> assertEquals(cityNodeExpected.getName(), groupNodeActual.getEventsCaused().get(0).getCity().getName(),
                        () -> "should return group node with event city name: " + groupNodeActual.getEventsCaused().get(0).getCity().getName() + ", but was: "
                                + cityNodeExpected.getName()),
                () -> assertEquals(cityNodeExpected.getLatitude(), groupNodeActual.getEventsCaused().get(0).getCity().getLatitude(),
                        () -> "should return group node with event city latitude: " + groupNodeActual.getEventsCaused().get(0).getCity().getLatitude() + ", but was: "
                                + cityNodeExpected.getLatitude()),
                () -> assertEquals(cityNodeExpected.getLongitude(), groupNodeActual.getEventsCaused().get(0).getCity().getLongitude(),
                        () -> "should return group node with event city longitude: " + groupNodeActual.getEventsCaused().get(0).getCity().getLongitude() + ", but was: "
                                + cityNodeExpected.getLongitude()),

                () -> assertEquals(newEventNodeExpected.getId(), groupNodeActual.getEventsCaused().get(1).getId(), () -> "should return group event node with id: " + newEventNodeExpected.getId() + ", but was: "
                        + groupNodeActual.getEventsCaused().get(1).getId()),
                () -> assertEquals(newEventNodeExpected.getSummary(), groupNodeActual.getEventsCaused().get(1).getSummary(), () -> "should return group event node with summary: " + newEventNodeExpected.getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getSummary()),
                () -> assertEquals(newEventNodeExpected.getMotive(), groupNodeActual.getEventsCaused().get(1).getMotive(), () -> "should return group event node with motive: " + newEventNodeExpected.getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getMotive()),
                () -> assertEquals(newEventNodeExpected.getDate(), groupNodeActual.getEventsCaused().get(1).getDate(), () -> "should return group event node with date: " + newEventNodeExpected.getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getDate()),
                () -> assertEquals(newEventNodeExpected.getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents(), () -> "should return group event node which was part of multiple incidents: " + newEventNodeExpected.getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents()),
                () -> assertEquals(newEventNodeExpected.getIsSuccessful(), groupNodeActual.getEventsCaused().get(1).getIsSuccessful(), () -> "should return group event node which was successful: " + newEventNodeExpected.getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuccessful()),
                () -> assertEquals(newEventNodeExpected.getIsSuicidal(), groupNodeActual.getEventsCaused().get(1).getIsSuicidal(), () -> "should return group event node which was suicidal: " + newEventNodeExpected.getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuicidal()),
                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group node with event node with not null target, but was: null"),
                () -> assertEquals(newTargetNodeExpected, groupNodeActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group node with event target: " + groupNodeActual.getEventsCaused().get(1).getTarget() + ", but was: "
                                + newTargetNodeExpected),
                () -> assertEquals(newTargetNodeExpected.getId(), groupNodeActual.getEventsCaused().get(1).getTarget().getId(),
                        () -> "should return group node with event target id: " + groupNodeActual.getEventsCaused().get(1).getTarget().getId() + ", but was: "
                                + newTargetNodeExpected.getId()),
                () -> assertEquals(newTargetNodeExpected.getTarget(), groupNodeActual.getEventsCaused().get(1).getTarget().getTarget(),
                        () -> "should return group node with event target name: " + groupNodeActual.getEventsCaused().get(1).getTarget().getTarget() + ", but was: "
                                + newTargetNodeExpected.getTarget()),

                () -> assertEquals(newTargetNodeExpected.getCountryOfOrigin(), groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin(),
                        () -> "should return group node with event node with country: " + newTargetNodeExpected.getCountryOfOrigin() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin()),
                () -> assertEquals(newTargetNodeExpected.getCountryOfOrigin().getId(), groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getId(),
                        () -> "should return group node with event node with country id: " + newTargetNodeExpected.getCountryOfOrigin().getId()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget().getId()),
                () -> assertEquals(newTargetNodeExpected.getCountryOfOrigin().getName(), groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getName(),
                        () -> "should return group node with event node with country name: " + newTargetNodeExpected.getCountryOfOrigin().getName()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin()),

                () -> assertNotNull(groupNodeActual.getEventsCaused().get(1).getCity(),
                        () -> "should return group node with event node with not null city, but was: null"),
                () -> assertEquals(newEventNodeExpected.getCity(), groupNodeActual.getEventsCaused().get(1).getCity(),
                        () -> "should return group node with event city: " + groupNodeActual.getEventsCaused().get(1).getCity() + ", but was: "
                                + newEventNodeExpected.getCity()),
                () -> assertEquals(newEventNodeExpected.getCity().getId(), groupNodeActual.getEventsCaused().get(1).getCity().getId(),
                        () -> "should return group node with event city id: " + groupNodeActual.getEventsCaused().get(1).getCity().getId() + ", but was: "
                                + newEventNodeExpected.getCity().getId()),
                () -> assertEquals(newEventNodeExpected.getCity().getName(), groupNodeActual.getEventsCaused().get(1).getCity().getName(),
                        () -> "should return group node with event city name: " + groupNodeActual.getEventsCaused().get(1).getCity().getName() + ", but was: "
                                + newEventNodeExpected.getCity().getName()),
                () -> assertEquals(newEventNodeExpected.getCity().getLatitude(), groupNodeActual.getEventsCaused().get(1).getCity().getLatitude(),
                        () -> "should return group node with event city latitude: " + groupNodeActual.getEventsCaused().get(1).getCity().getLatitude() + ", but was: "
                                + newEventNodeExpected.getCity().getLatitude()),
                () -> assertEquals(newEventNodeExpected.getCity().getLongitude(), groupNodeActual.getEventsCaused().get(1).getCity().getLongitude(),
                        () -> "should return group node with event city longitude: " + groupNodeActual.getEventsCaused().get(1).getCity().getLongitude() + ", but was: "
                                + newEventNodeExpected.getCity().getLongitude()),
                () -> verify(groupRepository, times(1)).findById(groupId),
                () -> verify(groupRepository, times(1)).save(groupNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(groupRepository),
                () -> verify(eventService, times(1)).saveNew(eventDTO),
                () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_add_event_to_group_but_group_does_not_exist_should_throw_error() {

        Long groupId = 1L;

        EventDTO eventDTO = (EventDTO) eventBuilder.build(ObjectType.DTO);

        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        Optional<GroupNode> groupNodeOptional = groupService.addEventToGroup(groupId, eventDTO);

        assertAll(
                () -> assertTrue(groupNodeOptional.isEmpty(),
                        () -> "should return empty group node optional, but was: " + groupNodeOptional.get()),
                () -> verify(groupRepository, times(1)).findById(groupId),
                () -> verifyNoMoreInteractions(groupRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(eventService));
    }

    @Test
    void when_delete_group_events_should_delete_only_group_events_and_targets() {

        Long groupId = 1L;
        Long eventId = 1L;
        Long eventId2 = 2L;

        CountryNode countryNode = (CountryNode) countryBuilder.build(ObjectType.NODE);
        TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNode).build(ObjectType.NODE);
        CountryNode countryNode2 = (CountryNode) countryBuilder.build(ObjectType.NODE);
        TargetNode targetNode2 = (TargetNode) targetBuilder.withCountry(countryNode2).build(ObjectType.NODE);
        CityNode cityNode = (CityNode) cityBuilder.build(ObjectType.NODE);
        CityNode cityNode2 = (CityNode) cityBuilder.build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withId(eventId).withTarget(targetNode).withCity(cityNode).
                build(ObjectType.NODE);
        EventNode eventNodeExpected2 = (EventNode) eventBuilder.withId(eventId2).withTarget(targetNode2).withCity(cityNode2)
                .build(ObjectType.NODE);
        GroupNode groupNodeExpected = (GroupNode) groupBuilder.withId(groupId).withEventsCaused(List.of(eventNodeExpected, eventNodeExpected2)).build(ObjectType.NODE);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(groupNodeExpected));
        when(eventService.delete(eventId)).thenReturn(Optional.of(eventNodeExpected));
        when(eventService.delete(eventId2)).thenReturn(Optional.of(eventNodeExpected2));

        Optional<GroupNode> groupNodeOptionalActual = groupService.deleteAllGroupEvents(eventId);

        GroupNode groupNodeActual = groupNodeOptionalActual.get();

        assertAll(
                () -> assertNotNull(groupNodeActual.getId(),
                        () -> "should return group node with id, but was: " + groupNodeActual.getId()),
                () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                        () -> "should return group node with name: " + groupNodeExpected.getName() + ", but was: "
                                + groupNodeActual.getName()),

                () -> assertEquals(eventNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getId(), () -> "should return group event node with id: " + eventNodeExpected.getId() + ", but was: "
                        + groupNodeActual.getEventsCaused().get(0).getId()),
                () -> assertEquals(eventNodeExpected.getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group event node with summary: " + eventNodeExpected.getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(eventNodeExpected.getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(), () -> "should return group event node with motive: " + eventNodeExpected.getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(eventNodeExpected.getDate(), groupNodeActual.getEventsCaused().get(0).getDate(), () -> "should return group event node with date: " + eventNodeExpected.getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(), () -> "should return group event node which was part of multiple incidents: " + eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected.getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group event node which was successful: " + eventNodeExpected.getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), groupNodeActual.getEventsCaused().get(0).getIsSuicidal(), () -> "should return group event node which was suicidal: " + eventNodeExpected.getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group node with event node with not null target, but was: null"),
                () -> assertEquals(targetNode, groupNodeActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group node with event target: " + groupNodeActual.getEventsCaused().get(0).getTarget() + ", but was: "
                                + targetNode),
                () -> assertEquals(targetNode.getId(), groupNodeActual.getEventsCaused().get(0).getTarget().getId(),
                        () -> "should return group node with event target id: " + groupNodeActual.getEventsCaused().get(0).getTarget().getId() + ", but was: "
                                + targetNode.getId()),
                () -> assertEquals(targetNode.getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget().getTarget(),
                        () -> "should return group node with event target name: " + groupNodeActual.getEventsCaused().get(0).getTarget().getTarget() + ", but was: "
                                + targetNode.getTarget()),

                () -> assertEquals(countryNode, groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin(),
                        () -> "should return group node with event node with country: " + countryNode + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),
                () -> assertEquals(countryNode.getId(), groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getId(),
                        () -> "should return group node with event node with country id: " + countryNode.getId()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getId()),
                () -> assertEquals(countryNode.getName(), groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getName(),
                        () -> "should return group node with event node with country name: " + countryNode.getName()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),

                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getCity(),
                        () -> "should return group node with event node with not null city, but was: null"),
                () -> assertEquals(cityNode, groupNodeActual.getEventsCaused().get(0).getCity(),
                        () -> "should return group node with event city: " + groupNodeActual.getEventsCaused().get(0).getCity() + ", but was: "
                                + cityNode),
                () -> assertEquals(cityNode.getId(), groupNodeActual.getEventsCaused().get(0).getCity().getId(),
                        () -> "should return group node with event city id: " + groupNodeActual.getEventsCaused().get(0).getCity().getId() + ", but was: "
                                + cityNode.getId()),
                () -> assertEquals(cityNode.getName(), groupNodeActual.getEventsCaused().get(0).getCity().getName(),
                        () -> "should return group node with event city name: " + groupNodeActual.getEventsCaused().get(0).getCity().getName() + ", but was: "
                                + cityNode.getName()),
                () -> assertEquals(cityNode.getLatitude(), groupNodeActual.getEventsCaused().get(0).getCity().getLatitude(),
                        () -> "should return group node with event city latitude: " + groupNodeActual.getEventsCaused().get(0).getCity().getLatitude() + ", but was: "
                                + cityNode.getLatitude()),
                () -> assertEquals(cityNode.getLongitude(), groupNodeActual.getEventsCaused().get(0).getCity().getLongitude(),
                        () -> "should return group node with event city longitude: " + groupNodeActual.getEventsCaused().get(0).getCity().getLongitude() + ", but was: "
                                + cityNode.getLongitude()),

                () -> assertEquals(eventNodeExpected2.getId(), groupNodeActual.getEventsCaused().get(1).getId(), () -> "should return group event node with id: " + eventNodeExpected2.getId() + ", but was: "
                        + groupNodeActual.getEventsCaused().get(1).getId()),
                () -> assertEquals(eventNodeExpected2.getSummary(), groupNodeActual.getEventsCaused().get(1).getSummary(), () -> "should return group event node with summary: " + eventNodeExpected2.getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getSummary()),
                () -> assertEquals(eventNodeExpected2.getMotive(), groupNodeActual.getEventsCaused().get(1).getMotive(), () -> "should return group event node with motive: " + eventNodeExpected2.getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getMotive()),
                () -> assertEquals(eventNodeExpected2.getDate(), groupNodeActual.getEventsCaused().get(1).getDate(), () -> "should return group event node with date: " + eventNodeExpected2.getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getDate()),
                () -> assertEquals(eventNodeExpected2.getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents(), () -> "should return group event node which was part of multiple incidents: " + eventNodeExpected2.getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected2.getIsSuccessful(), groupNodeActual.getEventsCaused().get(1).getIsSuccessful(), () -> "should return group event node which was successful: " + eventNodeExpected2.getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuccessful()),
                () -> assertEquals(eventNodeExpected2.getIsSuicidal(), groupNodeActual.getEventsCaused().get(1).getIsSuicidal(), () -> "should return group event node which was suicidal: " + eventNodeExpected2.getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuicidal()),
                () -> assertNotNull(groupNodeActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group node with event node with not null target, but was: null"),
                () -> assertEquals(targetNode, groupNodeActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group node with event target: " + groupNodeActual.getEventsCaused().get(1).getTarget() + ", but was: "
                                + targetNode2),
                () -> assertEquals(targetNode2.getId(), groupNodeActual.getEventsCaused().get(1).getTarget().getId(),
                        () -> "should return group node with event target id: " + groupNodeActual.getEventsCaused().get(1).getTarget().getId() + ", but was: "
                                + targetNode2.getId()),
                () -> assertEquals(targetNode2.getTarget(), groupNodeActual.getEventsCaused().get(1).getTarget().getTarget(),
                        () -> "should return group node with event target name: " + groupNodeActual.getEventsCaused().get(1).getTarget().getTarget() + ", but was: "
                                + targetNode2.getTarget()),

                () -> assertEquals(targetNode2.getCountryOfOrigin(), groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin(),
                        () -> "should return group node with event node with country: " + targetNode2.getCountryOfOrigin() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin()),
                () -> assertEquals(targetNode2.getCountryOfOrigin().getId(), groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getId(),
                        () -> "should return group node with event node with country id: " + targetNode2.getCountryOfOrigin().getId()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget().getId()),
                () -> assertEquals(targetNode2.getCountryOfOrigin().getName(), groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getName(),
                        () -> "should return group node with event node with country name: " + targetNode2.getCountryOfOrigin().getName()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin()),

                () -> assertNotNull(groupNodeActual.getEventsCaused().get(1).getCity(),
                        () -> "should return group node with event node with not null city, but was: null"),
                () -> assertEquals(cityNode2, groupNodeActual.getEventsCaused().get(1).getCity(),
                        () -> "should return group node with event city: " + groupNodeActual.getEventsCaused().get(1).getCity() + ", but was: "
                                + cityNode2),
                () -> assertEquals(cityNode2.getId(), groupNodeActual.getEventsCaused().get(1).getCity().getId(),
                        () -> "should return group node with event city id: " + groupNodeActual.getEventsCaused().get(1).getCity().getId() + ", but was: "
                                + cityNode2.getId()),
                () -> assertEquals(cityNode2.getName(), groupNodeActual.getEventsCaused().get(1).getCity().getName(),
                        () -> "should return group node with event city name: " + groupNodeActual.getEventsCaused().get(1).getCity().getName() + ", but was: "
                                + cityNode2.getName()),
                () -> assertEquals(cityNode2.getLatitude(), groupNodeActual.getEventsCaused().get(1).getCity().getLatitude(),
                        () -> "should return group node with event city latitude: " + groupNodeActual.getEventsCaused().get(1).getCity().getLatitude() + ", but was: "
                                + cityNode2.getLatitude()),
                () -> assertEquals(cityNode2.getLongitude(), groupNodeActual.getEventsCaused().get(1).getCity().getLongitude(),
                        () -> "should return group node with event city longitude: " + groupNodeActual.getEventsCaused().get(1).getCity().getLongitude() + ", but was: "
                                + cityNode2.getLongitude()),
                () -> verify(groupRepository, times(1)).findById(eventId),
                () -> verifyNoMoreInteractions(groupRepository),
                () -> verify(eventService, times(1)).delete(eventId),
                () -> verify(eventService, times(1)).delete(eventId2),
                () -> verifyNoMoreInteractions(eventService),
                () -> verifyNoInteractions(objectMapper));
    }

    @Test
    void when_delete_group_events_by_id_but_group_does_not_exist_should_return_empty_optional() {

        Long groupId = 1L;

        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        Optional<GroupNode> groupNodeOptional = groupService.deleteAllGroupEvents(groupId);

        assertAll(
                () -> assertTrue(groupNodeOptional.isEmpty(),
                        () -> "should return empty group node optional, but was: " + groupNodeOptional.get()),
                () -> verify(groupRepository, times(1)).findById(groupId),
                () -> verifyNoMoreInteractions(groupRepository),
                () -> verifyNoInteractions(objectMapper),
                () -> verifyNoInteractions(eventService));
    }
}
