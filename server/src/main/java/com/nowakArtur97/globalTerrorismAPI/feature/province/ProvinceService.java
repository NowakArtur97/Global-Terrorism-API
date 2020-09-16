package com.nowakArtur97.globalTerrorismAPI.feature.province;

import com.nowakArtur97.globalTerrorismAPI.common.service.GenericService;

import java.util.Optional;

public interface ProvinceService extends GenericService<ProvinceNode, ProvinceDTO> {

    Optional<ProvinceNode> findByNameAndCountryName(String provinceName, String countryName);
}
