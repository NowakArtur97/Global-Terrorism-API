package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.feature.user.UserDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.user.UserNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.UserBuilder;
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
class UserMapperTest {

    private ObjectMapper objectMapper;

    @Mock
    private ModelMapper modelMapper;

    private static UserBuilder userBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        userBuilder = new UserBuilder();
    }

    @BeforeEach
    private void setUp() {

        objectMapper = new ObjectMapperImpl(modelMapper);
    }

    @Test
    void when_map_user_dto_to_node_should_return_user_node() {

        UserDTO userDTO = (UserDTO) userBuilder.build(ObjectType.DTO);
        UserNode userNodeExpected = (UserNode) userBuilder.withId(null).build(ObjectType.NODE);

        when(modelMapper.map(userDTO, UserNode.class)).thenReturn(userNodeExpected);

        UserNode userNodeActual = objectMapper.map(userDTO, UserNode.class);

        assertAll(
                () -> assertEquals(userNodeExpected.getUserName(), userNodeActual.getUserName(),
                        () -> "should return user with user name: " + userNodeExpected.getUserName() + ", but was: " + userNodeActual.getUserName()),
                () -> assertEquals(userNodeExpected.getPassword(), userNodeActual.getPassword(),
                        () -> "should return user with user password: " + userNodeExpected.getPassword() + ", but was: " + userNodeActual.getPassword()),
                () -> assertEquals(userNodeExpected.getEmail(), userNodeActual.getEmail(),
                        () -> "should return user with user email: " + userNodeExpected.getEmail() + ", but was: " + userNodeActual.getEmail()),
                () -> assertTrue(userNodeActual.getRoles().isEmpty(),
                        () -> "should return user with empty roles list, but was: " + userNodeActual.getRoles()),
                () -> assertEquals(userNodeExpected.getRoles(), userNodeActual.getRoles(),
                        () -> "should return user with user roles: " + userNodeExpected.getRoles() + ", but was: " + userNodeActual.getRoles()),
                () -> verify(modelMapper, times(1)).map(userDTO, UserNode.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @Test
    void when_map_user_node_to_dto_should_return_user_dto() {

        UserNode userNode = (UserNode) userBuilder.build(ObjectType.NODE);
        UserDTO userDTOExpected = (UserDTO) userBuilder.build(ObjectType.DTO);

        when(modelMapper.map(userNode, UserDTO.class)).thenReturn(userDTOExpected);

        UserDTO userDTOActual = objectMapper.map(userNode, UserDTO.class);

        assertAll(
                () -> assertEquals(userDTOExpected.getUserName(), userDTOActual.getUserName(),
                        () -> "should return user with user name: " + userDTOExpected.getUserName() + ", but was: " + userDTOActual.getUserName()),
                () -> assertEquals(userDTOExpected.getPassword(), userDTOActual.getPassword(),
                        () -> "should return user with user password: " + userDTOExpected.getPassword() + ", but was: " + userDTOActual.getPassword()),
                () -> assertEquals(userDTOExpected.getEmail(), userDTOActual.getEmail(),
                        () -> "should return user with user email: " + userDTOExpected.getEmail() + ", but was: " + userDTOActual.getEmail()),
                () -> verify(modelMapper, times(1)).map(userNode, UserDTO.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }
}