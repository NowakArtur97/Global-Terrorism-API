package com.NowakArtur97.GlobalTerrorismAPI.service.api;

import com.NowakArtur97.GlobalTerrorismAPI.dto.UserDTO;
import com.NowakArtur97.GlobalTerrorismAPI.node.UserNode;

public interface UserService {

    UserNode register(UserDTO userDTO);

    UserNode findByUserName(String userName);
}
