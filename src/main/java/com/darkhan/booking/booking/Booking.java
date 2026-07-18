package com.darkhan.booking.booking;

import com.darkhan.booking.event.Seat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "booking")
@Getter
@Setter
@NoArgsConstructor
public class Booking {
    @Id
    @UuidGenerator
    private UUID id;

    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @CreationTimestamp
    private Instant createdAt;

    public Booking(String userId, Seat seat, BookingStatus status) {
        this.userId = userId;
        this.seat = seat;
        this.status = status;
    }


}
