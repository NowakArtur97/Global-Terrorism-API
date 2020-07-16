package com.NowakArtur97.GlobalTerrorismAPI.validator;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.validation.UniqueUserName;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.UserService;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class UniqueUserNameConstraintValidator implements ConstraintValidator<UniqueUserName, String> {

    private final UserService userService;

    @Override
    public boolean isValid(String userName, ConstraintValidatorContext context) {

        return userService.findByUserName(userName).isEmpty();
    }
}
