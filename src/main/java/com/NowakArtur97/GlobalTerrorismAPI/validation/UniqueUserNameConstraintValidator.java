package com.NowakArtur97.GlobalTerrorismAPI.validation;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.UniqueUserName;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class UniqueUserNameConstraintValidator implements ConstraintValidator<UniqueUserName, String> {

    private final UserService userService;

    @Override
    public void initialize(UniqueUserName constraintAnnotation) {
    }

    @Override
    public boolean isValid(String userName, ConstraintValidatorContext context) {

        return userService.findByUserName(userName).isEmpty();
    }
}
