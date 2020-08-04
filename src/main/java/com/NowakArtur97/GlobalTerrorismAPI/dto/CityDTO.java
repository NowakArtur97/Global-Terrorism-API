package com.NowakArtur97.GlobalTerrorismAPI.dto;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.City;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@ApiModel(description = "Model responsible for City validation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityDTO implements DTONode, City {

    @ApiModelProperty(notes = "The city's name", required = true, example = "City")
    @NotBlank(message = "{city.name.notBlank}")
    private String name;

    @ApiModelProperty(notes = "The city's latitude", required = true, example = "1.00")
    @Min(value = -90, message = "{city.longitude.min}")
    @Max(value = 90, message = "{city.longitude.max}")
    private double latitude;

    @ApiModelProperty(notes = "The city's longitude", required = true, example = "1.00")
    @Min(value = -180, message = "{city.longitude.min}")
    @Max(value = 180, message = "{city.longitude.max}")
    private double longitude;
}
