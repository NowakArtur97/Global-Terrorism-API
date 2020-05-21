package com.NowakArtur97.GlobalTerrorismAPI.service.api;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTONode;
import com.NowakArtur97.GlobalTerrorismAPI.node.Node;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface GenericService<T extends Node> {

    Optional<T> findById(Long id);

    Page<T> findAll(Pageable pageable);

    T save(T node);

    T saveNew(DTONode dto);

    T update(T node, DTONode dto);

    Optional<T> delete(Long id);
}
