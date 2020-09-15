package com.NowakArtur97.GlobalTerrorismAPI.feature.target;

import com.NowakArtur97.GlobalTerrorismAPI.common.baseModel.DTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(description = "Model responsible for Target validation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetDTO implements DTO, Target {

    @ApiModelProperty(notes = "The target's name", required = true, example = "Target")
    @NotBlank(message = "{target.target.notBlank}")
    private String target;

    @ApiModelProperty(notes = "The target's country of origin", required = true, example = "Country")
    @Valid
    @NotNull(message = "{country.name.notBlank}")
    private CountryDTO countryOfOrigin;
}
