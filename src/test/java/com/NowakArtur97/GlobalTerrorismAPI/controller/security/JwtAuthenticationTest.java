package com.NowakArtur97.GlobalTerrorismAPI.controller.security;

import com.NowakArtur97.GlobalTerrorismAPI.node.RoleNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.UserNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.*;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.jwt.JwtUtilImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("AuthenticationController_Tests")
class JwtAuthenticationTest {

    private final String TARGET_BASE_PATH = "http://localhost:8080/api/v1/targets";
    private final String EVENT_BASE_PATH = "http://localhost:8080/api/v1/events";
    private final String GROUP_BASE_PATH = "http://localhost:8080/api/v1/groups";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtilImpl jwtUtil;

    private final static UserNode userNode = new UserNode("user1234", "Password1234!", "user1234email@.com",
            Set.of(new RoleNode("user")));

    @BeforeAll
    private static void setUpUser(@Autowired UserRepository userRepository) {

        userRepository.save(userNode);
    }

    @AfterAll
    private static void tearDown(@Autowired UserRepository userRepository, @Autowired GroupRepository groupRepository,
                                 @Autowired CountryRepository countryRepository, @Autowired EventRepository eventRepository,
                                 @Autowired TargetRepository targetRepository) {

        userRepository.deleteAll();

        countryRepository.deleteAll();

        groupRepository.deleteAll();

        eventRepository.deleteAll();

        targetRepository.deleteAll();
    }

    @ParameterizedTest(name = "{index}: For URL: {0} with JWT  token should return content")
    @ValueSource(strings = {TARGET_BASE_PATH, EVENT_BASE_PATH, GROUP_BASE_PATH})
    void when_valid_jwt_token_is_in_request_headers_should_return_content(String url) {

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(get(url).header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("content").exists())
                        .andExpect(jsonPath("errors").doesNotExist()));
    }

    @ParameterizedTest(name = "{index}: For URL: {0} without JWT  token should return error response")
    @ValueSource(strings = {TARGET_BASE_PATH, EVENT_BASE_PATH, GROUP_BASE_PATH})
    void when_jwt_token_is_missing_in_request_headers_should_return_error_response(String url) {

        assertAll(
                () -> mockMvc
                        .perform(get(url).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(401)))
                        .andExpect(jsonPath("errors[0]", is("Missing JWT token in request headers.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For URL: {0} with invalid JWT  token should return content")
    @ValueSource(strings = {TARGET_BASE_PATH, EVENT_BASE_PATH, GROUP_BASE_PATH})
    void when_valid_jwt_token_is_in_request_headers_without_authorization_type_should_return_content(String url) {

        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user"))));

        assertAll(
                () -> mockMvc
                        .perform(get(url).header("Authorization", token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(401)))
                        .andExpect(jsonPath("errors[0]", is("Missing JWT token in request headers.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For URL: {0} with invalid JWT  token should return error response")
    @ValueSource(strings = {TARGET_BASE_PATH, EVENT_BASE_PATH, GROUP_BASE_PATH})
    void when_invalid_jwt_token_is_in_request_headers_should_return_error_response(String url) {

        String invalidToken = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
                List.of(new SimpleGrantedAuthority("user")))) + "ASSD!@#asd";

        assertAll(
                () -> mockMvc
                        .perform(get(url).header("Authorization", "Bearer " + invalidToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]",
                                is("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For URL: {0} with invalid JWT  token with wrong format should return error response")
    @ValueSource(strings = {TARGET_BASE_PATH, EVENT_BASE_PATH, GROUP_BASE_PATH})
    void when_invalid_jwt_token_with_wrong_format_is_in_request_headers_should_return_error_response(String url) {

        String invalidToken = "invalidToken";

        assertAll(
                () -> mockMvc
                        .perform(get(url).header("Authorization", "Bearer " + invalidToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]",
                                is("JWT strings must contain exactly 2 period characters. Found: 0")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }

    @ParameterizedTest(name = "{index}: For URL: {0} with expired JWT  token should return error response")
    @ValueSource(strings = {TARGET_BASE_PATH, EVENT_BASE_PATH, GROUP_BASE_PATH})
    void when_expired_jwt_token_is_in_request_headers_should_return_error_response(String url) {

        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlcjEyMzQiLCJleHAiOjE1OTQ0MDI3MTMsImlhdCI6MTU5NDM2NjcxM30.b07GCRtf9-ba5gVQ789-6Do9PQ3YsK2nyI7Hj8QkQFA";

        assertAll(
                () -> mockMvc
                        .perform(get(url).header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", containsString("JWT expired")))
                        .andExpect(jsonPath("errors", hasSize(1))));
    }
}