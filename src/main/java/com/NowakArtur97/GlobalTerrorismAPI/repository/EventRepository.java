package com.NowakArtur97.GlobalTerrorismAPI.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;

public interface EventRepository extends Neo4jRepository<EventNode, Long> {

}