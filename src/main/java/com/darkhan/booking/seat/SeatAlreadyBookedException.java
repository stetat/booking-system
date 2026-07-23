package com.darkhan.booking.seat;

import lombok.Getter;

import java.util.UUID;

@Getter
public class SeatAlreadyBookedException extends RuntimeException {
    private final UUID seatId;

    public SeatAlreadyBookedException(UUID seatId) {
        super("Seat with UUID: " + seatId + " has booking");
        this.seatId = seatId;
    }
}
