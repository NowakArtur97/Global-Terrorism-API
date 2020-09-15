package com.NowakArtur97.GlobalTerrorismAPI.feature.region;

import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
class RegionExistsConstraintValidator implements ConstraintValidator<RegionExists, String> {

    private final RegionService regionService;

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {

        return regionService.existsByName(name);
    }
}
