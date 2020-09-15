package com.NowakArtur97.GlobalTerrorismAPI.feature.event;

import com.NowakArtur97.GlobalTerrorismAPI.feature.target.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.common.service.GenericService;

import java.util.Optional;

public interface EventService extends GenericService<EventNode, EventDTO> {

    Optional<EventNode> deleteEventTarget(Long id);

    EventNode addOrUpdateEventTarget(EventNode eventNode, TargetDTO targetDTO);
}
