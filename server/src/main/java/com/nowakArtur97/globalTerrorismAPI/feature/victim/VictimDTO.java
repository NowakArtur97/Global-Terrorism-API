package com.nowakArtur97.globalTerrorismAPI.feature.victim;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.DTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@ApiModel(description = "Model responsible for Victim validation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidNumberOfPerpetratorsFatalities(message = "{victim.numberOfPerpetratorsFatalities.lowerThanTotal}")
@ValidNumberOfPerpetratorsInjured(message = "{victim.numberOfPerpetratorsInjured.lowerThanTotal}")
public class VictimDTO implements DTO, Victim {

    @ApiModelProperty(notes = "The event's total number of fatalities", required = true, example = "100")
    @NotNull(message = "{victim.totalNumberOfFatalities.notNull}")
    @Min(value = 0, message = "{victim.totalNumberOfFatalities.min}")
    private Long totalNumberOfFatalities;

    @ApiModelProperty(notes = "The event's total number of perpetrators fatalities", required = true, example = "100")
    @NotNull(message = "{victim.numberOfPerpetratorsFatalities.notNull}")
    @Min(value = 0, message = "{victim.numberOfPerpetratorsFatalities.min}")
    private Long numberOfPerpetratorsFatalities;

    @ApiModelProperty(notes = "The event's total number of injured", required = true, example = "100")
    @NotNull(message = "{victim.totalNumberOfInjured.notNull}")
    @Min(value = 0, message = "{victim.totalNumberOfInjured.min}")
    private Long totalNumberOfInjured;

    @ApiModelProperty(notes = "The event's total number of perpetrators injured", required = true, example = "100")
    @NotNull(message = "{victim.numberOfPerpetratorsInjured.notNull}")
    @Min(value = 0, message = "{victim.numberOfPerpetratorsInjured.min}")
    private Long numberOfPerpetratorsInjured;

    @ApiModelProperty(notes = "The event's total value of property damage", required = true, example = "100")
    @NotNull(message = "{victim.valueOfPropertyDamage.notNull}")
    @Min(value = 0, message = "{victim.valueOfPropertyDamage.min}")
    private Long valueOfPropertyDamage;
}
