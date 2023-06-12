package ru.practicum.shareit.item;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRDto;

import java.util.List;

public interface ItemService {
    @Transactional
    ItemDto add(ItemDto dto, Long userId);

    @Transactional
    ItemDto update(Long userId, Long itemId, ItemDto dto);

    @Transactional(readOnly = true)
    ItemRDto get(Long itemId, Long userId);

    @Transactional(readOnly = true)
    List<ItemRDto> getList(Long userId);

    @Transactional
    List<ItemDto> search(String query);

    @Transactional
    CommentDto addNewComment(CommentDto comment, Long userId, Long itemId);
}
