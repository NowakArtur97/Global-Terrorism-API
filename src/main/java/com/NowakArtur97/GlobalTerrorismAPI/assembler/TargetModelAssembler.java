package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import com.NowakArtur97.GlobalTerrorismAPI.controller.TargetController;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TargetModelAssembler extends RepresentationModelAssemblerSupport<TargetNode, TargetModel> {

    private final CountryModelAssembler countryModelAssembler;

    private final ObjectMapper objectMapper;

    public TargetModelAssembler(CountryModelAssembler countryModelAssembler, ObjectMapper objectMapper) {

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
