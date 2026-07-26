package com.darkhan.booking.seat;

import lombok.Getter;

import java.util.UUID;

public record SeatResponse(UUID id, UUID eventId, String label, boolean available) {

    public static SeatResponse from(Seat s, boolean available) {
        return new SeatResponse(s.getId(), s.getEvent().getId(), s.getLabel(), available);
    }
}
