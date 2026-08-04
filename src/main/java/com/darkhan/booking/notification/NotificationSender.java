package com.darkhan.booking.notification;

import com.darkhan.booking.booking.BookingConfirmedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationSender {

    void send(BookingConfirmedMessage message) {
        log.info("Sending email to user with userId: {}\nBooking id: {}\nEvent id: {}\nSeat id: {}\nLabel: {}",
                message.userId(), message.bookingId(), message.eventId(), message.seatId(), message.label());
    }
}

