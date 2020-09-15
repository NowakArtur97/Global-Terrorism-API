package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.*;
import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionNode;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.*;
import com.NowakArtur97.GlobalTerrorismAPI.node.*;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.*;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("ObjectMapper_Tests")
class EventMapperTest {

    private ObjectMapper objectMapper;

    @Mock
    private ModelMapper modelMapper;

    private static RegionBuilder regionBuilder;
    private static CountryBuilder countryBuilder;
    private static TargetBuilder targetBuilder;
    private static ProvinceBuilder provinceBuilder;
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

        objectMapper = new ObjectMapperImpl(modelMapper);
    }

    @Test
    void when_map_event_dto_to_node_should_return_node() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withId(null).withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withId(null).withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.withId(null).withProvince(provinceNodeExpected).build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withId(null).withTarget(targetNodeExpected)
                .withCity(cityNodeExpected)
                .build(ObjectType.NODE);

        when(modelMapper.map(eventDTO, EventNode.class)).thenReturn(eventNodeExpected);

        EventNode eventNodeActual = objectMapper.map(eventDTO, EventNode.class);

        assertAll(
                () -> assertNull(eventNodeActual.getId(),
                        () -> "should return event node with id as null, but was: " + eventNodeActual.getId()),
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
                () -> assertNull(eventNodeActual.getTarget().getId(),
                        () -> "should return event target with id as null, but was: "
                                + eventNodeActual.getTarget().getId()),
                () -> assertEquals(targetNodeExpected.getTarget(), eventNodeActual.getTarget().getTarget(),
                        () -> "should return event node with target: " + targetNodeExpected.getTarget()
                                + ", but was: " + eventNodeActual.getTarget().getTarget()),
                () -> assertNotNull(eventNodeActual.getTarget().getCountryOfOrigin().getId(),
                        () -> "should not return event event model with id as null, but was: null"),
                () -> assertEquals(countryNodeExpected.getName(), eventNodeActual.getTarget().getCountryOfOrigin().getName(),
                        () -> "should return event event model with country name: " + countryNodeExpected.getName()
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
                        () -> "should return event node with not null city, but was: null"),
                () -> assertNull(eventNodeActual.getCity().getId(),
                        () -> "should return event city node with id as null, but was: "
                                + eventNodeActual.getCity().getId()),
                () -> assertEquals(cityNodeExpected.getId(), eventNodeActual.getCity().getId(),
                        () -> "should return event node with city id: " + cityNodeExpected.getId()
                                + ", but was: " + eventNodeActual.getCity().getId()),
                () -> assertEquals(cityNodeExpected.getName(), eventNodeActual.getCity().getName(),
                        () -> "should return event node with city name: " + cityNodeExpected.getName()
                                + ", but was: " + eventNodeActual.getCity().getName()),
                () -> assertEquals(cityNodeExpected.getLatitude(), eventNodeActual.getCity().getLatitude(),
                        () -> "should return event node with city latitude: " + cityNodeExpected.getLatitude()
                                + ", but was: " + eventNodeActual.getCity().getLatitude()),
                () -> assertEquals(cityNodeExpected.getLongitude(), eventNodeActual.getCity().getLongitude(),
                        () -> "should return event node with city longitude: " + cityNodeExpected.getLongitude()
                                + ", but was: " + eventNodeActual.getCity().getLongitude()),

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
                () -> verify(modelMapper, times(1)).map(eventDTO, EventNode.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @Test
    void when_map_event_node_to_dto_should_return_dto() {

        CountryNode countryNode = (CountryNode) countryBuilder.build(ObjectType.NODE);
        TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNode).build(ObjectType.NODE);
        CityNode cityNode = (CityNode) cityBuilder.build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).withCity(cityNode).build(ObjectType.NODE);
        CountryDTO countryDTO = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTOExpected = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);

        when(modelMapper.map(eventNode, EventDTO.class)).thenReturn(eventDTOExpected);

        EventDTO eventDTOActual = objectMapper.map(eventNode, EventDTO.class);

        assertAll(
                () -> assertEquals(eventDTOExpected.getSummary(), eventDTOActual.getSummary(),
                        () -> "should return event dto with summary: " + eventDTOExpected.getSummary() + ", but was: "
                                + eventDTOActual.getSummary()),
                () -> assertEquals(eventDTOExpected.getMotive(), eventDTOActual.getMotive(),
                        () -> "should return event dto with motive: " + eventDTOExpected.getMotive() + ", but was: "
                                + eventDTOActual.getMotive()),
                () -> assertEquals(eventDTOExpected.getDate(), eventDTOActual.getDate(),
                        () -> "should return event dto with date: " + eventDTOExpected.getDate() + ", but was: "
                                + eventDTOActual.getDate()),
                () -> assertEquals(eventDTOExpected.getIsPartOfMultipleIncidents(),
                        eventDTOActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event dto which was part of multiple incidents: "
                                + eventDTOExpected.getIsPartOfMultipleIncidents() + ", but was: "
                                + eventDTOActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventDTOExpected.getIsSuccessful(), eventDTOActual.getIsSuccessful(),
                        () -> "should return event dto which was successful: " + eventDTOExpected.getIsSuccessful()
                                + ", but was: " + eventDTOActual.getIsSuccessful()),
                () -> assertEquals(eventDTOExpected.getIsSuicidal(), eventDTOActual.getIsSuicidal(),
                        () -> "should return event dto which was suicidal: " + eventDTOExpected.getIsSuicidal()
                                + ", but was: " + eventDTOActual.getIsSuicidal()),
                () -> assertNotNull(eventDTOActual.getTarget(),
                        () -> "should return event dto with not null target, but was: null"),
                () -> assertEquals(targetDTO.getTarget(), eventDTOActual.getTarget().getTarget(),
                        () -> "should return event dto with target: " + targetDTO.getTarget()
                                + ", but was: " + eventDTOActual.getTarget().getTarget()),
                () -> assertEquals(countryDTO.getName(), eventDTOActual.getTarget().getCountryOfOrigin().getName(),
                        () -> "should return event dto with country name: " + countryDTO.getName()
                                + ", but was: " + eventDTOActual.getTarget().getCountryOfOrigin()),

                () -> assertNotNull(eventDTOActual.getCity(),
                        () -> "should return event dto dto not null city, but was: null"),
                () -> assertEquals(cityDTO.getName(), eventDTOActual.getCity().getName(),
                        () -> "should return event dto with city name: " + cityDTO.getName()
                                + ", but was: " + eventDTOActual.getCity().getName()),
                () -> assertEquals(cityDTO.getLatitude(), eventDTOActual.getCity().getLatitude(),
                        () -> "should return event dto with city latitude: " + cityDTO.getLatitude()
                                + ", but was: " + eventDTOActual.getCity().getLatitude()),
                () -> assertEquals(cityDTO.getLongitude(), eventDTOActual.getCity().getLongitude(),
                        () -> "should return event dto with city longitude: " + cityDTO.getLongitude()
                                + ", but was: " + eventDTOActual.getCity().getLongitude()),

                () -> assertNotNull(eventDTOActual.getCity().getProvince(),
                        () -> "should return event dto with not null province, but was: null"),
                () -> assertEquals(provinceDTO, eventDTOActual.getCity().getProvince(),
                        () -> "should return event dto with province: " + provinceDTO + ", but was: "
                                + eventDTOActual.getCity().getProvince()),
                () -> assertEquals(provinceDTO.getName(), eventDTOActual.getCity().getProvince().getName(),
                        () -> "should return event dto with province name: " + provinceDTO.getName() + ", but was: "
                                + eventDTOActual.getCity().getProvince().getName()),
                () -> assertEquals(countryDTO, eventDTOActual.getCity().getProvince().getCountry(),
                        () -> "should return event dto with country: " + countryDTO + ", but was: " + eventDTOActual.getCity().getProvince().getCountry()),
                () -> assertEquals(countryDTO.getName(), eventDTOActual.getCity().getProvince().getCountry().getName(),
                        () -> "should return event dto with country name: " + countryDTO.getName()
                                + ", but was: " + eventDTOActual.getCity().getProvince().getCountry()),
                () -> verify(modelMapper, times(1)).map(eventNode, EventDTO.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @Test
    void when_map_event_node_to_model_should_return_model() {

        RegionNode regionNode = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNode = (CountryNode) countryBuilder.withRegion(regionNode).build(ObjectType.NODE);
        TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNode).build(ObjectType.NODE);
        ProvinceNode provinceNode = (ProvinceNode) provinceBuilder.withCountry(countryNode)
                .build(ObjectType.NODE);
        CityNode cityNode = (CityNode) cityBuilder.withProvince(provinceNode).build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).withCity(cityNode).build(ObjectType.NODE);
        RegionModel regionModel = (RegionModel) regionBuilder.build(ObjectType.MODEL);
        CountryModel countryModel = (CountryModel) countryBuilder.withRegion(regionModel).build(ObjectType.MODEL);
        TargetModel targetModel = (TargetModel) targetBuilder.withCountry(countryModel).build(ObjectType.MODEL);
        ProvinceModel provinceModel = (ProvinceModel) provinceBuilder.withCountry(countryModel)
                .build(ObjectType.MODEL);
        CityModel cityModel = (CityModel) cityBuilder.withProvince(provinceModel).build(ObjectType.MODEL);
        EventModel eventModelExpected = (EventModel) eventBuilder.withTarget(targetModel).withCity(cityModel)
                .build(ObjectType.MODEL);

        when(modelMapper.map(eventNode, EventModel.class)).thenReturn(eventModelExpected);

        EventModel eventModelActual = objectMapper.map(eventNode, EventModel.class);

        assertAll(
                () -> assertEquals(eventModelExpected.getId(), eventModelActual.getId(),
                        () -> "should return event model with id: " + eventModelExpected.getId() + ", but was: "
                                + eventModelActual.getId()),
                () -> assertEquals(eventModelExpected.getSummary(), eventModelActual.getSummary(),
                        () -> "should return event model with summary: " + eventModelExpected.getSummary() + ", but was: "
                                + eventModelActual.getSummary()),
                () -> assertEquals(eventModelExpected.getMotive(), eventModelActual.getMotive(),
                        () -> "should return event model with motive: " + eventModelExpected.getMotive() + ", but was: "
                                + eventModelActual.getMotive()),
                () -> assertEquals(eventModelExpected.getDate(), eventModelActual.getDate(),
                        () -> "should return event model with date: " + eventModelExpected.getDate() + ", but was: "
                                + eventModelActual.getDate()),
                () -> assertEquals(eventModelExpected.getIsPartOfMultipleIncidents(),
                        eventModelActual.getIsPartOfMultipleIncidents(),
                        () -> "should return event model which was part of multiple incidents: "
                                + eventModelExpected.getIsPartOfMultipleIncidents() + ", but was: "
                                + eventModelActual.getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventModelExpected.getIsSuccessful(), eventModelActual.getIsSuccessful(),
                        () -> "should return event model which was successful: " + eventModelExpected.getIsSuccessful()
                                + ", but was: " + eventModelActual.getIsSuccessful()),
                () -> assertEquals(eventModelExpected.getIsSuicidal(), eventModelActual.getIsSuicidal(),
                        () -> "should return event model which was suicidal: " + eventModelExpected.getIsSuicidal()
                                + ", but was: " + eventModelActual.getIsSuicidal()),
                () -> assertNotNull(eventModelActual.getTarget(),
                        () -> "should return event model with not null target, but was: null"),
                () -> assertEquals(targetModel.getId(), eventModelActual.getTarget().getId(),
                        () -> "should return event model target with id: " + targetModel.getId() + ", but was: "
                                + eventModelActual.getTarget().getId()),
                () -> assertEquals(targetModel.getTarget(), eventModelActual.getTarget().getTarget(),
                        () -> "should return event model with target: " + targetModel.getTarget()
                                + ", but was: " + eventModelActual.getTarget().getTarget()),
                () -> assertEquals(countryModel.getId(), eventModelActual.getTarget().getCountryOfOrigin().getId(),
                        () -> "should return event model with country id: " + countryModel.getId()
                                + ", but was: " + eventModelActual.getTarget().getId()),
                () -> assertEquals(countryModel.getName(), eventModelActual.getTarget().getCountryOfOrigin().getName(),
                        () -> "should return event model with country name: " + countryModel.getName()
                                + ", but was: " + eventModelActual.getTarget().getCountryOfOrigin()),
                () -> assertEquals(regionModel, eventModelActual.getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return event model with region: " + regionModel + ", but was: " + eventModelActual.getTarget().getCountryOfOrigin().getRegion()),
                () -> assertEquals(regionModel.getId(), eventModelActual.getTarget().getCountryOfOrigin().getRegion().getId(),
                        () -> "should return event model with region id: " + regionModel.getId()
                                + ", but was: " + eventModelActual.getTarget().getCountryOfOrigin().getRegion().getId()),
                () -> assertEquals(regionModel.getName(), eventModelActual.getTarget().getCountryOfOrigin().getRegion().getName(),
                        () -> "should return event model with region name: " + regionModel.getName()
                                + ", but was: " + eventModelActual.getTarget().getCountryOfOrigin().getRegion().getName()),

                () -> assertNotNull(eventModelActual.getCity(),
                        () -> "should return event model not null city, but was: null"),
                () -> assertEquals(cityModel.getId(), eventModelActual.getCity().getId(),
                        () -> "should return event model with city id: " + cityModel.getId()
                                + ", but was: " + eventModelActual.getCity().getId()),
                () -> assertEquals(cityModel.getName(), eventModelActual.getCity().getName(),
                        () -> "should return event model with city name: " + cityModel.getName()
                                + ", but was: " + eventModelActual.getCity().getName()),
                () -> assertEquals(cityModel.getLatitude(), eventModelActual.getCity().getLatitude(),
                        () -> "should return event model with city latitude: " + cityModel.getLatitude()
                                + ", but was: " + eventModelActual.getCity().getLatitude()),
                () -> assertEquals(cityModel.getLongitude(), eventModelActual.getCity().getLongitude(),
                        () -> "should return event model with city longitude: " + cityModel.getLongitude()
                                + ", but was: " + eventModelActual.getCity().getLongitude()),

                () -> assertNotNull(eventModelActual.getCity().getProvince(),
                        () -> "should return event model with not null province, but was: null"),
                () -> assertEquals(provinceModel, eventModelActual.getCity().getProvince(),
                        () -> "should return event model with province: " + provinceModel + ", but was: "
                                + eventModelActual.getCity().getProvince()),
                () -> assertEquals(provinceModel.getId(), eventModelActual.getCity().getProvince().getId(),
                        () -> "should return event model with province id: " + provinceModel.getId()
                                + ", but was: " + eventModelActual.getCity().getProvince().getId()),
                () -> assertEquals(provinceModel.getName(), eventModelActual.getCity().getProvince().getName(),
                        () -> "should return event model with province name: " + provinceModel.getName() + ", but was: "
                                + eventModelActual.getCity().getProvince().getName()),
                () -> assertEquals(countryModel, eventModelActual.getCity().getProvince().getCountry(),
                        () -> "should return event model with country: " + countryModel + ", but was: " + eventModelActual.getCity().getProvince().getCountry()),
                () -> assertEquals(countryModel.getId(), eventModelActual.getCity().getProvince().getCountry().getId(),
                        () -> "should return event model with country id: " + countryModel.getId()
                                + ", but was: " + eventModelActual.getCity().getProvince().getCountry().getId()),
                () -> assertEquals(countryModel.getName(), eventModelActual.getCity().getProvince().getCountry().getName(),
                        () -> "should return event model with country name: " + countryModel.getName()
                                + ", but was: " + eventModelActual.getCity().getProvince().getCountry()),
                () -> assertNotNull(eventModelActual.getCity().getProvince().getCountry().getRegion(),
                        () -> "should return event model with not null region, but was: null"),
                () -> assertEquals(regionModel, eventModelActual.getCity().getProvince().getCountry().getRegion(),
                        () -> "should return event model with region: " + regionModel + ", but was: "
                                + eventModelActual.getCity().getProvince().getCountry().getRegion()),
                () -> assertEquals(regionModel.getId(), eventModelActual.getCity().getProvince().getCountry().getRegion().getId(),
                        () -> "should return event model with region id: " + regionModel.getId()
                                + ", but was: " + eventModelActual.getCity().getProvince().getCountry().getRegion().getId()),
                () -> assertEquals(regionModel.getName(), eventModelActual.getCity().getProvince().getCountry().getRegion().getName(),
                        () -> "should return event model with region name: " + regionModel.getName() + ", but was: "
                                + eventModelActual.getCity().getProvince().getCountry().getRegion().getName()),
                () -> verify(modelMapper, times(1)).map(eventNode, EventModel.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }
}