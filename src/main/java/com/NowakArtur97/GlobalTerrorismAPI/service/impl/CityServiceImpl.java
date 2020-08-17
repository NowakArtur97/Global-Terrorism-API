package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.CityDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.CityNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.CityRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.CityService;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.ProvinceService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class CityServiceImpl extends BaseGenericServiceImpl<CityNode> implements CityService {

    private final CityRepository repository;

    private final ObjectMapper objectMapper;

    private final ProvinceService provinceService;

    public CityServiceImpl(CityRepository repository, ObjectMapper objectMapper, ProvinceService provinceService) {
        super(repository);
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.provinceService = provinceService;
    }

    @Override
    public CityNode save(CityNode cityNode) {

        cityNode.setProvince(provinceService.findByNameAndCountryName(cityNode.getProvince())
                .orElse(provinceService.save(cityNode.getProvince())));

        return repository.save(cityNode);
    }

    @Override
    public CityNode saveNew(CityDTO cityDTO) {

        CityNode cityNode = objectMapper.map(cityDTO, CityNode.class);

        cityNode.setProvince(provinceService.findByNameAndCountryName(cityNode.getProvince())
                .orElse(provinceService.saveNew(cityDTO.getProvince())));

        return repository.save(cityNode);
    }

    @Override
    public Optional<CityNode> findByNameAndLatitudeAndLongitude(String name, Double latitude, Double longitude) {

        return repository.findByNameAndLatitudeAndLongitude(name, latitude, longitude);
    }
}
