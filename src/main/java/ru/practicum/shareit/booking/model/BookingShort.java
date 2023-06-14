package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BookingShort {
    private Long id;
    private Long bookerId;
}
