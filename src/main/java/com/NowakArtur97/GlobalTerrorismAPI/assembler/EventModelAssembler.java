package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;

@Component
public class EventModelAssembler extends RepresentationModelAssemblerSupport<EventNode, EventModel> {

	public EventModelAssembler(Class<?> controllerClass, Class<EventModel> resourceType) {
		super(controllerClass, resourceType);
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
		eventModel.setSuccessful((eventNode.isSuicide()));

//		eventModel.add(linkTo(methodOn(EventController.class).findEventById(eventModel.getId())).withSelfRel());

		return eventModel;
	}

}
