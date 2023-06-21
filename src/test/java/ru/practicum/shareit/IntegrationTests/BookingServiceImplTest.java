package ru.practicum.shareit.IntegrationTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReturningDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnsupportedStatus;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.status.Status;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
public class BookingServiceImplTest {

    ItemDto item = ItemDto.builder().name("Item").description("Desc").available(true).build();
    ItemDto item2 = ItemDto.builder().name("Item2").description("Desc2").available(false).build();
    UserDto userDto = UserDto.builder().id(1L).name("Name").email("mail@mail.ru").build();
    UserDto userDto2 = UserDto.builder().id(2L).name("Name2").email("mail2@mail.ru").build();

    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private BookingService bookingService;

    @Test
    void addBookingEXCEPTIONValidation() {
        UserDto user = userService.add(userDto);
        UserDto user2 = userService.add(userDto2);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusYears(1);
        ItemDto savedItemDto = itemService.add(item2, user.getId());
        BookingDto dto = new BookingDto(null, savedItemDto.getId(), start, end, Status.WAITING);
        assertThrowsExactly(ValidationException.class,
                () -> bookingService.save(user2.getId(), dto));
    }

    @Test
    void addBookingEXCEPTIONValidationOwner() {
        UserDto user = userService.add(userDto);
        UserDto user2 = userService.add(userDto2);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusYears(1);
        ItemDto savedItemDto = itemService.add(item, user.getId());
        BookingDto dto = new BookingDto(null, savedItemDto.getId(), start, end, Status.WAITING);
        assertThrowsExactly(NotFoundException.class,
                () -> bookingService.save(user.getId(), dto));
    }

    @Test
    void addBookingOK() {
        UserDto user = userService.add(userDto);
        UserDto user2 = userService.add(userDto2);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusYears(1);
        ItemDto savedItemDto = itemService.add(item, user.getId());
        BookingDto dto = new BookingDto(null, savedItemDto.getId(), start, end, Status.WAITING);
        BookingReturningDto booking = bookingService.save(user2.getId(), dto);
        assertNotNull(booking.getId());
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
        assertEquals(dto.getItemId(), booking.getItem().getId());
        assertEquals(Status.WAITING, booking.getStatus());
    }

    @Test
    void addBookingEXCEPTIONNotFoundUser() {
        UserDto user = userService.add(userDto);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusYears(1);
        ItemDto savedItemDto = itemService.add(item, user.getId());
        BookingDto dto = new BookingDto(null, savedItemDto.getId(), start, end, Status.WAITING);
        assertThrowsExactly(NotFoundException.class, () -> bookingService.save(100L, dto));
    }

    @Test
    void addBookingEXCEPTIONNotFoundItem() {
        UserDto user = userService.add(userDto);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusYears(1);
        BookingDto dto = new BookingDto(null, 10L, start, end, Status.WAITING);
        assertThrowsExactly(NotFoundException.class, () -> bookingService.save(user.getId(), dto));
    }

    @Test
    void updateBookingOK() {
        UserDto user = userService.add(userDto);
        UserDto user2 = userService.add(userDto2);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusYears(1);
        ItemDto savedItemDto = itemService.add(item, user.getId());
        BookingDto dto = new BookingDto(null, savedItemDto.getId(), start, end, Status.WAITING);
        BookingReturningDto booking = bookingService.save(user2.getId(), dto);
        bookingService.update(user.getId(), booking.getId(), true);
        assertEquals(Status.APPROVED, bookingService.findById(booking.getId(), user.getId()).getStatus());
    }

    @Test
    void getByIdOK() {
        UserDto user = userService.add(userDto);
        UserDto user2 = userService.add(userDto2);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusYears(1);
        ItemDto savedItemDto = itemService.add(item, user.getId());
        BookingDto dto = new BookingDto(null, savedItemDto.getId(), start, end, Status.WAITING);
        BookingReturningDto booking = bookingService.save(user2.getId(), dto);
        BookingReturningDto gettedByOwner = bookingService.findById(booking.getId(), user.getId());
        assertNotNull(gettedByOwner.getId());
        assertEquals(dto.getStart(), gettedByOwner.getStart());
        assertEquals(dto.getEnd(), gettedByOwner.getEnd());
        assertEquals(dto.getItemId(), gettedByOwner.getItem().getId());
        assertEquals(Status.WAITING, gettedByOwner.getStatus());
    }

    @Test
    void getByIdEXCEPTIONNotFound() {
        assertThrowsExactly(NotFoundException.class, () -> bookingService.findById(10L, 10L));
    }

    @Test
    void getByIdEXCEPTION() {
        UserDto user = userService.add(userDto);
        UserDto user2 = userService.add(userDto2);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusYears(1);
        ItemDto savedItemDto = itemService.add(item, user.getId());
        BookingDto dto = new BookingDto(null, savedItemDto.getId(), start, end, Status.WAITING);
        BookingReturningDto booking = bookingService.save(user2.getId(), dto);
        assertThrowsExactly(NotFoundException.class, () -> bookingService.findById(booking.getId(), 10L));
    }

    @Test
    void findAllByOwnerEXCEPTION() {
        UserDto user = userService.add(userDto);
        assertThrowsExactly(UnsupportedStatus.class, () ->
                bookingService.findAllForOwner(user.getId(), "KEK", 0, 10));
    }

    @Test
    void findAllByOwnerEXCEPTIONUser() {
        assertThrowsExactly(NotFoundException.class, () ->
                bookingService.findAllForOwner(100L, "ALL", 0, 10));
    }

    @Test
    void findAllByUserIdEXCEPTIONUser() {
        assertThrowsExactly(NotFoundException.class, () ->
                bookingService.findAllByUserId(100L, "ALL", 0, 10));
    }

    @Test
    void findAllByUserIdEXCEPTION() {
        UserDto user = userService.add(userDto);
        assertThrowsExactly(UnsupportedStatus.class, () ->
                bookingService.findAllByUserId(user.getId(), "KEK", 0, 10));
    }

    @Test
    void findAllByUserIdOK() {
        UserDto user = userService.add(userDto);
        UserDto user2 = userService.add(userDto2);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusYears(1);
        ItemDto savedItemDto = itemService.add(item, user.getId());
        BookingDto dto = new BookingDto(null, savedItemDto.getId(), start, end, Status.WAITING);
        BookingReturningDto booking = bookingService.save(user2.getId(), dto);

        assertEquals(1, bookingService.findAllByUserId(user2.getId(), "WAITING", 0, 10).size());
        assertEquals(1, bookingService.findAllByUserId(user2.getId(), "ALL", 0, 10).size());
        assertEquals(0, bookingService.findAllByUserId(user2.getId(), "PAST", 0, 10).size());
        assertEquals(1, bookingService.findAllByUserId(user2.getId(), "FUTURE", 0, 10).size());
        assertEquals(0, bookingService.findAllByUserId(user2.getId(), "REJECTED", 0, 10).size());
        assertEquals(0, bookingService.findAllByUserId(user2.getId(), "CURRENT", 0, 10).size());

        assertEquals(0, bookingService.findAllByUserId(user.getId(), "WAITING", 0, 10).size());
        assertEquals(0, bookingService.findAllByUserId(user.getId(), "ALL", 0, 10).size());
    }

    @Test
    void findAllByOwnerOK() {
        UserDto user = userService.add(userDto);
        UserDto user2 = userService.add(userDto2);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusYears(1);
        ItemDto savedItemDto = itemService.add(item, user.getId());
        BookingDto dto = new BookingDto(null, savedItemDto.getId(), start, end, Status.WAITING);
        BookingReturningDto booking = bookingService.save(user2.getId(), dto);

        assertEquals(1, bookingService.findAllForOwner(user.getId(), "WAITING", 0, 10).size());
        assertEquals(1, bookingService.findAllForOwner(user.getId(), "ALL", 0, 10).size());
        assertEquals(0, bookingService.findAllForOwner(user.getId(), "PAST", 0, 10).size());
        assertEquals(1, bookingService.findAllForOwner(user.getId(), "FUTURE", 0, 10).size());
        assertEquals(0, bookingService.findAllForOwner(user.getId(), "REJECTED", 0, 10).size());
        assertEquals(0, bookingService.findAllForOwner(user.getId(), "CURRENT", 0, 10).size());

        assertEquals(0, bookingService.findAllForOwner(user2.getId(), "WAITING", 0, 10).size());
        assertEquals(0, bookingService.findAllForOwner(user2.getId(), "ALL", 0, 10).size());
    }
}
