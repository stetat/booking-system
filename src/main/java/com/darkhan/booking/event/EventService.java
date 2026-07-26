package com.darkhan.booking.event;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    @Transactional
    public EventResponse create(String name, String venue, Instant startsAt) {
        Event event = new Event(name, venue, startsAt);
        eventRepository.save(event);

        return EventResponse.from(event);
    }
}
