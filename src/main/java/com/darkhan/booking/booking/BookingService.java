package com.darkhan.booking.booking;


import com.darkhan.booking.hold.HoldService;
import com.darkhan.booking.outbox.OutboxService;
import com.darkhan.booking.seat.Seat;
import com.darkhan.booking.seat.SeatAlreadyBookedException;
import com.darkhan.booking.seat.SeatNotFoundException;
import com.darkhan.booking.seat.SeatRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final OutboxService outboxService;

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

        BookingConfirmedMessage message = new BookingConfirmedMessage(booking.getId(), eventId, seatId, userId, seat.getLabel(), booking.getCreatedAt());
        outboxService.create(seatId, BookingTopics.BOOKING_CONFIRMED, message);


        eventPublisher.publishEvent(message);

        return booking;
    }
}
