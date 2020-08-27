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
    private static ProvinceBuilder provinceBuilder;
    private static CityBuilder cityBuilder;
    private static EventBuilder eventBuilder;
    private static GroupBuilder groupBuilder;
    private static UserBuilder userBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        regionBuilder = new RegionBuilder();
        countryBuilder = new CountryBuilder();
        targetBuilder = new TargetBuilder();
        provinceBuilder = new ProvinceBuilder();
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

        CountryDTO countryDTOExpected = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        TargetDTO targetDTOExpected = (TargetDTO) targetBuilder.withCountry(countryDTOExpected).build(ObjectType.DTO);

        TargetNode targetNodeActual = modelMapper.map(targetDTOExpected, TargetNode.class);

        assertAll(
                () -> assertNull(targetNodeActual.getId(),
                        () -> "should return target node with id as null, but was: " + targetNodeActual.getId()),
                () -> assertEquals(targetDTOExpected.getTarget(), targetNodeActual.getTarget(),
                        () -> "should return target node with target: " + targetDTOExpected.getTarget() + ", but was: "
                                + targetNodeActual.getTarget()),
                () -> assertNull(targetNodeActual.getCountryOfOrigin().getId(),
                        () -> "should return target node with id as null, but was: " + targetNodeActual.getId()),
                () -> assertEquals(countryDTOExpected.getName(), targetNodeActual.getCountryOfOrigin().getName(),
                        () -> "should return target node with country name: " + countryDTOExpected.getName()
                                + ", but was: " + targetNodeActual.getCountryOfOrigin()));
    }

    @Test
    void when_map_target_node_to_dto_should_return_valid_dto() {

        CountryNode countryNodeExpected = (CountryNode) countryBuilder.build(ObjectType.NODE);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withCountry(countryNodeExpected).build(ObjectType.NODE);

        TargetDTO targetDTOActual = modelMapper.map(targetNodeExpected, TargetDTO.class);

        assertAll(                () -> assertEquals(targetNodeExpected.getTarget(), targetDTOActual.getTarget(),
                () -> "should return target dto with target: " + targetDTOActual.getTarget() + ", but was: "
                        + targetDTOActual.getTarget()),

                () -> assertEquals(countryNodeExpected, targetDTOActual.getCountryOfOrigin(),
                        () -> "should return target dto with country: " + countryNodeExpected + ", but was: " + targetDTOActual.getCountryOfOrigin()),
                () -> assertEquals(countryNodeExpected.getName(), targetDTOActual.getCountryOfOrigin().getName(),
                        () -> "should return target dto with country name: " + countryNodeExpected.getName()
                                + ", but was: " + targetDTOActual.getCountryOfOrigin()));
    }

    @Test
    void when_map_target_node_to_model_should_return_valid_model() {

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected).build(ObjectType.NODE);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withCountry(countryNodeExpected).build(ObjectType.NODE);

        TargetModel targetModelActual = modelMapper.map(targetNodeExpected, TargetModel.class);

        assertAll(
                () -> assertEquals(targetNodeExpected.getTarget(), targetModelActual.getTarget(),
                        () -> "should return target model with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + targetModelActual.getTarget()),
                () -> assertEquals(targetNodeExpected.getTarget(), targetModelActual.getTarget(),
                        () -> "should return target model with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + targetModelActual.getTarget()),
                () -> assertEquals(countryNodeExpected.getId(), targetModelActual.getCountryOfOrigin().getId(),
                        () -> "should return target model with country id: " + countryNodeExpected.getId()
                                + ", but was: " + targetModelActual.getCountryOfOrigin().getId()),
                () -> assertEquals(countryNodeExpected.getName(), targetModelActual.getCountryOfOrigin().getName(),
                        () -> "should return target model with country name: " + countryNodeExpected.getName()
                                + ", but was: " + targetModelActual.getCountryOfOrigin().getName()),
                () -> assertEquals(regionNodeExpected.getId(), targetModelActual.getCountryOfOrigin().getRegion().getId(),
                        () -> "should return target model with region id: " + regionNodeExpected.getId()
                                + ", but was: " + targetModelActual.getCountryOfOrigin().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), targetModelActual.getCountryOfOrigin().getRegion().getName(),
                        () -> "should return target model with region name: " + regionNodeExpected.getName()
                                + ", but was: " + targetModelActual.getCountryOfOrigin().getRegion()));
    }

    @Test
    void when_map_event_dto_to_node_should_return_valid_node() {

        CountryDTO countryDTOExpected = (CountryDTO) countryBuilder.withId(null).build(ObjectType.DTO);
        TargetDTO targetDTOExpected = (TargetDTO) targetBuilder.withId(null).withCountry(countryDTOExpected).build(ObjectType.DTO);
        ProvinceDTO provinceDTOExpected = (ProvinceDTO) provinceBuilder.withId(null).withCountry(countryDTOExpected)
                .build(ObjectType.DTO);
        CityDTO cityDTOExpected = (CityDTO) cityBuilder.withId(null).withProvince(provinceDTOExpected).build(ObjectType.DTO);
        EventDTO eventDTOExpected = (EventDTO) eventBuilder.withId(null).withTarget(targetDTOExpected).withCity(cityDTOExpected)
                .build(ObjectType.DTO);

        EventNode eventNodeActual = modelMapper.map(eventDTOExpected, EventNode.class);

        assertAll(
                () -> assertNull(eventNodeActual.getId(),
                        () -> "should return event node with id, but was: " + eventNodeActual.getId()),
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
                                + eventDTOExpected.getIsPartOfMultipleIncidents() + ", but was was: "
                                + eventNodeActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventDTOExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
                        () -> "should return event node which was successful: " + eventDTOExpected.getIsSuccessful()
                                + ", but was: " + eventNodeActual.getIsSuccessful()),
                () -> assertEquals(eventDTOExpected.getIsSuicidal(), eventNodeActual.getIsSuicidal(),
                        () -> "should return event node which was suicidal: " + eventDTOExpected.getIsSuicidal()
                                + ", but was: " + eventNodeActual.getIsSuicidal()),
                () -> assertNotNull(eventNodeActual.getTarget(),
                        () -> "should return event node with not null target, but was: null"),
                () -> assertEquals(targetDTOExpected.getTarget(), eventNodeActual.getTarget().getTarget(),
                        () -> "should return event node with target: " + targetDTOExpected.getTarget() + ", but was: "
                                + eventNodeActual.getTarget().getTarget()),
                () -> assertEquals(countryDTOExpected.getName(), eventNodeActual.getTarget().getCountryOfOrigin().getName(),
                        () -> "should return event node with country name: " + countryDTOExpected.getName()
                                + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin()),
                () -> assertNull(eventNodeActual.getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return event node with null region, but was: "
                                + eventNodeActual.getTarget().getCountryOfOrigin().getRegion()),

                () -> assertNotNull(eventNodeActual.getCity(),
                        () -> "should return event node with not null cty, but was: null"),
                () -> assertEquals(cityDTOExpected.getName(), eventNodeActual.getCity().getName(),
                        () -> "should return event node with city name: " + cityDTOExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getName()),
                () -> assertEquals(cityDTOExpected.getLatitude(), eventNodeActual.getCity().getLatitude(),
                        () -> "should return event node with city latitude: " + cityDTOExpected.getLatitude() + ", but was: "
                                + eventNodeActual.getCity().getLatitude()),
                () -> assertEquals(cityDTOExpected.getLongitude(), eventNodeActual.getCity().getLongitude(),
                        () -> "should return event node with city longitude: " + cityDTOExpected.getLongitude() + ", but was: "
                                + eventNodeActual.getCity().getLongitude()),

                () -> assertNotNull(eventNodeActual.getCity().getProvince(),
                        () -> "should return event node with not null province, but was: null"),
                () -> assertEquals(provinceDTOExpected.getName(), eventNodeActual.getCity().getProvince().getName(),
                        () -> "should return event node with province name: " + provinceDTOExpected.getName() + ", but was: "
                                + eventNodeActual.getCity().getProvince().getName()),
                () -> assertEquals(countryDTOExpected.getName(), eventNodeActual.getCity().getProvince().getCountry().getName(),
                        () -> "should return event node with country name: " + countryDTOExpected.getName()
                                + ", but was: " + eventNodeActual.getCity().getProvince().getCountry()),
                () -> assertNull(eventNodeActual.getCity().getProvince().getCountry().getRegion(),
                        () -> "should return event node with null region, but was: "
                                + eventNodeActual.getCity().getProvince().getCountry().getRegion()));
    }

    @Test
    void when_map_event_node_to_dto_should_return_valid_dto() {

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
                                + eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was was: "
                                + eventDTOActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected.getIsSuccessful(), eventDTOActual.getIsSuccessful(),
                        () -> "should return event dto which was successful: " + eventNodeExpected.getIsSuccessful()
                                + ", but was: " + eventDTOActual.getIsSuccessful()),
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), eventDTOActual.getIsSuicidal(),
                        () -> "should return event dto which was suicidal: " + eventNodeExpected.getIsSuicidal()
                                + ", but was: " + eventDTOActual.getIsSuicidal()),
                () -> assertNotNull(eventDTOActual.getTarget(),
                        () -> "should return event dto with not null target, but was: null"),
                () -> assertEquals(targetNodeExpected.getTarget(), eventDTOActual.getTarget().getTarget(),
                        () -> "should return event dto with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + eventDTOActual.getTarget().getTarget()),
                () -> assertEquals(countryNodeExpected.getName(), eventDTOActual.getTarget().getCountryOfOrigin().getName(),
                        () -> "should return event dto with country name: " + countryNodeExpected.getName()
                                + ", but was: " + eventDTOActual.getTarget().getCountryOfOrigin()),

                () -> assertNotNull(eventDTOActual.getCity(),
                        () -> "should return event dto with not null cty, but was: null"),
                () -> assertEquals(cityNodeExpected.getName(), eventDTOActual.getCity().getName(),
                        () -> "should return event dto with city name: " + cityNodeExpected.getName() + ", but was: "
                                + eventDTOActual.getCity().getName()),
                () -> assertEquals(cityNodeExpected.getLatitude(), eventDTOActual.getCity().getLatitude(),
                        () -> "should return event dto with city latitude: " + cityNodeExpected.getLatitude() + ", but was: "
                                + eventDTOActual.getCity().getLatitude()),
                () -> assertEquals(cityNodeExpected.getLongitude(), eventDTOActual.getCity().getLongitude(),
                        () -> "should return event dto with city longitude: " + cityNodeExpected.getLongitude() + ", but was: "
                                + eventDTOActual.getCity().getLongitude()),

                () -> assertNotNull(eventDTOActual.getCity().getProvince(),
                        () -> "should return event dto with not null province, but was: null"),
                () -> assertEquals(provinceNodeExpected.getName(), eventDTOActual.getCity().getProvince().getName(),
                        () -> "should return event dto with province name: " + provinceNodeExpected.getName() + ", but was: "
                                + eventDTOActual.getCity().getProvince().getName()),
                () -> assertEquals(countryNodeExpected.getName(), eventDTOActual.getCity().getProvince().getCountry().getName(),
                        () -> "should return event dto with country name: " + countryNodeExpected.getName()
                                + ", but was: " + eventDTOActual.getCity().getProvince().getCountry()));
    }

    @Test
    void when_map_event_node_to_model_should_return_valid_model() {

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

        EventModel eventModelActual = modelMapper.map(eventNodeExpected, EventModel.class);

        assertAll(
                () -> assertNotNull(eventModelActual.getId(),
                        () -> "should return event model with id, but was: " + eventModelActual.getId()),
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
                                + eventNodeExpected.getIsPartOfMultipleIncidents() + ", but was was: "
                                + eventModelActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected.getIsSuccessful(), eventModelActual.getIsSuccessful(),
                        () -> "should return event model which was successful: " + eventNodeExpected.getIsSuccessful()
                                + ", but was: " + eventModelActual.getIsSuccessful()),
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), eventModelActual.getIsSuicidal(),
                        () -> "should return event model which was suicidal: " + eventNodeExpected.getIsSuicidal()
                                + ", but was: " + eventModelActual.getIsSuicidal()),
                () -> assertNotNull(eventModelActual.getTarget(),
                        () -> "should return event model with not null target, but was: null"),
                () -> assertEquals(targetNodeExpected.getId(), eventModelActual.getTarget().getId(),
                        () -> "should return event model with target id: " + targetNodeExpected.getId() + ", but was: "
                                + eventModelActual.getTarget().getId()),
                () -> assertEquals(targetNodeExpected.getTarget(), eventModelActual.getTarget().getTarget(),
                        () -> "should return event model with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + eventModelActual.getTarget().getTarget()),
                () -> assertEquals(countryNodeExpected.getId(), eventModelActual.getTarget().getCountryOfOrigin().getId(),
                        () -> "should return event model with country id: " + countryNodeExpected.getId()
                                + ", but was: " + eventModelActual.getTarget().getId()),
                () -> assertEquals(countryNodeExpected.getName(), eventModelActual.getTarget().getCountryOfOrigin().getName(),
                        () -> "should return event model with country name: " + countryNodeExpected.getName()
                                + ", but was: " + eventModelActual.getTarget().getCountryOfOrigin()),
                () -> assertNotNull(eventModelActual.getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return event model with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected.getId(), eventModelActual.getTarget().getCountryOfOrigin().getRegion().getId(),
                        () -> "should return event model with region id: " + regionNodeExpected.getId()
                                + ", but was: " + eventModelActual.getTarget().getCountryOfOrigin().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), eventModelActual.getTarget().getCountryOfOrigin().getRegion().getName(),
                        () -> "should return event model with region name: " + regionNodeExpected.getName() + ", but was: "
                                + eventModelActual.getTarget().getCountryOfOrigin().getRegion().getName()),

                () -> assertNotNull(eventModelActual.getCity(),
                        () -> "should return event model with not null cty, but was: null"),
                () -> assertEquals(cityNodeExpected.getName(), eventModelActual.getCity().getName(),
                        () -> "should return event model with city name: " + cityNodeExpected.getName() + ", but was: "
                                + eventModelActual.getCity().getName()),
                () -> assertEquals(cityNodeExpected.getLatitude(), eventModelActual.getCity().getLatitude(),
                        () -> "should return event model with city latitude: " + cityNodeExpected.getLatitude() + ", but was: "
                                + eventModelActual.getCity().getLatitude()),
                () -> assertEquals(cityNodeExpected.getLongitude(), eventModelActual.getCity().getLongitude(),
                        () -> "should return event model with city longitude: " + cityNodeExpected.getLongitude() + ", but was: "
                                + eventModelActual.getCity().getLongitude()),

                () -> assertNotNull(eventModelActual.getCity().getProvince(),
                        () -> "should return event model with not null province, but was: null"),
                () -> assertEquals(provinceNodeExpected.getId(), eventModelActual.getCity().getProvince().getId(),
                        () -> "should return event model with province id: " + provinceNodeExpected.getId()
                                + ", but was: " + eventModelActual.getCity().getProvince().getId()),
                () -> assertEquals(provinceNodeExpected.getName(), eventModelActual.getCity().getProvince().getName(),
                        () -> "should return event model with province name: " + provinceNodeExpected.getName() + ", but was: "
                                + eventModelActual.getCity().getProvince().getName()),
                () -> assertEquals(countryNodeExpected.getId(), eventModelActual.getCity().getProvince().getCountry().getId(),
                        () -> "should return event model with country id: " + countryNodeExpected.getId()
                                + ", but was: " + eventModelActual.getCity().getProvince().getCountry().getId()),
                () -> assertEquals(countryNodeExpected.getName(), eventModelActual.getCity().getProvince().getCountry().getName(),
                        () -> "should return event model with country name: " + countryNodeExpected.getName()
                                + ", but was: " + eventModelActual.getCity().getProvince().getCountry()),
                () -> assertNotNull(eventModelActual.getCity().getProvince().getCountry().getRegion(),
                        () -> "should return event model with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected.getId(), eventModelActual.getCity().getProvince().getCountry().getRegion().getId(),
                        () -> "should return event model with region id: " + regionNodeExpected.getId()
                                + ", but was: " + eventModelActual.getCity().getProvince().getCountry().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), eventModelActual.getCity().getProvince().getCountry().getRegion().getName(),
                        () -> "should return event model with region name: " + regionNodeExpected.getName() + ", but was: "
                                + eventModelActual.getCity().getProvince().getCountry().getRegion().getName()));
    }

    @Test
    void when_map_group_dto_to_node_should_return_valid_model() {

        CountryDTO countryDTOExpected = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        TargetDTO targetDTOExpected = (TargetDTO) targetBuilder.withCountry(countryDTOExpected).build(ObjectType.DTO);
        ProvinceDTO provinceDTOExpected = (ProvinceDTO) provinceBuilder.withCountry(countryDTOExpected)
                .build(ObjectType.DTO);
        CityDTO cityDTOExpected = (CityDTO) cityBuilder.withProvince(provinceDTOExpected).build(ObjectType.DTO);
        EventDTO eventDTOExpected = (EventDTO) eventBuilder.withTarget(targetDTOExpected).withCity(cityDTOExpected)
                .build(ObjectType.DTO);

        CountryDTO countryDTOExpected2 = (CountryDTO) countryBuilder.withName("country2").build(ObjectType.DTO);
        TargetDTO targetDTOExpected2 = (TargetDTO) targetBuilder.withTarget("target2").withCountry(countryDTOExpected2)
                .build(ObjectType.DTO);
        ProvinceDTO provinceDTOExpected2 = (ProvinceDTO) provinceBuilder.withName("province2")
                .withCountry(countryDTOExpected2).build(ObjectType.DTO);
        CityDTO cityDTOExpected2 = (CityDTO) cityBuilder.withName("city2").withProvince(provinceDTOExpected2)
                .build(ObjectType.DTO);
        EventDTO eventDTOExpected2 = (EventDTO) eventBuilder.withSummary("summary2").withTarget(targetDTOExpected2)
                .withCity(cityDTOExpected2).build(ObjectType.DTO);

        GroupDTO groupDTOExpected = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTOExpected, eventDTOExpected2))
                .build(ObjectType.DTO);

        GroupNode groupNodeActual = modelMapper.map(groupDTOExpected, GroupNode.class);

        assertAll(
                () -> assertNull(groupNodeActual.getId(),
                        () -> "should return group node with id, but was: " + groupNodeActual.getId()),
                () -> assertEquals(groupDTOExpected.getName(), groupNodeActual.getName(),
                        () -> "should return group node with name: " + groupDTOExpected.getName() + ", but was: "
                                + groupNodeActual.getName()),

                () -> assertEquals(eventDTOExpected.getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group event node with summary: " + eventDTOExpected.getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(eventDTOExpected.getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(),
                        () -> "should return group event node with motive: " + eventDTOExpected.getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(eventDTOExpected.getDate(), groupNodeActual.getEventsCaused().get(0).getDate(),
                        () -> "should return group event node with date: " + eventDTOExpected.getDate() +
                                ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(eventDTOExpected.getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        () -> "should return group event node which was part of multiple incidents: " + eventDTOExpected.getIsPartOfMultipleIncidents() +
                                ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventDTOExpected.getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group event node which was successful: " + eventDTOExpected.getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(eventDTOExpected.getIsSuicidal(), groupNodeActual.getEventsCaused().get(0).getIsSuicidal(), () -> "should return group event node which was suicidal: " + eventDTOExpected.getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group node with event node with not null target, but was: null"),
                () -> assertEquals(targetDTOExpected.getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget().getTarget(),
                        () -> "should return group node with event target name: " + groupNodeActual.getEventsCaused().get(0).getTarget().getTarget() + ", but was: "
                                + targetDTOExpected.getTarget()),

                () -> assertEquals(countryDTOExpected.getName(), groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getName(),
                        () -> "should return group node with event node with country name: " + countryDTOExpected.getName()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),
                () -> assertNull(groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return group node with null region, but was: " +
                                groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getRegion()),

                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getCity(),
                        () -> "should return group node with event node with not null city, but was: null"),
                () -> assertEquals(cityDTOExpected.getName(), groupNodeActual.getEventsCaused().get(0).getCity().getName(),
                        () -> "should return group node with event city name: " + groupNodeActual.getEventsCaused().get(0).getCity().getName() + ", but was: "
                                + cityDTOExpected.getName()),
                () -> assertEquals(cityDTOExpected.getLatitude(), groupNodeActual.getEventsCaused().get(0).getCity().getLatitude(),
                        () -> "should return group node with event city latitude: " + groupNodeActual.getEventsCaused().get(0).getCity().getLatitude() + ", but was: "
                                + cityDTOExpected.getLatitude()),
                () -> assertEquals(cityDTOExpected.getLongitude(), groupNodeActual.getEventsCaused().get(0).getCity().getLongitude(),
                        () -> "should return group node with event city longitude: " + groupNodeActual.getEventsCaused().get(0).getCity().getLongitude() + ", but was: "
                                + cityDTOExpected.getLongitude()),

                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getCity().getProvince(),
                        () -> "should return group node with not null province, but was: null"),
                () -> assertEquals(provinceDTOExpected.getName(), groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getName(),
                        () -> "should return group node with province name: " + provinceDTOExpected.getName() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getName()),
                () -> assertEquals(countryDTOExpected.getName(), groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getName(),
                        () -> "should return group node with country name: " + countryDTOExpected.getName()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry()),
                () -> assertNull(groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getRegion(),
                        () -> "should return group node with null region, but was: " +
                                groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getRegion()),

                () -> assertEquals(eventDTOExpected2.getSummary(), groupNodeActual.getEventsCaused().get(1).getSummary(), () -> "should return group event node with summary: " + eventDTOExpected2.getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getSummary()),
                () -> assertEquals(eventDTOExpected2.getMotive(), groupNodeActual.getEventsCaused().get(1).getMotive(), () -> "should return group event node with motive: " + eventDTOExpected2.getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getMotive()),
                () -> assertEquals(eventDTOExpected2.getDate(), groupNodeActual.getEventsCaused().get(1).getDate(), () -> "should return group event node with date: " + eventDTOExpected2.getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getDate()),
                () -> assertEquals(eventDTOExpected2.getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents(), () -> "should return group event node which was part of multiple incidents: " + eventDTOExpected2.getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventDTOExpected2.getIsSuccessful(), groupNodeActual.getEventsCaused().get(1).getIsSuccessful(), () -> "should return group event node which was successful: " + eventDTOExpected2.getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuccessful()),
                () -> assertEquals(eventDTOExpected2.getIsSuicidal(), groupNodeActual.getEventsCaused().get(1).getIsSuicidal(), () -> "should return group event node which was suicidal: " + eventDTOExpected2.getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuicidal()),
                () -> assertNotNull(targetDTOExpected2,
                        () -> "should return group event node with not null target, but was: null"),
                () -> assertNotNull(groupNodeActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group node with event node with not null target, but was: null"),
                () -> assertEquals(targetDTOExpected2.getTarget(), groupNodeActual.getEventsCaused().get(1).getTarget().getTarget(),
                        () -> "should return group node with event target name: " + groupNodeActual.getEventsCaused().get(1).getTarget().getTarget() + ", but was: "
                                + targetDTOExpected2.getTarget()),

                () -> assertEquals(countryDTOExpected2.getName(), groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getName(),
                        () -> "should return group node with event node with country name: " + countryDTOExpected2.getName()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin()),
                () -> assertNull(groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return group node with null region, but was: " +
                                groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getRegion()),

                () -> assertNotNull(groupNodeActual.getEventsCaused().get(1).getCity(),
                        () -> "should return group node with event node with not null city, but was: null"),
                () -> assertEquals(cityDTOExpected2.getName(), groupNodeActual.getEventsCaused().get(1).getCity().getName(),
                        () -> "should return group node with event city name: " + groupNodeActual.getEventsCaused().get(1).getCity().getName() + ", but was: "
                                + cityDTOExpected2.getName()),
                () -> assertEquals(cityDTOExpected2.getLatitude(), groupNodeActual.getEventsCaused().get(1).getCity().getLatitude(),
                        () -> "should return group node with event city latitude: " + groupNodeActual.getEventsCaused().get(1).getCity().getLatitude() + ", but was: "
                                + cityDTOExpected2.getLatitude()),
                () -> assertEquals(cityDTOExpected2.getLongitude(), groupNodeActual.getEventsCaused().get(1).getCity().getLongitude(),
                        () -> "should return group node with event city longitude: " + groupNodeActual.getEventsCaused().get(1).getCity().getLongitude() + ", but was: "
                                + cityDTOExpected2.getLongitude()),

                () -> assertNotNull(groupNodeActual.getEventsCaused().get(1).getCity().getProvince(),
                        () -> "should return group node with not null province, but was: null"),
                () -> assertEquals(provinceDTOExpected2.getName(), groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getName(),
                        () -> "should return group node with province name: " + provinceDTOExpected2.getName() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getName()),
                () -> assertEquals(countryDTOExpected2.getName(), groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getName(),
                        () -> "should return group node with country name: " + countryDTOExpected2.getName()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getCountry()),
                () -> assertNull(groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getRegion(),
                        () -> "should return group node with null region, but was: " +
                                groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getRegion()));
    }

    @Test
    void when_map_group_node_to_dto_should_return_valid_model() {

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

        RegionNode regionNodeExpected2 = (RegionNode) regionBuilder.withName("region2").build(ObjectType.NODE);
        CountryNode countryNodeExpected2 = (CountryNode) countryBuilder.withName("country2")
                .withRegion(regionNodeExpected2).build(ObjectType.NODE);
        TargetNode targetNodeExpected2 = (TargetNode) targetBuilder.withTarget("target2")
                .withCountry(countryNodeExpected2).build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected2 = (ProvinceNode) provinceBuilder.withName("province2")
                .withCountry(countryNodeExpected2).build(ObjectType.NODE);
        CityNode cityNodeExpected2 = (CityNode) cityBuilder.withName("city2").withProvince(provinceNodeExpected2)
                .build(ObjectType.NODE);
        EventNode eventNodeExpected2 = (EventNode) eventBuilder.withSummary("summary2").withTarget(targetNodeExpected2)
                .withCity(cityNodeExpected2)
                .build(ObjectType.NODE);

        GroupNode groupNodeExpected = (GroupNode) groupBuilder.
                withEventsCaused(List.of(eventNodeExpected, eventNodeExpected2))
                .build(ObjectType.NODE);

        GroupDTO groupDTOActual = modelMapper.map(groupNodeExpected, GroupDTO.class);

        assertAll(
                () -> assertEquals(groupNodeExpected.getName(), groupDTOActual.getName(),
                        () -> "should return group dto with name: " + groupNodeExpected.getName() + ", but was: "
                                + groupDTOActual.getName()),

                () -> assertEquals(eventNodeExpected.getSummary(), groupDTOActual.getEventsCaused().get(0).getSummary(), () -> "should return group event dto with summary: " + eventNodeExpected.getSummary() + ", but was: " + groupDTOActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(eventNodeExpected.getMotive(), groupDTOActual.getEventsCaused().get(0).getMotive(),
                        () -> "should return group event dto with motive: " + eventNodeExpected.getMotive() + ", but was: " + groupDTOActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(eventNodeExpected.getDate(), groupDTOActual.getEventsCaused().get(0).getDate(),
                        () -> "should return group event dto with date: " + eventNodeExpected.getDate() +
                                ", but was: " + groupDTOActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
                        groupDTOActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        () -> "should return group event dto which was part of multiple incidents: " + eventNodeExpected.getIsPartOfMultipleIncidents() +
                                ", but was was: " + groupDTOActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected.getIsSuccessful(), groupDTOActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group event dto which was successful: " + eventNodeExpected.getIsSuccessful() + ", but was: " + groupDTOActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), groupDTOActual.getEventsCaused().get(0).getIsSuicidal(), () -> "should return group event dto which was suicidal: " + eventNodeExpected.getIsSuicidal() + ", but was: " + groupDTOActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupDTOActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group dto with event dto with not null target, but was: null"),
                () -> assertEquals(targetNodeExpected.getTarget(), groupDTOActual.getEventsCaused().get(0).getTarget().getTarget(),
                        () -> "should return group dto with event target name: " + groupDTOActual.getEventsCaused().get(0).getTarget().getTarget() + ", but was: "
                                + targetNodeExpected.getTarget()),

                () -> assertEquals(countryNodeExpected.getName(), groupDTOActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getName(),
                        () -> "should return group dto with event dto with country name: " + countryNodeExpected.getName()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),

                () -> assertNotNull(groupDTOActual.getEventsCaused().get(0).getCity(),
                        () -> "should return group dto with event dto with not null city, but was: null"),
                () -> assertEquals(cityNodeExpected.getName(), groupDTOActual.getEventsCaused().get(0).getCity().getName(),
                        () -> "should return group dto with event city name: " + groupDTOActual.getEventsCaused().get(0).getCity().getName() + ", but was: "
                                + cityNodeExpected.getName()),
                () -> assertEquals(cityNodeExpected.getLatitude(), groupDTOActual.getEventsCaused().get(0).getCity().getLatitude(),
                        () -> "should return group dto with event city latitude: " + groupDTOActual.getEventsCaused().get(0).getCity().getLatitude() + ", but was: "
                                + cityNodeExpected.getLatitude()),
                () -> assertEquals(cityNodeExpected.getLongitude(), groupDTOActual.getEventsCaused().get(0).getCity().getLongitude(),
                        () -> "should return group dto with event city longitude: " + groupDTOActual.getEventsCaused().get(0).getCity().getLongitude() + ", but was: "
                                + cityNodeExpected.getLongitude()),

                () -> assertNotNull(groupDTOActual.getEventsCaused().get(0).getCity().getProvince(),
                        () -> "should return group dto with not null province, but was: null"),
                () -> assertEquals(provinceNodeExpected.getName(), groupDTOActual.getEventsCaused().get(0).getCity().getProvince().getName(),
                        () -> "should return group dto with province name: " + provinceNodeExpected.getName() + ", but was: "
                                + groupDTOActual.getEventsCaused().get(0).getCity().getProvince().getName()),
                () -> assertEquals(countryNodeExpected.getName(), groupDTOActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getName(),
                        () -> "should return group dto with country name: " + countryNodeExpected.getName()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(0).getCity().getProvince().getCountry()),

                () -> assertEquals(eventNodeExpected2.getSummary(), groupDTOActual.getEventsCaused().get(1).getSummary(), () -> "should return group event dto with summary: " + eventNodeExpected2.getSummary() + ", but was: " + groupDTOActual.getEventsCaused().get(1).getSummary()),
                () -> assertEquals(eventNodeExpected2.getMotive(), groupDTOActual.getEventsCaused().get(1).getMotive(), () -> "should return group event dto with motive: " + eventNodeExpected2.getMotive() + ", but was: " + groupDTOActual.getEventsCaused().get(1).getMotive()),
                () -> assertEquals(eventNodeExpected2.getDate(), groupDTOActual.getEventsCaused().get(1).getDate(), () -> "should return group event dto with date: " + eventNodeExpected2.getDate() + ", but was: " + groupDTOActual.getEventsCaused().get(1).getDate()),
                () -> assertEquals(eventNodeExpected2.getIsPartOfMultipleIncidents(),
                        groupDTOActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents(), () -> "should return group event dto which was part of multiple incidents: " + eventNodeExpected2.getIsPartOfMultipleIncidents() + ", but was was: " + groupDTOActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected2.getIsSuccessful(), groupDTOActual.getEventsCaused().get(1).getIsSuccessful(), () -> "should return group event dto which was successful: " + eventNodeExpected2.getIsSuccessful() + ", but was: " + groupDTOActual.getEventsCaused().get(1).getIsSuccessful()),
                () -> assertEquals(eventNodeExpected2.getIsSuicidal(), groupDTOActual.getEventsCaused().get(1).getIsSuicidal(), () -> "should return group event dto which was suicidal: " + eventNodeExpected2.getIsSuicidal() + ", but was: " + groupDTOActual.getEventsCaused().get(1).getIsSuicidal()),
                () -> assertNotNull(targetNodeExpected2,
                        () -> "should return group event dto with not null target, but was: null"),
                () -> assertNotNull(groupDTOActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group dto with event dto with not null target, but was: null"),
                () -> assertEquals(targetNodeExpected2.getTarget(), groupDTOActual.getEventsCaused().get(1).getTarget().getTarget(),
                        () -> "should return group dto with event target name: " + groupDTOActual.getEventsCaused().get(1).getTarget().getTarget() + ", but was: "
                                + targetNodeExpected2.getTarget()),

                () -> assertEquals(countryNodeExpected2.getName(), groupDTOActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getName(),
                        () -> "should return group dto with event dto with country name: " + countryNodeExpected2.getName()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin()),

                () -> assertNotNull(groupDTOActual.getEventsCaused().get(1).getCity(),
                        () -> "should return group dto with event dto with not null city, but was: null"),
                () -> assertEquals(cityNodeExpected2.getName(), groupDTOActual.getEventsCaused().get(1).getCity().getName(),
                        () -> "should return group dto with event city name: " + groupDTOActual.getEventsCaused().get(1).getCity().getName() + ", but was: "
                                + cityNodeExpected2.getName()),
                () -> assertEquals(cityNodeExpected2.getLatitude(), groupDTOActual.getEventsCaused().get(1).getCity().getLatitude(),
                        () -> "should return group dto with event city latitude: " + groupDTOActual.getEventsCaused().get(1).getCity().getLatitude() + ", but was: "
                                + cityNodeExpected2.getLatitude()),
                () -> assertEquals(cityNodeExpected2.getLongitude(), groupDTOActual.getEventsCaused().get(1).getCity().getLongitude(),
                        () -> "should return group dto with event city longitude: " + groupDTOActual.getEventsCaused().get(1).getCity().getLongitude() + ", but was: "
                                + cityNodeExpected2.getLongitude()),

                () -> assertNotNull(groupDTOActual.getEventsCaused().get(1).getCity().getProvince(),
                        () -> "should return group dto with not null province, but was: null"),
                () -> assertEquals(provinceNodeExpected2.getName(), groupDTOActual.getEventsCaused().get(1).getCity().getProvince().getName(),
                        () -> "should return group dto with province name: " + provinceNodeExpected2.getName() + ", but was: "
                                + groupDTOActual.getEventsCaused().get(1).getCity().getProvince().getName()),
                () -> assertEquals(countryNodeExpected2.getName(), groupDTOActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getName(),
                        () -> "should return group dto with country name: " + countryNodeExpected2.getName()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(1).getCity().getProvince().getCountry()));
    }

    @Test
    void when_map_group_node_to_model_should_return_valid_model() {

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

        RegionNode regionNodeExpected2 = (RegionNode) regionBuilder.withName("region2").build(ObjectType.NODE);
        CountryNode countryNodeExpected2 = (CountryNode) countryBuilder.withName("country2")
                .withRegion(regionNodeExpected2).build(ObjectType.NODE);
        TargetNode targetNodeExpected2 = (TargetNode) targetBuilder.withTarget("target2")
                .withCountry(countryNodeExpected2).build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected2 = (ProvinceNode) provinceBuilder.withName("province2")
                .withCountry(countryNodeExpected2).build(ObjectType.NODE);
        CityNode cityNodeExpected2 = (CityNode) cityBuilder.withName("city2").withProvince(provinceNodeExpected2)
                .build(ObjectType.NODE);
        EventNode eventNodeExpected2 = (EventNode) eventBuilder.withSummary("summary2").withTarget(targetNodeExpected2)
                .withCity(cityNodeExpected2)
                .build(ObjectType.NODE);

        GroupNode groupNodeExpected = (GroupNode) groupBuilder.
                withEventsCaused(List.of(eventNodeExpected, eventNodeExpected2))
                .build(ObjectType.NODE);

        GroupModel groupModelActual = modelMapper.map(groupNodeExpected, GroupModel.class);

        assertAll(
                () -> assertNotNull(groupModelActual.getId(),
                        () -> "should return group model with id, but was: " + groupModelActual.getId()),
                () -> assertEquals(groupNodeExpected.getName(), groupModelActual.getName(),
                        () -> "should return group model with name: " + groupNodeExpected.getName() + ", but was: "
                                + groupModelActual.getName()),

                () -> assertEquals(eventNodeExpected.getId(), groupModelActual.getEventsCaused().get(0).getId(), () -> "should return group event node with id: " + eventNodeExpected.getId() + ", but was: "
                        + groupModelActual.getEventsCaused().get(0).getId()),
                () -> assertEquals(eventNodeExpected.getSummary(), groupModelActual.getEventsCaused().get(0).getSummary(), () -> "should return group event node with summary: " + eventNodeExpected.getSummary() + ", but was: " + groupModelActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(eventNodeExpected.getMotive(), groupModelActual.getEventsCaused().get(0).getMotive(),
                        () -> "should return group event node with motive: " + eventNodeExpected.getMotive() + ", but was: " + groupModelActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(eventNodeExpected.getDate(), groupModelActual.getEventsCaused().get(0).getDate(),
                        () -> "should return group event node with date: " + eventNodeExpected.getDate() +
                                ", but was: " + groupModelActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
                        groupModelActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        () -> "should return group event node which was part of multiple incidents: " + eventNodeExpected.getIsPartOfMultipleIncidents() +
                                ", but was was: " + groupModelActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected.getIsSuccessful(), groupModelActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group event node which was successful: " + eventNodeExpected.getIsSuccessful() + ", but was: " + groupModelActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(eventNodeExpected.getIsSuicidal(), groupModelActual.getEventsCaused().get(0).getIsSuicidal(), () -> "should return group event node which was suicidal: " + eventNodeExpected.getIsSuicidal() + ", but was: " + groupModelActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupModelActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group model with event node with not null target, but was: null"),
                () -> assertEquals(targetNodeExpected.getId(), groupModelActual.getEventsCaused().get(0).getTarget().getId(),
                        () -> "should return group model with event target id: " + groupModelActual.getEventsCaused().get(0).getTarget().getId() + ", but was: "
                                + targetNodeExpected.getId()),
                () -> assertEquals(targetNodeExpected.getTarget(), groupModelActual.getEventsCaused().get(0).getTarget().getTarget(),
                        () -> "should return group model with event target name: " + groupModelActual.getEventsCaused().get(0).getTarget().getTarget() + ", but was: "
                                + targetNodeExpected.getTarget()),
                
                () -> assertEquals(countryNodeExpected.getId(), groupModelActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getId(),
                        () -> "should return group model with event node with country id: " + countryNodeExpected.getId()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getTarget().getId()),
                () -> assertEquals(countryNodeExpected.getName(), groupModelActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getName(),
                        () -> "should return group model with event node with country name: " + countryNodeExpected.getName()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),
                () -> assertNotNull(groupModelActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return group model with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected.getId(), groupModelActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getRegion().getId(),
                        () -> "should return group model with region id: " + regionNodeExpected.getId()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), groupModelActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getRegion().getName(),
                        () -> "should return group model with region name: " + regionNodeExpected.getName() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getRegion().getName()),

                () -> assertNotNull(groupModelActual.getEventsCaused().get(0).getCity(),
                        () -> "should return group model with event node with not null city, but was: null"),
                () -> assertEquals(cityNodeExpected.getId(), groupModelActual.getEventsCaused().get(0).getCity().getId(),
                        () -> "should return group model with event city id: " + groupModelActual.getEventsCaused().get(0).getCity().getId() + ", but was: "
                                + cityNodeExpected.getId()),
                () -> assertEquals(cityNodeExpected.getName(), groupModelActual.getEventsCaused().get(0).getCity().getName(),
                        () -> "should return group model with event city name: " + groupModelActual.getEventsCaused().get(0).getCity().getName() + ", but was: "
                                + cityNodeExpected.getName()),
                () -> assertEquals(cityNodeExpected.getLatitude(), groupModelActual.getEventsCaused().get(0).getCity().getLatitude(),
                        () -> "should return group model with event city latitude: " + groupModelActual.getEventsCaused().get(0).getCity().getLatitude() + ", but was: "
                                + cityNodeExpected.getLatitude()),
                () -> assertEquals(cityNodeExpected.getLongitude(), groupModelActual.getEventsCaused().get(0).getCity().getLongitude(),
                        () -> "should return group model with event city longitude: " + groupModelActual.getEventsCaused().get(0).getCity().getLongitude() + ", but was: "
                                + cityNodeExpected.getLongitude()),

                () -> assertNotNull(groupModelActual.getEventsCaused().get(0).getCity().getProvince(),
                        () -> "should return group model with not null province, but was: null"),
                () -> assertEquals(provinceNodeExpected.getId(), groupModelActual.getEventsCaused().get(0).getCity().getProvince().getId(),
                        () -> "should return group model with province id: " + provinceNodeExpected.getId()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getCity().getProvince().getId()),
                () -> assertEquals(provinceNodeExpected.getName(), groupModelActual.getEventsCaused().get(0).getCity().getProvince().getName(),
                        () -> "should return group model with province name: " + provinceNodeExpected.getName() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getCity().getProvince().getName()),
                () -> assertEquals(countryNodeExpected.getId(), groupModelActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getId(),
                        () -> "should return group model with country id: " + countryNodeExpected.getId()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getId()),
                () -> assertEquals(countryNodeExpected.getName(), groupModelActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getName(),
                        () -> "should return group model with country name: " + countryNodeExpected.getName()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getCity().getProvince().getCountry()),
                () -> assertNotNull(groupModelActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getRegion(),
                        () -> "should return group model with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected.getId(), groupModelActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getRegion().getId(),
                        () -> "should return group model with region id: " + regionNodeExpected.getId()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), groupModelActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getRegion().getName(),
                        () -> "should return group model with region name: " + regionNodeExpected.getName() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getRegion().getName()),

                () -> assertEquals(eventNodeExpected2.getId(), groupModelActual.getEventsCaused().get(1).getId(), () -> "should return group event node with id: " + eventNodeExpected2.getId() + ", but was: "
                        + groupModelActual.getEventsCaused().get(1).getId()),
                () -> assertEquals(eventNodeExpected2.getSummary(), groupModelActual.getEventsCaused().get(1).getSummary(), () -> "should return group event node with summary: " + eventNodeExpected2.getSummary() + ", but was: " + groupModelActual.getEventsCaused().get(1).getSummary()),
                () -> assertEquals(eventNodeExpected2.getMotive(), groupModelActual.getEventsCaused().get(1).getMotive(), () -> "should return group event node with motive: " + eventNodeExpected2.getMotive() + ", but was: " + groupModelActual.getEventsCaused().get(1).getMotive()),
                () -> assertEquals(eventNodeExpected2.getDate(), groupModelActual.getEventsCaused().get(1).getDate(), () -> "should return group event node with date: " + eventNodeExpected2.getDate() + ", but was: " + groupModelActual.getEventsCaused().get(1).getDate()),
                () -> assertEquals(eventNodeExpected2.getIsPartOfMultipleIncidents(),
                        groupModelActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents(), () -> "should return group event node which was part of multiple incidents: " + eventNodeExpected2.getIsPartOfMultipleIncidents() + ", but was was: " + groupModelActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected2.getIsSuccessful(), groupModelActual.getEventsCaused().get(1).getIsSuccessful(), () -> "should return group event node which was successful: " + eventNodeExpected2.getIsSuccessful() + ", but was: " + groupModelActual.getEventsCaused().get(1).getIsSuccessful()),
                () -> assertEquals(eventNodeExpected2.getIsSuicidal(), groupModelActual.getEventsCaused().get(1).getIsSuicidal(), () -> "should return group event node which was suicidal: " + eventNodeExpected2.getIsSuicidal() + ", but was: " + groupModelActual.getEventsCaused().get(1).getIsSuicidal()),
                () -> assertNotNull(targetNodeExpected2,
                        () -> "should return group event node with not null target, but was: null"),
                () -> assertNotNull(groupModelActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group model with event node with not null target, but was: null"),
                () -> assertEquals(targetNodeExpected2.getId(), groupModelActual.getEventsCaused().get(1).getTarget().getId(),
                        () -> "should return group model with event target id: " + groupModelActual.getEventsCaused().get(1).getTarget().getId() + ", but was: "
                                + targetNodeExpected2.getId()),
                () -> assertEquals(targetNodeExpected2.getTarget(), groupModelActual.getEventsCaused().get(1).getTarget().getTarget(),
                        () -> "should return group model with event target name: " + groupModelActual.getEventsCaused().get(1).getTarget().getTarget() + ", but was: "
                                + targetNodeExpected2.getTarget()),
                
                () -> assertEquals(countryNodeExpected2.getId(), groupModelActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getId(),
                        () -> "should return group model with event node with country id: " + countryNodeExpected2.getId()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getTarget().getId()),
                () -> assertEquals(countryNodeExpected2.getName(), groupModelActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getName(),
                        () -> "should return group model with event node with country name: " + countryNodeExpected2.getName()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin()),
                () -> assertNotNull(groupModelActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return group model with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected2.getId(), groupModelActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getRegion().getId(),
                        () -> "should return group model with region id: " + regionNodeExpected2.getId()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getRegion().getId()),
                () -> assertEquals(regionNodeExpected2.getName(), groupModelActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getRegion().getName(),
                        () -> "should return group model with region name: " + regionNodeExpected2.getName() + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getRegion().getName()),

                () -> assertNotNull(groupModelActual.getEventsCaused().get(1).getCity(),
                        () -> "should return group model with event node with not null city, but was: null"),
                () -> assertEquals(cityNodeExpected2.getId(), groupModelActual.getEventsCaused().get(1).getCity().getId(),
                        () -> "should return group model with event city id: " + groupModelActual.getEventsCaused().get(1).getCity().getId() + ", but was: "
                                + cityNodeExpected2.getId()),
                () -> assertEquals(cityNodeExpected2.getName(), groupModelActual.getEventsCaused().get(1).getCity().getName(),
                        () -> "should return group model with event city name: " + groupModelActual.getEventsCaused().get(1).getCity().getName() + ", but was: "
                                + cityNodeExpected2.getName()),
                () -> assertEquals(cityNodeExpected2.getLatitude(), groupModelActual.getEventsCaused().get(1).getCity().getLatitude(),
                        () -> "should return group model with event city latitude: " + groupModelActual.getEventsCaused().get(1).getCity().getLatitude() + ", but was: "
                                + cityNodeExpected2.getLatitude()),
                () -> assertEquals(cityNodeExpected2.getLongitude(), groupModelActual.getEventsCaused().get(1).getCity().getLongitude(),
                        () -> "should return group model with event city longitude: " + groupModelActual.getEventsCaused().get(1).getCity().getLongitude() + ", but was: "
                                + cityNodeExpected2.getLongitude()),

                () -> assertNotNull(groupModelActual.getEventsCaused().get(1).getCity().getProvince(),
                        () -> "should return group model with not null province, but was: null"),
                () -> assertEquals(provinceNodeExpected2.getId(), groupModelActual.getEventsCaused().get(1).getCity().getProvince().getId(),
                        () -> "should return group model with province id: " + provinceNodeExpected2.getId()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getCity().getProvince().getId()),
                () -> assertEquals(provinceNodeExpected2.getName(), groupModelActual.getEventsCaused().get(1).getCity().getProvince().getName(),
                        () -> "should return group model with province name: " + provinceNodeExpected2.getName() + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getCity().getProvince().getName()),
                () -> assertEquals(countryNodeExpected2.getId(), groupModelActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getId(),
                        () -> "should return group model with country id: " + countryNodeExpected2.getId()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getId()),
                () -> assertEquals(countryNodeExpected2.getName(), groupModelActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getName(),
                        () -> "should return group model with country name: " + countryNodeExpected2.getName()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getCity().getProvince().getCountry()),
                () -> assertNotNull(groupModelActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getRegion(),
                        () -> "should return group model with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected2.getId(), groupModelActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getRegion().getId(),
                        () -> "should return group model with region id: " + regionNodeExpected2.getId()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getRegion().getId()),
                () -> assertEquals(regionNodeExpected2.getName(), groupModelActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getRegion().getName(),
                        () -> "should return group model with region name: " + regionNodeExpected2.getName() + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getRegion().getName()));
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
