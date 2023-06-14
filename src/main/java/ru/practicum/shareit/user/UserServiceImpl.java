package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto add(UserDto dto) {
        log.info("POST user request received to endpoint [/users]");
        if (userValidate(dto)) {
            if (checkEmail(dto.getEmail())) {
                return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(dto)));
            }
            return null;
        } else {
            log.error("User validation exception");
            throw new ValidationException("UserDTO validation exception");
        }
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto dto) {
        log.info("PATCH user request received to endpoint [/users] id = {}", id);
        UserDto user = get(id);
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            if (checkEmail(dto.getEmail())) {
                userRepository.updateByNotNullFields(id, user.getEmail(), user.getName());
            }
        }
        userRepository.updateByNotNullFields(id, user.getEmail(), user.getName());
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto get(Long id) {
        log.info("GET user request received to endpoint [/users] id = {}", id);
        return UserMapper.toUserDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User not found for id = %d", id))));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("DELETE user request received to endpoint [/users] id = {}", id);
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        log.info("GET all users request received to endpoint [/users]");
        return userRepository.findAll()
                .stream().map(user -> UserMapper.toUserDto(user))
                .collect(Collectors.toList());
    }

    public boolean checkEmail(String email) {
        if (!email.contains("@")) {
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
}