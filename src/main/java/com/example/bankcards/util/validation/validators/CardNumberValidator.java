package com.example.bankcards.util.validation.validators;

import com.example.bankcards.util.validation.annoatation.CardNumber;
import com.example.bankcards.util.validation.annoatation.StrongPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CardNumberValidator implements ConstraintValidator<CardNumber, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        boolean lengthOk = value.length() == 16;
        boolean isInt = value.matches("\\d+");
        return lengthOk && isInt;
    }
}
