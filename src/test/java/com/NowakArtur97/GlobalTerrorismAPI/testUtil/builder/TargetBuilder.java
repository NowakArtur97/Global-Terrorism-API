package com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Target;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.TargetModel;
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

    public TargetBuilder withCountry(String target) {

        this.targetName = target;

        return this;
    }

    public Target build(ObjectType type) {

        Target target;

        switch (type) {

            case DTO:

                target = new TargetDTO(targetName, country);

                break;

            case NODE:

                target = new TargetNode(id, targetName);

                break;

            case MODEL:

                target = new TargetModel(id, targetName);

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
    }
}
