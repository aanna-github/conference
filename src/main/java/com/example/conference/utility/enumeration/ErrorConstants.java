package com.example.conference.utility.enumeration;

public enum ErrorConstants {
    INVALID_REQUEST_ERROR_TITLE("The request could not be understood by the server due to malformed syntax."),

    INVALID_INPUT_ERROR_TITLE("The request contains invalid data to perform the operation."),

    INTERNAL_SERVER_ERROR_TITLE("The server encountered an unexpected condition which prevented it from fulfilling the request."),

    NOT_FOUND_ERROR_TITLE("The server cannot find the requested resource."),

    ROOM_BUSY_ERROR_TITLE("The room busy for the date.");

    private final String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    ErrorConstants(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
