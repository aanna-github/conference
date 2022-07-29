package com.example.conference.exception;

public class InvalidInputException extends RuntimeException {
    private static final long serialVersionUID = -8299452040511861674L;

    public InvalidInputException(String message) {
        super(message);
    }
}
