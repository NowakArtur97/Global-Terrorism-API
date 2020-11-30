package com.nowakArtur97.globalTerrorismAPI.feature.victim;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ValidNumberOfPerpetratorsInjuredConstraintValidator.class)
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface ValidNumberOfPerpetratorsInjured {

    String message() default "Event number of perpetrators injured should not exceed the total number of injured.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
