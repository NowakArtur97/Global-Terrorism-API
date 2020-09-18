package com.nowakArtur97.globalTerrorismAPI.feature.user.shared;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.Node;
import lombok.*;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Objects;
import java.util.Set;

@NodeEntity(label = "User")
@Getter
@Setter
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

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof UserNode)) return false;

        UserNode userNode = (UserNode) o;
        return Objects.equals(getUserName(), userNode.getUserName()) &&
                Objects.equals(getPassword(), userNode.getPassword()) &&
                Objects.equals(getEmail(), userNode.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserName(), getPassword(), getEmail());
    }
}