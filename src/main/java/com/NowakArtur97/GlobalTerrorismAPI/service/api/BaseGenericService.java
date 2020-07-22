package com.NowakArtur97.GlobalTerrorismAPI.service.api;

import com.NowakArtur97.GlobalTerrorismAPI.node.Node;

public interface BaseGenericService<T extends Node> {

    T save(T node);
}
