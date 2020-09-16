package com.nowakArtur97.globalTerrorismAPI.common.service;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.Node;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BasicGenericService<T extends Node> {

    Optional<T> findById(Long id);

    Page<T> findAll(Pageable pageable);

    T save(T node);
}
