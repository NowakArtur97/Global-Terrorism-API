package com.NowakArtur97.GlobalTerrorismAPI.util.jw;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtilImpl implements JwtUtil {

    private static final String SECRET_KEY = "secret";

    private static final long TEN_HOURS = 1000 * 60 * 60 * 10;

    @Override
    public String generateToken(UserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();

        return createToken(claims, userDetails.getUsername());
    }

    @Override
    public Boolean validateToken(String token, UserDetails userDetail) {

        return (extractUsername(token).equals(userDetail.getUsername()) && !isTokenExpired(token));
    }

    @Override
    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Date extractExpirationDate(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

        Claims claim = extractAllClaims(token);

        return claimsResolver.apply(claim);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJwt(token).getBody();
    }

    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TEN_HOURS))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    private boolean isTokenExpired(String token) {

        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
