package com.nowakArtur97.globalTerrorismAPI.feature.user.registerUser.validation;

import com.nowakArtur97.globalTerrorismAPI.feature.user.registerUser.UserService;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
class UniqueUserNameConstraintValidator implements ConstraintValidator<UniqueUserName, String> {

    private final UserService userService;

    @Override
    public boolean isValid(String userName, ConstraintValidatorContext context) {

        return userService.findByUserName(userName).isEmpty();
    }
}
