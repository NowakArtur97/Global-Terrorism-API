package com.nowakArtur97.globalTerrorismAPI.feature.province;


import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

@ApiModel(description = "Details about the Province")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvinceModel extends RepresentationModel<ProvinceModel> implements Province {

    @ApiModelProperty(notes = "The unique id of the Province")
    private Long id;

    @ApiModelProperty(notes = "The province's name")
    private String name;

    @ApiModelProperty(notes = "The province's country")
    private CountryModel country;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof ProvinceModel)) return false;

        ProvinceModel that = (ProvinceModel) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getCountry(), that.getCountry());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getName(), getCountry());
    }
}
