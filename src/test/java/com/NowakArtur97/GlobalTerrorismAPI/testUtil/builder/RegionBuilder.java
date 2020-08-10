package com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Region;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.RegionModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.RegionNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;

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

        this.id = 1L;

        this.name = "region";
    }
}
