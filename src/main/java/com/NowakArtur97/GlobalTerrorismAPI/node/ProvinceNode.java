package com.NowakArtur97.GlobalTerrorismAPI.node;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "Province")
@Data
@NoArgsConstructor
public class ProvinceNode extends Node {

    private String name;

    public ProvinceNode(String name) {

        this.name = name;
    }

    public ProvinceNode(Long id, String name) {
        super(id);
        this.name = name;
    }
}
