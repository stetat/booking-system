package com.darkhan.booking.seat;

import lombok.Getter;

import java.util.UUID;

@Getter
public class SeatNotHeldException extends RuntimeException {
    private final UUID seatId;

    public SeatNotHeldException(UUID seatId) {
        super("Seat with id: " + seatId + " is not held.\nYou must first hold to be able to book");
        this.seatId = seatId;
    }
}
