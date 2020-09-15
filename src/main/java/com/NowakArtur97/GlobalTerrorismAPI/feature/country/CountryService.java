package com.NowakArtur97.GlobalTerrorismAPI.feature.country;

import com.NowakArtur97.GlobalTerrorismAPI.common.service.BasicGenericService;

import java.util.Optional;

public interface CountryService extends BasicGenericService<CountryNode> {

    Optional<CountryNode> findByName(String name);

    boolean existsByName(String name);
}
