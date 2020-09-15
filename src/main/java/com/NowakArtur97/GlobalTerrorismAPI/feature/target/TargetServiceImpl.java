package com.NowakArtur97.GlobalTerrorismAPI.feature.target;

import com.NowakArtur97.GlobalTerrorismAPI.exception.ResourceNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryService;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.repository.BaseRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.impl.GenericServiceImpl;
import org.springframework.stereotype.Service;

@Service
class TargetServiceImpl extends GenericServiceImpl<TargetNode, TargetDTO> implements TargetService {

    private final CountryService countryService;

    TargetServiceImpl(BaseRepository<TargetNode> repository, ObjectMapper objectMapper, CountryService countryService) {
        super(repository, objectMapper);
        this.countryService = countryService;
    }

    @Override
    public TargetNode save(TargetNode targetNode) {

        targetNode.setCountryOfOrigin(countryService.findByName(targetNode.getCountryOfOrigin().getName())
                .orElseThrow(() -> new ResourceNotFoundException("CountryModel")));

        return repository.save(targetNode);
    }

    @Override
    public TargetNode saveNew(TargetDTO targetDTO) {

        TargetNode targetNode = objectMapper.map(targetDTO, TargetNode.class);

        targetNode.setCountryOfOrigin(countryService.findByName(targetDTO.getCountryOfOrigin().getName())
                .orElseThrow(() -> new ResourceNotFoundException("CountryModel")));

        return repository.save(targetNode);
    }

    @Override
    public TargetNode update(TargetNode targetNode, TargetDTO targetDTO) {

        Long id = targetNode.getId();

        targetNode = objectMapper.map(targetDTO, TargetNode.class);

        targetNode.setId(id);

        targetNode.setCountryOfOrigin(countryService.findByName(targetDTO.getCountryOfOrigin().getName())
                .orElseThrow(() -> new ResourceNotFoundException("CountryModel")));

        return repository.save(targetNode);
    }

    @Override
    public boolean isDatabaseEmpty() {

        return repository.count() == 0;
    }
}
