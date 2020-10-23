package com.nowakArtur97.globalTerrorismAPI.feature.victim;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class ValidNumberOfPerpetratorInjuredConstraintValidator
        implements ConstraintValidator<ValidNumberOfPerpetratorInjured, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {

        VictimDTO victim = (VictimDTO) obj;

        // Other annotations are responsible for this validation, therefore the true was returned
        if (victim == null
                || victim.getTotalNumberOfInjured() == null
                || victim.getTotalNumberOfInjured() < 0
                || victim.getNumberOfPerpetratorInjured() == null
                || victim.getNumberOfPerpetratorInjured() < 0) {
            return true;
        }

        return victim.getTotalNumberOfInjured() >= victim.getNumberOfPerpetratorInjured();
    }
}
