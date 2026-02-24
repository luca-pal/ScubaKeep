package com.lucap.scubakeep.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a string is a valid ISO 3166-1 alpha-2 country code (ex: IT, AT, ES).
 *
 * <p>This constraint is intended for API input validation (DTO fields).
 * When used together with Spring's {@code @Valid}, invalid values will produce
 * a 400 Bad Request with field-level validation errors.</p>
 */
@Constraint(validatedBy = CountryCodeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CountryCode {

    String message() default "Country code must be a valid ISO 3166-1 alpha-2 code "
            + "(ex: IT, AT, ES)";

    // Required to create a new annotation type, will not use them
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
