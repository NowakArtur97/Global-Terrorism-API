package com.nowakArtur97.globalTerrorismAPI.feature.country;

import com.nowakArtur97.globalTerrorismAPI.common.service.BasicGenericService;

import java.util.Optional;

public interface CountryService extends BasicGenericService<CountryNode> {

    Optional<CountryNode> findByName(String name);

    boolean existsByName(String name);
}
