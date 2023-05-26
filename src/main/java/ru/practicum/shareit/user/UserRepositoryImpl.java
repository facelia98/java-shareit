package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> userMap = new HashMap<>();
    private Long id;

    @Override
    public UserDto add(User user) {
        user.setId(getNewId());
        userMap.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userMap.values().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private Long getNewId() {
        if (userMap.isEmpty()) {
            id = 1L;
            return id;
        }
        id++;
        return id;
    }

    @Override
    public boolean isEmailExist(Long userId, String email) {
        return userMap.values().stream()
                .anyMatch(user -> user.getEmail().equals(email) && !Objects.equals(user.getId(), userId));
    }

    @Override
    public UserDto updateUser(Long userId, User user) {
        if (userMap.get(userId) == null) {
            log.error("User not found exception for id = {}", userId);
            throw new NotFoundException("Пользователь с указанным id не существует");
        }
        User tmpUser = userMap.get(userId);
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            tmpUser.setEmail(user.getEmail());
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            tmpUser.setName(user.getName());
        }
        userMap.put(userId, tmpUser);
        return UserMapper.toUserDto(tmpUser);
    }

    @Override
    public UserDto getById(Long userId) {
        if (userMap.get(userId) == null) {
            log.error("User not found exception for id = {}", userId);
            throw new NotFoundException("Пользователь с указанным id не существует");
        }
        return UserMapper.toUserDto(userMap.get(userId));
    }

    @Override
    public boolean deleteUser(Long userId) {
        if (userMap.get(userId) == null) {
            log.error("User not found exception for id = {}", userId);
            throw new NotFoundException("Пользователь с указанным id не существует");
        }
        userMap.remove(userId);
        return true;
    }
}
