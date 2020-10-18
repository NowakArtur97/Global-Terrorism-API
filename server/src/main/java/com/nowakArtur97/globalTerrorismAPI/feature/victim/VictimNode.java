package com.nowakArtur97.globalTerrorismAPI.feature.victim;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.Node;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "Victim")
@Getter
@Setter
@NoArgsConstructor
public class VictimNode extends Node implements Victim {

    private long totalNumberOfFatalities;

    private long numberOfPerpetratorFatalities;

    private long totalNumberOfInjured;

    private long numberOfPerpetratorInjured;

    private long valueOfPropertyDamage;
}
