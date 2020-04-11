package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTOEntity;

public interface DTOMapper {

	Object convertToEntity(Object object, DTOEntity dto);
}
