package com.nowakArtur97.globalTerrorismAPI.feature.country;

import com.nowakArtur97.globalTerrorismAPI.common.repository.BaseRepository;

import java.util.Optional;

public interface CountryRepository extends BaseRepository<CountryNode> {

    Optional<CountryNode> findByName(String name);

    boolean existsByName(String name);
}
