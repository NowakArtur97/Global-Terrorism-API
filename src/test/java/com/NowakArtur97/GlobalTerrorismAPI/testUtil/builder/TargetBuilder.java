package com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Country;
import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Target;
import com.NowakArtur97.GlobalTerrorismAPI.dto.CountryDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.CountryModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;

public final class TargetBuilder {

    private Long id = 1L;

    private String targetName = "target";

    private String countryName = "country";

    private Country country = null;

    public TargetBuilder withId(Long id) {

        this.id = id;

        return this;
    }

    public TargetBuilder withTarget(String target) {

        this.targetName = target;

        return this;
    }

    public TargetBuilder withCountryName(String countryName) {

        this.countryName = countryName;

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

                target = new TargetDTO(targetName, new CountryDTO(countryName));

                break;

            case NODE:

                target = new TargetNode(id, targetName,
                        country != null ?
                                (CountryNode) country
                                : new CountryNode(countryName));

                break;

            case MODEL:

                target = new TargetModel(id, targetName,
                        country != null ?
                                (CountryModel) country
                                : new CountryModel(countryName));

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

        this.countryName = "country";

        this.country = null;
    }
}
