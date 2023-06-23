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
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.status.Status;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    private ItemService itemService;

    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private BookingRepository bookingRepository;
    @MockBean
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void beforeEach() {
        itemService = new ItemServiceImpl(itemRepository,
                commentRepository,
                userRepository,
                bookingRepository,
                itemRequestRepository);
    }

    @Test
    void getListTest() {
        User owner = User.builder().id(1L).name("Owner").email("owner@mail.ru").build();
        User booker = User.builder().id(2L).name("Booker").email("booker@mail.ru").build();
        Item item = Item.builder()
                .id(1L).owner(owner).name("Item").description("Desc").available(true)
                .build();
        Comment comment = Comment.builder()
                .id(1L).item(item).created(LocalDateTime.now()).text("Text").author(booker)
                .build();
        Booking booking = Booking.builder()
                .id(10L).booker(booker).item(item).status(Status.REJECTED)
                .start(LocalDateTime.now().minusWeeks(1)).end(LocalDateTime.now().minusDays(1))
                .build();

        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        Mockito.when(itemRepository.findAllByOwner_Id(1L, PageRequest.of(0, 10)))
                .thenReturn(List.of(item));
        Mockito.when(commentRepository.findAllByItem_Id(1L))
                .thenReturn(List.of(comment));
        Mockito.when(bookingRepository.findAllForItemId(1L))
                .thenReturn(List.of(booking));

        assertEquals(itemService.getList(1L, PageRequest.of(0, 10)),
                List.of(ItemMapper.toItemRDto(item, List.of(booking), List.of(comment))));
    }

}
