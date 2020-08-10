package com.NowakArtur97.GlobalTerrorismAPI.dto;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.validation.RegionExists;
import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Region;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(description = "Model responsible for Region validation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionDTO implements DTONode, Region {

    @ApiModelProperty(notes = "The region's name", required = true, example = "Region")
    @RegionExists(message = "{region.name.exists}")
    private String name;
}
