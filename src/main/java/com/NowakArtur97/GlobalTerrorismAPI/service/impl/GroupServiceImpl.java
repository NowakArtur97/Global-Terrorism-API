package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.GroupDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.BaseRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GroupService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
class GroupServiceImpl extends GenericServiceImpl<GroupNode, GroupDTO> implements GroupService {

    private final GenericService<EventNode, EventDTO> eventService;

    GroupServiceImpl(BaseRepository<GroupNode> repository, ObjectMapper objectMapper, GenericService<EventNode, EventDTO> eventService) {
        super(repository, objectMapper);
        this.eventService = eventService;
    }

    @Override
    public GroupNode save(GroupNode groupNode) {

        List<EventNode> eventsCaused = groupNode.getEventsCaused().stream()
                .map(eventService::save)
                .collect(Collectors.toList());

        groupNode.setEventsCaused(eventsCaused);

        return repository.save(groupNode);
    }

    @Override
    public GroupNode saveNew(GroupDTO groupDTO) {

        GroupNode groupNode = objectMapper.map(groupDTO, GroupNode.class);

        List<EventNode> eventsCaused = groupDTO.getEventsCaused().stream()
                .map(eventService::saveNew)
                .collect(Collectors.toList());

        groupNode.setEventsCaused(eventsCaused);

        return repository.save(groupNode);
    }

    @Override
    public GroupNode update(GroupNode groupNode, GroupDTO groupDTO) {

        Long id = groupNode.getId();

        deleteEvents(groupNode);

        List<EventNode> eventsCaused = saveNewEvents(groupDTO);

        groupNode = objectMapper.map(groupDTO, GroupNode.class);

        groupNode.setId(id);
        groupNode.setEventsCaused(eventsCaused);

        return repository.save(groupNode);
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
    public Optional<GroupNode> addEventToGroup(Long id, EventDTO eventDTO) {

        Optional<GroupNode> groupNodeOptional = findById(id);

        if (groupNodeOptional.isPresent()) {

            GroupNode groupNode = groupNodeOptional.get();

            EventNode eventNode = eventService.saveNew(eventDTO);

            groupNode.addEvent(eventNode);

            repository.save(groupNode);
        }

        return groupNodeOptional;
    }

    @Override
    public Optional<GroupNode> deleteAllGroupEvents(Long id) {

        Optional<GroupNode> groupNodeOptional = findById(id);

        groupNodeOptional.ifPresent(this::deleteEvents);

        return groupNodeOptional;
    }

    private List<EventNode> saveNewEvents(GroupDTO groupDTO) {

        return groupDTO.getEventsCaused().stream()
                .map(eventService::saveNew)
                .collect(Collectors.toList());
    }

    private void deleteEvents(GroupNode groupNode) {

        groupNode.getEventsCaused().forEach(event -> eventService.delete(event.getId()));
    }
}
