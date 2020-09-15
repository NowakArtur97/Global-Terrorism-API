package com.NowakArtur97.GlobalTerrorismAPI.feature.country;

import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CountryModelAssembler extends RepresentationModelAssemblerSupport<CountryNode, CountryModel> {

    private final RegionModelAssembler regionModelAssembler;

    private final ObjectMapper objectMapper;

    public CountryModelAssembler(RegionModelAssembler regionModelAssembler, ObjectMapper objectMapper) {

        super(CountryController.class, CountryModel.class);
        this.objectMapper = objectMapper;
        this.regionModelAssembler = regionModelAssembler;
    }

    @Override
    public CountryModel toModel(CountryNode countryNode) {

        CountryModel countryModel = objectMapper.map(countryNode, CountryModel.class);

        if (countryNode.getRegion() != null) {
            countryModel.setRegion(regionModelAssembler.toModel(countryNode.getRegion()));
        }

        countryModel.add(linkTo(methodOn(CountryController.class).findById(countryModel.getId())).withSelfRel());

        return countryModel;
    }
}
