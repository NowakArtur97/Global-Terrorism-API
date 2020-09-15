package com.NowakArtur97.GlobalTerrorismAPI.feature.city;

import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;

import java.util.Optional;

public interface CityService extends GenericService<CityNode, CityDTO> {

    Optional<CityNode> findByNameAndLatitudeAndLongitude(String name, Double latitude, Double longitude);

    CityNode saveNew(CityDTO cityDTO);
}
