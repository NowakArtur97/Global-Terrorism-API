package com.nowakArtur97.globalTerrorismAPI.feature.target;

import com.nowakArtur97.globalTerrorismAPI.common.service.GenericService;

public interface TargetService extends GenericService<TargetNode, TargetDTO> {

    boolean isDatabaseEmpty();
}
