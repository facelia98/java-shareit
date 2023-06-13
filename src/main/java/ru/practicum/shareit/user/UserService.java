package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    UserDto add(UserDto dto);

    UserDto update(Long id, UserDto dto);

    UserDto get(Long id);

    void delete(Long id);

    List<UserDto> getAll();
}
