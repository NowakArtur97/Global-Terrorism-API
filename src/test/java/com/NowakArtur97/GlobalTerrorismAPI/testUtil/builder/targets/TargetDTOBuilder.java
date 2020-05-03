package com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.targets;

import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;

final public class TargetDTOBuilder {

	private String target = "target";

	public TargetDTOBuilder withTarget(String target) {

		this.target = target;

		return this;
	}

	public TargetDTO build() {

		return new TargetDTO(target);
	}
}
