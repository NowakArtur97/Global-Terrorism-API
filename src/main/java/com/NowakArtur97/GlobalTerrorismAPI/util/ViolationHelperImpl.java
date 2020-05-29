package com.NowakArtur97.GlobalTerrorismAPI.util;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTONode;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.Node;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ViolationHelperImpl<T extends Node, D extends DTONode> implements ViolationHelper<T, D> {

    private final Validator validator;

    private final ObjectMapper objectMapper;

    @Override
    public void violate(T entity, Class<D> dtoType) {

        D dto = objectMapper.map(entity, dtoType);

        Set<ConstraintViolation<D>> violations = validator.validate(dto);

        if (!violations.isEmpty()) {

            throw new ConstraintViolationException(violations);
        }
    }
}
