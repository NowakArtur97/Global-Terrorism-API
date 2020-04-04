package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.TargetMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.TargetRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TargetServiceImpl implements TargetService {

	private final TargetRepository targetRepository;

	private final TargetMapper targetMapper;;

	@Override
	@Transactional(readOnly = true)
	public Page<TargetNode> findAll(Pageable pageable) {

		return targetRepository.findAll(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<TargetNode> findById(Long id) {

		return targetRepository.findById(id);
	}

	@Override
	@Transactional
	public TargetNode save(TargetDTO targetDTO) {

		TargetNode targetNode = targetMapper.mapDTOToNode(targetDTO);

		targetNode = targetRepository.save(targetNode);

		return targetNode;
	}
}
