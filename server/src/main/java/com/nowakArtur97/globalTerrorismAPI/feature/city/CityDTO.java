package com.nowakArtur97.globalTerrorismAPI.feature.city;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.DTO;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(description = "Model responsible for City validation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CityDTO implements DTO, City {

    @ApiModelProperty(notes = "The city's name", required = true, example = "City")
    @NotBlank(message = "{city.name.notBlank}")
    private String name;

    @ApiModelProperty(notes = "The city's latitude", required = true, example = "1.00")
    @NotNull(message = "{city.latitude.notNull}")
    @Min(value = -90, message = "{city.latitude.min}")
    @Max(value = 90, message = "{city.latitude.max}")
    private Double latitude;

    @ApiModelProperty(notes = "The city's longitude", required = true, example = "1.00")
    @NotNull(message = "{city.longitude.notNull}")
    @Min(value = -180, message = "{city.longitude.min}")
    @Max(value = 180, message = "{city.longitude.max}")
    private Double longitude;

    @ApiModelProperty(notes = "The city's province", required = true)
    @Valid
    @NotNull(message = "{province.name.notBlank}")
    private ProvinceDTO province;
}
