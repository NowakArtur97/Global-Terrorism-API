package com.nowakArtur97.globalTerrorismAPI.feature.victim;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class ValidNumberOfPerpetratorInjuredConstraintValidator
        implements ConstraintValidator<ValidNumberOfPerpetratorInjured, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {

        VictimDTO victim = (VictimDTO) obj;

        return victim.getTotalNumberOfInjured() >= victim.getNumberOfPerpetratorInjured();
    }
}
