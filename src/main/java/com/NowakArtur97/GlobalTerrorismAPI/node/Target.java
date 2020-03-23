package com.NowakArtur97.GlobalTerrorismAPI.node;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.hateoas.RepresentationModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NodeEntity(label = "Target")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class Target extends RepresentationModel<Target> {

	@Id
	@GeneratedValue
	private Long id;

	private String target;

	public Target(String target) {

		this.target = target;
	}
}
