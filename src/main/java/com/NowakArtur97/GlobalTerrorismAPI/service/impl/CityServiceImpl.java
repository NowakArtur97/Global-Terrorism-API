package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.CityDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.ProvinceDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.CityNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.ProvinceNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.CityRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.CityService;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.ProvinceService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class CityServiceImpl extends GenericServiceImpl<CityNode, CityDTO> implements CityService {

    private final int DEFAULT_DEPTH_FOR_CITY_NODE = 2;

    private final CityRepository repository;

    private final ProvinceService provinceService;

    public CityServiceImpl(CityRepository repository, ObjectMapper objectMapper, ProvinceService provinceService) {
        super(repository, objectMapper);
        this.repository = repository;
        this.provinceService = provinceService;
    }

    @Override
    public CityNode save(CityNode cityNode) {

        ProvinceNode provinceNode = cityNode.getProvince();

        Optional<ProvinceNode> provinceNodeOptional = provinceService
                .findByNameAndCountryName(provinceNode.getName(), provinceNode.getCountry().getName());

        if (provinceNodeOptional.isPresent()) {
            cityNode.setProvince(provinceNodeOptional.get());
        } else {
            cityNode.setProvince(provinceService.save(cityNode.getProvince()));
        }

        return repository.save(cityNode);
    }

    @Override
    public CityNode saveNew(CityDTO cityDTO) {

        CityNode cityNode = objectMapper.map(cityDTO, CityNode.class);

        ProvinceDTO provinceDTO = cityDTO.getProvince();

        Optional<ProvinceNode> provinceNodeOptional = provinceService
                .findByNameAndCountryName(provinceDTO.getName(), provinceDTO.getCountry().getName());

        if (provinceNodeOptional.isPresent()) {
            cityNode.setProvince(provinceNodeOptional.get());
        } else {
            cityNode.setProvince(provinceService.saveNew(cityDTO.getProvince()));
        }

        return repository.save(cityNode);
    }

    @Override
    public CityNode update(CityNode cityNode, CityDTO cityDTO) {

        Long id = cityNode.getId();

        ProvinceDTO provinceDTO = cityDTO.getProvince();

        Optional<ProvinceNode> provinceNodeOptional = provinceService
                .findByNameAndCountryName(provinceDTO.getName(), provinceDTO.getCountry().getName());

        ProvinceNode updatedProvince;

        if (provinceNodeOptional.isPresent()) {
            updatedProvince = provinceNodeOptional.get();
        } else {
            updatedProvince = provinceService.update(cityNode.getProvince(), cityDTO.getProvince());
        }

        cityNode = objectMapper.map(cityDTO, CityNode.class);

        cityNode.setId(id);
        cityNode.setProvince(updatedProvince);

        return repository.save(cityNode);
    }

    @Override
    public Optional<CityNode> findByNameAndLatitudeAndLongitude(String name, Double latitude, Double longitude) {

        return repository.findByNameAndLatitudeAndLongitude(name, latitude, longitude, DEFAULT_DEPTH_FOR_CITY_NODE);
    }
}
