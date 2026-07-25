package com.darkhan.booking.seat;

import lombok.Getter;

import java.util.UUID;

@Getter
public class SeatLockTimeoutException extends RuntimeException {
    private final UUID seatId;

    public SeatLockTimeoutException(UUID seatId) {
        super("Failed to get lock for seat with id: " + seatId + ". Try again.");
        this.seatId = seatId;
    }
}
