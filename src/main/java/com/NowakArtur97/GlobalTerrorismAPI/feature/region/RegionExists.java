package com.NowakArtur97.GlobalTerrorismAPI.feature.region;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = RegionExistsConstraintValidator.class)
@Target({FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@interface RegionExists {

    String message() default "A region with the given name does not exist.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
