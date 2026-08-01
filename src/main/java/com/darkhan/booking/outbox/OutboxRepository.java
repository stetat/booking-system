package com.darkhan.booking.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {

    @Query(value = "select * from outbox where published_at is null order by id limit :limit for update skip locked", nativeQuery = true)
    List<Outbox> findUnpublishedForUpdate(int limit);
}
