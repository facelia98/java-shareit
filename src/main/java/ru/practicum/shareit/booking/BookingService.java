package ru.practicum.shareit.booking;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReturningDto;

import java.util.List;

public interface BookingService {

    @Transactional
    BookingReturningDto save(Long userId, BookingDto bookingDto);

    @Transactional
    BookingReturningDto update(Long userId, Long bookingId, boolean approved);

    @Transactional(readOnly = true)
    BookingReturningDto findById(Long bookingId, Long userId);

    @Transactional(readOnly = true)
    List<BookingReturningDto> findAllByUserId(Long userId, String state);

    @Transactional(readOnly = true)
    List<BookingReturningDto> findAllForOwner(Long userId, String state);

}
