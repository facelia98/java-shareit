package ru.practicum.shareit.IntegrationTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {

    private UserService service;
    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        service = new UserServiceImpl(userRepository);
    }

    @Test
    void saveUser() {
        User user = User.builder().name("Name").email("email@email.ru").build();
        User savedUser = User.builder().id(1L).name("Name").email("email@email.ru").build();
        Mockito.when(userRepository.save(user)).thenReturn(savedUser);

        assertEquals(service.add(UserMapper.toUserDto(user)), UserMapper.toUserDto(savedUser));
    }

    @Test
    void updateUser() {
        User user = User.builder().id(1L).name("Name").email("email@email.ru").build();
        User update = User.builder().name("Updated Name").build();
        User updatedUser = User.builder().id(1L).name("Updated Name").email("email@email.ru").build();
        Mockito.when(userRepository.save(updatedUser)).thenReturn(updatedUser);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        assertEquals(service.update(1L, UserMapper.toUserDto(update)), UserMapper.toUserDto(updatedUser));
    }
}