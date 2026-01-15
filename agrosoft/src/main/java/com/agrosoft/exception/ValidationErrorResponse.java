package com.agrosoft.exception;

import java.util.Map;

public class ValidationErrorResponse extends ApiErrorResponse {

    private Map<String, String> fieldErrors;

    public ValidationErrorResponse(
            int status,
            String error,
            String message,
            String path,
            Map<String, String> fieldErrors
    ) {
        super(status, error, message, path);
        this.fieldErrors = fieldErrors;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
