package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.ProvinceDTO;
import com.NowakArtur97.GlobalTerrorismAPI.exception.ResourceNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.ProvinceNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.ProvinceRepository;
import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryService;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.ProvinceService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class ProvinceServiceImpl extends GenericServiceImpl<ProvinceNode, ProvinceDTO> implements ProvinceService {

    private final int DEFAULT_DEPTH_FOR_PROVINCE_NODE = 2;

    private final ProvinceRepository provinceRepository;

    private final CountryService countryService;

    ProvinceServiceImpl(ProvinceRepository provinceRepository, ObjectMapper objectMapper, CountryService countryService) {
        super(provinceRepository, objectMapper);
        this.countryService = countryService;
        this.provinceRepository = provinceRepository;
    }

    @Override
    public ProvinceNode save(ProvinceNode provinceNode) {

        provinceNode.setCountry(countryService.findByName(provinceNode.getCountry().getName())
                .orElseThrow(() -> new ResourceNotFoundException("CountryModel")));

        return provinceRepository.save(provinceNode);
    }

    @Override
    public ProvinceNode saveNew(ProvinceDTO provinceDTO) {

        ProvinceNode provinceNode = objectMapper.map(provinceDTO, ProvinceNode.class);

        provinceNode.setCountry(countryService.findByName(provinceDTO.getCountry().getName())
                .orElseThrow(() -> new ResourceNotFoundException("CountryModel")));

        return provinceRepository.save(provinceNode);
    }

    @Override
    public ProvinceNode update(ProvinceNode provinceNode, ProvinceDTO provinceDTO) {

        Long id = provinceNode.getId();

        provinceNode = objectMapper.map(provinceDTO, ProvinceNode.class);

        provinceNode.setId(id);

        provinceNode.setCountry(countryService.findByName(provinceDTO.getCountry().getName())
                .orElseThrow(() -> new ResourceNotFoundException("CountryModel")));

        return provinceRepository.save(provinceNode);
    }

    @Override
    public Optional<ProvinceNode> findByNameAndCountryName(String provinceName, String countryName) {

        return provinceRepository.findByNameAndCountry_Name(provinceName, countryName, DEFAULT_DEPTH_FOR_PROVINCE_NODE);
    }
}
