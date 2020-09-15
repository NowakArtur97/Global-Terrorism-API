package com.NowakArtur97.GlobalTerrorismAPI.feature.country;

import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionModelAssembler;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CountryModelAssembler extends RepresentationModelAssemblerSupport<CountryNode, CountryModel> {

    private final RegionModelAssembler regionModelAssembler;

    private final ModelMapper modelMapper;

    CountryModelAssembler(RegionModelAssembler regionModelAssembler, ModelMapper modelMapper) {

        super(CountryController.class, CountryModel.class);
        this.modelMapper = modelMapper;
        this.regionModelAssembler = regionModelAssembler;
    }

    @Override
    public CountryModel toModel(CountryNode countryNode) {

        CountryModel countryModel = modelMapper.map(countryNode, CountryModel.class);

        if (countryNode.getRegion() != null) {
            countryModel.setRegion(regionModelAssembler.toModel(countryNode.getRegion()));
        }

        countryModel.add(linkTo(methodOn(CountryController.class).findById(countryModel.getId())).withSelfRel());

        return countryModel;
    }
}
