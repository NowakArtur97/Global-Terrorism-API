package com.NowakArtur97.GlobalTerrorismAPI.validator;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.validation.CityAndTargetAreInSameCountry;
import com.NowakArtur97.GlobalTerrorismAPI.dto.CityDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CountriesMatchConstraintValidator implements ConstraintValidator<CityAndTargetAreInSameCountry, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {


        EventDTO event = (EventDTO) obj;

        if (event == null) {
            return false;
        }

        CityDTO city = event.getCity();
        TargetDTO target = event.getTarget();

        if (city == null || target == null || city.getProvince() == null ||
                city.getProvince().getCountry() == null || target.getCountryOfOrigin() == null) {
            return false;
        }

        String provincesCountryName = event.getCity().getProvince().getCountry().getName();
        String targetCountryName = event.getTarget().getCountryOfOrigin().getName();

        return provincesCountryName != null && provincesCountryName.equals(targetCountryName);
    }
}
