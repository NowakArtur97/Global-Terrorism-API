package com.NowakArtur97.GlobalTerrorismAPI.node;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Event;
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

    private Boolean isSuicide;

    @Relationship("TARGETS")
    private TargetNode target;

    public EventNode(String summary, String motive, Date date, Boolean isPartOfMultipleIncidents,
                     Boolean isSuccessful, Boolean isSuicide) {

        this.summary = summary;
        this.motive = motive;
        this.date = date;
        this.isPartOfMultipleIncidents = isPartOfMultipleIncidents;
        this.isSuccessful = isSuccessful;
        this.isSuicide = isSuicide;
    }

    public EventNode(String summary, String motive, Date date, Boolean isPartOfMultipleIncidents,
                     Boolean isSuccessful, Boolean isSuicide, TargetNode target) {

        this(summary, motive, date, isPartOfMultipleIncidents, isSuccessful, isSuicide);
        this.target = target;
    }

    @Builder
    public EventNode(Long id, String summary, String motive, Date date, Boolean isPartOfMultipleIncidents, Boolean isSuccessful, Boolean isSuicide, TargetNode target) {

        super(id);
        this.summary = summary;
        this.motive = motive;
        this.date = date;
        this.isPartOfMultipleIncidents = isPartOfMultipleIncidents;
        this.isSuccessful = isSuccessful;
        this.isSuicide = isSuicide;
        this.target = target;
    }
}
