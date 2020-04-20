package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.EventRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.EventService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventServiceImpl implements EventService {

	private final EventRepository eventRepository;

	@Override
	@Transactional(readOnly = true)
	public Page<EventNode> findAll(Pageable pageable) {

		return eventRepository.findAll(pageable);
	}

	@Override
	public EventNode save(EventNode eventNode) {

		return eventRepository.save(eventNode);
	}

}
