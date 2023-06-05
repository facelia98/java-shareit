package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> itemMap = new HashMap<>();
    private Long id;

    @Override
    public Item add(Item item) {
        item.setId(getNewId());
        itemMap.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        return itemMap.put(item.getId(), item);
    }

    @Override
    public Item get(Long itemId) {
        if (itemMap.get(itemId) == null) {
            log.error("Item not found exception for id = {}", itemId);
            throw new NotFoundException("Вещь с указанным id не существует");
        }
        return itemMap.get(itemId);
    }

    @Override
    public List<Item> getList(List<Long> itemsId) {
        List<Item> items = new ArrayList<>();
        for (Long l : itemsId) {
            items.add(itemMap.get(l));
        }
        return items;
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
