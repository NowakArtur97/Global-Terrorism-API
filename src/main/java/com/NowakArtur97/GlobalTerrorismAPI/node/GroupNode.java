package com.NowakArtur97.GlobalTerrorismAPI.node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "Group")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupNode {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public GroupNode(String name) {

        this.name = name;
    }
}
