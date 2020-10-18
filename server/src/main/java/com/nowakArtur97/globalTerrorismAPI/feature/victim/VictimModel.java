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

    @ApiModelProperty(notes = "The event's total number of perpetrator fatalities")
    private Long numberOfPerpetratorFatalities;

    @ApiModelProperty(notes = "The event's total number of injured")
    private Long totalNumberOfInjured;

    @ApiModelProperty(notes = "The event's total number of perpetrator injured")
    private Long numberOfPerpetratorInjured;

    @ApiModelProperty(notes = "The event's total value of property damage")
    private Long valueOfPropertyDamage;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof VictimModel)) return false;

        VictimModel that = (VictimModel) o;
        return Objects.equals(getTotalNumberOfFatalities(), that.getTotalNumberOfFatalities()) &&
                Objects.equals(getNumberOfPerpetratorFatalities(), that.getNumberOfPerpetratorFatalities()) &&
                Objects.equals(getTotalNumberOfInjured(), that.getTotalNumberOfInjured()) &&
                Objects.equals(getNumberOfPerpetratorInjured(), that.getNumberOfPerpetratorInjured()) &&
                Objects.equals(getValueOfPropertyDamage(), that.getValueOfPropertyDamage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTotalNumberOfFatalities(), getNumberOfPerpetratorFatalities(),
                getTotalNumberOfInjured(), getNumberOfPerpetratorInjured(), getValueOfPropertyDamage());
    }
}
