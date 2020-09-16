package com.nowakArtur97.globalTerrorismAPI.feature.city;


import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@ApiModel(description = "Details about the City")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
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
}
