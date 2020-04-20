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

	private boolean isPartOfMultipleIncidents;

	private boolean isSuccessful;

	private boolean isSuicide;

	@Relationship("TARGETS")
	private TargetNode target;

	public EventNode(String summary, String motive, Date date, boolean isPartOfMultipleIncidents,
			boolean isSuccessful, boolean isSuicide) {

		this.summary = summary;
		this.motive = motive;
		this.date = date;
		this.isPartOfMultipleIncidents = isPartOfMultipleIncidents;
		this.isSuccessful = isSuccessful;
		this.isSuicide = isSuicide;
	}

	public EventNode(String summary, String motive, Date date, boolean isPartOfMultipleIncidents,
			boolean isSuccessful, boolean isSuicide, TargetNode target) {

		this(summary, motive, date, isPartOfMultipleIncidents, isSuccessful, isSuicide);
		
		this.target = target;
	}
}
