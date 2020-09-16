package com.nowakArtur97.globalTerrorismAPI.feature.region;

import com.nowakArtur97.globalTerrorismAPI.common.service.BasicGenericService;

import java.util.Optional;

public interface RegionService extends BasicGenericService<RegionNode> {

    Optional<RegionNode> findByName(String name);

    boolean existsByName(String name);
}
