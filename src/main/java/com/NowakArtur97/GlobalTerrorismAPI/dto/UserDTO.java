package com.NowakArtur97.GlobalTerrorismAPI.dto;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.UniqueEmail;
import com.NowakArtur97.GlobalTerrorismAPI.annotation.UniqueUserName;
import com.NowakArtur97.GlobalTerrorismAPI.annotation.ValidPassword;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@ApiModel(description = "Model responsible for User validation during registration")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @ApiModelProperty(notes = "The user's name")
    @NotBlank(message = "{user.name.notBlank}")
    @UniqueUserName(message = "{user.name.unique}")
    private String userName;

    @ApiModelProperty(notes = "The user's password")
    @ValidPassword
    private String password;

    @ApiModelProperty(notes = "The user's email")
    @Email(message = "{user.email.wrongFormat}")
    @UniqueEmail(message = "{user.email.unique}")
    private String email;
}
