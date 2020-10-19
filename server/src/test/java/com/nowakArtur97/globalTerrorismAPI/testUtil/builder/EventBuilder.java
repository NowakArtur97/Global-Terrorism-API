package com.nowakArtur97.globalTerrorismAPI.testUtil.builder;

import com.nowakArtur97.globalTerrorismAPI.feature.target.Target;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.city.City;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityModel;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityNode;
import com.nowakArtur97.globalTerrorismAPI.feature.event.Event;
import com.nowakArtur97.globalTerrorismAPI.feature.event.EventDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.event.EventModel;
import com.nowakArtur97.globalTerrorismAPI.feature.event.EventNode;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetModel;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.ibm.icu.util.Calendar;

import java.util.Date;

public final class EventBuilder {

    private final static Date startDate = Calendar.getInstance().getTime();

    private Long id = 1L;

    private String summary = "summary";

    private String motive = "motive";

    private Date date = startDate;

    private Boolean isPartOfMultipleIncidents = true;

    private Boolean isSuccessful = true;

    private Boolean isSuicidal = true;

    private Target target = null;

    private City city = null;

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

    public EventBuilder withIsPartOfMultipleIncidents(Boolean isPartOfMultipleIncidents) {

        this.isPartOfMultipleIncidents = isPartOfMultipleIncidents;

        return this;
    }

    public EventBuilder withIsSuccessful(Boolean isSuccessful) {

        this.isSuccessful = isSuccessful;

        return this;
    }

    public EventBuilder withIsSuicidal(Boolean isSuicidal) {

        this.isSuicidal = isSuicidal;

        return this;
    }

    public EventBuilder withTarget(Target target) {

        this.target = target;

        return this;
    }

    public EventBuilder withCity(City city) {

        this.city = city;

        return this;
    }

    public Event build(ObjectType type) {

        Event event;

        switch (type) {

            case DTO:
                event = EventDTO.builder().summary(summary).motive(motive).date(date)
                        .isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
                        .isSuicidal(isSuicidal).target((TargetDTO) target).city((CityDTO) city).build();
                break;

            case NODE:
                event = EventNode.builder().id(id).summary(summary).motive(motive).date(date)
                        .isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
                        .isSuicidal(isSuicidal).target((TargetNode) target).city((CityNode) city).build();
                break;

            case MODEL:

                event = EventModel.builder().id(id).summary(summary).motive(motive).date(date)
                        .isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
                        .isSuicidal(isSuicidal).target((TargetModel) target).city((CityModel) city).build();
                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return event;
    }

    private void resetProperties() {

        id = 1L;
        summary = "summary";
        motive = "motive";
        date = startDate;
        isPartOfMultipleIncidents = true;
        isSuccessful = true;
        isSuicidal = true;
        target = null;
        city = null;
    }
}
