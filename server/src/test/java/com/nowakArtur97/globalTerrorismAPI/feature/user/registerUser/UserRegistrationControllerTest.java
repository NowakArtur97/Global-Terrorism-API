package com.nowakArtur97.globalTerrorismAPI.feature.user.registerUser;

import com.nowakArtur97.globalTerrorismAPI.advice.RestResponseGlobalEntityExceptionHandler;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.UserNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.UserBuilder;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.nowakArtur97.globalTerrorismAPI.testUtil.mapper.ObjectTestMapper;
import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("UserRegistrationController_Tests")
class UserRegistrationControllerTest {

    private final String AUTHENTICATION_BASE_PATH = "http://localhost:8080/api/v1/registration";

    private MockMvc mockMvc;

    private UserRegistrationController userRegistrationController;

    private RestResponseGlobalEntityExceptionHandler restResponseGlobalEntityExceptionHandler;

    @Mock
    private UserService userService;

    private static UserBuilder userBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        userBuilder = new UserBuilder();
    }

    @BeforeEach
    private void setUp() {

        userRegistrationController = new UserRegistrationController(userService);

        restResponseGlobalEntityExceptionHandler = new RestResponseGlobalEntityExceptionHandler();

        mockMvc = MockMvcBuilders.standaloneSetup(userRegistrationController, restResponseGlobalEntityExceptionHandler)
                .build();
    }

    @Test
    void when_register_valid_user_should_register_user() {

        UserDTO userDTO = (UserDTO) userBuilder.build(ObjectType.DTO);
        UserNode userNode = (UserNode) userBuilder.build(ObjectType.NODE);

        when(userService.register(userDTO)).thenReturn(userNode);

        assertAll(
                () -> mockMvc
                        .perform(post(AUTHENTICATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(content().string("Account created successfully")),
                () -> verify(userService, times(1)).register(userDTO),
                () -> verifyNoMoreInteractions(userService));
    }

    @Test
    void when_register_user_with_null_fields_should_return_error_response() {

        UserDTO userDTO = (UserDTO) userBuilder.withUserName(null).withPassword(null).withMatchingPassword(null).withEmail(null)
                .build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(AUTHENTICATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("{user.name.notBlank}")))
                        .andExpect(jsonPath("errors", hasItem("{user.password.notBlank}")))
                        .andExpect(jsonPath("errors", hasItem("{user.matchingPassword.notBlank}")))
                        .andExpect(jsonPath("errors", hasItem("{user.email.notBlank}")))
                        .andExpect(jsonPath("errors", hasSize(4))),
                () -> verifyNoInteractions(userService));
    }

    @ParameterizedTest(name = "{index}: For User name: {0}")
    @EmptySource
    @ValueSource(strings = {" "})
    void when_register_user_with_blank_user_name_should_return_error_response(String invalidUserName) {

        UserDTO userDTO = (UserDTO) userBuilder.withUserName(invalidUserName).build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(AUTHENTICATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("{user.name.notBlank}")))
                        .andExpect(jsonPath("errors", hasItem("{user.name.size}")))
                        .andExpect(jsonPath("errors", hasSize(2))),
                () -> verifyNoInteractions(userService));
    }

    @Test
    void when_register_user_with_too_short_user_name_should_return_error_response() {

        UserDTO userDTO = (UserDTO) userBuilder.withUserName("u").build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(AUTHENTICATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("{user.name.size}")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verifyNoInteractions(userService));
    }

    @Test
    void when_register_user_with_blank_email_should_return_error_response() {

        UserDTO userDTO = (UserDTO) userBuilder.withEmail("     ").build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(AUTHENTICATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors", hasItem("{user.email.notBlank}")))
                        .andExpect(jsonPath("errors", hasItem("{user.email.wrongFormat}")))
                        .andExpect(jsonPath("errors", hasSize(2))),
                () -> verifyNoInteractions(userService));
    }

    @ParameterizedTest(name = "{index}: For User email: {0}")
    @ValueSource(strings = {"wrongformat", "wrong.format"})
    void when_register_user_with_an_incorrect_format_email_should_return_error_response(String invalidEmail) {

        UserDTO userDTO = (UserDTO) userBuilder.withEmail(invalidEmail).build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(AUTHENTICATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("{user.email.wrongFormat}")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verifyNoInteractions(userService));
    }

    @ParameterizedTest(name = "{index}: For User password: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void when_register_user_with_blank_password_should_return_error_response(String invalidPassword) {

        UserDTO userDTO = (UserDTO) userBuilder.withPassword(invalidPassword).build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(AUTHENTICATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("{user.password.notBlank}")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verifyNoInteractions(userService));
    }


    @ParameterizedTest(name = "{index}: For User matching password: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void when_register_user_with_blank_matching_password_should_return_error_response(String invalidMatchingPassword) {

        UserDTO userDTO = (UserDTO) userBuilder.withMatchingPassword(invalidMatchingPassword).build(ObjectType.DTO);

        assertAll(
                () -> mockMvc
                        .perform(post(AUTHENTICATION_BASE_PATH)
                                .content(ObjectTestMapper.asJsonString(userDTO))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("{user.matchingPassword.notBlank}")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verifyNoInteractions(userService));
    }
}