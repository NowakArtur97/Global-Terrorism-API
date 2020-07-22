package com.NowakArtur97.GlobalTerrorismAPI.validator;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.validation.CountryExists;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.CountryService;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class CountryExistsConstraintValidator implements ConstraintValidator<CountryExists, String> {

    private final CountryService countryService;

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {

        return countryService.existsByName(name);
    }
}
