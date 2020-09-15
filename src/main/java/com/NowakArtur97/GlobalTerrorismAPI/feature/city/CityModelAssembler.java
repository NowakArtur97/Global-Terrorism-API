package com.NowakArtur97.GlobalTerrorismAPI.feature.city;

import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CityModelAssembler extends RepresentationModelAssemblerSupport<CityNode, CityModel> {

    private final ProvinceModelAssembler provinceModelAssembler;

    private final ObjectMapper objectMapper;

    CityModelAssembler(ProvinceModelAssembler provinceModelAssembler, ObjectMapper objectMapper) {

        super(CityController.class, CityModel.class);
        this.provinceModelAssembler = provinceModelAssembler;
        this.objectMapper = objectMapper;
    }

    @Override
    public CityModel toModel(CityNode cityNode) {

        CityModel cityModel = objectMapper.map(cityNode, CityModel.class);

        if (cityNode.getProvince() != null) {
            cityModel.setProvince(provinceModelAssembler.toModel(cityNode.getProvince()));
        }

        cityModel.add(linkTo(methodOn(CityController.class).findById(cityModel.getId())).withSelfRel());

        return cityModel;
    }
}
