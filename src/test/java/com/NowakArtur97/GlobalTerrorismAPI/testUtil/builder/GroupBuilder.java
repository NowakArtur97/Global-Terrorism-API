package com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Event;
import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Group;
import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.GroupDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.GroupModel;
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

    public GroupBuilder withEventsCaused(List<Event> eventsCaused) {

        this.eventsCaused = eventsCaused;

        return this;
    }

    public Group build(ObjectType type) {

        Group group;

        switch (type) {

            case DTO:

                List<EventDTO> eventDTOs = convertEvents(eventsCaused);

                group = new GroupDTO(name, eventDTOs);

                break;

            case NODE:

                List<EventNode> eventNodes = convertEvents(eventsCaused);

                group = new GroupNode(id, name, eventNodes);

                break;

            case MODEL:

                List<EventModel> eventModels = convertEvents(eventsCaused);

                group = new GroupModel(id, name, eventModels);

                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return group;
    }

    private <T> List<T> convertEvents(List<Event> eventsCaused) {

        List<T> events = new ArrayList<>();

        if (eventsCaused != null) {
            eventsCaused.forEach(event -> events.add((T) event));
        }

        return events;
    }

    private void resetProperties() {

        this.id = 1L;

        this.name = "group";

        this.eventsCaused = new ArrayList<>();
    }
}
