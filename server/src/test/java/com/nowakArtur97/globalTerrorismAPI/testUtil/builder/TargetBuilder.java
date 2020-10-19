package com.nowakArtur97.globalTerrorismAPI.testUtil.builder;

import com.nowakArtur97.globalTerrorismAPI.feature.country.Country;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryModel;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryNode;
import com.nowakArtur97.globalTerrorismAPI.feature.target.Target;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetModel;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;

public final class TargetBuilder {

    private Long id = 1L;

    private String targetName = "target";

    private Country country = null;

    public TargetBuilder withId(Long id) {

        this.id = id;

        return this;
    }

    public TargetBuilder withTarget(String target) {

        this.targetName = target;

        return this;
    }

    public TargetBuilder withCountry(Country country) {

        this.country = country;

        return this;
    }

    public Target build(ObjectType type) {

        Target target;

        switch (type) {

            case DTO:

                target = new TargetDTO(targetName, (CountryDTO) country);

                break;

            case NODE:

                target = new TargetNode(id, targetName, (CountryNode) country);

                break;

            case MODEL:

                target = new TargetModel(id, targetName, (CountryModel) country);

                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return target;
    }

    private void resetProperties() {

        id = 1L;
        targetName = "target";
        country = null;
    }
}
