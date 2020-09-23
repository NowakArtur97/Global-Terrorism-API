package com.nowakArtur97.globalTerrorismAPI.feature.user.loginUser;

import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@ApiModel(description = "User data required for Authentication")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest implements User {

    @ApiModelProperty(notes = "The user's name")
    private String userName;

    @ApiModelProperty(notes = "The user's password", required = true)
    private String password;

    @ApiModelProperty(notes = "The user's email")
    private String email;
}
