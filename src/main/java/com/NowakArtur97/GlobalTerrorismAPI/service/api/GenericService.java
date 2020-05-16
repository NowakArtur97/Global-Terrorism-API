package com.NowakArtur97.GlobalTerrorismAPI.service.api;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTONode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface GenericService<T> {

    Optional<T> findById(Long id);

    Page<T> findAll(Pageable pageable);

    T save(T node);

    T saveNew(DTONode dto);

    Optional<T> delete(Long id);
}
