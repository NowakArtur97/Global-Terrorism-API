package com.nowakArtur97.globalTerrorismAPI.feature.region;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.Node;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.Objects;

@NodeEntity(label = "Region")
@Getter
@Setter
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

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof RegionNode)) return false;

        RegionNode that = (RegionNode) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getName());
    }
}
