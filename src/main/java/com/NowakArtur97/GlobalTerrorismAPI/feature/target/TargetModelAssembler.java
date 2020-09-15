package com.NowakArtur97.GlobalTerrorismAPI.feature.target;

import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryModelAssembler;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TargetModelAssembler extends RepresentationModelAssemblerSupport<TargetNode, TargetModel> {

    private final CountryModelAssembler countryModelAssembler;

    private final ModelMapper modelMapper;

    TargetModelAssembler(CountryModelAssembler countryModelAssembler, ModelMapper modelMapper) {

        super(TargetController.class, TargetModel.class);
        this.countryModelAssembler = countryModelAssembler;
        this.modelMapper = modelMapper;
    }

    @Override
    public TargetModel toModel(TargetNode targetNode) {

        TargetModel targetModel = modelMapper.map(targetNode, TargetModel.class);

        if (targetNode.getCountryOfOrigin() != null) {
            targetModel.setCountryOfOrigin(countryModelAssembler.toModel(targetNode.getCountryOfOrigin()));
        }

        targetModel.add(linkTo(methodOn(TargetController.class).findById(targetModel.getId())).withSelfRel());

        return targetModel;
    }
}
