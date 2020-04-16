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

	private boolean wasPartOfMultipleIncidents;

	private boolean wasSuccessful;

	private boolean wasSuicide;

	public EventNode(String summary, String motive, Date date, boolean wasPartOfMultipleIncidents,
			boolean wasSuccessful, boolean wasSuicide) {

		this.summary = summary;
		this.motive = motive;
		this.date = date;
		this.wasPartOfMultipleIncidents = wasPartOfMultipleIncidents;
		this.wasSuccessful = wasSuccessful;
		this.wasSuicide = wasSuicide;
	}
}
