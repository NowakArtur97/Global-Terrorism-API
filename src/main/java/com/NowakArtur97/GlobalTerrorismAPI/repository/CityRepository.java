package com.NowakArtur97.GlobalTerrorismAPI.repository;

import com.NowakArtur97.GlobalTerrorismAPI.node.CityNode;

import java.util.Optional;

public interface CityRepository extends BaseRepository<CityNode> {

    Optional<CityNode> findByName(String name);
}
