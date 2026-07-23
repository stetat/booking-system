package com.darkhan.booking.booking;

import com.darkhan.booking.TestcontainersConfiguration;
import com.darkhan.booking.event.Event;
import com.darkhan.booking.event.EventRepository;
import com.darkhan.booking.hold.HoldService;
import com.darkhan.booking.seat.Seat;
import com.darkhan.booking.seat.SeatAlreadyBookedException;
import com.darkhan.booking.seat.SeatRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class SeatHoldNotReleasedOnRejectTest {

    @Autowired
    BookingService bookingService;

    @Autowired
    HoldService holdService;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    BookingRepository bookingRepository;


    @Test
    public void holdNotReleased() {
        Instant startsAt = LocalDateTime.of(2026, 10, 10, 10, 10)
                .atZone(ZoneId.of("Asia/Almaty"))
                .toInstant();

        Event event = eventRepository.save(new Event("final", "ortalyq", startsAt));
        Seat seat = seatRepository.save(new Seat(event, "vip"));

        holdService.hold(seat.getId(), "user-1");
        bookingService.book(seat.getId(), "user-1");

        holdService.hold(seat.getId(), "user-2");
        assertThatThrownBy(() -> bookingService.book(seat.getId(), "user-2"))
                .isInstanceOf(SeatAlreadyBookedException.class);

        assertThat(holdService.getSeatHolderUserId(seat.getId())).isEqualTo("user-2");
        holdService.deleteHold(seat.getId());
    }

    @AfterEach
    void cleanup() {
        bookingRepository.deleteAll();
        seatRepository.deleteAll();
        eventRepository.deleteAll();
    }
}
