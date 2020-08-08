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

    private final String BASE_PATH = "http://localhost:8080/api/events";

    private EventModelAssembler modelAssembler;

    @Mock
    private TargetModelAssembler targetModelAssembler;

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

        modelAssembler = new EventModelAssembler(targetModelAssembler, objectMapper);
    }

    @Test
    void when_map_event_node_to_model_should_return_event_model() {

        Long targetId = 1L;
        TargetNode targetNode = (TargetNode) targetBuilder.build(ObjectType.NODE);
        CityNode cityNode = (CityNode) cityBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).withCity(cityNode).build(ObjectType.NODE);
        TargetModel targetModel = (TargetModel) targetBuilder.build(ObjectType.MODEL);
        CityModel cityModel = (CityModel) cityBuilder.build(ObjectType.MODEL);
        EventModel eventModel = (EventModel) eventBuilder.withTarget(targetModel).withCity(cityModel).build(ObjectType.MODEL);
        String pathToLink = BASE_PATH + targetId.intValue();
        Link link = new Link(pathToLink);
        targetModel.add(link);

        when(objectMapper.map(eventNode, EventModel.class)).thenReturn(eventModel);
        when(targetModelAssembler.toModel(eventNode.getTarget())).thenReturn(targetModel);

        EventModel model = modelAssembler.toModel(eventNode);

        assertAll(
                () -> assertNotNull(model.getId(),
                        () -> "should return event model with new id, but was: " + model.getId()),
                () -> assertEquals(eventNode.getSummary(), model.getSummary(),
                        () -> "should return event model with summary: " + eventNode.getSummary() + ", but was: "
                                + model.getSummary()),
                () -> assertEquals(eventNode.getMotive(), model.getMotive(),
                        () -> "should return event model with motive: " + eventNode.getMotive() + ", but was: "
                                + model.getMotive()),
                () -> assertNotNull(model.getDate(),
                        () -> "should return event model with date: " + eventNode.getDate() + ", but was null"),
                () -> assertEquals(eventNode.getIsPartOfMultipleIncidents(), model.getIsPartOfMultipleIncidents(),
                        () -> "should return event model which was part of multiple incidents: "
                                + eventNode.getIsPartOfMultipleIncidents() + ", but was: "
                                + model.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNode.getIsSuccessful(), model.getIsSuccessful(),
                        () -> "should return event model which was successful: " + eventNode.getIsSuccessful()
                                + ", but was: " + model.getIsSuccessful()),
                () -> assertEquals(eventNode.getIsSuicidal(), model.getIsSuicidal(),
                        () -> "should return event model which was suicidal: " + eventNode.getIsSuicidal() + ", but was: "
                                + model.getIsSuicidal()),
                () -> assertNotNull(eventNode.getTarget(),
                        () -> "should return event model with not null target, but was: null"),
                () -> assertEquals(targetModel, model.getTarget(),
                        () -> "should return event model with target model: " + targetModel + ", but was: "
                                + model.getTarget()),
                () -> assertEquals(targetModel.getId(), model.getTarget().getId(),
                        () -> "should return event model with target model id: " + targetModel.getId() + ", but was: "
                                + model.getTarget().getId()),
                () -> assertEquals(targetModel.getTarget(), model.getTarget().getTarget(),
                        () -> "should return event model with target model name: " + targetModel.getTarget() + ", but was: "
                                + model.getTarget().getTarget()),
                () -> assertNotNull(eventNode.getCity(),
                        () -> "should return event model with not null city, but was: null"),
                () -> assertEquals(cityModel.getId(), model.getCity().getId(),
                        () -> "should return event model with city model id: " + cityModel.getId() + ", but was: "
                                + model.getCity().getId()),
                () -> assertEquals(cityModel.getName(), model.getCity().getName(),
                        () -> "should return event model with target city name: " + cityModel.getName() + ", but was: "
                                + model.getCity().getName()),
                () -> assertEquals(cityModel.getLatitude(), model.getCity().getLatitude(),
                        () -> "should return event model with target city latitude: " + cityModel.getLatitude() + ", but was: "
                                + model.getCity().getLatitude()),
                () -> assertEquals(cityModel.getLongitude(), model.getCity().getLongitude(),
                        () -> "should return event model with target city longitude: " + cityModel.getLongitude() + ", but was: "
                                + model.getCity().getLongitude()),
                () -> assertTrue(model.getCity().getLinks().isEmpty(),
                        () -> "should return event model with city model with links, but wasn't"),
                () -> assertFalse(model.getTarget().getLinks().isEmpty(),
                        () -> "should return event model with target model with links, but wasn't"),
                () -> assertNotNull(model.getLinks(), () -> "should return model with links, but was: " + model),
                () -> assertFalse(model.getLinks().isEmpty(),
                        () -> "should return model with links, but was: " + model),
                () -> verify(objectMapper, times(1)).map(eventNode, EventModel.class),
                () -> verifyNoMoreInteractions(objectMapper),
                () -> verify(targetModelAssembler, times(1)).toModel(eventNode.getTarget()),
                () -> verifyNoMoreInteractions(targetModelAssembler));
    }

    @Test
    void when_map_event_node_to_model_without_target_should_return_event_model_without_target() {

        CityNode cityNode = (CityNode) cityBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withCity(cityNode).build(ObjectType.NODE);

        CityModel cityModel = (CityModel) cityBuilder.build(ObjectType.MODEL);
        EventModel eventModel = (EventModel) eventBuilder.withCity(cityModel).build(ObjectType.MODEL);

        when(objectMapper.map(eventNode, EventModel.class)).thenReturn(eventModel);

        EventModel model = modelAssembler.toModel(eventNode);

        assertAll(
                () -> assertNotNull(model.getId(),
                        () -> "should return event model with new id, but was: " + model.getId()),
                () -> assertEquals(eventNode.getSummary(), model.getSummary(),
                        () -> "should return event model with summary: " + eventNode.getSummary() + ", but was: "
                                + model.getSummary()),
                () -> assertEquals(eventNode.getMotive(), model.getMotive(),
                        () -> "should return event model with motive: " + eventNode.getMotive() + ", but was: "
                                + model.getMotive()),
                () -> assertNotNull(model.getDate(),
                        () -> "should return event model with date: " + eventNode.getDate() + ", but was null"),
                () -> assertEquals(eventNode.getIsPartOfMultipleIncidents(), model.getIsPartOfMultipleIncidents(),
                        () -> "should return event model which was part of multiple incidents: "
                                + eventNode.getIsPartOfMultipleIncidents() + ", but was: "
                                + model.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNode.getIsSuccessful(), model.getIsSuccessful(),
                        () -> "should return event model which was successful: " + eventNode.getIsSuccessful()
                                + ", but was: " + model.getIsSuccessful()),
                () -> assertEquals(eventNode.getIsSuicidal(), model.getIsSuicidal(),
                        () -> "should return event model which was suicidal: " + eventNode.getIsSuicidal() + ", but was: "
                                + model.getIsSuicidal()),
                () -> assertNull(eventNode.getTarget(),
                        () -> "should return event model with null target, but wasn't: null"),
                () -> assertNotNull(eventNode.getCity(),
                        () -> "should return event model with not null city, but was: null"),
                () -> assertEquals(cityModel.getId(), model.getCity().getId(),
                        () -> "should return event model with city model id: " + cityModel.getId() + ", but was: "
                                + model.getCity().getId()),
                () -> assertEquals(cityModel.getName(), model.getCity().getName(),
                        () -> "should return event model with target city name: " + cityModel.getName() + ", but was: "
                                + model.getCity().getName()),
                () -> assertEquals(cityModel.getLatitude(), model.getCity().getLatitude(),
                        () -> "should return event model with target city latitude: " + cityModel.getLatitude() + ", but was: "
                                + model.getCity().getLatitude()),
                () -> assertEquals(cityModel.getLongitude(), model.getCity().getLongitude(),
                        () -> "should return event model with target city longitude: " + cityModel.getLongitude() + ", but was: "
                                + model.getCity().getLongitude()),
                () -> assertTrue(model.getCity().getLinks().isEmpty(),
                        () -> "should return event model with city model with links, but wasn't"),
                () -> assertNotNull(model.getLinks(), () -> "should return model with links, but was: " + model),
                () -> assertFalse(model.getLinks().isEmpty(),
                        () -> "should return model with links, but was: " + model),
                () -> verify(objectMapper, times(1)).map(eventNode, EventModel.class),
                () -> verifyNoMoreInteractions(objectMapper),
                () -> verifyNoInteractions(targetModelAssembler));
    }
}
