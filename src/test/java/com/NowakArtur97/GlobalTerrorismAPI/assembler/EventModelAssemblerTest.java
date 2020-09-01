package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.CityModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.CityNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.CityBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.Link;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventModelAssembler_Tests")
class EventModelAssemblerTest {

    private final String TARGET_BASE_PATH = "http://localhost/api/v1/targets";
    private final String CITY_BASE_PATH = "http://localhost/api/v1/cities";
    private final String EVENT_BASE_PATH = "http://localhost/api/v1/events";

    private EventModelAssembler modelAssembler;

    @Mock
    private TargetModelAssembler targetModelAssembler;

    @Mock
    private CityModelAssembler cityModelAssembler;

    @Mock
    private ObjectMapper objectMapper;

    private static TargetBuilder targetBuilder;
    private static CityBuilder cityBuilder;
    private static EventBuilder eventBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        targetBuilder = new TargetBuilder();
        cityBuilder = new CityBuilder();
        eventBuilder = new EventBuilder();
    }

    @BeforeEach
    private void setUp() {

        modelAssembler = new EventModelAssembler(targetModelAssembler, cityModelAssembler, objectMapper);
    }

    @Test
    void when_map_event_node_to_model_should_return_event_model() {

        Long targetId = 1L;
        Long cityId = 2L;
        Long eventId = 3L;
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

        when(objectMapper.map(eventNode, EventModel.class)).thenReturn(eventModel);
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
                        () -> "should return event model with target city name: " + cityModel.getName() + ", but was: "
                                + eventModelActual.getCity().getName()),
                () -> assertEquals(cityModel.getLatitude(), eventModelActual.getCity().getLatitude(),
                        () -> "should return event model with target city latitude: " + cityModel.getLatitude() + ", but was: "
                                + eventModelActual.getCity().getLatitude()),
                () -> assertEquals(cityModel.getLongitude(), eventModelActual.getCity().getLongitude(),
                        () -> "should return event model with target city longitude: " + cityModel.getLongitude() + ", but was: "
                                + eventModelActual.getCity().getLongitude()),

                () -> assertFalse(eventModelActual.getTarget().getLinks().isEmpty(),
                        () -> "should return event model with target model with links, but wasn't"),
                () -> assertFalse(eventModelActual.getCity().getLinks().isEmpty(),
                        () -> "should return event model with city model with links, but wasn't"),
                () -> assertNotNull(eventModelActual.getLinks(), () -> "should return model with links, but was: " + eventModelActual),
                () -> assertFalse(eventModelActual.getLinks().isEmpty(),
                        () -> "should return model with links, but was: " + eventModelActual),
                () -> verify(objectMapper, times(1)).map(eventNode, EventModel.class),
                () -> verifyNoMoreInteractions(objectMapper),
                () -> verify(targetModelAssembler, times(1)).toModel(eventNode.getTarget()),
                () -> verifyNoMoreInteractions(targetModelAssembler),
                () -> verify(cityModelAssembler, times(1)).toModel(eventNode.getCity()),
                () -> verifyNoMoreInteractions(cityModelAssembler));
    }

    @Test
    void when_map_event_node_to_model_without_target_should_return_event_model_without_target() {

        Long cityId = 1L;
        Long eventId = 2L;
        CityNode cityNode = (CityNode) cityBuilder.withId(cityId).build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withId(eventId).withCity(cityNode).build(ObjectType.NODE);
        CityModel cityModel = (CityModel) cityBuilder.withId(cityId).build(ObjectType.MODEL);
        String pathToCityLink = CITY_BASE_PATH + "/" + cityId.intValue();
        Link cityLink = new Link(pathToCityLink);
        cityModel.add(cityLink);
        EventModel eventModel = (EventModel) eventBuilder.withId(eventId).withCity(cityModel).build(ObjectType.MODEL);
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();

        when(objectMapper.map(eventNode, EventModel.class)).thenReturn(eventModel);
        when(cityModelAssembler.toModel(eventNode.getCity())).thenReturn(cityModel);

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

                () -> assertFalse(eventModelActual.getCity().getLinks().isEmpty(),
                        () -> "should return event model with city model with links, but wasn't"),
                () -> assertNotNull(eventModelActual.getLinks(), () -> "should return model with links, but was: " + eventModelActual),
                () -> assertFalse(eventModelActual.getLinks().isEmpty(),
                        () -> "should return model with links, but was: " + eventModelActual),
                () -> verify(objectMapper, times(1)).map(eventNode, EventModel.class),
                () -> verifyNoMoreInteractions(objectMapper),
                () -> verify(cityModelAssembler, times(1)).toModel(eventNode.getCity()),
                () -> verifyNoMoreInteractions(cityModelAssembler),
                () -> verifyNoInteractions(targetModelAssembler));
    }

    @Test
    void when_map_event_node_without_city_to_model_should_return_event_model_without_city() {

        Long targetId = 1L;
        Long eventId = 2L;
        TargetNode targetNode = (TargetNode) targetBuilder.withId(targetId).build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withId(eventId).withTarget(targetNode).build(ObjectType.NODE);
        TargetModel targetModel = (TargetModel) targetBuilder.withId(targetId).build(ObjectType.MODEL);
        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetId.intValue();
        Link targetLink = new Link(pathToTargetLink);
        targetModel.add(targetLink);
        EventModel eventModel = (EventModel) eventBuilder.withId(eventId).withTarget(targetModel).build(ObjectType.MODEL);
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventId.intValue();
        String pathToEventTargetLink = EVENT_BASE_PATH + "/" + eventId.intValue() + "/targets";

        when(objectMapper.map(eventNode, EventModel.class)).thenReturn(eventModel);
        when(targetModelAssembler.toModel(eventNode.getTarget())).thenReturn(targetModel);

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

                () -> assertFalse(eventModelActual.getTarget().getLinks().isEmpty(),
                        () -> "should return event model with target model with links, but wasn't"),
                () -> assertNotNull(eventModelActual.getLinks(), () -> "should return model with links, but was: " + eventModelActual),
                () -> assertFalse(eventModelActual.getLinks().isEmpty(),
                        () -> "should return model with links, but was: " + eventModelActual),
                () -> verify(objectMapper, times(1)).map(eventNode, EventModel.class),
                () -> verifyNoMoreInteractions(objectMapper),
                () -> verify(targetModelAssembler, times(1)).toModel(eventNode.getTarget()),
                () -> verifyNoMoreInteractions(targetModelAssembler),
                () -> verifyNoInteractions(cityModelAssembler));
    }
}
