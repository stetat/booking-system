package com.darkhan.booking.notification;

import com.darkhan.booking.booking.BookingConfirmedMessage;
import com.darkhan.booking.booking.BookingTopics;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = BookingTopics.BOOKING_CONFIRMED)
    public void bookingConfirmedListener(ConsumerRecord<String, String> record) {
        BookingConfirmedMessage message = objectMapper.readValue(record.value(), BookingConfirmedMessage.class);
        log.info("Sending email to user with id: {}\nBooking id: {}\nCreated at: {}\nEvent id: {}\nSeat id: {}\nLabel: {}",
                message.userId(), message.bookingId(), message.bookingCreatedAt(), message.eventId(), message.seatId(), message.label());

    }
}
