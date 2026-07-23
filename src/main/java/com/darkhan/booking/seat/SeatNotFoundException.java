package com.darkhan.booking.seat;

import lombok.Getter;

import java.util.UUID;

@Getter
public class SeatNotFoundException extends RuntimeException {

    private final UUID seatId;

    public SeatNotFoundException(UUID seatId) {
        super("Seat with UUID: " + seatId + " not found");
        this.seatId = seatId;
    }
}
