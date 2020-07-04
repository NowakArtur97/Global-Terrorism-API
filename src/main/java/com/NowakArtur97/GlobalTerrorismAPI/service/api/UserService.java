package com.NowakArtur97.GlobalTerrorismAPI.service.api;

import com.NowakArtur97.GlobalTerrorismAPI.dto.UserDTO;
import com.NowakArtur97.GlobalTerrorismAPI.node.UserNode;

import java.util.Optional;

public interface UserService {

    UserNode register(UserDTO userDTO);

    Optional<UserNode> findByUserName(String userName);

    Optional<UserNode> findByEmail(String email);
}
