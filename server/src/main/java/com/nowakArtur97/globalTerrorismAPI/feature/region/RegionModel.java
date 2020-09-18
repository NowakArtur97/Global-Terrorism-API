package com.nowakArtur97.globalTerrorismAPI.feature.region;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

@ApiModel(description = "Details about the Region")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionModel extends RepresentationModel<RegionModel> implements Region {

    @ApiModelProperty(notes = "The unique id of the Region")
    private Long id;

    @ApiModelProperty(notes = "The region's name")
    private String name;

    public RegionModel(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof RegionModel)) return false;

        RegionModel that = (RegionModel) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getName());
    }
}
