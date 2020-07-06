package com.NowakArtur97.GlobalTerrorismAPI.validator;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.validation.UniqueEmail;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.UserService;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class UniqueEmailConstraintValidator implements ConstraintValidator<UniqueEmail, String> {

    private final UserService userService;

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {

        return userService.findByEmail(email).isEmpty();
    }
}
