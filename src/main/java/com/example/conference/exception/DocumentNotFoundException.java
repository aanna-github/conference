package com.example.conference.exception;

public class DocumentNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -6208298193331745615L;

    public DocumentNotFoundException(String message) {
        super(message);
    }
}