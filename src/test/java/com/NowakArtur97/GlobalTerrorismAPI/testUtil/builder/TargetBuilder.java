package com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Target;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;

public final class TargetBuilder {

	private Long id = 1L;

	private String target = "target";

	public TargetBuilder withId(Long id) {

		this.id = id;

		return this;
	}

	public TargetBuilder withTarget(String target) {

		this.target = target;

		return this;
	}

	public Target build(ObjectType type) {

		switch (type) {

		case DTO:
			return new TargetDTO(target);

		case NODE:
			return new TargetNode(id, target);

		case MODEL:
			return new TargetModel(id, target);
		}

		throw new RuntimeException("The specified type does not exist");
	}
}
