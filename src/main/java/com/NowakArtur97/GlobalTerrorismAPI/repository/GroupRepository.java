package com.NowakArtur97.GlobalTerrorismAPI.repository;

import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface GroupRepository extends Neo4jRepository<GroupNode, Long> {

}
