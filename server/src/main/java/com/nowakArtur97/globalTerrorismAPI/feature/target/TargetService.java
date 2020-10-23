package com.nowakArtur97.globalTerrorismAPI.feature.target;

import com.nowakArtur97.globalTerrorismAPI.common.exception.ResourceNotFoundException;
import com.nowakArtur97.globalTerrorismAPI.common.repository.BaseRepository;
import com.nowakArtur97.globalTerrorismAPI.common.service.GenericServiceImpl;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public
class TargetService extends GenericServiceImpl<TargetNode, TargetDTO> {

    private final CountryService countryService;

    TargetService(BaseRepository<TargetNode> repository, ModelMapper modelMapper, CountryService countryService) {
        super(repository, modelMapper);
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

        TargetNode targetNode = modelMapper.map(targetDTO, TargetNode.class);

        targetNode.setCountryOfOrigin(countryService.findByName(targetDTO.getCountryOfOrigin().getName())
                .orElseThrow(() -> new ResourceNotFoundException("CountryModel")));

        return repository.save(targetNode);
    }

    @Override
    public TargetNode update(TargetNode targetNode, TargetDTO targetDTO) {

        Long id = targetNode.getId();

        targetNode = modelMapper.map(targetDTO, TargetNode.class);

        targetNode.setId(id);

        targetNode.setCountryOfOrigin(countryService.findByName(targetDTO.getCountryOfOrigin().getName())
                .orElseThrow(() -> new ResourceNotFoundException("CountryModel")));

        return repository.save(targetNode);
    }

    public boolean isDatabaseEmpty() {

        return repository.count() == 0;
    }
}
