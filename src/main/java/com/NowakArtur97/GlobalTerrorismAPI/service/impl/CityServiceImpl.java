package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.node.CityNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.CityRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.CityService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CityServiceImpl extends BaseGenericServiceImpl<CityNode> implements CityService {

    private final CityRepository repository;

    public CityServiceImpl(CityRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Optional<CityNode> findByName(String name) {

        return repository.findByName(name);
    }
}
