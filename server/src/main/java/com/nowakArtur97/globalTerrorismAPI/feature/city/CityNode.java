package com.nowakArtur97.globalTerrorismAPI.feature.city;

import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceNode;
import com.nowakArtur97.globalTerrorismAPI.common.baseModel.Node;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(label = "City")
@Data
@NoArgsConstructor
public class CityNode extends Node implements City {

    private String name;

    private Double latitude;

    private Double longitude;

    @Relationship("PART_OF")
    @EqualsAndHashCode.Exclude
    private ProvinceNode province;

    public CityNode(String name, Double latitude, Double longitude, ProvinceNode province) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.province = province;
    }

    public CityNode(Long id, String name, Double latitude, Double longitude, ProvinceNode province) {
        super(id);
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.province = province;
    }
}