package com.nowakArtur97.globalTerrorismAPI.feature.user.shared;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.Objects;

@NodeEntity(label = "Role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleNode extends Node {

    private String name;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof RoleNode)) return false;

        RoleNode roleNode = (RoleNode) o;
        return Objects.equals(getName(), roleNode.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
