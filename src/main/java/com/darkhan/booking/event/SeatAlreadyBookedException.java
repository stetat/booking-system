package com.darkhan.booking.event;

import java.util.UUID;

public class SeatAlreadyBookedException extends RuntimeException {
    public SeatAlreadyBookedException(UUID id) {
        super("Seat with UUID: " + id + " has booking");
    }
}
