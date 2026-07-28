package com.darkhan.booking.seat;

import com.darkhan.booking.booking.BookingConfirmedMessage;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class SeatMapCacheEvictionListener {

    private final StringRedisTemplate redis;


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingConfirmed(BookingConfirmedMessage event) {
        String key = SeatService.seatMapKey(event.eventId());
        redis.delete(key);
    }
}
