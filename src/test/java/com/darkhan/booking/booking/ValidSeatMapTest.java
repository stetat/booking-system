package com.darkhan.booking.booking;


import com.darkhan.booking.TestcontainersConfiguration;
import com.darkhan.booking.event.Event;
import com.darkhan.booking.event.EventRepository;
import com.darkhan.booking.hold.HoldService;
import com.darkhan.booking.seat.Seat;
import com.darkhan.booking.seat.SeatRepository;
import com.darkhan.booking.seat.SeatResponse;
import com.darkhan.booking.seat.SeatService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class ValidSeatMapTest {

    @Autowired
    SeatService seatService;

    @Autowired
    HoldService holdService;

    @Autowired
    BookingService bookingService;

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    StringRedisTemplate redis;

    @Test
    public void validSeatMap() {
        Instant startsAt = LocalDateTime.of(2026, 9, 9, 13, 4)
                .atZone(ZoneId.of("Asia/Almaty"))
                .toInstant();

        Event event = new Event("final", "ortalyq", startsAt);
        eventRepository.save(event);

        Seat seat = new Seat(event, "vip");
        seatRepository.save(seat);
        holdService.hold(seat.getId(), "user-1");

        List<SeatResponse> seatMap = seatService.getSeatMap(event.getId());
        boolean firstAvailable = seatMap
                .stream()
                .filter(seatResponse -> seatResponse.id().equals(seat.getId()))
                .toList()
                .getFirst()
                .available();

        Booking booking = bookingService.book(seat.getId(), "user-1");

        List<SeatResponse> seatMapAfter = seatService.getSeatMap(event.getId());
        boolean secondAvailable = seatMapAfter
                .stream()
                .filter(seatResponse -> seatResponse.id().equals(seat.getId()))
                .toList()
                .getFirst()
                .available();

        boolean bookingExists = bookingRepository.existsById(booking.getId());


        assertThat(firstAvailable).isTrue();
        assertThat(secondAvailable).isFalse();
        assertThat(bookingExists).isTrue();

    }

    @AfterEach
    void cleanup() {
        bookingRepository.deleteAll();
        seatRepository.deleteAll();
        eventRepository.deleteAll();
        redis.getConnectionFactory().getConnection().serverCommands().flushDb();


    }


}
