package com.nowakArtur97.globalTerrorismAPI.common.baseModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Node {

    @Id
    @GeneratedValue
    protected Long id;
}
