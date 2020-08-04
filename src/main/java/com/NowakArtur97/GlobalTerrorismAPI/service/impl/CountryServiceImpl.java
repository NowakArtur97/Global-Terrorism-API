package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.node.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.CountryRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.CountryService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class CountryServiceImpl extends BaseGenericServiceImpl<CountryNode> implements CountryService {

    private final CountryRepository repository;

    public CountryServiceImpl(CountryRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Optional<CountryNode> findByName(String name) {

        return repository.findByName(name);
    }

    @Override
    public boolean existsByName(String name) {

        return repository.existsByName(name);
    }
}
