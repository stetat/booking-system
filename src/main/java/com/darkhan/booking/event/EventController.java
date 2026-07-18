package com.darkhan.booking.event;

import com.darkhan.booking.seat.SeatRepository;
import com.darkhan.booking.seat.SeatResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class EventController {

    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    @GetMapping("/events")
    public List<EventResponse> getEvents() {
        return eventRepository.findAll()
                .stream()
                .map(EventResponse::from)
                .toList();
    }

    @GetMapping("/events/{eventId}/seats")
    public List<SeatResponse> getSeatsByEventId(@PathVariable UUID eventId) {
        return seatRepository.findAll()
                .stream()
                .filter(seat -> seat.getEvent().getId().equals(eventId))
                .map(SeatResponse::from)
                .toList();
    }

}
