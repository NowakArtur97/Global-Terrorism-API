package com.nowakArtur97.globalTerrorismAPI.feature.country;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.DTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@ApiModel(description = "Model responsible for Country validation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountryDTO implements DTO, Country {

    @ApiModelProperty(notes = "The country's name", required = true, example = "Country")
    @CountryExists(message = "{country.name.exists}")
    private String name;
}
