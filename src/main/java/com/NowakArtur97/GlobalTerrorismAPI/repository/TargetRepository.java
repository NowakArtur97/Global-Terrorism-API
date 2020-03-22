package com.NowakArtur97.GlobalTerrorismAPI.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.NowakArtur97.GlobalTerrorismAPI.node.Target;

public interface TargetRepository extends Neo4jRepository<Target, Long> {

}
