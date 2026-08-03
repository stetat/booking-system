package com.darkhan.booking.notification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inbox")
@Getter
@NoArgsConstructor
public class Inbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String consumerGroup;
    private UUID messageId;
    private Instant processedAt;

    public Inbox(String consumerGroup, UUID messageId) {
        this.consumerGroup = consumerGroup;
        this.messageId = messageId;
    }
}
