package com.darkhan.booking.booking;

import com.darkhan.booking.TestcontainersConfiguration;
import com.darkhan.booking.event.Event;
import com.darkhan.booking.event.EventRepository;
import com.darkhan.booking.seat.Seat;
import com.darkhan.booking.seat.SeatRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
public class BookingServiceTest {
    @Autowired
    BookingService bookingService;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Test
    void bookingFreeSeat_succeeds() {
        Instant startsAt = LocalDateTime.of(2026, 8, 19, 13, 55)
                .atZone(ZoneId.of("Asia/Almaty"))
                .toInstant();
        Event event = eventRepository.save(new Event("wc final", "ortalyq stadium", startsAt));
        Seat seat = seatRepository.save(new Seat(event, "vip"));

        Booking booking = bookingService.book(seat.getId(), "user-1");

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(bookingRepository.existsBySeatIdAndStatusNot(seat.getId(), BookingStatus.CANCELLED)).isTrue();

    }
}
