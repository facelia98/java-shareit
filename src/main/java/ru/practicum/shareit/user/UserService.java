package ru.practicum.shareit.user;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    @Transactional
    UserDto add(UserDto dto);

    @Transactional
    UserDto update(Long id, UserDto dto);

    @Transactional(readOnly = true)
    UserDto get(Long id);

    @Transactional
    void delete(Long id);

    @Transactional
    List<UserDto> getAll();
}
