package com.NowakArtur97.GlobalTerrorismAPI.feature.city;

import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceService;
import com.NowakArtur97.GlobalTerrorismAPI.common.service.GenericServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class CityServiceImpl extends GenericServiceImpl<CityNode, CityDTO> implements CityService {

    private final int DEFAULT_DEPTH_FOR_CITY_NODE = 2;

    private final CityRepository repository;

    private final ProvinceService provinceService;

    CityServiceImpl(CityRepository repository, ModelMapper modelMapper, ProvinceService provinceService) {
        super(repository, modelMapper);
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

        CityNode cityNode = modelMapper.map(cityDTO, CityNode.class);

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

        cityNode = modelMapper.map(cityDTO, CityNode.class);

        cityNode.setId(id);
        cityNode.setProvince(updatedProvince);

        return repository.save(cityNode);
    }

    @Override
    public Optional<CityNode> findByNameAndLatitudeAndLongitude(String name, Double latitude, Double longitude) {

        return repository.findByNameAndLatitudeAndLongitude(name, latitude, longitude, DEFAULT_DEPTH_FOR_CITY_NODE);
    }
}
