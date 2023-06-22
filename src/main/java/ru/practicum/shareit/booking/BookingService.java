package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReturningDto;

import java.util.List;

public interface BookingService {

    BookingReturningDto save(Long userId, BookingDto bookingDto);

    BookingReturningDto update(Long userId, Long bookingId, boolean approved);

    BookingReturningDto findById(Long bookingId, Long userId);

    List<BookingReturningDto> findAllByUserId(Long userId, String state, PageRequest pageRequest);

    List<BookingReturningDto> findAllForOwner(Long userId, String state, PageRequest pageRequest);

}
