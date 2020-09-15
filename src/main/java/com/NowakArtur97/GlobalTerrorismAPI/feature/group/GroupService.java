package com.NowakArtur97.GlobalTerrorismAPI.feature.group;

import com.NowakArtur97.GlobalTerrorismAPI.feature.event.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.common.service.GenericService;

import java.util.Optional;

public interface GroupService extends GenericService<GroupNode, GroupDTO> {

    Optional<GroupNode> addEventToGroup(Long id, EventDTO eventDTO);

    Optional<GroupNode> deleteAllGroupEvents(Long id);
}
