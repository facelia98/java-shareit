package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {
    ItemDto add(Item item);

    ItemDto update(Long userId, Long itemId, Item item);

    ItemDto get(Long itemId);

    List<ItemDto> getList(Long userId);

    List<ItemDto> search(String query);
}
