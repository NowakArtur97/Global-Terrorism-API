package com.NowakArtur97.GlobalTerrorismAPI.feature.event;

import com.NowakArtur97.GlobalTerrorismAPI.feature.city.CityModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.target.TargetModel;
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

    @ApiModelProperty(notes = "The events's summary")
    private String summary;

    @ApiModelProperty(notes = "The events's motive")
    private String motive;

    @ApiModelProperty(notes = "The events's date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;

    @ApiModelProperty(notes = "Was the event part of multiple incidents?")
    private Boolean isPartOfMultipleIncidents;

    @ApiModelProperty(notes = "Was the event successful?")
    private Boolean isSuccessful;

    @ApiModelProperty(notes = "Was the event suicide?")
    private Boolean isSuicidal;

    @ApiModelProperty(notes = "The event's target")
    private TargetModel target;

    @ApiModelProperty(notes = "The event's city")
    private CityModel city;
}
