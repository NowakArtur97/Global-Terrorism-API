package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import com.NowakArtur97.GlobalTerrorismAPI.controller.province.ProvinceController;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.ProvinceModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.ProvinceNode;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProvinceModelAssembler extends RepresentationModelAssemblerSupport<ProvinceNode, ProvinceModel> {

    private final ObjectMapper objectMapper;

    public ProvinceModelAssembler(ObjectMapper objectMapper) {

        super(ProvinceController.class, ProvinceModel.class);

        this.objectMapper = objectMapper;
    }

    @Override
    public ProvinceModel toModel(ProvinceNode provinceNode) {

        ProvinceModel provinceModel = objectMapper.map(provinceNode, ProvinceModel.class);

        provinceModel.add(linkTo(methodOn(ProvinceController.class).findById(provinceModel.getId())).withSelfRel());

        return provinceModel;
    }
}
