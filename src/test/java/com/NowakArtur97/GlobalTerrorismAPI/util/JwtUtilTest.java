package com.NowakArtur97.GlobalTerrorismAPI.util.jwt;

import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("JwtUtilImpl_Tests")
class JwtUtilImplTest {

    private JwtUtil jwtUtilI;

    private final String SECRET_KEY = "secret";

    @BeforeEach
    private void setUp() {

        jwtUtilI = new JwtUtilImpl();

        ReflectionTestUtils.setField(jwtUtilI, "secretKey", SECRET_KEY);
    }

    @Test
    void when_generate_token_should_return_token() {

        User userDetails = new User("user123", "Password1@",
                List.of(new SimpleGrantedAuthority("user")));

        String expectedToken = jwtUtilI.generateToken(userDetails);

        assertNotNull(expectedToken, () -> "shouldn't return token as null, but was");
    }

    @Test
    void when_generate_token_should_return_valid_token() {

        User userDetails = new User("user123", "Password1@",
                List.of(new SimpleGrantedAuthority("user")));

        String expectedToken = jwtUtilI.generateToken(userDetails);

        assertTrue(jwtUtilI.isTokenValid(expectedToken, userDetails), () -> "should return valid token, but was: " + expectedToken);
    }

    @Test
    void when_generate_token_should_return_token_with_user_details() {

        User userDetails = new User("user123", "Password1@",
                List.of(new SimpleGrantedAuthority("user")));

        String expectedToken = jwtUtilI.generateToken(userDetails);

        String expectedUserName = jwtUtilI.extractUserName(expectedToken);

        assertEquals(userDetails.getUsername(), expectedUserName, () -> "should return token with user name: " + userDetails.getUsername() + ", but was: " + expectedUserName);
    }

    @Test
    void when_generate_token_should_return_token_with_expiration_date() {

        User userDetails = new User("user123", "Password1@",
                List.of(new SimpleGrantedAuthority("user")));

        String expectedToken = jwtUtilI.generateToken(userDetails);

        Date expectedExpirationDate = jwtUtilI.extractExpirationDate(expectedToken);

        assertNotNull(expectedExpirationDate, () -> "shouldn't return token expiration date as null, but was");
    }
}
