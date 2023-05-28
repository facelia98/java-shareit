package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AccessDenied;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    public ItemDto add(ItemDto dto, Long userId) {
        userCheck(userId);
        log.info("POST item request received to endpoint [/items]");
        if (!validation(dto)) {
            log.error("Validation exception for item");
            throw new ValidationException("Item is invalid");
        }
        Item item = itemRepository.add(ItemMapper.toItem(dto, UserMapper.toUser(userService.get(userId))));
        userService.addOwnedItem(userId, item.getId());
        return ItemMapper.toItemDto(item);
    }

    public ItemDto update(Long userId, Long itemId, ItemDto dto) {
        userCheck(userId);
        log.info("PATCH item request received to endpoint [/items] with userId = {}, itemId = {}", userId, itemId);
        if (!Objects.equals(itemRepository.get(itemId).getOwner().getId(), userId)) {
            log.error("Access denied for userId = {}", userId);
            throw new AccessDenied("Access denied");
        }
        return ItemMapper.toItemDto(itemRepository.update(userId, itemId, ItemMapper.toItem(dto)));
    }

    public ItemDto get(Long itemId) {
        log.info("GET item request received to endpoint [/items] with itemId = {}", itemId);
        if (itemRepository.get(itemId) == null) {
            log.error("Item not found exception for itemId = {}", itemId);
            throw new NotFoundException("Item not found exception");
        }
        return ItemMapper.toItemDto(itemRepository.get(itemId));
    }

    public List<ItemDto> getList(Long userId) {
        log.info("GET item list request received to endpoint [/items] with userId = {}", userId);
        userCheck(userId);
        return itemRepository.getList(userService.get(userId).getItems())
                .stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    private boolean validation(ItemDto dto) {
        return dto.getAvailable() != null &&
                dto.getDescription() != null && !dto.getDescription().isBlank() &&
                dto.getName() != null && !dto.getName().isBlank();
    }

    public List<ItemDto> search(String query) {
        log.info("GET item list request received to endpoint [/items] with query = {}", query);
        if (query.isBlank()) {
            log.info("Empty query string for searching");
            return Collections.emptyList();
        }
        return itemRepository.search(query);
    }


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
}
