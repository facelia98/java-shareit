package ru.practicum.shareit.IntegrationTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.status.Status;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {

    private BookingService bookingService;

    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private BookingRepository bookingRepository;

    @BeforeEach
    void beforeEach() {
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void test() {
        User owner = User.builder().id(1L).name("Owner").email("owner@mail.ru").build();
        User booker = User.builder().id(2L).name("Booker").email("booker@mail.ru").build();
        Item item = Item.builder()
                .id(1L).owner(owner).name("Item").description("Desc").available(true)
                .build();
        Booking booking = Booking.builder()
                .id(10L).booker(booker).item(item).status(Status.REJECTED)
                .start(LocalDateTime.now().minusWeeks(1)).end(LocalDateTime.now().minusDays(1))
                .build();

        Mockito.when(userRepository.existsById(2L)).thenReturn(true);
        Mockito.when(bookingRepository.findAllByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 10)))
                .thenReturn(List.of(booking));

        assertEquals(bookingService.findAllByUserId(2L, "ALL", PageRequest.of(0, 10)),
                List.of(BookingMapper.toBookingReturningDto(booking)));
    }

}
