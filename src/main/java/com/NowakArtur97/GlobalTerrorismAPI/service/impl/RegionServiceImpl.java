package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.node.RegionNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.RegionRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.RegionService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class RegionServiceImpl extends BasicGenericServiceImpl<RegionNode> implements RegionService {

    private final RegionRepository repository;

    RegionServiceImpl(RegionRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Optional<RegionNode> findByName(String name) {

        return repository.findByName(name);
    }

    @Override
    public boolean existsByName(String name) {

        return repository.existsByName(name);
    }
}
