package com.NowakArtur97.GlobalTerrorismAPI.feature.country;

import com.NowakArtur97.GlobalTerrorismAPI.common.repository.BaseRepository;

import java.util.Optional;

public interface CountryRepository extends BaseRepository<CountryNode> {

    Optional<CountryNode> findByName(String name);

    boolean existsByName(String name);
}
