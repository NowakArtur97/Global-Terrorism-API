package com.nowakArtur97.globalTerrorismAPI.feature.target;

import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@ApiModel(description = "Details about the Target")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class TargetModel extends RepresentationModel<TargetModel> implements Target {

    @ApiModelProperty(notes = "The unique id of the Target")
    private Long id;

    @ApiModelProperty(notes = "The target's name")
    private String target;

    @ApiModelProperty(notes = "The target's country of origin")
    private CountryModel countryOfOrigin;
}
