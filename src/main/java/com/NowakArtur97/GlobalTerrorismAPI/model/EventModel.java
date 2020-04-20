package com.NowakArtur97.GlobalTerrorismAPI.model;

import java.util.Date;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@ApiModel(description = "Details about the Target")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class EventModel extends RepresentationModel<EventModel> {

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
	private boolean isPartOfMultipleIncidents;

	@ApiModelProperty(notes = "Was the event successful?", required = true)
	private boolean isSuccessful;

	@ApiModelProperty(notes = "Was the event suicide?", required = true)
	private boolean isSuicide;
}
