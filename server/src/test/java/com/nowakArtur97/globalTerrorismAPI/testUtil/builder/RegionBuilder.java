package com.nowakArtur97.globalTerrorismAPI.testUtil.builder;

import com.nowakArtur97.globalTerrorismAPI.feature.region.Region;
import com.nowakArtur97.globalTerrorismAPI.feature.region.RegionModel;
import com.nowakArtur97.globalTerrorismAPI.feature.region.RegionNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;

public final class RegionBuilder {

    private Long id = 1L;

    private String name = "region";

    public RegionBuilder withId(Long id) {

        this.id = id;

        return this;
    }

    public RegionBuilder withName(String name) {

        this.name = name;

        return this;
    }

    public Region build(ObjectType type) {

        Region region;

        switch (type) {

            case NODE:

                region = new RegionNode(id, name);

                break;

            case MODEL:

                region = new RegionModel(id, name);

                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return region;
    }

    private void resetProperties() {

        id = 1L;
        name = "region";
    }
}
