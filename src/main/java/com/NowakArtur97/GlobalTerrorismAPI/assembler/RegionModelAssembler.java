package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import com.NowakArtur97.GlobalTerrorismAPI.controller.region.RegionController;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.RegionModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.RegionNode;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RegionModelAssembler extends RepresentationModelAssemblerSupport<RegionNode, RegionModel> {

    private final ObjectMapper objectMapper;

    public RegionModelAssembler(ObjectMapper objectMapper) {

        super(RegionController.class, RegionModel.class);
        this.objectMapper = objectMapper;
    }

    @Override
    public RegionModel toModel(RegionNode regionNode) {

        RegionModel regionModel = objectMapper.map(regionNode, RegionModel.class);

        regionModel.add(linkTo(methodOn(RegionController.class).findById(regionModel.getId())).withSelfRel());

        return regionModel;
    }
}
