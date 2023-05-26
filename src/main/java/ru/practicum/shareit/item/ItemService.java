package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AccessDenied;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemDto add(ItemDto dto, Long userId) {
        log.info("POST item request received to endpoint [/items]");
        if (!validation(dto)) {
            log.error("Validation exception for item");
            throw new ValidationException("Item is invalid");
        }
        Item item = itemRepository.add(ItemMapper.toItem(dto, userRepository.getById(userId)));
        userRepository.addOwnedItem(userId, item.getId());
        return ItemMapper.toItemDto(item);
    }

    public ItemDto update(Long userId, Long itemId, ItemDto dto) {
        log.info("PATCH item request received to endpoint [/items] with userId = {}, itemId = {}", userId, itemId);
        Item item = itemRepository.get(itemId);
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            log.error("Access denied for userId = {}", userId);
            throw new AccessDenied("Access denied");
        }
        if (dto.getName() != null && !dto.getName().isBlank()) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.update(item));
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
        return itemRepository.getList(userRepository.getById(userId).getItems())
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
        if (userRepository.getById(userId) == null) {
            log.error("User not found exception");
            throw new NotFoundException("User not found exception");
        }
    }
}
