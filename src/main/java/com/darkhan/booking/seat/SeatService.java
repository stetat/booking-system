package com.darkhan.booking.seat;

import com.darkhan.booking.booking.BookingRepository;
import com.darkhan.booking.booking.BookingService;
import com.darkhan.booking.booking.BookingStatus;
import com.darkhan.booking.event.Event;
import com.darkhan.booking.event.EventNotFoundException;
import com.darkhan.booking.event.EventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SeatService {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    @Transactional
    public SeatResponse create(UUID eventId, String label) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        Seat seat = new Seat(event, label);
        seatRepository.save(seat);
        return SeatResponse.from(seat, true);
    }


    public List<SeatResponse> getSeatMap(UUID eventId) {
        String key = "seat-map:event:" + eventId;
        String cached = redis.opsForValue().get(key);

        if(cached != null) {
            return objectMapper.readValue(cached, new TypeReference<List<SeatResponse>>() {});
        }


        List<Seat> seats = seatRepository.findByEventId(eventId);
        Set<UUID> bookedIds = bookingRepository.findBookedSeatsByEventId(eventId, BookingStatus.CANCELLED);

        List<SeatResponse> seatMap =  seats.stream()
                .map(seat -> SeatResponse.from(seat, !bookedIds.contains(seat.getId())))
                .toList();


        String json = objectMapper.writeValueAsString(seatMap);
        redis.opsForValue().set(key, json, Duration.ofMinutes(10));

        return seatMap;
    }

}
