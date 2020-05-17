package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.DTOMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.EventRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.EventService;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final DTOMapper dtoMapper;

    private final TargetService targetService;

    @Override
    @Transactional(readOnly = true)
    public Page<EventNode> findAll(Pageable pageable) {

        return eventRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EventNode> findById(Long id) {

        return id != null ? eventRepository.findById(id) : Optional.empty();
    }

    @Override
    public EventNode save(EventNode eventNode) {

        return eventRepository.save(eventNode);
    }

    @Override
    public EventNode saveNew(EventDTO eventDTO) {

        EventNode eventNode = dtoMapper.mapToNode(eventDTO, EventNode.class);

        eventNode = eventRepository.save(eventNode);

        return eventNode;
    }

    @Override
    public EventNode update(EventNode eventNode, EventDTO eventDTO) {

        Long id = eventNode.getId();

        TargetNode targetNode = targetService.update(eventNode.getTarget(), eventDTO.getTarget());

        eventNode = dtoMapper.mapToNode(eventDTO, EventNode.class);

        eventNode.setId(id);

        eventNode.setTarget(targetNode);

        eventNode = eventRepository.save(eventNode);

        return eventNode;
    }

    @Override
    public Optional<EventNode> delete(Long id) {

        Optional<EventNode> eventNodeOptional = findById(id);

        if (eventNodeOptional.isPresent()) {

            EventNode eventNode = eventNodeOptional.get();

            targetService.delete(eventNode.getTarget().getId());

            eventRepository.delete(eventNode);
        }

        return eventNodeOptional;
    }
}
