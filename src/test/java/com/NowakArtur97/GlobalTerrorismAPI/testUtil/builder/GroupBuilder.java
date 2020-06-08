package com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Event;
import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Group;
import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.GroupDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.GroupModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;

import java.util.ArrayList;
import java.util.List;

public final class GroupBuilder {

	private Long id = 1L;

	private String name = "group";

	private List<Event> eventsCaused = new ArrayList<>();

	public GroupBuilder withId(Long id) {

		this.id = id;

		return this;
	}

	public GroupBuilder withName(String name) {

		this.name = name;

		return this;
	}

	public GroupBuilder withEventsCaused(List<Event> eventsCaused){

		this.eventsCaused = eventsCaused;

		return this;
	}

	public Group build(ObjectType type) {

		switch (type) {

		case DTO:

			List<EventDTO> eventDTOs = new ArrayList<>();
			eventsCaused.forEach(event -> eventDTOs.add((EventDTO) event));

			return new GroupDTO(name, eventDTOs);

		case NODE:

			List<EventNode> eventNodes = new ArrayList<>();
			eventsCaused.forEach(event -> eventNodes.add((EventNode) event));

			return new GroupNode(id, name, eventNodes);

		case MODEL:

			List<EventModel> eventModels = new ArrayList<>();
			eventsCaused.forEach(event -> eventModels.add((EventModel) event));

			return new GroupModel(id, name, eventModels);
		}

		throw new RuntimeException("The specified type does not exist");
	}
}
