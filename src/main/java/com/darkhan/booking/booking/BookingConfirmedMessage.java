package com.darkhan.booking.booking;

import java.time.Instant;
import java.util.UUID;

public record BookingConfirmedMessage(UUID bookingId, UUID eventId, UUID seatId, String userId, String label, Instant bookingCreatedAt) {
}
