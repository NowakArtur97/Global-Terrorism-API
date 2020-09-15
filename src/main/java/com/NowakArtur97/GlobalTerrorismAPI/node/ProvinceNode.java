package com.NowakArtur97.GlobalTerrorismAPI.node;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Province;
import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(label = "Province")
@Data
@NoArgsConstructor
public class ProvinceNode extends Node implements Province {

    private String name;

    @Relationship("PART_OF")
    private CountryNode country;

    public ProvinceNode(String name, CountryNode country) {

        this.name = name;
        this.country = country;
    }

    public ProvinceNode(Long id, String name, CountryNode country) {
        super(id);
        this.name = name;
        this.country = country;
    }
}
