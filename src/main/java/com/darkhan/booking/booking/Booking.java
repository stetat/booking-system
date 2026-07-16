package com.darkhan.booking.booking;

import com.darkhan.booking.event.Seat;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @UuidGenerator
    UUID id;

    String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    Seat seat;

    @Enumerated(EnumType.STRING)
    BookingStatus status;
    Instant createdAt;


}
