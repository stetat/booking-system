package com.darkhan.booking.notification;

import com.darkhan.booking.booking.BookingConfirmedMessage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Slf4j
public class NotificationService {

    private final InboxRepository inboxRepository;
    private final String consumerGroup;

    public NotificationService(InboxRepository inboxRepository,
                               @Value("${spring.kafka.consumer.group-id}") String consumerGroup) {
        this.inboxRepository = inboxRepository;
        this.consumerGroup = consumerGroup;
    }

    @Transactional
    public void handle(BookingConfirmedMessage message) {
        int claimed = inboxRepository.claim(consumerGroup, message.messageId(), Instant.now());

        if(claimed == 0) {
            log.info("duplicate message, skipping. message ID: {}", message.messageId());
            return;
        }

        log.info("Sending email to user with userId: {}\nBooking id: {}\nEvent id: {}\nSeat id: {}\nLabel: {}",
                message.userId(), message.bookingId(), message.eventId(), message.seatId(), message.label());
    }
}
