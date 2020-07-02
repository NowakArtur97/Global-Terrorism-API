package com.NowakArtur97.GlobalTerrorismAPI.util.jw;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.function.Function;

public interface JwtUtil {

    String generateToken(UserDetails userDetails);

    Boolean validateToken(String token, UserDetails userDetail);

    String extractUsername(String token);

    Date extractExpirationDate(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
}
