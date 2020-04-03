package com.NowakArtur97.GlobalTerrorismAPI.service.api;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;

public interface TargetService {

	Page<TargetNode> findAll(Pageable pageable);

	Optional<TargetNode> findById(Long id);

	Optional<TargetNode> save(TargetModel targetModel);
}
