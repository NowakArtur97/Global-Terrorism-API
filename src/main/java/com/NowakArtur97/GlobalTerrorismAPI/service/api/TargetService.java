package com.NowakArtur97.GlobalTerrorismAPI.service.api;

import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TargetService {

	Optional<TargetNode> findById(Long id);

	Page<TargetNode> findAll(Pageable pageable);

	TargetNode save(TargetNode targetNode);

	TargetNode saveNew(TargetDTO targetDTO);

	TargetNode update(TargetNode targetNode, TargetDTO targetDTO);

	Optional<TargetNode> delete(Long id);

	boolean isDatabaseEmpty();
}
