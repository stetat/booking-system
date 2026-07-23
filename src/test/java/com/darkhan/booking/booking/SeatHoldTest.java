package com.darkhan.booking.booking;

import com.darkhan.booking.TestcontainersConfiguration;
import com.darkhan.booking.event.Event;
import com.darkhan.booking.event.EventRepository;
import com.darkhan.booking.hold.HoldService;
import com.darkhan.booking.seat.Seat;
import com.darkhan.booking.seat.SeatAlreadyHeldException;
import com.darkhan.booking.seat.SeatRepository;
import com.darkhan.booking.seat.SeatResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class SeatHoldTest {

    @Autowired
    BookingService bookingService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    HoldService holdService;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Test
    public void seatHold_works() {
        Instant startsAt = LocalDateTime.of(2026, 9, 9, 14, 0)
                .atZone(ZoneId.of("Asia/Almaty"))
                .toInstant();
        Event event = eventRepository.save(new Event("wc26 final", "ortalyq", startsAt));
        Seat seat = seatRepository.save(new Seat(event, "vip"));

        boolean firstHoldAcquired = holdService.hold(seat.getId(), "user-1");
        bookingService.book(seat.getId(), "user-1");
        boolean holdIsGone = redisTemplate.opsForValue()
                .get("seat-hold:" + seat.getId()) == null;

        boolean secondHoldAcquired = holdService.hold(seat.getId(), "user-1");
        Exception e = null;

        try {
            boolean thirdHoldAcquired = holdService.hold(seat.getId(), "user-2");
        } catch (Exception ex) {
            e = ex;
        }

        e.printStackTrace();

        assertThat(firstHoldAcquired).isTrue();
        assertThat(holdIsGone).isTrue();
        assertThat(e).isInstanceOf(SeatAlreadyHeldException.class);
        assertThat(redisTemplate.getExpire("seat-hold:" + seat.getId())).isGreaterThan(0);
    }

    @AfterEach
    void cleanup() {
        bookingRepository.deleteAll();
        seatRepository.deleteAll();
        eventRepository.deleteAll();
    }

}
