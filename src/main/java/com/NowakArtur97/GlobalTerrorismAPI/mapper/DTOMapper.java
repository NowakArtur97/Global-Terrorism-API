package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTOEntity;

public interface DTOMapper {

	<T> T convertToEntity(DTOEntity dto, Class<T> destinationType);

	<T> T convertToDTO(Object entity, Class<T> destinationType);
}