package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.GroupDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.GroupNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.BaseRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GroupServiceImpl extends GenericServiceImpl<GroupNode, GroupDTO> {

    private final GenericService<EventNode, EventDTO> eventService;

    @Autowired
    public GroupServiceImpl(BaseRepository<GroupNode> repository, ObjectMapper dtoMapper, GenericService<EventNode, EventDTO> eventService) {
        super(repository, dtoMapper);

        this.eventService = eventService;
    }

//    @Override
//    public GroupNode update(GroupNode groupNode, GroupDTO groupDTO) {
//
//        Long id = groupNode.getId();
//
//        for (int index = 0; index < groupDTO.getEventsCaused().size(); index++) {
//
//            EventDTO eventDTO = groupDTO.getEventsCaused().get(index);
//            EventNode eventNode = groupNode.getEventsCaused().stream().filter(node -> eventDTO.getId());
//
//            eventService.update(eventNode, eventDTO);
//        }
//
//        groupNode = dtoMapper.map(groupDTO, GroupNode.class);
//
//        groupNode.setId(id);
//
//        return repository.save(groupNode);
//    }

    @Override
    public Optional<GroupNode> delete(Long id) {

        Optional<GroupNode> groupNodeOptional = findById(id);

        if (groupNodeOptional.isPresent()) {

            GroupNode groupNode = groupNodeOptional.get();

            groupNode.getEventsCaused().forEach(event -> eventService.delete(event.getId()));

            repository.delete(groupNode);
        }

        return groupNodeOptional;
    }
}
