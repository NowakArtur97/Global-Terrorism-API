package com.nowakArtur97.globalTerrorismAPI.feature.country;

import com.nowakArtur97.globalTerrorismAPI.common.service.BasicGenericServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public
class CountryService extends BasicGenericServiceImpl<CountryNode> {

    private final CountryRepository repository;

    CountryService(CountryRepository repository) {
        super(repository);
        this.repository = repository;
    }

    public Optional<CountryNode> findByName(String name) {

        return repository.findByName(name);
    }

    public boolean existsByName(String name) {

        return repository.existsByName(name);
    }
}
