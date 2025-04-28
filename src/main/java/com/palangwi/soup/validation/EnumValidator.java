package com.palangwi.soup.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<EnumValue, String> {

    private Enum<?>[] enumValues;
    private boolean ignoreCase;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.enumValues = constraintAnnotation.enumClass().getEnumConstants();
        this.ignoreCase = constraintAnnotation.ignoreCase();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        for (Enum<?> e : enumValues) {
            if (ignoreCase) {
                if (value.equalsIgnoreCase(e.name())) {
                    return true;
                }
            } else {
                if (value.equals(e.name())) {
                    return true;
                }
            }
        }
        return false;
    }
}