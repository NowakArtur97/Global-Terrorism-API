package com.NowakArtur97.GlobalTerrorismAPI.annotation.validation;

import com.NowakArtur97.GlobalTerrorismAPI.validator.CountriesMatchConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = CountriesMatchConstraintValidator.class)
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface CityAndTargetAreInSameCountry {

    String message() default "City and target should be located in the same country.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
