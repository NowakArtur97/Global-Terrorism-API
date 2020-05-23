package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTONode;
import com.NowakArtur97.GlobalTerrorismAPI.node.Node;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DTOMapperImpl<T extends Node, D extends DTONode> implements DTOMapper<T, D> {

    private final ModelMapper modelMapper;

    @Override
    public T mapToNode(D dto, Class<T> destinationType) {

        return modelMapper.map(dto, destinationType);
    }

    @Override
    public D mapToDTO(T entity, Class<D> destinationType) {

        return modelMapper.map(entity, destinationType);
    }
}
