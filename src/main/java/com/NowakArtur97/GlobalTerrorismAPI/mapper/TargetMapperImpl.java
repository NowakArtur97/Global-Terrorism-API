package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TargetMapperImpl implements TargetMapper {

	private final ModelMapper modelMapper;

	@Override
	public TargetNode mapDTOToNode(TargetDTO targetDTO) {

		TargetNode targetNode = modelMapper.map(targetDTO, TargetNode.class);

		return targetNode;
	}
}
