package com.darkhan.booking.seat;

import com.darkhan.booking.event.Event;

import java.util.UUID;

public record CreateSeatRequest(UUID eventId, String label) {


}
