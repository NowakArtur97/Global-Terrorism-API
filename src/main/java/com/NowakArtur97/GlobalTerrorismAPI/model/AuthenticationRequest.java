package com.NowakArtur97.GlobalTerrorismAPI.model;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@ApiModel(description = "User data required for Authentication")
@Data
@AllArgsConstructor
public class AuthenticationRequest {

    private String username;
    private String password;
}
