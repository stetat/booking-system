package com.darkhan.booking.booking;

import java.time.Instant;
import java.util.UUID;

public record BookingResponse(UUID id, BookingStatus status, Instant createdAt) {

    public static BookingResponse from(Booking b) {
        return new BookingResponse(b.getId(), b.getStatus(), b.getCreatedAt());
    }
}
