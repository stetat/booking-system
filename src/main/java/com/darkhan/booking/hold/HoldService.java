package com.darkhan.booking.hold;

import com.darkhan.booking.seat.SeatAlreadyHeldException;
import com.darkhan.booking.seat.SeatNotHeldException;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@AllArgsConstructor
public class HoldService {

    private final StringRedisTemplate redisTemplate;

    public boolean hold(UUID seatId, String userId) {
        boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent("seat-hold:" + seatId, userId, Duration.ofMinutes(10));

        if(!acquired) {
            throw new SeatAlreadyHeldException(seatId);
        }

        return true;
    }

    public String getSeatHolderUserId(UUID seatId) {
        return redisTemplate.opsForValue()
                .get("seat-hold:" + seatId);
    }

    public void deleteHold(UUID seatId) {
        redisTemplate.delete("seat-hold:" + seatId);
    }

    public void assertHeldBy(UUID seatId, String userId) {
        String seatHolderId = redisTemplate.opsForValue()
                .get("seat-hold:" + seatId);

        if(seatHolderId == null) {
            throw new SeatNotHeldException(seatId);
        }

        if(!seatHolderId.equals(userId)) {
            throw new SeatAlreadyHeldException(seatId);
        }
    }
}
