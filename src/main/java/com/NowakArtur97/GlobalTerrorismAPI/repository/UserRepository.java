package com.NowakArtur97.GlobalTerrorismAPI.repository;

import com.NowakArtur97.GlobalTerrorismAPI.node.UserNode;

import java.util.Optional;

public interface UserRepository extends BaseRepository<UserNode> {

    Optional<UserNode> findByUserName(String userName);
}
