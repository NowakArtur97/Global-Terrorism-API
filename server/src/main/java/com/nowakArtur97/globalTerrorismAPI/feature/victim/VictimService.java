package com.nowakArtur97.globalTerrorismAPI.feature.victim;

import com.nowakArtur97.globalTerrorismAPI.common.service.GenericServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class VictimService extends GenericServiceImpl<VictimNode, VictimDTO> {

    VictimService(VictimRepository repository, ModelMapper modelMapper) {
        super(repository, modelMapper);
    }
}
