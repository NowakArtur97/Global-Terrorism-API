package com.nowakArtur97.globalTerrorismAPI.testUtil.builder;

import com.nowakArtur97.globalTerrorismAPI.feature.user.loginUser.AuthenticationRequest;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.User;
import com.nowakArtur97.globalTerrorismAPI.feature.user.registerUser.UserDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.RoleNode;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.UserNode;
import com.nowakArtur97.globalTerrorismAPI.testUtil.builder.enums.ObjectType;

import java.util.HashSet;
import java.util.Set;

public final class UserBuilder {

    private Long id = 1L;

    private String userName = "user123";

    private String password = "SecretPassword123!@";

    private String matchingPassword = "SecretPassword123!@";

    private String email = "user@email.com";

    private Set<RoleNode> roles = new HashSet<>();

    public UserBuilder withId(Long id) {

        this.id = id;

        return this;
    }

    public UserBuilder withUserName(String userName) {

        this.userName = userName;

        return this;
    }

    public UserBuilder withPassword(String password) {

        this.password = password;

        return this;
    }

    public UserBuilder withMatchingPassword(String matchingPassword) {

        this.matchingPassword = matchingPassword;

        return this;
    }

    public UserBuilder withEmail(String email) {

        this.email = email;

        return this;
    }

    public UserBuilder withRoles(Set<RoleNode> roles) {

        this.roles = roles;

        return this;
    }

    public User build(ObjectType type) {

        User user;

        switch (type) {

            case DTO:

                user = new UserDTO(userName, password, matchingPassword, email);

                break;

            case NODE:

                user = new UserNode(id, userName, password, email, roles);

                break;

            case REQUEST:

                user = new AuthenticationRequest(userName, password, email);

                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return user;
    }

    private void resetProperties() {

        this.id = 1L;

        this.userName = "user123";

        this.password = "SecretPassword123!@";

        this.matchingPassword = "SecretPassword123!@";

        this.email = "user@email.com";

        this.roles = new HashSet<>();
    }
}
