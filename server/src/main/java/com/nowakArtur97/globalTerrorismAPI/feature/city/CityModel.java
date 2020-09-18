package com.nowakArtur97.globalTerrorismAPI.feature.city;


import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

@ApiModel(description = "Details about the City")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityModel extends RepresentationModel<CityModel> implements City {

    @ApiModelProperty(notes = "The unique id of the City")
    private Long id;

    @ApiModelProperty(notes = "The city's name")
    private String name;

    @ApiModelProperty(notes = "The city's latitude")
    private double latitude;

    @ApiModelProperty(notes = "The city's longitude")
    private double longitude;

    @ApiModelProperty(notes = "The city's province")
    private ProvinceModel province;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof CityModel)) return false;

        CityModel cityModel = (CityModel) o;
        return Double.compare(cityModel.getLatitude(), getLatitude()) == 0 &&
                Double.compare(cityModel.getLongitude(), getLongitude()) == 0 &&
                Objects.equals(getId(), cityModel.getId()) &&
                Objects.equals(getName(), cityModel.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getName(), getLatitude(), getLongitude());
    }
}
