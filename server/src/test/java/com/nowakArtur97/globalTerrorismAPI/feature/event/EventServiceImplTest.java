package com.nowakArtur97.globalTerrorismAPI.feature.event;

import com.nowakArtur97.globalTerrorismAPI.common.exception.ResourceNotFoundException;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityNode;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityService;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryNode;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceNode;
import com.nowakArtur97.globalTerrorismAPI.feature.region.RegionNode;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetNode;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetService;
import com.nowakArtur97.globalTerrorismAPI.feature.victim.VictimService;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.*;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventServiceImpl_Tests")
class EventServiceImplTest {

    private final int DEFAULT_DEPTH_FOR_JSON_PATCH = 5;

    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private TargetService targetService;

    @Mock
    private CityService cityService;

    @Mock
    private VictimService victimService;

    private static RegionBuilder regionBuilder;
    private static CountryBuilder countryBuilder;
    private static ProvinceBuilder provinceBuilder;
    private static TargetBuilder targetBuilder;
    private static CityBuilder cityBuilder;
    private static EventBuilder eventBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        regionBuilder = new RegionBuilder();
        countryBuilder = new CountryBuilder();
        targetBuilder = new TargetBuilder();
        provinceBuilder = new ProvinceBuilder();
        cityBuilder = new CityBuilder();
        eventBuilder = new EventBuilder();
    }

    @BeforeEach
    private void setUp() {

        eventService = new EventServiceImpl(eventRepository, modelMapper, targetService, cityService, victimService);
    }

    @Test
    void when_events_exist_and_return_all_events_should_return_events() {

        List<EventNode> eventsListExpected = createEventNodeList(3);

        Page<EventNode> eventsExpected = new PageImpl<>(eventsListExpected);

        Pageable pageable = PageRequest.of(0, 100);

        when(eventRepository.findAll(pageable)).thenReturn(eventsExpected);

        Page<EventNode> eventsActual = eventService.findAll(pageable);

        assertAll(() -> assertNotNull(eventsActual, () -> "shouldn't return null"),
                () -> assertEquals(eventsListExpected, eventsActual.getContent(),
                        () -> "should contain: " + eventsListExpected + ", but was: " + eventsActual.getContent()),
                () -> assertEquals(eventsExpected.getNumberOfElements(), eventsActual.getNumberOfElements(),
                        () -> "should return page with: " + eventsExpected.getNumberOfElements()
                                + " elements, but was: " + eventsActual.getNumberOfElements()),
                () -> verify(eventRepository, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(targetService),
                () -> verifyNoInteractions(cityService));
    }

    @Test
    void when_events_not_exist_and_return_all_events_should_not_return_any_events() {

        List<EventNode> eventsListExpected = new ArrayList<>();

        Page<EventNode> eventsExpected = new PageImpl<>(eventsListExpected);

        Pageable pageable = PageRequest.of(0, 100);

        when(eventRepository.findAll(pageable)).thenReturn(eventsExpected);

        Page<EventNode> eventsActual = eventService.findAll(pageable);

        assertAll(() -> assertNotNull(eventsActual, () -> "shouldn't return null"),
                () -> assertEquals(eventsListExpected, eventsActual.getContent(),
                        () -> "should contain empty list, but was: " + eventsActual.getContent()),
                () -> assertEquals(eventsListExpected, eventsActual.getContent(),
                        () -> "should contain: " + eventsListExpected + ", but was: " + eventsActual.getContent()),
                () -> assertEquals(eventsExpected.getNumberOfElements(), eventsActual.getNumberOfElements(),
                        () -> "should return empty page, but was: " + eventsActual.getNumberOfElements()),
                () -> verify(eventRepository, times(1)).findAll(pageable),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(targetService),
                () -> verifyNoInteractions(cityService));
    }

    @Test
    void when_event_exists_and_return_one_event_should_return_one_event() {

        Long expectedEventId = 1L;
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withTarget(targetNodeExpected).withCity(cityNodeExpected)
                .build(ObjectType.NODE);

        when(eventRepository.findById(expectedEventId)).thenReturn(Optional.of(eventNodeExpected));

        Optional<EventNode> eventNodeActualOptional = eventService.findById(expectedEventId);

        EventNode eventNodeActual = eventNodeActualOptional.get();

        assertAll(
                () -> assertNotNull(eventNodeActual.getId(),
                        () -> "should return event node with new id, but was: " + eventNodeActual.getId()),
                () -> assertEquals(eventNodeExpected.getSummary(), eventNodeActual.getSummary(),
                        () -> "should return event node with summary: " + eventNodeExpected.getSummary() + ", but was: "
                                + eventNodeActual.getSummary()),
                () -> assertEquals(eventNodeExpected.getMotive(), eventNodeActual.getMotive(),
                        () -> "should return event node with motive: " + eventNodeExpected.getMotive() + ", but was: "
                                + eventNodeActual.getMotive()),
                () -> assertEquals(eventNodeExpected.getDate(), eventNodeActual.getDate(),
                        () -> "should return event node with date: " + eventNodeExpected.getDate() + ", but was: "
                                + eventNodeActual.getDate()),
                () -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
                        eventNodeActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event node which was part of multiple incidents: "
                                + eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was: "
                                + eventNodeActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
                        () -> "should return event node which was successful: " + eventNodeExpected.getIsSuccessful()
                                + ", but was: " + eventNodeActual.getIsSuccessful()),
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), eventNodeActual.getIsSuicidal(),
                        () -> "should return event node which was suicidal: " + eventNodeExpected.getIsSuicidal()
                                + ", but was: " + eventNodeActual.getIsSuicidal()),
                () -> assertNotNull(eventNodeActual.getTarget(),
                        () -> "should return event node with not null target, but was: null"),
                () -> assertEquals(targetNodeExpected, eventNodeActual.getTarget(),
                        () -> "should return event node with target: " + targetNodeExpected + ", but was: "
                                + eventNodeActual.getTarget()),
                () -> assertEquals(targetNodeExpected.getId(), eventNodeActual.getTarget().getId(),
                        () -> "should return event node with target id: " + targetNodeExpected.getId() + ", but was: "
                                + eventNodeActual.getTarget().getId()),
                () -> assertEquals(targetNodeExpected.getTarget(), eventNodeActual.getTarget().getTarget(),
                        () -> "should return event node with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + eventNodeActual.getTarget().getTarget()),
                () -> assertNull(eventNodeActual.getTarget().getCountryOfOrigin(),
                        () -> "shouldn't return event node with country, but was: " + eventNodeActual.getTarget().getCountryOfOrigin()),
                () -> assertNotNull(eventNodeActual.getCity(),
                        () -> "should return event node with not null cty, but was: null"),
                () -> assertEquals(cityNodeExpected, eventNodeActual.getCity(),
                        () -> "should return event node with city: " + cityNodeExpected + ", but was: "
                                + eventNodeActual.getCity()),
                () -> assertEquals(cityNodeExpected.getName(), eventNodeActual.getCity().getName(),
                        () -> "should return event node with city name: " + cityNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getName()),
                () -> assertEquals(cityNodeExpected.getLatitude(), eventNodeActual.getCity().getLatitude(),
                        () -> "should return event node with city latitude: " + cityNodeExpected.getLatitude() + ", but was: "
                                + eventNodeActual.getCity().getLatitude()),
                () -> assertEquals(cityNodeExpected.getLongitude(), eventNodeActual.getCity().getLongitude(),
                        () -> "should return event node with city longitude: " + cityNodeExpected.getLongitude() + ", but was: "
                                + eventNodeActual.getCity().getLongitude()),
                () -> verify(eventRepository, times(1)).findById(expectedEventId),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(targetService),
                () -> verifyNoInteractions(cityService));
    }

    @Test
    void when_event_not_exists_and_return_one_event_should_return_empty_optional() {

        Long expectedEventId = 1L;

        when(eventRepository.findById(expectedEventId)).thenReturn(Optional.empty());

        Optional<EventNode> eventNodeActualOptional = eventService.findById(expectedEventId);

        assertAll(() -> assertTrue(eventNodeActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(eventRepository, times(1)).findById(expectedEventId),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(targetService),
                () -> verifyNoInteractions(cityService));
    }

    @Test
    void when_event_exists_and_return_one_event_with_depth_should_return_one_event_with_target_and_country() {

        Long expectedEventId = 1L;
        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.withProvince(provinceNodeExpected).build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withTarget(targetNodeExpected).withCity(cityNodeExpected)
                .build(ObjectType.NODE);

        when(eventRepository.findById(expectedEventId, DEFAULT_DEPTH_FOR_JSON_PATCH)).thenReturn(Optional.of(eventNodeExpected));

        Optional<EventNode> eventNodeActualOptional = eventService.findById(expectedEventId, DEFAULT_DEPTH_FOR_JSON_PATCH);

        EventNode eventNodeActual = eventNodeActualOptional.get();

        assertAll(
                () -> assertNotNull(eventNodeActual.getId(),
                        () -> "should return event node with new id, but was: " + eventNodeActual.getId()),
                () -> assertEquals(eventNodeExpected.getSummary(), eventNodeActual.getSummary(),
                        () -> "should return event node with summary: " + eventNodeExpected.getSummary() + ", but was: "
                                + eventNodeActual.getSummary()),
                () -> assertEquals(eventNodeExpected.getMotive(), eventNodeActual.getMotive(),
                        () -> "should return event node with motive: " + eventNodeExpected.getMotive() + ", but was: "
                                + eventNodeActual.getMotive()),
                () -> assertEquals(eventNodeExpected.getDate(), eventNodeActual.getDate(),
                        () -> "should return event node with date: " + eventNodeExpected.getDate() + ", but was: "
                                + eventNodeActual.getDate()),
                () -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
                        eventNodeActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event node which was part of multiple incidents: "
                                + eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was: "
                                + eventNodeActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
                        () -> "should return event node which was successful: " + eventNodeExpected.getIsSuccessful()
                                + ", but was: " + eventNodeActual.getIsSuccessful()),
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), eventNodeActual.getIsSuicidal(),
                        () -> "should return event node which was suicidal: " + eventNodeExpected.getIsSuicidal()
                                + ", but was: " + eventNodeActual.getIsSuicidal()),
                () -> assertNotNull(eventNodeActual.getTarget(),
                        () -> "should return event node with not null target, but was: null"),
                () -> assertEquals(targetNodeExpected, eventNodeActual.getTarget(),
                        () -> "should return event node with target: " + targetNodeExpected + ", but was: "
                                + eventNodeActual.getTarget()),
                () -> assertEquals(targetNodeExpected.getId(), eventNodeActual.getTarget().getId(),
                        () -> "should return event node with target id: " + targetNodeExpected.getId() + ", but was: "
                                + eventNodeActual.getTarget().getId()),
                () -> assertEquals(targetNodeExpected.getTarget(), eventNodeActual.getTarget().getTarget(),
                        () -> "should return event node with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + eventNodeActual.getTarget().getTarget()),
                () -> assertEquals(countryNodeExpected, eventNodeActual.getTarget().getCountryOfOrigin(),
                        () -> "should return event node with country: " + countryNodeExpected + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin()),
                () -> assertEquals(countryNodeExpected.getId(), eventNodeActual.getTarget().getCountryOfOrigin().getId(),
                        () -> "should return event node with country id: " + countryNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin().getId()),
                () -> assertEquals(countryNodeExpected.getName(), eventNodeActual.getTarget().getCountryOfOrigin().getName(),
                        () -> "should return event node with country name: " + countryNodeExpected.getName()
                                + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin()),
                () -> assertNotNull(eventNodeActual.getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return event node with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected, eventNodeActual.getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return event node with region: " + regionNodeExpected + ", but was: "
                                + eventNodeActual.getTarget().getCountryOfOrigin().getRegion()),
                () -> assertEquals(regionNodeExpected.getId(), eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getId(),
                        () -> "should return event node with region id: " + regionNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getName(),
                        () -> "should return event node with region name: " + regionNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getName()),

                () -> assertNotNull(eventNodeActual.getCity(),
                        () -> "should return event node with not null cty, but was: null"),
                () -> assertEquals(cityNodeExpected, eventNodeActual.getCity(),
                        () -> "should return event node with city: " + cityNodeExpected + ", but was: "
                                + eventNodeActual.getCity()),
                () -> assertEquals(cityNodeExpected.getId(), eventNodeActual.getCity().getId(),
                        () -> "should return event node with city id: " + cityNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getCity().getId()),
                () -> assertEquals(cityNodeExpected.getName(), eventNodeActual.getCity().getName(),
                        () -> "should return event node with city name: " + cityNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getName()),
                () -> assertEquals(cityNodeExpected.getLatitude(), eventNodeActual.getCity().getLatitude(),
                        () -> "should return event node with city latitude: " + cityNodeExpected.getLatitude() + ", but was: "
                                + eventNodeActual.getCity().getLatitude()),
                () -> assertEquals(cityNodeExpected.getLongitude(), eventNodeActual.getCity().getLongitude(),
                        () -> "should return event node with city longitude: " + cityNodeExpected.getLongitude() + ", but was: "
                                + eventNodeActual.getCity().getLongitude()),

                () -> assertNotNull(eventNodeActual.getCity().getProvince(),
                        () -> "should return event node with not null province, but was: null"),
                () -> assertEquals(provinceNodeExpected, eventNodeActual.getCity().getProvince(),
                        () -> "should return event node with province: " + provinceNodeExpected + ", but was: "
                                + eventNodeActual.getCity().getProvince()),
                () -> assertEquals(provinceNodeExpected.getId(), eventNodeActual.getCity().getProvince().getId(),
                        () -> "should return event node with province id: " + provinceNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getId()),
                () -> assertEquals(provinceNodeExpected.getName(), eventNodeActual.getCity().getProvince().getName(),
                        () -> "should return event node with province name: " + provinceNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getProvince().getName()),
                () -> assertEquals(countryNodeExpected, eventNodeActual.getCity().getProvince().getCountry(),
                        () -> "should return event node with country: " + countryNodeExpected + ", but was: " + eventNodeActual.getCity().getProvince().getCountry()),
                () -> assertEquals(countryNodeExpected.getId(), eventNodeActual.getCity().getProvince().getCountry().getId(),
                        () -> "should return event node with country id: " + countryNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getCountry().getId()),
                () -> assertEquals(countryNodeExpected.getName(), eventNodeActual.getCity().getProvince().getCountry().getName(),
                        () -> "should return event node with country name: " + countryNodeExpected.getName()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getCountry()),
                () -> assertNotNull(eventNodeActual.getCity().getProvince().getCountry().getRegion(),
                        () -> "should return event node with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected, eventNodeActual.getCity().getProvince().getCountry().getRegion(),
                        () -> "should return event node with region: " + regionNodeExpected + ", but was: "
                                + eventNodeActual.getCity().getProvince().getCountry().getRegion()),
                () -> assertEquals(regionNodeExpected.getId(), eventNodeActual.getCity().getProvince().getCountry().getRegion().getId(),
                        () -> "should return event node with region id: " + regionNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getCountry().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), eventNodeActual.getCity().getProvince().getCountry().getRegion().getName(),
                        () -> "should return event node with region name: " + regionNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getProvince().getCountry().getRegion().getName()),
                () -> verify(eventRepository, times(1)).findById(expectedEventId, DEFAULT_DEPTH_FOR_JSON_PATCH),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(targetService),
                () -> verifyNoInteractions(cityService));
    }

    @Test
    void when_event_not_exists_and_return_one_event_with_depth_should_return_empty_optional() {

        Long expectedEventId = 1L;

        when(eventRepository.findById(expectedEventId, DEFAULT_DEPTH_FOR_JSON_PATCH)).thenReturn(Optional.empty());

        Optional<EventNode> eventNodeActualOptional = eventService.findById(expectedEventId, DEFAULT_DEPTH_FOR_JSON_PATCH);

        assertAll(() -> assertTrue(eventNodeActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(eventRepository, times(1)).findById(expectedEventId, DEFAULT_DEPTH_FOR_JSON_PATCH),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(targetService),
                () -> verifyNoInteractions(cityService));
    }

    @Test
    void when_save_event_should_save_and_return_event() {

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.withProvince(provinceNodeExpected).build(ObjectType.NODE);
        EventNode eventNodeExpectedBeforeSave = (EventNode) eventBuilder.withTarget(targetNodeExpected)
                .withCity(cityNodeExpected).build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withTarget(targetNodeExpected).withCity(cityNodeExpected)
                .build(ObjectType.NODE);

        when(targetService.save(eventNodeExpectedBeforeSave.getTarget())).thenReturn(targetNodeExpected);
        when(cityService.findByNameAndLatitudeAndLongitude(eventNodeExpectedBeforeSave.getCity().getName(),
                cityNodeExpected.getLatitude(), eventNodeExpectedBeforeSave.getCity().getLongitude()))
                .thenReturn(Optional.of(cityNodeExpected));
        when(eventRepository.save(eventNodeExpectedBeforeSave)).thenReturn(eventNodeExpected);

        EventNode eventNodeActual = eventService.save(eventNodeExpectedBeforeSave);

        assertAll(
                () -> assertNotNull(eventNodeActual.getId(),
                        () -> "should return event node with new id, but was: " + eventNodeActual.getId()),
                () -> assertEquals(eventNodeExpected.getSummary(), eventNodeActual.getSummary(),
                        () -> "should return event node with summary: " + eventNodeExpected.getSummary() + ", but was: "
                                + eventNodeActual.getSummary()),
                () -> assertEquals(eventNodeExpected.getMotive(), eventNodeActual.getMotive(),
                        () -> "should return event node with motive: " + eventNodeExpected.getMotive() + ", but was: "
                                + eventNodeActual.getMotive()),
                () -> assertEquals(eventNodeExpected.getDate(), eventNodeActual.getDate(),
                        () -> "should return event node with date: " + eventNodeExpected.getDate() + ", but was: "
                                + eventNodeActual.getDate()),
                () -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
                        eventNodeActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event node which was part of multiple incidents: "
                                + eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was was: "
                                + eventNodeActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
                        () -> "should return event node which was successful: " + eventNodeExpected.getIsSuccessful()
                                + ", but was: " + eventNodeActual.getIsSuccessful()),
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), eventNodeActual.getIsSuicidal(),
                        () -> "should return event node which was suicidal: " + eventNodeExpected.getIsSuicidal()
                                + ", but was: " + eventNodeActual.getIsSuicidal()),
                () -> assertNotNull(eventNodeActual.getTarget(),
                        () -> "should return event node with not null target, but was: null"),
                () -> assertEquals(targetNodeExpected, eventNodeActual.getTarget(),
                        () -> "should return event node with target: " + targetNodeExpected + ", but was: "
                                + eventNodeActual.getTarget()),
                () -> assertEquals(targetNodeExpected.getId(), eventNodeActual.getTarget().getId(),
                        () -> "should return event node with target id: " + targetNodeExpected.getId() + ", but was: "
                                + eventNodeActual.getTarget().getId()),
                () -> assertEquals(targetNodeExpected.getTarget(), eventNodeActual.getTarget().getTarget(),
                        () -> "should return event node with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + eventNodeActual.getTarget().getTarget()),
                () -> assertEquals(countryNodeExpected, eventNodeActual.getTarget().getCountryOfOrigin(),
                        () -> "should return event node with country: " + countryNodeExpected + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin()),
                () -> assertEquals(countryNodeExpected.getId(), eventNodeActual.getTarget().getCountryOfOrigin().getId(),
                        () -> "should return event node with country id: " + countryNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getTarget().getId()),
                () -> assertEquals(countryNodeExpected.getName(), eventNodeActual.getTarget().getCountryOfOrigin().getName(),
                        () -> "should return event node with country name: " + countryNodeExpected.getName()
                                + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin()),
                () -> assertNotNull(eventNodeActual.getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return event node with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected, eventNodeActual.getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return event node with region: " + regionNodeExpected + ", but was: "
                                + eventNodeActual.getTarget().getCountryOfOrigin().getRegion()),
                () -> assertEquals(regionNodeExpected.getId(), eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getId(),
                        () -> "should return event node with region id: " + regionNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getName(),
                        () -> "should return event node with region name: " + regionNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getName()),

                () -> assertNotNull(eventNodeActual.getCity(),
                        () -> "should return event node with not null cty, but was: null"),
                () -> assertEquals(cityNodeExpected, eventNodeActual.getCity(),
                        () -> "should return event node with city: " + cityNodeExpected + ", but was: "
                                + eventNodeActual.getCity()),
                () -> assertEquals(cityNodeExpected.getName(), eventNodeActual.getCity().getName(),
                        () -> "should return event node with city name: " + cityNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getName()),
                () -> assertEquals(cityNodeExpected.getLatitude(), eventNodeActual.getCity().getLatitude(),
                        () -> "should return event node with city latitude: " + cityNodeExpected.getLatitude() + ", but was: "
                                + eventNodeActual.getCity().getLatitude()),
                () -> assertEquals(cityNodeExpected.getLongitude(), eventNodeActual.getCity().getLongitude(),
                        () -> "should return event node with city longitude: " + cityNodeExpected.getLongitude() + ", but was: "
                                + eventNodeActual.getCity().getLongitude()),

                () -> assertNotNull(eventNodeActual.getCity().getProvince(),
                        () -> "should return event node with not null province, but was: null"),
                () -> assertEquals(provinceNodeExpected, eventNodeActual.getCity().getProvince(),
                        () -> "should return event node with province: " + provinceNodeExpected + ", but was: "
                                + eventNodeActual.getCity().getProvince()),
                () -> assertEquals(provinceNodeExpected.getId(), eventNodeActual.getCity().getProvince().getId(),
                        () -> "should return event node with province id: " + provinceNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getId()),
                () -> assertEquals(provinceNodeExpected.getName(), eventNodeActual.getCity().getProvince().getName(),
                        () -> "should return event node with province name: " + provinceNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getProvince().getName()),
                () -> assertEquals(countryNodeExpected, eventNodeActual.getCity().getProvince().getCountry(),
                        () -> "should return event node with country: " + countryNodeExpected + ", but was: " + eventNodeActual.getCity().getProvince().getCountry()),
                () -> assertEquals(countryNodeExpected.getId(), eventNodeActual.getCity().getProvince().getCountry().getId(),
                        () -> "should return event node with country id: " + countryNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getCountry().getId()),
                () -> assertEquals(countryNodeExpected.getName(), eventNodeActual.getCity().getProvince().getCountry().getName(),
                        () -> "should return event node with country name: " + countryNodeExpected.getName()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getCountry()),
                () -> assertNotNull(eventNodeActual.getCity().getProvince().getCountry().getRegion(),
                        () -> "should return event node with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected, eventNodeActual.getCity().getProvince().getCountry().getRegion(),
                        () -> "should return event node with region: " + regionNodeExpected + ", but was: "
                                + eventNodeActual.getCity().getProvince().getCountry().getRegion()),
                () -> assertEquals(regionNodeExpected.getId(), eventNodeActual.getCity().getProvince().getCountry().getRegion().getId(),
                        () -> "should return event node with region id: " + regionNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getCountry().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), eventNodeActual.getCity().getProvince().getCountry().getRegion().getName(),
                        () -> "should return event node with region name: " + regionNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getProvince().getCountry().getRegion().getName()),
                () -> verify(targetService, times(1)).save(eventNodeExpectedBeforeSave.getTarget()),
                () -> verifyNoMoreInteractions(targetService),
                () -> verify(cityService, times(1))
                        .findByNameAndLatitudeAndLongitude(cityNodeExpected.getName(), cityNodeExpected.getLatitude(), cityNodeExpected.getLongitude()),
                () -> verifyNoMoreInteractions(cityService),
                () -> verify(eventRepository, times(1)).save(eventNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_save_new_event_should_save_and_return_new_event() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTOExpected = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);
        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withCountry(countryNodeExpected).build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.withProvince(provinceNodeExpected).build(ObjectType.NODE);
        EventNode eventNodeExpectedBeforeSave = (EventNode) eventBuilder.withTarget(targetNodeExpected).withCity(cityNodeExpected)
                .build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withTarget(targetNodeExpected).withCity(cityNodeExpected)
                .build(ObjectType.NODE);

        when(modelMapper.map(eventDTOExpected, EventNode.class)).thenReturn(eventNodeExpectedBeforeSave);
        when(targetService.saveNew(targetDTO)).thenReturn(targetNodeExpected);
        when(cityService.findByNameAndLatitudeAndLongitude(cityDTO.getName(), cityDTO.getLatitude(), cityDTO.getLongitude()))
                .thenReturn(Optional.of(cityNodeExpected));
        when(eventRepository.save(eventNodeExpectedBeforeSave)).thenReturn(eventNodeExpected);

        EventNode eventNodeActual = eventService.saveNew(eventDTOExpected);

        assertAll(
                () -> assertNotNull(eventNodeActual.getId(),
                        () -> "should return event node with id, but was: " + eventNodeActual.getId()),
                () -> assertEquals(eventNodeExpected.getSummary(), eventNodeActual.getSummary(),
                        () -> "should return event node with summary: " + eventNodeExpected.getSummary() + ", but was: "
                                + eventNodeActual.getSummary()),
                () -> assertEquals(eventNodeExpected.getMotive(), eventNodeActual.getMotive(),
                        () -> "should return event node with motive: " + eventNodeExpected.getMotive() + ", but was: "
                                + eventNodeActual.getMotive()),
                () -> assertEquals(eventNodeExpected.getDate(), eventNodeActual.getDate(),
                        () -> "should return event node with date: " + eventNodeExpected.getDate() + ", but was: "
                                + eventNodeActual.getDate()),
                () -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
                        eventNodeActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event node which was part of multiple incidents: "
                                + eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was was: "
                                + eventNodeActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
                        () -> "should return event node which was successful: " + eventNodeExpected.getIsSuccessful()
                                + ", but was: " + eventNodeActual.getIsSuccessful()),
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), eventNodeActual.getIsSuicidal(),
                        () -> "should return event node which was suicidal: " + eventNodeExpected.getIsSuicidal()
                                + ", but was: " + eventNodeActual.getIsSuicidal()),
                () -> assertNotNull(eventNodeActual.getTarget(),
                        () -> "should return event node with not null target, but was: null"),
                () -> assertEquals(targetNodeExpected, eventNodeActual.getTarget(),
                        () -> "should return event node with target: " + targetNodeExpected + ", but was: "
                                + eventNodeActual.getTarget()),
                () -> assertEquals(targetNodeExpected.getId(), eventNodeActual.getTarget().getId(),
                        () -> "should return event node with target id: " + targetNodeExpected.getId() + ", but was: "
                                + eventNodeActual.getTarget().getId()),
                () -> assertEquals(targetNodeExpected.getTarget(), eventNodeActual.getTarget().getTarget(),
                        () -> "should return event node with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + eventNodeActual.getTarget().getTarget()),
                () -> assertEquals(countryNodeExpected, eventNodeActual.getTarget().getCountryOfOrigin(),
                        () -> "should return event node with country: " + countryNodeExpected + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin()),
                () -> assertEquals(countryNodeExpected.getId(), eventNodeActual.getTarget().getCountryOfOrigin().getId(),
                        () -> "should return event node with country id: " + countryNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getTarget().getId()),
                () -> assertEquals(countryNodeExpected.getName(), eventNodeActual.getTarget().getCountryOfOrigin().getName(),
                        () -> "should return event node with country name: " + countryNodeExpected.getName()
                                + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin()),
                () -> assertNotNull(eventNodeActual.getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return event node with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected, eventNodeActual.getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return event node with region: " + regionNodeExpected + ", but was: "
                                + eventNodeActual.getTarget().getCountryOfOrigin().getRegion()),
                () -> assertEquals(regionNodeExpected.getId(), eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getId(),
                        () -> "should return event node with region id: " + regionNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getName(),
                        () -> "should return event node with region name: " + regionNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getName()),

                () -> assertNotNull(eventNodeActual.getCity(),
                        () -> "should return event node with not null cty, but was: null"),
                () -> assertEquals(cityNodeExpected, eventNodeActual.getCity(),
                        () -> "should return event node with city: " + cityNodeExpected + ", but was: "
                                + eventNodeActual.getCity()),
                () -> assertEquals(cityNodeExpected.getName(), eventNodeActual.getCity().getName(),
                        () -> "should return event node with city name: " + cityNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getName()),
                () -> assertEquals(cityNodeExpected.getLatitude(), eventNodeActual.getCity().getLatitude(),
                        () -> "should return event node with city latitude: " + cityNodeExpected.getLatitude() + ", but was: "
                                + eventNodeActual.getCity().getLatitude()),
                () -> assertEquals(cityNodeExpected.getLongitude(), eventNodeActual.getCity().getLongitude(),
                        () -> "should return event node with city longitude: " + cityNodeExpected.getLongitude() + ", but was: "
                                + eventNodeActual.getCity().getLongitude()),

                () -> assertNotNull(eventNodeActual.getCity().getProvince(),
                        () -> "should return event node with not null province, but was: null"),
                () -> assertEquals(provinceNodeExpected, eventNodeActual.getCity().getProvince(),
                        () -> "should return event node with province: " + provinceNodeExpected + ", but was: "
                                + eventNodeActual.getCity().getProvince()),
                () -> assertEquals(provinceNodeExpected.getId(), eventNodeActual.getCity().getProvince().getId(),
                        () -> "should return event node with province id: " + provinceNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getId()),
                () -> assertEquals(provinceNodeExpected.getName(), eventNodeActual.getCity().getProvince().getName(),
                        () -> "should return event node with province name: " + provinceNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getProvince().getName()),
                () -> assertEquals(countryNodeExpected, eventNodeActual.getCity().getProvince().getCountry(),
                        () -> "should return event node with country: " + countryNodeExpected + ", but was: " + eventNodeActual.getCity().getProvince().getCountry()),
                () -> assertEquals(countryNodeExpected.getId(), eventNodeActual.getCity().getProvince().getCountry().getId(),
                        () -> "should return event node with country id: " + countryNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getCountry().getId()),
                () -> assertEquals(countryNodeExpected.getName(), eventNodeActual.getCity().getProvince().getCountry().getName(),
                        () -> "should return event node with country name: " + countryNodeExpected.getName()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getCountry()),
                () -> assertNotNull(eventNodeActual.getCity().getProvince().getCountry().getRegion(),
                        () -> "should return event node with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected, eventNodeActual.getCity().getProvince().getCountry().getRegion(),
                        () -> "should return event node with region: " + regionNodeExpected + ", but was: "
                                + eventNodeActual.getCity().getProvince().getCountry().getRegion()),
                () -> assertEquals(regionNodeExpected.getId(), eventNodeActual.getCity().getProvince().getCountry().getRegion().getId(),
                        () -> "should return event node with region id: " + regionNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getCountry().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), eventNodeActual.getCity().getProvince().getCountry().getRegion().getName(),
                        () -> "should return event node with region name: " + regionNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getProvince().getCountry().getRegion().getName()),
                () -> verify(targetService, times(1)).saveNew(targetDTO),
                () -> verify(modelMapper, times(1)).map(eventDTOExpected, EventNode.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(targetService, times(1)).saveNew(eventDTOExpected.getTarget()),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(cityService, times(1)).findByNameAndLatitudeAndLongitude(cityDTO.getName(), cityDTO.getLatitude(), cityDTO.getLongitude()),
                () -> verifyNoMoreInteractions(cityService),
                () -> verify(eventRepository, times(1)).save(eventNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(eventRepository));
    }

    @Test
    void when_save_new_event_with_new_city_should_save_and_return_new_event_with_new_city() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTOExpected = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withCountry(countryNodeExpected).build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.withProvince(provinceNodeExpected).build(ObjectType.NODE);
        EventNode eventNodeExpectedBeforeSave = (EventNode) eventBuilder.withTarget(targetNodeExpected).withCity(cityNodeExpected)
                .build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withTarget(targetNodeExpected).withCity(cityNodeExpected)
                .build(ObjectType.NODE);

        when(modelMapper.map(eventDTOExpected, EventNode.class)).thenReturn(eventNodeExpectedBeforeSave);
        when(targetService.saveNew(eventDTOExpected.getTarget())).thenReturn(targetNodeExpected);
        when(cityService.findByNameAndLatitudeAndLongitude(cityDTO.getName(), cityDTO.getLatitude(), cityDTO.getLongitude()))
                .thenReturn(Optional.empty());
        when(cityService.saveNew(cityDTO)).thenReturn(cityNodeExpected);
        when(eventRepository.save(eventNodeExpectedBeforeSave)).thenReturn(eventNodeExpected);

        EventNode eventNodeActual = eventService.saveNew(eventDTOExpected);

        assertAll(
                () -> assertNotNull(eventNodeActual.getId(),
                        () -> "should return event node with id, but was: " + eventNodeActual.getId()),
                () -> assertEquals(eventNodeExpected.getSummary(), eventNodeActual.getSummary(),
                        () -> "should return event node with summary: " + eventNodeExpected.getSummary() + ", but was: "
                                + eventNodeActual.getSummary()),
                () -> assertEquals(eventNodeExpected.getMotive(), eventNodeActual.getMotive(),
                        () -> "should return event node with motive: " + eventNodeExpected.getMotive() + ", but was: "
                                + eventNodeActual.getMotive()),
                () -> assertEquals(eventNodeExpected.getDate(), eventNodeActual.getDate(),
                        () -> "should return event node with date: " + eventNodeExpected.getDate() + ", but was: "
                                + eventNodeActual.getDate()),
                () -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
                        eventNodeActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event node which was part of multiple incidents: "
                                + eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was was: "
                                + eventNodeActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
                        () -> "should return event node which was successful: " + eventNodeExpected.getIsSuccessful()
                                + ", but was: " + eventNodeActual.getIsSuccessful()),
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), eventNodeActual.getIsSuicidal(),
                        () -> "should return event node which was suicidal: " + eventNodeExpected.getIsSuicidal()
                                + ", but was: " + eventNodeActual.getIsSuicidal()),
                () -> assertNotNull(eventNodeActual.getTarget(),
                        () -> "should return event node with not null target, but was: null"),
                () -> assertEquals(targetNodeExpected, eventNodeActual.getTarget(),
                        () -> "should return event node with target: " + targetNodeExpected + ", but was: "
                                + eventNodeActual.getTarget()),
                () -> assertEquals(targetNodeExpected.getId(), eventNodeActual.getTarget().getId(),
                        () -> "should return event node with target id: " + targetNodeExpected.getId() + ", but was: "
                                + eventNodeActual.getTarget().getId()),
                () -> assertEquals(targetNodeExpected.getTarget(), eventNodeActual.getTarget().getTarget(),
                        () -> "should return event node with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + eventNodeActual.getTarget().getTarget()),
                () -> assertEquals(countryNodeExpected, eventNodeActual.getTarget().getCountryOfOrigin(),
                        () -> "should return event node with country: " + countryNodeExpected + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin()),
                () -> assertEquals(countryNodeExpected.getId(), eventNodeActual.getTarget().getCountryOfOrigin().getId(),
                        () -> "should return event node with country id: " + countryNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getTarget().getId()),
                () -> assertEquals(countryNodeExpected.getName(), eventNodeActual.getTarget().getCountryOfOrigin().getName(),
                        () -> "should return event node with country name: " + countryNodeExpected.getName()
                                + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin()),
                () -> assertNotNull(eventNodeActual.getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return event node with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected, eventNodeActual.getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return event node with region: " + regionNodeExpected + ", but was: "
                                + eventNodeActual.getTarget().getCountryOfOrigin().getRegion()),
                () -> assertEquals(regionNodeExpected.getId(), eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getId(),
                        () -> "should return event node with region id: " + regionNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getName(),
                        () -> "should return event node with region name: " + regionNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getName()),

                () -> assertNotNull(eventNodeActual.getCity(),
                        () -> "should return event node with not null cty, but was: null"),
                () -> assertEquals(cityNodeExpected, eventNodeActual.getCity(),
                        () -> "should return event node with city: " + cityNodeExpected + ", but was: "
                                + eventNodeActual.getCity()),
                () -> assertEquals(cityNodeExpected.getName(), eventNodeActual.getCity().getName(),
                        () -> "should return event node with city name: " + cityNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getName()),
                () -> assertEquals(cityNodeExpected.getLatitude(), eventNodeActual.getCity().getLatitude(),
                        () -> "should return event node with city latitude: " + cityNodeExpected.getLatitude() + ", but was: "
                                + eventNodeActual.getCity().getLatitude()),
                () -> assertEquals(cityNodeExpected.getLongitude(), eventNodeActual.getCity().getLongitude(),
                        () -> "should return event node with city longitude: " + cityNodeExpected.getLongitude() + ", but was: "
                                + eventNodeActual.getCity().getLongitude()),

                () -> assertNotNull(eventNodeActual.getCity().getProvince(),
                        () -> "should return event node with not null province, but was: null"),
                () -> assertEquals(provinceNodeExpected, eventNodeActual.getCity().getProvince(),
                        () -> "should return event node with province: " + provinceNodeExpected + ", but was: "
                                + eventNodeActual.getCity().getProvince()),
                () -> assertEquals(provinceNodeExpected.getId(), eventNodeActual.getCity().getProvince().getId(),
                        () -> "should return event node with province id: " + provinceNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getId()),
                () -> assertEquals(provinceNodeExpected.getName(), eventNodeActual.getCity().getProvince().getName(),
                        () -> "should return event node with province name: " + provinceNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getProvince().getName()),
                () -> assertEquals(countryNodeExpected, eventNodeActual.getCity().getProvince().getCountry(),
                        () -> "should return event node with country: " + countryNodeExpected + ", but was: " + eventNodeActual.getCity().getProvince().getCountry()),
                () -> assertEquals(countryNodeExpected.getId(), eventNodeActual.getCity().getProvince().getCountry().getId(),
                        () -> "should return event node with country id: " + countryNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getCountry().getId()),
                () -> assertEquals(countryNodeExpected.getName(), eventNodeActual.getCity().getProvince().getCountry().getName(),
                        () -> "should return event node with country name: " + countryNodeExpected.getName()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getCountry()),
                () -> assertNotNull(eventNodeActual.getCity().getProvince().getCountry().getRegion(),
                        () -> "should return event node with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected, eventNodeActual.getCity().getProvince().getCountry().getRegion(),
                        () -> "should return event node with region: " + regionNodeExpected + ", but was: "
                                + eventNodeActual.getCity().getProvince().getCountry().getRegion()),
                () -> assertEquals(regionNodeExpected.getId(), eventNodeActual.getCity().getProvince().getCountry().getRegion().getId(),
                        () -> "should return event node with region id: " + regionNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getCountry().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), eventNodeActual.getCity().getProvince().getCountry().getRegion().getName(),
                        () -> "should return event node with region name: " + regionNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getProvince().getCountry().getRegion().getName()),
                () -> verify(modelMapper, times(1)).map(eventDTOExpected, EventNode.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(targetService, times(1)).saveNew(eventDTOExpected.getTarget()),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(cityService, times(1)).findByNameAndLatitudeAndLongitude(cityDTO.getName(), cityDTO.getLatitude(), cityDTO.getLongitude()),
                () -> verify(cityService, times(1)).saveNew(cityDTO),
                () -> verifyNoMoreInteractions(cityService),
                () -> verify(eventRepository, times(1)).save(eventNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(eventRepository));
    }

    @Test
    void when_update_event_should_update_event() throws ParseException {

        String updatedCountryName = "country updated";
        String updatedTargetName = "target2";
        String updatedCityName = "city2";
        String updatedProvinceName = "province2";
        double updatedCityLatitude = 13.0;
        double updatedCityLongitude = -11.0;
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withName(updatedProvinceName).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withName(updatedCityName).withLatitude(updatedCityLatitude)
                .withLongitude(updatedCityLongitude).withProvince(provinceDTO).build(ObjectType.DTO);
        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(updatedCountryName).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget(updatedTargetName).withCountry(countryDTO).build(ObjectType.DTO);

        String updatedSummary = "summary updated";
        String updatedMotive = "motive updated";
        Date updatedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("01/08/2010 02:00:00:000");
        boolean updatedIsPartOfMultipleIncidents = false;
        boolean updatedIsSuccessful = false;
        boolean updatedIsSuicidal = false;
        EventDTO eventDTOExpected = (EventDTO) eventBuilder.withDate(updatedDate).withSummary(updatedSummary)
                .withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents).withIsSuccessful(updatedIsSuccessful)
                .withIsSuicidal(updatedIsSuicidal).withMotive(updatedMotive).withTarget(targetDTO).withCity(cityDTO)
                .build(ObjectType.DTO);

        RegionNode regionNode = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNode = (CountryNode) countryBuilder.withRegion(regionNode).build(ObjectType.NODE);
        TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNode).build(ObjectType.NODE);
        RegionNode updatedRegionNode = (RegionNode) regionBuilder.withName("region2").build(ObjectType.NODE);
        CountryNode updatedCountryNode = (CountryNode) countryBuilder.withName(updatedCountryName)
                .withRegion(updatedRegionNode).build(ObjectType.NODE);
        TargetNode updatedTargetNode = (TargetNode) targetBuilder.withTarget(updatedTargetName).withCountry(updatedCountryNode)
                .build(ObjectType.NODE);
        ProvinceNode provinceNode = (ProvinceNode) provinceBuilder.withCountry(countryNode)
                .build(ObjectType.NODE);
        ProvinceNode updatedProvinceNode = (ProvinceNode) provinceBuilder.withName(updatedProvinceName)
                .withCountry(updatedCountryNode).build(ObjectType.NODE);
        CityNode cityNode = (CityNode) cityBuilder.withProvince(provinceNode).build(ObjectType.NODE);
        CityNode updatedCityNode = (CityNode) cityBuilder.withName(updatedCityName).withLatitude(updatedCityLatitude)
                .withLongitude(updatedCityLongitude).withProvince(updatedProvinceNode).build(ObjectType.NODE);
        EventNode eventNodeExpectedBeforeMethod = (EventNode) eventBuilder.withTarget(targetNode).withCity(cityNode)
                .build(ObjectType.NODE);
        EventNode eventNodeExpectedBeforeSetIdAndTarget = (EventNode) eventBuilder.withDate(updatedDate).withSummary(updatedSummary)
                .withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents).withIsSuccessful(updatedIsSuccessful)
                .withIsSuicidal(updatedIsSuicidal).withMotive(updatedMotive).withTarget(targetNode).withCity(cityNode)
                .build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withDate(updatedDate).withSummary(updatedSummary)
                .withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents).withIsSuccessful(updatedIsSuccessful)
                .withIsSuicidal(updatedIsSuicidal).withMotive(updatedMotive).withTarget(updatedTargetNode)
                .withCity(updatedCityNode).build(ObjectType.NODE);

        when(targetService.update(targetNode, targetDTO)).thenReturn(updatedTargetNode);
        when(modelMapper.map(eventDTOExpected, EventNode.class)).thenReturn(eventNodeExpectedBeforeSetIdAndTarget);
        when(cityService.findByNameAndLatitudeAndLongitude(cityDTO.getName(), cityDTO.getLatitude(), cityDTO.getLongitude()))
                .thenReturn(Optional.empty());
        when(cityService.saveNew(cityDTO)).thenReturn(updatedCityNode);
        when(eventRepository.save(eventNodeExpectedBeforeSetIdAndTarget)).thenReturn(eventNodeExpected);

        EventNode eventNodeActual = eventService.update(eventNodeExpectedBeforeMethod, eventDTOExpected);

        assertAll(
                () -> assertNotNull(eventNodeActual.getId(),
                        () -> "should return event node with new id, but was: " + eventNodeActual.getId()),
                () -> assertEquals(eventNodeExpected.getSummary(), eventNodeActual.getSummary(),
                        () -> "should return event node with summary: " + eventNodeExpected.getSummary() + ", but was: "
                                + eventNodeActual.getSummary()),
                () -> assertEquals(eventNodeExpected.getMotive(), eventNodeActual.getMotive(),
                        () -> "should return event node with motive: " + eventNodeExpected.getMotive() + ", but was: "
                                + eventNodeActual.getMotive()),
                () -> assertEquals(eventNodeExpected.getDate(), eventNodeActual.getDate(),
                        () -> "should return event node with date: " + eventNodeExpected.getDate() + ", but was: "
                                + eventNodeActual.getDate()),
                () -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
                        eventNodeActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event node which was part of multiple incidents: "
                                + eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was was: "
                                + eventNodeActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
                        () -> "should return event node which was successful: " + eventNodeExpected.getIsSuccessful()
                                + ", but was: " + eventNodeActual.getIsSuccessful()),
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), eventNodeActual.getIsSuicidal(),
                        () -> "should return event node which was suicidal: " + eventNodeExpected.getIsSuicidal()
                                + ", but was: " + eventNodeActual.getIsSuicidal()),
                () -> assertNotNull(eventNodeActual.getTarget(),
                        () -> "should return event node with not null target, but was: null"),
                () -> assertEquals(updatedTargetNode, eventNodeActual.getTarget(),
                        () -> "should return event node with target: " + updatedTargetNode + ", but was: "
                                + eventNodeActual.getTarget()),
                () -> assertEquals(updatedTargetNode.getId(), eventNodeActual.getTarget().getId(),
                        () -> "should return event node with target id: " + updatedTargetNode.getId() + ", but was: "
                                + eventNodeActual.getTarget().getId()),
                () -> assertEquals(updatedTargetNode.getTarget(), eventNodeActual.getTarget().getTarget(),
                        () -> "should return event node with target: " + updatedTargetNode.getTarget() + ", but was: "
                                + eventNodeActual.getTarget().getTarget()),
                () -> assertEquals(updatedCountryNode, eventNodeActual.getTarget().getCountryOfOrigin(),
                        () -> "should return event node with country: " + updatedCountryNode + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin()),
                () -> assertEquals(updatedCountryNode.getId(), eventNodeActual.getTarget().getCountryOfOrigin().getId(),
                        () -> "should return event node with country id: " + updatedCountryNode.getId()
                                + ", but was: " + eventNodeActual.getTarget().getId()),
                () -> assertEquals(updatedCountryNode.getName(), eventNodeActual.getTarget().getCountryOfOrigin().getName(),
                        () -> "should return event node with country name: " + updatedCountryNode.getName()
                                + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin()),
                () -> assertNotNull(eventNodeActual.getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return event node with not null region, but was: null"),
                () -> assertEquals(updatedRegionNode, eventNodeActual.getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return event node with region: " + updatedRegionNode + ", but was: "
                                + eventNodeActual.getTarget().getCountryOfOrigin().getRegion()),
                () -> assertEquals(updatedRegionNode.getId(), eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getId(),
                        () -> "should return event node with region id: " + updatedRegionNode.getId()
                                + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getId()),
                () -> assertEquals(updatedRegionNode.getName(), eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getName(),
                        () -> "should return event node with region name: " + updatedRegionNode.getName() + ", but was: "
                                + eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getName()),

                () -> assertNotNull(eventNodeActual.getCity(),
                        () -> "should return event node with not null cty, but was: null"),
                () -> assertEquals(updatedCityNode, eventNodeActual.getCity(),
                        () -> "should return event node with city: " + updatedCityNode + ", but was: "
                                + eventNodeActual.getCity()),
                () -> assertEquals(updatedCityNode.getName(), eventNodeActual.getCity().getName(),
                        () -> "should return event node with city name: " + updatedCityNode.getName() + ", but was: "
                                + eventNodeActual.getCity().getName()),
                () -> assertEquals(updatedCityNode.getLatitude(), eventNodeActual.getCity().getLatitude(),
                        () -> "should return event node with city latitude: " + updatedCityNode.getLatitude() + ", but was: "
                                + eventNodeActual.getCity().getLatitude()),
                () -> assertEquals(updatedCityNode.getLongitude(), eventNodeActual.getCity().getLongitude(),
                        () -> "should return event node with city longitude: " + updatedCityNode.getLongitude() + ", but was: "
                                + eventNodeActual.getCity().getLongitude()),

                () -> assertNotNull(eventNodeActual.getCity().getProvince(),
                        () -> "should return event node with not null province, but was: null"),
                () -> assertEquals(updatedProvinceNode, eventNodeActual.getCity().getProvince(),
                        () -> "should return event node with province: " + updatedProvinceNode + ", but was: "
                                + eventNodeActual.getCity().getProvince()),
                () -> assertEquals(updatedProvinceNode.getId(), eventNodeActual.getCity().getProvince().getId(),
                        () -> "should return event node with province id: " + updatedProvinceNode.getId()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getId()),
                () -> assertEquals(updatedProvinceNode.getName(), eventNodeActual.getCity().getProvince().getName(),
                        () -> "should return event node with province name: " + updatedProvinceNode.getName() + ", but was: "
                                + eventNodeActual.getCity().getProvince().getName()),
                () -> assertEquals(updatedCountryNode, eventNodeActual.getCity().getProvince().getCountry(),
                        () -> "should return event node with country: " + updatedCountryName + ", but was: " + eventNodeActual.getCity().getProvince().getCountry()),
                () -> assertEquals(updatedCountryNode.getId(), eventNodeActual.getCity().getProvince().getCountry().getId(),
                        () -> "should return event node with country id: " + updatedCountryNode.getId()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getCountry().getId()),
                () -> assertEquals(updatedCountryNode.getName(), eventNodeActual.getCity().getProvince().getCountry().getName(),
                        () -> "should return event node with country name: " + updatedCountryNode.getName()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getCountry()),
                () -> assertNotNull(eventNodeActual.getCity().getProvince().getCountry().getRegion(),
                        () -> "should return event node with not null region, but was: null"),
                () -> assertEquals(updatedRegionNode, eventNodeActual.getCity().getProvince().getCountry().getRegion(),
                        () -> "should return event node with region: " + updatedRegionNode + ", but was: "
                                + eventNodeActual.getCity().getProvince().getCountry().getRegion()),
                () -> assertEquals(updatedRegionNode.getId(), eventNodeActual.getCity().getProvince().getCountry().getRegion().getId(),
                        () -> "should return event node with region id: " + updatedRegionNode.getId()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getCountry().getRegion().getId()),
                () -> assertEquals(updatedRegionNode.getName(), eventNodeActual.getCity().getProvince().getCountry().getRegion().getName(),
                        () -> "should return event node with region name: " + updatedRegionNode.getName() + ", but was: "
                                + eventNodeActual.getCity().getProvince().getCountry().getRegion().getName()),
                () -> verify(targetService, times(1)).update(targetNode, targetDTO),
                () -> verifyNoMoreInteractions(targetService),
                () -> verify(modelMapper, times(1)).map(eventDTOExpected, EventNode.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(cityService, times(1)).findByNameAndLatitudeAndLongitude(cityDTO.getName(), cityDTO.getLatitude(), cityDTO.getLongitude()),
                () -> verify(cityService, times(1)).saveNew(cityDTO),
                () -> verifyNoMoreInteractions(cityService),
                () -> verify(eventRepository, times(1)).save(eventNodeExpectedBeforeSetIdAndTarget),
                () -> verifyNoMoreInteractions(eventRepository));
    }

    @Test
    void when_update_event_without_target_should_save_new_target_and_return_event() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        TargetDTO targetDTOExpected = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected).build(ObjectType.NODE);
        TargetNode newTargetNode = (TargetNode) targetBuilder.withCountry(countryNodeExpected).build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.withProvince(provinceNodeExpected).build(ObjectType.NODE);

        EventNode eventNodeExpectedBeforeSetTarget = (EventNode) eventBuilder.withCity(cityNodeExpected).build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withTarget(newTargetNode).withCity(cityNodeExpected)
                .build(ObjectType.NODE);

        when(targetService.saveNew(targetDTOExpected)).thenReturn(newTargetNode);
        when(eventRepository.save(eventNodeExpectedBeforeSetTarget)).thenReturn(eventNodeExpected);

        EventNode eventNodeActual = eventService.addOrUpdateEventTarget(eventNodeExpectedBeforeSetTarget, targetDTOExpected);

        assertAll(
                () -> assertNotNull(eventNodeActual.getId(),
                        () -> "should return event node with new id, but was: " + eventNodeActual.getId()),
                () -> assertEquals(eventNodeExpected.getSummary(), eventNodeActual.getSummary(),
                        () -> "should return event node with summary: " + eventNodeExpected.getSummary() + ", but was: "
                                + eventNodeActual.getSummary()),
                () -> assertEquals(eventNodeExpected.getMotive(), eventNodeActual.getMotive(),
                        () -> "should return event node with motive: " + eventNodeExpected.getMotive() + ", but was: "
                                + eventNodeActual.getMotive()),
                () -> assertEquals(eventNodeExpected.getDate(), eventNodeActual.getDate(),
                        () -> "should return event node with date: " + eventNodeExpected.getDate() + ", but was: "
                                + eventNodeActual.getDate()),
                () -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
                        eventNodeActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event node which was part of multiple incidents: "
                                + eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was was: "
                                + eventNodeActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
                        () -> "should return event node which was successful: " + eventNodeExpected.getIsSuccessful()
                                + ", but was: " + eventNodeActual.getIsSuccessful()),
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), eventNodeActual.getIsSuicidal(),
                        () -> "should return event node which was suicidal: " + eventNodeExpected.getIsSuicidal()
                                + ", but was: " + eventNodeActual.getIsSuicidal()),
                () -> assertNotNull(eventNodeActual.getTarget(),
                        () -> "should return event node with not null target, but was: null"),
                () -> assertEquals(newTargetNode, eventNodeActual.getTarget(),
                        () -> "should return event node with target: " + newTargetNode + ", but was: "
                                + eventNodeActual.getTarget()),
                () -> assertEquals(newTargetNode.getId(), eventNodeActual.getTarget().getId(),
                        () -> "should return event node with target id: " + newTargetNode.getId() + ", but was: "
                                + eventNodeActual.getTarget().getId()),
                () -> assertEquals(newTargetNode.getTarget(), eventNodeActual.getTarget().getTarget(),
                        () -> "should return event node with target: " + newTargetNode.getTarget() + ", but was: "
                                + eventNodeActual.getTarget().getTarget()),
                () -> assertEquals(newTargetNode.getCountryOfOrigin(), eventNodeActual.getTarget().getCountryOfOrigin(),
                        () -> "should return event node with country: " + newTargetNode.getCountryOfOrigin() + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin()),
                () -> assertEquals(newTargetNode.getCountryOfOrigin().getId(), eventNodeActual.getTarget().getCountryOfOrigin().getId(),
                        () -> "should return event node with country id: " + newTargetNode.getCountryOfOrigin().getId()
                                + ", but was: " + eventNodeActual.getTarget().getId()),
                () -> assertEquals(newTargetNode.getCountryOfOrigin().getName(), eventNodeActual.getTarget().getCountryOfOrigin().getName(),
                        () -> "should return event node with country name: " + newTargetNode.getCountryOfOrigin().getName()
                                + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin()),
                () -> assertNotNull(eventNodeActual.getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return event node with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected, eventNodeActual.getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return event node with region: " + regionNodeExpected + ", but was: "
                                + eventNodeActual.getTarget().getCountryOfOrigin().getRegion()),
                () -> assertEquals(regionNodeExpected.getId(), eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getId(),
                        () -> "should return event node with region id: " + regionNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getName(),
                        () -> "should return event node with region name: " + regionNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getTarget().getCountryOfOrigin().getRegion().getName()),

                () -> assertNotNull(eventNodeActual.getCity(),
                        () -> "should return event node with not null cty, but was: null"),
                () -> assertEquals(cityNodeExpected, eventNodeActual.getCity(),
                        () -> "should return event node with city: " + cityNodeExpected + ", but was: "
                                + eventNodeActual.getCity()),
                () -> assertEquals(cityNodeExpected.getName(), eventNodeActual.getCity().getName(),
                        () -> "should return event node with city name: " + cityNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getName()),
                () -> assertEquals(cityNodeExpected.getLatitude(), eventNodeActual.getCity().getLatitude(),
                        () -> "should return event node with city latitude: " + cityNodeExpected.getLatitude() + ", but was: "
                                + eventNodeActual.getCity().getLatitude()),
                () -> assertEquals(cityNodeExpected.getLongitude(), eventNodeActual.getCity().getLongitude(),
                        () -> "should return event node with city longitude: " + cityNodeExpected.getLongitude() + ", but was: "
                                + eventNodeActual.getCity().getLongitude()),

                () -> assertNotNull(eventNodeActual.getCity().getProvince(),
                        () -> "should return event node with not null province, but was: null"),
                () -> assertEquals(provinceNodeExpected, eventNodeActual.getCity().getProvince(),
                        () -> "should return event node with province: " + provinceNodeExpected + ", but was: "
                                + eventNodeActual.getCity().getProvince()),
                () -> assertEquals(provinceNodeExpected.getId(), eventNodeActual.getCity().getProvince().getId(),
                        () -> "should return event node with province id: " + provinceNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getId()),
                () -> assertEquals(provinceNodeExpected.getName(), eventNodeActual.getCity().getProvince().getName(),
                        () -> "should return event node with province name: " + provinceNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getProvince().getName()),
                () -> assertEquals(countryNodeExpected, eventNodeActual.getCity().getProvince().getCountry(),
                        () -> "should return event node with country: " + countryNodeExpected + ", but was: " + eventNodeActual.getCity().getProvince().getCountry()),
                () -> assertEquals(countryNodeExpected.getId(), eventNodeActual.getCity().getProvince().getCountry().getId(),
                        () -> "should return event node with country id: " + countryNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getCountry().getId()),
                () -> assertEquals(countryNodeExpected.getName(), eventNodeActual.getCity().getProvince().getCountry().getName(),
                        () -> "should return event node with country name: " + countryNodeExpected.getName()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getCountry()),
                () -> assertNotNull(eventNodeActual.getCity().getProvince().getCountry().getRegion(),
                        () -> "should return event node with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected, eventNodeActual.getCity().getProvince().getCountry().getRegion(),
                        () -> "should return event node with region: " + regionNodeExpected + ", but was: "
                                + eventNodeActual.getCity().getProvince().getCountry().getRegion()),
                () -> assertEquals(regionNodeExpected.getId(), eventNodeActual.getCity().getProvince().getCountry().getRegion().getId(),
                        () -> "should return event node with region id: " + regionNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getCountry().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), eventNodeActual.getCity().getProvince().getCountry().getRegion().getName(),
                        () -> "should return event node with region name: " + regionNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getProvince().getCountry().getRegion().getName()),
                () -> verify(targetService, times(1)).saveNew(targetDTOExpected),
                () -> verifyNoMoreInteractions(targetService),
                () -> verify(eventRepository, times(1)).save(eventNodeExpected),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(cityService));
    }

    @Test
    void when_delete_event_without_target_should_delete_event() {

        Long eventId = 1L;

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected).build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.withProvince(provinceNodeExpected).build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withCity(cityNodeExpected).build(ObjectType.NODE);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventNodeExpected));

        Optional<EventNode> eventNodeOptionalActual = eventService.delete(eventId);

        EventNode eventNodeActual = eventNodeOptionalActual.get();

        assertAll(
                () -> assertNotNull(eventNodeActual.getId(),
                        () -> "should return event node with new id, but was: " + eventNodeActual.getId()),
                () -> assertEquals(eventNodeExpected.getSummary(), eventNodeActual.getSummary(),
                        () -> "should return event node with summary: " + eventNodeExpected.getSummary() + ", but was: "
                                + eventNodeActual.getSummary()),
                () -> assertEquals(eventNodeExpected.getMotive(), eventNodeActual.getMotive(),
                        () -> "should return event node with motive: " + eventNodeExpected.getMotive() + ", but was: "
                                + eventNodeActual.getMotive()),
                () -> assertEquals(eventNodeExpected.getDate(), eventNodeActual.getDate(),
                        () -> "should return event node with date: " + eventNodeExpected.getDate() + ", but was: "
                                + eventNodeActual.getDate()),
                () -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
                        eventNodeActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event node which was part of multiple incidents: "
                                + eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was was: "
                                + eventNodeActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
                        () -> "should return event node which was successful: " + eventNodeExpected.getIsSuccessful()
                                + ", but was: " + eventNodeActual.getIsSuccessful()),
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), eventNodeActual.getIsSuicidal(),
                        () -> "should return event node which was suicidal: " + eventNodeExpected.getIsSuicidal()
                                + ", but was: " + eventNodeActual.getIsSuicidal()),
                () -> assertNull(eventNodeActual.getTarget(),
                        () -> "should return event node with null target, but was: " + eventNodeActual.getTarget()),
                () -> assertNotNull(eventNodeActual.getCity(),
                        () -> "should return event node with not null cty, but was: null"),
                () -> assertEquals(cityNodeExpected, eventNodeActual.getCity(),
                        () -> "should return event node with city: " + cityNodeExpected + ", but was: "
                                + eventNodeActual.getCity()),
                () -> assertEquals(cityNodeExpected.getName(), eventNodeActual.getCity().getName(),
                        () -> "should return event node with city name: " + cityNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getName()),
                () -> assertEquals(cityNodeExpected.getLatitude(), eventNodeActual.getCity().getLatitude(),
                        () -> "should return event node with city latitude: " + cityNodeExpected.getLatitude() + ", but was: "
                                + eventNodeActual.getCity().getLatitude()),
                () -> assertEquals(cityNodeExpected.getLongitude(), eventNodeActual.getCity().getLongitude(),
                        () -> "should return event node with city longitude: " + cityNodeExpected.getLongitude() + ", but was: "
                                + eventNodeActual.getCity().getLongitude()),

                () -> assertNotNull(eventNodeActual.getCity().getProvince(),
                        () -> "should return event node with not null province, but was: null"),
                () -> assertEquals(provinceNodeExpected, eventNodeActual.getCity().getProvince(),
                        () -> "should return event node with province: " + provinceNodeExpected + ", but was: "
                                + eventNodeActual.getCity().getProvince()),
                () -> assertEquals(provinceNodeExpected.getId(), eventNodeActual.getCity().getProvince().getId(),
                        () -> "should return event node with province id: " + provinceNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getId()),
                () -> assertEquals(provinceNodeExpected.getName(), eventNodeActual.getCity().getProvince().getName(),
                        () -> "should return event node with province name: " + provinceNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getProvince().getName()),
                () -> assertEquals(countryNodeExpected, eventNodeActual.getCity().getProvince().getCountry(),
                        () -> "should return event node with country: " + countryNodeExpected + ", but was: " + eventNodeActual.getCity().getProvince().getCountry()),
                () -> assertEquals(countryNodeExpected.getId(), eventNodeActual.getCity().getProvince().getCountry().getId(),
                        () -> "should return event node with country id: " + countryNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getCountry().getId()),
                () -> assertEquals(countryNodeExpected.getName(), eventNodeActual.getCity().getProvince().getCountry().getName(),
                        () -> "should return event node with country name: " + countryNodeExpected.getName()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getCountry()),
                () -> assertNotNull(eventNodeActual.getCity().getProvince().getCountry().getRegion(),
                        () -> "should return event node with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected, eventNodeActual.getCity().getProvince().getCountry().getRegion(),
                        () -> "should return event node with region: " + regionNodeExpected + ", but was: "
                                + eventNodeActual.getCity().getProvince().getCountry().getRegion()),
                () -> assertEquals(regionNodeExpected.getId(), eventNodeActual.getCity().getProvince().getCountry().getRegion().getId(),
                        () -> "should return event node with region id: " + regionNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getCountry().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), eventNodeActual.getCity().getProvince().getCountry().getRegion().getName(),
                        () -> "should return event node with region name: " + regionNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getProvince().getCountry().getRegion().getName()),
                () -> verify(eventRepository, times(1)).findById(eventId),
                () -> verify(eventRepository, times(1)).delete(eventNodeExpected),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verifyNoInteractions(targetService),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(cityService));
    }

    @Test
    void when_delete_event_should_delete_event_and_target() {

        Long eventId = 2L;
        Long targetId = 1L;

        CountryNode countryNodeExpected = (CountryNode) countryBuilder.build(ObjectType.NODE);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withId(targetId).withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withTarget(targetNodeExpected).withCity(cityNodeExpected)
                .build(ObjectType.NODE);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventNodeExpected));
        when(targetService.delete(targetId)).thenReturn(Optional.of(targetNodeExpected));

        Optional<EventNode> eventNodeOptionalActual = eventService.delete(eventId);

        EventNode eventNodeActual = eventNodeOptionalActual.get();

        assertAll(
                () -> assertNotNull(eventNodeActual.getId(),
                        () -> "should return event node with new id, but was: " + eventNodeActual.getId()),
                () -> assertEquals(eventNodeExpected.getSummary(), eventNodeActual.getSummary(),
                        () -> "should return event node with summary: " + eventNodeExpected.getSummary() + ", but was: "
                                + eventNodeActual.getSummary()),
                () -> assertEquals(eventNodeExpected.getMotive(), eventNodeActual.getMotive(),
                        () -> "should return event node with motive: " + eventNodeExpected.getMotive() + ", but was: "
                                + eventNodeActual.getMotive()),
                () -> assertEquals(eventNodeExpected.getDate(), eventNodeActual.getDate(),
                        () -> "should return event node with date: " + eventNodeExpected.getDate() + ", but was: "
                                + eventNodeActual.getDate()),
                () -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
                        eventNodeActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event node which was part of multiple incidents: "
                                + eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was was: "
                                + eventNodeActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
                        () -> "should return event node which was successful: " + eventNodeExpected.getIsSuccessful()
                                + ", but was: " + eventNodeActual.getIsSuccessful()),
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), eventNodeActual.getIsSuicidal(),
                        () -> "should return event node which was suicidal: " + eventNodeExpected.getIsSuicidal()
                                + ", but was: " + eventNodeActual.getIsSuicidal()),
                () -> assertNotNull(eventNodeActual.getTarget(),
                        () -> "should return event node with not null target, but was: null"),
                () -> assertEquals(targetNodeExpected, eventNodeActual.getTarget(),
                        () -> "should return event node with target: " + targetNodeExpected + ", but was: "
                                + eventNodeActual.getTarget()),
                () -> assertEquals(targetNodeExpected.getId(), eventNodeActual.getTarget().getId(),
                        () -> "should return event node with target id: " + targetNodeExpected.getId() + ", but was: "
                                + eventNodeActual.getTarget().getId()),
                () -> assertEquals(targetNodeExpected.getTarget(), eventNodeActual.getTarget().getTarget(),
                        () -> "should return event node with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + eventNodeActual.getTarget().getTarget()),
                () -> assertEquals(countryNodeExpected, eventNodeActual.getTarget().getCountryOfOrigin(),
                        () -> "should return event node with country: " + countryNodeExpected + ", but was: " +
                                eventNodeActual.getTarget().getCountryOfOrigin()),
                () -> assertEquals(countryNodeExpected.getId(), eventNodeActual.getTarget().getCountryOfOrigin().getId(),
                        () -> "should return event node with country id: " + countryNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getTarget().getId()),
                () -> assertEquals(countryNodeExpected.getName(), eventNodeActual.getTarget().getCountryOfOrigin().getName(),
                        () -> "should return event node with country name: " + countryNodeExpected.getName()
                                + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin()),
                () -> assertNull(eventNodeActual.getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return event node with null region, but was: " +
                                eventNodeActual.getTarget().getCountryOfOrigin().getRegion()),

                () -> assertNotNull(eventNodeActual.getCity(),
                        () -> "should return event node with not null cty, but was: null"),
                () -> assertEquals(cityNodeExpected, eventNodeActual.getCity(),
                        () -> "should return event node with city: " + cityNodeExpected + ", but was: "
                                + eventNodeActual.getCity()),
                () -> assertEquals(cityNodeExpected.getName(), eventNodeActual.getCity().getName(),
                        () -> "should return event node with city name: " + cityNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getName()),
                () -> assertEquals(cityNodeExpected.getLatitude(), eventNodeActual.getCity().getLatitude(),
                        () -> "should return event node with city latitude: " + cityNodeExpected.getLatitude() + ", but was: "
                                + eventNodeActual.getCity().getLatitude()),
                () -> assertEquals(cityNodeExpected.getLongitude(), eventNodeActual.getCity().getLongitude(),
                        () -> "should return event node with city longitude: " + cityNodeExpected.getLongitude() + ", but was: "
                                + eventNodeActual.getCity().getLongitude()),

                () -> assertNull(eventNodeActual.getCity().getProvince(),
                        () -> "should return event node with null province, but was: " + eventNodeActual.getCity().getProvince()),
                () -> verify(eventRepository, times(1)).findById(eventId),
                () -> verify(eventRepository, times(1)).delete(eventNodeExpected),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verify(targetService, times(1)).delete(targetId),
                () -> verifyNoMoreInteractions(targetService),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(cityService));
    }

    @Test
    void when_delete_event_by_id_not_existing_event_should_return_empty_optional() {

        Long eventId = 1L;

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        Optional<EventNode> eventNodeOptional = eventService.delete(eventId);

        assertAll(
                () -> assertTrue(eventNodeOptional.isEmpty(),
                        () -> "should return empty event node optional, but was: " + eventNodeOptional.get()),
                () -> verify(eventRepository, times(1)).findById(eventId),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(targetService),
                () -> verifyNoInteractions(cityService));
    }

    @Test
    void when_delete_event_target_should_delete_target() {

        Long eventId = 1L;
        Long targetId = 2L;

        CountryNode countryNodeExpected = (CountryNode) countryBuilder.build(ObjectType.NODE);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withId(targetId).withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withId(eventId)
                .withTarget(targetNodeExpected).withCity(cityNodeExpected)
                .build(ObjectType.NODE);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventNodeExpected));
        when(targetService.delete(targetId)).thenReturn(Optional.of(targetNodeExpected));

        Optional<EventNode> eventNodeOptionalActual = eventService.deleteEventTarget(eventId);

        EventNode eventNodeActual = eventNodeOptionalActual.get();

        assertAll(
                () -> assertNotNull(eventNodeActual.getId(),
                        () -> "should return event node with new id, but was: " + eventNodeActual.getId()),
                () -> assertEquals(eventNodeExpected.getSummary(), eventNodeActual.getSummary(),
                        () -> "should return event node with summary: " + eventNodeExpected.getSummary() + ", but was: "
                                + eventNodeActual.getSummary()),
                () -> assertEquals(eventNodeExpected.getMotive(), eventNodeActual.getMotive(),
                        () -> "should return event node with motive: " + eventNodeExpected.getMotive() + ", but was: "
                                + eventNodeActual.getMotive()),
                () -> assertEquals(eventNodeExpected.getDate(), eventNodeActual.getDate(),
                        () -> "should return event node with date: " + eventNodeExpected.getDate() + ", but was: "
                                + eventNodeActual.getDate()),
                () -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
                        eventNodeActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event node which was part of multiple incidents: "
                                + eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was was: "
                                + eventNodeActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
                        () -> "should return event node which was successful: " + eventNodeExpected.getIsSuccessful()
                                + ", but was: " + eventNodeActual.getIsSuccessful()),
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), eventNodeActual.getIsSuicidal(),
                        () -> "should return event node which was suicidal: " + eventNodeExpected.getIsSuicidal()
                                + ", but was: " + eventNodeActual.getIsSuicidal()),
                () -> assertNotNull(eventNodeActual.getTarget(),
                        () -> "should return event node with not null target, but was: null"),
                () -> assertEquals(targetNodeExpected, eventNodeActual.getTarget(),
                        () -> "should return event node with target: " + targetNodeExpected + ", but was: "
                                + eventNodeActual.getTarget()),
                () -> assertEquals(targetNodeExpected.getId(), eventNodeActual.getTarget().getId(),
                        () -> "should return event node with target id: " + targetNodeExpected.getId() + ", but was: "
                                + eventNodeActual.getTarget().getId()),
                () -> assertEquals(targetNodeExpected.getTarget(), eventNodeActual.getTarget().getTarget(),
                        () -> "should return event node with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + eventNodeActual.getTarget().getTarget()),
                () -> assertEquals(countryNodeExpected, eventNodeActual.getTarget().getCountryOfOrigin(),
                        () -> "should return event node with country: " + countryNodeExpected + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin()),
                () -> assertEquals(countryNodeExpected.getId(), eventNodeActual.getTarget().getCountryOfOrigin().getId(),
                        () -> "should return event node with country id: " + countryNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getTarget().getId()),
                () -> assertEquals(countryNodeExpected.getName(), eventNodeActual.getTarget().getCountryOfOrigin().getName(),
                        () -> "should return event node with country name: " + countryNodeExpected.getName()
                                + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin()),
                () -> assertNull(eventNodeActual.getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return event node with not null region, but was: null"),

                () -> assertNotNull(eventNodeActual.getCity(),
                        () -> "should return event node with not null cty, but was: null"),
                () -> assertEquals(cityNodeExpected, eventNodeActual.getCity(),
                        () -> "should return event node with city: " + cityNodeExpected + ", but was: "
                                + eventNodeActual.getCity()),
                () -> assertEquals(cityNodeExpected.getName(), eventNodeActual.getCity().getName(),
                        () -> "should return event node with city name: " + cityNodeExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getName()),
                () -> assertEquals(cityNodeExpected.getLatitude(), eventNodeActual.getCity().getLatitude(),
                        () -> "should return event node with city latitude: " + cityNodeExpected.getLatitude() + ", but was: "
                                + eventNodeActual.getCity().getLatitude()),
                () -> assertEquals(cityNodeExpected.getLongitude(), eventNodeActual.getCity().getLongitude(),
                        () -> "should return event node with city longitude: " + cityNodeExpected.getLongitude() + ", but was: "
                                + eventNodeActual.getCity().getLongitude()),

                () -> assertNull(eventNodeActual.getCity().getProvince(),
                        () -> "should return event node with null province, but was: " +
                                eventNodeActual.getCity().getProvince()),
                () -> verify(eventRepository, times(1)).findById(eventId),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verify(targetService, times(1)).delete(targetId),
                () -> verifyNoMoreInteractions(targetService),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(cityService));
    }

    @Test
    void when_delete_event_target_but_event_does_not_have_target_should_throw_exception() {

        Long eventId = 1L;

        EventNode eventNodeExpected = (EventNode) eventBuilder.withId(eventId).build(ObjectType.NODE);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventNodeExpected));

        assertAll(
                () -> assertThrows(ResourceNotFoundException.class,
                        () -> eventService.deleteEventTarget(eventId), "should throw ResourceNotFoundException but wasn't"),
                () -> verify(eventRepository, times(1)).findById(eventId),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(targetService),
                () -> verifyNoInteractions(cityService));
    }

    @Test
    void when_delete_event_target_but_event_does_not_exist_should_return_empty_optional() {

        Long eventId = 1L;

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        Optional<EventNode> eventNodeOptional = eventService.deleteEventTarget(eventId);

        assertAll(
                () -> assertTrue(eventNodeOptional.isEmpty(),
                        () -> "should return empty event node optional, but was: " + eventNodeOptional.get()),
                () -> verify(eventRepository, times(1)).findById(eventId),
                () -> verifyNoMoreInteractions(eventRepository),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(targetService),
                () -> verifyNoInteractions(cityService));
    }

    private List<EventNode> createEventNodeList(int listSize) {

        EventBuilder eventBuilder = new EventBuilder();

        List<EventNode> eventsListExpected = new ArrayList<>();

        int count = 0;

        while (count < listSize) {

            CityNode cityNode = (CityNode) cityBuilder.withId((long) count).withName("city" + count).withLatitude(10.0 + count)
                    .withLongitude(15.0 + count).build(ObjectType.NODE);

            TargetNode targetNode = (TargetNode) targetBuilder.withId((long) count).withTarget("target" + count)
                    .build(ObjectType.NODE);

            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).withCity(cityNode).build(ObjectType.NODE);

            eventsListExpected.add(eventNode);

            count++;
        }

        return eventsListExpected;
    }
}
