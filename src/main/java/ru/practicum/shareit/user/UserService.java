package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicateException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto add(UserDto dto) {
        log.info("POST user request received to endpoint [/users]");
        if (userValidate(dto)) {
            if (checkEmail(dto.getId(), dto.getEmail())) {
                return userRepository.add(UserMapper.toUser(dto));
            }
            return null;
        } else {
            log.error("Validation exception");
            throw new ValidationException("UserDTO validation exception");
        }
    }

    public UserDto update(Long id, UserDto dto) {
        log.info("PATCH user request received to endpoint [/users] id = {}", id);
        if (dto.getEmail() != null) {
            if (checkEmail(id, dto.getEmail())) {
                return userRepository.updateUser(id, UserMapper.toUser(dto));
            }
        }
        return userRepository.updateUser(id, UserMapper.toUser(dto));
    }

    public UserDto get(Long id) {
        log.info("GET user request received to endpoint [/users] id = {}", id);
        return userRepository.getById(id);
    }

    public boolean delete(Long id) {
        log.info("DELETE user request received to endpoint [/users] id = {}", id);
        return userRepository.deleteUser(id);
    }

    public List<UserDto> getAll() {
        log.info("GET all users request received to endpoint [/users]");
        return userRepository.getAll();
    }

    public boolean checkEmail(Long id, String email) {
        if (userRepository.isEmailExist(id, email)) {
            log.error("Email validation error: duplicate");
            throw new DuplicateException(email);
        } else if (!email.contains("@")) {
            log.error("Email validation error: invalid format");
            throw new ValidationException("Invalid email format");
        } else if (email.isBlank()) {
            log.error("Email validation error: empty");
            throw new ValidationException("Email is empty");
        }
        return true;
    }

    public boolean userValidate(UserDto dto) {
        return dto.getName() != null &&
                dto.getEmail() != null &&
                !dto.getName().isBlank();
    }

    public void addOwnedItem(Long userId, Long itemId) {
        userRepository.addOwnedItem(userId, itemId);
    }
}
