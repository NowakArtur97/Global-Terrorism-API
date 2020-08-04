package com.NowakArtur97.GlobalTerrorismAPI.dto;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Event;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;

@ApiModel(description = "Model responsible for Event validation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO implements DTONode, Event {

	@ApiModelProperty(notes = "The events's summary", required = true, example = "Summary")
	@NotBlank(message = "{event.summary.notBlank}")
	private String summary;

	@ApiModelProperty(notes = "The event's motive", required = true, example = "Motive")
	@NotBlank(message = "{event.motive.notBlank}")
	private String motive;

	@ApiModelProperty(notes = "The event's date", required = true, example = "2000-01-31")
	@Past(message = "{event.date.past}")
	@NotNull(message = "{event.date.notNull}")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date date;

	@ApiModelProperty(notes = "Was the event part of multiple incidents?", required = true, example = "true")
	@NotNull(message = "{event.isPartOfMultipleIncidents.notNull}")
	private Boolean isPartOfMultipleIncidents;

	@ApiModelProperty(notes = "Was the event successful?", required = true, example = "true")
	@NotNull(message = "{event.isSuccessful.notNull}")
	private Boolean isSuccessful;

	@ApiModelProperty(notes = "Was the event suicide?", required = true, example = "true")
	@NotNull(message = "{event.isSuicidal.notNull}")
	private Boolean isSuicidal;

	@ApiModelProperty(notes = "The event's target", required = true)
	@Valid
	@NotNull(message = "{target.target.notBlank}")
	private TargetDTO target;

	@ApiModelProperty(notes = "The event's city", required = true)
	@Valid
	private CityDTO city;
}
