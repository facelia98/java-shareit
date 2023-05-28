package ru.practicum.shareit.user;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final Table<Long, String, User> userMap = HashBasedTable.create();
    private Long id;

    @Override
    public UserDto add(User user) {
        user.setId(getNewId());
        userMap.put(user.getId(), user.getEmail(), user);
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
        if (userMap.columnKeySet().contains(email)) {
            return userMap.column(email).get(userId) == null;
        }
        return false;
    }

    @Override
    public UserDto updateUser(Long userId, User user) {
        if (!userMap.row(userId).values().stream().findFirst().isPresent()) {
            log.error("User not found exception for id = {}", userId);
            throw new NotFoundException("Пользователь с указанным id не существует");
        }
        User tmpUser = userMap.row(userId).values().stream().findFirst().get();
        userMap.remove(userId, tmpUser.getEmail());
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            tmpUser.setEmail(user.getEmail());
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            tmpUser.setName(user.getName());
        }
        userMap.put(userId, tmpUser.getEmail(), tmpUser);
        return UserMapper.toUserDto(tmpUser);
    }

    @Override
    public UserDto getById(Long userId) {
        if (!userMap.containsRow(userId)) {
            log.error("User not found exception for id = {}", userId);
            throw new NotFoundException("Пользователь с указанным id не существует");
        }
        return UserMapper.toUserDto(userMap.row(userId).values().stream().findFirst().get());
    }

    @Override
    public boolean deleteUser(Long userId) {
        if (!userMap.row(userId).values().stream().findFirst().isPresent()) {
            log.error("User not found exception for id = {}", userId);
            throw new NotFoundException("Пользователь с указанным id не существует");
        }
        userMap.remove(userId, userMap.row(userId).values().stream().findFirst().get().getEmail());
        return true;
    }

    @Override
    public void addOwnedItem(Long userId, Long itemId) {
        userMap.row(userId).values().stream().findFirst().get().getItems().add(itemId);
    }
}
