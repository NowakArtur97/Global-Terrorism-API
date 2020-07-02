package com.NowakArtur97.GlobalTerrorismAPI.controller.security;

import com.NowakArtur97.GlobalTerrorismAPI.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/authentication")
@Slf4j
public class AuthenticationController {

    @PostMapping("/registration")
    public ResponseEntity registerUser(@RequestBody @Valid UserDTO userDTO) {

        log.info(userDTO.toString());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
