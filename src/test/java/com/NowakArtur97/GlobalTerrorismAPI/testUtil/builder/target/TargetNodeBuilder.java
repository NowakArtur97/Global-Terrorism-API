package com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.target;

import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;

final public class TargetNodeBuilder {

	private Long id = 1L;

	private String target = "target";

	public TargetNodeBuilder withId(Long id) {

		this.id = id;

		return this;
	}

	public TargetNodeBuilder withTarget(String target) {

		this.target = target;

		return this;
	}

	public TargetNode build() {

		return new TargetNode(id, target);
	}
}
