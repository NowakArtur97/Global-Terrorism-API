package com.NowakArtur97.GlobalTerrorismAPI.util;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.NowakArtur97.GlobalTerrorismAPI.mapper.DTOMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ViolationHelperImpl implements ViolationHelper {

	private final Validator validator;

	private final DTOMapper dtoMapper;

	@Override
	public <T> void violate(Object entity, Class<T> dtoType) {

		T dto = dtoMapper.mapToDTO(entity, dtoType);

		Set<ConstraintViolation<T>> violations = validator.validate(dto);

		if (!violations.isEmpty()) {

			throw new ConstraintViolationException(violations);
		}
	}
}
