package com.nowakArtur97.globalTerrorismAPI.feature.user.registerUser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "Model responsible for User status response before registration")
@Getter
@Setter
@AllArgsConstructor
class UserDataStatusCheckResponse implements User {

    @ApiModelProperty(notes = "The user's name status")
    @JsonProperty(value = "isUserNameAvailable")
    private boolean isUserNameAvailable;

    @ApiModelProperty(notes = "The user's email status")
    @JsonProperty(value = "isEmailAvailable")
    private boolean isEmailAvailable;
}