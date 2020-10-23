package com.nowakArtur97.globalTerrorismAPI.feature.victim;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class ValidNumberOfPerpetratorFatalitiesConstraintValidator
        implements ConstraintValidator<ValidNumberOfPerpetratorFatalities, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {

        VictimDTO victim = (VictimDTO) obj;

        // Other annotations are responsible for this validation, therefore the true was returned
        if (victim == null
                || victim.getTotalNumberOfFatalities() == null
                || victim.getTotalNumberOfFatalities() < 0
                || victim.getNumberOfPerpetratorFatalities() == null
                || victim.getNumberOfPerpetratorFatalities() < 0) {
            return true;
        }

        return victim.getTotalNumberOfFatalities() >= victim.getNumberOfPerpetratorFatalities();
    }
}
