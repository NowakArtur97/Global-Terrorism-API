package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;

public interface TargetMapper {

	TargetNode mapDTOToNode(TargetDTO targetDTO);
}
