package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.DTOMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.TargetRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TargetServiceImpl implements TargetService {

	private final TargetRepository targetRepository;

	private final DTOMapper dtoMapper;

	@Override
	@Transactional(readOnly = true)
	public Page<TargetNode> findAll(Pageable pageable) {

		return targetRepository.findAll(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<TargetNode> findById(Long id) {

		return id != null ? targetRepository.findById(id) : Optional.empty();
	}

	@Override
	public TargetNode saveOrUpdate(Long id, TargetDTO targetDTO) {

		TargetNode targetNode = dtoMapper.mapToNode(targetDTO, TargetNode.class);

		targetNode.setId(id != null ? id : null);

		targetNode = targetRepository.save(targetNode);

		return targetNode;
	}

	@Override
	public TargetNode persistUpdate(TargetNode targetNode) {

		return targetRepository.save(targetNode);
	}

	@Override
	public Optional<TargetNode> delete(Long id) {

		Optional<TargetNode> targetNodeOptional = findById(id);

		if (targetNodeOptional.isPresent()) {

			targetRepository.delete(targetNodeOptional.get());
		}

		return targetNodeOptional;
	}

	@Override
	public boolean isDatabaseEmpty() {

		return targetRepository.count() == 0;
	}
}
