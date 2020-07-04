package com.NowakArtur97.GlobalTerrorismAPI.node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@NodeEntity(label = "User")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNode extends Node {

    private String userName;

    private String password;

    private String email;

    @Relationship(type = "HAS_ROLE")
    private Set<RoleNode> roles;
}