package com.NowakArtur97.GlobalTerrorismAPI.common.util;

import com.NowakArtur97.GlobalTerrorismAPI.common.baseModel.DTO;
import com.NowakArtur97.GlobalTerrorismAPI.common.baseModel.Node;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ViolationUtil<T extends Node, D extends DTO> {

    private final Validator validator;

    private final ModelMapper modelMapper;

    public void violate(T entity, Class<D> dtoType) {

        D dto = modelMapper.map(entity, dtoType);

        Set<ConstraintViolation<D>> violations = validator.validate(dto);

        if (!violations.isEmpty()) {

            throw new ConstraintViolationException(violations);
        }
    }
}
