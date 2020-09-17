package com.nowakArtur97.globalTerrorismAPI.feature.event;

import com.nowakArtur97.globalTerrorismAPI.feature.city.CityNode;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryNode;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryRepository;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceNode;
import com.nowakArtur97.globalTerrorismAPI.feature.region.RegionNode;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetNode;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.RoleNode;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.UserNode;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.UserRepository;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.CountryBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.nowakArtur97.globalTerrorismAPI.testUtil.configuration.Neo4jTestConfiguration;
import com.nowakArtur97.globalTerrorismAPI.testUtil.database.Neo4jDatabaseUtil;
import com.nowakArtur97.globalTerrorismAPI.testUtil.mapper.ObjectTestMapper;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.nowakArtur97.globalTerrorismAPI.common.util.JwtUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
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

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(Neo4jTestConfiguration.class)
@AutoConfigureMockMvc
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("TargetController_Tests")
class EventTargetControllerPutMethodTest {

    private final String REGION_BASE_PATH = "http://localhost:8080/api/v1/regions";
    private final String COUNTRY_BASE_PATH = "http://localhost:8080/api/v1/countries";
    private final String TARGET_BASE_PATH = "http://localhost:8080/api/v1/targets";
    private final String EVENT_BASE_PATH = "http://localhost:8080/api/v1/events";
    private final String LINK_WITH_PARAMETER = EVENT_BASE_PATH + "/" + "{id}/targets";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    private static CountryBuilder countryBuilder;
    private static TargetBuilder targetBuilder;

    private final static UserNode userNode = new UserNode("user1234", "Password1234!", "user1234email@.com",
            Set.of(new RoleNode("user")));

    private final static RegionNode regionNode = new RegionNode("region");
    private final static RegionNode anotherRegionNode = new RegionNode("another region");

    private final static CountryNode countryNode = new CountryNode("country", regionNode);
    private final static CountryNode anotherCountryNode = new CountryNode("another country", anotherRegionNode);

    private final static TargetNode targetNode = new TargetNode("target", countryNode);

    private final static ProvinceNode provinceNode = new ProvinceNode("province", countryNode);

    private final static CityNode cityNode = new CityNode("city", 45.0, 45.0, provinceNode);

    private final static EventNode eventNodeWithoutTarget = new EventNode("summary", "motive", new Date(),
            true, true, true);
    private final static EventNode eventNodeWithTarget = new EventNode("summary", "motive", new Date(),
            true, false, false, targetNode, cityNode);

    @BeforeAll
    private static void setUpBuilders() {

        countryBuilder = new CountryBuilder();
        targetBuilder = new TargetBuilder();
    }

    @BeforeAll
    private static void setUp(@Autowired UserRepository userRepository, @Autowired EventRepository eventRepository,
                              @Autowired CountryRepository countryRepository) {

        userRepository.save(userNode);

        countryRepository.save(anotherCountryNode);

        eventRepository.save(eventNodeWithoutTarget);
        eventRepository.save(eventNodeWithTarget);
    }

    @AfterAll
    private static void tearDown(@Autowired Neo4jDatabaseUtil neo4jDatabaseUtil) {

        neo4jDatabaseUtil.cleanDatabase();
    }

    @Test
    void when_add_valid_event_to_target_should_return_new_target_as_model() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);

        String pathToRegionLink = REGION_BASE_PATH + "/" + regionNode.getId().intValue();
        String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryNode.getId().intValue();

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNodeWithoutTarget.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(targetDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", notNullValue()))
                        .andExpect(jsonPath("id", notNullValue()))
                        .andExpect(jsonPath("target", is(targetDTO.getTarget())))
                        .andExpect(jsonPath("countryOfOrigin.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("countryOfOrigin.id", is(countryNode.getId().intValue())))
                        .andExpect(jsonPath("countryOfOrigin.name", is(countryNode.getName())))
                        .andExpect(jsonPath("countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("countryOfOrigin.region.id", is(regionNode.getId().intValue())))
                        .andExpect(jsonPath("countryOfOrigin.region.name", is(regionNode.getName()))));
    }

    @Test
    void when_update_valid_event_target_should_return_updated_target_as_model() {

        String updatedTargetName = "updated target";
        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(anotherCountryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget(updatedTargetName).withCountry(countryDTO).build(ObjectType.DTO);

        String pathToRegionLink = REGION_BASE_PATH + "/" + anotherRegionNode.getId().intValue();
        String pathToCountryLink = COUNTRY_BASE_PATH + "/" + anotherCountryNode.getId().intValue();
        String pathToLink = TARGET_BASE_PATH + "/" + targetNode.getId().intValue();

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNodeWithTarget.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(targetDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToLink)))
                        .andExpect(jsonPath("id", is(targetNode.getId().intValue())))
                        .andExpect(jsonPath("target", is(updatedTargetName)))
                        .andExpect(jsonPath("countryOfOrigin.links[0].href", is(pathToCountryLink)))
                        .andExpect(jsonPath("countryOfOrigin.links[1].href").doesNotExist())
                        .andExpect(jsonPath("countryOfOrigin.id", is(anotherCountryNode.getId().intValue())))
                        .andExpect(jsonPath("countryOfOrigin.name", is(anotherCountryNode.getName())))
                        .andExpect(jsonPath("countryOfOrigin.region.links[0].href", is(pathToRegionLink)))
                        .andExpect(jsonPath("countryOfOrigin.region.links[1].href").doesNotExist())
                        .andExpect(jsonPath("countryOfOrigin.region.id", is(anotherRegionNode.getId().intValue())))
                        .andExpect(jsonPath("countryOfOrigin.region.name", is(anotherRegionNode.getName()))));
    }

    @ParameterizedTest(name = "{index}: Target Name: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_update_invalid_event_target_should_return_errors(String invalidTargetName) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget(invalidTargetName).withCountry(countryDTO).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNodeWithTarget.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(targetDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Target name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: Target Country: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void when_add_target_with_not_existing_country_should_return_errors(String invalidCountryName) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(invalidCountryName).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, eventNodeWithTarget.getId())
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(targetDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("A country with the provided name does not exist.")))
                        .andExpect(jsonPath("errors", Matchers.hasSize(1))));
    }

    @Test
    void when_add_valid_target_to_event_but_event_not_exist_should_return_error_response() {

        Long notExistingId = 10000L;

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(put(LINK_WITH_PARAMETER, notExistingId)
                                .header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(targetDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(content().json("{'status': 404}"))
                        .andExpect(jsonPath("errors[0]", is("Could not find EventModel with id: " + notExistingId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }
}