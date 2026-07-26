package com.darkhan.booking.seat;

import com.darkhan.booking.booking.BookingResponse;
import com.darkhan.booking.booking.BookingService;
import com.darkhan.booking.booking.SeatBookingService;
import com.darkhan.booking.hold.HoldService;
import com.darkhan.booking.booking.CreateBookingRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class SeatController {

    private final SeatRepository seatRepository;
    private final BookingService bookingService;
    private final SeatBookingService seatBookingService;
    private final HoldService holdService;
    private final SeatService seatService;

    @PostMapping("/seats/{seatId}/bookings")
    public BookingResponse createBookingBySeatId(@PathVariable UUID seatId, @RequestBody CreateBookingRequest request) {
        return BookingResponse.from(seatBookingService.book(seatId, request.userId()));
    }

    @PostMapping("/seats/{seatId}/hold")
    public boolean createHoldBySeatId(@PathVariable UUID seatId, @RequestBody CreateHoldRequest request) {
        return holdService.hold(seatId, request.userId());
    }

    @PostMapping("/seats")
    public ResponseEntity<SeatResponse> createSeatByEventId(@Valid @RequestBody CreateSeatRequest request) {
        SeatResponse response = seatService.create(request.eventId(), request.label());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
