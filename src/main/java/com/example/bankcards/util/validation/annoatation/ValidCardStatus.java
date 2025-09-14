package com.example.bankcards.util.validation.annoatation;

import com.example.bankcards.util.validation.validators.ValidCardStatusValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidCardStatusValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCardStatus {
    String message() default "Статус карты должен быть одним из 3 значений (ACTIVE, BLOCKED, EXPIRED)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
