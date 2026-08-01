package com.darkhan.booking.outbox;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void create(UUID aggregateId, String topic, Object payload) {
        String jsonPayload = objectMapper.writeValueAsString(payload);
        Outbox outbox = new Outbox(aggregateId, topic, jsonPayload);
        outboxRepository.save(outbox);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public List<Outbox> claimUnpublished(int batchSize) {
        return outboxRepository.findUnpublishedForUpdate(batchSize);
    }


}
