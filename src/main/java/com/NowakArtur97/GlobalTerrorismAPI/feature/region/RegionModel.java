package com.NowakArtur97.GlobalTerrorismAPI.feature.region;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@ApiModel(description = "Details about the Region")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class RegionModel extends RepresentationModel<RegionModel> implements Region {

    @ApiModelProperty(notes = "The unique id of the Region")
    private Long id;

    @ApiModelProperty(notes = "The region's name")
    private String name;

    public RegionModel(String name) {
        this.name = name;
    }
}
