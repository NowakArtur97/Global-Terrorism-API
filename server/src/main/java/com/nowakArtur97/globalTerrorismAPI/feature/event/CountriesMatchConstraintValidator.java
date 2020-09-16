package com.nowakArtur97.globalTerrorismAPI.feature.event;

import com.nowakArtur97.globalTerrorismAPI.feature.city.CityDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class CountriesMatchConstraintValidator implements ConstraintValidator<ProvinceAndTargetAreInSameCountry, Object> {

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
