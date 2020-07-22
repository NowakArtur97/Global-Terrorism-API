package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.exception.ResourceNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.BaseRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.CountryService;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;
import org.springframework.stereotype.Service;

@Service
class TargetServiceImpl extends GenericServiceImpl<TargetNode, TargetDTO> implements TargetService {

    private final CountryService countryService;

    TargetServiceImpl(BaseRepository<TargetNode> repository, ObjectMapper objectMapper, CountryService countryService) {
        super(repository, objectMapper);
        this.countryService = countryService;
    }

    @Override
    public TargetNode saveNew(TargetDTO targetDTO) {

        TargetNode targetNode = objectMapper.map(targetDTO, TargetNode.class);

        targetNode.setCountryOfOrigin(countryService.findByName(targetDTO.getCountryOfOrigin().getName())
                .orElseThrow(() -> new ResourceNotFoundException("CountryModel")));

        return repository.save(targetNode);
    }

    @Override
    public boolean isDatabaseEmpty() {

        return repository.count() == 0;
    }
}
