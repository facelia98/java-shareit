package ru.practicum.shareit.JpaTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.status.Status;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class BookingRepositoryTest {

    Item item;
    Item item2;
    User user;
    User user2;
    @Autowired
    private BookingRepository repository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        user = userRepository.save(User.builder().name("Name").email("e@e.com").build());
        user2 = userRepository.save(User.builder().name("Name2").email("e2@e.com").build());
        item = itemRepository.save(Item.builder().name("Item").owner(user).description("Desc").available(true).build());
        item2 = itemRepository.save(Item.builder().name("Item2").owner(user).description("Desc2").available(false).build());
    }

    @Test
    public void findAllForItemIdTestOK() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusYears(1);
        Booking booking = repository.save(new Booking(1L, item, start, end, null, Status.WAITING));
        assertEquals(List.of(booking), repository.findAllForItemId(item.getId()));
    }

    @Test
    public void findAllForItemIdTestEmptyList() {
        assertEquals(List.of(), repository.findAllForItemId(10L));
    }

    @Test
    public void findAllForOwnerTestOK() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusYears(1);
        Booking booking = repository.save(new Booking(1L, item, start, end, user2, Status.WAITING));
        List<Booking> list = repository.findAllForOwner(user.getId(), PageRequest.ofSize(10));
        assertEquals(list, List.of(booking));
    }

    @Test
    public void findAllForOwnerTestEmpty() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusYears(1);
        repository.save(new Booking(1L, item, start, end, user2, Status.WAITING));
        List<Booking> list = repository.findAllForOwner(user2.getId(), PageRequest.ofSize(10));
        assertEquals(list, List.of());
    }

    @Test
    public void findAllForOwnerWithStatusTestOK() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusYears(1);
        Booking booking = repository.save(new Booking(1L, item, start, end, user2, Status.WAITING));
        List<Booking> list = repository.findAllForOwner(user.getId(), Status.WAITING, PageRequest.ofSize(10));
        assertEquals(list, List.of(booking));
    }

    @Test
    public void findAllForOwnerWithStatusTestEmpty() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusYears(1);
        repository.save(new Booking(1L, item, start, end, user2, Status.WAITING));
        List<Booking> list = repository.findAllForOwner(user.getId(), Status.REJECTED, PageRequest.ofSize(10));
        assertEquals(list, List.of());
    }

    @Test
    public void findAllByOwnerIdAndStartAfterOrderByStartDescTestOK() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking2 = repository.save(new Booking(1L, item, start, end, user2, Status.WAITING));
        Booking booking1 = repository.save(new Booking(2L, item, start.plusDays(5), end.plusYears(1), user2, Status.WAITING));
        List<Booking> list = repository.findAllByOwnerIdAndStartAfterOrderByStartDesc(
                user.getId(), LocalDateTime.now(), PageRequest.ofSize(10));
        assertEquals(List.of(booking1, booking2), list);
    }

    @Test
    public void findAllForOwnerAndBeforeInstantTestOK() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking2 = repository.save(new Booking(1L, item, start, end, user2, Status.WAITING));
        repository.save(new Booking(2L, item, start.plusDays(5), end.plusYears(1), user2, Status.WAITING));
        List<Booking> list = repository.findAllForOwnerAndBeforeInstant(
                user.getId(), LocalDateTime.now().plusMonths(1), PageRequest.ofSize(10));
        assertEquals(List.of(booking2), list);
    }

    @Test
    public void findAllForOwnerCurrentTestEMPTY() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        repository.save(new Booking(1L, item, start, end, user2, Status.WAITING));
        repository.save(new Booking(2L, item, start.plusDays(5), end.plusYears(1), user2, Status.WAITING));
        List<Booking> list = repository.findAllForOwnerCurrent(
                user.getId(), LocalDateTime.now().plusHours(12), PageRequest.ofSize(10));
        assertEquals(List.of(), list);
    }

    @Test
    public void findAllForOwnerCurrentTestOK() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        Booking booking2 = repository.save(new Booking(1L, item, start, end, user2, Status.APPROVED));
        repository.save(new Booking(2L, item, start.plusDays(5), end.plusYears(1), user2, Status.APPROVED));
        List<Booking> list = repository.findAllForOwnerCurrent(
                user.getId(), LocalDateTime.now().plusHours(12), PageRequest.ofSize(10));
        assertEquals(List.of(booking2), list);
    }

    @Test
    public void isAvailableForBookingTestOK() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        assertTrue(!repository.isAvailableForBooking(start, start.plusHours(1), item.getId()));
        repository.save(new Booking(1L, item, start, end, user2, Status.APPROVED));
        assertFalse(!repository.isAvailableForBooking(start, start.plusHours(1), item.getId()));

    }
}
