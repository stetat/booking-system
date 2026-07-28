package com.darkhan.booking.seat;

import com.darkhan.booking.booking.BookingConfirmed;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class SeatMapCacheEvictionListener {

    private final StringRedisTemplate redis;
    private final SeatService seatService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingConfirmed(BookingConfirmed event) {
        String key = seatService.seatMapKey(event.eventId());
        redis.delete(key);
    }
}
