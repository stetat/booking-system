package com.darkhan.booking.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class EventNotFoundException extends RuntimeException {
    private final UUID eventId;

    public EventNotFoundException(UUID eventId) {
        super("Event with id: " + eventId + " not found");
        this.eventId = eventId;
    }
}
