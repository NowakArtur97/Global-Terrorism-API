package com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder;

import java.util.Date;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Target;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.TargetType;
import com.ibm.icu.util.Calendar;

public final class EventBuilder {

	private Long id = 1L;

	private String summary = "summary";

	private String motive = "motive";

	private Date date = Calendar.getInstance().getTime();

	private Boolean isPartOfMultipleIncidents = true;

	private Boolean isSuccessful = true;

	private Boolean isSuicide = true;

	public EventBuilder withId(Long id) {

		this.id = id;

		return this;
	}

	public EventBuilder withSummary(String summary) {

		this.summary = summary;

		return this;
	}

	public EventBuilder withMotive(String motive) {

		this.motive = motive;

		return this;
	}

	public EventBuilder withDate(Date date) {

		this.date = date;

		return this;
	}

	public EventBuilder isPartOfMultipleIncidents(Boolean isPartOfMultipleIncidents) {

		this.isPartOfMultipleIncidents = isPartOfMultipleIncidents;

		return this;
	}

	public EventBuilder isSuccessful(Boolean isSuccessful) {

		this.isSuccessful = isSuccessful;

		return this;
	}

	public EventBuilder isSuicide(Boolean isSuicide) {

		this.isSuicide = isSuicide;

		return this;
	}
}
