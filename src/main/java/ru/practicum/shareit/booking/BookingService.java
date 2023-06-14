package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReturningDto;

import java.util.List;

public interface BookingService {

    BookingReturningDto save(Long userId, BookingDto bookingDto);

    BookingReturningDto update(Long userId, Long bookingId, boolean approved);

    BookingReturningDto findById(Long bookingId, Long userId);

    List<BookingReturningDto> findAllByUserId(Long userId, String state, int from, int size);

    List<BookingReturningDto> findAllForOwner(Long userId, String state, int from, int size);

}
