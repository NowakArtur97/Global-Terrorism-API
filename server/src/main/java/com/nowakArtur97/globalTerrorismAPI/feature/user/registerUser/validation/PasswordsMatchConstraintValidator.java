package com.nowakArtur97.globalTerrorismAPI.feature.user.registerUser.validation;

import com.nowakArtur97.globalTerrorismAPI.feature.user.registerUser.UserDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class PasswordsMatchConstraintValidator implements ConstraintValidator<PasswordsMatch, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {

        UserDTO user = (UserDTO) obj;

        return user.getPassword().equals(user.getMatchingPassword());
    }
}
