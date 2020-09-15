package com.NowakArtur97.GlobalTerrorismAPI.feature.target;

import com.NowakArtur97.GlobalTerrorismAPI.common.service.GenericService;

public interface TargetService extends GenericService<TargetNode, TargetDTO> {

    boolean isDatabaseEmpty();
}
