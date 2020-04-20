package com.NowakArtur97.GlobalTerrorismAPI.node;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NodeEntity(label = "Target")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetNode {

	@Id
	@GeneratedValue
	private Long id;

	private String target;
	
	public TargetNode(String target) {

		this.target = target;
	}
}
