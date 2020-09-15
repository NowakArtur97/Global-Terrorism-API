package com.NowakArtur97.GlobalTerrorismAPI.feature.group;

import com.NowakArtur97.GlobalTerrorismAPI.feature.event.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;

@NodeEntity(label = "Group")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupNode extends Node implements Group {

    private String name;

    @Relationship(type = "CARRIES_OUT", direction = Relationship.UNDIRECTED)
    private List<EventNode> eventsCaused = new ArrayList<>();

    public GroupNode(String name) {

        this.name = name;
    }

    public GroupNode(Long id, String name, List<EventNode> eventsCaused) {

        this.id = id;
        this.name = name;
        this.eventsCaused = eventsCaused;
    }

    public void addEvent(EventNode eventNode) {

        eventsCaused.add(eventNode);
    }
}