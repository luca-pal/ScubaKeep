package com.lucap.scubakeep.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CountryCodeValidatorTest {

    private CountryCodeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CountryCodeValidator();
    }

    /**
     * Tests that null or blank values return true, letting @NotBlank handle them.
     */
    @Test
    void isValid_ShouldReturnTrue_WhenValueIsBlankOrNull() {
        assertTrue(validator.isValid(null, null));
        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
    }

    /**
     * Tests valid ISO country codes in various formats.
     */
    @ParameterizedTest
    @ValueSource(strings = {"IT", "at", " us ", "ES"})
    void isValid_ShouldReturnTrue_WhenValueIsValidISO(String code) {
        assertTrue(validator.isValid(code, null));
    }

    /**
     * Tests invalid codes (wrong length or not in ISO set).
     */
    @ParameterizedTest
    @ValueSource(strings = {"ITA", "A", "ZZ", "12"})
    void isValid_ShouldReturnFalse_WhenValueIsInvalid(String code) {
        assertFalse(validator.isValid(code, null));
    }
}
