package com.darkhan.booking.hold;

import com.darkhan.booking.booking.BookingConfirmed;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class HoldReleaseListener {

    private final HoldService holdService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingConfirmed(BookingConfirmed event) {
        holdService.deleteHold(event.seatId());
    }
}
