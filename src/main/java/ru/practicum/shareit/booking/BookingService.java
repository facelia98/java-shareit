package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReturningDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.AccessDenied;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnsupportedStatus;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.status.Status;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    public BookingReturningDto save(Long userId, BookingDto bookingDto) {
        log.info("POST booking request received to endpoint [/bookings]");
        if (!itemRepository.existsById(bookingDto.getItemId())) {
            throw new NotFoundException("Item not found");
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("DateTime val should be after local time");
        }
        if (bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new ValidationException("DateTime vals is equals");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("End time should be later of start time");
        }
        if (!itemRepository.getById(bookingDto.getItemId()).getAvailable()) {
            throw new ValidationException("Item is not available");
        }
        if (itemRepository.getById(bookingDto.getItemId()).getOwner().getId().equals(userId)) {
            throw new NotFoundException("Owner can't take booking owned item");
        }
        Item item = itemRepository.getById(bookingDto.getItemId());
        User user = userRepository.getById(userId);
        Booking b = bookingRepository.save(BookingMapper.toBooking(bookingDto, user, item));
        return BookingMapper.toBookingReturningDto(b);
    }

    public BookingReturningDto update(Long userId, Long bookingId, boolean approved) {
        log.info("PATCH booking request received to endpoint [/bookings]");
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found"));
        if (booking.getItem().getOwner().getId() != userId) {
            log.error("Access denied for userId = {}", userId);
            throw new AccessDenied("Access denied");
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            log.error("Approved already!");
            throw new ValidationException("Invalid");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingReturningDto(bookingRepository.getById(bookingId));
    }

    public BookingReturningDto findById(Long bookingId, Long userId) {
        log.info("GET booking request received to endpoint [/bookings]");
        if (!bookingRepository.existsById(bookingId)) {
            log.error("Booking not found");
            throw new NotFoundException("Booking not found");
        }
        Booking booking = bookingRepository.findById(bookingId).get();
        if (!(booking.getBooker().getId().equals(userId) ||
                booking.getItem().getOwner().getId().equals(userId))) {
            log.error("Booking not found");
            throw new NotFoundException("Booking not found");
        }
        return BookingMapper.toBookingReturningDto(bookingRepository.getById(bookingId));
    }

    public List<BookingReturningDto> findAllByUserId(Long userId, String state) {
        log.info("GET booking list for owner request received to endpoint [/bookings]");
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        LocalDateTime currentTime = LocalDateTime.now();
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId)
                        .stream()
                        .map(booking -> BookingMapper.toBookingReturningDto(booking))
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findBookingByBookerIdAndEndIsBeforeOrderByStartDesc(userId, currentTime)
                        .stream()
                        .map(booking -> BookingMapper.toBookingReturningDto(booking))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findBookingByBookerIdAndEndIsAfterOrderByStartDesc(userId, currentTime)
                        .stream()
                        .map(booking -> BookingMapper.toBookingReturningDto(booking))
                        .collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING)
                        .stream()
                        .map(booking -> BookingMapper.toBookingReturningDto(booking))
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED)
                        .stream()
                        .map(booking -> BookingMapper.toBookingReturningDto(booking))
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, currentTime, currentTime)
                        .stream()
                        .map(booking -> BookingMapper.toBookingReturningDto(booking))
                        .sorted(Comparator.comparing(BookingReturningDto::getStart))
                        .collect(Collectors.toList());
            default:
                throw new UnsupportedStatus(String.format("Unknown state: %s", state));
        }

    }

    public List<BookingReturningDto> findAllForOwner(Long userId, String state) {
        log.info("GET booking list for owner request received to endpoint [/bookings]");
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        LocalDateTime currentTime = LocalDateTime.now();
        switch (state) {
            case "ALL":
                return bookingRepository.findAllForOwner(userId)
                        .stream()
                        .map(booking -> BookingMapper.toBookingReturningDto(booking))
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findAllForOwnerAndBeforeInstant(userId, currentTime)
                        .stream()
                        .map(booking -> BookingMapper.toBookingReturningDto(booking))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findAllByOwnerIdAndStartAfterOrderByStartDesc(userId, currentTime)
                        .stream()
                        .map(booking -> BookingMapper.toBookingReturningDto(booking))
                        .collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findAllForOwner(userId, Status.WAITING)
                        .stream()
                        .map(booking -> BookingMapper.toBookingReturningDto(booking))
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findAllForOwner(userId, Status.REJECTED)
                        .stream()
                        .map(booking -> BookingMapper.toBookingReturningDto(booking))
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findAllForOwnerCurrent(userId, currentTime)
                        .stream()
                        .map(booking -> BookingMapper.toBookingReturningDto(booking))
                        .collect(Collectors.toList());
            default:
                throw new UnsupportedStatus(String.format("Unknown state: %s", state));
        }
    }
}
