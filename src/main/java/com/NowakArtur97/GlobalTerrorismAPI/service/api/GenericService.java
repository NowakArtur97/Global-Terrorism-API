package com.NowakArtur97.GlobalTerrorismAPI.service.api;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTONode;
import com.NowakArtur97.GlobalTerrorismAPI.node.Node;

import java.util.Optional;

public interface GenericService<T extends Node, D extends DTONode> extends BasicGenericService<T> {

    Optional<T> findById(Long id, int depth);

    T saveNew(D dto);

    T update(T node, D dto);

    Optional<T> delete(Long id);
}
