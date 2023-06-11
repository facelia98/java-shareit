package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRDto;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping("/{itemId}/comment")
    public CommentDto addNewComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody CommentDto comment,
                                    @PathVariable("itemId") Long itemId) {
        return itemService.addNewComment(comment, userId, itemId);
    }

    @PostMapping
    public ItemDto addNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto item) {
        return itemService.add(item, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable("id") Long itemId,
                              @RequestBody ItemDto item) {
        return itemService.update(userId, itemId, item);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @RequestParam("text") String query) {
        return itemService.search(query);
    }

    @GetMapping("/{id}")
    public ItemRDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @PathVariable("id") Long itemId) {
        return itemService.get(itemId, userId);
    }

    @GetMapping
    public List<ItemRDto> getItemsListForOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getList(userId);
    }
}
