package com.darkhan.booking.booking;


import com.darkhan.booking.hold.HoldService;
import com.darkhan.booking.seat.*;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
        UUID eventId = seat.getEvent().getId();

        if(bookingRepository.existsBySeatIdAndStatusNot(seatId, BookingStatus.CANCELLED)) {
            throw new SeatAlreadyBookedException(seatId);
        }

        Booking booking = new Booking(userId, seat, BookingStatus.CONFIRMED);
        bookingRepository.saveAndFlush(booking);

        eventPublisher.publishEvent(new BookingConfirmedMessage(booking.getId(), eventId, seatId, userId, seat.getLabel(), booking.getCreatedAt()));

        return booking;
    }
}
