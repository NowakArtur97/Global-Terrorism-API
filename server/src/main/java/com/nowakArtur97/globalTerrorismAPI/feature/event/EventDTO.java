package com.nowakArtur97.globalTerrorismAPI.feature.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nowakArtur97.globalTerrorismAPI.common.baseModel.DTO;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;

@ApiModel(description = "Model responsible for Event validation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ProvinceAndTargetAreInSameCountry(message = "{event.provinceAndTarget.sameCountry}")
public class EventDTO implements DTO, Event {

    @ApiModelProperty(notes = "The event's summary", required = true, example = "Summary")
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
    @NotNull(message = "{city.name.notBlank}")
    private CityDTO city;
}
