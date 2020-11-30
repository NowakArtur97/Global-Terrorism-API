package com.nowakArtur97.globalTerrorismAPI.feature.victim;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

@ApiModel(description = "Details about the Event Casualties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VictimModel extends RepresentationModel<VictimModel> implements Victim {

    @ApiModelProperty(notes = "The unique id of the Victim")
    private Long id;

    @ApiModelProperty(notes = "The event's total number of fatalities")
    private Long totalNumberOfFatalities;

    @ApiModelProperty(notes = "The event's total number of perpetrators fatalities")
    private Long numberOfPerpetratorsFatalities;

    @ApiModelProperty(notes = "The event's total number of injured")
    private Long totalNumberOfInjured;

    @ApiModelProperty(notes = "The event's total number of perpetrators injured")
    private Long numberOfPerpetratorsInjured;

    @ApiModelProperty(notes = "The event's total value of property damage")
    private Long valueOfPropertyDamage;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof VictimModel)) return false;

        VictimModel that = (VictimModel) o;
        return Objects.equals(getTotalNumberOfFatalities(), that.getTotalNumberOfFatalities()) &&
                Objects.equals(getNumberOfPerpetratorsFatalities(), that.getNumberOfPerpetratorsFatalities()) &&
                Objects.equals(getTotalNumberOfInjured(), that.getTotalNumberOfInjured()) &&
                Objects.equals(getNumberOfPerpetratorsInjured(), that.getNumberOfPerpetratorsInjured()) &&
                Objects.equals(getValueOfPropertyDamage(), that.getValueOfPropertyDamage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTotalNumberOfFatalities(), getNumberOfPerpetratorsFatalities(),
                getTotalNumberOfInjured(), getNumberOfPerpetratorsInjured(), getValueOfPropertyDamage());
    }
}
