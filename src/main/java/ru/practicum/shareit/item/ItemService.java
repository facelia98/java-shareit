package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemDto add(ItemDto dto) {
        log.info("POST item request received to endpoint [/items]");
        if (!validation(dto)) {
            log.error("Validation exception for item");
            throw new ValidationException("Item is invalid");
        }
        return itemRepository.add(ItemMapper.toItem(dto));
    }

    public ItemDto update(Long userId, Long itemId, ItemDto dto) {
        log.info("PATCH item request received to endpoint [/items] with userId = {}, itemId = {}", userId, itemId);
        return itemRepository.update(userId, itemId, ItemMapper.toItem(dto));
    }

    public ItemDto get(Long itemId) {
        log.info("GET item request received to endpoint [/items] with itemId = {}", itemId);
        return itemRepository.get(itemId);
    }

    public List<ItemDto> getList(Long userId) {
        log.info("GET item list request received to endpoint [/items] with userId = {}", userId);
        return itemRepository.getList(userId);
    }

    private boolean validation(ItemDto dto) {
        return dto.getAvailable() != null &&
                dto.getDescription() != null && !dto.getDescription().isBlank() &&
                dto.getName() != null && !dto.getName().isBlank();
    }

    public List<ItemDto> search(String query) {
        log.info("GET item list request received to endpoint [/items] with query = {}", query);
        return itemRepository.search(query);
    }
}
