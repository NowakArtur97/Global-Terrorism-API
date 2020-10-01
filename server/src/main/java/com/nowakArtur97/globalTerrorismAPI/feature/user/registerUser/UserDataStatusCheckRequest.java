package com.nowakArtur97.globalTerrorismAPI.feature.user.registerUser;

import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@ApiModel(description = "Model responsible for User status check before registration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class UserDataStatusCheckRequest implements User {

    @ApiModelProperty(notes = "The user's name for status check")
    private String userName;

    @ApiModelProperty(notes = "The user's email for status check")
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDataStatusCheckRequest)) return false;

        UserDataStatusCheckRequest that = (UserDataStatusCheckRequest) o;
        return Objects.equals(getUserName(), that.getUserName()) &&
                Objects.equals(getEmail(), that.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserName(), getEmail());
    }
}