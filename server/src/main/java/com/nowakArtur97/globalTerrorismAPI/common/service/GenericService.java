package com.nowakArtur97.globalTerrorismAPI.common.service;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.DTO;
import com.nowakArtur97.globalTerrorismAPI.common.baseModel.Node;

import java.util.Optional;

public interface GenericService<T extends Node, D extends DTO> extends BasicGenericService<T> {

    Optional<T> findById(Long id, int depth);

    T saveNew(D dto);

    T update(T node, D dto);

    Optional<T> delete(Long id);
}
