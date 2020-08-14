package com.NowakArtur97.GlobalTerrorismAPI.model.response;


import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Province;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@ApiModel(description = "Details about the Province")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class ProvinceModel extends RepresentationModel<ProvinceModel> implements Province {

    @ApiModelProperty(notes = "The unique id of the Province")
    private Long id;

    @ApiModelProperty(notes = "The province's name")
    private String name;    

    @ApiModelProperty(notes = "The province's country")
    private CountryModel country;
}
