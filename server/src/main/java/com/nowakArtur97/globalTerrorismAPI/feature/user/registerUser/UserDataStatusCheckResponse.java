package com.nowakArtur97.globalTerrorismAPI.feature.user.registerUser;

import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "Model responsible for User status response registration")
@Getter
@Setter
@AllArgsConstructor
public class UserDataStatusCheckResponse implements User {

    @ApiModelProperty(notes = "The user's name status")
    private boolean isUserNameAvailable;

    @ApiModelProperty(notes = "The user's email status")
    private boolean isEmailAvailable;
}