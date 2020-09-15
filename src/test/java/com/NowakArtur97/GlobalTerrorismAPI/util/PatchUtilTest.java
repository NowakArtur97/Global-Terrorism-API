package com.NowakArtur97.GlobalTerrorismAPI.util.patch;

import com.NowakArtur97.GlobalTerrorismAPI.feature.city.CityNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.event.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.group.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.target.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.*;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.json.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("PatchHelperImpl_Tests")
class PatchHelperImplTest {

    private PatchHelper patchHelper;

    @Mock
    private ObjectMapper objectMapper;

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

        patchHelper = new PatchHelperImpl(objectMapper);
    }

    @Nested
    @Tag("TargetPatch_Tests")
    class TargetPatchTest {

        @Test
        void when_patch_target_node_should_return_patched_target_node() {

            RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
            CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected).build(ObjectType.NODE);
            TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNodeExpected).build(ObjectType.NODE);

            String updatedTargetName = "updated target";
            TargetNode targetNodeExpected = (TargetNode) targetBuilder.withTarget(updatedTargetName).withCountry(countryNodeExpected)
                    .build(ObjectType.NODE);

            JsonPatch targetAsJsonPatch = Json.createPatchBuilder().replace("/target", updatedTargetName).build();

            JsonStructure target = Json.createObjectBuilder().add("target", updatedTargetName).build();

            JsonValue patched = targetAsJsonPatch.apply(target);

            when(objectMapper.convertValue(targetNode, JsonStructure.class)).thenReturn(target);
            when(objectMapper.convertValue(patched, TargetNode.class)).thenReturn(targetNodeExpected);

            TargetNode targetNodeActual = patchHelper.patch(targetAsJsonPatch, targetNode, TargetNode.class);

            assertAll(
                    () -> assertEquals(targetNodeExpected.getId(), targetNodeActual.getId(),
                            () -> "should return target node with id: " + targetNodeExpected.getId() + ", but was: "
                                    + targetNodeActual.getId()),
                    () -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
                            () -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: " + targetNodeActual.getTarget()),
                    () -> assertEquals(countryNodeExpected, targetNodeActual.getCountryOfOrigin(),
                            () -> "should return target node with country: " + countryNodeExpected + ", but was: " + targetNodeActual.getCountryOfOrigin()),
                    () -> assertEquals(countryNodeExpected.getId(), targetNodeActual.getCountryOfOrigin().getId(),
                            () -> "should return target node with country id: " + countryNodeExpected.getId()
                                    + ", but was: " + targetNodeActual.getId()),
                    () -> assertEquals(countryNodeExpected.getName(), targetNodeActual.getCountryOfOrigin().getName(),
                            () -> "should return target node with country name: " + countryNodeExpected.getName()
                                    + ", but was: " + targetNodeActual.getCountryOfOrigin()),
                    () -> assertEquals(regionNodeExpected, targetNodeActual.getCountryOfOrigin().getRegion(),
                            () -> "should return target node with region: " + regionNodeExpected + ", but was: " + targetNodeActual.getCountryOfOrigin().getRegion()),
                    () -> assertEquals(regionNodeExpected.getId(), targetNodeActual.getCountryOfOrigin().getRegion().getId(),
                            () -> "should return target node with region id: " + regionNodeExpected.getId()
                                    + ", but was: " + targetNodeActual.getCountryOfOrigin().getRegion().getId()),
                    () -> assertEquals(regionNodeExpected.getName(), targetNodeActual.getCountryOfOrigin().getRegion().getName(),
                            () -> "should return target node with region name: " + regionNodeExpected.getName()
                                    + ", but was: " + targetNodeActual.getCountryOfOrigin().getRegion().getName()),
                    () -> verify(objectMapper, times(1)).convertValue(targetNode, JsonStructure.class),
                    () -> verify(objectMapper, times(1)).convertValue(patched, TargetNode.class),
                    () -> verifyNoMoreInteractions(objectMapper));
        }

        @Test
        void when_patch_target_node_country_should_return_patched_target_node() {

            RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
            CountryNode countryNode = (CountryNode) countryBuilder.withRegion(regionNodeExpected).build(ObjectType.NODE);
            TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNode).build(ObjectType.NODE);

            String updatedCountryName = "updated country";
            CountryNode updatedCountryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected).withName(updatedCountryName)
                    .build(ObjectType.NODE);
            TargetNode targetNodeExpected = (TargetNode) targetBuilder.withCountry(updatedCountryNodeExpected)
                    .build(ObjectType.NODE);

            JsonPatch targetAsJsonPatch = Json.createPatchBuilder().replace("/countryOfOrigin/name", updatedCountryName)
                    .build();

            JsonStructure target = Json.createObjectBuilder()
                    .add("countryOfOrigin", Json.createObjectBuilder().add("name", updatedCountryName)).build();

            JsonValue patched = targetAsJsonPatch.apply(target);

            when(objectMapper.convertValue(targetNode, JsonStructure.class)).thenReturn(target);
            when(objectMapper.convertValue(patched, TargetNode.class)).thenReturn(targetNodeExpected);

            TargetNode targetNodeActual = patchHelper.patch(targetAsJsonPatch, targetNode, TargetNode.class);

            assertAll(
                    () -> assertEquals(targetNodeExpected.getId(), targetNodeActual.getId(),
                            () -> "should return target node with id: " + targetNodeExpected.getId() + ", but was: "
                                    + targetNodeActual.getId()),
                    () -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
                            () -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: " + targetNodeActual.getTarget()),
                    () -> assertEquals(updatedCountryNodeExpected, targetNodeActual.getCountryOfOrigin(),
                            () -> "should return target node with country: " + updatedCountryNodeExpected + ", but was: " + targetNodeActual.getCountryOfOrigin()),
                    () -> assertEquals(updatedCountryNodeExpected.getId(), targetNodeActual.getCountryOfOrigin().getId(),
                            () -> "should return target node with country id: " + updatedCountryNodeExpected.getId()
                                    + ", but was: " + targetNodeActual.getId()),
                    () -> assertEquals(updatedCountryNodeExpected.getName(), targetNodeActual.getCountryOfOrigin().getName(),
                            () -> "should return target node with country name: " + updatedCountryNodeExpected.getName()
                                    + ", but was: " + targetNodeActual.getCountryOfOrigin()),
                    () -> assertEquals(regionNodeExpected, targetNodeActual.getCountryOfOrigin().getRegion(),
                            () -> "should return target node with region: " + regionNodeExpected + ", but was: " + targetNodeActual.getCountryOfOrigin().getRegion()),
                    () -> assertEquals(regionNodeExpected.getId(), targetNodeActual.getCountryOfOrigin().getRegion().getId(),
                            () -> "should return target node with region id: " + regionNodeExpected.getId()
                                    + ", but was: " + targetNodeActual.getCountryOfOrigin().getRegion().getId()),
                    () -> assertEquals(regionNodeExpected.getName(), targetNodeActual.getCountryOfOrigin().getRegion().getName(),
                            () -> "should return target node with region name: " + regionNodeExpected.getName()
                                    + ", but was: " + targetNodeActual.getCountryOfOrigin().getRegion().getName()),
                    () -> verify(objectMapper, times(1)).convertValue(targetNode, JsonStructure.class),
                    () -> verify(objectMapper, times(1)).convertValue(patched, TargetNode.class),
                    () -> verifyNoMoreInteractions(objectMapper));
        }

        @Test
        void when_merge_patch_target_node_should_return_patched_target_node() {

            RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
            CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected).build(ObjectType.NODE);
            TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNodeExpected).build(ObjectType.NODE);

            String updatedTargetName = "updated target";
            TargetNode targetNodeExpected = (TargetNode) targetBuilder.withTarget(updatedTargetName)
                    .withCountry(countryNodeExpected).build(ObjectType.NODE);

            JsonMergePatch targetAsJsonMergePatch = Json
                    .createMergePatch(Json.createObjectBuilder().add("target", updatedTargetName).build());

            JsonValue target = Json.createObjectBuilder().add("target", updatedTargetName).build();

            JsonValue patched = targetAsJsonMergePatch.apply(target);

            when(objectMapper.convertValue(targetNode, JsonValue.class)).thenReturn(target);
            when(objectMapper.convertValue(patched, TargetNode.class)).thenReturn(targetNodeExpected);

            TargetNode targetNodeActual = patchHelper.mergePatch(targetAsJsonMergePatch, targetNode, TargetNode.class);

            assertAll(
                    () -> assertEquals(targetNodeExpected.getId(), targetNodeActual.getId(),
                            () -> "should return target node with id: " + targetNodeExpected.getId() + ", but was: "
                                    + targetNodeActual.getId()),
                    () -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
                            () -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: " + targetNodeActual.getTarget()),
                    () -> assertEquals(countryNodeExpected, targetNodeActual.getCountryOfOrigin(),
                            () -> "should return target node with country: " + countryNodeExpected + ", but was: " + targetNodeActual.getCountryOfOrigin()),
                    () -> assertEquals(countryNodeExpected.getId(), targetNodeActual.getCountryOfOrigin().getId(),
                            () -> "should return target node with country id: " + countryNodeExpected.getId()
                                    + ", but was: " + targetNodeActual.getId()),
                    () -> assertEquals(countryNodeExpected.getName(), targetNodeActual.getCountryOfOrigin().getName(),
                            () -> "should return target node with country name: " + countryNodeExpected.getName()
                                    + ", but was: " + targetNodeActual.getCountryOfOrigin()),
                    () -> assertEquals(regionNodeExpected, targetNodeActual.getCountryOfOrigin().getRegion(),
                            () -> "should return target node with region: " + regionNodeExpected + ", but was: " + targetNodeActual.getCountryOfOrigin().getRegion()),
                    () -> assertEquals(regionNodeExpected.getId(), targetNodeActual.getCountryOfOrigin().getRegion().getId(),
                            () -> "should return target node with region id: " + regionNodeExpected.getId()
                                    + ", but was: " + targetNodeActual.getCountryOfOrigin().getRegion().getId()),
                    () -> assertEquals(regionNodeExpected.getName(), targetNodeActual.getCountryOfOrigin().getRegion().getName(),
                            () -> "should return target node with region name: " + regionNodeExpected.getName()
                                    + ", but was: " + targetNodeActual.getCountryOfOrigin().getRegion().getName()),
                    () -> verify(objectMapper, times(1)).convertValue(targetNode, JsonValue.class),
                    () -> verify(objectMapper, times(1)).convertValue(patched, TargetNode.class),
                    () -> verifyNoMoreInteractions(objectMapper));
        }

        @Test
        void when_merge_patch_target_node_country_should_return_patched_target_node() {

            RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
            CountryNode countryNode = (CountryNode) countryBuilder.withRegion(regionNodeExpected).build(ObjectType.NODE);
            TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNode).build(ObjectType.NODE);

            String updatedCountryName = "updated country";
            CountryNode updatedCountryNodeExpected = (CountryNode) countryBuilder.withName(updatedCountryName)
                    .withRegion(regionNodeExpected).build(ObjectType.NODE);
            TargetNode targetNodeExpected = (TargetNode) targetBuilder.withCountry(updatedCountryNodeExpected)
                    .build(ObjectType.NODE);

            JsonMergePatch targetAsJsonMergePatch = Json
                    .createMergePatch(Json.createObjectBuilder().add("/countryOfOrigin/name", updatedCountryName)
                            .build());

            JsonStructure target = Json.createObjectBuilder()
                    .add("countryOfOrigin", Json.createObjectBuilder().add("name", updatedCountryName))
                    .build();

            JsonValue patched = targetAsJsonMergePatch.apply(target);

            when(objectMapper.convertValue(targetNode, JsonValue.class)).thenReturn(target);
            when(objectMapper.convertValue(patched, TargetNode.class)).thenReturn(targetNodeExpected);

            TargetNode targetNodeActual = patchHelper.mergePatch(targetAsJsonMergePatch, targetNode, TargetNode.class);

            assertAll(
                    () -> assertEquals(targetNodeExpected.getId(), targetNodeActual.getId(),
                            () -> "should return target node with id: " + targetNodeExpected.getId() + ", but was: "
                                    + targetNodeActual.getId()),
                    () -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
                            () -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: " + targetNodeActual.getTarget()),
                    () -> assertEquals(updatedCountryNodeExpected, targetNodeActual.getCountryOfOrigin(),
                            () -> "should return target node with country: " + updatedCountryNodeExpected + ", but was: " + targetNodeActual.getCountryOfOrigin()),
                    () -> assertEquals(updatedCountryNodeExpected.getId(), targetNodeActual.getCountryOfOrigin().getId(),
                            () -> "should return target node with country id: " + updatedCountryNodeExpected.getId()
                                    + ", but was: " + targetNodeActual.getId()),
                    () -> assertEquals(updatedCountryNodeExpected.getName(), targetNodeActual.getCountryOfOrigin().getName(),
                            () -> "should return target node with country name: " + updatedCountryNodeExpected.getName()
                                    + ", but was: " + targetNodeActual.getCountryOfOrigin()),
                    () -> assertEquals(regionNodeExpected, targetNodeActual.getCountryOfOrigin().getRegion(),
                            () -> "should return target node with region: " + regionNodeExpected + ", but was: " + targetNodeActual.getCountryOfOrigin().getRegion()),
                    () -> assertEquals(regionNodeExpected.getId(), targetNodeActual.getCountryOfOrigin().getRegion().getId(),
                            () -> "should return target node with region id: " + regionNodeExpected.getId()
                                    + ", but was: " + targetNodeActual.getCountryOfOrigin().getRegion().getId()),
                    () -> assertEquals(regionNodeExpected.getName(), targetNodeActual.getCountryOfOrigin().getRegion().getName(),
                            () -> "should return target node with region name: " + regionNodeExpected.getName()
                                    + ", but was: " + targetNodeActual.getCountryOfOrigin().getRegion().getName()),
                    () -> verify(objectMapper, times(1)).convertValue(targetNode, JsonValue.class),
                    () -> verify(objectMapper, times(1)).convertValue(patched, TargetNode.class),
                    () -> verifyNoMoreInteractions(objectMapper));
        }
    }

    @Nested
    @Tag("EventPatch_Tests")
    class EventPatchTest {

        @Test
        void when_patch_event_node_should_return_patched_event_node() throws ParseException {

            RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
            CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                    .build(ObjectType.NODE);
            TargetNode targetNodeExpected = (TargetNode) targetBuilder.withCountry(countryNodeExpected).build(ObjectType.NODE);
            ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                    .build(ObjectType.NODE);
            CityNode cityNodeExpected = (CityNode) cityBuilder.withProvince(provinceNodeExpected).build(ObjectType.NODE);

            Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2000-09-01");
            String updatedSummary = "updated summary";
            String updatedMotive = "updated motive";
            Date updatedDate = new SimpleDateFormat("yyyy-MM-dd").parse("2000-10-02");
            boolean updatedIsPartOfMultipleIncidents = false;
            boolean updatedIsSuccessful = false;
            boolean updatedIsSuicide = false;

            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNodeExpected).withCity(cityNodeExpected)
                    .build(ObjectType.NODE);

            EventNode eventNodeExpected = (EventNode) eventBuilder.withSummary(updatedSummary).withMotive(updatedMotive)
                    .withDate(updatedDate).withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents)
                    .withIsSuccessful(updatedIsSuccessful).withIsSuicidal(updatedIsSuicide).withTarget(targetNodeExpected)
                    .withCity(cityNodeExpected).build(ObjectType.NODE);

            JsonPatch eventAsJsonPatch = Json.createPatchBuilder().replace("/summary", updatedSummary)
                    .replace("/motive", updatedMotive).replace("/date", date.toString())
                    .replace("/isPartOfMultipleIncidents", updatedIsPartOfMultipleIncidents)
                    .replace("/isSuccessful", updatedIsSuccessful).replace("/isSuicidal", updatedIsSuicide).build();

            JsonStructure event = Json.createObjectBuilder().add("summary", updatedSummary).add("motive", updatedMotive)
                    .add("date", updatedDate.toString())
                    .add("isPartOfMultipleIncidents", updatedIsPartOfMultipleIncidents)
                    .add("isSuccessful", updatedIsSuccessful).add("isSuicidal", updatedIsSuicide).build();

            JsonValue patched = eventAsJsonPatch.apply(event);

            when(objectMapper.convertValue(eventNode, JsonStructure.class)).thenReturn(event);
            when(objectMapper.convertValue(patched, EventNode.class)).thenReturn(eventNodeExpected);

            EventNode eventNodeActual = patchHelper.patch(eventAsJsonPatch, eventNode, EventNode.class);

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
                    () -> verify(objectMapper, times(1)).convertValue(eventNode, JsonStructure.class),
                    () -> verify(objectMapper, times(1)).convertValue(patched, EventNode.class),
                    () -> verifyNoMoreInteractions(objectMapper));
        }

        @Test
        void when_patch_event_nodes_target_and_city_should_return_event_node_with_patched_target_and_city() {

            RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
            CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                    .build(ObjectType.NODE);
            TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNodeExpected).build(ObjectType.NODE);
            ProvinceNode provinceNode = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                    .build(ObjectType.NODE);
            CityNode cityNode = (CityNode) cityBuilder.withProvince(provinceNode).build(ObjectType.NODE);
            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).withCity(cityNode).build(ObjectType.NODE);

            String updatedCountryName = "updated country";
            String updatedProvinceName = "updated province";
            String updatedCityName = "updated city";
            double updatedCityLatitude = 12.0;
            double updatedCityLongitude = 12.0;
            String updatedTarget = "updated target";
            CountryNode updatedCountryNodeExpected = (CountryNode) countryBuilder.withName(updatedCountryName)
                    .withRegion(regionNodeExpected).build(ObjectType.NODE);
            TargetNode updatedTargetNodeExpected = (TargetNode) targetBuilder.withTarget(updatedTarget)
                    .withCountry(updatedCountryNodeExpected).build(ObjectType.NODE);
            ProvinceNode updatedProvinceNodeExpected = (ProvinceNode) provinceBuilder.withName(updatedProvinceName)
                    .withCountry(updatedCountryNodeExpected).build(ObjectType.NODE);
            CityNode updatedCityNodeExpected = (CityNode) cityBuilder.withName(updatedCityName)
                    .withLatitude(updatedCityLatitude).withLongitude(updatedCityLongitude)
                    .withProvince(updatedProvinceNodeExpected).build(ObjectType.NODE);
            EventNode eventNodeExpected = (EventNode) eventBuilder.withTarget(updatedTargetNodeExpected)
                    .withCity(updatedCityNodeExpected).build(ObjectType.NODE);

            JsonPatch eventAsJsonPatch = Json.createPatchBuilder()
                    .replace("/target/target", updatedTarget)
                    .replace("/target/countryOfOrigin/name", updatedCountryName)
                    .replace("/city/name", updatedCityName)
                    .replace("/city/latitude", String.valueOf(updatedCityLatitude))
                    .replace("/city/longitude", String.valueOf(updatedCityLongitude))
                    .replace("/city/province/name", updatedProvinceName)
                    .replace("/city/province/country/name", updatedCountryName)
                    .build();

            JsonStructure event = Json.createObjectBuilder()
                    .add("target", Json.createObjectBuilder().add("target", updatedTarget)
                            .add("countryOfOrigin", Json.createObjectBuilder().add("name", updatedCountryName)))
                    .add("city", Json.createObjectBuilder().add("name", updatedCityName)
                            .add("latitude", updatedCityLatitude)
                            .add("longitude", updatedCityLongitude)
                            .add("province", Json.createObjectBuilder().add("name", updatedProvinceName)
                                    .add("country", Json.createObjectBuilder().add("name", updatedCountryName))))
                    .build();

            JsonValue patched = eventAsJsonPatch.apply(event);

            when(objectMapper.convertValue(eventNode, JsonStructure.class)).thenReturn(event);
            when(objectMapper.convertValue(patched, EventNode.class)).thenReturn(eventNodeExpected);

            EventNode eventNodeActual = patchHelper.patch(eventAsJsonPatch, eventNode, EventNode.class);

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
                    () -> assertEquals(updatedTargetNodeExpected, eventNodeActual.getTarget(),
                            () -> "should return event node with target: " + updatedTargetNodeExpected + ", but was: "
                                    + eventNodeActual.getTarget()),
                    () -> assertEquals(updatedTargetNodeExpected.getId(), eventNodeActual.getTarget().getId(),
                            () -> "should return event node with target id: " + updatedTargetNodeExpected.getId() + ", but was: "
                                    + eventNodeActual.getTarget().getId()),
                    () -> assertEquals(updatedTargetNodeExpected.getTarget(), eventNodeActual.getTarget().getTarget(),
                            () -> "should return event node with target: " + updatedTargetNodeExpected.getTarget() + ", but was: "
                                    + eventNodeActual.getTarget().getTarget()),
                    () -> assertEquals(updatedCountryNodeExpected, eventNodeActual.getTarget().getCountryOfOrigin(),
                            () -> "should return event node with country: " + updatedCountryNodeExpected + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin()),
                    () -> assertEquals(updatedCountryNodeExpected.getId(), eventNodeActual.getTarget().getCountryOfOrigin().getId(),
                            () -> "should return event node with country id: " + countryNodeExpected.getId()
                                    + ", but was: " + eventNodeActual.getTarget().getId()),
                    () -> assertEquals(updatedCountryNodeExpected.getName(), eventNodeActual.getTarget().getCountryOfOrigin().getName(),
                            () -> "should return event node with country name: " + updatedCountryNodeExpected.getName()
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
                    () -> assertEquals(updatedCityNodeExpected, eventNodeActual.getCity(),
                            () -> "should return event node with city: " + updatedCityNodeExpected + ", but was: "
                                    + eventNodeActual.getCity()),
                    () -> assertEquals(updatedCityNodeExpected.getName(), eventNodeActual.getCity().getName(),
                            () -> "should return event node with city name: " + updatedCityNodeExpected.getName() + ", but was: "
                                    + eventNodeActual.getCity().getName()),
                    () -> assertEquals(updatedCityNodeExpected.getLatitude(), eventNodeActual.getCity().getLatitude(),
                            () -> "should return event node with city latitude: " + updatedCityNodeExpected.getLatitude() + ", but was: "
                                    + eventNodeActual.getCity().getLatitude()),
                    () -> assertEquals(updatedCityNodeExpected.getLongitude(), eventNodeActual.getCity().getLongitude(),
                            () -> "should return event node with city longitude: " + updatedCityNodeExpected.getLongitude() + ", but was: "
                                    + eventNodeActual.getCity().getLongitude()),

                    () -> assertNotNull(eventNodeActual.getCity().getProvince(),
                            () -> "should return event node with not null province, but was: null"),
                    () -> assertEquals(updatedProvinceNodeExpected, eventNodeActual.getCity().getProvince(),
                            () -> "should return event node with province: " + updatedProvinceNodeExpected + ", but was: "
                                    + eventNodeActual.getCity().getProvince()),
                    () -> assertEquals(updatedProvinceNodeExpected.getId(), eventNodeActual.getCity().getProvince().getId(),
                            () -> "should return event node with province id: " + updatedProvinceNodeExpected.getId()
                                    + ", but was: " + eventNodeActual.getCity().getProvince().getId()),
                    () -> assertEquals(updatedProvinceNodeExpected.getName(), eventNodeActual.getCity().getProvince().getName(),
                            () -> "should return event node with province name: " + updatedProvinceNodeExpected.getName() + ", but was: "
                                    + eventNodeActual.getCity().getProvince().getName()),
                    () -> assertEquals(updatedCountryNodeExpected, eventNodeActual.getCity().getProvince().getCountry(),
                            () -> "should return event node with country: " + updatedCountryNodeExpected + ", but was: " + eventNodeActual.getCity().getProvince().getCountry()),
                    () -> assertEquals(updatedCountryNodeExpected.getId(), eventNodeActual.getCity().getProvince().getCountry().getId(),
                            () -> "should return event node with country id: " + updatedCountryNodeExpected.getId()
                                    + ", but was: " + eventNodeActual.getCity().getProvince().getCountry().getId()),
                    () -> assertEquals(updatedCountryNodeExpected.getName(), eventNodeActual.getCity().getProvince().getCountry().getName(),
                            () -> "should return event node with country name: " + updatedCountryNodeExpected.getName()
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
                    () -> verify(objectMapper, times(1)).convertValue(eventNode, JsonStructure.class),
                    () -> verify(objectMapper, times(1)).convertValue(patched, EventNode.class),
                    () -> verifyNoMoreInteractions(objectMapper));
        }

        @Test
        void when_merge_patch_event_node_should_return_patched_event_node() throws ParseException {

            Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2000-09-01");
            RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
            CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                    .build(ObjectType.NODE);
            TargetNode targetNodeExpected = (TargetNode) targetBuilder.withCountry(countryNodeExpected).build(ObjectType.NODE);
            ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                    .build(ObjectType.NODE);
            CityNode cityNodeExpected = (CityNode) cityBuilder.withProvince(provinceNodeExpected).build(ObjectType.NODE);

            String updatedSummary = "updated summary";
            String updatedMotive = "updated motive";
            Date updatedDate = new SimpleDateFormat("yyyy-MM-dd").parse("2000-10-02");
            boolean updatedIsPartOfMultipleIncidents = false;
            boolean updatedIsSuccessful = false;
            boolean updatedIsSuicide = false;

            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNodeExpected).withCity(cityNodeExpected).build(ObjectType.NODE);

            EventNode updatedEventNodeExpected = (EventNode) eventBuilder.withSummary(updatedSummary).withMotive(updatedMotive)
                    .withDate(updatedDate).withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents)
                    .withIsSuccessful(updatedIsSuccessful).withIsSuicidal(updatedIsSuicide).withTarget(targetNodeExpected)
                    .withCity(cityNodeExpected).build(ObjectType.NODE);

            JsonMergePatch eventAsJsonMergePatch = Json.createMergePatch(Json.createObjectBuilder()
                    .add("summary", updatedSummary).add("motive", updatedMotive).add("date", date.toString())
                    .add("isPartOfMultipleIncidents", updatedIsPartOfMultipleIncidents)
                    .add("isSuccessful", updatedIsSuccessful).add("isSuicidal", updatedIsSuicide).build());

            JsonStructure event = Json.createObjectBuilder().add("summary", updatedSummary).add("motive", updatedMotive)
                    .add("date", updatedDate.toString())
                    .add("isPartOfMultipleIncidents", updatedIsPartOfMultipleIncidents)
                    .add("isSuccessful", updatedIsSuccessful).add("isSuicidal", updatedIsSuicide).build();

            JsonValue patched = eventAsJsonMergePatch.apply(event);

            when(objectMapper.convertValue(eventNode, JsonValue.class)).thenReturn(event);
            when(objectMapper.convertValue(patched, EventNode.class)).thenReturn(updatedEventNodeExpected);

            EventNode eventNodeActual = patchHelper.mergePatch(eventAsJsonMergePatch, eventNode, EventNode.class);

            assertAll(
                    () -> assertNotNull(eventNodeActual.getId(),
                            () -> "should return event node with id, but was: " + eventNodeActual.getId()),
                    () -> assertEquals(updatedEventNodeExpected.getSummary(), eventNodeActual.getSummary(),
                            () -> "should return event node with summary: " + updatedEventNodeExpected.getSummary() + ", but was: "
                                    + eventNodeActual.getSummary()),
                    () -> assertEquals(updatedEventNodeExpected.getMotive(), eventNodeActual.getMotive(),
                            () -> "should return event node with motive: " + updatedEventNodeExpected.getMotive() + ", but was: "
                                    + eventNodeActual.getMotive()),
                    () -> assertEquals(updatedEventNodeExpected.getDate(), eventNodeActual.getDate(),
                            () -> "should return event node with date: " + updatedEventNodeExpected.getDate() + ", but was: "
                                    + eventNodeActual.getDate()),
                    () -> assertEquals(updatedEventNodeExpected.getIsPartOfMultipleIncidents(),
                            eventNodeActual.getIsPartOfMultipleIncidents(),
                            () -> "should return event node which was part of multiple incidents: "
                                    + updatedEventNodeExpected.getIsPartOfMultipleIncidents() + ", but was was: "
                                    + eventNodeActual.getIsPartOfMultipleIncidents()),
                    () -> assertEquals(updatedEventNodeExpected.getIsSuccessful(), eventNodeActual.getIsSuccessful(),
                            () -> "should return event node which was successful: " + updatedEventNodeExpected.getIsSuccessful()
                                    + ", but was: " + eventNodeActual.getIsSuccessful()),
                    () -> assertEquals(updatedEventNodeExpected.getIsSuicidal(), eventNodeActual.getIsSuicidal(),
                            () -> "should return event node which was suicidal: " + updatedEventNodeExpected.getIsSuicidal()
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
                    () -> verify(objectMapper, times(1)).convertValue(eventNode, JsonValue.class),
                    () -> verify(objectMapper, times(1)).convertValue(patched, EventNode.class),
                    () -> verifyNoMoreInteractions(objectMapper));
        }

        @Test
        void when_merge_patch_event_nodes_target_and_city_should_return_event_node_with_patched_target_and_city() {

            RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
            CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                    .build(ObjectType.NODE);
            TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNodeExpected).build(ObjectType.NODE);
            ProvinceNode provinceNode = (ProvinceNode) provinceBuilder.withCountry(countryNodeExpected)
                    .build(ObjectType.NODE);
            CityNode cityNode = (CityNode) cityBuilder.withProvince(provinceNode).build(ObjectType.NODE);
            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).withCity(cityNode).build(ObjectType.NODE);

            String updatedCountryName = "updated country";
            String updatedProvinceName = "updated province";
            String updatedCityName = "updated city";
            double updatedCityLatitude = 12.0;
            double updatedCityLongitude = 12.0;
            String updatedTarget = "updated target";
            CountryNode updatedCountryNodeExpected = (CountryNode) countryBuilder.withName(updatedCountryName)
                    .withRegion(regionNodeExpected).build(ObjectType.NODE);
            TargetNode updatedTargetNodeExpected = (TargetNode) targetBuilder.withTarget(updatedTarget)
                    .withCountry(updatedCountryNodeExpected).build(ObjectType.NODE);
            ProvinceNode updatedProvinceNodeExpected = (ProvinceNode) provinceBuilder.withName(updatedProvinceName)
                    .withCountry(updatedCountryNodeExpected).build(ObjectType.NODE);
            CityNode updatedCityNodeExpected = (CityNode) cityBuilder.withName(updatedCityName)
                    .withLatitude(updatedCityLatitude).withLongitude(updatedCityLongitude)
                    .withProvince(updatedProvinceNodeExpected).build(ObjectType.NODE);
            EventNode eventNodeExpected = (EventNode) eventBuilder.withTarget(updatedTargetNodeExpected)
                    .withCity(updatedCityNodeExpected).build(ObjectType.NODE);

            JsonMergePatch eventAsJsonMergePatch = Json
                    .createMergePatch(
                            Json.createObjectBuilder()
                                    .add("/target/target", updatedTarget)
                                    .add("/target/countryOfOrigin/name", updatedCountryName)
                                    .add("/city/name", updatedCityName)
                                    .add("/city/latitude", updatedCityLatitude)
                                    .add("/city/longitude", updatedCityLongitude)
                                    .add("/city/province/name", updatedProvinceName)
                                    .add("/city/province/country/name", updatedCountryName)
                                    .build());

            JsonStructure event = Json.createObjectBuilder()
                    .add("target", Json.createObjectBuilder()
                            .add("target", updatedTarget)
                            .add("countryOfOrigin", Json.createObjectBuilder().add("name", updatedCountryName)))
                    .add("city", Json.createObjectBuilder()
                            .add("name", updatedCityName)
                            .add("latitude", updatedCityLatitude)
                            .add("longitude", updatedCityLongitude)
                            .add("province", Json.createObjectBuilder().add("name", updatedProvinceName)
                                    .add("country", Json.createObjectBuilder().add("name", updatedCountryName))))
                    .build();

            JsonValue patched = eventAsJsonMergePatch.apply(event);

            when(objectMapper.convertValue(eventNode, JsonValue.class)).thenReturn(event);
            when(objectMapper.convertValue(patched, EventNode.class)).thenReturn(eventNodeExpected);

            EventNode eventNodeActual = patchHelper.mergePatch(eventAsJsonMergePatch, eventNode, EventNode.class);

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
                    () -> assertEquals(updatedTargetNodeExpected, eventNodeActual.getTarget(),
                            () -> "should return event node with target: " + updatedTargetNodeExpected + ", but was: "
                                    + eventNodeActual.getTarget()),
                    () -> assertEquals(updatedTargetNodeExpected.getId(), eventNodeActual.getTarget().getId(),
                            () -> "should return event node with target id: " + updatedTargetNodeExpected.getId() + ", but was: "
                                    + eventNodeActual.getTarget().getId()),
                    () -> assertEquals(updatedTargetNodeExpected.getTarget(), eventNodeActual.getTarget().getTarget(),
                            () -> "should return event node with target: " + updatedTargetNodeExpected.getTarget() + ", but was: "
                                    + eventNodeActual.getTarget().getTarget()),
                    () -> assertEquals(updatedCountryNodeExpected, eventNodeActual.getTarget().getCountryOfOrigin(),
                            () -> "should return event node with country: " + updatedCountryNodeExpected + ", but was: " + eventNodeActual.getTarget().getCountryOfOrigin()),
                    () -> assertEquals(updatedCountryNodeExpected.getId(), eventNodeActual.getTarget().getCountryOfOrigin().getId(),
                            () -> "should return event node with country id: " + countryNodeExpected.getId()
                                    + ", but was: " + eventNodeActual.getTarget().getId()),
                    () -> assertEquals(updatedCountryNodeExpected.getName(), eventNodeActual.getTarget().getCountryOfOrigin().getName(),
                            () -> "should return event node with country name: " + updatedCountryNodeExpected.getName()
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
                    () -> assertEquals(updatedCityNodeExpected, eventNodeActual.getCity(),
                            () -> "should return event node with city: " + updatedCityNodeExpected + ", but was: "
                                    + eventNodeActual.getCity()),
                    () -> assertEquals(updatedCityNodeExpected.getName(), eventNodeActual.getCity().getName(),
                            () -> "should return event node with city name: " + updatedCityNodeExpected.getName() + ", but was: "
                                    + eventNodeActual.getCity().getName()),
                    () -> assertEquals(updatedCityNodeExpected.getLatitude(), eventNodeActual.getCity().getLatitude(),
                            () -> "should return event node with city latitude: " + updatedCityNodeExpected.getLatitude() + ", but was: "
                                    + eventNodeActual.getCity().getLatitude()),
                    () -> assertEquals(updatedCityNodeExpected.getLongitude(), eventNodeActual.getCity().getLongitude(),
                            () -> "should return event node with city longitude: " + updatedCityNodeExpected.getLongitude() + ", but was: "
                                    + eventNodeActual.getCity().getLongitude()),

                    () -> assertNotNull(eventNodeActual.getCity().getProvince(),
                            () -> "should return event node with not null province, but was: null"),
                    () -> assertEquals(updatedProvinceNodeExpected, eventNodeActual.getCity().getProvince(),
                            () -> "should return event node with province: " + updatedProvinceNodeExpected + ", but was: "
                                    + eventNodeActual.getCity().getProvince()),
                    () -> assertEquals(updatedProvinceNodeExpected.getId(), eventNodeActual.getCity().getProvince().getId(),
                            () -> "should return event node with province id: " + updatedProvinceNodeExpected.getId()
                                    + ", but was: " + eventNodeActual.getCity().getProvince().getId()),
                    () -> assertEquals(updatedProvinceNodeExpected.getName(), eventNodeActual.getCity().getProvince().getName(),
                            () -> "should return event node with province name: " + updatedProvinceNodeExpected.getName() + ", but was: "
                                    + eventNodeActual.getCity().getProvince().getName()),
                    () -> assertEquals(updatedCountryNodeExpected, eventNodeActual.getCity().getProvince().getCountry(),
                            () -> "should return event node with country: " + updatedCountryNodeExpected + ", but was: " + eventNodeActual.getCity().getProvince().getCountry()),
                    () -> assertEquals(updatedCountryNodeExpected.getId(), eventNodeActual.getCity().getProvince().getCountry().getId(),
                            () -> "should return event node with country id: " + updatedCountryNodeExpected.getId()
                                    + ", but was: " + eventNodeActual.getCity().getProvince().getCountry().getId()),
                    () -> assertEquals(updatedCountryNodeExpected.getName(), eventNodeActual.getCity().getProvince().getCountry().getName(),
                            () -> "should return event node with country name: " + updatedCountryNodeExpected.getName()
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
                    () -> verify(objectMapper, times(1)).convertValue(eventNode, JsonValue.class),
                    () -> verify(objectMapper, times(1)).convertValue(patched, EventNode.class),
                    () -> verifyNoMoreInteractions(objectMapper));
        }
    }

    @Nested
    @Tag("GroupPatch_Tests")
    class GroupPatchTest {

        @Test
        void when_patch_group_node_should_return_patched_group_node() {

            String updatedName = "updated group name";

            RegionNode regionNodeExpected = (RegionNode) regionBuilder.withId(null).build(ObjectType.NODE);
            CountryNode countryNodeExpected = (CountryNode) countryBuilder.withId(null).withRegion(regionNodeExpected)
                    .build(ObjectType.NODE);
            TargetNode targetNodeExpected = (TargetNode) targetBuilder.withId(null).withCountry(countryNodeExpected)
                    .build(ObjectType.NODE);
            ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withId(null).withCountry(countryNodeExpected)
                    .build(ObjectType.NODE);
            CityNode cityNodeExpected = (CityNode) cityBuilder.withId(null).withProvince(provinceNodeExpected)
                    .build(ObjectType.NODE);
            EventNode eventNodeExpected = (EventNode) eventBuilder.withId(null).withTarget(targetNodeExpected)
                    .withCity(cityNodeExpected).build(ObjectType.NODE);
            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNodeExpected)).build(ObjectType.NODE);
            GroupNode groupNodeExpected = (GroupNode) groupBuilder.withName(updatedName)
                    .withEventsCaused(List.of(eventNodeExpected))
                    .build(ObjectType.NODE);

            JsonPatch groupAsJsonPatch = Json.createPatchBuilder().replace("/name", updatedName).build();

            JsonStructure group = Json.createObjectBuilder().add("name", updatedName).build();

            JsonValue patched = groupAsJsonPatch.apply(group);

            when(objectMapper.convertValue(groupNode, JsonStructure.class)).thenReturn(group);
            when(objectMapper.convertValue(patched, GroupNode.class)).thenReturn(groupNodeExpected);

            GroupNode groupNodeActual = patchHelper.patch(groupAsJsonPatch, groupNode, GroupNode.class);

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
                    () -> verify(objectMapper, times(1)).convertValue(groupNode, JsonStructure.class),
                    () -> verify(objectMapper, times(1)).convertValue(patched, GroupNode.class),
                    () -> verifyNoMoreInteractions(objectMapper));
        }

        @Test
        void when_patch_group_node_events_should_return_group_node_with_patched_events() throws ParseException {

            RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
            CountryNode countryNode = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                    .build(ObjectType.NODE);
            TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNode).build(ObjectType.NODE);
            ProvinceNode provinceNode = (ProvinceNode) provinceBuilder.withCountry(countryNode)
                    .build(ObjectType.NODE);
            CityNode cityNode = (CityNode) cityBuilder.withProvince(provinceNode).build(ObjectType.NODE);

            Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2000-09-01");
            String updatedSummary = "updated summary";
            String updatedMotive = "updated motive";
            Date updatedDate = new SimpleDateFormat("yyyy-MM-dd").parse("2000-10-02");
            boolean updatedIsPartOfMultipleIncidents = false;
            boolean updatedIsSuccessful = false;
            boolean updatedIsSuicide = false;

            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).withCity(cityNode)
                    .build(ObjectType.NODE);

            String updatedCountryName = "updated country";
            String updatedProvinceName = "updated province";
            String updatedCityName = "updated city";
            double updatedCityLatitude = 12.0;
            double updatedCityLongitude = 12.0;
            String updatedTarget = "updated target";
            CountryNode updatedCountryNodeExpected = (CountryNode) countryBuilder.withName(updatedCountryName)
                    .withRegion(regionNodeExpected).build(ObjectType.NODE);
            TargetNode updatedTargetNodeExpected = (TargetNode) targetBuilder.withTarget(updatedTarget)
                    .withCountry(updatedCountryNodeExpected).build(ObjectType.NODE);
            ProvinceNode updatedProvinceNodeExpected = (ProvinceNode) provinceBuilder.withName(updatedProvinceName)
                    .withCountry(updatedCountryNodeExpected).build(ObjectType.NODE);
            CityNode updatedCityNodeExpected = (CityNode) cityBuilder.withName(updatedCityName)
                    .withLatitude(updatedCityLatitude).withLongitude(updatedCityLongitude)
                    .withProvince(updatedProvinceNodeExpected).build(ObjectType.NODE);
            EventNode updatedEventNodeExpected = (EventNode) eventBuilder.withSummary(updatedSummary).withMotive(updatedMotive)
                    .withDate(updatedDate).withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents)
                    .withIsSuccessful(updatedIsSuccessful).withIsSuicidal(updatedIsSuicide)
                    .withTarget(updatedTargetNodeExpected).withCity(updatedCityNodeExpected).build(ObjectType.NODE);

            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
            GroupNode groupNodeExpected = (GroupNode) groupBuilder.withEventsCaused(List.of(updatedEventNodeExpected))
                    .build(ObjectType.NODE);

            JsonPatch groupAsJsonPatch = Json.createPatchBuilder()
                    .replace("/eventsCaused[0]/summary", updatedSummary)
                    .replace("/eventsCaused[0]/motive", updatedMotive)
                    .replace("/eventsCaused[0]/date", date.toString())
                    .replace("/eventsCaused[0]/isPartOfMultipleIncidents", updatedIsPartOfMultipleIncidents)
                    .replace("/eventsCaused[0]/isSuccessful", updatedIsSuccessful)
                    .replace("/eventsCaused[0]/isSuicidal", updatedIsSuicide)
                    .replace("/eventsCaused[0]/target/target", updatedTarget)
                    .replace("/eventsCaused[0]/target/countryOfOrigin/name", updatedCountryName)
                    .replace("/eventsCaused[0]/city/name", updatedCityName)
                    .replace("/eventsCaused[0]/city/latitude", String.valueOf(updatedCityLatitude))
                    .replace("/eventsCaused[0]/city/longitude", String.valueOf(updatedCityLongitude))
                    .replace("/eventsCaused[0]/city/province/name", updatedProvinceName)
                    .replace("/eventsCaused[0]/city/province/country/name", updatedCountryName)
                    .build();

            JsonStructure group = Json.createObjectBuilder()
                    .add("eventsCaused[0]",
                            Json.createObjectBuilder()
                                    .add("summary", updatedSummary).add("motive", updatedMotive)
                                    .add("date", updatedDate.toString())
                                    .add("isPartOfMultipleIncidents", updatedIsPartOfMultipleIncidents)
                                    .add("isSuccessful", updatedIsSuccessful)
                                    .add("isSuicidal", updatedIsSuicide)
                                    .add("target", Json.createObjectBuilder().add("target", updatedTarget)
                                            .add("countryOfOrigin",
                                                    Json.createObjectBuilder().add("name", updatedCountryName)))
                                    .add("city", Json.createObjectBuilder()
                                            .add("name", updatedCityName)
                                            .add("latitude", updatedCityLatitude)
                                            .add("longitude", updatedCityLongitude)
                                            .add("province", Json.createObjectBuilder().add("name", updatedProvinceName)
                                                    .add("country",
                                                            Json.createObjectBuilder().add("name", updatedCountryName))))
                    )
                    .build();

            JsonValue patched = groupAsJsonPatch.apply(group);

            when(objectMapper.convertValue(groupNode, JsonStructure.class)).thenReturn(group);
            when(objectMapper.convertValue(patched, GroupNode.class)).thenReturn(groupNodeExpected);

            GroupNode groupNodeActual = patchHelper.patch(groupAsJsonPatch, groupNode, GroupNode.class);

            assertAll(
                    () -> assertNotNull(groupNodeActual.getId(),
                            () -> "should return group node with id, but was: " + groupNodeActual.getId()),
                    () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                            () -> "should return group node with name: " + groupNodeExpected.getName() + ", but was: "
                                    + groupNodeActual.getName()),

                    () -> assertEquals(updatedEventNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getId(), () -> "should return group event node with id: " + updatedEventNodeExpected.getId() + ", but was: "
                            + groupNodeActual.getEventsCaused().get(0).getId()),
                    () -> assertEquals(updatedEventNodeExpected.getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group event node with summary: " + updatedEventNodeExpected.getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                    () -> assertEquals(updatedEventNodeExpected.getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(), () -> "should return group event node with motive: " + updatedEventNodeExpected.getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                    () -> assertEquals(updatedEventNodeExpected.getDate(), groupNodeActual.getEventsCaused().get(0).getDate(), () -> "should return group event node with date: " + updatedEventNodeExpected.getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                    () -> assertEquals(updatedEventNodeExpected.getIsPartOfMultipleIncidents(),
                            groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(), () -> "should return group event node which was part of multiple incidents: " + updatedEventNodeExpected.getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                    () -> assertEquals(updatedEventNodeExpected.getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group event node which was successful: " + updatedEventNodeExpected.getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                    () -> assertEquals(updatedEventNodeExpected.getIsSuicidal(), groupNodeActual.getEventsCaused().get(0).getIsSuicidal(), () -> "should return group event node which was suicidal: " + updatedEventNodeExpected.getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicidal()),
                    () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getTarget(),
                            () -> "should return group node with event node with not null target, but was: null"),
                    () -> assertEquals(updatedTargetNodeExpected, groupNodeActual.getEventsCaused().get(0).getTarget(),
                            () -> "should return group node with event target: " + groupNodeActual.getEventsCaused().get(0).getTarget() + ", but was: "
                                    + updatedTargetNodeExpected),
                    () -> assertEquals(updatedTargetNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getTarget().getId(),
                            () -> "should return group node with event target id: " + groupNodeActual.getEventsCaused().get(0).getTarget().getId() + ", but was: "
                                    + updatedTargetNodeExpected.getId()),
                    () -> assertEquals(updatedTargetNodeExpected.getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget().getTarget(),
                            () -> "should return group node with event target name: " + groupNodeActual.getEventsCaused().get(0).getTarget().getTarget() + ", but was: "
                                    + updatedTargetNodeExpected.getTarget()),

                    () -> assertEquals(updatedCountryNodeExpected, groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin(),
                            () -> "should return group node with event node with country: " + updatedCountryNodeExpected + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),
                    () -> assertEquals(updatedCountryNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getId(),
                            () -> "should return group node with event node with country id: " + updatedCountryNodeExpected.getId()
                                    + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getId()),
                    () -> assertEquals(updatedCountryNodeExpected.getName(), groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getName(),
                            () -> "should return group node with event node with country name: " + updatedCountryNodeExpected.getName()
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
                    () -> assertEquals(updatedCityNodeExpected, groupNodeActual.getEventsCaused().get(0).getCity(),
                            () -> "should return group node with event city: " + groupNodeActual.getEventsCaused().get(0).getCity() + ", but was: "
                                    + updatedCityNodeExpected),
                    () -> assertEquals(updatedCityNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getCity().getId(),
                            () -> "should return group node with event city id: " + groupNodeActual.getEventsCaused().get(0).getCity().getId() + ", but was: "
                                    + updatedCityNodeExpected.getId()),
                    () -> assertEquals(updatedCityNodeExpected.getName(), groupNodeActual.getEventsCaused().get(0).getCity().getName(),
                            () -> "should return group node with event city name: " + groupNodeActual.getEventsCaused().get(0).getCity().getName() + ", but was: "
                                    + updatedCityNodeExpected.getName()),
                    () -> assertEquals(updatedCityNodeExpected.getLatitude(), groupNodeActual.getEventsCaused().get(0).getCity().getLatitude(),
                            () -> "should return group node with event city latitude: " + groupNodeActual.getEventsCaused().get(0).getCity().getLatitude() + ", but was: "
                                    + updatedCityNodeExpected.getLatitude()),
                    () -> assertEquals(updatedCityNodeExpected.getLongitude(), groupNodeActual.getEventsCaused().get(0).getCity().getLongitude(),
                            () -> "should return group node with event city longitude: " + groupNodeActual.getEventsCaused().get(0).getCity().getLongitude() + ", but was: "
                                    + updatedCityNodeExpected.getLongitude()),

                    () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getCity().getProvince(),
                            () -> "should return group node with not null province, but was: null"),
                    () -> assertEquals(updatedProvinceNodeExpected, groupNodeActual.getEventsCaused().get(0).getCity().getProvince(),
                            () -> "should return group node with province: " + updatedProvinceNodeExpected + ", but was: "
                                    + groupNodeActual.getEventsCaused().get(0).getCity().getProvince()),
                    () -> assertEquals(updatedProvinceNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getId(),
                            () -> "should return group node with province id: " + updatedProvinceNodeExpected.getId()
                                    + ", but was: " + groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getId()),
                    () -> assertEquals(updatedProvinceNodeExpected.getName(), groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getName(),
                            () -> "should return group node with province name: " + updatedProvinceNodeExpected.getName() + ", but was: "
                                    + groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getName()),
                    () -> assertEquals(updatedCountryNodeExpected, groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry(),
                            () -> "should return group node with country: " + updatedCountryNodeExpected + ", but was: " + groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry()),
                    () -> assertEquals(updatedCountryNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getId(),
                            () -> "should return group node with country id: " + updatedCountryNodeExpected.getId()
                                    + ", but was: " + groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getId()),
                    () -> assertEquals(updatedCountryNodeExpected.getName(), groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getName(),
                            () -> "should return group node with country name: " + updatedCountryNodeExpected.getName()
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
                    () -> verify(objectMapper, times(1)).convertValue(groupNode, JsonStructure.class),
                    () -> verify(objectMapper, times(1)).convertValue(patched, GroupNode.class),
                    () -> verifyNoMoreInteractions(objectMapper));
        }

        @Test
        void when_merge_patch_group_node_should_return_patched_group_node() {

            String updatedName = "updated group name";

            RegionNode regionNodeExpected = (RegionNode) regionBuilder.withId(null).build(ObjectType.NODE);
            CountryNode countryNodeExpected = (CountryNode) countryBuilder.withId(null).withRegion(regionNodeExpected)
                    .build(ObjectType.NODE);
            TargetNode targetNodeExpected = (TargetNode) targetBuilder.withId(null).withCountry(countryNodeExpected)
                    .build(ObjectType.NODE);
            ProvinceNode provinceNodeExpected = (ProvinceNode) provinceBuilder.withId(null).withCountry(countryNodeExpected)
                    .build(ObjectType.NODE);
            CityNode cityNodeExpected = (CityNode) cityBuilder.withId(null).withProvince(provinceNodeExpected)
                    .build(ObjectType.NODE);
            EventNode eventNodeExpected = (EventNode) eventBuilder.withId(null).withTarget(targetNodeExpected)
                    .withCity(cityNodeExpected).build(ObjectType.NODE);
            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNodeExpected)).build(ObjectType.NODE);
            GroupNode groupNodeExpected = (GroupNode) groupBuilder.withName(updatedName)
                    .withEventsCaused(List.of(eventNodeExpected))
                    .build(ObjectType.NODE);

            JsonMergePatch groupAsJsonMergePatch = Json.createMergePatch(Json.createObjectBuilder()
                    .add("name", updatedName).build());

            JsonStructure group = Json.createObjectBuilder().add("name", updatedName).build();

            JsonValue patched = groupAsJsonMergePatch.apply(group);

            when(objectMapper.convertValue(groupNode, JsonValue.class)).thenReturn(group);
            when(objectMapper.convertValue(patched, GroupNode.class)).thenReturn(groupNodeExpected);

            GroupNode groupNodeActual = patchHelper.mergePatch(groupAsJsonMergePatch, groupNode, GroupNode.class);

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
                    () -> verify(objectMapper, times(1)).convertValue(groupNode, JsonValue.class),
                    () -> verify(objectMapper, times(1)).convertValue(patched, GroupNode.class),
                    () -> verifyNoMoreInteractions(objectMapper));
        }

        @Test
        void when_merge_patch_group_node_events_should_return_group_node_with_patched_events() throws ParseException {

            RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
            CountryNode countryNode = (CountryNode) countryBuilder.withRegion(regionNodeExpected)
                    .build(ObjectType.NODE);
            TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNode).build(ObjectType.NODE);
            ProvinceNode provinceNode = (ProvinceNode) provinceBuilder.withCountry(countryNode)
                    .build(ObjectType.NODE);
            CityNode cityNode = (CityNode) cityBuilder.withProvince(provinceNode).build(ObjectType.NODE);

            Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2000-09-01");
            String updatedSummary = "updated summary";
            String updatedMotive = "updated motive";
            Date updatedDate = new SimpleDateFormat("yyyy-MM-dd").parse("2000-10-02");
            boolean updatedIsPartOfMultipleIncidents = false;
            boolean updatedIsSuccessful = false;
            boolean updatedIsSuicide = false;

            EventNode eventNode = (EventNode) eventBuilder.withTarget(targetNode).withCity(cityNode)
                    .build(ObjectType.NODE);

            String updatedCountryName = "updated country";
            String updatedProvinceName = "updated province";
            String updatedCityName = "updated city";
            double updatedCityLatitude = 12.0;
            double updatedCityLongitude = 12.0;
            String updatedTarget = "updated target";
            CountryNode updatedCountryNodeExpected = (CountryNode) countryBuilder.withName(updatedCountryName)
                    .withRegion(regionNodeExpected).build(ObjectType.NODE);
            TargetNode updatedTargetNodeExpected = (TargetNode) targetBuilder.withTarget(updatedTarget)
                    .withCountry(updatedCountryNodeExpected).build(ObjectType.NODE);
            ProvinceNode updatedProvinceNodeExpected = (ProvinceNode) provinceBuilder.withName(updatedProvinceName)
                    .withCountry(updatedCountryNodeExpected).build(ObjectType.NODE);
            CityNode updatedCityNodeExpected = (CityNode) cityBuilder.withName(updatedCityName)
                    .withLatitude(updatedCityLatitude).withLongitude(updatedCityLongitude)
                    .withProvince(updatedProvinceNodeExpected).build(ObjectType.NODE);
            EventNode updatedEventNodeExpected = (EventNode) eventBuilder.withSummary(updatedSummary).withMotive(updatedMotive)
                    .withDate(updatedDate).withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents)
                    .withIsSuccessful(updatedIsSuccessful).withIsSuicidal(updatedIsSuicide)
                    .withTarget(updatedTargetNodeExpected).withCity(updatedCityNodeExpected).build(ObjectType.NODE);

            GroupNode groupNode = (GroupNode) groupBuilder.withEventsCaused(List.of(eventNode)).build(ObjectType.NODE);
            GroupNode groupNodeExpected = (GroupNode) groupBuilder.withEventsCaused(List.of(updatedEventNodeExpected))
                    .build(ObjectType.NODE);

            JsonMergePatch groupAsJsonMergePatch = Json.createMergePatch(Json.createObjectBuilder()
                    .add("/eventsCaused[0]/summary", updatedSummary)
                    .add("/eventsCaused[0]/motive", updatedMotive)
                    .add("/eventsCaused[0]/date", date.toString())
                    .add("/eventsCaused[0]/isPartOfMultipleIncidents", updatedIsPartOfMultipleIncidents)
                    .add("/eventsCaused[0]/isSuccessful", updatedIsSuccessful)
                    .add("/eventsCaused[0]/isSuicidal", updatedIsSuicide)
                    .add("/eventsCaused[0]/target/target", updatedTarget)
                    .add("/eventsCaused[0]/target/countryOfOrigin/name", updatedCountryName)
                    .add("/eventsCaused[0]/city/name", updatedCityName)
                    .add("/eventsCaused[0]/city/latitude", updatedCityLatitude)
                    .add("/eventsCaused[0]/city/longitude", updatedCityLongitude)
                    .add("/eventsCaused[0]/city/province/name", updatedProvinceName)
                    .add("/eventsCaused[0]/city/province/country/name", updatedCountryName)
                    .build());

            JsonStructure group = Json.createObjectBuilder()
                    .add("eventsCaused[0]", Json.createObjectBuilder()
                            .add("summary", updatedSummary)
                            .add("motive", updatedMotive)
                            .add("date", updatedDate.toString())
                            .add("isPartOfMultipleIncidents", updatedIsPartOfMultipleIncidents)
                            .add("isSuccessful", updatedIsSuccessful)
                            .add("isSuicidal", updatedIsSuicide)
                            .add("target", Json.createObjectBuilder().add("target", updatedTarget)
                                    .add("countryOfOrigin", Json.createObjectBuilder().add("name", updatedCountryName))))
                    .add("city", Json.createObjectBuilder()
                            .add("name", updatedCityName)
                            .add("latitude", updatedCityLatitude)
                            .add("longitude", updatedCityLongitude)
                            .add("province", Json.createObjectBuilder().add("name", updatedProvinceName)
                                    .add("country", Json.createObjectBuilder().add("name", updatedCountryName))))
                    .build();

            JsonValue patched = groupAsJsonMergePatch.apply(group);

            when(objectMapper.convertValue(groupNode, JsonValue.class)).thenReturn(group);
            when(objectMapper.convertValue(patched, GroupNode.class)).thenReturn(groupNodeExpected);

            GroupNode groupNodeActual = patchHelper.mergePatch(groupAsJsonMergePatch, groupNode, GroupNode.class);

            assertAll(
                    () -> assertNotNull(groupNodeActual.getId(),
                            () -> "should return group node with id, but was: " + groupNodeActual.getId()),
                    () -> assertEquals(groupNodeExpected.getName(), groupNodeActual.getName(),
                            () -> "should return group node with name: " + groupNodeExpected.getName() + ", but was: "
                                    + groupNodeActual.getName()),

                    () -> assertEquals(updatedEventNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getId(), () -> "should return group event node with id: " + updatedEventNodeExpected.getId() + ", but was: "
                            + groupNodeActual.getEventsCaused().get(0).getId()),
                    () -> assertEquals(updatedEventNodeExpected.getSummary(), groupNodeActual.getEventsCaused().get(0).getSummary(), () -> "should return group event node with summary: " + updatedEventNodeExpected.getSummary() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getSummary()),
                    () -> assertEquals(updatedEventNodeExpected.getMotive(), groupNodeActual.getEventsCaused().get(0).getMotive(), () -> "should return group event node with motive: " + updatedEventNodeExpected.getMotive() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getMotive()),
                    () -> assertEquals(updatedEventNodeExpected.getDate(), groupNodeActual.getEventsCaused().get(0).getDate(), () -> "should return group event node with date: " + updatedEventNodeExpected.getDate() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getDate()),
                    () -> assertEquals(updatedEventNodeExpected.getIsPartOfMultipleIncidents(),
                            groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents(), () -> "should return group event node which was part of multiple incidents: " + updatedEventNodeExpected.getIsPartOfMultipleIncidents() + ", but was was: " + groupNodeActual.getEventsCaused().get(0).getIsPartOfMultipleIncidents()),
                    () -> assertEquals(updatedEventNodeExpected.getIsSuccessful(), groupNodeActual.getEventsCaused().get(0).getIsSuccessful(), () -> "should return group event node which was successful: " + updatedEventNodeExpected.getIsSuccessful() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuccessful()),
                    () -> assertEquals(updatedEventNodeExpected.getIsSuicidal(), groupNodeActual.getEventsCaused().get(0).getIsSuicidal(), () -> "should return group event node which was suicidal: " + updatedEventNodeExpected.getIsSuicidal() + ", but was: " + groupNodeActual.getEventsCaused().get(0).getIsSuicidal()),
                    () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getTarget(),
                            () -> "should return group node with event node with not null target, but was: null"),
                    () -> assertEquals(updatedTargetNodeExpected, groupNodeActual.getEventsCaused().get(0).getTarget(),
                            () -> "should return group node with event target: " + groupNodeActual.getEventsCaused().get(0).getTarget() + ", but was: "
                                    + updatedTargetNodeExpected),
                    () -> assertEquals(updatedTargetNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getTarget().getId(),
                            () -> "should return group node with event target id: " + groupNodeActual.getEventsCaused().get(0).getTarget().getId() + ", but was: "
                                    + updatedTargetNodeExpected.getId()),
                    () -> assertEquals(updatedTargetNodeExpected.getTarget(), groupNodeActual.getEventsCaused().get(0).getTarget().getTarget(),
                            () -> "should return group node with event target name: " + groupNodeActual.getEventsCaused().get(0).getTarget().getTarget() + ", but was: "
                                    + updatedTargetNodeExpected.getTarget()),

                    () -> assertEquals(updatedCountryNodeExpected, groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin(),
                            () -> "should return group node with event node with country: " + updatedCountryNodeExpected + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin()),
                    () -> assertEquals(updatedCountryNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getId(),
                            () -> "should return group node with event node with country id: " + updatedCountryNodeExpected.getId()
                                    + ", but was: " + groupNodeActual.getEventsCaused().get(0).getTarget().getId()),
                    () -> assertEquals(updatedCountryNodeExpected.getName(), groupNodeActual.getEventsCaused().get(0).getTarget().getCountryOfOrigin().getName(),
                            () -> "should return group node with event node with country name: " + updatedCountryNodeExpected.getName()
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
                    () -> assertEquals(updatedCityNodeExpected, groupNodeActual.getEventsCaused().get(0).getCity(),
                            () -> "should return group node with event city: " + groupNodeActual.getEventsCaused().get(0).getCity() + ", but was: "
                                    + updatedCityNodeExpected),
                    () -> assertEquals(updatedCityNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getCity().getId(),
                            () -> "should return group node with event city id: " + groupNodeActual.getEventsCaused().get(0).getCity().getId() + ", but was: "
                                    + updatedCityNodeExpected.getId()),
                    () -> assertEquals(updatedCityNodeExpected.getName(), groupNodeActual.getEventsCaused().get(0).getCity().getName(),
                            () -> "should return group node with event city name: " + groupNodeActual.getEventsCaused().get(0).getCity().getName() + ", but was: "
                                    + updatedCityNodeExpected.getName()),
                    () -> assertEquals(updatedCityNodeExpected.getLatitude(), groupNodeActual.getEventsCaused().get(0).getCity().getLatitude(),
                            () -> "should return group node with event city latitude: " + groupNodeActual.getEventsCaused().get(0).getCity().getLatitude() + ", but was: "
                                    + updatedCityNodeExpected.getLatitude()),
                    () -> assertEquals(updatedCityNodeExpected.getLongitude(), groupNodeActual.getEventsCaused().get(0).getCity().getLongitude(),
                            () -> "should return group node with event city longitude: " + groupNodeActual.getEventsCaused().get(0).getCity().getLongitude() + ", but was: "
                                    + updatedCityNodeExpected.getLongitude()),

                    () -> assertNotNull(groupNodeActual.getEventsCaused().get(0).getCity().getProvince(),
                            () -> "should return group node with not null province, but was: null"),
                    () -> assertEquals(updatedProvinceNodeExpected, groupNodeActual.getEventsCaused().get(0).getCity().getProvince(),
                            () -> "should return group node with province: " + updatedProvinceNodeExpected + ", but was: "
                                    + groupNodeActual.getEventsCaused().get(0).getCity().getProvince()),
                    () -> assertEquals(updatedProvinceNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getId(),
                            () -> "should return group node with province id: " + updatedProvinceNodeExpected.getId()
                                    + ", but was: " + groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getId()),
                    () -> assertEquals(updatedProvinceNodeExpected.getName(), groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getName(),
                            () -> "should return group node with province name: " + updatedProvinceNodeExpected.getName() + ", but was: "
                                    + groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getName()),
                    () -> assertEquals(updatedCountryNodeExpected, groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry(),
                            () -> "should return group node with country: " + updatedCountryNodeExpected + ", but was: " + groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry()),
                    () -> assertEquals(updatedCountryNodeExpected.getId(), groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getId(),
                            () -> "should return group node with country id: " + updatedCountryNodeExpected.getId()
                                    + ", but was: " + groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getId()),
                    () -> assertEquals(updatedCountryNodeExpected.getName(), groupNodeActual.getEventsCaused().get(0).getCity().getProvince().getCountry().getName(),
                            () -> "should return group node with country name: " + updatedCountryNodeExpected.getName()
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
                    () -> verify(objectMapper, times(1)).convertValue(groupNode, JsonValue.class),
                    () -> verify(objectMapper, times(1)).convertValue(patched, GroupNode.class),
                    () -> verifyNoMoreInteractions(objectMapper));
        }
    }
}
