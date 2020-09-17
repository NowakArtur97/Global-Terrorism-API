package com.nowakArtur97.globalTerrorismAPI.feature.user.registerUser;

import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.UserNode;

import java.util.Optional;

public interface UserService {

    UserNode register(UserDTO userDTO);

    Optional<UserNode> findByUserName(String userName);

    Optional<UserNode> findByEmail(String email);

    Optional<UserNode> findByUserNameOrEmail(String userName, String email);
}
