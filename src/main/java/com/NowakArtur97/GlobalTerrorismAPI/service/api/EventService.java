package com.NowakArtur97.GlobalTerrorismAPI.service.api;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;

public interface EventService {

	Optional<EventNode> findById(Long id);

	Page<EventNode> findAll(Pageable pageable);

	EventNode save(EventNode eventNode);

	EventNode saveNew(EventDTO eventDTO);

	EventNode update(EventNode eventNode, EventDTO eventDTO);

	Optional<EventNode> delete(Long id);
}
