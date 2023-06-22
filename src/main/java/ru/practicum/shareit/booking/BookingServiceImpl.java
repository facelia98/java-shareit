package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingReturningDto save(Long userId, BookingDto bookingDto) {
        log.info("POST booking request received to endpoint [/bookings]");
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User not found for id = {}", userId);
            throw new NotFoundException("User not found");
        });
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> {
            log.error("Item not found for id = {}", bookingDto.getItemId());
            throw new NotFoundException("Item not found");
        });
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isBefore(LocalDateTime.now())
                || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            log.error("Time validation failed");
            throw new ValidationException("Time validation failed1");
        }
        if (bookingRepository.isAvailableForBooking(bookingDto.getStart(),
                bookingDto.getEnd(), bookingDto.getItemId())) {
            log.error("Validation failed");
            throw new ValidationException("Validation failed");
        }
        if (!item.getAvailable()) {
            log.error("Item is not available");
            throw new ValidationException("Item is not available");
        }
        if (item.getOwner().getId().equals(userId)) {
            log.error("Owner can't take booking owned item");
            throw new NotFoundException("Owner can't take booking owned item");
        }
        return BookingMapper
                .toBookingReturningDto(bookingRepository.save(BookingMapper
                        .toBooking(bookingDto, user, item)));
    }

    @Override
    @Transactional
    public BookingReturningDto update(Long userId, Long bookingId, boolean approved) {
        log.info("PATCH update statis of booking request received to endpoint [/bookings]");
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found"));
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            log.error("Access denied for userId = {}", userId);
            throw new AccessDenied("Access denied");
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            log.error("Booking status is not WAITING!");
            throw new ValidationException("Invalid status - not WAITING");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingReturningDto(bookingRepository.getById(bookingId));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingReturningDto findById(Long bookingId, Long userId) {
        log.info("GET booking request received to endpoint [/bookings]");
        if (!bookingRepository.existsById(bookingId)) {
            log.error("Booking not found for id = {}", bookingId);
            throw new NotFoundException("Booking not found");
        }
        Booking booking = bookingRepository.findById(bookingId).get();
        if (!(booking.getBooker().getId().equals(userId) ||
                booking.getItem().getOwner().getId().equals(userId))) {
            log.error("Booking not found for user/owner_id = {}", userId);
            throw new NotFoundException("Booking not found for current user");
        }
        return BookingMapper.toBookingReturningDto(bookingRepository.getById(bookingId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingReturningDto> findAllByUserId(Long userId, String state, PageRequest pageRequest) {
         /*if (from < 0 || size <= 0) {
            throw new ValidationException("Incorrect Size or Num of first element");
        }*/
        log.info("GET booking list for owner request received to endpoint [/bookings]");
        if (!userRepository.existsById(userId)) {
            log.error("Booking not found for user/owner_id = {}", userId);
            throw new NotFoundException("User not found");
        }
        LocalDateTime currentTime = LocalDateTime.now();
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingReturningDto)
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, currentTime, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingReturningDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findAllByBookerIdAndEndIsAfterOrderByStartDesc(userId, currentTime, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingReturningDto)
                        .collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingReturningDto)
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingReturningDto)
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, currentTime, currentTime, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingReturningDto)
                        .sorted(Comparator.comparing(BookingReturningDto::getStart))
                        .collect(Collectors.toList());
            default:
                log.error("Unknown status = {}", state);
                throw new UnsupportedStatus(String.format("Unknown state: %s", state));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingReturningDto> findAllForOwner(Long userId, String state, PageRequest pageRequest) {
        /*if (from < 0 || size <= 0) {
            throw new ValidationException("Incorrect Size or Num of first element");
        }*/
        log.info("GET booking list for owner request received to endpoint [/bookings]");
        if (!userRepository.existsById(userId)) {
            log.error("Not found user for id = {}", userId);
            throw new NotFoundException("User not found");
        }
        LocalDateTime currentTime = LocalDateTime.now();
        switch (state) {
            case "ALL":
                return bookingRepository.findAllForOwner(userId, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingReturningDto)
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findAllForOwnerAndBeforeInstant(userId, currentTime, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingReturningDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findAllByOwnerIdAndStartAfterOrderByStartDesc(userId, currentTime, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingReturningDto)
                        .collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findAllForOwner(userId, Status.WAITING, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingReturningDto)
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findAllForOwner(userId, Status.REJECTED, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingReturningDto)
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findAllForOwnerCurrent(userId, currentTime, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingReturningDto)
                        .collect(Collectors.toList());
            default:
                log.error("Unknown status = {}", state);
                throw new UnsupportedStatus(String.format("Unknown state: %s", state));
        }
    }
}
