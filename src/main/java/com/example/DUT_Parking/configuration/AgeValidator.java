package com.example.DUT_Parking.configuration;

import com.example.DUT_Parking.DTO.Age;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;

public class AgeValidator implements ConstraintValidator<Age, Object> {
    private int minAge;

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        LocalDate dateOfBirth;

        if (value instanceof LocalDate) {
            dateOfBirth = (LocalDate) value;
        } else if (value instanceof Date) {
            dateOfBirth = ((Date) value).toLocalDate();
        } else {
            return false;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears() >= minAge;
    }

    @Override
    public void initialize(Age constraintAnnotation) {
        this.minAge = constraintAnnotation.min();
    }
}
