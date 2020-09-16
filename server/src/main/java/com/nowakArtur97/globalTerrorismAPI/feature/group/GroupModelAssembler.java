package com.nowakArtur97.globalTerrorismAPI.feature.group;

import com.nowakArtur97.globalTerrorismAPI.feature.event.EventModel;
import com.nowakArtur97.globalTerrorismAPI.feature.event.EventModelAssembler;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class GroupModelAssembler extends RepresentationModelAssemblerSupport<GroupNode, GroupModel> {

    private final EventModelAssembler eventModelAssembler;

    private final ModelMapper modelMapper;

    GroupModelAssembler(EventModelAssembler eventModelAssembler, ModelMapper modelMapper) {

        super(GroupController.class, GroupModel.class);
        this.eventModelAssembler = eventModelAssembler;
        this.modelMapper = modelMapper;
    }

    @Override
    public GroupModel toModel(GroupNode groupNode) {

        GroupModel groupModel = modelMapper.map(groupNode, GroupModel.class);

        List<EventModel> eventsCaused = groupNode.getEventsCaused()
                .stream().map(eventModelAssembler::toModel)
                .collect(Collectors.toList());

        groupModel.setEventsCaused(eventsCaused);

        groupModel.add(linkTo(methodOn(GroupController.class).findById(groupModel.getId())).withSelfRel());
        groupModel.add(linkTo(methodOn(GroupEventsController.class).findGroupEvents(groupModel.getId(), Pageable.unpaged()))
                .withRel("eventsCaused"));

        return groupModel;
    }
}
