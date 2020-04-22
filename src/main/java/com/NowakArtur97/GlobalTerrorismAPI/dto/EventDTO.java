package com.NowakArtur97.GlobalTerrorismAPI.dto;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(description = "Model responsible for Event validation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO implements DTONode {

	@ApiModelProperty(notes = "The events's summary", required = true)
	@NotBlank(message = "{event.summary.notBlank}")
	private String summary;

	@ApiModelProperty(notes = "The events's motive", required = true)
	@NotBlank(message = "{event.motive.notBlank}")
	private String motive;

	@ApiModelProperty(notes = "The events's date", required = true)
	@Past(message = "{event.date.past}")
	@NotNull(message = "{event.date.notNull}")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date date;

	@ApiModelProperty(notes = "Was the event part of multiple incidents?", required = true)
	@NotNull(message = "{event.isPartOfMultipleIncidents.notNull}")
	private Boolean isPartOfMultipleIncidents;

	@ApiModelProperty(notes = "Was the event successful?", required = true)
	@NotNull(message = "{event.isSuccessful.notNull}")
	private Boolean isSuccessful;

	@ApiModelProperty(notes = "Was the event suicide?", required = true)
	@NotNull(message = "{event.isSuicide.notNull}")
	private Boolean isSuicide;

//	@ApiModelProperty(notes = "The event's event", required = true)
//	private TArgetDTO target;
}
