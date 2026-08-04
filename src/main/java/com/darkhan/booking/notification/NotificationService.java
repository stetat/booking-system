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
    private final NotificationSender notificationSender;

    public NotificationService(InboxRepository inboxRepository,
                               @Value("${spring.kafka.consumer.group-id}") String consumerGroup, NotificationSender notificationSender) {
        this.inboxRepository = inboxRepository;
        this.consumerGroup = consumerGroup;
        this.notificationSender = notificationSender;

    }

    @Transactional
    public void handle(BookingConfirmedMessage message) {
        int claimed = inboxRepository.claim(consumerGroup, message.messageId(), Instant.now());

        if(claimed == 0) {
            log.info("duplicate message, skipping. message ID: {}", message.messageId());
            return;
        }

        notificationSender.send(message);

    }
}
