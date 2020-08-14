package com.NowakArtur97.GlobalTerrorismAPI.service.api;

import com.NowakArtur97.GlobalTerrorismAPI.dto.ProvinceDTO;
import com.NowakArtur97.GlobalTerrorismAPI.node.ProvinceNode;

import java.util.Optional;

public interface ProvinceService extends GenericService<ProvinceNode, ProvinceDTO> {

    Optional<ProvinceNode> findByName(String name);
}
