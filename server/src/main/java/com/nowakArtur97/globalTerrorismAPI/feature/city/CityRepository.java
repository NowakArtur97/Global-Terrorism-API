package com.nowakArtur97.globalTerrorismAPI.feature.city;

import com.nowakArtur97.globalTerrorismAPI.common.repository.BaseRepository;
import org.springframework.data.neo4j.annotation.Depth;

import java.util.Optional;

public interface CityRepository extends BaseRepository<CityNode> {

    Optional<CityNode> findByNameAndLatitudeAndLongitude(String name, Double latitude, Double longitude, @Depth int depth);
}
