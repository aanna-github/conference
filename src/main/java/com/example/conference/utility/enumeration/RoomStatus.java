package com.example.conference.utility.enumeration;

public enum RoomStatus {
    FREE,

    BOOKED;

    public static RoomStatus findByName(String name) {
        RoomStatus result = null;
        for (RoomStatus status : values()) {
            if (status.name().equalsIgnoreCase(name)) {
                result = status;
                break;
            }
        }
        return result;
    }
}
