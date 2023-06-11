package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.status.Status;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Builder
@Data
public class BookingReturningDto {
    private Long id;
    private Item item;
    private LocalDateTime start;
    private LocalDateTime end;
    private User booker;
    private Status status;
}
