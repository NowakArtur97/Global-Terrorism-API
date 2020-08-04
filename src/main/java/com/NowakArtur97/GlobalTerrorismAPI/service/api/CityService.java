package com.NowakArtur97.GlobalTerrorismAPI.service.api;

import com.NowakArtur97.GlobalTerrorismAPI.dto.CityDTO;
import com.NowakArtur97.GlobalTerrorismAPI.node.CityNode;

import java.util.Optional;

public interface CityService extends BaseGenericService<CityNode> {

    Optional<CityNode> findByName(String name);

    CityNode saveNew(CityDTO cityDTO);
}
