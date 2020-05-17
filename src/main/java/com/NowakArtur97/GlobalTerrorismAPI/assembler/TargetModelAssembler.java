package com.NowakArtur97.GlobalTerrorismAPI.assembler;

import com.NowakArtur97.GlobalTerrorismAPI.controller.TargetController;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TargetModelAssembler extends RepresentationModelAssemblerSupport<TargetNode, TargetModel> {

	public TargetModelAssembler() {
		super(TargetController.class, TargetModel.class);
	}

	@Override
	public TargetModel toModel(TargetNode targetNode) {

		TargetModel targetModel = instantiateModel(targetNode);

		targetModel.setId(targetNode.getId());
		targetModel.setTarget(targetNode.getTarget());

		targetModel.add(linkTo(methodOn(TargetController.class).findById(targetModel.getId())).withSelfRel());

		return targetModel;
	}
}
