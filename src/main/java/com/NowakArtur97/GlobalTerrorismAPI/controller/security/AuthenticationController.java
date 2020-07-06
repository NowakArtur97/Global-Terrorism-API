package com.NowakArtur97.GlobalTerrorismAPI.controller.security;

import com.NowakArtur97.GlobalTerrorismAPI.constraintGroup.BasicUserValidationConstraints;
import com.NowakArtur97.GlobalTerrorismAPI.dto.UserDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.ErrorResponse;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.UserService;
import com.NowakArtur97.GlobalTerrorismAPI.tag.AuthenticationTag;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/authentication")
@Api(tags = {AuthenticationTag.RESOURCE})
@RequiredArgsConstructor
@Validated(BasicUserValidationConstraints.class)
public class AuthenticationController {

    private final UserService userService;

    @PostMapping
    @ApiOperation(value = "Create an account", notes = "Create an account. Required for generating API key.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully created a new account", response = String.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity registerUser(@ApiParam(value = "User data", name = "user", required = true) @RequestBody
                                       @Valid UserDTO userDTO) {

        userService.register(userDTO);

        return new ResponseEntity<>("Account created successfully", HttpStatus.CREATED);
    }
}
