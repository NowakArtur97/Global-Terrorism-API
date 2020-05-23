package com.NowakArtur97.GlobalTerrorismAPI.service.api;

import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;

public interface TargetService extends GenericService<TargetNode, TargetDTO> {

    boolean isDatabaseEmpty();
}
