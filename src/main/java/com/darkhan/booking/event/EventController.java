package com.darkhan.booking.event;

import com.darkhan.booking.seat.SeatService;
import com.darkhan.booking.seat.SeatRepository;
import com.darkhan.booking.seat.SeatResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class EventController {

    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;
    private final SeatService seatMapService;
    private final EventService eventService;

    @GetMapping("/events")
    public List<EventResponse> getEvents() {
        return eventRepository.findAll()
                .stream()
                .map(EventResponse::from)
                .toList();
    }

    @PostMapping("/events")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest request) {
        EventResponse response = eventService.create(request.name(), request.venue(), request.startsAt());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/events/{eventId}/seats")
    public List<SeatResponse> getSeatsByEventId(@PathVariable UUID eventId) {
        return seatMapService.getSeatMap(eventId);
    }

}
