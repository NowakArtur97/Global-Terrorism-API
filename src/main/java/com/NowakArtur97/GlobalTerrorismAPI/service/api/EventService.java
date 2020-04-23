package com.NowakArtur97.GlobalTerrorismAPI.service.api;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;

public interface EventService {

	Page<EventNode> findAll(Pageable pageable);

	EventNode save(EventNode eventNode);

	EventNode saveNew(EventDTO eventDTO);

	Optional<EventNode> findById(Long id);

	Optional<EventNode> delete(Long id);
}
