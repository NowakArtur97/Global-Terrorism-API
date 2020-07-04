package com.NowakArtur97.GlobalTerrorismAPI.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(description = "User data required for Authentication")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {

    @ApiModelProperty(notes = "The user's name")
    private String userName;

    @ApiModelProperty(notes = "The user's password", required = true)
    private String password;

    @ApiModelProperty(notes = "The user's email")
    private String email;
}
