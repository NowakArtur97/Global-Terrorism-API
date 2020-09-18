package com.nowakArtur97.globalTerrorismAPI.feature.target;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.DTO;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(description = "Model responsible for Target validation")
@Getter
@Setter
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
