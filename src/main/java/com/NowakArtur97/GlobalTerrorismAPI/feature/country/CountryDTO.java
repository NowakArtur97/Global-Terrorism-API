package com.NowakArtur97.GlobalTerrorismAPI.feature.country;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTONode;
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
    @CountryExists(message = "{country.name.exists}")
    private String name;
}
