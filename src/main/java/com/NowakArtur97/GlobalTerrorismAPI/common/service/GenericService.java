package com.NowakArtur97.GlobalTerrorismAPI.common.service;

import com.NowakArtur97.GlobalTerrorismAPI.common.baseModel.DTO;
import com.NowakArtur97.GlobalTerrorismAPI.common.baseModel.Node;

import java.util.Optional;

public interface GenericService<T extends Node, D extends DTO> extends BasicGenericService<T> {

    Optional<T> findById(Long id, int depth);

    T saveNew(D dto);

    T update(T node, D dto);

    Optional<T> delete(Long id);
}
