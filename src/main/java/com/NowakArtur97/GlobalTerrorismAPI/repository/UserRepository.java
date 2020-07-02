package com.NowakArtur97.GlobalTerrorismAPI.repository;

import com.NowakArtur97.GlobalTerrorismAPI.node.UserNode;

public interface UserRepository extends BaseRepository<UserNode> {

    UserNode findByUserName(String userName);
}
