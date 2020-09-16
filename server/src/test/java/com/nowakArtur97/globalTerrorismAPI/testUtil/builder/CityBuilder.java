package com.nowakArtur97.globalTerrorismAPI.testUtil.builder;

import com.nowakArtur97.globalTerrorismAPI.feature.city.City;
import com.nowakArtur97.globalTerrorismAPI.feature.province.Province;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityModel;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceModel;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityNode;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;

public final class CityBuilder {

    private Long id = 1L;

    private String name = "city";

    private Double latitude = 45.0;

    private Double longitude = 45.0;

    private Province province = null;

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

    public CityBuilder withProvince(Province province) {

        this.province = province;

        return this;
    }

    public City build(ObjectType type) {

        City city;

        switch (type) {

            case DTO:

                city = new CityDTO(name, latitude, longitude, (ProvinceDTO) province);

                break;

            case NODE:

                city = new CityNode(id, name, latitude, longitude, (ProvinceNode) province);

                break;

            case MODEL:

                city = new CityModel(id, name, latitude, longitude, (ProvinceModel) province);

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

        this.latitude = 45.0;

        this.longitude = 45.0;

        this.province = null;
    }
}
