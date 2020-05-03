package com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.targets;

import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;

final public class TargetModelBuilder {

	private Long id = 1L;

	private String target = "target";

	public TargetModelBuilder withId(Long id) {

		this.id = id;

		return this;
	}

	public TargetModelBuilder withTarget(String target) {

		this.target = target;

		return this;
	}

	public TargetModel build() {

		return new TargetModel(id, target);
	}
}
