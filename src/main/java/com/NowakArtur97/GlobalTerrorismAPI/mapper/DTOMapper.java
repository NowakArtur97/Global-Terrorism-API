package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTOEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DTOMapper {

	private final ModelMapper modelMapper;

	public Object convertToEntity(Object object, DTOEntity dto) {

		return modelMapper.map(object, dto.getClass());
	}
}
