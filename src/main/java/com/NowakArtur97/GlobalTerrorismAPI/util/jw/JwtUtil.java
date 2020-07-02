package com.NowakArtur97.GlobalTerrorismAPI.util.jw;

import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.function.Function;

public interface JwtUtil {

    String extractUsername(String token);

    Date extractExpirationDate(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
}
