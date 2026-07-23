package com.darkhan.booking.seat;

import lombok.Getter;

import java.util.UUID;

@Getter
public class SeatAlreadyHeldException extends RuntimeException {
    private final UUID seatId;

    public SeatAlreadyHeldException(UUID seatId) {
        super("Seat with id: " + seatId + " is already held");
        this.seatId = seatId;
    }
}
