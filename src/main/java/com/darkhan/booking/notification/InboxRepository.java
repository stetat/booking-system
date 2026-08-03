package com.darkhan.booking.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.UUID;

public interface InboxRepository extends JpaRepository<Inbox, Long> {

    @Modifying
    @Query(value = """
            INSERT INTO inbox (consumer_group, message_id, processed_at)
            VALUES (:consumerGroup, :messageId, :processedAt)
            ON CONFLICT (consumer_group, message_id) DO NOTHING                    
            """, nativeQuery = true)
    int claim(String consumerGroup, UUID messageId, Instant processedAt);
}
