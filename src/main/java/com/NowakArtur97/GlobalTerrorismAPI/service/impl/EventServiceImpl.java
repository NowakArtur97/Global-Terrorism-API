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

    EventServiceImpl(BaseRepository<EventNode> repository, ObjectMapper objectMapper, GenericService<TargetNode, TargetDTO> targetService) {
        super(repository, objectMapper);
        this.targetService = targetService;
    }

    @Override
    public EventNode save(EventNode eventNode) {

        eventNode.setTarget(targetService.save(eventNode.getTarget()));

        return repository.save(eventNode);
    }

    @Override
    public EventNode saveNew(EventDTO eventDTO) {

        EventNode eventNode = objectMapper.map(eventDTO, EventNode.class);

        eventNode.setTarget(targetService.saveNew(eventDTO.getTarget()));

        return repository.save(eventNode);
    }

    @Override
    public EventNode update(EventNode eventNode, EventDTO eventDTO) {

        Long id = eventNode.getId();

        TargetNode updatedTarget = targetService.update(eventNode.getTarget(), eventDTO.getTarget());

        eventNode = objectMapper.map(eventDTO, EventNode.class);

        eventNode.setId(id);
        eventNode.setTarget(updatedTarget);

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

    @Override
    public EventNode addOrUpdateEventTarget(EventNode eventNode, TargetDTO targetDTO) {

        TargetNode targetNode;

        if (eventNode.getTarget() != null) {

            targetNode = targetService.update(eventNode.getTarget(), targetDTO);

        } else {

            targetNode = targetService.saveNew(targetDTO);
        }

        eventNode.setTarget(targetNode);

        return repository.save(eventNode);
    }
}
