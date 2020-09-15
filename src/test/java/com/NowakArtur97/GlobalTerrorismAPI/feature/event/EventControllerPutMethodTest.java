package com.NowakArtur97.GlobalTerrorismAPI.feature.event;

import com.NowakArtur97.GlobalTerrorismAPI.feature.city.CityDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.city.CityNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.city.CityRepository;
import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.target.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.target.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.user.RoleNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.user.UserNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.user.UserRepository;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.*;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.configuration.Neo4jTestConfiguration;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.database.Neo4jDatabaseUtil;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.mapper.ObjectTestMapper;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.common.util.JwtUtil;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(Neo4jTestConfiguration.class)
@AutoConfigureMockMvc
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventController_Tests")
class EventControllerPutMethodTest {

    private final String REGION_BASE_PATH = "http://localhost:8080/api/v1/regions";
    private final String COUNTRY_BASE_PATH = "http://localhost:8080/api/v1/countries";
    private final String TARGET_BASE_PATH = "http://localhost:8080/api/v1/targets";
    private final String PROVINCE_BASE_PATH = "http://localhost:8080/api/v1/provinces";
    private final String CITY_BASE_PATH = "http://localhost:8080/api/v1/cities";
    private final String EVENT_BASE_PATH = "http://localhost:8080/api/v1/events";
    private final String LINK_WITH_PARAMETER = EVENT_BASE_PATH + "/" + "{id}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    private static CountryBuilder countryBuilder;
    private static TargetBuilder targetBuilder;
    private static ProvinceBuilder provinceBuilder;
    private static CityBuilder cityBuilder;
    private static EventBuilder eventBuilder;

    private final static UserNode userNode = new UserNode("user1234", "Password1234!", "user1234email@.com",
            Set.of(new RoleNode("user")));

    private final static RegionNode regionNode = new RegionNode("region name");
    private final static RegionNode anotherRegionNode = new RegionNode("another region name");

    private final static CountryNode countryNode = new CountryNode("country name", regionNode);
    private final static CountryNode anotherCountryNode = new CountryNode("another country name", anotherRegionNode);

    private final static TargetNode targetNode = new TargetNode("target name", countryNode);

    private final static ProvinceNode provinceNode = new ProvinceNode("province name", countryNode);
    private final static ProvinceNode anotherProvinceNode = new ProvinceNode("province name 2", anotherCountryNode);

    private final static CityNode cityNode = new CityNode("city name", 25.0, 35.0, provinceNode);
    private final static CityNode anotherCityNode = new CityNode("city name 2", 15.0, -15.0,
            anotherProvinceNode);

    private final static EventNode eventNode = new EventNode("event summary", "event motive", new Date(),
            true, true, true, targetNode, cityNode);

    @BeforeAll
    private static void setUpBuilders() {

        countryBuilder = new CountryBuilder();
        targetBuilder = new TargetBuilder();
        provinceBuilder = new ProvinceBuilder();
        cityBuilder = new CityBuilder();
        eventBuilder = new EventBuilder();
    }

    @BeforeAll
    private static void setUp(@Autowired UserRepository userRepository, @Autowired EventRepository eventRepository,
                              @Autowired CityRepository cityRepository) {

        userRepository.save(userNode);

        cityRepository.save(anotherCityNode);

        eventRepository.save(eventNode);
    }

    @AfterAll
    private static void tearDown(@Autowired Neo4jDatabaseUtil neo4jDatabaseUtil) {

        neo4jDatabaseUtil.cleanDatabase();
    }

    @Test
    void when_update_valid_event_should_return_updated_event() throws ParseException {

        String updatedSummary = "summary updated";
        String updatedMotive = "motive updated";
        Date updatedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("01/08/2010 02:00:00:000");
        boolean updatedIsPartOfMultipleIncidents = false;
        boolean updatedIsSuccessful = false;
        boolean updatedIsSuicidal = false;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withSummary(updatedSummary).withMotive(updatedMotive).withDate(updatedDate)
                .withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents).withIsSuccessful(updatedIsSuccessful)
                .withIsSuicidal(updatedIsSuccessful).withTarget(targetDTO).withCity(cityDTO)
                .build(ObjectType.DTO);

        String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
        String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetNode.getId().intValue();
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue();
        String pathToTargetEventLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue() + "/targets";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToEventLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToTargetEventLink)))
                        .andExpect(jsonPath("id", is(eventNode.getId().intValue())))
                        .andExpect(jsonPath("summary", is(updatedSummary)))
                        .andExpect(jsonPath("motive", is(updatedMotive)))
                        .andExpect(jsonPath("date", is(notNullValue())))
                        .andExpect(jsonPath("isSuicidal", is(updatedIsSuicidal)))
                        .andExpect(jsonPath("isSuccessful", is(updatedIsSuccessful)))
                        .andExpect(jsonPath("isPartOfMultipleIncidents", is(updatedIsPartOfMultipleIncidents)))
                        .andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
                        .andExpect(jsonPath("target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("target.id", is(targetNode.getId().intValue())))
                        .andExpect(jsonPath("target.target", is(targetDTO.getTarget())))
                        .andExpect(jsonPath("target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("target.countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("target.countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("target.countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("target.countryOfOrigin.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("city.links[0].href", notNullValue()))
                        .andExpect(jsonPath("city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.id", notNullValue()))
                        .andExpect(jsonPath("city.name", is(cityDTO.getName())))
                        .andExpect(jsonPath("city.latitude", is(cityDTO.getLatitude())))
                        .andExpect(jsonPath("city.longitude", is(cityDTO.getLongitude())))
                        .andExpect(jsonPath("city.province.links[0].href", notNullValue()))
                        .andExpect(jsonPath("city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.province.id", notNullValue()))
                        .andExpect(jsonPath("city.province.name", is(provinceDTO.getName())))
                        .andExpect(jsonPath("city.province.country.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.province.country.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("city.province.country.name", is(countryDTO.getName())))
                        .andExpect(jsonPath("city.province.country.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.province.country.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("city.province.country.region.name", is(regionNode.getName()))));
    }

    @Test
    void when_update_event_target_should_return_event_with_updated_target() {

        String updatedTarget = "updated target";
        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(anotherCountryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget(updatedTarget).withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withName(anotherProvinceNode.getName())
                .withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withName(anotherCityNode.getName()).withLatitude(anotherCityNode.getLatitude())
                .withLongitude(anotherCityNode.getLongitude()).withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);

        String pathToRegionLink = REGION_BASE_PATH + "/" + anotherRegionNode.getId().intValue();
        String pathToCountryLink = COUNTRY_BASE_PATH + "/" + anotherCountryNode.getId().intValue();
        String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + anotherProvinceNode.getId().intValue();
        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetNode.getId().intValue();
        String pathToCityLink = CITY_BASE_PATH + "/" + anotherCityNode.getId().intValue();
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue();
        String pathToTargetEventLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue() + "/targets";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToEventLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToTargetEventLink)))
                        .andExpect(jsonPath("id", is(eventNode.getId().intValue())))
                        .andExpect(jsonPath("summary", is(eventDTO.getSummary())))
                        .andExpect(jsonPath("motive", is(eventDTO.getMotive())))
                        .andExpect(jsonPath("date", is(notNullValue())))
                        .andExpect(jsonPath("isSuicidal", is(eventDTO.getIsSuicidal())))
                        .andExpect(jsonPath("isSuccessful", is(eventDTO.getIsSuccessful())))
                        .andExpect(jsonPath("isPartOfMultipleIncidents", is(eventDTO.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
                        .andExpect(jsonPath("target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("target.id", is(targetNode.getId().intValue())))
                        .andExpect(jsonPath("target.target", is(targetDTO.getTarget())))
                        .andExpect(jsonPath("target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("target.countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("target.countryOfOrigin.id", is(anotherCountryNode.getId().intValue())))
                        .andExpect(jsonPath("target.countryOfOrigin.name", is(anotherCountryNode.getName())))
                        .andExpect(jsonPath("target.countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("target.countryOfOrigin.region.id", is(anotherRegionNode.getId().intValue())))
                        .andExpect(jsonPath("target.countryOfOrigin.region.name", is(anotherRegionNode.getName())))
                        .andExpect(jsonPath("city.links[0].href", is(pathToCityLink)))
                        .andExpect(jsonPath("city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.id", is(anotherCityNode.getId().intValue())))
                        .andExpect(jsonPath("city.name", is(anotherCityNode.getName())))
                        .andExpect(jsonPath("city.latitude", is(anotherCityNode.getLatitude())))
                        .andExpect(jsonPath("city.longitude", is(anotherCityNode.getLongitude())))
                        .andExpect(jsonPath("city.province.links[0].href", is(pathToProvinceLink)))
                        .andExpect(jsonPath("city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.province.id", is(anotherProvinceNode.getId().intValue())))
                        .andExpect(jsonPath("city.province.name", is(anotherProvinceNode.getName())))
                        .andExpect(jsonPath("city.province.country.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.province.country.id", is(anotherCountryNode.getId().intValue())))
                        .andExpect(jsonPath("city.province.country.name", is(anotherCountryNode.getName())))
                        .andExpect(jsonPath("city.province.country.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.province.country.region.id", is(anotherRegionNode.getId().intValue())))
                        .andExpect(jsonPath("city.province.country.region.name", is(anotherRegionNode.getName()))));
    }

    @Test
    void when_update_event_with_existing_city_should_return_event_with_existing_city() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withName(cityNode.getName()).withLatitude(cityNode.getLatitude())
                .withLongitude(cityNode.getLongitude()).withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO)
                .build(ObjectType.DTO);

        String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
        String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetNode.getId().intValue();
        String pathToCityLink = CITY_BASE_PATH + "/" + cityNode.getId().intValue();
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue();
        String pathToTargetEventLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue() + "/targets";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToEventLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToTargetEventLink)))
                        .andExpect(jsonPath("id", is(eventNode.getId().intValue())))
                        .andExpect(jsonPath("summary", is(eventDTO.getSummary())))
                        .andExpect(jsonPath("motive", is(eventDTO.getMotive())))
                        .andExpect(jsonPath("date", is(notNullValue())))
                        .andExpect(jsonPath("isSuicidal", is(eventDTO.getIsSuicidal())))
                        .andExpect(jsonPath("isSuccessful", is(eventDTO.getIsSuccessful())))
                        .andExpect(jsonPath("isPartOfMultipleIncidents", is(eventDTO.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
                        .andExpect(jsonPath("target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("target.id", is(targetNode.getId().intValue())))
                        .andExpect(jsonPath("target.target", is(targetDTO.getTarget())))
                        .andExpect(jsonPath("target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("target.countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("target.countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("target.countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("target.countryOfOrigin.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("city.links[0].href", is(pathToCityLink)))
                        .andExpect(jsonPath("city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.id", is(cityNode.getId().intValue())))
                        .andExpect(jsonPath("city.name", is(cityNode.getName())))
                        .andExpect(jsonPath("city.latitude", is(cityNode.getLatitude())))
                        .andExpect(jsonPath("city.longitude", is(cityNode.getLongitude())))
                        .andExpect(jsonPath("city.province.links[0].href", notNullValue()))
                        .andExpect(jsonPath("city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.province.id", notNullValue()))
                        .andExpect(jsonPath("city.province.name", is(provinceDTO.getName())))
                        .andExpect(jsonPath("city.province.country.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.province.country.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("city.province.country.name", is(countryNode.getName())))
                        .andExpect(jsonPath("city.province.country.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.province.country.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("city.province.country.region.name", is(regionNode.getName()))));
    }

    @Test
    void when_update_event_with_new_city_should_return_event_with_new_city() {

        String cityName = "some new city name";
        Double cityLatitude = 12.0;
        Double cityLongitude = 13.0;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withName(provinceNode.getName()).withCountry(countryDTO)
                .build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withName(cityName).withLatitude(cityLatitude)
                .withLongitude(cityLongitude).withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO)
                .build(ObjectType.DTO);

        String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
        String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
        String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + provinceNode.getId().intValue();
        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetNode.getId().intValue();
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue();
        String pathToTargetEventLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue() + "/targets";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToEventLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToTargetEventLink)))
                        .andExpect(jsonPath("id", is(eventNode.getId().intValue())))
                        .andExpect(jsonPath("summary", is(eventDTO.getSummary())))
                        .andExpect(jsonPath("motive", is(eventDTO.getMotive())))
                        .andExpect(jsonPath("date", is(notNullValue())))
                        .andExpect(jsonPath("isSuicidal", is(eventDTO.getIsSuicidal())))
                        .andExpect(jsonPath("isSuccessful", is(eventDTO.getIsSuccessful())))
                        .andExpect(jsonPath("isPartOfMultipleIncidents", is(eventDTO.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
                        .andExpect(jsonPath("target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("target.id", is(targetNode.getId().intValue())))
                        .andExpect(jsonPath("target.target", is(targetDTO.getTarget())))
                        .andExpect(jsonPath("target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("target.countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("target.countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("target.countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("target.countryOfOrigin.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("city.links[0].href", notNullValue()))
                        .andExpect(jsonPath("city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.id", notNullValue()))
                        .andExpect(jsonPath("city.name", is(cityName)))
                        .andExpect(jsonPath("city.latitude", is(cityLatitude)))
                        .andExpect(jsonPath("city.longitude", is(cityLongitude)))
                        .andExpect(jsonPath("city.province.links[0].href", is(pathToProvinceLink)))
                        .andExpect(jsonPath("city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.province.id", is(provinceNode.getId().intValue())))
                        .andExpect(jsonPath("city.province.name", is(provinceNode.getName())))
                        .andExpect(jsonPath("city.province.country.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.province.country.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("city.province.country.name", is(countryNode.getName())))
                        .andExpect(jsonPath("city.province.country.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.province.country.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("city.province.country.region.name", is(regionNode.getName()))));
    }

    @Test
    void when_update_event_province_should_return_event_with_updated_province() {

        String updatedProvinceName = "province updated";

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withName(updatedProvinceName).withCountry(countryDTO)
                .build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withName(cityNode.getName()).withLatitude(cityNode.getLatitude())
                .withLongitude(cityNode.getLongitude()).withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO)
                .build(ObjectType.DTO);

        String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
        String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();
        String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + provinceNode.getId().intValue();
        String pathToTargetLink = TARGET_BASE_PATH + "/" + targetNode.getId().intValue();
        String pathToCityLink = CITY_BASE_PATH + "/" + cityNode.getId().intValue();
        String pathToEventLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue();
        String pathToTargetEventLink = EVENT_BASE_PATH + "/" + eventNode.getId().intValue() + "/targets";

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToEventLink)))
                        .andExpect(jsonPath("links[1].href", is(pathToTargetEventLink)))
                        .andExpect(jsonPath("id", is(eventNode.getId().intValue())))
                        .andExpect(jsonPath("summary", is(eventDTO.getSummary())))
                        .andExpect(jsonPath("motive", is(eventDTO.getMotive())))
                        .andExpect(jsonPath("date", is(notNullValue())))
                        .andExpect(jsonPath("isSuicidal", is(eventDTO.getIsSuicidal())))
                        .andExpect(jsonPath("isSuccessful", is(eventDTO.getIsSuccessful())))
                        .andExpect(jsonPath("isPartOfMultipleIncidents", is(eventDTO.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("target.links[0].href", is(pathToTargetLink)))
                        .andExpect(jsonPath("target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("target.id", is(targetNode.getId().intValue())))
                        .andExpect(jsonPath("target.target", is(targetDTO.getTarget())))
                        .andExpect(jsonPath("target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("target.countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("target.countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("target.countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("target.countryOfOrigin.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("city.links[0].href", is(pathToCityLink)))
                        .andExpect(jsonPath("city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.id", is(cityNode.getId().intValue())))
                        .andExpect(jsonPath("city.name", is(cityNode.getName())))
                        .andExpect(jsonPath("city.latitude", is(cityNode.getLatitude())))
                        .andExpect(jsonPath("city.longitude", is(cityNode.getLongitude())))
                        .andExpect(jsonPath("city.province.links[0].href", is(pathToProvinceLink)))
                        .andExpect(jsonPath("city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.province.id", is(provinceNode.getId().intValue())))
                        .andExpect(jsonPath("city.province.name", is(updatedProvinceName)))
                        .andExpect(jsonPath("city.province.country.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.province.country.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("city.province.country.name", is(countryNode.getName())))
                        .andExpect(jsonPath("city.province.country.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.province.country.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("city.province.country.region.name", is(regionNode.getName()))));
    }

    @Test
    void when_update_event_with_not_existing_id_should_return_new_event() {

        Long notExistingId = 10000L;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);

        String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
        String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, notExistingId)
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", notNullValue()))
                        .andExpect(jsonPath("links[1].href", notNullValue()))
                        .andExpect(jsonPath("id", notNullValue()))
                        .andExpect(jsonPath("summary", is(eventDTO.getSummary())))
                        .andExpect(jsonPath("motive", is(eventDTO.getMotive())))
                        .andExpect(jsonPath("date", is(notNullValue())))
                        .andExpect(jsonPath("isSuicidal", is(eventDTO.getIsSuicidal())))
                        .andExpect(jsonPath("isSuccessful", is(eventDTO.getIsSuccessful())))
                        .andExpect(jsonPath("isPartOfMultipleIncidents", is(eventDTO.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("target.links[0].href", notNullValue()))
                        .andExpect(jsonPath("target.links[1].href").doesNotExist())
                        .andExpect(jsonPath("target.id", notNullValue()))
                        .andExpect(jsonPath("target.target", is(targetDTO.getTarget())))
                        .andExpect(jsonPath("target.countryOfOrigin.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("target.countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("target.countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("target.countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("target.countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("target.countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("target.countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("target.countryOfOrigin.region.name", is(regionNode.getName())))
                        .andExpect(jsonPath("city.links[0].href", notNullValue()))
                        .andExpect(jsonPath("city.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.id", notNullValue()))
                        .andExpect(jsonPath("city.name", is(cityDTO.getName())))
                        .andExpect(jsonPath("city.latitude", is(cityDTO.getLatitude())))
                        .andExpect(jsonPath("city.longitude", is(cityDTO.getLongitude())))
                        .andExpect(jsonPath("city.province.links[0].href", notNullValue()))
                        .andExpect(jsonPath("city.province.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.province.id", notNullValue()))
                        .andExpect(jsonPath("city.province.name", is(provinceDTO.getName())))
                        .andExpect(jsonPath("city.province.country.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("city.province.country.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.province.country.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("city.province.country.name", is(countryNode.getName())))
                        .andExpect(jsonPath("city.province.country.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("city.province.country.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("city.province.country.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("city.province.country.region.name", is(regionNode.getName()))));
    }

    @Test
    void when_update_event_with_null_fields_should_return_errors() {

        EventDTO eventDTO = (EventDTO) eventBuilder.withId(null).withSummary(null).withMotive(null).withDate(null)
                .withIsPartOfMultipleIncidents(null).withIsSuccessful(null).withIsSuicidal(null)
                .withTarget(null).withCity(null)
                .build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Event summary cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Event motive cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Event date cannot be null.")))
                        .andExpect(jsonPath("errors",
                                hasItem("Event must have information on whether it has been part of many incidents.")))
                        .andExpect(jsonPath("errors",
                                hasItem("Event must have information about whether it was successful.")))
                        .andExpect(jsonPath("errors",
                                hasItem("Event must have information about whether it was a suicidal attack.")))
                        .andExpect(jsonPath("errors", hasItem("Target name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("City name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(8))));
    }

    @ParameterizedTest(name = "{index}: Event Target Country: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_event_with_not_existing_country_should_return_errors(String invalidCountryName) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(invalidCountryName).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("A country with the provided name does not exist.")))
                        .andExpect(jsonPath("errors[1]", is("A country with the provided name does not exist.")))
                        .andExpect(jsonPath("errors", Matchers.hasSize(2))));
    }

    @ParameterizedTest(name = "{index}: For Event Target: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_event_with_invalid_target_should_return_errors(String invalidTarget) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget(invalidTarget).withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Target name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Event summary: {0}")
    @EmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_event_with_invalid_summary_should_return_errors(String invalidSummary) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withSummary(invalidSummary).withTarget(targetDTO).withCity(cityDTO)
                .build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event summary cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Event motive: {0}")
    @EmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_event_with_invalid_motive_should_return_errors(String invalidMotive) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withMotive(invalidMotive).withTarget(targetDTO).withCity(cityDTO)
                .build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event motive cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_event_with_date_in_the_future_should_return_errors() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(2090, Calendar.FEBRUARY, 1);
        Date invalidDate = calendar.getTime();
        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withDate(invalidDate).withTarget(targetDTO).withCity(cityDTO)
                .build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event date cannot be in the future.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Event City name: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_event_with_invalid_city_name_should_return_errors(String invalidCityName) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withName(invalidCityName).withProvince(provinceDTO)
                .build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_event_with_invalid_geographical_location_of_city_should_return_errors() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withLatitude(null).withLongitude(null).withProvince(null)
                .build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("City latitude cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("City longitude cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Province name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Province and target should be located in the same country.")))
                        .andExpect(jsonPath("errors", hasSize(4))));
    }

    @Test
    void when_update_event_with_too_small_city_latitude_should_return_errors() {

        Double invalidCityLatitude = -91.0;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withLatitude(invalidCityLatitude).withProvince(provinceDTO)
                .build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City latitude must be greater or equal to -90.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_event_with_too_big_city_latitude_should_return_errors() {

        Double invalidCityLatitude = 91.0;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withLatitude(invalidCityLatitude).withProvince(provinceDTO)
                .build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City latitude must be less or equal to 90.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_event_with_too_small_city_longitude_should_return_errors() {

        Double invalidCityLongitude = -181.0;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withLongitude(invalidCityLongitude).withProvince(provinceDTO)
                .build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City longitude must be greater or equal to -180.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_event_with_too_big_city_longitude_should_return_errors() {

        Double invalidCityLongitude = 181.0;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withLongitude(invalidCityLongitude).withProvince(provinceDTO)
                .build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("City longitude must be less or equal to 180.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_event_with_province_and_target_in_different_countries_should_return_errors() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        CountryDTO countryDTO2 = (CountryDTO) countryBuilder.withName(anotherCountryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(countryDTO2).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Province and target should be located in the same country.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Event Province name: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_event_with_invalid_province_name_should_return_errors(String invalidProvinceName) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withName(invalidProvinceName)
                .withCountry(countryDTO).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Province name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_update_event_without_province_country_should_return_errors() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        ProvinceDTO provinceDTO = (ProvinceDTO) provinceBuilder.withCountry(null).build(ObjectType.DTO);
        CityDTO cityDTO = (CityDTO) cityBuilder.withProvince(provinceDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).withCity(cityDTO).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNode.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Country name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasItem("Province and target should be located in the same country.")))
                        .andExpect(jsonPath("errors", hasSize(2))));
    }
}
