package com.nowakArtur97.globalTerrorismAPI.feature.country;


import com.nowakArtur97.globalTerrorismAPI.feature.region.RegionModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

@ApiModel(description = "Details about the Country")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountryModel extends RepresentationModel<CountryModel> implements Country {

    @ApiModelProperty(notes = "The unique id of the Country")
    private Long id;

    @ApiModelProperty(notes = "The country's name")
    private String name;

    @ApiModelProperty(notes = "The country's region")
    private RegionModel region;

    public CountryModel(String name, RegionModel region) {
        this.name = name;
        this.region = region;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof CountryModel)) return false;

        CountryModel that = (CountryModel) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getRegion(), that.getRegion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getName(), getRegion());
    }
}
