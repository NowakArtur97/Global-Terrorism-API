package com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder;

import com.NowakArtur97.GlobalTerrorismAPI.feature.country.Country;
import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Target;
import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;

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

        this.id = 1L;

        this.targetName = "target";

        this.country = null;
    }
}
