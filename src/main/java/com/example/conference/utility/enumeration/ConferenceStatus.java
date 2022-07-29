package com.example.conference.utility.enumeration;

public enum ConferenceStatus {
    SCHEDULED,

    ACTIVE,

    CANCELED;

    public static ConferenceStatus findByName(String name) {
        ConferenceStatus result = null;
        for (ConferenceStatus status : values()) {
            if (status.name().equalsIgnoreCase(name)) {
                result = status;
                break;
            }
        }
        return result;
    }
}
