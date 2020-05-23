package com.NowakArtur97.GlobalTerrorismAPI.util;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTONode;
import com.NowakArtur97.GlobalTerrorismAPI.node.Node;

public interface ViolationHelper<T extends Node, D extends DTONode> {

    void violate(T entity, Class<D> dtoType);
}
