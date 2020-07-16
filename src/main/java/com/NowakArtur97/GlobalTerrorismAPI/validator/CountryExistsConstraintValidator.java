package com.NowakArtur97.GlobalTerrorismAPI.validator;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.validation.CountryExists;
import com.NowakArtur97.GlobalTerrorismAPI.repository.CountryRepository;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class CountryExistsConstraintValidator implements ConstraintValidator<CountryExists, String> {

    private final CountryRepository countryRepository;

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {

        return countryRepository.existsByName(name);
    }
}
