package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.CityDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.CityNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.CityRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.CityService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class CityServiceImpl extends BaseGenericServiceImpl<CityNode> implements CityService {

    private final CityRepository repository;

    private final ObjectMapper objectMapper;

    public CityServiceImpl(CityRepository repository, ObjectMapper objectMapper) {
        super(repository);
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<CityNode> findByName(String name) {

        return repository.findByName(name);
    }

    @Override
    public CityNode saveNew(CityDTO cityDTO) {

        CityNode cityNode = objectMapper.map(cityDTO, CityNode.class);

        return repository.save(cityNode);
    }
}
