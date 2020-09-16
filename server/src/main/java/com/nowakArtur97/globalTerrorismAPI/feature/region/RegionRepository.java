package com.nowakArtur97.globalTerrorismAPI.feature.region;

import com.nowakArtur97.globalTerrorismAPI.common.repository.BaseRepository;

import java.util.Optional;

interface RegionRepository extends BaseRepository<RegionNode> {

    Optional<RegionNode> findByName(String name);

    boolean existsByName(String name);
}
