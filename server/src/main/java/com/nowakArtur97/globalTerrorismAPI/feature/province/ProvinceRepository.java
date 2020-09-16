package com.nowakArtur97.globalTerrorismAPI.feature.province;

import com.nowakArtur97.globalTerrorismAPI.common.repository.BaseRepository;
import org.springframework.data.neo4j.annotation.Depth;

import java.util.Optional;

public interface ProvinceRepository extends BaseRepository<ProvinceNode> {

    Optional<ProvinceNode> findByNameAndCountry_Name(String name, String countryName, @Depth int depth);
}
