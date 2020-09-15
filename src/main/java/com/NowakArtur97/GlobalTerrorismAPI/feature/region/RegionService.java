package com.NowakArtur97.GlobalTerrorismAPI.feature.region;

import com.NowakArtur97.GlobalTerrorismAPI.service.api.BasicGenericService;

import java.util.Optional;

public interface RegionService extends BasicGenericService<RegionNode> {

    Optional<RegionNode> findByName(String name);

    boolean existsByName(String name);
}
