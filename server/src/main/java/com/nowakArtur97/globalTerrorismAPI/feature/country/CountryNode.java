package com.nowakArtur97.globalTerrorismAPI.feature.country;

import com.nowakArtur97.globalTerrorismAPI.feature.region.RegionNode;
import com.nowakArtur97.globalTerrorismAPI.common.baseModel.Node;
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

    public CountryNode(String name, RegionNode region) {

        this.name = name;
        this.region = region;
    }

    public CountryNode(Long id, String name, RegionNode region) {
        super(id);
        this.name = name;
        this.region = region;
    }
}
