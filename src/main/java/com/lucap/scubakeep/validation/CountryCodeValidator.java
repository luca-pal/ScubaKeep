package com.lucap.scubakeep.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Locale;
import java.util.Set;

/**
 * Checks that a string is a valid ISO 3166-1 alpha-2 country code (e.g., IT, AT, ES).
 * Uses {@link java.util.Locale#getISOCountries()} as the source of valid codes.
 */
public class CountryCodeValidator implements ConstraintValidator<CountryCode, String> {

    private static final Set<String> ISO_COUNTRIES = Set.of(Locale.getISOCountries());

    /**
     * Returns true if the value is a valid ISO country code.
     * Blank/null is considered valid here (use @NotBlank).
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        // Let @NotBlank handle null/blank checks
        if (value == null || value.isBlank()) {
            return true;
        }

        String code = value.trim().toUpperCase(Locale.ROOT);

        // Must be exactly 2 letters
        if (code.length() != 2) {
            return false;
        }

        // Must be one of the official ISO 3166-1 alpha-2 codes
        return ISO_COUNTRIES.contains(code);
    }
}
