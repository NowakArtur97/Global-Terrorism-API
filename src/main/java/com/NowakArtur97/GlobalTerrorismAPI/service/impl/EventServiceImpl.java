package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.DTOMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.BaseRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EventServiceImpl extends GenericServiceImpl<EventNode, EventDTO> {

    private final GenericService<TargetNode, TargetDTO> targetService;

    @Autowired
    public EventServiceImpl(BaseRepository<EventNode> repository, DTOMapper<EventNode, EventDTO> dtoMapper, GenericService<TargetNode, TargetDTO> targetService) {

        super(repository, dtoMapper);

        this.targetService = targetService;
    }

    @Override
    public EventNode update(EventNode eventNode, EventDTO eventDTO) {

        Long id = eventNode.getId();

        TargetNode targetNode = targetService.update(eventNode.getTarget(), eventDTO.getTarget());

        eventNode = dtoMapper.mapToNode(eventDTO, EventNode.class);

        eventNode.setId(id);

        eventNode.setTarget(targetNode);

        eventNode = repository.save(eventNode);

        return eventNode;
    }

    @Override
    public Optional<EventNode> delete(Long id) {

        Optional<EventNode> eventNodeOptional = findById(id);

        if (eventNodeOptional.isPresent()) {

            EventNode eventNode = eventNodeOptional.get();

            targetService.delete(eventNode.getTarget().getId());

            repository.delete(eventNode);
        }

        return eventNodeOptional;
    }
}
