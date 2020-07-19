package com.NowakArtur97.GlobalTerrorismAPI.dto;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.validation.CountryExists;
import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Country;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(description = "Model responsible for Country validation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryDTO implements DTONode, Country {

    @ApiModelProperty(notes = "The country's name", required = true, example = "Country")
    @CountryExists(message = "{country.countryOfOrigin.exists}")
    private String name;
}
