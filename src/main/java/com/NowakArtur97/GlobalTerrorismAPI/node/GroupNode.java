package com.NowakArtur97.GlobalTerrorismAPI.node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity(label = "Group")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupNode {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Relationship(type = "CARRIES_OUT", direction = Relationship.UNDIRECTED)
    private Set<EventNode> eventsCaused = new HashSet<>();

    public GroupNode(String name) {

        this.name = name;
    }

    public void addEvent(EventNode eventNode) {

        eventsCaused.add(eventNode);
    }
}
