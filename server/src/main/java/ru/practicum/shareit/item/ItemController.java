package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRDto;

import java.util.List;

@RequiredArgsConstructor
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
    public List<ItemDto> search(@RequestParam("text") String query,
                                @RequestParam(value = "from", defaultValue = "0") int from,
                                @RequestParam(value = "size", defaultValue = "20") int size) {
        return itemService.search(query, PageRequest.of(from / size, size));
    }

    @GetMapping("/{id}")
    public ItemRDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @PathVariable("id") Long itemId) {
        return itemService.get(itemId, userId);
    }

    @GetMapping
    public List<ItemRDto> getItemsListForOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(value = "from", defaultValue = "0") int from,
                                               @RequestParam(value = "size", defaultValue = "20") int size) {
        return itemService.getList(userId, PageRequest.of(from / size, size));
    }
}
