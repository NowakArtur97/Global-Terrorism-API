package com.NowakArtur97.GlobalTerrorismAPI.feature.country;

import com.NowakArtur97.GlobalTerrorismAPI.repository.BaseRepository;

import java.util.Optional;

interface CountryRepository extends BaseRepository<CountryNode> {

    Optional<CountryNode> findByName(String name);

    boolean existsByName(String name);
}
