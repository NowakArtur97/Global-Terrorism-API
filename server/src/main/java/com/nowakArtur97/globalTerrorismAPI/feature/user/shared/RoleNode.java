package com.nowakArtur97.globalTerrorismAPI.feature.user.shared;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "Role")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleNode extends Node {

    private String name;
}
