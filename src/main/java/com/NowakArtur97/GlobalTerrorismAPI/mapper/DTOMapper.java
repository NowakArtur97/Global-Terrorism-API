package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTONode;
import com.NowakArtur97.GlobalTerrorismAPI.node.Node;

public interface DTOMapper<T extends Node, D extends DTONode> {

    T mapToNode(D dto, Class<T> destinationType);

    D mapToDTO(T node, Class<D> destinationType);
}