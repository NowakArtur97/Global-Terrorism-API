package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.node.Node;
import com.NowakArtur97.GlobalTerrorismAPI.repository.BaseRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.BasicGenericService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public abstract class BasicGenericServiceImpl<T extends Node> implements BasicGenericService<T> {

    final BaseRepository<T> repository;

    public BasicGenericServiceImpl(BaseRepository<T> repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<T> findById(Long id) {

        return id != null ? repository.findById(id) : Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<T> findAll(Pageable pageable) {

        return repository.findAll(pageable);
    }

    @Override
    public T save(T node) {

        return repository.save(node);
    }
}
