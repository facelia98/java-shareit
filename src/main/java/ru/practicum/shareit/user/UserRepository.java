package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    UserDto add(User user);

    List<UserDto> getAll();

    boolean isEmailExist(Long id, String email);

    UserDto updateUser(Long id, User user);

    UserDto getById(Long id);

    boolean deleteUser(Long id);
}
