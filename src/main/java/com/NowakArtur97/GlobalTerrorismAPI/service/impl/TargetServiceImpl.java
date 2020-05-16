package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTONode;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.DTOMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.TargetRepository;
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
public class TargetServiceImpl implements TargetService {

	private final TargetRepository targetRepository;

	private final DTOMapper dtoMapper;

	@Override
	@Transactional(readOnly = true)
	public Optional<TargetNode> findById(Long id) {

		return id != null ? targetRepository.findById(id) : Optional.empty();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<TargetNode> findAll(Pageable pageable) {

		return targetRepository.findAll(pageable);
	}

	@Override
	public TargetNode save(TargetNode targetNode) {

		return targetRepository.save(targetNode);
	}

	@Override
	public TargetNode saveNew(DTONode targetDTO) {

		TargetNode targetNode = dtoMapper.mapToNode(targetDTO, TargetNode.class);

		targetNode = targetRepository.save(targetNode);

		return targetNode;
	}

	@Override
	public TargetNode update(TargetNode targetNode, DTONode targetDTO) {

		Long id = targetNode.getId();

		targetNode = dtoMapper.mapToNode(targetDTO, TargetNode.class);

		targetNode.setId(id);

		targetNode = targetRepository.save(targetNode);

		return targetNode;
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
