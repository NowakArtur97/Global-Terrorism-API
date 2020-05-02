package com.NowakArtur97.GlobalTerrorismAPI.node;

import java.util.Date;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

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

	private Boolean isPartOfMultipleIncidents;

	private Boolean isSuccessful;

	private Boolean isSuicide;

	@Relationship("TARGETS")
	private TargetNode target;

	public EventNode(String summary, String motive, Date date, Boolean isPartOfMultipleIncidents,
			Boolean isSuccessful, Boolean isSuicide) {

		this.summary = summary;
		this.motive = motive;
		this.date = date;
		this.isPartOfMultipleIncidents = isPartOfMultipleIncidents;
		this.isSuccessful = isSuccessful;
		this.isSuicide = isSuicide;
	}

	public EventNode(String summary, String motive, Date date, Boolean isPartOfMultipleIncidents,
			Boolean isSuccessful, Boolean isSuicide, TargetNode target) {

		this(summary, motive, date, isPartOfMultipleIncidents, isSuccessful, isSuicide);
		
		this.target = target;
	}
}
