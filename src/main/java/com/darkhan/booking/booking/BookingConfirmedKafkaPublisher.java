package com.darkhan.booking.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tools.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
@Slf4j
public class BookingConfirmedKafkaPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingConfirmed(BookingConfirmedMessage event) {
        String key = event.seatId().toString();
        String json = objectMapper.writeValueAsString(event);
        kafkaTemplate.send(BookingTopics.BOOKING_CONFIRMED, key, json)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Message is lost\nException: ", ex);}
                });
    }
}
