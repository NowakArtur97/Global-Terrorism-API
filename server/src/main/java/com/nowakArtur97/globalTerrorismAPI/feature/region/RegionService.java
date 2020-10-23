package com.nowakArtur97.globalTerrorismAPI.feature.region;

import com.nowakArtur97.globalTerrorismAPI.common.service.BasicGenericServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegionService extends BasicGenericServiceImpl<RegionNode> {

    private final RegionRepository repository;

    RegionService(RegionRepository repository) {
        super(repository);
        this.repository = repository;
    }

    public Optional<RegionNode> findByName(String name) {

        return repository.findByName(name);
    }

    public boolean existsByName(String name) {

        return repository.existsByName(name);
    }
}
