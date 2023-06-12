package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserServiceImpl userServiceImpl;

    @PostMapping
    public UserDto addNewUser(@RequestBody UserDto user) {
        return userServiceImpl.add(user);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userServiceImpl.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable("id") Long id) {
        return userServiceImpl.get(id);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") Long id, @RequestBody UserDto dto) {
        return userServiceImpl.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        userServiceImpl.delete(id);
    }
}
