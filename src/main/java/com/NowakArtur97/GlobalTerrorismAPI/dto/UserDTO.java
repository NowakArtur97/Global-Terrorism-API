package com.NowakArtur97.GlobalTerrorismAPI.dto;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.ValidPassword;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@ApiModel(description = "Model responsible for User validation during registration")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @ApiModelProperty(notes = "The user's name")
    @NotBlank(message = "{user.name.notBlank}")
    private String userName;

    @ApiModelProperty(notes = "The user's password")
    @NotBlank(message = "{user.password.notBlank}")
    @ValidPassword(message = "{user.password.notBlank}")
    private String password;
}
