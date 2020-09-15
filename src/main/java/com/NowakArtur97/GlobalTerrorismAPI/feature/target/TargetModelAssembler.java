package com.NowakArtur97.GlobalTerrorismAPI.feature.target;

import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TargetModelAssembler extends RepresentationModelAssemblerSupport<TargetNode, TargetModel> {

    private final CountryModelAssembler countryModelAssembler;

    private final ObjectMapper objectMapper;

    TargetModelAssembler(CountryModelAssembler countryModelAssembler, ObjectMapper objectMapper) {

        super(TargetController.class, TargetModel.class);
        this.countryModelAssembler = countryModelAssembler;
        this.objectMapper = objectMapper;
    }

    @Override
    public TargetModel toModel(TargetNode targetNode) {

        TargetModel targetModel = objectMapper.map(targetNode, TargetModel.class);

        if (targetNode.getCountryOfOrigin() != null) {
            targetModel.setCountryOfOrigin(countryModelAssembler.toModel(targetNode.getCountryOfOrigin()));
        }

        targetModel.add(linkTo(methodOn(TargetController.class).findById(targetModel.getId())).withSelfRel());

        return targetModel;
    }
}
