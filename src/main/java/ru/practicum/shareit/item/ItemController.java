package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

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
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable("id") Long itemId) {
        return itemService.get(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsListForOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getList(userId);
    }
}
