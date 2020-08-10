package com.NowakArtur97.GlobalTerrorismAPI.node;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Country;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(label = "Country")
@Data
@NoArgsConstructor
public class CountryNode extends Node implements Country {

    private String name;

    @Relationship("PART_OF")
    private RegionNode region;

    // TO REMOVE
    public CountryNode(String name) {

        this.name = name;
    }

    public CountryNode(String name, RegionNode region) {

        this.name = name;
        this.region = region;
    }

    public CountryNode(Long id, String name, RegionNode region) {
        super(id);
        this.name = name;
    }
}
