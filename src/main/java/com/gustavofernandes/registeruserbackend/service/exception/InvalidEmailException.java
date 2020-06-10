package com.gustavofernandes.registeruserbackend.service.exception;

public class InvalidEmailException extends Exception {
    public InvalidEmailException(String message) {
        super(message);
    }
}
