package com.NowakArtur97.GlobalTerrorismAPI.node;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(label = "Province")
@Data
@NoArgsConstructor
public class ProvinceNode extends Node {

    private String name;

    @Relationship("PART_OF")
    private CountryNode country;

    public ProvinceNode(String name) {

        this.name = name;
    }

    public ProvinceNode(Long id, String name, CountryNode country) {
        super(id);
        this.name = name;
        this.country = country;
    }
}
