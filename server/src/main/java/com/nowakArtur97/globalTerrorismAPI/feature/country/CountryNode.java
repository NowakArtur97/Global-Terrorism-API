package com.nowakArtur97.globalTerrorismAPI.feature.country;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.Node;
import com.nowakArtur97.globalTerrorismAPI.feature.region.RegionNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Objects;

@NodeEntity(label = "Country")
@Getter
@Setter
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

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof CountryNode)) return false;

        CountryNode that = (CountryNode) o;
        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getRegion(), that.getRegion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getRegion());
    }
}
