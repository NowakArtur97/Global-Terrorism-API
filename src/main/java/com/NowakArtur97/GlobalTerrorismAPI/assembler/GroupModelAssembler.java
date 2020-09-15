package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import com.NowakArtur97.GlobalTerrorismAPI.controller.GroupController;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GroupEventsController;
import com.NowakArtur97.GlobalTerrorismAPI.feature.event.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.event.EventModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.GroupModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GroupModelAssembler extends RepresentationModelAssemblerSupport<GroupNode, GroupModel> {

    private final EventModelAssembler eventModelAssembler;

    private final ObjectMapper objectMapper;

    public GroupModelAssembler(EventModelAssembler eventModelAssembler, ObjectMapper objectMapper) {

        super(GroupController.class, GroupModel.class);
        this.eventModelAssembler = eventModelAssembler;
        this.objectMapper = objectMapper;
    }

    @Override
    public GroupModel toModel(GroupNode groupNode) {

        GroupModel groupModel = objectMapper.map(groupNode, GroupModel.class);

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
