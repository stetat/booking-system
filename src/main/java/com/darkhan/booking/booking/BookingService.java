package com.darkhan.booking.booking;


import com.darkhan.booking.event.Seat;
import com.darkhan.booking.event.SeatAlreadyBookedException;
import com.darkhan.booking.event.SeatNotFoundException;
import com.darkhan.booking.event.SeatRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BookingService {

    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public Booking book(UUID seatId, String userId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

        if(bookingRepository.existsBySeatIdAndStatusNot(seatId, BookingStatus.CANCELLED)) {
            throw new SeatAlreadyBookedException(seatId);
        }

        Booking booking = new Booking(userId, seat, BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        return booking;
    }
}
