package com.darkhan.booking.seat;

import java.util.UUID;

public class SeatNotFoundException extends RuntimeException {
    public SeatNotFoundException(UUID id) {
        super("Seat with UUID: " + id + " not found");
    }
}
