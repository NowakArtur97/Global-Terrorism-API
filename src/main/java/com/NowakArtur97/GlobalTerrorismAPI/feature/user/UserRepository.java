package com.NowakArtur97.GlobalTerrorismAPI.feature.user;

import com.NowakArtur97.GlobalTerrorismAPI.common.repository.BaseRepository;

import java.util.Optional;

public interface UserRepository extends BaseRepository<UserNode> {

    Optional<UserNode> findByUserName(String userName);

    Optional<UserNode> findByEmail(String email);

    Optional<UserNode> findByUserNameOrEmail(String userName, String email);
}
