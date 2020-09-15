package com.NowakArtur97.GlobalTerrorismAPI.feature.province;

import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryModelAssembler;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProvinceModelAssembler extends RepresentationModelAssemblerSupport<ProvinceNode, ProvinceModel> {

    private final CountryModelAssembler countryModelAssembler;

    private final ModelMapper modelMapper;

    ProvinceModelAssembler(CountryModelAssembler countryModelAssembler, ModelMapper modelMapper) {

        super(ProvinceController.class, ProvinceModel.class);
        this.modelMapper = modelMapper;
        this.countryModelAssembler = countryModelAssembler;
    }

    @Override
    public ProvinceModel toModel(ProvinceNode provinceNode) {

        ProvinceModel provinceModel = modelMapper.map(provinceNode, ProvinceModel.class);

        if (provinceNode.getCountry() != null) {
            provinceModel.setCountry(countryModelAssembler.toModel(provinceNode.getCountry()));
        }

        provinceModel.add(linkTo(methodOn(ProvinceController.class).findById(provinceModel.getId())).withSelfRel());

        return provinceModel;
    }
}
