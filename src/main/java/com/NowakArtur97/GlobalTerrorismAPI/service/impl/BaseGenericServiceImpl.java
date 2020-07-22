package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.node.Node;
import com.NowakArtur97.GlobalTerrorismAPI.repository.BaseRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.BaseGenericService;
import org.springframework.stereotype.Service;

@Service
abstract class BaseGenericServiceImpl<T extends Node> implements BaseGenericService<T> {

    protected final BaseRepository<T> repository;

    BaseGenericServiceImpl(BaseRepository<T> repository) {
        this.repository = repository;
    }

    @Override
    public T save(T node) {

        return repository.save(node);
    }
}
