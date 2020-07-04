package com.NowakArtur97.GlobalTerrorismAPI.validator;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.PasswordsMatch;
import com.NowakArtur97.GlobalTerrorismAPI.dto.UserDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordsMatchConstraintValidator implements ConstraintValidator<PasswordsMatch, Object> {

    @Override
    public void initialize(PasswordsMatch constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {

        UserDTO user = (UserDTO) obj;

        return user != null
                && user.getPassword() != null
                && user.getMatchingPassword() != null
                && user.getPassword().equals(user.getMatchingPassword());
    }
}
