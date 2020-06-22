package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTONode;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.Node;
import com.NowakArtur97.GlobalTerrorismAPI.repository.BaseRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
abstract class GenericServiceImpl<T extends Node, D extends DTONode> implements GenericService<T, D> {

    private static final int DEFAULT_SEARCHING_DEPTH = 1;

    private final Class<T> typeParameterClass;

    protected final BaseRepository<T> repository;

    protected final ObjectMapper objectMapper;

    GenericServiceImpl(BaseRepository<T> repository, ObjectMapper objectMapper) {
        this.typeParameterClass = (Class<T>) GenericTypeResolver.resolveTypeArguments(getClass(), GenericServiceImpl.class)[0];
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<T> findById(Long id) {

        return id != null ? repository.findById(id) : Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<T> findAll(Pageable pageable) {

        return repository.findAll(pageable, DEFAULT_SEARCHING_DEPTH);
    }

    @Override
    public T save(T node) {

        return repository.save(node);
    }

    @Override
    public T saveNew(D dto) {

        T node = objectMapper.map(dto, typeParameterClass);

        return repository.save(node);
    }

    @Override
    public T update(T node, D dto) {

        Long id = node.getId();

        node = objectMapper.map(dto, typeParameterClass);

        node.setId(id);

        return repository.save(node);
    }

    @Override
    public Optional<T> delete(Long id) {

        Optional<T> nodeOptional = findById(id);

        nodeOptional.ifPresent(repository::delete);

        return nodeOptional;
    }
}
