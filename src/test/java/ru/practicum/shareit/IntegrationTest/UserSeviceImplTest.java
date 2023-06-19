package ru.practicum.shareit.IntegrationTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
public class UserSeviceImplTest {
    UserDto userDto = UserDto.builder().id(1L).name("Name").email("mail@mail.ru").build();
    UserDto userDto2 = UserDto.builder().id(2L).name("Name2").email("mail2@mail.ru").build();
    UserDto userDto3 = UserDto.builder().id(3L).name("Name3").email("mail3@mail.ru").build();
    @Autowired
    private UserService userService;

    @Test
    public void addUserOK() {
        UserDto savedUserDto = userService.add(userDto);
        assertNotNull(savedUserDto.getId());
        assertEquals(userDto.getName(), savedUserDto.getName());
        assertEquals(userDto.getEmail(), savedUserDto.getEmail());
    }

    @Test
    public void updateUserOK() {
        UserDto savedUserDto = userService.add(userDto2);
        userDto2.setEmail("newEmail@mail.ru");
        userService.update(savedUserDto.getId(), userDto2);
        UserDto updatedUserDto = userService.get(savedUserDto.getId());
        assertEquals(userDto2.getName(), updatedUserDto.getName());
        assertEquals(userDto2.getEmail(), updatedUserDto.getEmail());
    }

    @Test
    public void updateUserEXCEPTION() {
        UserDto savedUserDto = userService.add(userDto3);
        userDto3.setEmail("newEmail");
        userDto3.setName(null);
        assertThrowsExactly(ValidationException.class,
                () -> userService.update(savedUserDto.getId(), userDto3));
    }

    @Test
    public void getAllOK() {
        userService.add(userDto);
        userService.add(userDto2);
        userService.add(userDto3);
        List<UserDto> userDtoList = userService.getAll();
        assertEquals(userDtoList.size(), 3);
    }

    @Test
    public void getUserEXCEPTION() {
        assertThrowsExactly(NotFoundException.class, () -> userService.get(-1L));
    }

    @Test
    public void deleteUserOK() {
        userService.add(userDto);
        assertNotNull(userService.get(1L));
        userService.delete(1L);
        assertThrowsExactly(NotFoundException.class, () -> userService.get(1L));
    }
}
