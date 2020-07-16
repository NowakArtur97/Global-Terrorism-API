package com.NowakArtur97.GlobalTerrorismAPI.node;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Target;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(label = "Target")
@Data
@NoArgsConstructor
public class TargetNode extends Node implements Target {

	private String target;

	@Relationship("IS_FROM")
	private CountryNode countryOfOrigin;

	public TargetNode(String target) {

		this.target = target;
	}

	public TargetNode(Long id, String target) {

		super(id);
		this.target = target;
	}
}
