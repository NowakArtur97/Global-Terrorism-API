package com.nowakArtur97.globalTerrorismAPI.feature.event;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.Node;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityNode;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetNode;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Date;
import java.util.Objects;

@NodeEntity(label = "Event")
@Getter
@Setter
@NoArgsConstructor
public class EventNode extends Node implements Event {

    private String summary;

    private String motive;

    private Date date;

    private Boolean isPartOfMultipleIncidents;

    private Boolean isSuccessful;

    private Boolean isSuicidal;

    @Relationship("TARGETS")
    private TargetNode target;

    @Relationship("LOCATED_IN")
    private CityNode city;

    public EventNode(String summary, String motive, Date date, Boolean isPartOfMultipleIncidents, Boolean isSuccessful,
                     Boolean isSuicidal, TargetNode target, CityNode city) {
        this.summary = summary;
        this.motive = motive;
        this.date = date;
        this.isPartOfMultipleIncidents = isPartOfMultipleIncidents;
        this.isSuccessful = isSuccessful;
        this.isSuicidal = isSuicidal;
        this.target = target;
        this.city = city;
    }

    @Builder
    public EventNode(Long id, String summary, String motive, Date date, Boolean isPartOfMultipleIncidents, Boolean isSuccessful,
                     Boolean isSuicidal, TargetNode target, CityNode city) {
        super(id);
        this.summary = summary;
        this.motive = motive;
        this.date = date;
        this.isPartOfMultipleIncidents = isPartOfMultipleIncidents;
        this.isSuccessful = isSuccessful;
        this.isSuicidal = isSuicidal;
        this.target = target;
        this.city = city;
    }

    public EventNode(String summary, String motive, Date date, Boolean isPartOfMultipleIncidents, Boolean isSuccessful,
                     Boolean isSuicidal) {
        this.summary = summary;
        this.motive = motive;
        this.date = date;
        this.isPartOfMultipleIncidents = isPartOfMultipleIncidents;
        this.isSuccessful = isSuccessful;
        this.isSuicidal = isSuicidal;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof EventNode)) return false;

        EventNode eventNode = (EventNode) o;
        return Objects.equals(getSummary(), eventNode.getSummary()) &&
                Objects.equals(getMotive(), eventNode.getMotive()) &&
                Objects.equals(getDate(), eventNode.getDate()) &&
                Objects.equals(getIsPartOfMultipleIncidents(), eventNode.getIsPartOfMultipleIncidents()) &&
                Objects.equals(getIsSuccessful(), eventNode.getIsSuccessful()) &&
                Objects.equals(getIsSuicidal(), eventNode.getIsSuicidal());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSummary(), getMotive(), getDate(), getIsPartOfMultipleIncidents(), getIsSuccessful(),
                getIsSuicidal());
    }
}
