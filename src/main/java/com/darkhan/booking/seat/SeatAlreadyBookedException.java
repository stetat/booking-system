package com.darkhan.booking.seat;

import java.util.UUID;

public class SeatAlreadyBookedException extends RuntimeException {
    public SeatAlreadyBookedException(UUID id) {
        super("Seat with UUID: " + id + " has booking");
    }
}
