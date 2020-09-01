package com.NowakArtur97.GlobalTerrorismAPI.controller.group;

import com.NowakArtur97.GlobalTerrorismAPI.dto.*;
import com.NowakArtur97.GlobalTerrorismAPI.node.*;
import com.NowakArtur97.GlobalTerrorismAPI.repository.CountryRepository;
import com.NowakArtur97.GlobalTerrorismAPI.repository.GroupRepository;
import com.NowakArtur97.GlobalTerrorismAPI.repository.ProvinceRepository;
import com.NowakArtur97.GlobalTerrorismAPI.repository.UserRepository;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.*;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.configuration.Neo4jTestConfiguration;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.database.Neo4jDatabaseUtil;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.mapper.ObjectTestMapper;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.jwt.JwtUtil;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(Neo4jTestConfiguration.class)
@AutoConfigureMockMvc
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("GroupController_Tests")
class GroupControllerPutMethodTest {

    private final String GROUP_BASE_PATH = "http://localhost:8080/api/v1/groups";
    private final String CITY_BASE_PATH = "http://localhost:8080/api/v1/cities";
    private final String LINK_WITH_PARAMETER = GROUP_BASE_PATH + "/" + "{id}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    private static CountryBuilder countryBuilder;
    private static TargetBuilder targetBuilder;
    private static ProvinceBuilder provinceBuilder;
    private static CityBuilder cityBuilder;
    private static EventBuilder eventBuilder;
    private static GroupBuilder groupBuilder;

    private final static UserNode userNode = new UserNode("user1234", "Password1234!", "user1234email@.com",
            Set.of(new RoleNode("user")));

    private final static RegionNode regionNode = new RegionNode("region name");
    private final static RegionNode anotherRegionNode = new RegionNode("region name 2");

    private final static CountryNode countryNode = new CountryNode("country name", regionNode);
    private final static CountryNode anotherCountryNode = new CountryNode("country name 2", anotherRegionNode);

    private final static TargetNode targetNode = new TargetNode("target name", countryNode);
    private final static TargetNode targetNode2 = new TargetNode("target name 2", countryNode);

    private final static ProvinceNode provinceNode = new ProvinceNode("province name", countryNode);

    private final static CityNode cityNode = new CityNode("city name", 25.0, 41.0, provinceNode);
    private final static CityNode anotherCityNode = new CityNode("city name 2", 11.0, 25.0, provinceNode);

    private final static EventNode eventNode = new EventNode("summary", "motive", new Date(),
            true, true, true, targetNode, cityNode);
    private final static EventNode eventNode2 = new EventNode("summary 2", "motive 2", new Date(),
            false, false, false, targetNode2, anotherCityNode);

    private final static GroupNode groupNode = new GroupNode("group name", List.of(eventNode, eventNode2));

    @BeforeAll
    private static void setUpBuilders() {

        countryBuilder = new CountryBuilder();
        targetBuilder = new TargetBuilder();
        provinceBuilder = new ProvinceBuilder();
        cityBuilder = new CityBuilder();
        eventBuilder = new EventBuilder();
        groupBuilder = new GroupBuilder();
    }

    @BeforeAll
    private static void setUp(@Autowired UserRepository userRepository, @Autowired GroupRepository groupRepository,
                              @Autowired CountryRepository countryRepository) {

        userRepository.save(userNode);

        countryRepository.save(anotherCountryNode);

        groupRepository.save(groupNode);
    }

    @AfterAll
    private static void tearDown(@Autowired Neo4jDatabaseUtil neo4jDatabaseUtil, @Autowired ProvinceRepository provinceRepository) {

        provinceRepository.findAll().forEach(System.out::println);

        neo4jDatabaseUtil.cleanDatabase();
    }

    @Test
    void when_update_valid_group_should_return_updated_group_as_model() {

        String updatedGroupName = "new group name";

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget(targetNode.getTarget()).withCountry(countryDTO)
                .build(ObjectType.DTO);
        TargetDTO targetDTO2 = (TargetDTO) targetBuilder.withTarget(targetNode2.getTarget()).withCountry(countryDTO)
                .build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO2 = (ProvinceDTO) provinceBuilder.withName("new country province")
                .withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        CityDTO cityDTO2 = (CityDTO) cityBuilder.withName("new event city name").withProvince(provinceDTO2)
                .build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withSummary(eventNode.getSummary()).withMotive(eventNode.getMotive())
                .withDate(eventNode.getDate()).withIsPartOfMultipleIncidents(eventNode.getIsPartOfMultipleIncidents())
                .withIsSuccessful(eventNode.getIsSuccessful()).withIsSuicidal(eventNode.getIsSuicidal())
                .withTarget(targetDTO).withCity(cityDTO)
                .build(ObjectType.DTO);
        EventDTO eventDTO2 = (EventDTO) eventBuilder.withSummary(eventNode2.getSummary()).withMotive(eventNode2.getMotive())
                .withDate(eventNode2.getDate()).withIsPartOfMultipleIncidents(eventNode2.getIsPartOfMultipleIncidents())
                .withIsSuccessful(eventNode2.getIsSuccessful()).withIsSuicidal(eventNode2.getIsSuicidal())
                .withTarget(targetDTO2).withCity(cityDTO2)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withName(updatedGroupName).withEventsCaused(List.of(eventDTO, eventDTO2))
                .build(ObjectType.DTO);

        String pathToGroupLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue();
        String pathToEventsLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue() + "/events";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToGroupLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
                        .andExpect(jsonPath("id", is(groupNode.getId().intValue())))
                        .andExpect(jsonPath("name", is(groupDTO.getName())))
                        .andExpect(jsonPath("eventsCaused[0].links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].links[1].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].summary", is(eventDTO.getSummary())))
                        .andExpect(jsonPath("eventsCaused[0].motive", is(eventDTO.getMotive())))
                        .andExpect(jsonPath("eventsCaused[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventDTO.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(eventDTO.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(eventDTO.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents", is(eventDTO.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[0].target.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].target.target", is(targetDTO.getTarget())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[0].city.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].city.name", is(cityDTO.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.latitude", is(cityDTO.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.longitude", is(cityDTO.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].city.province.name", is(provinceDTO.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.links").isEmpty())

                        .andExpect(jsonPath("eventsCaused[1].links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].links[1].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].summary", is(eventDTO2.getSummary())))
                        .andExpect(jsonPath("eventsCaused[1].motive", is(eventDTO2.getMotive())))
                        .andExpect(jsonPath("eventsCaused[1].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventDTO2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[1].isSuicidal", is(eventDTO2.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[1].isSuccessful", is(eventDTO2.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[1].isPartOfMultipleIncidents", is(eventDTO2.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[1].target.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].target.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].target.target", is(targetDTO2.getTarget())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[1].city.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].city.name", is(cityDTO2.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.latitude", is(cityDTO2.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[1].city.longitude", is(cityDTO2.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].city.province.name", is(provinceDTO2.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[2]").doesNotExist()));
    }

    @Test
    void when_update_valid_group_with_events_should_return_updated_group_as_model() throws ParseException {

        String updatedTargetName = "new target name";

        String updatedProvinceName = "new province name";
        String updatedCityName = "new city name";
        Double updatedCityLatitude = -15.0;
        Double updatedCityLongitude = -15.0;

        String updatedSummary = "summary updated";
        String updatedMotive = "motive updated";
        Date updatedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("01/08/2010 02:00:00:000");
        boolean updatedIsPartOfMultipleIncidents = false;
        boolean updatedIsSuccessful = false;
        boolean updatedIsSuicidal = false;

        String updatedGroupName = "new group name";

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        CountryDTO countryDTO2 = (CountryDTO) countryBuilder.withName(anotherCountryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget(updatedTargetName).withCountry(countryDTO)
                .build(ObjectType.DTO);
        TargetDTO targetDTO2 = (TargetDTO) targetBuilder.withTarget(updatedTargetName + " 2").withCountry(countryDTO2)
                .build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO2 = (ProvinceDTO) provinceBuilder.withName(updatedProvinceName)
                .withCountry(countryDTO2).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withName("new city name").withProvince(provinceDTO).build(ObjectType.DTO);
        CityDTO cityDTO2 = (CityDTO) cityBuilder.withName(updatedCityName).withLatitude(updatedCityLatitude)
                .withLongitude(updatedCityLongitude).withProvince(provinceDTO2).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withSummary(updatedSummary + " 2").withMotive(updatedMotive + " 2")
                .withDate(updatedDate).withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents)
                .withIsSuccessful(updatedIsSuccessful).withIsSuicidal(updatedIsSuicidal)
                .withTarget(targetDTO).withCity(cityDTO)
                .build(ObjectType.DTO);
        EventDTO eventDTO2 = (EventDTO) eventBuilder.withSummary(updatedSummary).withMotive(updatedMotive)
                .withDate(updatedDate).withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents)
                .withIsSuccessful(updatedIsSuccessful).withIsSuicidal(updatedIsSuicidal).withTarget(targetDTO2)
                .withTarget(targetDTO2).withCity(cityDTO2)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withName(updatedGroupName).withEventsCaused(List.of(eventDTO, eventDTO2))
                .build(ObjectType.DTO);

        String pathToGroupLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue();
        String pathToEventsLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue() + "/events";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToGroupLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
                        .andExpect(jsonPath("id", is(groupNode.getId().intValue())))
                        .andExpect(jsonPath("name", is(groupDTO.getName())))
                        .andExpect(jsonPath("eventsCaused[0].links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].links[1].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].summary", is(eventDTO.getSummary())))
                        .andExpect(jsonPath("eventsCaused[0].motive", is(eventDTO.getMotive())))
                        .andExpect(jsonPath("eventsCaused[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventDTO.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(eventDTO.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(eventDTO.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents", is(eventDTO.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[0].target.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].target.target", is(targetDTO.getTarget())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[0].city.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].city.name", is(cityDTO.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.latitude", is(cityDTO.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.longitude", is(cityDTO.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].city.province.name", is(provinceDTO.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.links").isEmpty())

                        .andExpect(jsonPath("eventsCaused[1].links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].links[1].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].summary", is(eventDTO2.getSummary())))
                        .andExpect(jsonPath("eventsCaused[1].motive", is(eventDTO2.getMotive())))
                        .andExpect(jsonPath("eventsCaused[1].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventDTO2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[1].isSuicidal", is(eventDTO2.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[1].isSuccessful", is(eventDTO2.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[1].isPartOfMultipleIncidents", is(eventDTO2.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[1].target.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].target.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].target.target", is(targetDTO2.getTarget())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.id", is(anotherCountryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.name", is(anotherCountryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.id", is(anotherRegionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.name", is(anotherRegionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[1].city.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].city.name", is(cityDTO2.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.latitude", is(cityDTO2.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[1].city.longitude", is(cityDTO2.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].city.province.name", is(updatedProvinceName)))
                        .andExpect(jsonPath("eventsCaused[1].city.province.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.id", is(anotherCountryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.name", is(anotherCountryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.id", is(anotherRegionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.name", is(anotherRegionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[2]").doesNotExist()));
    }

    @Test
    void when_update_valid_group_with_events_using_existing_city_should_return_updated_group_as_model() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        TargetDTO targetDTO2 = (TargetDTO) targetBuilder.withTarget("target2").withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withName(provinceNode.getName()).withCountry(countryDTO)
                .build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withName(cityNode.getName()).withLatitude(cityNode.getLatitude())
                .withLongitude(cityNode.getLongitude()).withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO)
                .build(ObjectType.DTO);
        EventDTO eventDTO2 = (EventDTO) eventBuilder.withTarget(targetDTO2).withCity(cityDTO)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO, eventDTO2))
                .build(ObjectType.DTO);

        String pathToCityLink = CITY_BASE_PATH + "/" + cityNode.getId().intValue();
        String pathToGroupLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue();
        String pathToEventsLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue() + "/events";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToGroupLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
                        .andExpect(jsonPath("id", is(groupNode.getId().intValue())))
                        .andExpect(jsonPath("name", is(groupDTO.getName())))
                        .andExpect(jsonPath("eventsCaused[0].links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].links[1].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].summary", is(eventDTO.getSummary())))
                        .andExpect(jsonPath("eventsCaused[0].motive", is(eventDTO.getMotive())))
                        .andExpect(jsonPath("eventsCaused[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventDTO.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(eventDTO.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(eventDTO.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents", is(eventDTO.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[0].target.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].target.target", is(targetDTO.getTarget())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[0].city.links[0].href", is(pathToCityLink)))
                        .andExpect(jsonPath("eventsCaused[0].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].city.name", is(cityNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.latitude", is(cityNode.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.longitude", is(cityNode.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.id", is(provinceNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.name", is(provinceNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.links").isEmpty())

                        .andExpect(jsonPath("eventsCaused[1].links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].links[1].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].summary", is(eventDTO2.getSummary())))
                        .andExpect(jsonPath("eventsCaused[1].motive", is(eventDTO2.getMotive())))
                        .andExpect(jsonPath("eventsCaused[1].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventDTO2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[1].isSuicidal", is(eventDTO2.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[1].isSuccessful", is(eventDTO2.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[1].isPartOfMultipleIncidents", is(eventDTO2.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[1].target.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].target.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].target.target", is(targetDTO2.getTarget())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.id", is(is(regionNode.getId().intValue()))))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[1].city.links[0].href", is(pathToCityLink)))
                        .andExpect(jsonPath("eventsCaused[1].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.id", is(cityNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.name", is(cityNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.latitude", is(cityNode.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[1].city.longitude", is(cityNode.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.id", is(provinceNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.name", is(provinceNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[2]").doesNotExist()));
    }

    @Test
    void when_update_valid_group_with_not_existing_id_should_return_new_group_as_model() {

        Long notExistingId = 10000L;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        CountryDTO countryDTO2 = (CountryDTO) countryBuilder.withName(anotherCountryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        TargetDTO targetDTO2 = (TargetDTO) targetBuilder.withTarget("target 2").withCountry(countryDTO2)
                .build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withName(provinceNode.getName())
                .withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO2 = (ProvinceDTO) provinceBuilder.withName("new country province")
                .withCountry(countryDTO2).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withName(anotherCityNode.getName())
                .withLatitude(anotherCityNode.getLatitude()).withLongitude(anotherCityNode.getLongitude())
                .withProvince(provinceDTO).build(ObjectType.DTO);
        CityDTO cityDTO2 = (CityDTO) cityBuilder.withName("new group city name").withProvince(provinceDTO2)
                .build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);
        EventDTO eventDTO2 = (EventDTO) eventBuilder.withMotive("motive 2").withSummary("summary 2")
                .withIsSuicidal(false).withIsSuccessful(false).withIsPartOfMultipleIncidents(false)
                .withTarget(targetDTO2).withCity(cityDTO2).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO, eventDTO2)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, notExistingId)
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", notNullValue()))
                        .andExpect(jsonPath("links[1].href", notNullValue()))
                        .andExpect(jsonPath("id", notNullValue()))
                        .andExpect(jsonPath("name", is(groupDTO.getName())))
                        .andExpect(jsonPath("eventsCaused[0].links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].links[1].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].summary", is(eventDTO.getSummary())))
                        .andExpect(jsonPath("eventsCaused[0].motive", is(eventDTO.getMotive())))
                        .andExpect(jsonPath("eventsCaused[0].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventDTO.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(eventDTO.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(eventDTO.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents", is(eventDTO.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[0].target.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].target.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].target.target", is(targetDTO.getTarget())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.region.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[0].city.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[0].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[0].city.id", is(anotherCityNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.name", is(anotherCityNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.latitude", is(anotherCityNode.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.longitude", is(anotherCityNode.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.id", is(provinceNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.name", is(provinceNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.name", is(countryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[0].city.province.country.region.links").isEmpty())

                        .andExpect(jsonPath("eventsCaused[1].links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].links[1].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].summary", is(eventDTO2.getSummary())))
                        .andExpect(jsonPath("eventsCaused[1].motive", is(eventDTO2.getMotive())))
                        .andExpect(jsonPath("eventsCaused[1].date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventDTO2.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("eventsCaused[1].isSuicidal", is(eventDTO2.getIsSuicidal())))
                        .andExpect(jsonPath("eventsCaused[1].isSuccessful", is(eventDTO2.getIsSuccessful())))
                        .andExpect(jsonPath("eventsCaused[1].isPartOfMultipleIncidents", is(eventDTO2.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("eventsCaused[1].target.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].target.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].target.target", is(targetDTO2.getTarget())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.id", is(anotherCountryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.name", is(anotherCountryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.id", is(anotherRegionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.name", is(anotherRegionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.region.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[1].city.links[0].href", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("eventsCaused[1].city.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].city.name", is(cityDTO2.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.latitude", is(cityDTO2.getLatitude())))
                        .andExpect(jsonPath("eventsCaused[1].city.longitude", is(cityDTO2.getLongitude())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.id", notNullValue()))
                        .andExpect(jsonPath("eventsCaused[1].city.province.name", is(provinceDTO2.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.id", is(anotherCountryNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.name", is(anotherCountryNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.id", is(anotherRegionNode.getId().intValue())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.name", is(anotherRegionNode.getName())))
                        .andExpect(jsonPath("eventsCaused[1].city.province.country.region.links").isEmpty())
                        .andExpect(jsonPath("eventsCaused[2]").doesNotExist()));
    }

    @Test
    void when_update_group_with_null_fields_should_return_errors() {

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withName(null).withEventsCaused(null).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Group name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("List of Events caused by the Group cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(2))));
    }

    @Test
    void when_update_group_with_null_event_fields_should_return_errors() {

        EventDTO eventDTO = (EventDTO) eventBuilder.withId(null).withSummary(null).withMotive(null).withDate(null)
                .withIsPartOfMultipleIncidents(null).withIsSuccessful(null).withIsSuicidal(null)
                .withTarget(null).withCity(null)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Event summary cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Event motive cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Event date cannot be null.")))
                        .andExpect(jsonPath("errors", hasItem("Event must have information on whether it has been part of many incidents.")))
                        .andExpect(jsonPath("errors", hasItem("Event must have information about whether it was successful.")))
                        .andExpect(jsonPath("errors", hasItem("Event must have information about whether it was a suicidal attack.")))
                        .andExpect(jsonPath("errors", hasItem("Target name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("City name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(8))));
    }

    @Test
    void when_update_group_with_empty_events_list_should_return_errors() {

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(new ArrayList<>()).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("List of Events caused by the Group cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Group name: {0}")
    @EmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_group_with_invalid_name_should_return_errors(String invalidName) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withName(invalidName).withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Group name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: Group Target Country: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_add_group_with_not_existing_country_should_return_errors(String invalidCountryName) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(invalidCountryName).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("A country with the provided name does not exist.")))
                        .andExpect(jsonPath("errors", Matchers.hasSize(2))));
    }

    @ParameterizedTest(name = "{index}: For Group Target: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_group_event_with_invalid_target_should_return_errors(String invalidTarget) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget(invalidTarget).withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Target name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Group event summary: {0}")
    @EmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_group_event_with_invalid_summary_should_return_errors(String invalidSummary) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withSummary(invalidSummary).withTarget(targetDTO).withCity(cityDTO)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event summary cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Group event motive: {0}")
    @EmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_group_event_with_invalid_motive_should_return_errors(String invalidMotive) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withMotive(invalidMotive).withTarget(targetDTO).withCity(cityDTO)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event motive cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_group_event_with_date_in_the_future_should_return_errors() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(2090, Calendar.FEBRUARY, 1);
        Date invalidDate = calendar.getTime();
        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withDate(invalidDate).withTarget(targetDTO).withCity(cityDTO)
                .build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event date cannot be in the future.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Group Event City name: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_group_event_with_invalid_city_name_should_return_errors(String invalidCityName) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withName(invalidCityName).withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_group_event_with_invalid_geographical_location_of_city_should_return_errors() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withLatitude(null).withLongitude(null).withProvince(null)
                .build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("City latitude cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("City longitude cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Province name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Province and target should be located in the same country.")))
                        .andExpect(jsonPath("errors", hasSize(4))));
    }

    @Test
    void when_update_group_event_with_too_small_city_latitude_should_return_errors() {

        Double invalidCityLatitude = -91.0;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withLatitude(invalidCityLatitude).withProvince(provinceDTO)
                .build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City latitude must be greater or equal to -90.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_group_event_with_too_big_city_latitude_should_return_errors() {

        Double invalidCityLatitude = 91.0;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withLatitude(invalidCityLatitude).withProvince(provinceDTO)
                .build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City latitude must be less or equal to 90.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_group_event_with_too_small_city_longitude_should_return_errors() {

        Double invalidCityLongitude = -181.0;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withLongitude(invalidCityLongitude).withProvince(provinceDTO)
                .build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City longitude must be greater or equal to -180.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_group_event_with_too_big_city_longitude_should_return_errors() {

        Double invalidCityLongitude = 181.0;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withLongitude(invalidCityLongitude).withProvince(provinceDTO)
                .build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);
        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City longitude must be less or equal to 180.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_add_group_event_with_province_and_target_in_different_countries_should_return_errors() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        CountryDTO countryDTO2 = (CountryDTO) countryBuilder.withName(anotherCountryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO2).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Province and target should be located in the same country.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Group Event Province name: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_add_group_event_with_invalid_province_name_should_return_errors(String invalidProvinceName) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withName(invalidProvinceName)
                .withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Province name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_add_group_event_without_province_country_should_return_errors() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(null).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);

        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, groupNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(groupDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Country name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Province and target should be located in the same country.")))
                        .andExpect(jsonPath("errors", hasSize(2))));
    }
}
