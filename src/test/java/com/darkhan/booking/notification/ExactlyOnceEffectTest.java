package com.darkhan.booking.notification;

import com.darkhan.booking.TestcontainersConfiguration;
import com.darkhan.booking.booking.BookingConfirmedMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class ExactlyOnceEffectTest {

    @Autowired
    NotificationService notificationService;

    @Autowired
    InboxRepository inboxRepository;

    @MockitoSpyBean
    NotificationSender notificationSender;


    @Test
    void processesDuplicateMessageOnlyOnce() {


        BookingConfirmedMessage message = new BookingConfirmedMessage(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "user-1", "vip-1", Instant.now());
        notificationService.handle(message);
        notificationService.handle(message);

        verify(notificationSender, times(1)).send(message);
        assertThat(inboxRepository.count()).isEqualTo(1);


    }

    @AfterEach
    void cleanup() {
        inboxRepository.deleteAll();
    }
}
