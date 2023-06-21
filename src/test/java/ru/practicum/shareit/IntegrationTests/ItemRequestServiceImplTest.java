package ru.practicum.shareit.IntegrationTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestDtoReturned;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
public class ItemRequestServiceImplTest {

    UserDto userDto = UserDto.builder().id(1L).name("Name").email("mail@mail.ru").build();
    UserDto userDto2 = UserDto.builder().id(2L).name("Name2").email("mail2@mail.ru").build();

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRequestService itemRequestService;

    @Test
    void addRequestOK() {
        User u = UserMapper.toUser(userService.add(userDto));
        ItemRequestDto itemRequestDto =
                ItemRequestDto.builder().description("Ручка").build();
        ItemRequestDtoReturned savedDto = itemRequestService.add(itemRequestDto, u.getId());
        assertNotNull(savedDto.getId());
        assertEquals(itemRequestDto.getDescription(), savedDto.getDescription());
        assertEquals(u, savedDto.getRequestor());
    }

    @Test
    void addRequestEXCEPTION_404() {
        ItemRequestDto itemRequestDto =
                ItemRequestDto.builder().description("Ручка").build();
        assertThrowsExactly(NotFoundException.class, () -> itemRequestService.add(itemRequestDto, 2L));
    }

    @Test
    void addRequestEXCEPTION_400() {
        ItemRequestDto itemRequestDto =
                ItemRequestDto.builder().description(" ").build();
        assertThrowsExactly(ValidationException.class, () -> itemRequestService.add(itemRequestDto, 2L));
    }

    @Test
    void getByIdOK() {
        User u = UserMapper.toUser(userService.add(userDto));
        ItemRequestDto itemRequestDto =
                ItemRequestDto.builder().description("Ручка").build();
        ItemRequestDtoReturned savedDto = itemRequestService.add(itemRequestDto, u.getId());
        ItemRequestDtoReturned getted = itemRequestService.get(savedDto.getId(), u.getId());
        assertEquals(savedDto.getDescription(), getted.getDescription());
        assertEquals(itemRequestDto.getDescription(), getted.getDescription());
        assertEquals(u, getted.getRequestor());
    }

    @Test
    void getAllOK() {
        User u = UserMapper.toUser(userService.add(userDto));
        User u2 = UserMapper.toUser(userService.add(userDto2));
        ItemRequestDto itemRequestDto =
                ItemRequestDto.builder().description("Ручка").build();
        ItemRequestDto itemRequestDto2 =
                ItemRequestDto.builder().description("Нож").build();
        ItemRequestDto itemRequestDto3 =
                ItemRequestDto.builder().description("Диплом").build();
        itemRequestService.add(itemRequestDto, u.getId());
        itemRequestService.add(itemRequestDto2, u.getId());
        itemRequestService.add(itemRequestDto3, u2.getId());
        assertEquals(2, itemRequestService.getAll(u.getId()).size());
        assertEquals(1, itemRequestService.getAll(u2.getId()).size());

    }

    @Test
    void getAllFromOthersOK() {
        User u = UserMapper.toUser(userService.add(userDto));
        User u2 = UserMapper.toUser(userService.add(userDto2));
        ItemRequestDto itemRequestDto =
                ItemRequestDto.builder().description("Ручка").build();
        ItemRequestDto itemRequestDto2 =
                ItemRequestDto.builder().description("Нож").build();
        ItemRequestDto itemRequestDto3 =
                ItemRequestDto.builder().description("Диплом").build();
        itemRequestService.add(itemRequestDto, u.getId());
        itemRequestService.add(itemRequestDto2, u.getId());
        itemRequestService.add(itemRequestDto3, u2.getId());
        assertEquals(1, itemRequestService.getAllFromOthers(u.getId(), 0, 10).size());
        assertEquals(2, itemRequestService.getAllFromOthers(u2.getId(), 0, 10).size());
    }
}
