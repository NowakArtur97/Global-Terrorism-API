package com.NowakArtur97.GlobalTerrorismAPI.feature.region;

import com.NowakArtur97.GlobalTerrorismAPI.repository.BaseRepository;

import java.util.Optional;

interface RegionRepository extends BaseRepository<RegionNode> {

    Optional<RegionNode> findByName(String name);

    boolean existsByName(String name);
}
