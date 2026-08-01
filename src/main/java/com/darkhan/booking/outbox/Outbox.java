package com.darkhan.booking.outbox;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox")
@Getter
@Setter
@NoArgsConstructor
public class Outbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID aggregateId;
    private String topic;
    private String payload;

    @CreationTimestamp
    private Instant createdAt;

    private Instant publishedAt;

    public Outbox(UUID aggregateId, String topic, String payload) {
        this.aggregateId = aggregateId;
        this.topic = topic;
        this.payload = payload;
    }
}
