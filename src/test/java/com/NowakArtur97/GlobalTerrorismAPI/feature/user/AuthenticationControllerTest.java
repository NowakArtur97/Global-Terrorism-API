package com.NowakArtur97.GlobalTerrorismAPI.feature.user;

import com.NowakArtur97.GlobalTerrorismAPI.advice.AuthenticationControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.advice.RestResponseGlobalEntityExceptionHandler;
import com.NowakArtur97.GlobalTerrorismAPI.feature.user.AuthenticationController;
import com.NowakArtur97.GlobalTerrorismAPI.feature.user.AuthenticationRequest;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.CustomUserDetailsService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.mapper.ObjectTestMapper;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.jwt.JwtUtil;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("AuthenticationController_Tests")
class AuthenticationControllerTest {

    private final String AUTHENTICATION_BASE_PATH = "http://localhost:8080/api/v1/authentication";

    private MockMvc mockMvc;

    private AuthenticationController authenticationController;

    private RestResponseGlobalEntityExceptionHandler restResponseGlobalEntityExceptionHandler;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    private void setUp() {

        authenticationController = new AuthenticationController(customUserDetailsService, authenticationManager, jwtUtil);

        restResponseGlobalEntityExceptionHandler = new RestResponseGlobalEntityExceptionHandler();

        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController, restResponseGlobalEntityExceptionHandler)
                .setControllerAdvice(new AuthenticationControllerAdvice())
                .build();
    }

    @Test
    void when_authenticate_valid_user_should_generate_token() {

        String userName = "user123";
        String password = "Password1@";
        String email = "email@email.com";

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(userName, password, email);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userName, password);
        User userDetails = new User(userName, password, List.of(new SimpleGrantedAuthority("user")));
        String token = "generated token";

        when(customUserDetailsService.loadUserByUsername(userName)).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn(token);

        assertAll(
                () -> mockMvc
                        .perform(post(AUTHENTICATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(authenticationRequest))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("token", is(token))),
                () -> verify(customUserDetailsService, times(1)).loadUserByUsername(userName),
                () -> verifyNoMoreInteractions(customUserDetailsService),
                () -> verify(authenticationManager, times(1)).authenticate(usernamePasswordAuthenticationToken),
                () -> verifyNoMoreInteractions(authenticationManager),
                () -> verify(jwtUtil, times(1)).generateToken(userDetails),
                () -> verifyNoMoreInteractions(jwtUtil));
    }


    @Test
    void when_authenticate_not_existing_user_should_return_error_response() {

        String userName = "user123";
        String password = "Password1@";
        String email = "email@email.com";

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(userName, password, email);

        when(customUserDetailsService.loadUserByUsername(userName)).thenThrow(new UsernameNotFoundException("User with name/email: '" + userName + "' not found."));

        assertAll(
                () -> mockMvc
                        .perform(post(AUTHENTICATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(authenticationRequest))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(401)))
                        .andExpect(jsonPath("errors[0]", is("User with name/email: '" + userName + "' not found.")))
                        .andExpect(jsonPath("errors", IsCollectionWithSize.hasSize(1))),
                () -> verify(customUserDetailsService, times(1)).loadUserByUsername(userName),
                () -> verifyNoMoreInteractions(customUserDetailsService),
                () -> verifyNoInteractions(authenticationManager),
                () -> verifyNoInteractions(jwtUtil));
    }

    @Test
    void when_authenticate_user_with_incorrect_data_should_return_error_response() {

        String userName = "user123";
        String password = "Password1@";
        String email = "email@email.com";

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(userName, password, email);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userName, password);
        User userDetails = new User(userName, password, List.of(new SimpleGrantedAuthority("user")));

        when(customUserDetailsService.loadUserByUsername(userName)).thenReturn(userDetails);
        when(authenticationManager.authenticate(usernamePasswordAuthenticationToken)).thenThrow(new BadCredentialsException(""));

        assertAll(
                () -> mockMvc
                        .perform(post(AUTHENTICATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(authenticationRequest))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp").isNotEmpty())
                        .andExpect(jsonPath("status", is(401)))
                        .andExpect(jsonPath("errors[0]", is("Invalid login credentials.")))
                        .andExpect(jsonPath("errors", IsCollectionWithSize.hasSize(1))),
                () -> verify(customUserDetailsService, times(1)).loadUserByUsername(userName),
                () -> verifyNoMoreInteractions(customUserDetailsService),
                () -> verify(authenticationManager, times(1)).authenticate(usernamePasswordAuthenticationToken),
                () -> verifyNoMoreInteractions(authenticationManager),
                () -> verifyNoInteractions(jwtUtil));
    }
}