package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.NowakArtur97.GlobalTerrorismAPI.controller.EventController;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;

@Component
public class EventModelAssembler extends RepresentationModelAssemblerSupport<EventNode, EventModel> {

	private final TargetModelAssembler targetModelAssembler;

	public EventModelAssembler(TargetModelAssembler targetModelAssembler) {

		super(EventController.class, EventModel.class);

		this.targetModelAssembler = targetModelAssembler;
	}

	@Override
	public EventModel toModel(EventNode eventNode) {

		EventModel eventModel = instantiateModel(eventNode);

		eventModel.setId((eventNode.getId()));
		eventModel.setSummary((eventNode.getSummary()));
		eventModel.setMotive((eventNode.getMotive()));
		eventModel.setDate((eventNode.getDate()));
		eventModel.setPartOfMultipleIncidents((eventNode.isPartOfMultipleIncidents()));
		eventModel.setSuccessful((eventNode.isSuccessful()));
		eventModel.setSuicide((eventNode.isSuicide()));

		eventModel
				.setTarget(eventNode.getTarget() != null ? targetModelAssembler.toModel(eventNode.getTarget()) : null);

		eventModel.add(linkTo(methodOn(EventController.class).findEventById(eventModel.getId())).withSelfRel());

		return eventModel;
	}
}
