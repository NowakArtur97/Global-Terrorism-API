package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import com.NowakArtur97.GlobalTerrorismAPI.controller.EventController;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
		eventModel.setIsPartOfMultipleIncidents((eventNode.getIsPartOfMultipleIncidents()));
		eventModel.setIsSuccessful((eventNode.getIsSuccessful()));
		eventModel.setIsSuicide((eventNode.getIsSuicide()));

		eventModel
				.setTarget(eventNode.getTarget() != null ? targetModelAssembler.toModel(eventNode.getTarget()) : null);

		eventModel.add(linkTo(methodOn(EventController.class).findById(eventModel.getId())).withSelfRel());

		return eventModel;
	}
}
