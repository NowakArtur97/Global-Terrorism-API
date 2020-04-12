package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTONode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DTOMapperImpl implements DTOMapper {

	private final ModelMapper modelMapper;

	@Override
	public <T> T mapToNode(DTONode dto, Class<T> destinationType) {

		return modelMapper.map(dto, destinationType);
	}

	@Override
	public <T> T mapToDTO(Object entity, Class<T> destinationType) {

		return modelMapper.map(entity, destinationType);
	}
}
