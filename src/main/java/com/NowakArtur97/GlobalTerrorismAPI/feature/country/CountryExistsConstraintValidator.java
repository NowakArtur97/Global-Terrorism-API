package com.NowakArtur97.GlobalTerrorismAPI.feature.country;

import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
class CountryExistsConstraintValidator implements ConstraintValidator<CountryExists, String> {

    private final CountryService countryService;

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {

        return countryService.existsByName(name);
    }
}
