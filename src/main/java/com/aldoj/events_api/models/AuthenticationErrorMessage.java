package com.aldoj.events_api.models;

public enum AuthenticationErrorMessage {
    INVALID_CREDENTIALS("INVALID_CREDENTIALS"),
    USER_ALREADY_REGISTERED("USER ALREADY REGISTERED");

    private final String message;

    AuthenticationErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
