package ru.practicum.shareit.IntegrationTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.exceptions.AccessDenied;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRDto;
import ru.practicum.shareit.status.Status;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
public class ItemServiceImplTest {

    ItemDto item = ItemDto.builder().name("Item").description("Desc").available(true).build();
    ItemDto item2 = ItemDto.builder().name("Item2").description("Desc2").available(false).build();
    UserDto userDto = UserDto.builder().id(1L).name("Name").email("mail@mail.ru").build();
    UserDto userDto2 = UserDto.builder().id(2L).name("Name2").email("mail2@mail.ru").build();
    BookingShort bookingShort = BookingShort.builder().id(1L).bookerId(1L).build();
    BookingShort bookingShort2 = BookingShort.builder().id(2L).bookerId(2L).build();

    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingService bookingService;

    @Test
    public void addItemOK() {
        UserDto user = userService.add(userDto);
        ItemDto savedItemDto = itemService.add(item, user.getId());
        assertNotNull(savedItemDto.getId());
        assertEquals(item.getName(), savedItemDto.getName());
        assertEquals(item.getAvailable(), savedItemDto.getAvailable());
        assertEquals(item.getDescription(), savedItemDto.getDescription());
        assertNull(item.getRequestId());
    }

    @Test
    public void addItemEXCEPTION() {
        userService.add(userDto);
        assertThrowsExactly(ValidationException.class,
                () -> itemService.add(ItemDto.builder().name(null).build(), 1L));
        assertThrowsExactly(NotFoundException.class,
                () -> itemService.add(item2, -1L));
    }

    @Test
    public void getItemOK() {
        UserDto u = userService.add(userDto);
        ItemDto savedItemDto = itemService.add(item, u.getId());
        ItemRDto gettedItemDto = itemService.get(savedItemDto.getId(), 1L);
        assertEquals(item.getDescription(), gettedItemDto.getDescription());
        assertEquals(item.getName(), gettedItemDto.getName());
        assertEquals(item.getAvailable(), gettedItemDto.getAvailable());
    }

    @Test
    public void getItemEXCEPTION() {
        userService.add(userDto);
        assertThrowsExactly(NotFoundException.class, () -> itemService.get(2L, 1L));
    }

    @Test
    public void updateItemOK() {
        UserDto u = userService.add(userDto);
        ItemDto savedItemDto = itemService.add(item, u.getId());
        itemService.update(u.getId(), savedItemDto.getId(),
                ItemDto.builder().description("Updated description").name("Updated name")
                        .available(true).build());
        assertEquals("Updated description",
                itemService.get(savedItemDto.getId(), u.getId()).getDescription());
    }

    @Test
    public void updateItemEXCEPTIONNotFound() {
        UserDto u = userService.add(userDto);
        assertThrowsExactly(NotFoundException.class,
                () -> itemService.update(2L, 10L,
                        ItemDto.builder().description(null).build()));
    }

    @Test
    public void updateItemEXCEPTION() {
        UserDto u = userService.add(userDto);
        ItemDto savedItemDto = itemService.add(item, u.getId());
        assertThrowsExactly(AccessDenied.class,
                () -> itemService.update(2L, savedItemDto.getId(),
                        ItemDto.builder().description(null).build()));
    }

    @Test
    public void getListEXCEPTION() {
        assertThrowsExactly(NotFoundException.class,
                () -> itemService.getList(10L, 0, 1));
    }

    @Test
    public void getListEXCEPTIONPagination() {
        UserDto u = userService.add(userDto);
        itemService.add(item, u.getId());
        itemService.add(item2, u.getId());
        assertThrowsExactly(ValidationException.class,
                () -> itemService.getList(u.getId(), -1, -1));
    }

    @Test
    public void getListOK() {
        UserDto u = userService.add(userDto);
        itemService.add(item, u.getId());
        itemService.add(item2, u.getId());
        List<ItemRDto> shouldContainsOne = itemService.getList(u.getId(), 1, 1);
        List<ItemRDto> shouldContainsTwo = itemService.getList(u.getId(), 0, 2);
        assertEquals(1, shouldContainsOne.size());
        assertEquals(2, shouldContainsTwo.size());
    }

    @Test
    public void searchEmptyOK() {
        UserDto u = userService.add(userDto);
        itemService.add(item, u.getId());
        itemService.add(item2, u.getId());
        assertEquals(List.of(), itemService.search(" ", 0, 10));
    }

    @Test
    public void searchEXCEPTION() {
        UserDto u = userService.add(userDto);
        itemService.add(item, u.getId());
        itemService.add(item2, u.getId());
        assertThrowsExactly(ValidationException.class,
                () -> itemService.search(" ", -1, -1));
    }

    @Test
    public void searchOK() {
        UserDto u = userService.add(userDto);
        itemService.add(item, u.getId());
        itemService.add(item2, u.getId());
        List<ItemDto> shouldContainsOne = itemService.search("Item", 0, 10);
        assertEquals(1, shouldContainsOne.size());
        List<ItemDto> shouldNotContains = itemService.search("kek", 0, 10);
        assertEquals(0, shouldNotContains.size());
    }

    @Test
    public void addCommentOK() {
        UserDto user = userService.add(userDto);
        UserDto user2 = userService.add(userDto2);
        LocalDateTime start = LocalDateTime.now().minusWeeks(1);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        ItemDto savedItemDto = itemService.add(item, user.getId());
        BookingDto dto = new BookingDto(null, savedItemDto.getId(), start, end, Status.WAITING);
        bookingService.save(user2.getId(), dto);

        CommentDto comment = CommentDto.builder().text("Text").build();
        CommentDto returned = itemService.addNewComment(comment, user2.getId(), savedItemDto.getId());
        assertEquals(comment.getText(), returned.getText());
        assertEquals(user2.getName(), returned.getAuthorName());
        assertNotNull(returned.getCreated());
    }

    @Test
    public void addCommentEXCEPTIONUserNotFound() {
        UserDto user = userService.add(userDto);
        UserDto user2 = userService.add(userDto2);
        LocalDateTime start = LocalDateTime.now().minusWeeks(1);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        ItemDto savedItemDto = itemService.add(item, user.getId());
        BookingDto dto = new BookingDto(null, savedItemDto.getId(), start, end, Status.WAITING);
        bookingService.save(user2.getId(), dto);

        CommentDto comment = CommentDto.builder().text("Text").build();
        assertThrowsExactly(NotFoundException.class,
                () -> itemService.addNewComment(comment, 100L, savedItemDto.getId()));
    }

    @Test
    public void addCommentEXCEPTIONItemNotFound() {
        UserDto user = userService.add(userDto);
        UserDto user2 = userService.add(userDto2);
        LocalDateTime start = LocalDateTime.now().minusWeeks(1);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        ItemDto savedItemDto = itemService.add(item, user.getId());
        BookingDto dto = new BookingDto(null, savedItemDto.getId(), start, end, Status.WAITING);
        bookingService.save(user2.getId(), dto);

        CommentDto comment = CommentDto.builder().text("Text").build();
        assertThrowsExactly(NotFoundException.class,
                () -> itemService.addNewComment(comment, user2.getId(), 100L));
    }
}
