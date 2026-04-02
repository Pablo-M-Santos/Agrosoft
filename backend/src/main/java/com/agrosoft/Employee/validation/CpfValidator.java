package com.agrosoft.Employee.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<ValidCpf, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String cpf = value.replaceAll("\\D", "");

        if (cpf.isEmpty()) {
            return false;
        }

        if (cpf.length() != 11) {
            return false;
        }

        if (cpf.chars().distinct().count() == 1) {
            return false;
        }

        return isValidCheckDigits(cpf);
    }

    private boolean isValidCheckDigits(String cpf) {
        int firstDigit = calculateDigit(cpf.substring(0, 9), 10);
        int secondDigit = calculateDigit(cpf.substring(0, 9) + firstDigit, 11);

        return cpf.equals(cpf.substring(0, 9) + firstDigit + secondDigit);
    }

    private int calculateDigit(String base, int weight) {
        int sum = 0;

        for (int i = 0; i < base.length(); i++) {
            sum += Character.getNumericValue(base.charAt(i)) * (weight - i);
        }

        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }
}