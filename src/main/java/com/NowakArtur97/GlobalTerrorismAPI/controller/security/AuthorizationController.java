package com.NowakArtur97.GlobalTerrorismAPI.controller.security;

import com.NowakArtur97.GlobalTerrorismAPI.model.request.AuthenticationRequest;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/authorization")
@RequiredArgsConstructor
@Slf4j
public class AuthorizationController {

    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping("/authenticate")
    public ResponseEntity loginUser(@RequestBody AuthenticationRequest authenticationRequest) {

        UserDetails user = customUserDetailsService.loadUserByUsername(authenticationRequest.getUserName());

        log.info(user.getUsername());
        log.info(user.getPassword());
        log.info(user.getAuthorities().toString());

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
