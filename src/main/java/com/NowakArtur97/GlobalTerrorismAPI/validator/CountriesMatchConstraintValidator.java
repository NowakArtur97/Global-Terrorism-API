package com.NowakArtur97.GlobalTerrorismAPI.validator;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.validation.ProvinceAndTargetAreInSameCountry;
import com.NowakArtur97.GlobalTerrorismAPI.dto.CityDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CountriesMatchConstraintValidator implements ConstraintValidator<ProvinceAndTargetAreInSameCountry, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {

        EventDTO event = (EventDTO) obj;

        if (event == null) {
            return true;
        }

        CityDTO city = event.getCity();
        TargetDTO target = event.getTarget();

        if (city == null && target == null) {
            return true;
        }

        if (city == null || city.getProvince() == null || city.getProvince().getCountry() == null
                || target == null || target.getCountryOfOrigin() == null) {
            return false;
        }

        String provincesCountryName = city.getProvince().getCountry().getName();
        String targetCountryName = target.getCountryOfOrigin().getName();

        if (provincesCountryName == null && targetCountryName == null) {
            return true;
        }

        return provincesCountryName != null && provincesCountryName.equals(targetCountryName);
    }
}
