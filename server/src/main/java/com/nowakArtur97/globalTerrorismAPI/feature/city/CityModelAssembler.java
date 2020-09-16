package com.nowakArtur97.globalTerrorismAPI.feature.city;

import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceModelAssembler;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CityModelAssembler extends RepresentationModelAssemblerSupport<CityNode, CityModel> {

    private final ProvinceModelAssembler provinceModelAssembler;

    private final ModelMapper modelMapper;

    CityModelAssembler(ProvinceModelAssembler provinceModelAssembler, ModelMapper modelMapper) {

        super(CityController.class, CityModel.class);
        this.provinceModelAssembler = provinceModelAssembler;
        this.modelMapper = modelMapper;
    }

    @Override
    public CityModel toModel(CityNode cityNode) {

        CityModel cityModel = modelMapper.map(cityNode, CityModel.class);

        if (cityNode.getProvince() != null) {
            cityModel.setProvince(provinceModelAssembler.toModel(cityNode.getProvince()));
        }

        cityModel.add(linkTo(methodOn(CityController.class).findById(cityModel.getId())).withSelfRel());

        return cityModel;
    }
}
