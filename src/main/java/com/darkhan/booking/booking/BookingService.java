package com.darkhan.booking.booking;


import com.darkhan.booking.seat.Seat;
import com.darkhan.booking.seat.SeatAlreadyBookedException;
import com.darkhan.booking.seat.SeatNotFoundException;
import com.darkhan.booking.seat.SeatRepository;

import jakarta.persistence.LockModeType;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class BookingService {

    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public Booking book(UUID seatId, String userId) {
        Seat seat = seatRepository.findByIdForUpdate(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

        if(bookingRepository.existsBySeatIdAndStatusNot(seatId, BookingStatus.CANCELLED)) {
            throw new SeatAlreadyBookedException(seatId);
        }

        Booking booking = new Booking(userId, seat, BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        return booking;
    }
}
