package com.example.conference.configuration.security.domain;

public enum RoleEnum {
    ROLE_USER("USER"),

    ROLE_MODERATOR("MODERATOR"),

    ROLE_ADMIN("ADMIN");

    private final String value;

    RoleEnum(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static RoleEnum findByName(String name) {
        RoleEnum result = null;
        for (RoleEnum status : values()) {
            if (status.name().equalsIgnoreCase(name)) {
                result = status;
                break;
            }
        }
        return result;
    }
}
