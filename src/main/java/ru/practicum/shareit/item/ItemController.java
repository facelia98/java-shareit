package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.AccessDenied;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    private void userCheck(Long userId) {
        if (userId == null) {
            log.error("UserId is empty");
            throw new ValidationException("UserId is empty");
        }
        if (userService.get(userId) == null) {
            log.error("User not found exception");
            throw new NotFoundException("User not found exception");
        }
    }

    @PostMapping
    public ItemDto addNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto item) {
        userCheck(userId);
        item.setOwner(UserMapper.toUser(userService.get(userId)));
        return itemService.add(item);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable("id") Long itemId,
                              @RequestBody ItemDto item) {
        userCheck(userId);
        if (!Objects.equals(itemService.get(itemId).getOwner().getId(), userId)) {
            log.error("Access denied for userId = {}", userId);
            throw new AccessDenied("Access denied");
        }
        return itemService.update(userId, itemId, item);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @RequestParam("text") String query) {
        if (query.isBlank()) {
            log.info("Empty query string for searching");
            return Collections.emptyList();
        }
        return itemService.search(query);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable("id") Long itemId) {
        if (itemService.get(itemId) == null) {
            log.error("Item not found exception for itemId = {}", itemId);
            throw new NotFoundException("Item not found exception");
        }
        return itemService.get(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsListForOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        userCheck(userId);
        return itemService.getList(userId);
    }
}
