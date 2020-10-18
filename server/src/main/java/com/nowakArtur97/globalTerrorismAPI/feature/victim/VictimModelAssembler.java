package com.nowakArtur97.globalTerrorismAPI.feature.victim;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class VictimModelAssembler extends RepresentationModelAssemblerSupport<VictimNode, VictimModel> {

    private final ModelMapper modelMapper;

    VictimModelAssembler(ModelMapper modelMapper) {

        super(VictimController.class, VictimModel.class);
        this.modelMapper = modelMapper;
    }

    @Override
    public VictimModel toModel(VictimNode victimNode) {

        VictimModel victimModel = modelMapper.map(victimNode, VictimModel.class);

        victimModel.add(linkTo(methodOn(VictimController.class).findById(victimModel.getId())).withSelfRel());

        return modelMapper.map(victimNode, VictimModel.class);
    }
}
