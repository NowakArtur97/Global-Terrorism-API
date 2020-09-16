package com.nowakArtur97.globalTerrorismAPI.testUtil.builder;

import com.nowakArtur97.globalTerrorismAPI.feature.country.Country;
import com.nowakArtur97.globalTerrorismAPI.feature.province.Province;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryModel;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceModel;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryNode;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;

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
