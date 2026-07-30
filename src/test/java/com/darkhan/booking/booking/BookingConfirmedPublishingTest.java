package com.darkhan.booking.booking;

import com.darkhan.booking.TestcontainersConfiguration;
import com.darkhan.booking.event.Event;
import com.darkhan.booking.event.EventRepository;
import com.darkhan.booking.hold.HoldService;
import com.darkhan.booking.seat.Seat;
import com.darkhan.booking.seat.SeatRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(properties = { "spring.kafka.consumer.auto-offset-reset=earliest"})
@Import(TestcontainersConfiguration.class)
public class BookingConfirmedPublishingTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    StringRedisTemplate redis;

    @Autowired
    HoldService holdService;

    @Autowired
    BookingService bookingService;

    @Autowired
    BookingConfirmedCollector collector;

    @Autowired
    ObjectMapper objectMapper;

    @TestConfiguration
    static class BookingConfirmedCollector {
        final CountDownLatch latch = new CountDownLatch(1);
        final ConcurrentLinkedQueue<String> payloads = new ConcurrentLinkedQueue<>();

        @KafkaListener(topics = BookingTopics.BOOKING_CONFIRMED, groupId = "test-observer")
        void onBookingConfirmed(String payload) {
            payloads.add(payload);
            latch.countDown();
        }
    }

    @Test
    void publishesMessageWhenBookingCommits() throws InterruptedException {
        Instant startsAt = LocalDateTime.of(2026, 9, 9, 13, 4)
                .atZone(ZoneId.of("Asia/Almaty"))
                .toInstant();

        Event event = new Event("final", "ortalyq", startsAt);
        eventRepository.save(event);

        Seat seat = new Seat(event, "vip");
        seatRepository.save(seat);

        holdService.hold(seat.getId(), "user-1");
        bookingService.book(seat.getId(), "user-1");

        boolean arrived = collector.latch.await(10, TimeUnit.SECONDS);

        assertThat(arrived).isTrue();
        BookingConfirmedMessage message = objectMapper.readValue(collector.payloads.peek(), BookingConfirmedMessage.class);

        assertThat(collector.payloads).hasSize(1);
        assertThat(message.seatId()).isEqualTo(seat.getId());
        assertThat(message.userId()).isEqualTo("user-1");
        assertThat(message.bookingCreatedAt()).isNotNull();
    }

    @AfterEach
    void cleanup() {
        bookingRepository.deleteAll();
        seatRepository.deleteAll();
        eventRepository.deleteAll();
        redis.getConnectionFactory().getConnection().serverCommands().flushDb();
    }
}


