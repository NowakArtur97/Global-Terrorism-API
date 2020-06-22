package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.GroupDTO;
import com.NowakArtur97.GlobalTerrorismAPI.exception.ResourceNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.BaseRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GroupService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
class GroupServiceImpl extends GenericServiceImpl<GroupNode, GroupDTO> implements GroupService {

    private final GenericService<EventNode, EventDTO> eventService;

    GroupServiceImpl(BaseRepository<GroupNode> repository, ObjectMapper dtoMapper, GenericService<EventNode, EventDTO> eventService) {
        super(repository, dtoMapper);
        this.eventService = eventService;
    }

    @Override
    public GroupNode update(GroupNode groupNode, GroupDTO groupDTO) {

        Long id = groupNode.getId();

        deleteEvents(groupNode);

        saveNewEvents(groupDTO);

        groupNode = objectMapper.map(groupDTO, GroupNode.class);

        groupNode.setId(id);

        return repository.save(groupNode);
    }

    private void saveNewEvents(GroupDTO groupDTO) {

        groupDTO.getEventsCaused().forEach(eventService::saveNew);
    }

    private void deleteEvents(GroupNode groupNode) {

        groupNode.getEventsCaused().forEach(event -> eventService.delete(event.getId()));
    }

    @Override
    public Optional<GroupNode> delete(Long id) {

        Optional<GroupNode> groupNodeOptional = findById(id);

        if (groupNodeOptional.isPresent()) {

            GroupNode groupNode = groupNodeOptional.get();

            deleteEvents(groupNode);

            repository.delete(groupNode);
        }

        return groupNodeOptional;
    }

    @Override
    public List<EventNode> findAllEventsCausedByGroup(Long id) {

        return findById(id).orElseThrow(() -> new ResourceNotFoundException("GroupModel", id)).getEventsCaused();
    }

    @Override
    public GroupNode addEventToGroup(Long id, EventDTO eventDTO) {

        GroupNode groupNode = findById(id).orElseThrow(() -> new ResourceNotFoundException("GroupModel", id));

        EventNode eventNode = eventService.saveNew(eventDTO);

        groupNode.addEvent(eventNode);

        return repository.save(groupNode);
    }
}
