package com.nowakArtur97.globalTerrorismAPI.feature.victim;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class ValidNumberOfPerpetratorFatalitiesConstraintValidator
        implements ConstraintValidator<ValidNumberOfPerpetratorFatalities, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {

        VictimDTO victim = (VictimDTO) obj;

        return victim.getTotalNumberOfFatalities() >= victim.getNumberOfPerpetratorFatalities();
    }
}
