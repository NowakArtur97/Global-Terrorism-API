package com.NowakArtur97.GlobalTerrorismAPI.node;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Target;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "Target")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetNode implements Target {

	@Id
	@GeneratedValue
	private Long id;

	private String target;
	
	public TargetNode(String target) {

		this.target = target;
	}
}
