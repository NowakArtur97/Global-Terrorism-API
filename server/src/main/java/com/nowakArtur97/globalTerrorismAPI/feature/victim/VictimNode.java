package com.nowakArtur97.globalTerrorismAPI.feature.victim;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.Node;
import lombok.*;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.Objects;

@NodeEntity(label = "Victim")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VictimNode extends Node implements Victim {

    private Long totalNumberOfFatalities;

    private Long numberOfPerpetratorsFatalities;

    private Long totalNumberOfInjured;

    private Long numberOfPerpetratorsInjured;

    private Long valueOfPropertyDamage;

    @Builder
    public VictimNode(Long id, Long totalNumberOfFatalities, Long numberOfPerpetratorsFatalities, Long totalNumberOfInjured,
                      Long numberOfPerpetratorsInjured, Long valueOfPropertyDamage) {
        super(id);
        this.totalNumberOfFatalities = totalNumberOfFatalities;
        this.numberOfPerpetratorsFatalities = numberOfPerpetratorsFatalities;
        this.totalNumberOfInjured = totalNumberOfInjured;
        this.numberOfPerpetratorsInjured = numberOfPerpetratorsInjured;
        this.valueOfPropertyDamage = valueOfPropertyDamage;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof VictimNode)) return false;

        VictimNode that = (VictimNode) o;
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
