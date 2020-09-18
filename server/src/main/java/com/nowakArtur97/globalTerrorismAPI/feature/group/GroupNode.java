package com.nowakArtur97.globalTerrorismAPI.feature.group;

import com.nowakArtur97.globalTerrorismAPI.feature.event.EventNode;
import com.nowakArtur97.globalTerrorismAPI.common.baseModel.Node;
import lombok.*;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NodeEntity(label = "Group")
@Getter
@Setter
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
        super(id);
        this.name = name;
        this.eventsCaused = eventsCaused;
    }

    public void addEvent(EventNode eventNode) {

        eventsCaused.add(eventNode);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof GroupNode)) return false;

        GroupNode groupNode = (GroupNode) o;
        return Objects.equals(getName(), groupNode.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getName());
    }
}