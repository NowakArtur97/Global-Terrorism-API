package com.NowakArtur97.GlobalTerrorismAPI.feature.province;

import com.NowakArtur97.GlobalTerrorismAPI.common.service.GenericService;

import java.util.Optional;

public interface ProvinceService extends GenericService<ProvinceNode, ProvinceDTO> {

    Optional<ProvinceNode> findByNameAndCountryName(String provinceName, String countryName);
}
