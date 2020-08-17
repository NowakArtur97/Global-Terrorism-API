package com.NowakArtur97.GlobalTerrorismAPI.repository;

import com.NowakArtur97.GlobalTerrorismAPI.node.ProvinceNode;

import java.util.Optional;

public interface ProvinceRepository extends BaseRepository<ProvinceNode> {

    Optional<ProvinceNode> findByNameAndCountry_Name(String name, String countryName);
}
