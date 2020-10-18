package com.nowakArtur97.globalTerrorismAPI.feature.victim;

import com.nowakArtur97.globalTerrorismAPI.common.service.BasicGenericServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class VictimService extends BasicGenericServiceImpl<VictimNode> {

    VictimService(VictimRepository repository) {
        super(repository);
    }
}
