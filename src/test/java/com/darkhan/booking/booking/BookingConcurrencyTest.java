package com.darkhan.booking.booking;

import com.darkhan.booking.TestcontainersConfiguration;
import com.darkhan.booking.event.Event;
import com.darkhan.booking.event.EventRepository;
import com.darkhan.booking.seat.Seat;
import com.darkhan.booking.seat.SeatAlreadyBookedException;
import com.darkhan.booking.seat.SeatRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class BookingConcurrencyTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    BookingService bookingService;

    @Autowired
    BookingRepository bookingRepository;


    @Test
    void bookingConcurrencyTest() throws InterruptedException {
        Instant startsAt = LocalDateTime.of(2026, 8, 10, 20, 0)
                .atZone(ZoneId.of("Asia/Almaty"))
                .toInstant();
        Event event = new Event("test", "test", startsAt);
        eventRepository.save(event);

        Seat seat = new Seat(event, "vip");
        seatRepository.save(seat);

        int N = 200;
        ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(N);

        AtomicInteger ok = new AtomicInteger();
        AtomicInteger conflict = new AtomicInteger();
        AtomicInteger dbViolation = new AtomicInteger();
        AtomicInteger other = new AtomicInteger();

        for(int i = 0; i < N; i++) {
            final int idx = i;
            pool.submit(() -> {
                try {
                    startGate.await();
                    Booking booking = bookingService.book(seat.getId(), "user-" + idx);
                    ok.incrementAndGet();
                } catch (SeatAlreadyBookedException e) {
                    conflict.incrementAndGet();
                } catch (DataIntegrityViolationException e) {
                    dbViolation.incrementAndGet();
                } catch (Exception e) {
                    other.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    done.countDown();
                }
            });
        }

        startGate.countDown();
        boolean threadsFinished = done.await(30, TimeUnit.SECONDS);
        pool.shutdown();

        System.out.println(" ------------------------------\n");
        System.out.println("successfull bookings: " + bookingRepository.count() + " -- " + ok.get());
        System.out.println("db violations: " + dbViolation.get());
        System.out.println("seat already booked exceptions: " + conflict.get());
        System.out.println("others: " + other);
        System.out.println("\n------------------------------ ");

        assertThat(bookingRepository.count()).isEqualTo(1);
        assertThat(ok.get()).isEqualTo(1);
        assertThat(dbViolation.get()).isGreaterThan(0);
        assertThat(threadsFinished).isEqualTo(true);

    }

    @AfterEach
    void cleanup() {
        bookingRepository.deleteAll();
        seatRepository.deleteAll();
        eventRepository.deleteAll();
    }
}
