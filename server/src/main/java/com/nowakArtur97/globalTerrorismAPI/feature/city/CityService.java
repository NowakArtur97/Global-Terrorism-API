package com.nowakArtur97.globalTerrorismAPI.feature.city;

import com.nowakArtur97.globalTerrorismAPI.common.service.GenericService;

import java.util.Optional;

public interface CityService extends GenericService<CityNode, CityDTO> {

    Optional<CityNode> findByNameAndLatitudeAndLongitude(String name, Double latitude, Double longitude);

    CityNode saveNew(CityDTO cityDTO);
}
