package com.NowakArtur97.GlobalTerrorismAPI.dto;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Province;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(description = "Model responsible for Province validation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceDTO implements DTONode, Province {

    @ApiModelProperty(notes = "The province's name", required = true, example = "Province")
    @NotBlank(message = "{province.name.notBlank}")
    private String name;

    @ApiModelProperty(notes = "The province's country", required = true)
    @Valid
    @NotNull(message = "{country.name.exists}")
    private CountryDTO country;
}
