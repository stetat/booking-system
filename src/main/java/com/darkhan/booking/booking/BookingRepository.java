package com.darkhan.booking.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    boolean existsBySeatIdAndStatusNot(UUID seatId, BookingStatus status);

    @Query("select b.seat.id from Booking b where b.seat.event.id = :eventId and b.status <> :excluded")
    Set<UUID> findBookedSeatsByEventId(UUID eventId, BookingStatus excluded);
}
