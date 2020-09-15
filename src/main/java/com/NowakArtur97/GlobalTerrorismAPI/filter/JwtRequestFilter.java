package com.NowakArtur97.GlobalTerrorismAPI.filter;

import com.NowakArtur97.GlobalTerrorismAPI.common.exception.JwtTokenMissingException;
import com.NowakArtur97.GlobalTerrorismAPI.feature.user.CustomUserDetailsService;
import com.NowakArtur97.GlobalTerrorismAPI.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtUtil jwtUtil;

    @Value("#{'${jwt.ignoredEndpoints}'.split(',')}")
    private List<String> ignoredEndpointsList;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        String userName;
        String jwt;

        if (isBearerTypeAuthorization(authorizationHeader)) {

            jwt = authorizationHeader.substring(7);
            userName = jwtUtil.extractUserName(jwt);
        } else {
            throw new JwtTokenMissingException("Missing JWT token in request headers.");
        }

        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);

            if (jwtUtil.isTokenValid(jwt, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getRequestURI().substring(request.getContextPath().length());

        for (String endpoint : ignoredEndpointsList) {
            if (path.contains(endpoint)) {

                return true;
            }
        }

        return false;
    }

    private boolean isBearerTypeAuthorization(String authorizationHeader) {

        return authorizationHeader != null && authorizationHeader.startsWith("Bearer ");
    }
}
