package com.darkhan.booking.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record CreateEventRequest(@NotBlank String name,
                                 @NotBlank String venue,
                                 @Valid  Instant startsAt) {
}
