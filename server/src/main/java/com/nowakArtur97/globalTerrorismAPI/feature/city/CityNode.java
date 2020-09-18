package com.nowakArtur97.globalTerrorismAPI.feature.city;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.Node;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Objects;

@NodeEntity(label = "City")
@Getter
@Setter
@NoArgsConstructor
public class CityNode extends Node implements City {

    private String name;

    private Double latitude;

    private Double longitude;

    @Relationship("PART_OF")
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

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof CityNode)) return false;

        CityNode cityNode = (CityNode) o;
        return getName().equals(cityNode.getName()) &&
                getLatitude().equals(cityNode.getLatitude()) &&
                getLongitude().equals(cityNode.getLongitude());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getName(), getLatitude(), getLongitude());
    }
}
