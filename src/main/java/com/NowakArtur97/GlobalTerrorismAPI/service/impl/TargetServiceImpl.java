package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.BaseRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;
import org.springframework.stereotype.Service;

@Service
class TargetServiceImpl extends GenericServiceImpl<TargetNode, TargetDTO> implements TargetService {

    TargetServiceImpl(BaseRepository<TargetNode> repository, ObjectMapper objectMapper) {
        super(repository, objectMapper);
    }

    @Override
    public boolean isDatabaseEmpty() {

        return repository.count() == 0;
    }
}
