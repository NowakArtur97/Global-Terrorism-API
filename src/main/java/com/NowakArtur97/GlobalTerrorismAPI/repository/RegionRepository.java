package com.NowakArtur97.GlobalTerrorismAPI.repository;

import com.NowakArtur97.GlobalTerrorismAPI.node.RegionNode;

import java.util.Optional;

public interface RegionRepository extends BaseRepository<RegionNode> {

    Optional<RegionNode> findByName(String name);

    boolean existsByName(String name);
}
