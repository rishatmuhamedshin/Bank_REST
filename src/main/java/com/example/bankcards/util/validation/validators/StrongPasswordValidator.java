package com.example.bankcards.util.validation.validators;

import com.example.bankcards.util.validation.annoatation.StrongPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        boolean lengthOk = value.length() >= 8;
        boolean hasDigit = value.chars().anyMatch(Character::isDigit);
        boolean hasLetter = value.chars().anyMatch(Character::isLetter);
        return lengthOk && hasDigit && hasLetter;
    }
}
