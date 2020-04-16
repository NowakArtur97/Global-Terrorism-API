package com.NowakArtur97.GlobalTerrorismAPI.node;

import java.util.Date;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NodeEntity(label = "Event")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventNode {

	@Id
	@GeneratedValue
	private Long id;

	private String summary;
	
	private String motive;

	private Date date;

	private boolean partOfMultipleIncident;

	private boolean successful;

	private boolean suicide;
}
