package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReturningDto;

import java.util.List;


@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingReturningDto addNewBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Validated @RequestBody BookingDto bookingDto) {
        return bookingService.save(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingReturningDto updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable("bookingId") Long bookingId,
                                             @RequestParam(value = "approved") String approved) {
        return bookingService.update(userId, bookingId, Boolean.parseBoolean(approved));
    }

    @GetMapping("/{bookingId}")
    public BookingReturningDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable("bookingId") Long bookingId) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping()
    public List<BookingReturningDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingService.findAllByUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingReturningDto> findAllForOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                     @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingService.findAllForOwner(ownerId, state);
    }
}
