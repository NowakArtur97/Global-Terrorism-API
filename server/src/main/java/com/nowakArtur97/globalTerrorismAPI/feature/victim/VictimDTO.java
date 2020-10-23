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
@ValidNumberOfPerpetratorFatalities(message = "{victim.numberOfPerpetratorFatalities.lowerThanTotal}",
        groups = BasicVictimValidationConstraints.class)
@ValidNumberOfPerpetratorInjured(message = "{victim.numberOfPerpetratorInjured.lowerThanTotal}",
        groups = BasicVictimValidationConstraints.class)
public class VictimDTO implements DTO, Victim {

    @ApiModelProperty(notes = "The event's total number of fatalities", required = true, example = "100")
    @NotNull(message = "{victim.totalNumberOfFatalities.notNull}")
    @Min(value = 0, message = "{victim.totalNumberOfFatalities.min}")
    private Long totalNumberOfFatalities;

    @ApiModelProperty(notes = "The event's total number of perpetrator fatalities", required = true, example = "100")
    @NotNull(message = "{victim.numberOfPerpetratorFatalities.notNull}")
    @Min(value = 0, message = "{victim.numberOfPerpetratorFatalities.min}")
    private Long numberOfPerpetratorFatalities;

    @ApiModelProperty(notes = "The event's total number of injured", required = true, example = "100")
    @NotNull(message = "{victim.totalNumberOfInjured.notNull}")
    @Min(value = 0, message = "{victim.totalNumberOfInjured.min}")
    private Long totalNumberOfInjured;

    @ApiModelProperty(notes = "The event's total number of perpetrator injured", required = true, example = "100")
    @NotNull(message = "{victim.numberOfPerpetratorInjured.notNull}")
    @Min(value = 0, message = "{victim.numberOfPerpetratorInjured.min}")
    private Long numberOfPerpetratorInjured;

    @ApiModelProperty(notes = "The event's total value of property damage", required = true, example = "100")
    @NotNull(message = "{victim.valueOfPropertyDamage.notNull}")
    @Min(value = 0, message = "{victim.valueOfPropertyDamage.min}")
    private Long valueOfPropertyDamage;
}
