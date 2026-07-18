package com.darkhan.booking.event;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "event")
@Getter
@Setter
@NoArgsConstructor
public class Event {
    @Id
    @UuidGenerator
    private UUID id;

    private String name;
    private String venue;
    private Instant startsAt;

    public Event(String name, String venue, Instant startsAt) {
        this.name = name;
        this.venue = venue;
        this.startsAt = startsAt;
    }
}
