package com.NowakArtur97.GlobalTerrorismAPI.dto;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Target;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@ApiModel(description = "Model responsible for Target validation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetDTO implements DTONode, Target {

    @ApiModelProperty(notes = "The target's name", required = true, example = "Target")
    @NotBlank(message = "{target.target.notBlank}")
    private String target;

    @ApiModelProperty(notes = "The target's country of origin", required = true, example = "Country")
    @CountryExists(message = "{target.countryOfOrigin.exists}")
    private String countryOfOrigin;
}
