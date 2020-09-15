package com.NowakArtur97.GlobalTerrorismAPI.feature.event;

import com.NowakArtur97.GlobalTerrorismAPI.feature.city.CityNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.target.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.Node;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Date;

@NodeEntity(label = "Event")
@Data
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
}
