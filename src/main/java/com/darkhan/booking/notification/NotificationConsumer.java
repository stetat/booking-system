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
public class NotificationConsumer {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @KafkaListener(topics = BookingTopics.BOOKING_CONFIRMED)
    public void bookingConfirmedListener(ConsumerRecord<String, String> record) {
        BookingConfirmedMessage message = objectMapper.readValue(record.value(), BookingConfirmedMessage.class);
        notificationService.handle(message);
    }


}
