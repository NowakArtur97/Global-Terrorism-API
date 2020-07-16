package com.NowakArtur97.GlobalTerrorismAPI.validator;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.validation.PasswordsMatch;
import com.NowakArtur97.GlobalTerrorismAPI.dto.UserDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordsMatchConstraintValidator implements ConstraintValidator<PasswordsMatch, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {

        UserDTO user = (UserDTO) obj;

        return user.getPassword().equals(user.getMatchingPassword());
    }
}
