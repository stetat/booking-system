package com.darkhan.booking.event;

import java.time.Instant;
import java.util.UUID;

public record EventResponse(UUID id, String name, String venue, Instant startsAt) {

    public static EventResponse from(Event e) {
        return new EventResponse(e.getId(), e.getName(), e.getVenue(), e.getStartsAt());
    }
}
