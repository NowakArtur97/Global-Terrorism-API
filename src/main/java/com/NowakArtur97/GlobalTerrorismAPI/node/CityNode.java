package com.NowakArtur97.GlobalTerrorismAPI.node;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.City;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "City")
@Data
@NoArgsConstructor
public class CityNode extends Node implements City {

    private String name;

    private double latitude;

    private double longitude;

    public CityNode(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public CityNode(Long id, String name, double latitude, double longitude) {
        super(id);
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
