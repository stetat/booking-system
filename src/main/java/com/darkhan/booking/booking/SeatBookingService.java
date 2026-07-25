package com.darkhan.booking.booking;

import com.darkhan.booking.seat.SeatLockTimeoutException;
import lombok.AllArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class SeatBookingService {

    private final BookingService bookingService;
    private final RedissonClient redissonClient;

    public Booking book(UUID seatId, String userId) {
        RLock lock = redissonClient.getLock("lock:seat:" + seatId);
        try {
            boolean acquired = lock.tryLock(5, TimeUnit.SECONDS);
            if(!acquired) {
                throw new SeatLockTimeoutException(seatId);
            }

            return bookingService.book(seatId, userId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if(lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }




    }
}
