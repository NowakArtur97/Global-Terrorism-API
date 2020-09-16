package com.nowakArtur97.globalTerrorismAPI.feature.region;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RegionModelAssembler extends RepresentationModelAssemblerSupport<RegionNode, RegionModel> {

    private final ModelMapper modelMapper;

    RegionModelAssembler(ModelMapper modelMapper) {

        super(RegionController.class, RegionModel.class);
        this.modelMapper = modelMapper;
    }

    @Override
    public RegionModel toModel(RegionNode regionNode) {

        RegionModel regionModel = modelMapper.map(regionNode, RegionModel.class);

        regionModel.add(linkTo(methodOn(RegionController.class).findById(regionModel.getId())).withSelfRel());

        return regionModel;
    }
}
