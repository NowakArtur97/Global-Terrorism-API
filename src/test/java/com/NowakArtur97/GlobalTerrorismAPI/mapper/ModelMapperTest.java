package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.*;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.GroupModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.*;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.*;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("ModelMapper_Tests")
class ModelMapperTest {

    private ModelMapper modelMapper;

    private static RegionBuilder regionBuilder;
    private static CountryBuilder countryBuilder;
    private static TargetBuilder targetBuilder;
    private static CityBuilder cityBuilder;
    private static EventBuilder eventBuilder;
    private static GroupBuilder groupBuilder;
    private static UserBuilder userBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        regionBuilder = new RegionBuilder();
        countryBuilder = new CountryBuilder();
        targetBuilder = new TargetBuilder();
        cityBuilder = new CityBuilder();
        eventBuilder = new EventBuilder();
        groupBuilder = new GroupBuilder();
        userBuilder = new UserBuilder();
    }

    @BeforeEach
    private void setUp() {

        modelMapper = new ModelMapper();
    }

    @Test
    void when_map_target_dto_to_node_should_return_valid_node() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        TargetDTO targetNodeExpected = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);

        TargetNode targetNodeActual = modelMapper.map(targetNodeExpected, TargetNode.class);

        assertAll(
                () -> assertNull(targetNodeActual.getId(),
                        () -> "should return target node with id as null, but was: " + targetNodeActual.getId()),
                () -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
                        () -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + targetNodeActual.getTarget()),
                () -> assertNull(targetNodeActual.getCountryOfOrigin().getId(),
                        () -> "should return target node with id as null, but was: " + targetNodeActual.getId()),
                () -> assertEquals(countryDTO.getName(), targetNodeActual.getCountryOfOrigin().getName(),
                        () -> "should return target node with country name: " + countryDTO.getName()
                                + ", but was: " + targetNodeActual.getCountryOfOrigin()));
    }

    @Test
    void when_map_target_node_to_dto_should_return_valid_dto() {

        CountryNode countryNode = (CountryNode) countryBuilder.build(ObjectType.NODE);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withCountry(countryNode).build(ObjectType.NODE);

        TargetDTO targetDTOActual = modelMapper.map(targetNodeExpected, TargetDTO.class);

        assertAll(() -> assertEquals(targetNodeExpected.getTarget(), targetDTOActual.getTarget(),
                () -> "should return target dto with target: " + targetDTOActual.getTarget() + ", but was: "
                        + targetNodeExpected.getTarget()),
                () -> assertEquals(countryNode.getName(), targetDTOActual.getCountryOfOrigin().getName(),
                        () -> "should return target dto with country name: " + countryNode.getName()
                                + ", but was: " + targetDTOActual.getCountryOfOrigin()));
    }

    @Test
    void when_map_target_node_to_model_should_return_valid_model() {

        RegionNode regionNode = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNode = (CountryNode) countryBuilder.withRegion(regionNode).build(ObjectType.NODE);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withCountry(countryNode).build(ObjectType.NODE);

        TargetModel targetModelActual = modelMapper.map(targetNodeExpected, TargetModel.class);

        assertAll(
                () -> assertEquals(targetNodeExpected.getTarget(), targetModelActual.getTarget(),
                        () -> "should return target model with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + targetModelActual.getTarget()),
                () -> assertEquals(targetNodeExpected.getTarget(), targetModelActual.getTarget(),
                        () -> "should return target model with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + targetModelActual.getTarget()),
                () -> assertEquals(countryNode.getId(), targetModelActual.getCountryOfOrigin().getId(),
                        () -> "should return target model with country id: " + countryNode.getId()
                                + ", but was: " + targetModelActual.getCountryOfOrigin().getId()),
                () -> assertEquals(countryNode.getName(), targetModelActual.getCountryOfOrigin().getName(),
                        () -> "should return target model with country name: " + countryNode.getName()
                                + ", but was: " + targetModelActual.getCountryOfOrigin().getName()),
                () -> assertEquals(regionNode.getId(), targetModelActual.getCountryOfOrigin().getRegion().getId(),
                        () -> "should return target model with region id: " + regionNode.getId()
                                + ", but was: " + targetModelActual.getCountryOfOrigin().getRegion().getId()),
                () -> assertEquals(regionNode.getName(), targetModelActual.getCountryOfOrigin().getRegion().getName(),
                        () -> "should return target model with region name: " + regionNode.getName()
                                + ", but was: " + targetModelActual.getCountryOfOrigin().getRegion()));
    }

    @Test
    void when_map_event_dto_to_node_should_return_valid_node() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.build(ObjectType.DTO);
        EventDTO eventDTOExpected = (EventDTO) eventBuilder.withId(null).withTarget(targetDTO).withCity(cityDTO)
                .build(ObjectType.DTO);

        EventNode eventNodeActual = modelMapper.map(eventDTOExpected, EventNode.class);

        assertAll(
                () -> assertNull(eventNodeActual.getId(),
                        () -> "should return event node with id as null, but was: " + eventNodeActual.getId()),
                () -> assertEquals(eventDTOExpected.getSummary(), eventNodeActual.getSummary(),
                        () -> "should return event node with summary: " + eventDTOExpected.getSummary() + ", but was: "
                                + eventNodeActual.getSummary()),
                () -> assertEquals(eventDTOExpected.getMotive(), eventNodeActual.getMotive(),
                        () -> "should return event node with motive: " + eventDTOExpected.getMotive() + ", but was: "
                                + eventNodeActual.getMotive()),
                () -> assertEquals(eventDTOExpected.getDate(), eventNodeActual.getDate(),
                        () -> "should return event node with date: " + eventDTOExpected.getDate() + ", but was: "
                                + eventNodeActual.getDate()),
                () -> assertEquals(eventDTOExpected.getIsPartOfMultipleIncidents(),
                        eventNodeActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event node which was part of multiple incidents: "
                                + eventDTOExpected.getIsPartOfMultipleIncidents() + ", but was: "
                                + eventNodeActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventDTOExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
                        () -> "should return event node which was successful: " + eventDTOExpected.getIsSuccessful()
                                + ", but was: " + eventNodeActual.getIsSuccessful()),
                () -> assertEquals(eventDTOExpected.getIsSuicidal(), eventNodeActual.getIsSuicidal(),
                        () -> "should return event node which was suicidal: " + eventDTOExpected.getIsSuicidal() + ", but was: "
                                + eventNodeActual.getIsSuicidal()),
                () -> assertNotNull(eventNodeActual.getTarget(),
                        () -> "should return event node with not null target, but was: null"),
                () -> assertNull(eventNodeActual.getTarget().getId(),
                        () -> "should return event target node with id as null, but was: "
                                + eventNodeActual.getTarget().getId()),
                () -> assertEquals(targetDTO.getTarget(), eventNodeActual.getTarget().getTarget(),
                        () -> "should return event node with target: " + targetDTO.getTarget()
                                + ", but was: " + eventNodeActual.getTarget().getTarget()),
                () -> assertNull(eventNodeActual.getTarget().getCountryOfOrigin().getId(),
                        () -> "should return event target node with id as null, but was: " + eventNodeActual.getId()),
                () -> assertEquals(countryDTO.getName(), eventNodeActual.getTarget().getCountryOfOrigin().getName(),
                        () -> "should return event target node with country name: " + countryDTO.getName()
                                + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin()),

                () -> assertNotNull(eventNodeActual.getCity(),
                        () -> "should return event node with not null city, but was: null"),
                () -> assertNull(eventNodeActual.getCity().getId(),
                        () -> "should return event city node with id as null, but was: "
                                + eventNodeActual.getCity().getId()),
                () -> assertEquals(cityDTO.getName(), eventNodeActual.getCity().getName(),
                        () -> "should return event node with city name: " + cityDTO.getName()
                                + ", but was: " + eventNodeActual.getCity().getName()),
                () -> assertEquals(cityDTO.getLatitude(), eventNodeActual.getCity().getLatitude(),
                        () -> "should return event node with city latitude: " + cityDTO.getLatitude()
                                + ", but was: " + eventNodeActual.getCity().getLatitude()),
                () -> assertEquals(cityDTO.getLongitude(), eventNodeActual.getCity().getLongitude(),
                        () -> "should return event node with city longitude: " + cityDTO.getLongitude()
                                + ", but was: " + eventNodeActual.getCity().getLongitude()));
    }

    @Test
    void when_map_event_node_to_dto_should_return_valid_dto() {

        CountryNode countryNode = (CountryNode) countryBuilder.build(ObjectType.NODE);
        TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNode).build(ObjectType.NODE);
        CityNode cityNode = (CityNode) cityBuilder.build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withTarget(targetNode).withCity(cityNode).build(ObjectType.NODE);

        EventDTO eventDTOActual = modelMapper.map(eventNodeExpected, EventDTO.class);

        assertAll(
                () -> assertEquals(eventNodeExpected.getSummary(), eventDTOActual.getSummary(),
                        () -> "should return event dto with summary: " + eventNodeExpected.getSummary() + ", but was: "
                                + eventDTOActual.getSummary()),
                () -> assertEquals(eventNodeExpected.getMotive(), eventDTOActual.getMotive(),
                        () -> "should return event dto with motive: " + eventNodeExpected.getMotive() + ", but was: "
                                + eventDTOActual.getMotive()),
                () -> assertEquals(eventNodeExpected.getDate(), eventDTOActual.getDate(),
                        () -> "should return event dto with date: " + eventNodeExpected.getDate() + ", but was: "
                                + eventDTOActual.getDate()),
                () -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
                        eventDTOActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event dto which was part of multiple incidents: "
                                + eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was: "
                                + eventDTOActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected.getIsSuccessful(), eventDTOActual.getIsSuccessful(),
                        () -> "should return event dto which was successful: " + eventNodeExpected.getIsSuccessful()
                                + ", but was: " + eventDTOActual.getIsSuccessful()),
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), eventDTOActual.getIsSuicidal(),
                        () -> "should return event dto which was suicidal: " + eventNodeExpected.getIsSuicidal() + ", but was: "
                                + eventDTOActual.getIsSuicidal()),
                () -> assertNotNull(targetNode,
                        () -> "should return event dto with not null target, but was: null"),
                () -> assertEquals(targetNode.getTarget(), eventDTOActual.getTarget().getTarget(),
                        () -> "should return event dto with target: " + targetNode.getTarget()
                                + ", but was: " + eventDTOActual.getTarget().getTarget()),
                () -> assertEquals(countryNode.getName(), eventDTOActual.getTarget().getCountryOfOrigin().getName(),
                        () -> "should return event dto with country name: " + countryNode.getName()
                                + ", but was: " + eventDTOActual.getTarget().getCountryOfOrigin()),
                () -> assertNotNull(eventDTOActual.getCity(),
                        () -> "should return event node dto not null city, but was: null"),
                () -> assertEquals(cityNode.getName(), eventDTOActual.getCity().getName(),
                        () -> "should return event dto with city name: " + cityNode.getName()
                                + ", but was: " + eventDTOActual.getCity().getName()),
                () -> assertEquals(cityNode.getLatitude(), eventDTOActual.getCity().getLatitude(),
                        () -> "should return event dto with city latitude: " + cityNode.getLatitude()
                                + ", but was: " + eventDTOActual.getCity().getLatitude()),
                () -> assertEquals(cityNode.getLongitude(), eventDTOActual.getCity().getLongitude(),
                        () -> "should return event dto with city longitude: " + cityNode.getLongitude()
                                + ", but was: " + eventDTOActual.getCity().getLongitude()));
    }

    @Test
    void when_map_event_node_to_model_should_return_valid_model() {

        CountryNode countryNode = (CountryNode) countryBuilder.build(ObjectType.NODE);
        TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNode).build(ObjectType.NODE);
        CityNode cityNode = (CityNode) cityBuilder.build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withTarget(targetNode).withCity(cityNode).build(ObjectType.NODE);

        EventModel eventModelActual = modelMapper.map(eventNodeExpected, EventModel.class);

        assertAll(
                () -> assertEquals(eventNodeExpected.getId(), eventModelActual.getId(),
                        () -> "should return event model with id: " + eventNodeExpected.getId() + ", but was: "
                                + eventModelActual.getId()),
                () -> assertEquals(eventNodeExpected.getSummary(), eventModelActual.getSummary(),
                        () -> "should return event model with summary: " + eventNodeExpected.getSummary() + ", but was: "
                                + eventModelActual.getSummary()),
                () -> assertEquals(eventNodeExpected.getMotive(), eventModelActual.getMotive(),
                        () -> "should return event model with motive: " + eventNodeExpected.getMotive() + ", but was: "
                                + eventModelActual.getMotive()),
                () -> assertEquals(eventNodeExpected.getDate(), eventModelActual.getDate(),
                        () -> "should return event model with date: " + eventNodeExpected.getDate() + ", but was: "
                                + eventModelActual.getDate()),
                () -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
                        eventModelActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event model which was part of multiple incidents: "
                                + eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was: "
                                + eventModelActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected.getIsSuccessful(), eventModelActual.getIsSuccessful(),
                        () -> "should return event model which was successful: " + eventNodeExpected.getIsSuccessful()
                                + ", but was: " + eventModelActual.getIsSuccessful()),
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), eventModelActual.getIsSuicidal(),
                        () -> "should return event model which was suicidal: " + eventNodeExpected.getIsSuicidal()
                                + ", but was: " + eventModelActual.getIsSuicidal()),
                () -> assertNotNull(eventModelActual.getTarget(),
                        () -> "should return event model with not null target, but was: null"),
                () -> assertEquals(targetNode.getId(), eventModelActual.getTarget().getId(),
                        () -> "should return event model target with id: " + targetNode.getId() + ", but was: "
                                + eventModelActual.getTarget().getId()),
                () -> assertEquals(targetNode.getTarget(), eventModelActual.getTarget().getTarget(),
                        () -> "should return event model with target: " + targetNode.getTarget()
                                + ", but was: " + eventModelActual.getTarget().getTarget()),
                () -> assertEquals(countryNode.getId(), eventModelActual.getTarget().getCountryOfOrigin().getId(),
                        () -> "should return event model with country id: " + countryNode.getId()
                                + ", but was: " + eventModelActual.getTarget().getId()),
                () -> assertEquals(countryNode.getName(), eventModelActual.getTarget().getCountryOfOrigin().getName(),
                        () -> "should return event model with country name: " + countryNode.getName()
                                + ", but was: " + eventModelActual.getTarget().getCountryOfOrigin()),

                () -> assertNotNull(eventModelActual.getCity(),
                        () -> "should return event node model not null city, but was: null"),
                () -> assertEquals(cityNode.getName(), eventModelActual.getCity().getName(),
                        () -> "should return event model with city name: " + cityNode.getName()
                                + ", but was: " + eventModelActual.getCity().getName()),
                () -> assertEquals(cityNode.getLatitude(), eventModelActual.getCity().getLatitude(),
                        () -> "should return event model with city latitude: " + cityNode.getLatitude()
                                + ", but was: " + eventModelActual.getCity().getLatitude()),
                () -> assertEquals(cityNode.getLongitude(), eventModelActual.getCity().getLongitude(),
                        () -> "should return event model with city longitude: " + cityNode.getLongitude()
                                + ", but was: " + eventModelActual.getCity().getLongitude()));
    }

    @Test
    void when_map_group_dto_to_node_should_return_valid_model() {

        String group = "group";

        String summary = "summary";
        String motive = "motive";
        boolean isPartOfMultipleIncidents = true;
        boolean isSuccessful = true;
        boolean isSuicidal = true;

        TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget("target1").build(ObjectType.DTO);

        CityDTO cityDTO = (CityDTO) cityBuilder.withName("city1").build(ObjectType.DTO);

        EventDTO eventDTO = (EventDTO) eventBuilder.withSummary(summary)
                .withMotive(motive).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetDTO).withCity(cityDTO)
                .build(ObjectType.DTO);

        isPartOfMultipleIncidents = false;
        isSuccessful = false;
        isSuicidal = false;

        TargetDTO targetDTO2 = (TargetDTO) targetBuilder.withTarget("target2").build(ObjectType.DTO);

        CityDTO cityDTO2 = (CityDTO) cityBuilder.withName("city2").build(ObjectType.DTO);

        EventDTO eventDTO2 = (EventDTO) eventBuilder.withSummary(summary + 2)
                .withMotive(motive + 2).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetDTO2).withCity(cityDTO2)
                .build(ObjectType.DTO);

        GroupDTO groupDTOExpected = (GroupDTO) groupBuilder.withName(group).withEventsCaused(List.of(eventDTO, eventDTO2))
                .build(ObjectType.DTO);

        GroupNode groupNodeActual = modelMapper.map(groupDTOExpected, GroupNode.class);

        assertAll(
                () -> assertEquals(groupDTOExpected.getName(), groupNodeActual.getName(),
                        () -> "should return group node with name: " + groupDTOExpected.getName() + ", but was: "
                                + groupNodeActual.getName()),

                () -> assertEquals(groupDTOExpected.getEventsCaused().get(0).getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(),
                        () -> "should return group's event node with summary: " + groupDTOExpected.getEventsCaused().get(0).getSummary() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(0).getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(),
                        () -> "should return group's event node with motive: " + groupDTOExpected.getEventsCaused().get(0).getMotive() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(0).getDate(), groupNodeActual.getEventsCaused().get(0).getDate(),
                        () -> "should return group's event node with date: " + groupDTOExpected.getEventsCaused().get(0).getDate() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        () -> "should return group's event node which was part of multiple incidents: "
                                + groupDTOExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(0).getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(),
                        () -> "should return group's event node which was successful: " + groupDTOExpected.getEventsCaused().get(0).getIsSuccessful()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(0).getIsSuicidal(), groupNodeActual.getEventsCaused().get(0).getIsSuicidal(),
                        () -> "should return group's event node which was suicidal: " + groupDTOExpected.getEventsCaused().get(0).getIsSuicidal()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group's event node with not null target, but was: null"),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(0).getTarget().getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget().getTarget(),
                        () -> "should return group's event node with target: " + groupDTOExpected.getEventsCaused().get(0).getTarget().getTarget()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget().getTarget()),
                () -> assertNotNull(groupNodeActual.getEventsCaused().get(1).getCity(),
                        () -> "should return event node node not null city, but was: null"),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(1).getCity().getName(), groupNodeActual.getEventsCaused().get(1).getCity().getName(),
                        () -> "should return event node with city name: " + groupDTOExpected.getEventsCaused().get(1).getCity().getName()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getCity().getName()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(1).getCity().getLatitude(), groupNodeActual.getEventsCaused().get(1).getCity().getLatitude(),
                        () -> "should return event node with city latitude: " + groupDTOExpected.getEventsCaused().get(1).getCity().getLatitude()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getCity().getLatitude()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(1).getCity().getLongitude(), groupNodeActual.getEventsCaused().get(1).getCity().getLongitude(),
                        () -> "should return event node with city longitude: " + groupDTOExpected.getEventsCaused().get(1).getCity().getLongitude()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getCity().getLongitude()),


                () -> assertEquals(groupDTOExpected.getEventsCaused().get(1).getSummary(), groupNodeActual.getEventsCaused().get(1).getSummary(),
                        () -> "should return group's event node with summary: " + groupDTOExpected.getEventsCaused().get(1).getSummary() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(1).getSummary()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(1).getMotive(), groupNodeActual.getEventsCaused().get(1).getMotive(),
                        () -> "should return group's event node with motive: " + groupDTOExpected.getEventsCaused().get(1).getMotive() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(1).getMotive()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(1).getDate(), groupNodeActual.getEventsCaused().get(1).getDate(),
                        () -> "should return group's event node with date: " + groupDTOExpected.getEventsCaused().get(1).getDate() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(1).getDate()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(1).getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents(),
                        () -> "should return group's event node which was part of multiple incidents: "
                                + groupDTOExpected.getEventsCaused().get(1).getIsPartOfMultipleIncidents() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(1).getIsSuccessful(), groupNodeActual.getEventsCaused().get(1).getIsSuccessful(),
                        () -> "should return group's event node which was successful: " + groupDTOExpected.getEventsCaused().get(1).getIsSuccessful()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuccessful()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(1).getIsSuicidal(), groupNodeActual.getEventsCaused().get(1).getIsSuicidal(),
                        () -> "should return group's event node which was suicidal: " + groupDTOExpected.getEventsCaused().get(1).getIsSuicidal()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuicidal()),
                () -> assertNotNull(groupNodeActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group's event node with not null target, but was: null"),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(1).getTarget().getTarget(), groupNodeActual.getEventsCaused().get(1).getTarget().getTarget(),
                        () -> "should return group's event node with target: " + groupDTOExpected.getEventsCaused().get(1).getTarget().getTarget()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget().getTarget()),

                () -> assertNotNull(groupNodeActual.getEventsCaused().get(1).getCity(),
                        () -> "should return event node node not null city, but was: null"),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(1).getCity().getName(), groupNodeActual.getEventsCaused().get(1).getCity().getName(),
                        () -> "should return event node with city name: " + groupDTOExpected.getEventsCaused().get(1).getCity().getName()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getCity().getName()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(1).getCity().getLatitude(), groupNodeActual.getEventsCaused().get(1).getCity().getLatitude(),
                        () -> "should return event node with city latitude: " + groupDTOExpected.getEventsCaused().get(1).getCity().getLatitude()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getCity().getLatitude()),
                () -> assertEquals(groupDTOExpected.getEventsCaused().get(1).getCity().getLongitude(), groupNodeActual.getEventsCaused().get(1).getCity().getLongitude(),
                        () -> "should return event node with city longitude: " + groupDTOExpected.getEventsCaused().get(1).getCity().getLongitude()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getCity().getLongitude()));
    }

    @Test
    void when_map_group_node_to_dto_should_return_valid_model() {

        Long targetId = 1L;
        Long cityId = 3L;
        Long eventId = 3L;
        Long groupId = 4L;

        String group = "group";

        String summary = "summary";
        String motive = "motive";
        boolean isPartOfMultipleIncidents = true;
        boolean isSuccessful = true;
        boolean isSuicidal = true;

        TargetNode targetNode1 = (TargetNode) targetBuilder.withId(targetId).withTarget("target" + targetId)
                .build(ObjectType.NODE);

        CityNode cityNode1 = (CityNode) cityBuilder.withId(cityId).withName("city" + cityId).build(ObjectType.NODE);

        EventNode eventNode1 = (EventNode) eventBuilder.withId(eventId).withSummary(summary + eventId)
                .withMotive(motive + eventId).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetNode1).withCity(cityNode1)
                .build(ObjectType.NODE);

        targetId++;
        cityId++;
        eventId++;
        isPartOfMultipleIncidents = false;
        isSuccessful = false;
        isSuicidal = false;

        TargetNode targetNode2 = (TargetNode) targetBuilder.withId(targetId).withTarget("target" + targetId)
                .build(ObjectType.NODE);

        CityNode cityNode2 = (CityNode) cityBuilder.withId(cityId).build(ObjectType.NODE);

        EventNode eventNode2 = (EventNode) eventBuilder.withId(eventId).withSummary(summary + eventId)
                .withMotive(motive + eventId).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetNode2).withCity(cityNode2)
                .build(ObjectType.NODE);

        GroupNode groupNodeExpected = (GroupNode) groupBuilder.withId(groupId).withName(group).withEventsCaused(List.of(eventNode1, eventNode2)).build(ObjectType.NODE);

        GroupDTO groupDTOActual = modelMapper.map(groupNodeExpected, GroupDTO.class);

        assertAll(
                () -> assertEquals(groupNodeExpected.getName(), groupDTOActual.getName(),
                        () -> "should return group dto with name: " + groupNodeExpected.getName() + ", but was: "
                                + groupDTOActual.getName()),

                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getSummary(), groupDTOActual.getEventsCaused().get(0).getSummary(),
                        () -> "should return group's event dto with summary: " + groupNodeExpected.getEventsCaused().get(0).getSummary() + ", but was: "
                                + groupDTOActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getMotive(), groupDTOActual.getEventsCaused().get(0).getMotive(),
                        () -> "should return group's event dto with motive: " + groupNodeExpected.getEventsCaused().get(0).getMotive() + ", but was: "
                                + groupDTOActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getDate(), groupDTOActual.getEventsCaused().get(0).getDate(),
                        () -> "should return group's event dto with date: " + groupNodeExpected.getEventsCaused().get(0).getDate() + ", but was: "
                                + groupDTOActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        groupDTOActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        () -> "should return group's event dto which was part of multiple incidents: "
                                + groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents() + ", but was: "
                                + groupDTOActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuccessful(), groupDTOActual.getEventsCaused().get(0).getIsSuccessful(),
                        () -> "should return group's event dto which was successful: " + groupNodeExpected.getEventsCaused().get(0).getIsSuccessful()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuicidal(), groupDTOActual.getEventsCaused().get(0).getIsSuicidal(),
                        () -> "should return group's event dto which was suicidal: " + groupNodeExpected.getEventsCaused().get(0).getIsSuicidal()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupDTOActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group's event dto with not null target, but was: null"),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getTarget().getTarget(), groupDTOActual.getEventsCaused().get(0).getTarget().getTarget(),
                        () -> "should return group's event dto with target: " + groupNodeExpected.getEventsCaused().get(0).getTarget().getTarget()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(1).getTarget().getTarget()),
                () -> assertNotNull(groupDTOActual.getEventsCaused().get(1).getCity(),
                        () -> "should return event node dto not null city, but was: null"),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getCity().getName(), groupDTOActual.getEventsCaused().get(1).getCity().getName(),
                        () -> "should return event dto with city name: " + groupNodeExpected.getEventsCaused().get(1).getCity().getName()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(1).getCity().getName()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getCity().getLatitude(), groupDTOActual.getEventsCaused().get(1).getCity().getLatitude(),
                        () -> "should return event dto with city latitude: " + groupNodeExpected.getEventsCaused().get(1).getCity().getLatitude()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(1).getCity().getLatitude()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getCity().getLongitude(), groupDTOActual.getEventsCaused().get(1).getCity().getLongitude(),
                        () -> "should return event dto with city longitude: " + groupNodeExpected.getEventsCaused().get(1).getCity().getLongitude()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(1).getCity().getLongitude()),


                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getSummary(), groupDTOActual.getEventsCaused().get(1).getSummary(),
                        () -> "should return group's event dto with summary: " + groupNodeExpected.getEventsCaused().get(1).getSummary() + ", but was: "
                                + groupDTOActual.getEventsCaused().get(1).getSummary()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getMotive(), groupDTOActual.getEventsCaused().get(1).getMotive(),
                        () -> "should return group's event dto with motive: " + groupNodeExpected.getEventsCaused().get(1).getMotive() + ", but was: "
                                + groupDTOActual.getEventsCaused().get(1).getMotive()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getDate(), groupDTOActual.getEventsCaused().get(1).getDate(),
                        () -> "should return group's event dto with date: " + groupNodeExpected.getEventsCaused().get(1).getDate() + ", but was: "
                                + groupDTOActual.getEventsCaused().get(1).getDate()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getIsPartOfMultipleIncidents(),
                        groupDTOActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents(),
                        () -> "should return group's event dto which was part of multiple incidents: "
                                + groupNodeExpected.getEventsCaused().get(1).getIsPartOfMultipleIncidents() + ", but was: "
                                + groupDTOActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getIsSuccessful(), groupDTOActual.getEventsCaused().get(1).getIsSuccessful(),
                        () -> "should return group's event dto which was successful: " + groupNodeExpected.getEventsCaused().get(1).getIsSuccessful()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(1).getIsSuccessful()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getIsSuicidal(), groupDTOActual.getEventsCaused().get(1).getIsSuicidal(),
                        () -> "should return group's event dto which was suicidal: " + groupNodeExpected.getEventsCaused().get(1).getIsSuicidal()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(1).getIsSuicidal()),
                () -> assertNotNull(groupDTOActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group's event dto with not null target, but was: null"),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getTarget().getTarget(), groupDTOActual.getEventsCaused().get(1).getTarget().getTarget(),
                        () -> "should return group's event dto with target: " + groupNodeExpected.getEventsCaused().get(1).getTarget().getTarget()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(1).getTarget().getTarget()),

                () -> assertNotNull(groupDTOActual.getEventsCaused().get(1).getCity(),
                        () -> "should return event node dto not null city, but was: null"),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getCity().getName(), groupDTOActual.getEventsCaused().get(1).getCity().getName(),
                        () -> "should return event dto with city name: " + groupNodeExpected.getEventsCaused().get(1).getCity().getName()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(1).getCity().getName()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getCity().getLatitude(), groupDTOActual.getEventsCaused().get(1).getCity().getLatitude(),
                        () -> "should return event dto with city latitude: " + groupNodeExpected.getEventsCaused().get(1).getCity().getLatitude()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(1).getCity().getLatitude()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getCity().getLongitude(), groupDTOActual.getEventsCaused().get(1).getCity().getLongitude(),
                        () -> "should return event dto with city longitude: " + groupNodeExpected.getEventsCaused().get(1).getCity().getLongitude()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(1).getCity().getLongitude()));
    }

    @Test
    void when_map_group_node_to_model_should_return_valid_model() {

        Long targetId = 1L;
        Long cityId = 3L;
        Long eventId = 3L;
        Long groupId = 4L;

        String group = "group";

        String summary = "summary";
        String motive = "motive";
        boolean isPartOfMultipleIncidents = true;
        boolean isSuccessful = true;
        boolean isSuicidal = true;

        TargetNode targetNode1 = (TargetNode) targetBuilder.withId(targetId).withTarget("target" + targetId)
                .build(ObjectType.NODE);

        CityNode cityNode1 = (CityNode) cityBuilder.withId(cityId).withName("city" + cityId).build(ObjectType.NODE);

        EventNode eventNode1 = (EventNode) eventBuilder.withId(eventId).withSummary(summary + eventId)
                .withMotive(motive + eventId).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetNode1).withCity(cityNode1)
                .build(ObjectType.NODE);

        targetId++;
        cityId++;
        eventId++;
        isPartOfMultipleIncidents = false;
        isSuccessful = false;
        isSuicidal = false;

        TargetNode targetNode2 = (TargetNode) targetBuilder.withId(targetId).withTarget("target" + targetId)
                .build(ObjectType.NODE);

        CityNode cityNode2 = (CityNode) cityBuilder.withId(cityId).build(ObjectType.NODE);

        EventNode eventNode2 = (EventNode) eventBuilder.withId(eventId).withSummary(summary + eventId)
                .withMotive(motive + eventId).withIsPartOfMultipleIncidents(isPartOfMultipleIncidents)
                .withIsSuccessful(isSuccessful).withIsSuicidal(isSuicidal).withTarget(targetNode2).withCity(cityNode2)
                .build(ObjectType.NODE);

        GroupNode groupNodeExpected = (GroupNode) groupBuilder.withId(groupId).withName(group).withEventsCaused(List.of(eventNode1, eventNode2)).build(ObjectType.NODE);

        GroupModel groupModelActual = modelMapper.map(groupNodeExpected, GroupModel.class);

        assertAll(
                () -> assertEquals(groupNodeExpected.getId(), groupModelActual.getId(),
                        () -> "should return group model with id: " + groupNodeExpected.getId() + ", but was: "
                                + groupModelActual.getId()),
                () -> assertEquals(groupNodeExpected.getName(), groupModelActual.getName(),
                        () -> "should return group model with name: " + groupNodeExpected.getName() + ", but was: "
                                + groupModelActual.getName()),

                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getSummary(), groupModelActual.getEventsCaused().get(0).getSummary(),
                        () -> "should return group's event model with summary: " + groupNodeExpected.getEventsCaused().get(0).getSummary() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getMotive(), groupModelActual.getEventsCaused().get(0).getMotive(),
                        () -> "should return group's event model with motive: " + groupNodeExpected.getEventsCaused().get(0).getMotive() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getDate(), groupModelActual.getEventsCaused().get(0).getDate(),
                        () -> "should return group's event model with date: " + groupNodeExpected.getEventsCaused().get(0).getDate() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        groupModelActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        () -> "should return group's event model which was part of multiple incidents: "
                                + groupNodeExpected.getEventsCaused().get(0).getIsPartOfMultipleIncidents() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuccessful(), groupModelActual.getEventsCaused().get(0).getIsSuccessful(),
                        () -> "should return group's event model which was successful: " + groupNodeExpected.getEventsCaused().get(0).getIsSuccessful()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getIsSuicidal(), groupModelActual.getEventsCaused().get(0).getIsSuicidal(),
                        () -> "should return group's event model which was suicidal: " + groupNodeExpected.getEventsCaused().get(0).getIsSuicidal()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupModelActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group's event model with not null target, but was: null"),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getTarget().getId(), groupModelActual.getEventsCaused().get(0).getTarget().getId(),
                        () -> "should return group's event model target with id: " + groupNodeExpected.getEventsCaused().get(0).getTarget().getId() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getTarget().getId()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(0).getTarget().getTarget(), groupModelActual.getEventsCaused().get(0).getTarget().getTarget(),
                        () -> "should return group's event model with target: " + groupNodeExpected.getEventsCaused().get(0).getTarget().getTarget()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getTarget().getTarget()),
                () -> assertNotNull(groupModelActual.getEventsCaused().get(1).getCity(),
                        () -> "should return event node model not null city, but was: null"),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getCity().getName(), groupModelActual.getEventsCaused().get(1).getCity().getName(),
                        () -> "should return event model with city name: " + groupNodeExpected.getEventsCaused().get(1).getCity().getName()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getCity().getName()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getCity().getLatitude(), groupModelActual.getEventsCaused().get(1).getCity().getLatitude(),
                        () -> "should return event model with city latitude: " + groupNodeExpected.getEventsCaused().get(1).getCity().getLatitude()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getCity().getLatitude()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getCity().getLongitude(), groupModelActual.getEventsCaused().get(1).getCity().getLongitude(),
                        () -> "should return event model with city longitude: " + groupNodeExpected.getEventsCaused().get(1).getCity().getLongitude()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getCity().getLongitude()),


                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getSummary(), groupModelActual.getEventsCaused().get(1).getSummary(),
                        () -> "should return group's event model with summary: " + groupNodeExpected.getEventsCaused().get(1).getSummary() + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getSummary()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getMotive(), groupModelActual.getEventsCaused().get(1).getMotive(),
                        () -> "should return group's event model with motive: " + groupNodeExpected.getEventsCaused().get(1).getMotive() + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getMotive()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getDate(), groupModelActual.getEventsCaused().get(1).getDate(),
                        () -> "should return group's event model with date: " + groupNodeExpected.getEventsCaused().get(1).getDate() + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getDate()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getIsPartOfMultipleIncidents(),
                        groupModelActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents(),
                        () -> "should return group's event model which was part of multiple incidents: "
                                + groupNodeExpected.getEventsCaused().get(1).getIsPartOfMultipleIncidents() + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getIsSuccessful(), groupModelActual.getEventsCaused().get(1).getIsSuccessful(),
                        () -> "should return group's event model which was successful: " + groupNodeExpected.getEventsCaused().get(1).getIsSuccessful()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getIsSuccessful()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getIsSuicidal(), groupModelActual.getEventsCaused().get(1).getIsSuicidal(),
                        () -> "should return group's event model which was suicidal: " + groupNodeExpected.getEventsCaused().get(1).getIsSuicidal()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getIsSuicidal()),
                () -> assertNotNull(groupModelActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group's event model with not null target, but was: null"),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getTarget().getId(), groupModelActual.getEventsCaused().get(1).getTarget().getId(),
                        () -> "should return group's event model target with id: " + groupNodeExpected.getEventsCaused().get(1).getTarget().getId() + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getTarget().getId()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getTarget().getTarget(), groupModelActual.getEventsCaused().get(1).getTarget().getTarget(),
                        () -> "should return group's event model with target: " + groupNodeExpected.getEventsCaused().get(1).getTarget().getTarget()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getTarget().getTarget()),

                () -> assertNotNull(groupModelActual.getEventsCaused().get(1).getCity(),
                        () -> "should return event node model not null city, but was: null"),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getCity().getName(), groupModelActual.getEventsCaused().get(1).getCity().getName(),
                        () -> "should return event model with city name: " + groupNodeExpected.getEventsCaused().get(1).getCity().getName()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getCity().getName()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getCity().getLatitude(), groupModelActual.getEventsCaused().get(1).getCity().getLatitude(),
                        () -> "should return event model with city latitude: " + groupNodeExpected.getEventsCaused().get(1).getCity().getLatitude()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getCity().getLatitude()),
                () -> assertEquals(groupNodeExpected.getEventsCaused().get(1).getCity().getLongitude(), groupModelActual.getEventsCaused().get(1).getCity().getLongitude(),
                        () -> "should return event model with city longitude: " + groupNodeExpected.getEventsCaused().get(1).getCity().getLongitude()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getCity().getLongitude()));
    }

    @Test
    void when_map_user_dto_to_node_should_return_valid_node() {

        UserDTO userDTO = (UserDTO) userBuilder.build(ObjectType.DTO);
        UserNode userNodeExpected = (UserNode) userBuilder.withId(null).withRoles(null).build(ObjectType.NODE);

        UserNode userNodeActual = modelMapper.map(userDTO, UserNode.class);

        assertAll(
                () -> assertEquals(userNodeExpected.getUserName(), userNodeActual.getUserName(),
                        () -> "should return user with user name: " + userNodeExpected.getUserName() + ", but was: " + userNodeActual.getUserName()),
                () -> assertEquals(userNodeExpected.getPassword(), userNodeActual.getPassword(),
                        () -> "should return user with user password: " + userNodeExpected.getPassword() + ", but was: " + userNodeActual.getPassword()),
                () -> assertEquals(userNodeExpected.getEmail(), userNodeActual.getEmail(),
                        () -> "should return user with user email: " + userNodeExpected.getEmail() + ", but was: " + userNodeActual.getEmail()),
                () -> assertNull(userNodeActual.getRoles(),
                        () -> "should return user with roles list as null, but was: " + userNodeActual.getRoles()),
                () -> assertEquals(userNodeExpected.getRoles(), userNodeActual.getRoles(),
                        () -> "should return user with user roles: " + userNodeExpected.getRoles() + ", but was: " + userNodeActual.getRoles()));
    }

    @Test
    void when_map_user_node_to_dto_should_return_valid_dto() {

        UserNode userNode = (UserNode) userBuilder.build(ObjectType.NODE);
        UserDTO userDTOExpected = (UserDTO) userBuilder.build(ObjectType.DTO);

        UserDTO userDTOActual = modelMapper.map(userNode, UserDTO.class);

        assertAll(
                () -> assertEquals(userDTOExpected.getUserName(), userDTOActual.getUserName(),
                        () -> "should return user with user name: " + userDTOExpected.getUserName() + ", but was: " + userDTOActual.getUserName()),
                () -> assertEquals(userDTOExpected.getPassword(), userDTOActual.getPassword(),
                        () -> "should return user with user password: " + userDTOExpected.getPassword() + ", but was: " + userDTOActual.getPassword()),
                () -> assertEquals(userDTOExpected.getEmail(), userDTOActual.getEmail(),
                        () -> "should return user with user email: " + userDTOExpected.getEmail() + ", but was: " + userDTOActual.getEmail()));
    }
}
