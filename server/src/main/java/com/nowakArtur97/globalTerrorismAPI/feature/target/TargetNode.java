package com.nowakArtur97.globalTerrorismAPI.feature.target;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.Node;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Objects;

@NodeEntity(label = "Target")
@Getter
@Setter
@NoArgsConstructor
public class TargetNode extends Node implements Target {

    private String target;

    @Relationship("IS_FROM")
    private CountryNode countryOfOrigin;

    public TargetNode(Long id, String target, CountryNode countryOfOrigin) {

        super(id);
        this.target = target;
        this.countryOfOrigin = countryOfOrigin;
    }

    public TargetNode(String target, CountryNode countryOfOrigin) {

        this.target = target;
        this.countryOfOrigin = countryOfOrigin;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof TargetNode)) return false;

        TargetNode that = (TargetNode) o;
        return Objects.equals(getTarget(), that.getTarget());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTarget());
    }
}
