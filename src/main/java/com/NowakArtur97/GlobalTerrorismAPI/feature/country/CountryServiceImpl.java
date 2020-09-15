package com.NowakArtur97.GlobalTerrorismAPI.feature.country;

import com.NowakArtur97.GlobalTerrorismAPI.service.impl.BasicGenericServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class CountryServiceImpl extends BasicGenericServiceImpl<CountryNode> implements CountryService {

    private final CountryRepository repository;

    CountryServiceImpl(CountryRepository repository) {
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
