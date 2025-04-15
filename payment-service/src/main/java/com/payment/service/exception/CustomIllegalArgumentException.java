package com.payment.service.exception;

import java.util.List;

public class CustomIllegalArgumentException extends IllegalArgumentException {
    private final List<String> errors;

    public CustomIllegalArgumentException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}