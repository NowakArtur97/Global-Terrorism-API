package com.NowakArtur97.GlobalTerrorismAPI.node;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "Country")
@Data
@NoArgsConstructor
public class CountryNode extends Node {

    private String name;

    public CountryNode(String name) {

        this.name = name;
    }
}
