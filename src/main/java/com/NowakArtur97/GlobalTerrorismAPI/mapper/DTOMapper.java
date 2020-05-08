package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTONode;

public interface DTOMapper {

	<T> T mapToNode(DTONode dto, Class<T> destinationType);

	<T> T mapToDTO(Object node, Class<T> destinationType);
}