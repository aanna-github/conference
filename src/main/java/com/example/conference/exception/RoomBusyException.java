package com.example.conference.exception;


import java.time.LocalDateTime;

public class RoomBusyException extends RuntimeException {
    private static final long serialVersionUID = -7053091254745169752L;

    private String roomId;

    private final LocalDateTime eventDate;

    public RoomBusyException(String message, LocalDateTime eventDate) {
        super(String.format("%s: %s", message, eventDate));
        this.eventDate = eventDate;
    }

    public RoomBusyException(String message, String roomId, LocalDateTime eventDate) {
        super(String.format("%s: %s", message, eventDate));
        this.roomId = roomId;
        this.eventDate = eventDate;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }
}
