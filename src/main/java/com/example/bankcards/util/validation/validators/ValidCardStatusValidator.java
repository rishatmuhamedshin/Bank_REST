package com.example.bankcards.util.validation.validators;

import com.example.bankcards.entity.enumeration.CardStatus;
import com.example.bankcards.util.validation.annoatation.ValidCardStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class ValidCardStatusValidator implements ConstraintValidator<ValidCardStatus, CardStatus> {

    @Override
    public boolean isValid(CardStatus value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return Arrays.asList(CardStatus.values()).contains(value);
    }
}