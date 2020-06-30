package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.exception.ResourceNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.BaseRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.EventService;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class EventServiceImpl extends GenericServiceImpl<EventNode, EventDTO> implements EventService {

    private final GenericService<TargetNode, TargetDTO> targetService;

    EventServiceImpl(BaseRepository<EventNode> repository, ObjectMapper dtoMapper, GenericService<TargetNode, TargetDTO> targetService) {
        super(repository, dtoMapper);
        this.targetService = targetService;
    }

    @Override
    public EventNode update(EventNode eventNode, EventDTO eventDTO) {

        Long id = eventNode.getId();

        targetService.update(eventNode.getTarget(), eventDTO.getTarget());

        eventNode = objectMapper.map(eventDTO, EventNode.class);

        eventNode.setId(id);

        return repository.save(eventNode);
    }

    @Override
    public Optional<EventNode> delete(Long id) {

        Optional<EventNode> eventNodeOptional = findById(id);

        if (eventNodeOptional.isPresent()) {

            EventNode eventNode = eventNodeOptional.get();

            if (eventNode.getTarget() != null) {
                targetService.delete(eventNode.getTarget().getId());
            }

            repository.delete(eventNode);
        }

        return eventNodeOptional;
    }

    @Override
    public Optional<EventNode> deleteEventTarget(Long id) {

        Optional<EventNode> eventNodeOptional = findById(id);

        if (eventNodeOptional.isPresent()) {
            if (eventNodeOptional.get().getTarget() != null) {
                targetService.delete(eventNodeOptional.get().getTarget().getId());
            } else {
                throw new ResourceNotFoundException("TargetModel");
            }
        }

        return eventNodeOptional;
    }
}
