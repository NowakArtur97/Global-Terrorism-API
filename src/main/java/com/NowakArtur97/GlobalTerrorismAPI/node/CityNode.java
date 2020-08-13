package com.NowakArtur97.GlobalTerrorismAPI.node;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.City;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(label = "City")
@Data
@NoArgsConstructor
public class CityNode extends Node implements City {

    private String name;

    private double latitude;

    private double longitude;

    @Relationship("PART_OF")
    private ProvinceNode province;

    public CityNode(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public CityNode(Long id, String name, double latitude, double longitude, ProvinceNode province) {
        super(id);
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.province = province;
    }
}
