package com.darkhan.booking.seat;

import com.darkhan.booking.booking.BookingResponse;
import com.darkhan.booking.booking.BookingService;
import com.darkhan.booking.booking.CreateBookingRequest;
import lombok.AllArgsConstructor;
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

    @PostMapping("/seats/{seatId}/bookings")
    public BookingResponse createBookingBySeatId(@PathVariable UUID seatId, @RequestBody CreateBookingRequest request) {
        return BookingResponse.from(bookingService.book(seatId, request.userId()));
    }

}
