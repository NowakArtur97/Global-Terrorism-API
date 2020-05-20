package com.NowakArtur97.GlobalTerrorismAPI.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T> extends Neo4jRepository<T, Long> {

}
