package com.nowakArtur97.globalTerrorismAPI.feature.event;

import com.nowakArtur97.globalTerrorismAPI.feature.city.CityModelAssembler;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetModelAssembler;
import com.nowakArtur97.globalTerrorismAPI.feature.victim.VictimModelAssembler;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EventModelAssembler extends RepresentationModelAssemblerSupport<EventNode, EventModel> {

    private final TargetModelAssembler targetModelAssembler;

    private final CityModelAssembler cityModelAssembler;

    private final VictimModelAssembler victimModelAssembler;

    private final ModelMapper modelMapper;

    EventModelAssembler(TargetModelAssembler targetModelAssembler, CityModelAssembler cityModelAssembler,
                        VictimModelAssembler victimModelAssembler, ModelMapper modelMapper) {

        super(EventController.class, EventModel.class);
        this.targetModelAssembler = targetModelAssembler;
        this.cityModelAssembler = cityModelAssembler;
        this.victimModelAssembler = victimModelAssembler;
        this.modelMapper = modelMapper;
    }

    @Override
    public EventModel toModel(EventNode eventNode) {

        EventModel eventModel = modelMapper.map(eventNode, EventModel.class);

        eventModel.add(linkTo(methodOn(EventController.class).findById(eventModel.getId())).withSelfRel());

        if (eventNode.getTarget() != null) {
            eventModel.setTarget(targetModelAssembler.toModel(eventNode.getTarget()));

            eventModel.add(linkTo(methodOn(EventTargetController.class).findEventTarget(eventModel.getId())).withRel("target"));
        }

        if (eventNode.getCity() != null) {
            eventModel.setCity(cityModelAssembler.toModel(eventNode.getCity()));
        }

        if (eventNode.getVictim() != null) {
            eventModel.setVictim(victimModelAssembler.toModel(eventNode.getVictim()));
        }

        return eventModel;
    }
}
