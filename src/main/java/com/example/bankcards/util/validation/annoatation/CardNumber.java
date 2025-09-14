package com.example.bankcards.util.validation.annoatation;

import com.example.bankcards.util.validation.validators.CardNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CardNumberValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CardNumber {
    String message() default "Номер карты не соответствует требованиям";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
