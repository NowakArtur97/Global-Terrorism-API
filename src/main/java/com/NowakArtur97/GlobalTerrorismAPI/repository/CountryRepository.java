package com.NowakArtur97.GlobalTerrorismAPI.repository;

import com.NowakArtur97.GlobalTerrorismAPI.node.CountryNode;

import java.util.Optional;

public interface CountryRepository extends BaseRepository<CountryNode> {

    Optional<CountryNode> findByName(String name);

    boolean existsByName(String name);
}
