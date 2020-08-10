package com.NowakArtur97.GlobalTerrorismAPI.validator;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.validation.RegionExists;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.CountryService;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class RegionExistsConstraintValidator implements ConstraintValidator<RegionExists, String> {

    private final RegionService regionService;

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {

        return regionService.existsByName(name);
    }
}
