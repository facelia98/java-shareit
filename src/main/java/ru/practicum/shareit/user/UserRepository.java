package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    User add(User user);

    List<UserDto> getAll();

    boolean isEmailExist(Long id, String email);

    User updateUser(Long id, User user);

    User getById(Long id);

    boolean deleteUser(Long id);

    void addOwnedItem(Long userId, Long itemId);
}
