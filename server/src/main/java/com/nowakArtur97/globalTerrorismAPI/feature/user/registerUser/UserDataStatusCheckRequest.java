package com.nowakArtur97.globalTerrorismAPI.feature.user.registerUser;

import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "Model responsible for User status check before registration")
@Getter
@Setter
@AllArgsConstructor
public class UserDataStatusCheckRequest implements User {

    @ApiModelProperty(notes = "The user's name for status check")
    private String userName;

    @ApiModelProperty(notes = "The user's email for status check")
    private String email;
}