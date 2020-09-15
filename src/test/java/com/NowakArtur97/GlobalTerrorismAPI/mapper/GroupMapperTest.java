package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.GroupDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.target.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.city.CityDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.city.CityModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.city.CityNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.event.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.event.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.event.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionNode;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.GroupModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.target.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.target.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.*;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("ObjectMapper_Tests")
class GroupMapperTest {

    private ObjectMapper objectMapper;

    @Mock
    private ModelMapper modelMapper;

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

        objectMapper = new ObjectMapperImpl(modelMapper);
    }

    @Test
    void when_map_group_node_to_model_should_return_model() {

        RegionNode regionNode = (RegionNode) regionBuilder.withId(1L).build(ObjectType.NODE);
        CountryNode countryNode = (CountryNode) countryBuilder.withId(2L).withRegion(regionNode).build(ObjectType.NODE);
        TargetNode targetNode = (TargetNode) targetBuilder.withId(3L).withCountry(countryNode).build(ObjectType.NODE);
        ProvinceNode provinceNode = (ProvinceNode) provinceBuilder.withId(4L).withCountry(countryNode)
                .build(ObjectType.NODE);
        CityNode cityNode = (CityNode) cityBuilder.withId(5L).withProvince(provinceNode).build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withId(6L).withTarget(targetNode).withCity(cityNode)
                .build(ObjectType.NODE);

        RegionNode regionNode2 = (RegionNode) regionBuilder.withId(7L).withName("region2").build(ObjectType.NODE);
        CountryNode countryNode2 = (CountryNode) countryBuilder.withId(8L).withName("country2").withRegion(regionNode2)
                .build(ObjectType.NODE);
        TargetNode targetNode2 = (TargetNode) targetBuilder.withId(9L).withTarget("target2").withCountry(countryNode2)
                .build(ObjectType.NODE);
        ProvinceNode provinceNode2 = (ProvinceNode) provinceBuilder.withId(10L).withName("province2").withCountry(countryNode2)
                .build(ObjectType.NODE);
        CityNode cityNode2 = (CityNode) cityBuilder.withId(11L).withName("city2").withProvince(provinceNode2).build(ObjectType.NODE);
        EventNode eventNode2 = (EventNode) eventBuilder.withId(12L).withSummary("summary2").withTarget(targetNode2)
                .withCity(cityNode2).build(ObjectType.NODE);

        GroupNode groupNode = (GroupNode) groupBuilder.withId(13L).withEventsCaused(List.of(eventNode, eventNode2))
                .build(ObjectType.NODE);

        RegionModel regionModelExpected = (RegionModel) regionBuilder.withId(1L).build(ObjectType.MODEL);
        CountryModel countryModelExpected = (CountryModel) countryBuilder.withId(2L).withRegion(regionModelExpected)
                .build(ObjectType.MODEL);
        TargetModel targetModelExpected = (TargetModel) targetBuilder.withId(3L).withCountry(countryModelExpected)
                .build(ObjectType.MODEL);
        ProvinceModel provinceModelExpected = (ProvinceModel) provinceBuilder.withId(4L).withCountry(countryModelExpected)
                .build(ObjectType.MODEL);
        CityModel cityModelExpected = (CityModel) cityBuilder.withId(5L).withProvince(provinceModelExpected).build(ObjectType.MODEL);
        EventModel eventModelExpected = (EventModel) eventBuilder.withId(6L).withTarget(targetModelExpected)
                .withCity(cityModelExpected).build(ObjectType.MODEL);

        RegionModel regionModelExpected2 = (RegionModel) regionBuilder.withId(7L).withName("region2").build(ObjectType.MODEL);
        CountryModel countryModelExpected2 = (CountryModel) countryBuilder.withId(8L).withName("country2")
                .withRegion(regionModelExpected2).build(ObjectType.MODEL);
        TargetModel targetModelExpected2 = (TargetModel) targetBuilder.withId(9L).withTarget("target2")
                .withCountry(countryModelExpected2).build(ObjectType.MODEL);
        ProvinceModel provinceModelExpected2 = (ProvinceModel) provinceBuilder.withId(10L).withName("province2")
                .withCountry(countryModelExpected2).build(ObjectType.MODEL);
        CityModel cityModelExpected2 = (CityModel) cityBuilder.withId(11L).withName("city2").withProvince(provinceModelExpected2)
                .build(ObjectType.MODEL);
        EventModel eventModelExpected2 = (EventModel) eventBuilder.withId(12L).withSummary("summary2")
                .withTarget(targetModelExpected2).withCity(cityModelExpected2).build(ObjectType.MODEL);

        GroupModel groupModelExpected = (GroupModel) groupBuilder.withId(13L)
                .withEventsCaused(List.of(eventModelExpected, eventModelExpected2))
                .build(ObjectType.MODEL);

        when(modelMapper.map(groupNode, GroupModel.class)).thenReturn(groupModelExpected);

        GroupModel groupModelActual = objectMapper.map(groupNode, GroupModel.class);

        assertAll(
                () -> assertNotNull(groupModelActual.getId(),
                        () -> "should return group model with id, but was: " + groupModelActual.getId()),
                () -> assertEquals(groupModelExpected.getName(), groupModelActual.getName(),
                        () -> "should return group model with name: " + groupModelExpected.getName() + ", but was: "
                                + groupModelActual.getName()),

                () -> assertEquals(eventModelExpected.getId(), groupModelActual.getEventsCaused().get(0).getId(), () -> "should return group event node with id: " + eventModelExpected.getId() + ", but was: "
                        + groupModelActual.getEventsCaused().get(0).getId()),
                () -> assertEquals(eventModelExpected.getSummary(), groupModelActual.getEventsCaused().get(0).getSummary(), () -> "should return group event node with summary: " + eventModelExpected.getSummary() + ", but was: " + groupModelActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(eventModelExpected.getMotive(), groupModelActual.getEventsCaused().get(0).getMotive(),
                        () -> "should return group event node with motive: " + eventModelExpected.getMotive() + ", but was: " + groupModelActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(eventModelExpected.getDate(), groupModelActual.getEventsCaused().get(0).getDate(),
                        () -> "should return group event node with date: " + eventModelExpected.getDate() +
                                ", but was: " + groupModelActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(eventModelExpected.getIsPartOfMultipleIncidents(),
                        groupModelActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        () -> "should return group event node which was part of multiple incidents: " + eventModelExpected.getIsPartOfMultipleIncidents() +
                                ", but was was: " + groupModelActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventModelExpected.getIsSuccessful(), groupModelActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group event node which was successful: " + eventModelExpected.getIsSuccessful() + ", but was: " + groupModelActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(eventModelExpected.getIsSuicidal(), groupModelActual.getEventsCaused().get(0).getIsSuicidal(), () -> "should return group event node which was suicidal: " + eventModelExpected.getIsSuicidal() + ", but was: " + groupModelActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupModelActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group model with event node with not null target, but was: null"),
                () -> assertEquals(targetModelExpected, groupModelActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group model with event target: " + groupModelActual.getEventsCaused().get(0).getTarget() + ", but was: "
                                + targetModelExpected),
                () -> assertEquals(targetModelExpected.getId(), groupModelActual.getEventsCaused().get(0).getTarget().getId(),
                        () -> "should return group model with event target id: " + groupModelActual.getEventsCaused().get(0).getTarget().getId() + ", but was: "
                                + targetModelExpected.getId()),
                () -> assertEquals(targetModelExpected.getTarget(), groupModelActual.getEventsCaused().get(0).getTarget().getTarget(),
                        () -> "should return group model with event target name: " + groupModelActual.getEventsCaused().get(0).getTarget().getTarget() + ", but was: "
                                + targetModelExpected.getTarget()),

                () -> assertEquals(countryModelExpected, groupModelActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin(),
                        () -> "should return group model with event node with country: " + countryModelExpected + ", but was: " + groupModelActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),
                () -> assertEquals(countryModelExpected.getId(), groupModelActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getId(),
                        () -> "should return group model with event node with country id: " + countryModelExpected.getId()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getTarget().getId()),
                () -> assertEquals(countryModelExpected.getName(), groupModelActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getName(),
                        () -> "should return group model with event node with country name: " + countryModelExpected.getName()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),
                () -> assertNotNull(groupModelActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return group model with not null region, but was: null"),
                () -> assertEquals(regionModelExpected, groupModelActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return group model with region: " + regionModelExpected + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getRegion()),
                () -> assertEquals(regionModelExpected.getId(), groupModelActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getRegion().getId(),
                        () -> "should return group model with region id: " + regionModelExpected.getId()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getRegion().getId()),
                () -> assertEquals(regionModelExpected.getName(), groupModelActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getRegion().getName(),
                        () -> "should return group model with region name: " + regionModelExpected.getName() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getRegion().getName()),

                () -> assertNotNull(groupModelActual.getEventsCaused().get(0).getCity(),
                        () -> "should return group model with event node with not null city, but was: null"),
                () -> assertEquals(cityModelExpected, groupModelActual.getEventsCaused().get(0).getCity(),
                        () -> "should return group model with event city: " + groupModelActual.getEventsCaused().get(0).getCity() + ", but was: "
                                + cityModelExpected),
                () -> assertEquals(cityModelExpected.getId(), groupModelActual.getEventsCaused().get(0).getCity().getId(),
                        () -> "should return group model with event city id: " + groupModelActual.getEventsCaused().get(0).getCity().getId() + ", but was: "
                                + cityModelExpected.getId()),
                () -> assertEquals(cityModelExpected.getName(), groupModelActual.getEventsCaused().get(0).getCity().getName(),
                        () -> "should return group model with event city name: " + groupModelActual.getEventsCaused().get(0).getCity().getName() + ", but was: "
                                + cityModelExpected.getName()),
                () -> assertEquals(cityModelExpected.getLatitude(), groupModelActual.getEventsCaused().get(0).getCity().getLatitude(),
                        () -> "should return group model with event city latitude: " + groupModelActual.getEventsCaused().get(0).getCity().getLatitude() + ", but was: "
                                + cityModelExpected.getLatitude()),
                () -> assertEquals(cityModelExpected.getLongitude(), groupModelActual.getEventsCaused().get(0).getCity().getLongitude(),
                        () -> "should return group model with event city longitude: " + groupModelActual.getEventsCaused().get(0).getCity().getLongitude() + ", but was: "
                                + cityModelExpected.getLongitude()),

                () -> assertNotNull(groupModelActual.getEventsCaused().get(0).getCity().getProvince(),
                        () -> "should return group model with not null province, but was: null"),
                () -> assertEquals(provinceModelExpected, groupModelActual.getEventsCaused().get(0).getCity().getProvince(),
                        () -> "should return group model with province: " + provinceModelExpected + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getCity().getProvince()),
                () -> assertEquals(provinceModelExpected.getId(), groupModelActual.getEventsCaused().get(0).getCity().getProvince().getId(),
                        () -> "should return group model with province id: " + provinceModelExpected.getId()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getCity().getProvince().getId()),
                () -> assertEquals(provinceModelExpected.getName(), groupModelActual.getEventsCaused().get(0).getCity().getProvince().getName(),
                        () -> "should return group model with province name: " + provinceModelExpected.getName() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getCity().getProvince().getName()),
                () -> assertEquals(countryModelExpected, groupModelActual.getEventsCaused().get(0).getCity().getProvince().getCountry(),
                        () -> "should return group model with country: " + countryModelExpected + ", but was: " + groupModelActual.getEventsCaused().get(0).getCity().getProvince().getCountry()),
                () -> assertEquals(countryModelExpected.getId(), groupModelActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getId(),
                        () -> "should return group model with country id: " + countryModelExpected.getId()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getId()),
                () -> assertEquals(countryModelExpected.getName(), groupModelActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getName(),
                        () -> "should return group model with country name: " + countryModelExpected.getName()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getCity().getProvince().getCountry()),
                () -> assertNotNull(groupModelActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getRegion(),
                        () -> "should return group model with not null region, but was: null"),
                () -> assertEquals(regionModelExpected, groupModelActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getRegion(),
                        () -> "should return group model with region: " + regionModelExpected + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getRegion()),
                () -> assertEquals(regionModelExpected.getId(), groupModelActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getRegion().getId(),
                        () -> "should return group model with region id: " + regionModelExpected.getId()
                                + ", but was: " + groupModelActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getRegion().getId()),
                () -> assertEquals(regionModelExpected.getName(), groupModelActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getRegion().getName(),
                        () -> "should return group model with region name: " + regionModelExpected.getName() + ", but was: "
                                + groupModelActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getRegion().getName()),

                () -> assertEquals(eventModelExpected2.getId(), groupModelActual.getEventsCaused().get(1).getId(), () -> "should return group event node with id: " + eventModelExpected2.getId() + ", but was: "
                        + groupModelActual.getEventsCaused().get(1).getId()),
                () -> assertEquals(eventModelExpected2.getSummary(), groupModelActual.getEventsCaused().get(1).getSummary(), () -> "should return group event node with summary: " + eventModelExpected2.getSummary() + ", but was: " + groupModelActual.getEventsCaused().get(1).getSummary()),
                () -> assertEquals(eventModelExpected2.getMotive(), groupModelActual.getEventsCaused().get(1).getMotive(), () -> "should return group event node with motive: " + eventModelExpected2.getMotive() + ", but was: " + groupModelActual.getEventsCaused().get(1).getMotive()),
                () -> assertEquals(eventModelExpected2.getDate(), groupModelActual.getEventsCaused().get(1).getDate(), () -> "should return group event node with date: " + eventModelExpected2.getDate() + ", but was: " + groupModelActual.getEventsCaused().get(1).getDate()),
                () -> assertEquals(eventModelExpected2.getIsPartOfMultipleIncidents(),
                        groupModelActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents(), () -> "should return group event node which was part of multiple incidents: " + eventModelExpected2.getIsPartOfMultipleIncidents() + ", but was was: " + groupModelActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventModelExpected2.getIsSuccessful(), groupModelActual.getEventsCaused().get(1).getIsSuccessful(), () -> "should return group event node which was successful: " + eventModelExpected2.getIsSuccessful() + ", but was: " + groupModelActual.getEventsCaused().get(1).getIsSuccessful()),
                () -> assertEquals(eventModelExpected2.getIsSuicidal(), groupModelActual.getEventsCaused().get(1).getIsSuicidal(), () -> "should return group event node which was suicidal: " + eventModelExpected2.getIsSuicidal() + ", but was: " + groupModelActual.getEventsCaused().get(1).getIsSuicidal()),
                () -> assertNotNull(targetModelExpected2,
                        () -> "should return group event node with not null target, but was: null"),
                () -> assertEquals(targetModelExpected2, groupModelActual.getEventsCaused().get(1).getTarget(), () -> "should return group event node with target: " + targetModelExpected2 + ", but was: " + groupModelActual.getEventsCaused().get(1).getTarget()),
                () -> assertNotNull(groupModelActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group model with event node with not null target, but was: null"),
                () -> assertEquals(targetModelExpected2, groupModelActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group model with event target: " + groupModelActual.getEventsCaused().get(1).getTarget() + ", but was: "
                                + targetModelExpected2),
                () -> assertEquals(targetModelExpected2.getId(), groupModelActual.getEventsCaused().get(1).getTarget().getId(),
                        () -> "should return group model with event target id: " + groupModelActual.getEventsCaused().get(1).getTarget().getId() + ", but was: "
                                + targetModelExpected2.getId()),
                () -> assertEquals(targetModelExpected2.getTarget(), groupModelActual.getEventsCaused().get(1).getTarget().getTarget(),
                        () -> "should return group model with event target name: " + groupModelActual.getEventsCaused().get(1).getTarget().getTarget() + ", but was: "
                                + targetModelExpected2.getTarget()),

                () -> assertEquals(countryModelExpected2, groupModelActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin(),
                        () -> "should return group model with event node with country: " + countryModelExpected2 + ", but was: " + groupModelActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin()),
                () -> assertEquals(countryModelExpected2.getId(), groupModelActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getId(),
                        () -> "should return group model with event node with country id: " + countryModelExpected2.getId()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getTarget().getId()),
                () -> assertEquals(countryModelExpected2.getName(), groupModelActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getName(),
                        () -> "should return group model with event node with country name: " + countryModelExpected2.getName()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin()),
                () -> assertNotNull(groupModelActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return group model with not null region, but was: null"),
                () -> assertEquals(regionModelExpected2, groupModelActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return group model with region: " + regionModelExpected2 + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getRegion()),
                () -> assertEquals(regionModelExpected2.getId(), groupModelActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getRegion().getId(),
                        () -> "should return group model with region id: " + regionModelExpected2.getId()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getRegion().getId()),
                () -> assertEquals(regionModelExpected2.getName(), groupModelActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getRegion().getName(),
                        () -> "should return group model with region name: " + regionModelExpected2.getName() + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getRegion().getName()),

                () -> assertNotNull(groupModelActual.getEventsCaused().get(1).getCity(),
                        () -> "should return group model with event node with not null city, but was: null"),
                () -> assertEquals(cityModelExpected2, groupModelActual.getEventsCaused().get(1).getCity(),
                        () -> "should return group model with event city: " + groupModelActual.getEventsCaused().get(1).getCity() + ", but was: "
                                + cityModelExpected2),
                () -> assertEquals(cityModelExpected2.getId(), groupModelActual.getEventsCaused().get(1).getCity().getId(),
                        () -> "should return group model with event city id: " + groupModelActual.getEventsCaused().get(1).getCity().getId() + ", but was: "
                                + cityModelExpected2.getId()),
                () -> assertEquals(cityModelExpected2.getName(), groupModelActual.getEventsCaused().get(1).getCity().getName(),
                        () -> "should return group model with event city name: " + groupModelActual.getEventsCaused().get(1).getCity().getName() + ", but was: "
                                + cityModelExpected2.getName()),
                () -> assertEquals(cityModelExpected2.getLatitude(), groupModelActual.getEventsCaused().get(1).getCity().getLatitude(),
                        () -> "should return group model with event city latitude: " + groupModelActual.getEventsCaused().get(1).getCity().getLatitude() + ", but was: "
                                + cityModelExpected2.getLatitude()),
                () -> assertEquals(cityModelExpected2.getLongitude(), groupModelActual.getEventsCaused().get(1).getCity().getLongitude(),
                        () -> "should return group model with event city longitude: " + groupModelActual.getEventsCaused().get(1).getCity().getLongitude() + ", but was: "
                                + cityModelExpected2.getLongitude()),

                () -> assertNotNull(groupModelActual.getEventsCaused().get(1).getCity().getProvince(),
                        () -> "should return group model with not null province, but was: null"),
                () -> assertEquals(provinceModelExpected2, groupModelActual.getEventsCaused().get(1).getCity().getProvince(),
                        () -> "should return group model with province: " + provinceModelExpected2 + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getCity().getProvince()),
                () -> assertEquals(provinceModelExpected2.getId(), groupModelActual.getEventsCaused().get(1).getCity().getProvince().getId(),
                        () -> "should return group model with province id: " + provinceModelExpected2.getId()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getCity().getProvince().getId()),
                () -> assertEquals(provinceModelExpected2.getName(), groupModelActual.getEventsCaused().get(1).getCity().getProvince().getName(),
                        () -> "should return group model with province name: " + provinceModelExpected2.getName() + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getCity().getProvince().getName()),
                () -> assertEquals(countryModelExpected2, groupModelActual.getEventsCaused().get(1).getCity().getProvince().getCountry(),
                        () -> "should return group model with country: " + countryModelExpected2 + ", but was: " + groupModelActual.getEventsCaused().get(1).getCity().getProvince().getCountry()),
                () -> assertEquals(countryModelExpected2.getId(), groupModelActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getId(),
                        () -> "should return group model with country id: " + countryModelExpected2.getId()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getId()),
                () -> assertEquals(countryModelExpected2.getName(), groupModelActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getName(),
                        () -> "should return group model with country name: " + countryModelExpected2.getName()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getCity().getProvince().getCountry()),
                () -> assertNotNull(groupModelActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getRegion(),
                        () -> "should return group model with not null region, but was: null"),
                () -> assertEquals(regionModelExpected2, groupModelActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getRegion(),
                        () -> "should return group model with region: " + regionModelExpected2 + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getRegion()),
                () -> assertEquals(regionModelExpected2.getId(), groupModelActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getRegion().getId(),
                        () -> "should return group model with region id: " + regionModelExpected2.getId()
                                + ", but was: " + groupModelActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getRegion().getId()),
                () -> assertEquals(regionModelExpected2.getName(), groupModelActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getRegion().getName(),
                        () -> "should return group model with region name: " + regionModelExpected2.getName() + ", but was: "
                                + groupModelActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getRegion().getName()),
                () -> verify(modelMapper, times(1)).map(groupNode, GroupModel.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @Test
    void when_map_group_dto_to_node_should_return_node() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO)
                .build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO)
                .build(ObjectType.DTO);

        CountryDTO countryDTO2 = (CountryDTO) countryBuilder.withName("country2").build(ObjectType.DTO);
        TargetDTO targetDTO2 = (TargetDTO) targetBuilder.withTarget("target2").withCountry(countryDTO2)
                .build(ObjectType.DTO);
        ProvinceDTO provinceDTO2 = (ProvinceDTO) provinceBuilder.withName("province2")
                .withCountry(countryDTO2).build(ObjectType.DTO);
        CityDTO cityDTO2 = (CityDTO) cityBuilder.withName("city2").withProvince(provinceDTO2)
                .build(ObjectType.DTO);
        EventDTO eventDTO2 = (EventDTO) eventBuilder.withSummary("summary2").withTarget(targetDTO2)
                .withCity(cityDTO2).build(ObjectType.DTO);

        GroupDTO groupDTO = (GroupDTO) groupBuilder
                .withEventsCaused(List.of(eventDTO, eventDTO2))
                .build(ObjectType.DTO);

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.withId(null).build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withId(null).withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withId(null).withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withId(null).withCountry(countryNodeExpected)
                .build(ObjectType.NODE);
        CityNode cityNodeExpected = (CityNode) cityBuilder.withId(null).withProvince(provinceNodeExpected).build(ObjectType.NODE);
        EventNode eventNodeExpected = (EventNode) eventBuilder.withId(null).withTarget(targetNodeExpected).withCity(cityNodeExpected)
                .build(ObjectType.NODE);

        RegionNode regionNodeExpected2 = (RegionNode) regionBuilder.withId(null).withName("region2").build(ObjectType.NODE);
        CountryNode countryNodeExpected2 = (CountryNode) countryBuilder.withId(null).withName("country2")
                .withRegion(regionNodeExpected2).build(ObjectType.NODE);
        TargetNode targetNodeExpected2 = (TargetNode) targetBuilder.withId(null).withTarget("target2")
                .withCountry(countryNodeExpected2).build(ObjectType.NODE);
        ProvinceNode provinceNodeExpected2 = (ProvinceNode) provinceBuilder.withId(null).withName("province2")
                .withCountry(countryNodeExpected2).build(ObjectType.NODE);
        CityNode cityNodeExpected2 = (CityNode) cityBuilder.withId(null).withName("city2").withProvince(provinceNodeExpected2)
                .build(ObjectType.NODE);
        EventNode eventNodeExpected2 = (EventNode) eventBuilder.withId(null).withSummary("summary2").withTarget(targetNodeExpected2)
                .withCity(cityNodeExpected2)
                .build(ObjectType.NODE);

        GroupNode groupNodeExpected = (GroupNode) groupBuilder.withId(null).
                withEventsCaused(List.of(eventNodeExpected, eventNodeExpected2))
                .build(ObjectType.NODE);

        when(modelMapper.map(groupDTO, GroupNode.class)).thenReturn(groupNodeExpected);

        GroupNode groupNodeActual = objectMapper.map(groupDTO, GroupNode.class);

        assertAll(
                () -> assertNull(groupNodeActual.getId(),
                        () -> "should return group node with id, but was: " + groupNodeActual.getId()),
                () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                        () -> "should return group node with name: " + groupNodeExpected.getName() + ", but was: "
                                + groupNodeActual.getName()),

                () -> assertEquals(eventNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getId(), () -> "should return group event node with id: " + eventNodeExpected.getId() + ", but was: "
                        + groupNodeActual.getEventsCaused().get(0).getId()),
                () -> assertEquals(eventNodeExpected.getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group event node with summary: " + eventNodeExpected.getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(eventNodeExpected.getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(),
                        () -> "should return group event node with motive: " + eventNodeExpected.getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(eventNodeExpected.getDate(), groupNodeActual.getEventsCaused().get(0).getDate(),
                        () -> "should return group event node with date: " + eventNodeExpected.getDate() +
                                ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(eventNodeExpected.getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        () -> "should return group event node which was part of multiple incidents: " + eventNodeExpected.getIsPartOfMultipleIncidents() +
                                ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
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
                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return group node with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected, groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return group node with region: " + regionNodeExpected + ", but was: "
                                + groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getRegion()),
                () -> assertEquals(regionNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getRegion().getId(),
                        () -> "should return group node with region id: " + regionNodeExpected.getId()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getRegion().getName(),
                        () -> "should return group node with region name: " + regionNodeExpected.getName() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getRegion().getName()),

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

                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getCity().getProvince(),
                        () -> "should return group node with not null province, but was: null"),
                () -> assertEquals(provinceNodeExpected, groupNodeActual.getEventsCaused().get(0).getCity().getProvince(),
                        () -> "should return group node with province: " + provinceNodeExpected + ", but was: "
                                + groupNodeActual.getEventsCaused().get(0).getCity().getProvince()),
                () -> assertEquals(provinceNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getId(),
                        () -> "should return group node with province id: " + provinceNodeExpected.getId()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getId()),
                () -> assertEquals(provinceNodeExpected.getName(), groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getName(),
                        () -> "should return group node with province name: " + provinceNodeExpected.getName() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getName()),
                () -> assertEquals(countryNodeExpected, groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry(),
                        () -> "should return group node with country: " + countryNodeExpected + ", but was: " + groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry()),
                () -> assertEquals(countryNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getId(),
                        () -> "should return group node with country id: " + countryNodeExpected.getId()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getId()),
                () -> assertEquals(countryNodeExpected.getName(), groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getName(),
                        () -> "should return group node with country name: " + countryNodeExpected.getName()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry()),
                () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getRegion(),
                        () -> "should return group node with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected, groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getRegion(),
                        () -> "should return group node with region: " + regionNodeExpected + ", but was: "
                                + groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getRegion()),
                () -> assertEquals(regionNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getRegion().getId(),
                        () -> "should return group node with region id: " + regionNodeExpected.getId()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getRegion().getName(),
                        () -> "should return group node with region name: " + regionNodeExpected.getName() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getRegion().getName()),

                () -> assertEquals(eventNodeExpected2.getId(), groupNodeActual.getEventsCaused().get(1).getId(), () -> "should return group event node with id: " + eventNodeExpected2.getId() + ", but was: "
                        + groupNodeActual.getEventsCaused().get(1).getId()),
                () -> assertEquals(eventNodeExpected2.getSummary(), groupNodeActual.getEventsCaused().get(1).getSummary(), () -> "should return group event node with summary: " + eventNodeExpected2.getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getSummary()),
                () -> assertEquals(eventNodeExpected2.getMotive(), groupNodeActual.getEventsCaused().get(1).getMotive(), () -> "should return group event node with motive: " + eventNodeExpected2.getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getMotive()),
                () -> assertEquals(eventNodeExpected2.getDate(), groupNodeActual.getEventsCaused().get(1).getDate(), () -> "should return group event node with date: " + eventNodeExpected2.getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getDate()),
                () -> assertEquals(eventNodeExpected2.getIsPartOfMultipleIncidents(),
                        groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents(), () -> "should return group event node which was part of multiple incidents: " + eventNodeExpected2.getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventNodeExpected2.getIsSuccessful(), groupNodeActual.getEventsCaused().get(1).getIsSuccessful(), () -> "should return group event node which was successful: " + eventNodeExpected2.getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuccessful()),
                () -> assertEquals(eventNodeExpected2.getIsSuicidal(), groupNodeActual.getEventsCaused().get(1).getIsSuicidal(), () -> "should return group event node which was suicidal: " + eventNodeExpected2.getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(1).getIsSuicidal()),
                () -> assertNotNull(targetNodeExpected2,
                        () -> "should return group event node with not null target, but was: null"),
                () -> assertEquals(targetNodeExpected2, groupNodeActual.getEventsCaused().get(1).getTarget(), () -> "should return group event node with target: " + targetNodeExpected2 + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget()),
                () -> assertNotNull(groupNodeActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group node with event node with not null target, but was: null"),
                () -> assertEquals(targetNodeExpected2, groupNodeActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group node with event target: " + groupNodeActual.getEventsCaused().get(1).getTarget() + ", but was: "
                                + targetNodeExpected2),
                () -> assertEquals(targetNodeExpected2.getId(), groupNodeActual.getEventsCaused().get(1).getTarget().getId(),
                        () -> "should return group node with event target id: " + groupNodeActual.getEventsCaused().get(1).getTarget().getId() + ", but was: "
                                + targetNodeExpected2.getId()),
                () -> assertEquals(targetNodeExpected2.getTarget(), groupNodeActual.getEventsCaused().get(1).getTarget().getTarget(),
                        () -> "should return group node with event target name: " + groupNodeActual.getEventsCaused().get(1).getTarget().getTarget() + ", but was: "
                                + targetNodeExpected2.getTarget()),

                () -> assertEquals(countryNodeExpected2, groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin(),
                        () -> "should return group node with event node with country: " + countryNodeExpected2 + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin()),
                () -> assertEquals(countryNodeExpected2.getId(), groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getId(),
                        () -> "should return group node with event node with country id: " + countryNodeExpected2.getId()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget().getId()),
                () -> assertEquals(countryNodeExpected2.getName(), groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getName(),
                        () -> "should return group node with event node with country name: " + countryNodeExpected2.getName()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin()),
                () -> assertNotNull(groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return group node with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected2, groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getRegion(),
                        () -> "should return group node with region: " + regionNodeExpected2 + ", but was: "
                                + groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getRegion()),
                () -> assertEquals(regionNodeExpected2.getId(), groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getRegion().getId(),
                        () -> "should return group node with region id: " + regionNodeExpected2.getId()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getRegion().getId()),
                () -> assertEquals(regionNodeExpected2.getName(), groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getRegion().getName(),
                        () -> "should return group node with region name: " + regionNodeExpected2.getName() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getRegion().getName()),

                () -> assertNotNull(groupNodeActual.getEventsCaused().get(1).getCity(),
                        () -> "should return group node with event node with not null city, but was: null"),
                () -> assertEquals(cityNodeExpected2, groupNodeActual.getEventsCaused().get(1).getCity(),
                        () -> "should return group node with event city: " + groupNodeActual.getEventsCaused().get(1).getCity() + ", but was: "
                                + cityNodeExpected2),
                () -> assertEquals(cityNodeExpected2.getId(), groupNodeActual.getEventsCaused().get(1).getCity().getId(),
                        () -> "should return group node with event city id: " + groupNodeActual.getEventsCaused().get(1).getCity().getId() + ", but was: "
                                + cityNodeExpected2.getId()),
                () -> assertEquals(cityNodeExpected2.getName(), groupNodeActual.getEventsCaused().get(1).getCity().getName(),
                        () -> "should return group node with event city name: " + groupNodeActual.getEventsCaused().get(1).getCity().getName() + ", but was: "
                                + cityNodeExpected2.getName()),
                () -> assertEquals(cityNodeExpected2.getLatitude(), groupNodeActual.getEventsCaused().get(1).getCity().getLatitude(),
                        () -> "should return group node with event city latitude: " + groupNodeActual.getEventsCaused().get(1).getCity().getLatitude() + ", but was: "
                                + cityNodeExpected2.getLatitude()),
                () -> assertEquals(cityNodeExpected2.getLongitude(), groupNodeActual.getEventsCaused().get(1).getCity().getLongitude(),
                        () -> "should return group node with event city longitude: " + groupNodeActual.getEventsCaused().get(1).getCity().getLongitude() + ", but was: "
                                + cityNodeExpected2.getLongitude()),

                () -> assertNotNull(groupNodeActual.getEventsCaused().get(1).getCity().getProvince(),
                        () -> "should return group node with not null province, but was: null"),
                () -> assertEquals(provinceNodeExpected2, groupNodeActual.getEventsCaused().get(1).getCity().getProvince(),
                        () -> "should return group node with province: " + provinceNodeExpected2 + ", but was: "
                                + groupNodeActual.getEventsCaused().get(1).getCity().getProvince()),
                () -> assertEquals(provinceNodeExpected2.getId(), groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getId(),
                        () -> "should return group node with province id: " + provinceNodeExpected2.getId()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getId()),
                () -> assertEquals(provinceNodeExpected2.getName(), groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getName(),
                        () -> "should return group node with province name: " + provinceNodeExpected2.getName() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getName()),
                () -> assertEquals(countryNodeExpected2, groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getCountry(),
                        () -> "should return group node with country: " + countryNodeExpected2 + ", but was: " + groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getCountry()),
                () -> assertEquals(countryNodeExpected2.getId(), groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getId(),
                        () -> "should return group node with country id: " + countryNodeExpected2.getId()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getId()),
                () -> assertEquals(countryNodeExpected2.getName(), groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getName(),
                        () -> "should return group node with country name: " + countryNodeExpected2.getName()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getCountry()),
                () -> assertNotNull(groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getRegion(),
                        () -> "should return group node with not null region, but was: null"),
                () -> assertEquals(regionNodeExpected2, groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getRegion(),
                        () -> "should return group node with region: " + regionNodeExpected2 + ", but was: "
                                + groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getRegion()),
                () -> assertEquals(regionNodeExpected2.getId(), groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getRegion().getId(),
                        () -> "should return group node with region id: " + regionNodeExpected2.getId()
                                + ", but was: " + groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getRegion().getId()),
                () -> assertEquals(regionNodeExpected2.getName(), groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getRegion().getName(),
                        () -> "should return group node with region name: " + regionNodeExpected2.getName() + ", but was: "
                                + groupNodeActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getRegion().getName()),
                () -> verify(modelMapper, times(1)).map(groupDTO, GroupNode.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @Test
    void when_map_group_node_to_dto_should_return_node() {

        RegionNode regionNode = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNode = (CountryNode) countryBuilder.withRegion(regionNode)
                .build(ObjectType.NODE);
        TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNode)
                .build(ObjectType.NODE);
        ProvinceNode provinceNode = (ProvinceNode) provinceBuilder.withCountry(countryNode)
                .build(ObjectType.NODE);
        CityNode cityNode = (CityNode) cityBuilder.withProvince(provinceNode).build(ObjectType.NODE);
        EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).withCity(cityNode)
                .build(ObjectType.NODE);

        RegionNode regionNode2 = (RegionNode) regionBuilder.withName("region2").build(ObjectType.NODE);
        CountryNode countryNode2 = (CountryNode) countryBuilder.withName("country2")
                .withRegion(regionNode2).build(ObjectType.NODE);
        TargetNode targetNode2 = (TargetNode) targetBuilder.withTarget("target2")
                .withCountry(countryNode2).build(ObjectType.NODE);
        ProvinceNode provinceNode2 = (ProvinceNode) provinceBuilder.withName("province2")
                .withCountry(countryNode2).build(ObjectType.NODE);
        CityNode cityNode2 = (CityNode) cityBuilder.withName("city2").withProvince(provinceNode2)
                .build(ObjectType.NODE);
        EventNode eventNode2 = (EventNode) eventBuilder.withSummary("summary2").withTarget(targetNode2)
                .withCity(cityNode2)
                .build(ObjectType.NODE);

        GroupNode groupNode = (GroupNode) groupBuilder.
                withEventsCaused(List.of(eventNode, eventNode2))
                .build(ObjectType.NODE);

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

        when(modelMapper.map(groupNode, GroupDTO.class)).thenReturn(groupDTOExpected);

        GroupDTO groupDTOActual = objectMapper.map(groupNode, GroupDTO.class);

        assertAll(
                () -> assertEquals(groupDTOExpected.getName(), groupDTOActual.getName(),
                        () -> "should return group dto with name: " + groupDTOExpected.getName() + ", but was: "
                                + groupDTOActual.getName()),

                () -> assertEquals(eventDTOExpected.getSummary(), groupDTOActual.getEventsCaused().get(0).getSummary(), () -> "should return group event dto with summary: " + eventDTOExpected.getSummary() + ", but was: " + groupDTOActual.getEventsCaused().get(0).getSummary()),
                () -> assertEquals(eventDTOExpected.getMotive(), groupDTOActual.getEventsCaused().get(0).getMotive(),
                        () -> "should return group event dto with motive: " + eventDTOExpected.getMotive() + ", but was: " + groupDTOActual.getEventsCaused().get(0).getMotive()),
                () -> assertEquals(eventDTOExpected.getDate(), groupDTOActual.getEventsCaused().get(0).getDate(),
                        () -> "should return group event dto with date: " + eventDTOExpected.getDate() +
                                ", but was: " + groupDTOActual.getEventsCaused().get(0).getDate()),
                () -> assertEquals(eventDTOExpected.getIsPartOfMultipleIncidents(),
                        groupDTOActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(),
                        () -> "should return group event dto which was part of multiple incidents: " + eventDTOExpected.getIsPartOfMultipleIncidents() +
                                ", but was was: " + groupDTOActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventDTOExpected.getIsSuccessful(), groupDTOActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group event dto which was successful: " + eventDTOExpected.getIsSuccessful() + ", but was: " + groupDTOActual.getEventsCaused().get(0).getIsSuccessful()),
                () -> assertEquals(eventDTOExpected.getIsSuicidal(), groupDTOActual.getEventsCaused().get(0).getIsSuicidal(), () -> "should return group event dto which was suicidal: " + eventDTOExpected.getIsSuicidal() + ", but was: " + groupDTOActual.getEventsCaused().get(0).getIsSuicidal()),
                () -> assertNotNull(groupDTOActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group dto with event dto with not null target, but was: null"),
                () -> assertEquals(targetDTOExpected, groupDTOActual.getEventsCaused().get(0).getTarget(),
                        () -> "should return group dto with event target: " + groupDTOActual.getEventsCaused().get(0).getTarget() + ", but was: "
                                + targetDTOExpected),
                () -> assertEquals(targetDTOExpected.getTarget(), groupDTOActual.getEventsCaused().get(0).getTarget().getTarget(),
                        () -> "should return group dto with event target name: " + groupDTOActual.getEventsCaused().get(0).getTarget().getTarget() + ", but was: "
                                + targetDTOExpected.getTarget()),

                () -> assertEquals(countryDTOExpected, groupDTOActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin(),
                        () -> "should return group dto with event dto with country: " + countryDTOExpected + ", but was: " + groupDTOActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),
                () -> assertEquals(countryDTOExpected.getName(), groupDTOActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getName(),
                        () -> "should return group dto with event dto with country name: " + countryDTOExpected.getName()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),

                () -> assertNotNull(groupDTOActual.getEventsCaused().get(0).getCity(),
                        () -> "should return group dto with event dto with not null city, but was: null"),
                () -> assertEquals(cityDTOExpected, groupDTOActual.getEventsCaused().get(0).getCity(),
                        () -> "should return group dto with event city: " + groupDTOActual.getEventsCaused().get(0).getCity() + ", but was: "
                                + cityDTOExpected),
                () -> assertEquals(cityDTOExpected.getName(), groupDTOActual.getEventsCaused().get(0).getCity().getName(),
                        () -> "should return group dto with event city name: " + groupDTOActual.getEventsCaused().get(0).getCity().getName() + ", but was: "
                                + cityDTOExpected.getName()),
                () -> assertEquals(cityDTOExpected.getLatitude(), groupDTOActual.getEventsCaused().get(0).getCity().getLatitude(),
                        () -> "should return group dto with event city latitude: " + groupDTOActual.getEventsCaused().get(0).getCity().getLatitude() + ", but was: "
                                + cityDTOExpected.getLatitude()),
                () -> assertEquals(cityDTOExpected.getLongitude(), groupDTOActual.getEventsCaused().get(0).getCity().getLongitude(),
                        () -> "should return group dto with event city longitude: " + groupDTOActual.getEventsCaused().get(0).getCity().getLongitude() + ", but was: "
                                + cityDTOExpected.getLongitude()),

                () -> assertNotNull(groupDTOActual.getEventsCaused().get(0).getCity().getProvince(),
                        () -> "should return group dto with not null province, but was: null"),
                () -> assertEquals(provinceDTOExpected, groupDTOActual.getEventsCaused().get(0).getCity().getProvince(),
                        () -> "should return group dto with province: " + provinceDTOExpected + ", but was: "
                                + groupDTOActual.getEventsCaused().get(0).getCity().getProvince()),
                () -> assertEquals(provinceDTOExpected.getName(), groupDTOActual.getEventsCaused().get(0).getCity().getProvince().getName(),
                        () -> "should return group dto with province name: " + provinceDTOExpected.getName() + ", but was: "
                                + groupDTOActual.getEventsCaused().get(0).getCity().getProvince().getName()),
                () -> assertEquals(countryDTOExpected, groupDTOActual.getEventsCaused().get(0).getCity().getProvince().getCountry(),
                        () -> "should return group dto with country: " + countryDTOExpected + ", but was: " + groupDTOActual.getEventsCaused().get(0).getCity().getProvince().getCountry()),
                () -> assertEquals(countryDTOExpected.getName(), groupDTOActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getName(),
                        () -> "should return group dto with country name: " + countryDTOExpected.getName()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(0).getCity().getProvince().getCountry()),

                () -> assertEquals(eventDTOExpected2.getSummary(), groupDTOActual.getEventsCaused().get(1).getSummary(), () -> "should return group event dto with summary: " + eventDTOExpected2.getSummary() + ", but was: " + groupDTOActual.getEventsCaused().get(1).getSummary()),
                () -> assertEquals(eventDTOExpected2.getMotive(), groupDTOActual.getEventsCaused().get(1).getMotive(), () -> "should return group event dto with motive: " + eventDTOExpected2.getMotive() + ", but was: " + groupDTOActual.getEventsCaused().get(1).getMotive()),
                () -> assertEquals(eventDTOExpected2.getDate(), groupDTOActual.getEventsCaused().get(1).getDate(), () -> "should return group event dto with date: " + eventDTOExpected2.getDate() + ", but was: " + groupDTOActual.getEventsCaused().get(1).getDate()),
                () -> assertEquals(eventDTOExpected2.getIsPartOfMultipleIncidents(),
                        groupDTOActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents(), () -> "should return group event dto which was part of multiple incidents: " + eventDTOExpected2.getIsPartOfMultipleIncidents() + ", but was was: " + groupDTOActual.getEventsCaused().get(1).getIsPartOfMultipleIncidents()),
                () -> assertEquals(eventDTOExpected2.getIsSuccessful(), groupDTOActual.getEventsCaused().get(1).getIsSuccessful(), () -> "should return group event dto which was successful: " + eventDTOExpected2.getIsSuccessful() + ", but was: " + groupDTOActual.getEventsCaused().get(1).getIsSuccessful()),
                () -> assertEquals(eventDTOExpected2.getIsSuicidal(), groupDTOActual.getEventsCaused().get(1).getIsSuicidal(), () -> "should return group event dto which was suicidal: " + eventDTOExpected2.getIsSuicidal() + ", but was: " + groupDTOActual.getEventsCaused().get(1).getIsSuicidal()),
                () -> assertNotNull(targetDTOExpected2,
                        () -> "should return group event dto with not null target, but was: null"),
                () -> assertEquals(targetDTOExpected2, groupDTOActual.getEventsCaused().get(1).getTarget(), () -> "should return group event dto with target: " + targetDTOExpected2 + ", but was: " + groupDTOActual.getEventsCaused().get(1).getTarget()),
                () -> assertNotNull(groupDTOActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group dto with event dto with not null target, but was: null"),
                () -> assertEquals(targetDTOExpected2, groupDTOActual.getEventsCaused().get(1).getTarget(),
                        () -> "should return group dto with event target: " + groupDTOActual.getEventsCaused().get(1).getTarget() + ", but was: "
                                + targetDTOExpected2),
                () -> assertEquals(targetDTOExpected2.getTarget(), groupDTOActual.getEventsCaused().get(1).getTarget().getTarget(),
                        () -> "should return group dto with event target name: " + groupDTOActual.getEventsCaused().get(1).getTarget().getTarget() + ", but was: "
                                + targetDTOExpected2.getTarget()),

                () -> assertEquals(countryDTOExpected2, groupDTOActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin(),
                        () -> "should return group dto with event dto with country: " + countryDTOExpected2 + ", but was: " + groupDTOActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin()),
                () -> assertEquals(countryDTOExpected2.getName(), groupDTOActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin().getName(),
                        () -> "should return group dto with event dto with country name: " + countryDTOExpected2.getName()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(1).getTarget().getCountryOfOrigin()),

                () -> assertNotNull(groupDTOActual.getEventsCaused().get(1).getCity(),
                        () -> "should return group dto with event dto with not null city, but was: null"),
                () -> assertEquals(cityDTOExpected2, groupDTOActual.getEventsCaused().get(1).getCity(),
                        () -> "should return group dto with event city: " + groupDTOActual.getEventsCaused().get(1).getCity() + ", but was: "
                                + cityDTOExpected2),
                () -> assertEquals(cityDTOExpected2.getName(), groupDTOActual.getEventsCaused().get(1).getCity().getName(),
                        () -> "should return group dto with event city name: " + groupDTOActual.getEventsCaused().get(1).getCity().getName() + ", but was: "
                                + cityDTOExpected2.getName()),
                () -> assertEquals(cityDTOExpected2.getLatitude(), groupDTOActual.getEventsCaused().get(1).getCity().getLatitude(),
                        () -> "should return group dto with event city latitude: " + groupDTOActual.getEventsCaused().get(1).getCity().getLatitude() + ", but was: "
                                + cityDTOExpected2.getLatitude()),
                () -> assertEquals(cityDTOExpected2.getLongitude(), groupDTOActual.getEventsCaused().get(1).getCity().getLongitude(),
                        () -> "should return group dto with event city longitude: " + groupDTOActual.getEventsCaused().get(1).getCity().getLongitude() + ", but was: "
                                + cityDTOExpected2.getLongitude()),

                () -> assertNotNull(groupDTOActual.getEventsCaused().get(1).getCity().getProvince(),
                        () -> "should return group dto with not null province, but was: null"),
                () -> assertEquals(provinceDTOExpected2, groupDTOActual.getEventsCaused().get(1).getCity().getProvince(),
                        () -> "should return group dto with province: " + provinceDTOExpected2 + ", but was: "
                                + groupDTOActual.getEventsCaused().get(1).getCity().getProvince()),
                () -> assertEquals(provinceDTOExpected2.getName(), groupDTOActual.getEventsCaused().get(1).getCity().getProvince().getName(),
                        () -> "should return group dto with province name: " + provinceDTOExpected2.getName() + ", but was: "
                                + groupDTOActual.getEventsCaused().get(1).getCity().getProvince().getName()),
                () -> assertEquals(countryDTOExpected2, groupDTOActual.getEventsCaused().get(1).getCity().getProvince().getCountry(),
                        () -> "should return group dto with country: " + countryDTOExpected2 + ", but was: " + groupDTOActual.getEventsCaused().get(1).getCity().getProvince().getCountry()),
                () -> assertEquals(countryDTOExpected2.getName(), groupDTOActual.getEventsCaused().get(1).getCity().getProvince().getCountry().getName(),
                        () -> "should return group dto with country name: " + countryDTOExpected2.getName()
                                + ", but was: " + groupDTOActual.getEventsCaused().get(1).getCity().getProvince().getCountry()),
                () -> verify(modelMapper, times(1)).map(groupNode, GroupDTO.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }
}