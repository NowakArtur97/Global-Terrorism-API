package com.NowakArtur97.GlobalTerrorismAPI.service.api;

import com.NowakArtur97.GlobalTerrorismAPI.dto.CityDTO;
import com.NowakArtur97.GlobalTerrorismAPI.node.CityNode;

import java.util.Optional;

public interface CityService extends BaseGenericService<CityNode> {

    Optional<CityNode> findByNameAndLatitudeAndLongitude(String name, Double latitude, Double longitude);

    CityNode saveNew(CityDTO cityDTO);
}
