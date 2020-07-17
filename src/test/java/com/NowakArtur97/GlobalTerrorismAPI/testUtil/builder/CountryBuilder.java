package com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Country;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.CountryModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;

public final class CountryBuilder {

    private Long id = 1L;

    private String name = "country";

    public CountryBuilder withId(Long id) {

        this.id = id;

        return this;
    }

    public CountryBuilder withName(String name) {

        this.name = name;

        return this;
    }

    public Country build(ObjectType type) {

        Country country;

        switch (type) {

            case NODE:

                country = new CountryNode(id, name);

                break;

            case MODEL:

                country = new CountryModel(id, name);

                break;

            case DTO:
            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return country;
    }

    private void resetProperties() {

        this.id = 1L;

        this.name = "country";
    }
}
