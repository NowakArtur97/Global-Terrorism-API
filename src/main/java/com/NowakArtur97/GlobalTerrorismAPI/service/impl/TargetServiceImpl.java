package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTONode;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.DTOMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TargetServiceImpl extends GenericServiceImpl<TargetNode> {

    @Autowired
    public TargetServiceImpl(BaseRepository<TargetNode> repository, DTOMapper dtoMapper) {
        super(repository, dtoMapper);
    }

    @Override
    public TargetNode update(TargetNode targetNode, DTONode dto) {

        Long id = targetNode.getId();

        TargetDTO targetDTO = (TargetDTO) dto;

        targetNode = dtoMapper.mapToNode(targetDTO, TargetNode.class);

        targetNode.setId(id);

        targetNode = repository.save(targetNode);

        return targetNode;
    }

    //    @Override
    public boolean isDatabaseEmpty() {

        return repository.count() == 0;
    }
}
