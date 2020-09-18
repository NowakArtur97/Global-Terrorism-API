package com.nowakArtur97.globalTerrorismAPI.feature.province;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.Node;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Objects;

@NodeEntity(label = "Province")
@Getter
@Setter
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

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof ProvinceNode)) return false;

        ProvinceNode that = (ProvinceNode) o;
        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getCountry(), that.getCountry());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getName(), getCountry());
    }
}
