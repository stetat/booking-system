package com.darkhan.booking.booking;

import com.darkhan.booking.TestcontainersConfiguration;
import com.darkhan.booking.event.Event;
import com.darkhan.booking.event.EventRepository;
import com.darkhan.booking.hold.HoldService;
import com.darkhan.booking.outbox.Outbox;
import com.darkhan.booking.outbox.OutboxRepository;
import com.darkhan.booking.seat.Seat;
import com.darkhan.booking.seat.SeatRepository;
import jakarta.websocket.SendResult;
import org.assertj.core.api.AtomicBooleanAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


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
    OutboxRepository outboxRepository;

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

    @MockitoSpyBean
    KafkaTemplate<String, String> kafkaTemplate;



    @TestConfiguration
    static class BookingConfirmedCollector {
        volatile CountDownLatch latch = new CountDownLatch(1);
        final ConcurrentLinkedQueue<String> payloads = new ConcurrentLinkedQueue<>();

        void reset() {
            latch = new CountDownLatch(1);
            payloads.clear();
        }

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
        assertThat(collector.payloads).hasSize(1);
        BookingConfirmedMessage message = objectMapper.readValue(collector.payloads.peek(), BookingConfirmedMessage.class);

        assertThat(message.messageId()).isNotNull();
        assertThat(message.seatId()).isEqualTo(seat.getId());
        assertThat(message.userId()).isEqualTo("user-1");
        assertThat(message.bookingCreatedAt()).isNotNull();
    }

    @Test
    void eventSurvivesBrokerOutageAndPublishesOnRecovery() throws InterruptedException {
        doReturn(CompletableFuture.failedFuture(new RuntimeException("broker down")))
                .when(kafkaTemplate)
                .send(anyString(), anyString(), anyString());

        Instant startsAt = LocalDateTime.of(2026, 9, 9, 13, 4)
                .atZone(ZoneId.of("Asia/Almaty"))
                .toInstant();

        Event event = new Event("final", "ortalyq", startsAt);
        eventRepository.save(event);

        Seat seat = new Seat(event, "vip");
        seatRepository.save(seat);

        holdService.hold(seat.getId(), "user-1");
        bookingService.book(seat.getId(), "user-1");

        assertThat(bookingRepository.count()).isEqualTo(1);
        assertThat(outboxRepository.findAll().getFirst().getPublishedAt()).isNull();

        boolean arrived = collector.latch.await(3, TimeUnit.SECONDS);
        assertThat(arrived).isFalse();

        // removing stub
        doCallRealMethod().when(kafkaTemplate).send(anyString(), anyString(), anyString());

        boolean recovered = collector.latch.await(10, TimeUnit.SECONDS);
        assertThat(recovered).isTrue();

        assertThat(collector.payloads).hasSize(1);
        BookingConfirmedMessage payload = objectMapper.readValue(collector.payloads.peek(), BookingConfirmedMessage.class);
        assertThat(payload.seatId()).isEqualTo(seat.getId());


        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            List<Outbox> rows = outboxRepository.findAll();
            assertThat(rows).hasSize(1);
            assertThat(rows.getFirst().getPublishedAt()).isNotNull();
        });
    }

    @Test
    void republishesEventWhenRelayCrashesAfterBrokerAck() {
        AtomicBoolean firstCall = new AtomicBoolean(true);

        doAnswer(invocation -> {
            Object result = invocation.callRealMethod();
            ((CompletableFuture<?>) result).join();

            if (firstCall.compareAndSet(true, false)) {
                throw new RuntimeException("relay crashed after ack");
            }

            return result;
        })
                .when(kafkaTemplate)
                .send(anyString(), anyString(), anyString());

        Instant startsAt = LocalDateTime.of(2026, 9, 9, 13, 4)
                .atZone(ZoneId.of("Asia/Almaty"))
                .toInstant();

        Event event = new Event("final", "ortalyq", startsAt);
        eventRepository.save(event);

        Seat seat = new Seat(event, "vip");
        seatRepository.save(seat);

        holdService.hold(seat.getId(), "user-1");
        bookingService.book(seat.getId(), "user-1");

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            assertThat(collector.payloads).hasSize(2);
        });

        List<String> delivered = List.copyOf(collector.payloads);
        assertThat(delivered.getFirst()).isEqualTo(delivered.getLast());
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            List<Outbox> rows = outboxRepository.findAll();
//            assertThat(rows).hasSize(1);
            assertThat(rows.getFirst().getPublishedAt()).isNotNull();
        });

    }


    @BeforeEach
    void resetCollector() {
        collector.reset();
    }

    @AfterEach
    void cleanup() {
        bookingRepository.deleteAll();
        seatRepository.deleteAll();
        eventRepository.deleteAll();
        outboxRepository.deleteAll();
        redis.getConnectionFactory().getConnection().serverCommands().flushDb();
    }
}


