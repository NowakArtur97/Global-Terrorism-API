package com.NowakArtur97.GlobalTerrorismAPI.common.repository;

import com.NowakArtur97.GlobalTerrorismAPI.common.baseModel.Node;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T extends Node> extends Neo4jRepository<T, Long> {

}
