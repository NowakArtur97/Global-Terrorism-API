package com.nowakArtur97.globalTerrorismAPI.feature.region;

import com.nowakArtur97.globalTerrorismAPI.common.service.BasicGenericServiceImpl;
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
