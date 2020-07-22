package com.NowakArtur97.GlobalTerrorismAPI.service.api;

import com.NowakArtur97.GlobalTerrorismAPI.node.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;

import java.util.Optional;

public interface CountryService extends BaseGenericService<GroupNode> {

    Optional<CountryNode> findByName(String name);

    boolean existsByName(String name);
}
