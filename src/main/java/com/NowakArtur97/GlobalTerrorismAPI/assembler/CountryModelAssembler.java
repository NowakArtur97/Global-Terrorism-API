package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import com.NowakArtur97.GlobalTerrorismAPI.controller.country.CountryController;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.CountryModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.CountryNode;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CountryModelAssembler extends RepresentationModelAssemblerSupport<CountryNode, CountryModel> {

    private final ObjectMapper objectMapper;

    public CountryModelAssembler(ObjectMapper objectMapper) {

        super(CountryController.class, CountryModel.class);
        this.objectMapper = objectMapper;
    }

    @Override
    public CountryModel toModel(CountryNode countryNode) {

        CountryModel countryModel = objectMapper.map(countryNode, CountryModel.class);

        countryModel.add(linkTo(methodOn(CountryController.class).findById(countryModel.getId())).withSelfRel());

        return countryModel;
    }
}
