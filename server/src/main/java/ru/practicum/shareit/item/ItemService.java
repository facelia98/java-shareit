package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRDto;

import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto dto, Long userId);

    ItemDto update(Long userId, Long itemId, ItemDto dto);

    ItemRDto get(Long itemId, Long userId);

    List<ItemRDto> getList(Long userId, PageRequest pageRequest);

    List<ItemDto> search(String query, PageRequest pageRequest);

    CommentDto addNewComment(CommentDto comment, Long userId, Long itemId);
}
