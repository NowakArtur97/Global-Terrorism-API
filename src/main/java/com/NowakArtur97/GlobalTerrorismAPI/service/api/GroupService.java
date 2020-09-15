package com.NowakArtur97.GlobalTerrorismAPI.service.api;

import com.NowakArtur97.GlobalTerrorismAPI.dto.GroupDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.event.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;

import java.util.Optional;

public interface GroupService extends GenericService<GroupNode, GroupDTO> {

    Optional<GroupNode> addEventToGroup(Long id, EventDTO eventDTO);

    Optional<GroupNode> deleteAllGroupEvents(Long id);
}
