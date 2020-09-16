package com.nowakArtur97.globalTerrorismAPI.common.repository;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.Node;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T extends Node> extends Neo4jRepository<T, Long> {

}
