package com.NowakArtur97.GlobalTerrorismAPI.service.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface GenericService<T, D> {

    Optional<T> findById(Long id);

    Page<T> findAll(Pageable pageable);

    T save(T node);

    T saveNew(D dto);

    T update(T node, D dto);

    Optional<T> delete(Long id);
}
