package com.nowakArtur97.globalTerrorismAPI.testUtil.builder;

import com.nowakArtur97.globalTerrorismAPI.feature.victim.Victim;
import com.nowakArtur97.globalTerrorismAPI.feature.victim.VictimDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.victim.VictimModel;
import com.nowakArtur97.globalTerrorismAPI.feature.victim.VictimNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;

public final class VictimBuilder {

    private Long id = 1L;

    private Long totalNumberOfFatalities = 100L;

    private Long numberOfPerpetratorFatalities = 10L;

    private Long totalNumberOfInjured = 200L;

    private Long numberOfPerpetratorInjured = 200L;

    private Long valueOfPropertyDamage = 4000L;

    public VictimBuilder withId(Long id) {

        this.id = id;

        return this;
    }

    public VictimBuilder withTotalNumberOfFatalities(Long totalNumberOfFatalities) {

        this.totalNumberOfFatalities = totalNumberOfFatalities;

        return this;
    }

    public VictimBuilder withNumberOfPerpetratorFatalities(Long numberOfPerpetratorFatalities) {

        this.numberOfPerpetratorFatalities = numberOfPerpetratorFatalities;

        return this;
    }

    public VictimBuilder withTotalNumberOfInjured(Long totalNumberOfInjured) {

        this.totalNumberOfInjured = totalNumberOfInjured;

        return this;
    }

    public VictimBuilder withNumberOfPerpetratorInjured(Long numberOfPerpetratorInjured) {

        this.numberOfPerpetratorInjured = numberOfPerpetratorInjured;

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
                        .numberOfPerpetratorFatalities(numberOfPerpetratorFatalities)
                        .totalNumberOfInjured(totalNumberOfInjured)
                        .numberOfPerpetratorInjured(numberOfPerpetratorInjured)
                        .valueOfPropertyDamage(valueOfPropertyDamage)
                        .build();

            case NODE:

                victim = VictimNode.builder()
                        .id(id)
                        .totalNumberOfFatalities(totalNumberOfFatalities)
                        .numberOfPerpetratorFatalities(numberOfPerpetratorFatalities)
                        .totalNumberOfInjured(totalNumberOfInjured)
                        .numberOfPerpetratorInjured(numberOfPerpetratorInjured)
                        .valueOfPropertyDamage(valueOfPropertyDamage)
                        .build();

                break;

            case MODEL:

                victim = VictimModel.builder()
                        .id(id)
                        .totalNumberOfFatalities(totalNumberOfFatalities)
                        .numberOfPerpetratorFatalities(numberOfPerpetratorFatalities)
                        .totalNumberOfInjured(totalNumberOfInjured)
                        .numberOfPerpetratorInjured(numberOfPerpetratorInjured)
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
        numberOfPerpetratorFatalities = 10L;
        totalNumberOfInjured = 200L;
        numberOfPerpetratorInjured = 200L;
        valueOfPropertyDamage = 4000L;
    }
}
