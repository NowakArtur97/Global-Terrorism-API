package com.nowakArtur97.globalTerrorismAPI.feature.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityModel;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.Objects;

@ApiModel(description = "Details about the Target")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventModel extends RepresentationModel<EventModel> implements Event {

    @ApiModelProperty(notes = "The unique id of the Event")
    private Long id;

    @ApiModelProperty(notes = "The event's summary")
    private String summary;

    @ApiModelProperty(notes = "The event's motive")
    private String motive;

    @ApiModelProperty(notes = "The event's date")
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

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof EventModel)) return false;

        EventModel that = (EventModel) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getSummary(), that.getSummary()) &&
                Objects.equals(getMotive(), that.getMotive()) &&
                Objects.equals(getDate(), that.getDate()) &&
                Objects.equals(getIsPartOfMultipleIncidents(), that.getIsPartOfMultipleIncidents()) &&
                Objects.equals(getIsSuccessful(), that.getIsSuccessful()) &&
                Objects.equals(getIsSuicidal(), that.getIsSuicidal());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getSummary(), getMotive(), getDate(),
                getIsPartOfMultipleIncidents(), getIsSuccessful(), getIsSuicidal());
    }
}
