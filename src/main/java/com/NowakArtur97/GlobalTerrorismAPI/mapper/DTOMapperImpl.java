package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTOEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DTOMapperImpl implements DTOMapper {

	private final ModelMapper modelMapper;

	@Override
	public <T> T convertToEntity(DTOEntity dto, Class<T> destinationType) {

		return modelMapper.map(dto, destinationType);
	}

	@Override
	public <T> T convertToDTO(Object entity, Class<T> destinationType) {

		return modelMapper.map(entity, destinationType);
	}
}
