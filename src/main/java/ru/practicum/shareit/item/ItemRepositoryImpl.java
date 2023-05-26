package ru.practicum.shareit.item;

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
public class ItemRepositoryImpl implements ItemRepository {
    Map<Long, Item> itemMap = new HashMap<>();
    private Long id;

    @Override
    public ItemDto add(Item item) {
        item.setId(getNewId());
        itemMap.put(item.getId(), item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, Item item) {
        if (itemMap.get(itemId) == null) {
            log.error("Item not found exception for id = {}", itemId);
            throw new NotFoundException("Вещь с указанным id не существует");
        }
        Item tmpItem = itemMap.get(itemId);
        if (item.getName() != null && !item.getName().isBlank()) {
            tmpItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            tmpItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            tmpItem.setAvailable(item.getAvailable());
        }
        itemMap.put(itemId, tmpItem);
        return ItemMapper.toItemDto(tmpItem);
    }

    @Override
    public ItemDto get(Long itemId) {
        return ItemMapper.toItemDto(itemMap.get(itemId));
    }

    @Override
    public List<ItemDto> getList(Long userId) {
        return itemMap.values().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String query) {
        return itemMap.values().stream()
                .filter(item -> item.getAvailable() &&
                        (item.getName().toLowerCase().contains(query.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(query.toLowerCase())))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Long getNewId() {
        if (itemMap.isEmpty()) {
            id = 1L;
            return id;
        }
        id++;
        return id;
    }
}
