package com.darkhan.booking.outbox;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@AllArgsConstructor
public class OutboxRelay {
    private static final int BATCH_SIZE = 100;

    private final OutboxService outboxService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    @Scheduled(fixedDelay = 1000)
    public void relayPending() {
        List<Outbox> unpublished = outboxService.claimUnpublished(BATCH_SIZE);

        for(Outbox row : unpublished) {
            kafkaTemplate.send(row.getTopic(), row.getAggregateId().toString(), row.getPayload()).join();
            row.setPublishedAt(Instant.now());
        }
    }
}
