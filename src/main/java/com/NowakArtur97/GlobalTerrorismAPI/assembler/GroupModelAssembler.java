package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import com.NowakArtur97.GlobalTerrorismAPI.controller.GroupController;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.model.GroupModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

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

//        eventModel
//                .setTarget(eventNode.getTarget() != null ? targetModelAssembler.toModel(eventNode.getTarget()) : null);

        groupModel.add(linkTo(methodOn(GroupController.class).findById(groupModel.getId())).withSelfRel());

        return groupModel;
    }
}
