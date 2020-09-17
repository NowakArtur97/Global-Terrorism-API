package com.nowakArtur97.globalTerrorismAPI.feature.user.registerUser;

import com.nowakArtur97.globalTerrorismAPI.feature.user.registerUser.validation.PasswordsMatch;
import com.nowakArtur97.globalTerrorismAPI.feature.user.registerUser.validation.UniqueEmail;
import com.nowakArtur97.globalTerrorismAPI.feature.user.registerUser.validation.UniqueUserName;
import com.nowakArtur97.globalTerrorismAPI.feature.user.registerUser.validation.ValidPasswords;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@ApiModel(description = "Model responsible for User validation during registration")
@Data
@NoArgsConstructor
@AllArgsConstructor
@PasswordsMatch(message = "{user.password.notMatch}", groups = BasicUserValidationConstraints.class)
@ValidPasswords(groups = BasicUserValidationConstraints.class)
public class UserDTO implements User {

    @ApiModelProperty(notes = "The user's name")
    @UniqueUserName(message = "{user.name.unique}", groups = BasicUserValidationConstraints.class)
    @NotBlank(message = "{user.name.notBlank}")
    @Size(min = 5, max = 20, message = "{user.name.size}")
    private String userName;

    @ApiModelProperty(notes = "The user's password")
    @NotBlank(message = "{user.password.notBlank}")
    private String password;

    @ApiModelProperty(notes = "The user's password for confirmation")
    @NotBlank(message = "{user.matchingPassword.notBlank}")
    private String matchingPassword;

    @ApiModelProperty(notes = "The user's email")
    @Email(message = "{user.email.wrongFormat}")
    @NotBlank(message = "{user.email.notBlank}")
    @UniqueEmail(message = "{user.email.unique}", groups = BasicUserValidationConstraints.class)
    private String email;
}
