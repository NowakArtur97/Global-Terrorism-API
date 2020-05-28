package com.NowakArtur97.GlobalTerrorismAPI.model;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Event;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;

@ApiModel(description = "Details about the Target")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class EventModel extends RepresentationModel<EventModel> implements Event {

	@ApiModelProperty(notes = "The unique id of the Event")
	private Long id;

	@ApiModelProperty(notes = "The events's summary", required = true)
	private String summary;

	@ApiModelProperty(notes = "The events's motive", required = true)
	private String motive;

	@ApiModelProperty(notes = "The events's date", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date date;

	@ApiModelProperty(notes = "Was the event part of multiple incidents?", required = true)
	private Boolean isPartOfMultipleIncidents;

	@ApiModelProperty(notes = "Was the event successful?", required = true)
	private Boolean isSuccessful;

	@ApiModelProperty(notes = "Was the event suicide?", required = true)
	private Boolean isSuicide;

	@ApiModelProperty(notes = "The event's target", required = true)
	private TargetModel target;
}
