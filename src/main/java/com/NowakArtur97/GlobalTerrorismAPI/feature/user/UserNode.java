package com.NowakArtur97.GlobalTerrorismAPI.feature.user;

import com.NowakArtur97.GlobalTerrorismAPI.node.Node;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@NodeEntity(label = "User")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNode extends Node implements User {

    private String userName;

    private String password;

    private String email;

    @Relationship(type = "HAS_ROLE")
    private Set<RoleNode> roles;

    @Builder
    public UserNode(Long id, String userName, String password, String email, Set<RoleNode> roles) {
        super(id);
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.roles = roles;
    }
}