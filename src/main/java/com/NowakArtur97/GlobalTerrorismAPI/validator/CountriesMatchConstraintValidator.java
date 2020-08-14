package com.NowakArtur97.GlobalTerrorismAPI.validator;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.validation.CityAndTargetAreInSameCountry;
import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CountriesMatchConstraintValidator implements ConstraintValidator<CityAndTargetAreInSameCountry, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {

        EventDTO event = (EventDTO) obj;

        String provincesCountryName = event.getCity().getProvince().getCountry().getName();
        String targetCountryName = event.getTarget().getCountryOfOrigin().getName();

        return provincesCountryName.equals(targetCountryName);
    }
}
