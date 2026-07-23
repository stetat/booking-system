package com.darkhan.booking.booking;


import com.darkhan.booking.hold.HoldService;
import com.darkhan.booking.seat.*;

import jakarta.persistence.LockModeType;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class BookingService {

    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final HoldService holdService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Booking book(UUID seatId, String userId) {

        holdService.assertHeldBy(seatId, userId);

        Seat seat = seatRepository.findByIdForUpdate(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

        if(bookingRepository.existsBySeatIdAndStatusNot(seatId, BookingStatus.CANCELLED)) {
            throw new SeatAlreadyBookedException(seatId);
        }

        Booking booking = new Booking(userId, seat, BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        eventPublisher.publishEvent(new BookingConfirmed(seat.getId()));


        return booking;
    }
}
