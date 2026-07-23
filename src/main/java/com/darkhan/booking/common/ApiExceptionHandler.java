package com.darkhan.booking.common;

import com.darkhan.booking.seat.SeatAlreadyBookedException;
import com.darkhan.booking.seat.SeatAlreadyHeldException;
import com.darkhan.booking.seat.SeatNotFoundException;
import com.darkhan.booking.seat.SeatNotHeldException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(SeatNotFoundException.class)
    public ResponseEntity<?> handle(SeatNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("seatId", ex.getSeatId(), "error", "SEAT_NOT_FOUND"));
    }

    @ExceptionHandler(SeatAlreadyBookedException.class)
    public ResponseEntity<?> handle(SeatAlreadyBookedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("seatId", ex.getSeatId(), "error", "SEAT_ALREADY_BOOKED"));
    }

    @ExceptionHandler(SeatAlreadyHeldException.class)
    public ResponseEntity<?> handle(SeatAlreadyHeldException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("seatId", ex.getSeatId(), "error", "SEAT_ALREADY_HELD"));
    }

    @ExceptionHandler(SeatNotHeldException.class)
    public ResponseEntity<?> handle(SeatNotHeldException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("seatId", ex.getSeatId(), "error", "SEAT_NOT_HELD"));
    }


}
