package com.NowakArtur97.GlobalTerrorismAPI.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.NowakArtur97.GlobalTerrorismAPI.node.Target;
import com.NowakArtur97.GlobalTerrorismAPI.repository.TargetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TargetServiceImpl implements TargetService {

	private final TargetRepository targetRepository;
	
	@Override
	public Iterable<Target> findAll() {
		
		return targetRepository.findAll();
	}
}
