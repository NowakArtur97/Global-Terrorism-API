package com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.City;
import com.NowakArtur97.GlobalTerrorismAPI.dto.CityDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.CityModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.CityNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;

public final class CityBuilder {

    private Long id = 1L;

    private String name = "city";

    private double latitude = 45.0;

    private double longitude = 45.0;

    public CityBuilder withId(Long id) {

        this.id = id;

        return this;
    }

    public CityBuilder withName(String name) {

        this.name = name;

        return this;
    }

    public CityBuilder withLatitude(Double latitude) {

        this.latitude = latitude;

        return this;
    }

    public CityBuilder withLongitude(Double longitude) {

        this.longitude = longitude;

        return this;
    }

    public City build(ObjectType type) {

        City city;

        switch (type) {

            case DTO:

                city = new CityDTO(name, latitude, longitude);

                break;

            case NODE:

                city = new CityNode(id, name, latitude, longitude);

                break;

            case MODEL:

                city = new CityModel(id, name, latitude, longitude);

                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return city;
    }

    private void resetProperties() {

        this.id = 1L;

        this.name = "city";
    }
}
