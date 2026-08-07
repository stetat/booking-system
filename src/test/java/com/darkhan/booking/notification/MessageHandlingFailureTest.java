package com.darkhan.booking.notification;

import com.darkhan.booking.TestcontainersConfiguration;
import com.darkhan.booking.booking.BookingConfirmedMessage;
import com.darkhan.booking.booking.BookingTopics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;


import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = "spring.kafka.consumer.auto-offset-reset=earliest")
@Import(TestcontainersConfiguration.class)
public class MessageHandlingFailureTest {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    InboxRepository inboxRepository;

    @MockitoSpyBean
    NotificationSender notificationSender;

    @Test
    void retriesTenTimesThenDropsFailedMessage() {
        doThrow(new RuntimeException("sender down"))
                .when(notificationSender)
                .send(any(BookingConfirmedMessage.class));

        UUID seatId = UUID.randomUUID();

        BookingConfirmedMessage poisonMessage = new BookingConfirmedMessage(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), seatId, "user-1", "vip-1", Instant.now());
        String poisonJson = objectMapper.writeValueAsString(poisonMessage);
        kafkaTemplate.send(BookingTopics.BOOKING_CONFIRMED, seatId.toString(), poisonJson);

        verify(notificationSender, after(5_000).times(10)).send(any(BookingConfirmedMessage.class));


        doCallRealMethod()
                .when(notificationSender)
                .send(any(BookingConfirmedMessage.class));

        BookingConfirmedMessage goodMessage = new BookingConfirmedMessage(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), seatId, "user-1", "vip-1", Instant.now());
        String goodJson = objectMapper.writeValueAsString(goodMessage);
        kafkaTemplate.send(BookingTopics.BOOKING_CONFIRMED, seatId.toString(), goodJson);

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            List<Inbox> rows = inboxRepository.findAll();
            assertThat(rows).hasSize(1);
            assertThat(rows.getFirst().getMessageId()).isEqualTo(goodMessage.messageId());
        });


    }

    @AfterEach
    void cleanUp() {
        inboxRepository.deleteAll();
    }
}
