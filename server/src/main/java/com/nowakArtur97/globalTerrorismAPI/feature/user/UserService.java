package com.nowakArtur97.globalTerrorismAPI.feature.user;

import java.util.Optional;

public interface UserService {

    UserNode register(UserDTO userDTO);

    Optional<UserNode> findByUserName(String userName);

    Optional<UserNode> findByEmail(String email);

    Optional<UserNode> findByUserNameOrEmail(String userName, String email);
}
