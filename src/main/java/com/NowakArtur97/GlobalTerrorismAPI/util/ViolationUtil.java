package com.NowakArtur97.GlobalTerrorismAPI.util;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTONode;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.Node;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ViolationUtil<T extends Node, D extends DTONode> {

    private final Validator validator;

    private final ObjectMapper objectMapper;

    public void violate(T entity, Class<D> dtoType) {

        D dto = objectMapper.map(entity, dtoType);

        Set<ConstraintViolation<D>> violations = validator.validate(dto);

        if (!violations.isEmpty()) {

            throw new ConstraintViolationException(violations);
        }
    }
}
