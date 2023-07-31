package com.khattab.islandcampsitereservation.validation.annotation;

import com.khattab.islandcampsitereservation.validation.CustomDatesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CustomDatesValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StartDateAndEndDateValidation {
    String message() default "StartDate should be at least 1 day ahead of arrival and up to 1 month in advance, startDate is before endDate, and the day range is max 3 days apart";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}