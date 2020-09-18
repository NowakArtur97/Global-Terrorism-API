package com.nowakArtur97.globalTerrorismAPI.feature.province;

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

@ApiModel(description = "Model responsible for Province validation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceDTO implements DTO, Province {

    @ApiModelProperty(notes = "The province's name", required = true, example = "Province")
    @NotBlank(message = "{province.name.notBlank}")
    private String name;

    @ApiModelProperty(notes = "The province's country", required = true)
    @Valid
    @NotNull(message = "{country.name.notBlank}")
    private CountryDTO country;
}
