package com.NowakArtur97.GlobalTerrorismAPI.feature.user;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class PasswordsMatchConstraintValidator implements ConstraintValidator<PasswordsMatch, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {

        UserDTO user = (UserDTO) obj;

        return user.getPassword().equals(user.getMatchingPassword());
    }
}
