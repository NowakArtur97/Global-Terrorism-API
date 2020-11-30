package com.nowakArtur97.globalTerrorismAPI.feature.event;

import com.nowakArtur97.globalTerrorismAPI.feature.city.CityModel;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityModelAssembler;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityNode;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetModel;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetModelAssembler;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetNode;
import com.nowakArtur97.globalTerrorismAPI.feature.victim.VictimModel;
import com.nowakArtur97.globalTerrorismAPI.feature.victim.VictimModelAssembler;
import com.nowakArtur97.globalTerrorismAPI.feature.victim.VictimNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.CityBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.EventBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.VictimBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventModelAssembler_Tests")
@DisabledOnOs(OS.LINUX)
class EventModelAssemblerTest {

    private final String TARGET_BASE_PATH = "http://localhost/api/v1/targets";
    private final String CITY_BASE_PATH = "http://localhost/api/v1/cities";
    private final String VICTIM_BASE_PATH = "http://localhost/api/v1/victims";
    private final String EVENT_BASE_PATH = "http://localhost/api/v1/events";

    private static TargetBuilder targetBuilder;
    private static CityBuilder cityBuilder;
    private static VictimBuilder victimBuilder;
    private static EventBuilder eventBuilder;

    private EventModelAssembler modelAssembler;

    @Mock
    private TargetModelAssembler targetModelAssembler;

    @Mock
    private CityModelAssembler cityModelAssembler;

    @Mock
    private VictimModelAssembler victimModelAssembler;

    @Mock
    private ModelMapper modelMapper;

    @BeforeAll
    private static void setUpBuilders() {

        targetBuilder = new TargetBuilder();
        cityBuilder = new CityBuilder();
        victimBuilder = new VictimBuilder();
        eventBuilder = new EventBuilder();
    }

    @BeforeEach
    private void setUp() {

        modelAssembler = new EventModelAssembler(targetModelAssembler, cityModelAssembler, victimModelAssembler, modelMapper);
    }

    @Test
    void when_map_event_node_to_model_should_return_event_model() {

        Long targetId = 1L;
        Long cityId = 2L;
        Long victimId = 3L;
        Long eventId = 4L;
        TargetNode targetNode = (TargetNode) targetBuilder.withId(targetId).build(ObjectType.NODE);
        CityNode cityNode = (CityNode) cityBuilder.withId(cityId).build(ObjectType.NODE);
        VictimNode victimNode = (VictimNode) victimBuilder.withId(victimId).build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withId(eventId).withTarget(targetNode).withCity(cityNode).withVictim(victimNode)
                .build(ObjectType.NODE);
        TargetModel targetModel = (TargetModel) targetBuilder.withId(targetId).build(ObjectType.MODEL);
        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetId.intValue();
        Link targetLink = new Link(pathToTargetLink);
        targetModel.add(targetLink);
        CityModel cityModel = (CityModel) cityBuilder.withId(cityId).build(ObjectType.MODEL);
        String pathToCityLink = CITY_BASE_PATH + "/" + targetId.intValue();
        Link cityLink = new Link(pathToCityLink);
        cityModel.add(cityLink);
        VictimModel victimModel = (VictimModel) victimBuilder.withId(victimId).build(ObjectType.MODEL);
        String pathToVictimLink = VICTIM_BASE_PATH + "/" + victimId.intValue();
        Link victimLink = new Link(pathToVictimLink);
        victimModel.add(victimLink);
        EventModel eventModel = (EventModel) eventBuilder.withId(eventId).withTarget(targetModel).withCity(cityModel)
                .withVictim(victimModel).build(ObjectType.MODEL);
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
        String pathToEventTargetLink = EVENT_BASE_PATH + "/" + eventId.intValue() + "/targets";

        when(modelMapper.map(eventNode, EventModel.class)).thenReturn(eventModel);
        when(targetModelAssembler.toModel(eventNode.getTarget())).thenReturn(targetModel);
        when(cityModelAssembler.toModel(eventNode.getCity())).thenReturn(cityModel);
        when(victimModelAssembler.toModel(eventNode.getVictim())).thenReturn(victimModel);

        EventModel eventModelActual = modelAssembler.toModel(eventNode);

        assertAll(
                () -> assertEquals(pathToEventLink, eventModelActual.getLink("self").get().getHref(),
                        () -> "should return event model with self link: " + pathToEventLink + ", but was: "
                                + eventModelActual.getLink("self").get().getHref()),
                () -> assertEquals(pathToEventTargetLink, eventModelActual.getLink("target").get().getHref(),
                        () -> "should return event model with target link: " + pathToEventTargetLink + ", but was: "
                                + eventModelActual.getLink("target").get().getHref()),
                () -> assertEquals(pathToTargetLink, eventModelActual.getTarget().getLink("self").get().getHref(),
                        () -> "should return event target model with self link: " + pathToTargetLink + ", but was: "
                                + eventModelActual.getTarget().getLink("self").get().getHref()),
                () -> assertEquals(pathToCityLink, eventModelActual.getCity().getLink("self").get().getHref(),
                        () -> "should return event city model with self link: " + pathToCityLink + ", but was: "
                                + eventModelActual.getCity().getLink("self").get().getHref()),
                () -> assertEquals(pathToVictimLink, eventModelActual.getVictim().getLink("self").get().getHref(),
                        () -> "should return event victim model with self link: " + pathToVictimLink + ", but was: "
                                + eventModelActual.getVictim().getLink("self").get().getHref()),

                () -> assertEquals(eventNode.getId(), eventModelActual.getId(),
                        () -> "should return event model with id: " + eventNode.getSummary() + ", but was: "
                                + eventModelActual.getSummary()), () -> assertEquals(eventNode.getSummary(), eventModelActual.getSummary(),
                        () -> "should return event model with summary: " + eventNode.getSummary() + ", but was: "
                                + eventModelActual.getSummary()),
                () -> assertEquals(eventNode.getMotive(), eventModelActual.getMotive(),
                        () -> "should return event model with motive: " + eventNode.getMotive() + ", but was: "
                                + eventModelActual.getMotive()),
                () -> assertNotNull(eventModelActual.getDate(),
                        () -> "should return event model with date: " + eventNode.getDate() + ", but was null"),
                () -> assertEquals(eventNode.getIsPartOfMultipleIncidents(), eventModelActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event model which was part of multiple incidents: "
                                + eventNode.getIsPartOfMultipleIncidents() + ", but was: "
                                + eventModelActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNode.getIsSuccessful(), eventModelActual.getIsSuccessful(),
                        () -> "should return event model which was successful: " + eventNode.getIsSuccessful()
                                + ", but was: " + eventModelActual.getIsSuccessful()),
                () -> assertEquals(eventNode.getIsSuicidal(), eventModelActual.getIsSuicidal(),
                        () -> "should return event model which was suicidal: " + eventNode.getIsSuicidal() + ", but was: "
                                + eventModelActual.getIsSuicidal()),
                () -> assertNotNull(eventNode.getTarget(),
                        () -> "should return event model with not null target, but was: null"),
                () -> assertEquals(targetModel, eventModelActual.getTarget(),
                        () -> "should return event model with target model: " + targetModel + ", but was: "
                                + eventModelActual.getTarget()),
                () -> assertEquals(targetModel.getId(), eventModelActual.getTarget().getId(),
                        () -> "should return event model with target model id: " + targetModel.getId() + ", but was: "
                                + eventModelActual.getTarget().getId()),
                () -> assertEquals(targetModel.getTarget(), eventModelActual.getTarget().getTarget(),
                        () -> "should return event model with target model name: " + targetModel.getTarget() + ", but was: "
                                + eventModelActual.getTarget().getTarget()),
                () -> assertNotNull(eventNode.getCity(),
                        () -> "should return event model with not null city, but was: null"),
                () -> assertEquals(cityModel.getId(), eventModelActual.getCity().getId(),
                        () -> "should return event model with city model id: " + cityModel.getId() + ", but was: "
                                + eventModelActual.getCity().getId()),
                () -> assertEquals(cityModel.getName(), eventModelActual.getCity().getName(),
                        () -> "should return event model with city name: " + cityModel.getName() + ", but was: "
                                + eventModelActual.getCity().getName()),
                () -> assertEquals(cityModel.getLatitude(), eventModelActual.getCity().getLatitude(),
                        () -> "should return event model with city latitude: " + cityModel.getLatitude() + ", but was: "
                                + eventModelActual.getCity().getLatitude()),
                () -> assertEquals(cityModel.getLongitude(), eventModelActual.getCity().getLongitude(),
                        () -> "should return event model with city longitude: " + cityModel.getLongitude() + ", but was: "
                                + eventModelActual.getCity().getLongitude()),

                () -> assertNotNull(eventNode.getVictim(),
                        () -> "should return event model with not null victim, but was: null"),
                () -> assertEquals(victimModel.getId(), eventModelActual.getVictim().getId(),
                        () -> "should return event model with victim model id: " + victimModel.getId() + ", but was: "
                                + eventModelActual.getVictim().getId()),
                () -> assertEquals(victimModel.getTotalNumberOfFatalities(), eventModelActual.getVictim().getTotalNumberOfFatalities(),
                        () -> "should return event model with victim total number of fatalities: "
                                + victimModel.getTotalNumberOfFatalities() + ", but was: "
                                + eventModelActual.getVictim().getTotalNumberOfFatalities()),
                () -> assertEquals(victimModel.getNumberOfPerpetratorsFatalities(),
                        eventModelActual.getVictim().getNumberOfPerpetratorsFatalities(),
                        () -> "should return event model with victim number of perpetrators fatalities: "
                                + victimModel.getNumberOfPerpetratorsFatalities() + ", but was: "
                                + eventModelActual.getVictim().getNumberOfPerpetratorsFatalities()),
                () -> assertEquals(victimModel.getTotalNumberOfInjured(), eventModelActual.getVictim().getTotalNumberOfInjured(),
                        () -> "should return event model with victim total number of injured: "
                                + victimModel.getTotalNumberOfInjured() + ", but was: "
                                + eventModelActual.getVictim().getTotalNumberOfInjured()),
                () -> assertEquals(victimModel.getNumberOfPerpetratorsInjured(),
                        eventModelActual.getVictim().getNumberOfPerpetratorsInjured(),
                        () -> "should return event model with victim number of perpetrators injured: "
                                + victimModel.getNumberOfPerpetratorsInjured() + ", but was: "
                                + eventModelActual.getVictim().getNumberOfPerpetratorsInjured()),
                () -> assertEquals(victimModel.getValueOfPropertyDamage(),
                        eventModelActual.getVictim().getValueOfPropertyDamage(),
                        () -> "should return event model with victim value of property damage: "
                                + victimModel.getValueOfPropertyDamage() + ", but was: "
                                + eventModelActual.getVictim().getValueOfPropertyDamage()),

                () -> assertFalse(eventModelActual.getTarget().getLinks().isEmpty(),
                        () -> "should return event model with target model with links, but wasn't"),
                () -> assertFalse(eventModelActual.getCity().getLinks().isEmpty(),
                        () -> "should return event model with city model with links, but wasn't"),
                () -> assertFalse(eventModelActual.getVictim().getLinks().isEmpty(),
                        () -> "should return event model with victim model with links, but wasn't"),
                () -> assertNotNull(eventModelActual.getLinks(), () -> "should return model with links, but was: " + eventModelActual),
                () -> assertFalse(eventModelActual.getLinks().isEmpty(),
                        () -> "should return model with links, but was: " + eventModelActual),
                () -> verify(modelMapper, times(1)).map(eventNode, EventModel.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(targetModelAssembler, times(1)).toModel(eventNode.getTarget()),
                () -> verifyNoMoreInteractions(targetModelAssembler),
                () -> verify(cityModelAssembler, times(1)).toModel(eventNode.getCity()),
                () -> verifyNoMoreInteractions(cityModelAssembler),
                () -> verify(victimModelAssembler, times(1)).toModel(eventNode.getVictim()),
                () -> verifyNoMoreInteractions(victimModelAssembler));
    }

    @Test
    void when_map_event_node_to_model_without_target_should_return_event_model_without_target() {

        Long cityId = 1L;
        Long victimId = 2L;
        Long eventId = 3L;
        CityNode cityNode = (CityNode) cityBuilder.withId(cityId).build(ObjectType.NODE);
        VictimNode victimNode = (VictimNode) victimBuilder.withId(victimId).build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withId(eventId).withCity(cityNode).withVictim(victimNode)
                .build(ObjectType.NODE);
        CityModel cityModel = (CityModel) cityBuilder.withId(cityId).build(ObjectType.MODEL);
        String pathToCityLink = CITY_BASE_PATH + "/" + cityId.intValue();
        Link cityLink = new Link(pathToCityLink);
        cityModel.add(cityLink);
        VictimModel victimModel = (VictimModel) victimBuilder.withId(victimId).build(ObjectType.MODEL);
        String pathToVictimLink = VICTIM_BASE_PATH + "/" + victimId.intValue();
        Link victimLink = new Link(pathToVictimLink);
        victimModel.add(victimLink);
        EventModel eventModel = (EventModel) eventBuilder.withId(eventId).withCity(cityModel).withVictim(victimModel)
                .build(ObjectType.MODEL);
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();

        when(modelMapper.map(eventNode, EventModel.class)).thenReturn(eventModel);
        when(cityModelAssembler.toModel(eventNode.getCity())).thenReturn(cityModel);
        when(victimModelAssembler.toModel(eventNode.getVictim())).thenReturn(victimModel);

        EventModel eventModelActual = modelAssembler.toModel(eventNode);

        assertAll(
                () -> assertEquals(pathToEventLink, eventModelActual.getLink("self").get().getHref(),
                        () -> "should return event model with self link: " + pathToEventLink + ", but was: "
                                + eventModelActual.getLink("self").get().getHref()),
                () -> assertTrue(eventModelActual.getLink("target").isEmpty(),
                        () -> "should return event model without target link, but was: "
                                + eventModelActual.getLink("target").get().getHref()),
                () -> assertEquals(pathToCityLink, eventModelActual.getCity().getLink("self").get().getHref(),
                        () -> "should return event city model with self link: " + pathToCityLink + ", but was: "
                                + eventModelActual.getCity().getLink("self").get().getHref()),
                () -> assertEquals(pathToVictimLink, eventModelActual.getVictim().getLink("self").get().getHref(),
                        () -> "should return event victim model with self link: " + pathToVictimLink + ", but was: "
                                + eventModelActual.getVictim().getLink("self").get().getHref()),

                () -> assertNotNull(eventModelActual.getId(),
                        () -> "should return event model with id, but was null"),
                () -> assertEquals(eventNode.getId(), eventModelActual.getId(),
                        () -> "should return event model with id: " + eventNode.getSummary() + ", but was: "
                                + eventModelActual.getSummary()), () -> assertEquals(eventNode.getSummary(), eventModelActual.getSummary(),
                        () -> "should return event model with summary: " + eventNode.getSummary() + ", but was: "
                                + eventModelActual.getSummary()),
                () -> assertEquals(eventNode.getMotive(), eventModelActual.getMotive(),
                        () -> "should return event model with motive: " + eventNode.getMotive() + ", but was: "
                                + eventModelActual.getMotive()),
                () -> assertNotNull(eventModelActual.getDate(),
                        () -> "should return event model with date: " + eventNode.getDate() + ", but was null"),
                () -> assertEquals(eventNode.getIsPartOfMultipleIncidents(), eventModelActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event model which was part of multiple incidents: "
                                + eventNode.getIsPartOfMultipleIncidents() + ", but was: "
                                + eventModelActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNode.getIsSuccessful(), eventModelActual.getIsSuccessful(),
                        () -> "should return event model which was successful: " + eventNode.getIsSuccessful()
                                + ", but was: " + eventModelActual.getIsSuccessful()),
                () -> assertEquals(eventNode.getIsSuicidal(), eventModelActual.getIsSuicidal(),
                        () -> "should return event model which was suicidal: " + eventNode.getIsSuicidal() + ", but was: "
                                + eventModelActual.getIsSuicidal()),
                () -> assertNull(eventNode.getTarget(),
                        () -> "should return event model without target, but was: " + eventNode.getTarget()),
                () -> assertNotNull(eventNode.getCity(),
                        () -> "should return event model with not null city, but was: null"),
                () -> assertEquals(cityModel.getId(), eventModelActual.getCity().getId(),
                        () -> "should return event model with city model id: " + cityModel.getId() + ", but was: "
                                + eventModelActual.getCity().getId()),
                () -> assertEquals(cityModel.getName(), eventModelActual.getCity().getName(),
                        () -> "should return event model with target city name: " + cityModel.getName() + ", but was: "
                                + eventModelActual.getCity().getName()),
                () -> assertEquals(cityModel.getLatitude(), eventModelActual.getCity().getLatitude(),
                        () -> "should return event model with target city latitude: " + cityModel.getLatitude() + ", but was: "
                                + eventModelActual.getCity().getLatitude()),
                () -> assertEquals(cityModel.getLongitude(), eventModelActual.getCity().getLongitude(),
                        () -> "should return event model with target city longitude: " + cityModel.getLongitude() + ", but was: "
                                + eventModelActual.getCity().getLongitude()),

                () -> assertNotNull(eventNode.getVictim(),
                        () -> "should return event model with not null victim, but was: null"),
                () -> assertEquals(victimModel.getId(), eventModelActual.getVictim().getId(),
                        () -> "should return event model with victim model id: " + victimModel.getId() + ", but was: "
                                + eventModelActual.getVictim().getId()),
                () -> assertEquals(victimModel.getTotalNumberOfFatalities(), eventModelActual.getVictim().getTotalNumberOfFatalities(),
                        () -> "should return event model with victim total number of fatalities: "
                                + victimModel.getTotalNumberOfFatalities() + ", but was: "
                                + eventModelActual.getVictim().getTotalNumberOfFatalities()),
                () -> assertEquals(victimModel.getNumberOfPerpetratorsFatalities(),
                        eventModelActual.getVictim().getNumberOfPerpetratorsFatalities(),
                        () -> "should return event model with victim number of perpetrators fatalities: "
                                + victimModel.getNumberOfPerpetratorsFatalities() + ", but was: "
                                + eventModelActual.getVictim().getNumberOfPerpetratorsFatalities()),
                () -> assertEquals(victimModel.getTotalNumberOfInjured(), eventModelActual.getVictim().getTotalNumberOfInjured(),
                        () -> "should return event model with victim total number of injured: "
                                + victimModel.getTotalNumberOfInjured() + ", but was: "
                                + eventModelActual.getVictim().getTotalNumberOfInjured()),
                () -> assertEquals(victimModel.getNumberOfPerpetratorsInjured(),
                        eventModelActual.getVictim().getNumberOfPerpetratorsInjured(),
                        () -> "should return event model with victim number of perpetrators injured: "
                                + victimModel.getNumberOfPerpetratorsInjured() + ", but was: "
                                + eventModelActual.getVictim().getNumberOfPerpetratorsInjured()),
                () -> assertEquals(victimModel.getValueOfPropertyDamage(),
                        eventModelActual.getVictim().getValueOfPropertyDamage(),
                        () -> "should return event model with victim value of property damage: "
                                + victimModel.getValueOfPropertyDamage() + ", but was: "
                                + eventModelActual.getVictim().getValueOfPropertyDamage()),

                () -> assertFalse(eventModelActual.getCity().getLinks().isEmpty(),
                        () -> "should return event model with city model with links, but wasn't"),
                () -> assertFalse(eventModelActual.getVictim().getLinks().isEmpty(),
                        () -> "should return event model with victim model with links, but wasn't"),
                () -> assertNotNull(eventModelActual.getLinks(), () -> "should return model with links, but was: " + eventModelActual),
                () -> assertFalse(eventModelActual.getLinks().isEmpty(),
                        () -> "should return model with links, but was: " + eventModelActual),
                () -> verify(modelMapper, times(1)).map(eventNode, EventModel.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(cityModelAssembler, times(1)).toModel(eventNode.getCity()),
                () -> verifyNoMoreInteractions(cityModelAssembler),
                () -> verify(victimModelAssembler, times(1)).toModel(eventNode.getVictim()),
                () -> verifyNoMoreInteractions(victimModelAssembler),
                () -> verifyNoInteractions(targetModelAssembler));
    }

    @Test
    void when_map_event_node_without_city_to_model_should_return_event_model_without_city() {

        Long targetId = 1L;
        Long victimId = 2L;
        Long eventId = 3L;
        TargetNode targetNode = (TargetNode) targetBuilder.withId(targetId).build(ObjectType.NODE);
        VictimNode victimNode = (VictimNode) victimBuilder.withId(victimId).build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withId(eventId).withTarget(targetNode).withVictim(victimNode)
                .build(ObjectType.NODE);
        TargetModel targetModel = (TargetModel) targetBuilder.withId(targetId).build(ObjectType.MODEL);
        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetId.intValue();
        Link targetLink = new Link(pathToTargetLink);
        targetModel.add(targetLink);
        VictimModel victimModel = (VictimModel) victimBuilder.withId(victimId).build(ObjectType.MODEL);
        String pathToVictimLink = VICTIM_BASE_PATH + "/" + victimId.intValue();
        Link victimLink = new Link(pathToVictimLink);
        victimModel.add(victimLink);
        EventModel eventModel = (EventModel) eventBuilder.withId(eventId).withTarget(targetModel).withVictim(victimModel)
                .build(ObjectType.MODEL);
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
        String pathToEventTargetLink = EVENT_BASE_PATH + "/" + eventId.intValue() + "/targets";

        when(modelMapper.map(eventNode, EventModel.class)).thenReturn(eventModel);
        when(targetModelAssembler.toModel(eventNode.getTarget())).thenReturn(targetModel);
        when(victimModelAssembler.toModel(eventNode.getVictim())).thenReturn(victimModel);

        EventModel eventModelActual = modelAssembler.toModel(eventNode);

        assertAll(
                () -> assertEquals(pathToEventLink, eventModelActual.getLink("self").get().getHref(),
                        () -> "should return event model with self link: " + pathToEventLink + ", but was: "
                                + eventModelActual.getLink("self").get().getHref()),
                () -> assertEquals(pathToEventTargetLink, eventModelActual.getLink("target").get().getHref(),
                        () -> "should return event model with target link: " + pathToEventTargetLink + ", but was: "
                                + eventModelActual.getLink("target").get().getHref()),
                () -> assertEquals(pathToTargetLink, eventModelActual.getTarget().getLink("self").get().getHref(),
                        () -> "should return event target model with self link: " + pathToTargetLink + ", but was: "
                                + eventModelActual.getTarget().getLink("self").get().getHref()),
                () -> assertEquals(pathToVictimLink, eventModelActual.getVictim().getLink("self").get().getHref(),
                        () -> "should return event victim model with self link: " + pathToVictimLink + ", but was: "
                                + eventModelActual.getVictim().getLink("self").get().getHref()),

                () -> assertEquals(eventNode.getId(), eventModelActual.getId(),
                        () -> "should return event model with id: " + eventNode.getSummary() + ", but was: "
                                + eventModelActual.getSummary()), () -> assertEquals(eventNode.getSummary(), eventModelActual.getSummary(),
                        () -> "should return event model with summary: " + eventNode.getSummary() + ", but was: "
                                + eventModelActual.getSummary()),
                () -> assertEquals(eventNode.getMotive(), eventModelActual.getMotive(),
                        () -> "should return event model with motive: " + eventNode.getMotive() + ", but was: "
                                + eventModelActual.getMotive()),
                () -> assertNotNull(eventModelActual.getDate(),
                        () -> "should return event model with date: " + eventNode.getDate() + ", but was null"),
                () -> assertEquals(eventNode.getIsPartOfMultipleIncidents(), eventModelActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event model which was part of multiple incidents: "
                                + eventNode.getIsPartOfMultipleIncidents() + ", but was: "
                                + eventModelActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNode.getIsSuccessful(), eventModelActual.getIsSuccessful(),
                        () -> "should return event model which was successful: " + eventNode.getIsSuccessful()
                                + ", but was: " + eventModelActual.getIsSuccessful()),
                () -> assertEquals(eventNode.getIsSuicidal(), eventModelActual.getIsSuicidal(),
                        () -> "should return event model which was suicidal: " + eventNode.getIsSuicidal() + ", but was: "
                                + eventModelActual.getIsSuicidal()),
                () -> assertNotNull(eventNode.getTarget(),
                        () -> "should return event model with not null target, but was: null"),
                () -> assertEquals(targetModel, eventModelActual.getTarget(),
                        () -> "should return event model with target model: " + targetModel + ", but was: "
                                + eventModelActual.getTarget()),
                () -> assertEquals(targetModel.getId(), eventModelActual.getTarget().getId(),
                        () -> "should return event model with target model id: " + targetModel.getId() + ", but was: "
                                + eventModelActual.getTarget().getId()),
                () -> assertEquals(targetModel.getTarget(), eventModelActual.getTarget().getTarget(),
                        () -> "should return event model with target model name: " + targetModel.getTarget() + ", but was: "
                                + eventModelActual.getTarget().getTarget()),
                () -> assertNull(eventNode.getCity(),
                        () -> "should return event model with null city, but was: " + eventNode.getCity()),

                () -> assertNotNull(eventNode.getVictim(),
                        () -> "should return event model with not null victim, but was: null"),
                () -> assertEquals(victimModel.getId(), eventModelActual.getVictim().getId(),
                        () -> "should return event model with victim model id: " + victimModel.getId() + ", but was: "
                                + eventModelActual.getVictim().getId()),
                () -> assertEquals(victimModel.getTotalNumberOfFatalities(), eventModelActual.getVictim().getTotalNumberOfFatalities(),
                        () -> "should return event model with victim total number of fatalities: "
                                + victimModel.getTotalNumberOfFatalities() + ", but was: "
                                + eventModelActual.getVictim().getTotalNumberOfFatalities()),
                () -> assertEquals(victimModel.getNumberOfPerpetratorsFatalities(),
                        eventModelActual.getVictim().getNumberOfPerpetratorsFatalities(),
                        () -> "should return event model with victim number of perpetrators fatalities: "
                                + victimModel.getNumberOfPerpetratorsFatalities() + ", but was: "
                                + eventModelActual.getVictim().getNumberOfPerpetratorsFatalities()),
                () -> assertEquals(victimModel.getTotalNumberOfInjured(), eventModelActual.getVictim().getTotalNumberOfInjured(),
                        () -> "should return event model with victim total number of injured: "
                                + victimModel.getTotalNumberOfInjured() + ", but was: "
                                + eventModelActual.getVictim().getTotalNumberOfInjured()),
                () -> assertEquals(victimModel.getNumberOfPerpetratorsInjured(),
                        eventModelActual.getVictim().getNumberOfPerpetratorsInjured(),
                        () -> "should return event model with victim number of perpetrators injured: "
                                + victimModel.getNumberOfPerpetratorsInjured() + ", but was: "
                                + eventModelActual.getVictim().getNumberOfPerpetratorsInjured()),
                () -> assertEquals(victimModel.getValueOfPropertyDamage(),
                        eventModelActual.getVictim().getValueOfPropertyDamage(),
                        () -> "should return event model with victim value of property damage: "
                                + victimModel.getValueOfPropertyDamage() + ", but was: "
                                + eventModelActual.getVictim().getValueOfPropertyDamage()),

                () -> assertFalse(eventModelActual.getTarget().getLinks().isEmpty(),
                        () -> "should return event model with target model with links, but wasn't"),
                () -> assertFalse(eventModelActual.getVictim().getLinks().isEmpty(),
                        () -> "should return event model with victim model with links, but wasn't"),
                () -> assertNotNull(eventModelActual.getLinks(), () -> "should return model with links, but was: " 
                        + eventModelActual),
                () -> assertFalse(eventModelActual.getLinks().isEmpty(),
                        () -> "should return model with links, but was: " + eventModelActual),
                () -> verify(modelMapper, times(1)).map(eventNode, EventModel.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(targetModelAssembler, times(1)).toModel(eventNode.getTarget()),
                () -> verifyNoMoreInteractions(targetModelAssembler),
                () -> verify(victimModelAssembler, times(1)).toModel(eventNode.getVictim()),
                () -> verifyNoMoreInteractions(victimModelAssembler),
                () -> verifyNoInteractions(cityModelAssembler));
    }

    @Test
    void when_map_event_node_without_victim_to_model_should_return_event_model_without_victim() {

        Long targetId = 1L;
        Long cityId = 2L;
        Long eventId = 4L;
        TargetNode targetNode = (TargetNode) targetBuilder.withId(targetId).build(ObjectType.NODE);
        CityNode cityNode = (CityNode) cityBuilder.withId(cityId).build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withId(eventId).withTarget(targetNode).withCity(cityNode)
                .build(ObjectType.NODE);
        TargetModel targetModel = (TargetModel) targetBuilder.withId(targetId).build(ObjectType.MODEL);
        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetId.intValue();
        Link targetLink = new Link(pathToTargetLink);
        targetModel.add(targetLink);
        CityModel cityModel = (CityModel) cityBuilder.withId(cityId).build(ObjectType.MODEL);
        String pathToCityLink = CITY_BASE_PATH + "/" + targetId.intValue();
        Link cityLink = new Link(pathToCityLink);
        cityModel.add(cityLink);
        EventModel eventModel = (EventModel) eventBuilder.withId(eventId).withTarget(targetModel).withCity(cityModel)
                .build(ObjectType.MODEL);
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
        String pathToEventTargetLink = EVENT_BASE_PATH + "/" + eventId.intValue() + "/targets";

        when(modelMapper.map(eventNode, EventModel.class)).thenReturn(eventModel);
        when(targetModelAssembler.toModel(eventNode.getTarget())).thenReturn(targetModel);
        when(cityModelAssembler.toModel(eventNode.getCity())).thenReturn(cityModel);

        EventModel eventModelActual = modelAssembler.toModel(eventNode);

        assertAll(
                () -> assertEquals(pathToEventLink, eventModelActual.getLink("self").get().getHref(),
                        () -> "should return event model with self link: " + pathToEventLink + ", but was: "
                                + eventModelActual.getLink("self").get().getHref()),
                () -> assertEquals(pathToEventTargetLink, eventModelActual.getLink("target").get().getHref(),
                        () -> "should return event model with target link: " + pathToEventTargetLink + ", but was: "
                                + eventModelActual.getLink("target").get().getHref()),
                () -> assertEquals(pathToTargetLink, eventModelActual.getTarget().getLink("self").get().getHref(),
                        () -> "should return event target model with self link: " + pathToTargetLink + ", but was: "
                                + eventModelActual.getTarget().getLink("self").get().getHref()),
                () -> assertEquals(pathToCityLink, eventModelActual.getCity().getLink("self").get().getHref(),
                        () -> "should return event city model with self link: " + pathToCityLink + ", but was: "
                                + eventModelActual.getCity().getLink("self").get().getHref()),

                () -> assertEquals(eventNode.getId(), eventModelActual.getId(),
                        () -> "should return event model with id: " + eventNode.getSummary() + ", but was: "
                                + eventModelActual.getSummary()), () -> assertEquals(eventNode.getSummary(), eventModelActual.getSummary(),
                        () -> "should return event model with summary: " + eventNode.getSummary() + ", but was: "
                                + eventModelActual.getSummary()),
                () -> assertEquals(eventNode.getMotive(), eventModelActual.getMotive(),
                        () -> "should return event model with motive: " + eventNode.getMotive() + ", but was: "
                                + eventModelActual.getMotive()),
                () -> assertNotNull(eventModelActual.getDate(),
                        () -> "should return event model with date: " + eventNode.getDate() + ", but was null"),
                () -> assertEquals(eventNode.getIsPartOfMultipleIncidents(), eventModelActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event model which was part of multiple incidents: "
                                + eventNode.getIsPartOfMultipleIncidents() + ", but was: "
                                + eventModelActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNode.getIsSuccessful(), eventModelActual.getIsSuccessful(),
                        () -> "should return event model which was successful: " + eventNode.getIsSuccessful()
                                + ", but was: " + eventModelActual.getIsSuccessful()),
                () -> assertEquals(eventNode.getIsSuicidal(), eventModelActual.getIsSuicidal(),
                        () -> "should return event model which was suicidal: " + eventNode.getIsSuicidal() + ", but was: "
                                + eventModelActual.getIsSuicidal()),
                () -> assertNotNull(eventNode.getTarget(),
                        () -> "should return event model with not null target, but was: null"),
                () -> assertEquals(targetModel, eventModelActual.getTarget(),
                        () -> "should return event model with target model: " + targetModel + ", but was: "
                                + eventModelActual.getTarget()),
                () -> assertEquals(targetModel.getId(), eventModelActual.getTarget().getId(),
                        () -> "should return event model with target model id: " + targetModel.getId() + ", but was: "
                                + eventModelActual.getTarget().getId()),
                () -> assertEquals(targetModel.getTarget(), eventModelActual.getTarget().getTarget(),
                        () -> "should return event model with target model name: " + targetModel.getTarget() + ", but was: "
                                + eventModelActual.getTarget().getTarget()),
                () -> assertNotNull(eventNode.getCity(),
                        () -> "should return event model with not null city, but was: null"),
                () -> assertEquals(cityModel.getId(), eventModelActual.getCity().getId(),
                        () -> "should return event model with city model id: " + cityModel.getId() + ", but was: "
                                + eventModelActual.getCity().getId()),
                () -> assertEquals(cityModel.getName(), eventModelActual.getCity().getName(),
                        () -> "should return event model with city name: " + cityModel.getName() + ", but was: "
                                + eventModelActual.getCity().getName()),
                () -> assertEquals(cityModel.getLatitude(), eventModelActual.getCity().getLatitude(),
                        () -> "should return event model with city latitude: " + cityModel.getLatitude() + ", but was: "
                                + eventModelActual.getCity().getLatitude()),
                () -> assertEquals(cityModel.getLongitude(), eventModelActual.getCity().getLongitude(),
                        () -> "should return event model with city longitude: " + cityModel.getLongitude() + ", but was: "
                                + eventModelActual.getCity().getLongitude()),

                () -> assertNull(eventNode.getVictim(),
                        () -> "should return event model with null victim, but was: " + eventNode.getVictim()),

                () -> assertFalse(eventModelActual.getTarget().getLinks().isEmpty(),
                        () -> "should return event model with target model with links, but wasn't"),
                () -> assertFalse(eventModelActual.getCity().getLinks().isEmpty(),
                        () -> "should return event model with city model with links, but wasn't"),
                () -> assertNotNull(eventModelActual.getLinks(), () -> "should return model with links, but was: " + eventModelActual),
                () -> assertFalse(eventModelActual.getLinks().isEmpty(),
                        () -> "should return model with links, but was: " + eventModelActual),
                () -> verify(modelMapper, times(1)).map(eventNode, EventModel.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(targetModelAssembler, times(1)).toModel(eventNode.getTarget()),
                () -> verifyNoMoreInteractions(targetModelAssembler),
                () -> verify(cityModelAssembler, times(1)).toModel(eventNode.getCity()),
                () -> verifyNoMoreInteractions(cityModelAssembler),
                () -> verifyNoInteractions(victimModelAssembler));
    }
}
