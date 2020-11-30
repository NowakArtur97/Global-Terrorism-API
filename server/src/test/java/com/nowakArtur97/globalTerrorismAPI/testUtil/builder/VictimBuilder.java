package com.nowakArtur97.globalTerrorismAPI.testUtil.builder;

import com.nowakArtur97.globalTerrorismAPI.feature.victim.Victim;
import com.nowakArtur97.globalTerrorismAPI.feature.victim.VictimDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.victim.VictimModel;
import com.nowakArtur97.globalTerrorismAPI.feature.victim.VictimNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;

public final class VictimBuilder {

    private Long id = 1L;

    private Long totalNumberOfFatalities = 100L;

    private Long numberOfPerpetratorsFatalities = 10L;

    private Long totalNumberOfInjured = 200L;

    private Long numberOfPerpetratorsInjured = 200L;

    private Long valueOfPropertyDamage = 4000L;

    public VictimBuilder withId(Long id) {

        this.id = id;

        return this;
    }

    public VictimBuilder withTotalNumberOfFatalities(Long totalNumberOfFatalities) {

        this.totalNumberOfFatalities = totalNumberOfFatalities;

        return this;
    }

    public VictimBuilder withNumberOfPerpetratorsFatalities(Long numberOfPerpetratorsFatalities) {

        this.numberOfPerpetratorsFatalities = numberOfPerpetratorsFatalities;

        return this;
    }

    public VictimBuilder withTotalNumberOfInjured(Long totalNumberOfInjured) {

        this.totalNumberOfInjured = totalNumberOfInjured;

        return this;
    }

    public VictimBuilder withNumberOfPerpetratorsInjured(Long numberOfPerpetratorsInjured) {

        this.numberOfPerpetratorsInjured = numberOfPerpetratorsInjured;

        return this;
    }

    public VictimBuilder withValueOfPropertyDamage(Long valueOfPropertyDamage) {

        this.valueOfPropertyDamage = valueOfPropertyDamage;

        return this;
    }

    public Victim build(ObjectType type) {

        Victim victim;

        switch (type) {

            case DTO:

                victim = VictimDTO.builder()
                        .totalNumberOfFatalities(totalNumberOfFatalities)
                        .numberOfPerpetratorsFatalities(numberOfPerpetratorsFatalities)
                        .totalNumberOfInjured(totalNumberOfInjured)
                        .numberOfPerpetratorsInjured(numberOfPerpetratorsInjured)
                        .valueOfPropertyDamage(valueOfPropertyDamage)
                        .build();

                break;

            case NODE:

                victim = VictimNode.builder()
                        .id(id)
                        .totalNumberOfFatalities(totalNumberOfFatalities)
                        .numberOfPerpetratorsFatalities(numberOfPerpetratorsFatalities)
                        .totalNumberOfInjured(totalNumberOfInjured)
                        .numberOfPerpetratorsInjured(numberOfPerpetratorsInjured)
                        .valueOfPropertyDamage(valueOfPropertyDamage)
                        .build();

                break;

            case MODEL:

                victim = VictimModel.builder()
                        .id(id)
                        .totalNumberOfFatalities(totalNumberOfFatalities)
                        .numberOfPerpetratorsFatalities(numberOfPerpetratorsFatalities)
                        .totalNumberOfInjured(totalNumberOfInjured)
                        .numberOfPerpetratorsInjured(numberOfPerpetratorsInjured)
                        .valueOfPropertyDamage(valueOfPropertyDamage)
                        .build();

                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return victim;
    }

    private void resetProperties() {

        id = 1L;
        totalNumberOfFatalities = 100L;
        numberOfPerpetratorsFatalities = 10L;
        totalNumberOfInjured = 200L;
        numberOfPerpetratorsInjured = 200L;
        valueOfPropertyDamage = 4000L;
    }
}
