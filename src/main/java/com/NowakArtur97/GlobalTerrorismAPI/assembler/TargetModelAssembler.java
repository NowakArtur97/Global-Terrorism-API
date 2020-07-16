package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import com.NowakArtur97.GlobalTerrorismAPI.controller.target.TargetController;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.CountryModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@Slf4j
public class TargetModelAssembler extends RepresentationModelAssemblerSupport<TargetNode, TargetModel> {

    private final ObjectMapper objectMapper;

    public TargetModelAssembler(ObjectMapper objectMapper) {

        super(TargetController.class, TargetModel.class);
        this.objectMapper = objectMapper;
    }

    @Override
    public TargetModel toModel(TargetNode targetNode) {

        TargetModel targetModel = objectMapper.map(targetNode, TargetModel.class);

        if (targetModel.getCountryOfOrigin() != null) {
            targetModel.setCountryOfOrigin(objectMapper.map(targetModel.getCountryOfOrigin(), CountryModel.class));
        }

        log.info(String.valueOf(targetNode.getCountryOfOrigin() == null));

        targetModel.add(linkTo(methodOn(TargetController.class).findById(targetModel.getId())).withSelfRel());

        return targetModel;
    }
}
