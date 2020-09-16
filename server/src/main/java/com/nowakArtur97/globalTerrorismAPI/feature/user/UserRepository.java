package com.nowakArtur97.globalTerrorismAPI.feature.user;

import com.nowakArtur97.globalTerrorismAPI.common.repository.BaseRepository;

import java.util.Optional;

public interface UserRepository extends BaseRepository<UserNode> {

    Optional<UserNode> findByUserName(String userName);

    Optional<UserNode> findByEmail(String email);

    Optional<UserNode> findByUserNameOrEmail(String userName, String email);
}
