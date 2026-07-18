package com.darkhan.booking.seat;

import java.util.UUID;

public record SeatResponse(UUID id, UUID eventId, String label) {

    public static SeatResponse from(Seat s) {
        return new SeatResponse(s.getId(), s.getEvent().getId(), s.getLabel());
    }
}
