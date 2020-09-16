package com.nowakArtur97.globalTerrorismAPI.feature.event;

import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetDTO;
import com.nowakArtur97.globalTerrorismAPI.common.service.GenericService;

import java.util.Optional;

public interface EventService extends GenericService<EventNode, EventDTO> {

    Optional<EventNode> deleteEventTarget(Long id);

    EventNode addOrUpdateEventTarget(EventNode eventNode, TargetDTO targetDTO);
}
