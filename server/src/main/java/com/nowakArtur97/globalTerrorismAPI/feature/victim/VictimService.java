package com.nowakArtur97.globalTerrorismAPI.feature.victim;

import com.nowakArtur97.globalTerrorismAPI.common.service.BasicGenericServiceImpl;

public class VictimService extends BasicGenericServiceImpl<VictimNode> {

    private final VictimRepository repository;

    VictimService(VictimRepository repository) {
        super(repository);
        this.repository = repository;
    }
}
