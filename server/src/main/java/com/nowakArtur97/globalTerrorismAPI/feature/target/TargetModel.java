package com.nowakArtur97.globalTerrorismAPI.feature.target;

import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

@ApiModel(description = "Details about the Target")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetModel extends RepresentationModel<TargetModel> implements Target {

    @ApiModelProperty(notes = "The unique id of the Target")
    private Long id;

    @ApiModelProperty(notes = "The target's name")
    private String target;

    @ApiModelProperty(notes = "The target's country of origin")
    private CountryModel countryOfOrigin;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof TargetModel)) return false;

        TargetModel that = (TargetModel) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getTarget(), that.getTarget());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getTarget());
    }
}
