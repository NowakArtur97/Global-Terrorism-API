package com.nowakArtur97.globalTerrorismAPI.feature.user.registerUser;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.ErrorResponse;
import com.nowakArtur97.globalTerrorismAPI.common.util.JwtUtil;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.AuthenticationResponse;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.CustomUserDetailsService;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.UserNode;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/registration")
@Api(tags = {UserRegistrationTag.RESOURCE})
@RequiredArgsConstructor
@Validated(BasicUserValidationConstraints.class)
class UserRegistrationController {

    private final UserService userService;

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    @ApiOperation(value = "Create an account", notes = "Create an account. Required for generating API key.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully created a new account", response = String.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<AuthenticationResponse> registerUser(@ApiParam(value = "User data", name = "user", required = true)
                                                               @RequestBody @Valid UserDTO userDTO) {

        UserNode newUser = userService.register(userDTO);

        UserDetails userDetails = new User(newUser.getUserName(), newUser.getPassword(),
                customUserDetailsService.getAuthorities(newUser.getRoles()));

        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(token));
    }

    @PostMapping("/checkUserData")
    @ApiOperation(value = "Check User data before registration", notes = "Check User data before registration.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully checked data", response = UserDataStatusCheckResponse.class),
            @ApiResponse(code = 400, message = "Incorrectly entered data", response = ErrorResponse.class)})
    public ResponseEntity<UserDataStatusCheckResponse> checkUserData(
            @ApiParam(value = "User data", name = "userData", required = true)
            @RequestBody UserDataStatusCheckRequest userData) {

        return ResponseEntity.ok(userService.checkUserData(userData));
    }
}
