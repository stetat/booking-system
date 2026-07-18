package com.darkhan.booking.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    boolean existsBySeatIdAndStatusNot(UUID seatId, BookingStatus status);
}
