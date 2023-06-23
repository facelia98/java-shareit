package ru.practicum.shareit.ServicesTests;

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
import ru.practicum.shareit.user.UserServiceImpl;

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
    public void addUserEXCEPTION() {
        UserDto dto = UserDto.builder().id(3L).name("Name3").build();
        assertThrowsExactly(ValidationException.class, () -> userService.add(dto));
    }

    @Test
    public void addUserOK() {
        UserDto savedUserDto = userService.add(userDto);
        assertNotNull(savedUserDto.getId());
        assertEquals(userDto.getName(), savedUserDto.getName());
        assertEquals(userDto.getEmail(), savedUserDto.getEmail());
    }

    @Test
    public void updateUserOKWithoutEmail() {
        UserDto savedUserDto = userService.add(userDto2);
        userDto2.setEmail(null);
        userDto2.setName("NewName");
        userService.update(savedUserDto.getId(), userDto2);
        UserDto updatedUserDto = userService.get(savedUserDto.getId());
        assertEquals(userDto2.getName(), updatedUserDto.getName());
        assertEquals("mail2@mail.ru", updatedUserDto.getEmail());
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
        UserDto u = userService.add(userDto);
        assertNotNull(userService.get(u.getId()));
        userService.delete(u.getId());
        assertThrowsExactly(NotFoundException.class, () -> userService.get(u.getId()));
    }

    @Test
    public void checkEmailOK() {
        UserServiceImpl userServiceK = new UserServiceImpl(null);
        assertTrue(userServiceK.checkEmail("facelia98@mail.ru"));
        assertThrowsExactly(ValidationException.class, () -> userServiceK.checkEmail(" "));
        assertThrowsExactly(ValidationException.class, () -> userServiceK.checkEmail(""));
        assertThrowsExactly(ValidationException.class, () -> userServiceK.checkEmail("facelia98"));
    }
}
