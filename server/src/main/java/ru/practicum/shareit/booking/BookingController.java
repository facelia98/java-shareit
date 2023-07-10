package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReturningDto;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingReturningDto addNewBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody BookingDto bookingDto) {
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
                                                     @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                     @RequestParam(value = "from", defaultValue = "0") int from,
                                                     @RequestParam(value = "size", defaultValue = "20") int size) {
        return bookingService.findAllByUserId(userId, state, PageRequest.of(from / size, size));
    }

    @GetMapping("/owner")
    public List<BookingReturningDto> findAllForOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                     @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                     @RequestParam(value = "from", defaultValue = "0") int from,
                                                     @RequestParam(value = "size", defaultValue = "20") int size) {
        return bookingService.findAllForOwner(ownerId, state, PageRequest.of(from / size, size));
    }
}
