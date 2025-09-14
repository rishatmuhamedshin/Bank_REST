package com.example.bankcards.util.validation.annoatation;

import com.example.bankcards.util.validation.validators.StrongPasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
    String message() default "Пароль не соответствует требованиям сложности";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
