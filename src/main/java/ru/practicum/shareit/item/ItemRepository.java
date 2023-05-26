package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {
    Item add(Item item);

    Item update(Item item);

    Item get(Long itemId);

    List<Item> getList(List<Long> itemsId);

    List<ItemDto> search(String query);
}
