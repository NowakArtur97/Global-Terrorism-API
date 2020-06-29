package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import com.NowakArtur97.GlobalTerrorismAPI.controller.event.EventController;
import com.NowakArtur97.GlobalTerrorismAPI.controller.event.EventTargetController;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EventModelAssembler extends RepresentationModelAssemblerSupport<EventNode, EventModel> {

    private final TargetModelAssembler targetModelAssembler;

    private final ObjectMapper objectMapper;

    public EventModelAssembler(TargetModelAssembler targetModelAssembler, ObjectMapper objectMapper) {

        super(EventController.class, EventModel.class);

        this.targetModelAssembler = targetModelAssembler;

        this.objectMapper = objectMapper;
    }

    @Override
    public EventModel toModel(EventNode eventNode) {

        EventModel eventModel = objectMapper.map(eventNode, EventModel.class);

        if (eventNode.getTarget() != null) {
            eventModel.setTarget(targetModelAssembler.toModel(eventNode.getTarget()));
            
            eventModel.add(linkTo(methodOn(EventTargetController.class).findEventTarget(eventModel.getId(), Pageable.unpaged())).withRel("target"));
        }

        eventModel.add(linkTo(methodOn(EventController.class).findById(eventModel.getId())).withSelfRel());

        return eventModel;
    }
}
