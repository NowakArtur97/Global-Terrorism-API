package com.NowakArtur97.GlobalTerrorismAPI.controller.event;

import com.NowakArtur97.GlobalTerrorismAPI.dto.CountryDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.node.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.RoleNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.UserNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.CountryRepository;
import com.NowakArtur97.GlobalTerrorismAPI.repository.UserRepository;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.CountryBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.mapper.ObjectTestMapper;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.jwt.JwtUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("EventController_Tests")
class EventControllerPostMethodTest {

    private final String EVENT_BASE_PATH = "http://localhost:8080/api/v1/events";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    private static CountryBuilder countryBuilder;
    private static TargetBuilder targetBuilder;
    private static EventBuilder eventBuilder;

    private static UserNode userNode = new UserNode("user1234", "Password1234!", "user1234email@.com",
            Set.of(new RoleNode("user")));

    private static CountryNode countryNode = new CountryNode("country");

    @BeforeAll
    private static void setUpBuilders() {

        countryBuilder = new CountryBuilder();
        targetBuilder = new TargetBuilder();
        eventBuilder = new EventBuilder();
    }

    @BeforeAll
    private static void setUp(@Autowired UserRepository userRepository, @Autowired CountryRepository countryRepository) {

        userRepository.save(userNode);

        countryRepository.save(countryNode);
    }

    @AfterAll
    private static void tearDown(@Autowired UserRepository userRepository, @Autowired CountryRepository countryRepository) {

        userRepository.delete(userNode);

        countryRepository.delete(countryNode);
    }

    @Test
    void when_add_valid_event_should_return_new_event_as_model() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(post(EVENT_BASE_PATH).header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", notNullValue()))
                        .andExpect(jsonPath("links[1].href", notNullValue()))
                        .andExpect(jsonPath("id", notNullValue()))
                        .andExpect(jsonPath("summary", is(eventDTO.getSummary())))
                        .andExpect(jsonPath("motive", is(eventDTO.getMotive())))
                        .andExpect(jsonPath("date",
                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        .format(eventDTO.getDate().toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDate()))))
                        .andExpect(jsonPath("isSuicidal", is(eventDTO.getIsSuicidal())))
                        .andExpect(jsonPath("isSuccessful", is(eventDTO.getIsSuccessful())))
                        .andExpect(jsonPath("isPartOfMultipleIncidents", is(eventDTO.getIsPartOfMultipleIncidents())))
                        .andExpect(jsonPath("target.links[0].href", notNullValue()))
                        .andExpect(jsonPath("target.id", notNullValue()))
                        .andExpect(jsonPath("target.target", is(targetDTO.getTarget()))));
    }

    @Test
    void when_add_event_with_null_fields_should_return_errors() {

        EventDTO eventDTO = (EventDTO) eventBuilder.withId(null).withSummary(null).withMotive(null).withDate(null)
                .withIsPartOfMultipleIncidents(null).withIsSuccessful(null).withIsSuicidal(null).withTarget(null)
                .build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(post(EVENT_BASE_PATH).header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
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
                        .andExpect(jsonPath("errors", hasSize(7))));
    }

    @ParameterizedTest(name = "{index}: Event Target Country: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void when_add_event_with_not_existing_country_should_return_errors(String invalidCountryName) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(invalidCountryName).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(post(EVENT_BASE_PATH).header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("A country with the provided name does not exist.")))
                        .andExpect(jsonPath("errors", Matchers.hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Event Target: {0} should have violation")
    @EmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_add_event_with_invalid_target_should_return_errors(String invalidTarget) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget(invalidTarget).withCountry(countryDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(post(EVENT_BASE_PATH).header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Target name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Event summary: {0} should have violation")
    @EmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_add_event_with_invalid_summary_should_return_errors(String invalidSummary) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withSummary(invalidSummary).withTarget(targetDTO)
                .build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(post(EVENT_BASE_PATH).header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Event summary cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For Event motive: {0} should have violation")
    @EmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void when_add_event_with_invalid_motive_should_return_errors(String invalidMotive) {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withMotive(invalidMotive).withTarget(targetDTO)
                .build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(post(EVENT_BASE_PATH).header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("Event motive cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @Test
    void when_add_event_with_date_in_the_future_should_return_errors() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(2090, Calendar.FEBRUARY, 1);
        Date invalidDate = calendar.getTime();
        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        EventDTO eventDTO = (EventDTO) eventBuilder.withDate(invalidDate).withTarget(targetDTO).build(ObjectType.DTO);

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(post(EVENT_BASE_PATH).header("Authorization", "Bearer " + token)
                                .content(ObjectTestMapper.asJsonString(eventDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Event date cannot be in the future.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }
}
