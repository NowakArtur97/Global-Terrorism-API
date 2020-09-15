package com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder;

import com.NowakArtur97.GlobalTerrorismAPI.feature.country.Country;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.Province;
import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;

public final class ProvinceBuilder {

    private Long id = 1L;

    private String name = "province";

    private Country country = null;

    public ProvinceBuilder withId(Long id) {

        this.id = id;

        return this;
    }

    public ProvinceBuilder withName(String name) {

        this.name = name;

        return this;
    }

    public ProvinceBuilder withCountry(Country country) {

        this.country = country;

        return this;
    }

    public Province build(ObjectType type) {

        Province city;

        switch (type) {

            case DTO:

                city = new ProvinceDTO(name, (CountryDTO) country);

                break;

            case NODE:

                city = new ProvinceNode(id, name, (CountryNode) country);

                break;

            case MODEL:

                city = new ProvinceModel(id, name, (CountryModel) country);

                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return city;
    }

    private void resetProperties() {

        this.id = 1L;

        this.name = "province";

        this.country = null;
    }
}
