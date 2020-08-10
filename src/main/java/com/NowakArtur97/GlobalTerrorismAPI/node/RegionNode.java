package com.NowakArtur97.GlobalTerrorismAPI.node;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Region;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "Region")
@Data
@NoArgsConstructor
public class RegionNode extends Node implements Region {

    private String name;

    public RegionNode(String name) {

        this.name = name;
    }

    public RegionNode(Long id, String name) {
        super(id);
        this.name = name;
    }
}
