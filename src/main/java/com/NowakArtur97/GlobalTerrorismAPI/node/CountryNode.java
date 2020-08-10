package com.NowakArtur97.GlobalTerrorismAPI.node;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Country;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "Country")
@Data
@NoArgsConstructor
public class CountryNode extends Node implements Country {

    private String name;

    public CountryNode(String name) {

        this.name = name;
    }

    public CountryNode(Long id, String name) {
        super(id);
        this.name = name;
    }
}
