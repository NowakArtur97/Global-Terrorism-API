package com.NowakArtur97.GlobalTerrorismAPI.service.api;

import com.NowakArtur97.GlobalTerrorismAPI.node.RegionNode;

import java.util.Optional;

public interface RegionService extends BaseGenericService<RegionNode> {

    Optional<RegionNode> findByName(String name);

    boolean existsByName(String name);
}
