package com.nowakArtur97.globalTerrorismAPI.feature.province;

import com.nowakArtur97.globalTerrorismAPI.common.exception.ResourceNotFoundException;
import com.nowakArtur97.globalTerrorismAPI.common.service.GenericServiceImpl;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public
class ProvinceService extends GenericServiceImpl<ProvinceNode, ProvinceDTO> {

    private final int DEFAULT_DEPTH_FOR_PROVINCE_NODE = 2;

    private final ProvinceRepository provinceRepository;

    private final CountryService countryService;

    ProvinceService(ProvinceRepository provinceRepository, ModelMapper modelMapper, CountryService countryService) {
        super(provinceRepository, modelMapper);
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

        ProvinceNode provinceNode = modelMapper.map(provinceDTO, ProvinceNode.class);

        provinceNode.setCountry(countryService.findByName(provinceDTO.getCountry().getName())
                .orElseThrow(() -> new ResourceNotFoundException("CountryModel")));

        return provinceRepository.save(provinceNode);
    }

    @Override
    public ProvinceNode update(ProvinceNode provinceNode, ProvinceDTO provinceDTO) {

        Long id = provinceNode.getId();

        provinceNode = modelMapper.map(provinceDTO, ProvinceNode.class);

        provinceNode.setId(id);

        provinceNode.setCountry(countryService.findByName(provinceDTO.getCountry().getName())
                .orElseThrow(() -> new ResourceNotFoundException("CountryModel")));

        return provinceRepository.save(provinceNode);
    }

    public Optional<ProvinceNode> findByNameAndCountryName(String provinceName, String countryName) {

        return provinceRepository.findByNameAndCountry_Name(provinceName, countryName, DEFAULT_DEPTH_FOR_PROVINCE_NODE);
    }
}
