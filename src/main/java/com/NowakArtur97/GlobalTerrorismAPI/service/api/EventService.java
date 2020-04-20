package com.NowakArtur97.GlobalTerrorismAPI.service.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;

public interface EventService {

	Page<EventNode> findAll(Pageable pageable);

	EventNode save(EventNode eventNode);
}
