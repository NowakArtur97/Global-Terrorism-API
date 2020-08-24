package com.NowakArtur97.GlobalTerrorismAPI.repository;

import com.NowakArtur97.GlobalTerrorismAPI.node.CityNode;
import org.springframework.data.neo4j.annotation.Depth;

import java.util.Optional;

public interface CityRepository extends BaseRepository<CityNode> {

    Optional<CityNode> findByNameAndLatitudeAndLongitude(String name, Double latitude, Double longitude, @Depth int depth);
}
