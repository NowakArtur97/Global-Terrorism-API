package com.NowakArtur97.GlobalTerrorismAPI.node;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.City;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "City")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityNode extends Node implements City {

    private String name;

    private double latitude;

    private double longitude;
}
