package com.NowakArtur97.GlobalTerrorismAPI.service.api;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;

import java.util.Optional;

public interface EventService extends GenericService<EventNode, EventDTO> {

    Optional<EventNode> deleteEventTarget(Long id);

    EventNode addOrUpdateEventTarget(EventNode eventNode, TargetDTO targetDTO);
}
