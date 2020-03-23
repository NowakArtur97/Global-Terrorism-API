package com.NowakArtur97.GlobalTerrorismAPI.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;

public interface TargetRepository extends Neo4jRepository<TargetNode, Long> {

}
