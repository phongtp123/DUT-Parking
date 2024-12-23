package com.example.DUT_Parking.DTO;

import com.example.DUT_Parking.configuration.AgeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {AgeValidator.class})
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Age {
    String message() default "Age must be 18 years or older";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int min() default 18;
}
