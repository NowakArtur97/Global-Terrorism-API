package com.nowakArtur97.globalTerrorismAPI.feature.group;

import com.nowakArtur97.globalTerrorismAPI.feature.event.EventDTO;
import com.nowakArtur97.globalTerrorismAPI.common.service.GenericService;

import java.util.Optional;

public interface GroupService extends GenericService<GroupNode, GroupDTO> {

    Optional<GroupNode> addEventToGroup(Long id, EventDTO eventDTO);

    Optional<GroupNode> deleteAllGroupEvents(Long id);
}
